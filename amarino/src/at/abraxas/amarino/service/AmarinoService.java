/*
  Amarino - A prototyping software toolkit for Android and Arduino
  Copyright (c) 2010 Bonifaz Kaufmann.  All right reserved.
  
  This application and its library is free software; you can redistribute
  it and/or modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package at.abraxas.amarino.service;

import it.gerdavax.easybluetooth.BtSocket;
import it.gerdavax.easybluetooth.LocalDevice;
import it.gerdavax.easybluetooth.ReadyListener;
import it.gerdavax.easybluetooth.RemoteDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.BTDevice;
import at.abraxas.amarino.Event;
import at.abraxas.amarino.R;
import at.abraxas.amarino.db.AmarinoDbAdapter;
import at.abraxas.amarino.db.DBConfig;
import at.abraxas.amarino.intent.AmarinoServiceIntentConfig;
import at.abraxas.amarino.intent.DefaultAmarinoServiceIntentConfig;
import at.abraxas.amarino.log.Logger;
import at.abraxas.amarino.message.DefaultMessageBuilder;
import at.abraxas.amarino.message.MessageBuilder;

/**
 * $Id: AmarinoService.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class AmarinoService extends Service {

	protected static final int NOTIFY_ID = 119561;
	protected static final String TAG = "AmarinoService";
	protected static final int BUSY = 1;
	protected static final int ACTIVE_CONNECTIONS = 2;
	protected static final int NO_CONNECTIONS = 3;
	public static final int REPORT_BT_CONNECTED = 1000;
	public static final int REPORT_BT_COMMAND = 1010;
	public static final int COMMAND_BT_DATA_RECVD = 1000;
	public static final String BUNDLE_KEY_BT_DEVICE_ADDY = "bt.devaddy";
	public static final String BUNDLE_KEY_BT_DATA = "bt.data";

	private final IBinder binder = new AmarinoServiceBinder();
	private int serviceState = NO_CONNECTIONS;
	private LocalDevice localDevice;
	private AmarinoDbAdapter db;
	private DBConfig dbConfig;
	private AmarinoServiceIntentConfig intentConfig;
	private MessageBuilder msgBuilder;
	private PendingIntent notifLaunchIntent;
	private Class<? extends Activity> notifLaunchIntentClass;
	private Notification notification;
	private NotificationManager notifyManager;
	private Handler clientMessageHandler;

	/*
	 * most ppl will only use one Bluetooth device, thus lets start with
	 * capacity 1, <address, running thread>
	 */
	private HashMap<String, ConnectedThread> connections = new HashMap<String, ConnectedThread>(
			1);

	/*
	 * need to know which plugin has been activated for which device, <pluginId,
	 * list of devices>
	 */
	private HashMap<Integer, List<BTDevice>> enabledEvents = new HashMap<Integer, List<BTDevice>>();

	/**
	 * The resourceId of a custom Icon to use
	 */
	private int customNotificationIconId;

	/**
	 * @return the dbConfig
	 */
	public DBConfig getDbConfig() {
		return dbConfig;
	}

	/**
	 * @param dbConfig
	 *            the dbConfig to set
	 */
	public void setDbConfig(DBConfig dbConfig) {
		this.dbConfig = dbConfig;
	}

	/**
	 * @return the intentConfig
	 */
	public AmarinoServiceIntentConfig getIntentConfig() {
		return intentConfig;
	}

	/**
	 * @param intentConfig
	 *            the intentConfig to set. set a NULL value to disable intent
	 *            handling by this class.
	 */
	public void setIntentConfig(AmarinoServiceIntentConfig intentConfig) {
		this.intentConfig = intentConfig;
	}

	/**
	 * @param notifLaunchIntent
	 *            the notifLaunchIntent to set
	 */
	public void setNotifLaunchIntent(PendingIntent notifLaunchIntent) {
		this.notifLaunchIntent = notifLaunchIntent;
	}

	/**
	 * @param notifLaunchIntentClass
	 *            the notifLaunchIntentClass to set
	 */
	public void setNotifLaunchIntentClass(
			Class<? extends Activity> notifLaunchIntentClass) {
		this.notifLaunchIntentClass = notifLaunchIntentClass;
	}

	/**
	 * @return the msgBuilder
	 */
	public MessageBuilder getMsgBuilder() {
		return msgBuilder;
	}

	/**
	 * @param msgBuilder
	 *            the msgBuilder to set
	 */
	public void setMsgBuilder(MessageBuilder msgBuilder) {
		this.msgBuilder = msgBuilder;
	}

	/**
	 * @return the clientMessageHandler
	 */
	public Handler getClientMessageHandler() {
		return clientMessageHandler;
	}

	/**
	 * @param clientMessageHandler
	 *            the clientMessageHandler to set
	 */
	public void setClientMessageHandler(Handler clientMessageHandler) {
		this.clientMessageHandler = clientMessageHandler;
	}

	/**
	 * @return the connections
	 */
	public HashMap<String, ConnectedThread> getConnections() {
		return connections;
	}

	/**
	 * @param connections
	 *            the connections to set
	 */
	public void setConnections(HashMap<String, ConnectedThread> connections) {
		this.connections = connections;
	}

	/**
	 * @return the serviceState
	 */
	public int getServiceState() {
		return serviceState;
	}

	/**
	 * @param serviceState
	 *            the serviceState to set
	 */
	public void setServiceState(int serviceState) {
		this.serviceState = serviceState;
	}

	@Override
	public void onCreate() {
		Logger.d(TAG, "Background service created");
		super.onCreate();

		if (null == dbConfig) {
			db = new AmarinoDbAdapter(getApplicationContext());
		} else {
			db = new AmarinoDbAdapter(getApplicationContext(), dbConfig);
		}
		if (null == intentConfig) {
			intentConfig = new DefaultAmarinoServiceIntentConfig();
		}
		if (null == msgBuilder) {
			msgBuilder = new DefaultMessageBuilder();
		}

		initNotificationManager();

		// initialize reflection methods for backward compatibility of start and
		// stopForeground
		try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}

		IntentFilter filter = new IntentFilter(
				intentConfig.getIntentNameActionSend());
		registerReceiver(receiver, filter);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		handleIntent(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStart(intent, startId);
		return handleIntent(intent);
	}

	/**
	 * @param intent
	 * @return
	 */
	private int handleIntent(Intent intent) {
		// Logger.d(TAG, "onStart");

		if (intent == null || null == intentConfig) {
			// here we might restore our state if we got killed by the system
			// TODO
			return START_STICKY;
		}

		String action = intent.getAction();
		if (action == null) {
			return START_STICKY;
		}

		// someone wants to send data to arduino
		if (action.equals(intentConfig.getIntentNameActionSend())) {
			forwardDataToArduino(intent);
			return START_NOT_STICKY;
		}

		// publish the state of devices
		if (action.equals(intentConfig.getIntentNameActionConnectedDevices())) {
			broadcastConnectedDevicesList();
			return START_NOT_STICKY;
		}

		// this intent is used to surely disable all plug-ins
		// if a user forgot to call force disable after force enable was called
		if (action.equals(intentConfig.getIntentNameActionDisableAll())) {
			if (serviceState == NO_CONNECTIONS) {
				disableAllPlugins();
				stopSelf();
			}
			return START_NOT_STICKY;
		}

		/* --- CONNECT and DISCONNECT part --- */
		String address = intent
				.getStringExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS);
		if (address == null) {
			Logger.d(TAG, "EXTRA_DEVICE_ADDRESS not found!");
			return START_NOT_STICKY;
		}

		// connect and disconnect operations may take some time
		// we don't want to shutdown our service while it does some work
		serviceState = BUSY;

		if (!Amarino.isCorrectAddressFormat(address)) {
			Logger.d(TAG, getString(R.string.service_address_invalid, address));
			sendConnectionFailed(address);
			shutdownServiceIfNecessary();
		} else {
			if (intentConfig.getIntentNameActionConnect().equals(action)) {
				Logger.d(TAG, "ACTION_CONNECT request received");
				connect(address);
			} else if (intentConfig.getIntentNameActionDisconnect().equals(
					action)) {
				Logger.d(TAG, "ACTION_DISCONNECT request received");
				disconnect(address);
			}
		}

		return START_STICKY;
	}

	protected void forwardDataToArduino(Intent intent) {

		final int pluginId = intent.getIntExtra(
				DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, -1);
		// Log.d(TAG, "send from pluginID: " + pluginId);
		if (pluginId == -1) {
			// intent sent from app, not a plugin
			final String address = intent
					.getStringExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS);
			if (address == null) {
				Logger.d(TAG, "Data not sent! EXTRA_DEVICE_ADDRESS not set.");
				return;
			}

			String message = msgBuilder.getMessage(intent);
			if (message == null)
				return;

			// cutoff leading flag and ACK_FLAG for logger
			Logger.d(
					TAG,
					getString(R.string.service_message_to_send,
							message.substring(0, message.length() - 1)));

			try {
				sendData(address, message.getBytes("ISO-8859-1"));
			} catch (UnsupportedEncodingException e) {
				// use default encoding as fallback alternative if encoding ISO
				// 8859-1 is not possible
				Logger.d(TAG, "Encoding message using ISO-8859-1 not possible");
				sendData(address, message.getBytes());
			}
		} else {
			List<BTDevice> devices = enabledEvents.get(pluginId);

			if (devices != null && devices.size() != 0) {
				for (BTDevice device : devices) {
					// we have to put the flag into the intent in order to
					// fulfill the message builder requirements
					intent.putExtra(
							DefaultAmarinoServiceIntentConfig.EXTRA_FLAG,
							device.getEvents().get(pluginId).flag);
					// Log.d(TAG, "flag" + device.events.get(pluginId).flag);

					String message = msgBuilder.getMessage(intent);
					if (message == null)
						return;

					Logger.d(
							TAG,
							getString(R.string.service_message_to_send,
									message.substring(1, message.length() - 1)));

					sendData(device.getAddress(), message.getBytes());
				}
			} else {
				Logger.d(TAG, "No device associated with plugin: " + pluginId);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.d(TAG, "Background service stopped");

		// we do only stop our service if no connections are active, however
		// Android may kill our service without warning
		// clean up in case service gets killed from the system due to low
		// memory condition
		if (serviceState == ACTIVE_CONNECTIONS) {
			// TODO save which connections are active for recreating later when
			// service is restarted

			disableAllPlugins();
			for (ConnectedThread t : connections.values()) {
				t.cancel();
			}
		}
		unregisterReceiver(receiver);
		cancelNotification();

	}

	protected void shutdownService(boolean disablePlugins) {
		if (disablePlugins)
			disableAllPlugins();
		if (serviceState == NO_CONNECTIONS) {
			if (null != notifLaunchIntent) {
				notifyManager
						.notify(NOTIFY_ID,
								buildNotification(getString(R.string.service_no_active_connections)));
			}
			Logger.d(TAG, getString(R.string.service_ready_to_shutdown));
			stopSelf();
		}
	}

	protected void shutdownServiceIfNecessary() {
		if (connections.size() == 0) {
			serviceState = NO_CONNECTIONS;
			shutdownService(false);
		} else {
			serviceState = ACTIVE_CONNECTIONS;
			if (null != notifLaunchIntent) {
				notifyManager.notify(
						NOTIFY_ID,
						buildNotification(getString(
								R.string.service_active_connections,
								connections.size())));
			}
		}
	}

	protected void connect(final String address) {
		if (address == null)
			return;
		localDevice = LocalDevice.getInstance();
		localDevice.init(this, new ReadyListener() {
			@Override
			public void ready() {
				RemoteDevice device = localDevice.getRemoteForAddr(address);
				localDevice.destroy();
				new ConnectThread(device).start();
			}
		});

	}

	public void disconnect(final String address) {
		informPlugins(address, false);

		ConnectedThread ct = connections.remove(address);
		if (ct != null)
			ct.cancel();

		// end service if this was the last connection to disconnect
		if (connections.size() == 0) {
			serviceState = NO_CONNECTIONS;
			shutdownService(true);
		} else {
			serviceState = ACTIVE_CONNECTIONS;
			if (null != notifLaunchIntent) {
				notifyManager.notify(
						NOTIFY_ID,
						buildNotification(getString(
								R.string.service_active_connections,
								connections.size())));
			}
		}
	}

	public void sendData(final String address, byte[] data) {
		ConnectedThread ct = connections.get(address);
		if (ct != null)
			ct.write(data);
	}

	protected void informPlugins(String address, boolean enable) {
		db.open();
		BTDevice device = db.getDevice(address);

		if (device != null) {
			ArrayList<Event> events = db.fetchEvents(device.getId());
			device.setEvents(new HashMap<Integer, Event>());

			for (Event e : events) {
				if (enable) {
					// remember which plugin was started for which device
					// address
					List<BTDevice> devices = enabledEvents.get(e.pluginId);

					if (devices == null) {
						// plugin is not active
						devices = new LinkedList<BTDevice>();
						devices.add(device);
						enabledEvents.put(e.pluginId, devices);
					} else {
						// plugin already active, just add the new address
						devices.add(device);
					}
					// add to our fast HashMap for later use when sending data
					// we need fast retrival of pluginId->flag
					device.getEvents().put(e.pluginId, e);
					// start plugin no matter if it was active or not, plugins
					// must be able to handle consecutive start calls
					informPlugIn(e, address, true);
				} else {
					// only if this is the last device with a certain event
					// attached, disable the plugin
					List<BTDevice> devices = enabledEvents.get(e.pluginId);
					if (devices != null) {
						if (devices.remove(device)) {
							// address found and removed
							if (devices.size() == 0) {
								enabledEvents.remove(e.pluginId);
								// was the last device which used this plugin,
								// thus disable the plugin now
								informPlugIn(e, address, false);
							}
						}
					} else {
						Logger.d(TAG, "disable requested for Plugin " + e.name
								+ " detected, but was never enabled");
						// should not happen, but maybe disconnect was called
						// without ever connecting before
						informPlugIn(e, address, false);
					}
					// normally it shouldn't be any event with this id in
					// device's events map, but we double check
					device.getEvents().remove(e.pluginId);
				}
			}
		}
		db.close();
	}

	protected void informPlugIn(Event e, String address, boolean enable) {
		Logger.d(TAG, (enable ? getString(R.string.enable)
				: getString(R.string.disable)) + " " + e.name);
		Intent intent;
		if (enable)
			intent = new Intent(intentConfig.getIntentNameActionEnable());
		else
			intent = new Intent(intentConfig.getIntentNameActionDisable());

		intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS,
				address);
		intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID,
				e.pluginId);
		intent.putExtra(
				DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_SERVICE_CLASS_NAME,
				e.serviceClassName);

		intent.setPackage(e.packageName);
		sendBroadcast(intent);
	}

	protected void disableAllPlugins() {
		Intent intent = new Intent(intentConfig.getIntentNameActionDisable());
		sendBroadcast(intent);
	}

	protected void broadcastConnectedDevicesList() {
		Intent returnIntent = new Intent(
				intentConfig.getIntentNameActionConnectedDevices());
		if (connections.size() == 0) {
			sendBroadcast(returnIntent);
			shutdownService(false);
			return;
		}
		Set<String> addresses = connections.keySet();
		String[] result = new String[addresses.size()];
		result = addresses.toArray(result);
		returnIntent
				.putExtra(
						DefaultAmarinoServiceIntentConfig.EXTRA_CONNECTED_DEVICE_ADDRESSES,
						result);
		sendBroadcast(returnIntent);
	}

	protected void sendConnectionDisconnected(String address) {
		String info = getString(R.string.service_disconnected_from, address);
		Logger.d(TAG, info);
		if (null != notifLaunchIntent) {
			notifyManager.notify(NOTIFY_ID, buildNotification(info));
		}

		sendBroadcast(new Intent(intentConfig.getIntentNameActionDisconnected())
				.putExtra(
						DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS,
						address));

		broadcastConnectedDevicesList();
	}

	protected void sendConnectionFailed(String address) {
		String info = getString(R.string.service_connection_to_failed, address);
		Logger.d(TAG, info);
		if (null != notifLaunchIntent) {
			notifyManager.notify(NOTIFY_ID, buildNotification(info));
		}

		sendBroadcast(new Intent(
				intentConfig.getIntentNameActionConnectionFailed())
				.putExtra(
						DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS,
						address));
	}

	protected void sendPairingRequested(String address) {
		Logger.d(TAG, getString(R.string.service_pairing_request, address));
		sendBroadcast(new Intent(
				intentConfig.getIntentNameActionPairingRequested())
				.putExtra(
						DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS,
						address));
	}

	protected void sendConnectionEstablished(String address) {
		String info = getString(R.string.service_connected_to, address);
		Logger.d(TAG, info);
		if (null != clientMessageHandler) {
			Message m = Message.obtain();
			m.what = REPORT_BT_CONNECTED;
			Bundle b = new Bundle();
			b.putString(BUNDLE_KEY_BT_DEVICE_ADDY, address);
			clientMessageHandler.sendMessage(m);
		}
		sendBroadcast(new Intent(intentConfig.getIntentNameActionConnected())
				.putExtra(
						DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS,
						address));

		broadcastConnectedDevicesList();
		// TODO - not sure why we're invoking the startForeground method with a
		// notification in order to broadcast to our receivers that a device
		// is connected. It doesn't look like mStartForeground or
		// mStopForeground
		// are bound to class methods that exist.
		if (null != notifLaunchIntent) {
			startForegroundCompat(
					NOTIFY_ID,
					buildNotification(getString(
							R.string.service_active_connections,
							connections.size())));
		}
	}

	/* ---------- Binder ---------- */

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class AmarinoServiceBinder extends Binder {
		AmarinoService getService() {
			return AmarinoService.this;
		}
	}

	/* ---------- Notification ---------- */

	protected void initNotificationManager() {
		notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		if (null != notifLaunchIntentClass) {
			notifLaunchIntent = PendingIntent.getActivity(AmarinoService.this,
					0, new Intent(AmarinoService.this, notifLaunchIntentClass)
							.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		} else {
			// FIXME: ???
			notifLaunchIntent = null;
		}
	}

	protected Notification buildNotification(String title) {
		notification = new Notification(getNotificationIconId(), title,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(this, title,
				getString(R.string.service_notify_text), notifLaunchIntent);
		return notification;
	}

	protected void cancelNotification() {
		if (null != notifLaunchIntent) {
			notifyManager.cancel(NOTIFY_ID);
		}
	}

	protected void setNotificationIconId(int resId) {
		customNotificationIconId = resId;
	}

	protected int getNotificationIconId() {
		if (0 == customNotificationIconId) {
			return R.drawable.icon_small;
		}
		if (0 < customNotificationIconId) {
			return customNotificationIconId;
		}
		return 0;
	}

	/* ---------- Connection Threads ---------- */

	/**
	 * ConnectThread tries to establish a connection and starts the
	 * communication thread
	 */
	private class ConnectThread extends Thread {

		// private static final String TAG = "ConnectThread";
		private final UUID SPP_UUID = UUID
				.fromString("00001101-0000-1000-8000-00805F9B34FB");

		private final RemoteDevice mDevice;
		private BtSocket mSocket;

		public ConnectThread(RemoteDevice device) {
			mDevice = device;
		}

		public void run() {
			try {
				String info = getString(R.string.service_connecting_to,
						mDevice.getAddress());
				Logger.d(TAG, info);
				if (null != notifLaunchIntent) {
					notifyManager.notify(NOTIFY_ID, buildNotification(info));
				}

				boolean isPaired = false;

				try {
					isPaired = mDevice.ensurePaired();
				} catch (RuntimeException re) {
					// FIXME always use Log.e
					re.printStackTrace();
				}

				if (!isPaired) {
					// Log.d(TAG, "not paired!");
					sendPairingRequested(mDevice.getAddress());
					shutdownServiceIfNecessary();
				} else {
					// Log.d(TAG, "is paired!");
					// Let main thread do some stuff to render UI immediately
					Thread.yield();
					// Get a BluetoothSocket to connect with the given
					// BluetoothDevice
					try {
						mSocket = mDevice.openSocket(SPP_UUID);
					} catch (Exception e) {
						Logger.d(TAG,
								"Connection via SDP unsuccessful, try to connect via port directly");
						// 1.x Android devices only work this way since SDP was
						// not part of their firmware then
						mSocket = mDevice.openSocket(1);
					}
					// Do work to manage the connection (in a separate thread)
					manageConnectedSocket(mSocket);
				}
			}

			catch (Exception e) {
				sendConnectionFailed(mDevice.getAddress());
				// FIXME
				e.printStackTrace();
				if (mSocket != null)
					try {
						mSocket.close();
					} catch (IOException e1) {
					}
				shutdownServiceIfNecessary();
				return;
			}
		}

		/** Will cancel an in-progress connection, and close the socket */
		@SuppressWarnings("unused")
		public void cancel() {
			try {
				if (mSocket != null)
					mSocket.close();
				sendConnectionDisconnected(mDevice.getAddress());
			} catch (IOException e) {
				Log.e(TAG, "cannot close socket to " + mDevice.getAddress());
			}
		}

		private void manageConnectedSocket(BtSocket socket) {
			Logger.d(TAG, "connection established.");
			// pass the socket to a worker thread
			String address = mDevice.getAddress();
			ConnectedThread t = new ConnectedThread(socket, address);
			connections.put(address, t);
			t.start();
			serviceState = ACTIVE_CONNECTIONS;
			// now it is time to enable the plug-ins so that they can use our
			// socket
			informPlugins(address, true);
		}
	}

	/**
	 * ConnectedThread is holding the socket for communication with a Bluetooth
	 * device
	 */
	protected class ConnectedThread extends Thread {
		private final BtSocket mSocket;
		private final InputStream mInStream;
		private final OutputStream mOutStream;
		private final String mAddress;
		private StringBuffer forwardBuffer = new StringBuffer();

		public ConnectedThread(BtSocket socket, String address) {
			mSocket = socket;
			this.mAddress = address;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (Exception e) {
			}

			mInStream = tmpIn;
			mOutStream = tmpOut;
		}

		public void run() {

			byte[] buffer = new byte[1024]; // buffer store for the stream
			int bytes = 0; // bytes returned from read()
			String msg;

			sendConnectionEstablished(mAddress);

			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					// Read from the InputStream
					bytes = mInStream.read(buffer);

					// Send the obtained bytes to the UI Activity
					msg = new String(buffer, 0, (bytes != -1) ? bytes : 0);
					// Log.d(TAG, msg); // raw data with control flags

					forwardData(msg);

				} catch (IOException e) {
					Logger.d(TAG, "communication to " + mAddress + " halted");
					break;
				}
			}
		}

		private void forwardData(String data) {
			char c;
			for (int i = 0; i < data.length(); i++) {
				c = data.charAt(i);
				if (c == DefaultMessageBuilder.ARDUINO_MSG_FLAG) {
					// TODO this could be used to determine the data type
					// if (i+1<data.length()){
					// int dataType = data.charAt(i+1);
					// i++;
					// depending on the dataType we could convert the following
					// data appropriately
					// }
					// else {
					// // wait for the next char to be sent
					// }
				} else if (c == DefaultMessageBuilder.ACK_FLAG) {
					// message complete send the data
					forwardDataToClientHandler(forwardBuffer.toString());
					forwardDataToOtherApps(forwardBuffer.toString());
					forwardBuffer = new StringBuffer();
				} else {
					forwardBuffer.append(c);
				}
			}
		}

		private void forwardDataToOtherApps(String msg) {
			Logger.d(TAG, "Arduino says: " + msg);
			Intent intent = new Intent(
					intentConfig.getIntentNameActionReceived());
			intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA, msg);
			intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA_TYPE,
					DefaultAmarinoServiceIntentConfig.STRING_EXTRA);
			intent.putExtra(
					DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS,
					mAddress);
			sendBroadcast(intent);
		}

		private void forwardDataToClientHandler(String msg) {
			if (null != clientMessageHandler) {
				Logger.d(TAG, "Arduino says: " + msg);
				Message m = Message.obtain();
				m.what = REPORT_BT_COMMAND;
				m.arg1 = COMMAND_BT_DATA_RECVD;
				Bundle b = new Bundle();
				b.putString(BUNDLE_KEY_BT_DATA, msg);
				m.setData(b);
				clientMessageHandler.sendMessage(m);
			}
		}

		/* Call this from the main Activity to send data to the remote device */
		public void write(byte[] bytes) {
			try {
				mOutStream.write(bytes);
				Logger.d(TAG, "send to Arduino: " + new String(bytes));
			} catch (IOException e) {
			}
		}

		/* Call this from the main Activity to shutdown the connection */
		public void cancel() {
			try {
				mSocket.close();
				sendConnectionDisconnected(mAddress);
			} catch (IOException e) {
				Log.e(TAG, "cannot close socket to " + mAddress);
			}
		}
	}

	/* ---------- BroadcastReceiver ---------- */

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action == null)
				return;
			// Log.d(TAG, action);

			if (intentConfig.getIntentNameActionSend().equals(action)) {
				intent.setClass(context, this.getClass());
				handleIntent(intent);
			}
		}
	};

	/* ---------- use setForeground() but be also backward compatible ---------- */
	// TODO - not sure what we're trying to do here - we should not be trying to
	// grab foreground and notifications don't need this

	@SuppressWarnings("rawtypes")
	private static final Class[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	@SuppressWarnings("rawtypes")
	private static final Class[] mStopForegroundSignature = new Class[] { boolean.class };

	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	public void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			try {
				mStartForeground.invoke(this, mStartForegroundArgs);
			} catch (InvocationTargetException e) {
				// Should not happen.
				Log.w("MyApp", "Unable to invoke startForeground", e);
			} catch (IllegalAccessException e) {
				// Should not happen.
				Log.w("MyApp", "Unable to invoke startForeground", e);
			}
			return;
		}

		// Fall back on the old API.
		// causing grief in honeycomb+
		// setForeground(true);
		if (null != notifLaunchIntent) {
			notifyManager.notify(id, notification);
		}
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	public void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			try {
				mStopForeground.invoke(this, mStopForegroundArgs);
			} catch (InvocationTargetException e) {
				// Should not happen.
				Log.w("MyApp", "Unable to invoke stopForeground", e);
			} catch (IllegalAccessException e) {
				// Should not happen.
				Log.w("MyApp", "Unable to invoke stopForeground", e);
			}
			return;
		}
		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		cancelNotification();
		// causing grief in honeycomb+
		// setForeground(false);
	}
}
