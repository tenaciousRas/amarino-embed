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
package at.abraxas.amarino;

import it.gerdavax.android.bluetooth.BluetoothDevice;
import it.gerdavax.easybluetooth.BtSocket;
import it.gerdavax.easybluetooth.LocalDevice;
import it.gerdavax.easybluetooth.ReadyListener;
import it.gerdavax.easybluetooth.RemoteDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import at.abraxas.amarino.log.Logger;

/**
 * $Id: AmarinoService.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class AmarinoService extends Service {
	
	private static final int NOTIFY_ID = 119561;
	private static final String TAG = "AmarinoService";

	
	
	private static final int BUSY = 1;
	private static final int ACTIVE_CONNECTIONS = 2;
	private static final int NO_CONNECTIONS = 3;
	
	private final IBinder binder = new AmarinoServiceBinder();
	
	private LocalDevice localDevice;
	private PendingIntent launchIntent;
	private Notification notification;
	private NotificationManager notifyManager;
	private AmarinoDbAdapter db;

	/* most ppl will only use one Bluetooth device, thus lets start with capacity 1, <address, running thread> */
	private HashMap<String, ConnectedThread> connections = new HashMap<String, ConnectedThread>(1);
	
	/* need to know which plugin has been activated for which device, <pluginId, list of devices> */
	private HashMap<Integer, List<BTDevice>> enabledEvents = new HashMap<Integer, List<BTDevice>>();
	
	private int serviceState = NO_CONNECTIONS;

	
	
	@Override
	public void onCreate() {
		Logger.d(TAG, "Background service created");
		super.onCreate();
		
		db = new AmarinoDbAdapter(this);
		
		initNotificationManager();
		
		// initialize reflection methods for backward compatibility of start and stopForeground
		try {
            mStartForeground = getClass().getMethod("startForeground",
                    mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground",
                    mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            // Running on an older platform.
            mStartForeground = mStopForeground = null;
        }
		
		IntentFilter filter = new IntentFilter(AmarinoIntent.ACTION_SEND);
		registerReceiver(receiver, filter);
	}


	
	@Override
	public void onStart(Intent intent, int startId) {
		handleStart(intent, startId);
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		return handleStart(intent, startId);
    }
	
	private int handleStart(Intent intent, int startId){
		//Logger.d(TAG, "onStart");
		super.onStart(intent, startId);
		
		if (intent == null) {
			// here we might restore our state if we got killed by the system
			// TODO
			return START_STICKY;
		}

		String action = intent.getAction();
		if (action == null) return START_STICKY;
		
		// someone wants to send data to arduino
		if (action.equals(AmarinoIntent.ACTION_SEND)){
			forwardDataToArduino(intent);
			return START_NOT_STICKY;
		}
		
		// publish the state of devices
		if (action.equals(AmarinoIntent.ACTION_GET_CONNECTED_DEVICES)){
			broadcastConnectedDevicesList();
			return START_NOT_STICKY;
		}
		
		// this intent is used to surely disable all plug-ins
		// if a user forgot to call force disable after force enable was called
		if (action.equals(AmarinoIntent.ACTION_DISABLE_ALL)){
			if (serviceState == NO_CONNECTIONS) {
				disableAllPlugins();
				stopSelf();
			}
			return START_NOT_STICKY;
		}
		
		/* --- CONNECT and DISCONNECT part --- */
		String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
		if (address == null) {
			Logger.d(TAG, "EXTRA_DEVICE_ADDRESS not found!");
			return START_NOT_STICKY;
		}
		
		// connect and disconnect operations may take some time
		// we don't want to shutdown our service while it does some work
		serviceState = BUSY;
		
		if (!isCorrectAddressFormat(address)) {
			Logger.d(TAG, getString(R.string.service_address_invalid, address));
			sendConnectionFailed(address);
			shutdownServiceIfNecessary();
		}
		else {
			if (AmarinoIntent.ACTION_CONNECT.equals(action)){
				Logger.d(TAG, "ACTION_CONNECT request received");
				connect(address);
			}
			else if (AmarinoIntent.ACTION_DISCONNECT.equals(action)){
				Logger.d(TAG, "ACTION_DISCONNECT request received");
				disconnect(address);
			}
		}

		return START_STICKY;
	}


	private void forwardDataToArduino(Intent intent){
		
		final int pluginId = intent.getIntExtra(AmarinoIntent.EXTRA_PLUGIN_ID, -1);
		// Log.d(TAG, "send from pluginID: " + pluginId);
		if (pluginId == -1) {
			// intent sent from another app which is not a plugin
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			if (address == null) {
				Logger.d(TAG, "Data not sent! EXTRA_DEVICE_ADDRESS not set.");
				return;
			}

			String message = MessageBuilder.getMessage(intent);
			if (message == null) return; 
			
			// cutoff ACK_FLAG for logger
			Logger.d(TAG, getString(R.string.service_message_to_send, message.substring(0, message.length()-1)));
			
			final char flag = intent.getCharExtra(AmarinoIntent.EXTRA_FLAG, 'a');
			message = flag + message;
			sendData(address, message.getBytes());
		}
		else {
			List<BTDevice> devices = enabledEvents.get(pluginId);
			if (devices != null && devices.size() != 0){
				for (BTDevice device : devices){

					String message = MessageBuilder.getMessage(intent);
					if (message == null) return;
					
					Logger.d(TAG, getString(R.string.service_message_to_send, message.substring(0, message.length()-1)));

					message = device.events.get(pluginId).flag + message;
					sendData(device.getAddress(), message.getBytes());
				}
			}
			else {
				Logger.d(TAG, "No device associated with plugin: " + pluginId);
			}
		}
	}


	private boolean isCorrectAddressFormat(String address){
		if (address.length() != 17) return false;
		// TODO use regular expression to check format needs more specific regex
		return Pattern.matches("[[A-F][0-9][:]]+", address.toUpperCase());
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.d(TAG, "Background service stopped");
		
		// we do only stop our service if no connections are active, however Android may kill our service without warning
		// clean up in case service gets killed from the system due to low memory condition
		if (serviceState == ACTIVE_CONNECTIONS){
			// TODO save which connections are active for recreating later when service is restarted
			
			disableAllPlugins();
			for (ConnectedThread t : connections.values()){
				t.cancel();
			}
		}
		unregisterReceiver(receiver);
		cancelNotification();
		
	}
	
	private void shutdownService(boolean disablePlugins){
		if (disablePlugins) 
			disableAllPlugins();
		if (serviceState == NO_CONNECTIONS){
			notifyManager.notify(NOTIFY_ID, 
					getNotification(getString(R.string.service_no_active_connections)));
			Logger.d(TAG, getString(R.string.service_ready_to_shutdown));
			stopSelf();
		}
	}
	
	private void shutdownServiceIfNecessary() {
		if (connections.size() == 0){
			serviceState = NO_CONNECTIONS;
			shutdownService(false);
		}
		else {
			serviceState = ACTIVE_CONNECTIONS;
			notifyManager.notify(NOTIFY_ID, 
					getNotification(getString(R.string.service_active_connections, connections.size())));
		}
	}


	protected void connect(final String address){
		if (address == null) return;
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
	
	public void disconnect(final String address){
		informPlugins(address, false);
		
		ConnectedThread ct = connections.remove(address);
		if (ct != null)
			ct.cancel();

		// end service if this was the last connection to disconnect
		if (connections.size()==0){
			serviceState = NO_CONNECTIONS;
			shutdownService(true);
		}
		else {
			serviceState = ACTIVE_CONNECTIONS;
			notifyManager.notify(NOTIFY_ID, 
					getNotification(getString(R.string.service_active_connections, connections.size())));
		}
	}
	
	public void sendData(final String address, byte[] data){
		ConnectedThread ct = connections.get(address);
		if (ct != null)
			ct.write(data);
	}
	

	
	private void informPlugins(String address, boolean enable){
		db.open();
		BTDevice device = db.getDevice(address);
		
		if (device != null){
			ArrayList<Event> events = db.fetchEvents(device.id);
			device.events = new HashMap<Integer, Event>();
			
			for (Event e : events){
				if (enable) {
					// remember which plugin was started for which device address
					List<BTDevice> devices = enabledEvents.get(e.pluginId);
					
					if (devices == null) {
						// plugin is not active
						devices = new LinkedList<BTDevice>();
						devices.add(device);
						enabledEvents.put(e.pluginId, devices);
					}
					else {
						// plugin already active, just add the new address
						devices.add(device);
					}
					// add to our fast HashMap for later use when sending data we need fast retrival of pluginId->flag
					device.events.put(e.pluginId, e);
					// start plugin no matter if it was active or not, plugins must be able to handle consecutive start calls
					informPlugIn(e, address, true);
				}
				else {
					// only if this is the last device with a certain event attached, disable the plugin
					List<BTDevice> devices = enabledEvents.get(e.pluginId);
					if (devices != null) {
						if (devices.remove(device)){
							// address found and removed
							if (devices.size()==0){
								enabledEvents.remove(e.pluginId);
								// was the last device which used this plugin, thus disable the plugin now
								informPlugIn(e, address, false);
							}
						}
					}
					else {
						Logger.d(TAG, "disable requested for Plugin " + e.name + " detected, but was never enabled");
						// should not happen, but maybe disconnect was called without ever connecting before
						informPlugIn(e, address, false);
					}
					// normally it shouldn't be any event with this id in device's events map, but we double check
					device.events.remove(e.pluginId);
				}
			}
		}
		db.close();
	}
	
	private void informPlugIn(Event e, String address, boolean enable){
		Logger.d(TAG, (enable ? getString(R.string.enable) : getString(R.string.disable)) + " " + e.name);
		Intent intent;
		if (enable)
			intent = new Intent(AmarinoIntent.ACTION_ENABLE);
		else
			intent = new Intent(AmarinoIntent.ACTION_DISABLE);
		
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address);
		intent.putExtra(AmarinoIntent.EXTRA_PLUGIN_ID, e.pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_PLUGIN_SERVICE_CLASS_NAME, e.serviceClassName);
		intent.setPackage(e.packageName);
		sendBroadcast(intent);
	}
	
	private void disableAllPlugins(){
		Intent intent = new Intent(AmarinoIntent.ACTION_DISABLE);
		sendBroadcast(intent);
	}
	
	private void broadcastConnectedDevicesList() {
		Intent returnIntent = new Intent(AmarinoIntent.ACTION_CONNECTED_DEVICES);
		if (connections.size() == 0){
			sendBroadcast(returnIntent);
			shutdownService(false);
			return;
		}
		Set<String> addresses = connections.keySet();
		String[] result = new String[addresses.size()];
		result = addresses.toArray(result);
		returnIntent.putExtra(AmarinoIntent.EXTRA_CONNECTED_DEVICE_ADDRESSES, result);
		sendBroadcast(returnIntent);
	}
	
	private void sendConnectionDisconnected(String address){
		String info = getString(R.string.service_disconnected_from, address);
		Logger.d(TAG, info);
		notifyManager.notify(NOTIFY_ID, getNotification(info));
		
		sendBroadcast(new Intent(AmarinoIntent.ACTION_DISCONNECTED)
			.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address));
		
		broadcastConnectedDevicesList();
	}
	
	private void sendConnectionFailed(String address){
		String info = getString(R.string.service_connection_to_failed, address);
		Logger.d(TAG, info);
		notifyManager.notify(NOTIFY_ID, getNotification(info));
		
		sendBroadcast(new Intent(AmarinoIntent.ACTION_CONNECTION_FAILED)
			.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address));
	}
	
	private void sendPairingRequested(String address){
		Logger.d(TAG, getString(R.string.service_pairing_request, address));
		sendBroadcast(new Intent(AmarinoIntent.ACTION_PAIRING_REQUESTED)
			.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address));
	}
	
	private void sendConnectionEstablished(String address){
		String info = getString(R.string.service_connected_to, address);
		Logger.d(TAG, info);

		sendBroadcast(new Intent(AmarinoIntent.ACTION_CONNECTED)
			.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address));
		
		broadcastConnectedDevicesList();
		
		startForegroundCompat(NOTIFY_ID, 
				getNotification(getString(R.string.service_active_connections, connections.size())));
		
		
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
	
	private void initNotificationManager() {
		notifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		launchIntent = PendingIntent.getActivity(AmarinoService.this, 0, 
				new Intent(AmarinoService.this, MainScreen.class)
						.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}
	
	private Notification getNotification(String title) {
		notification = new Notification(R.drawable.icon_very_small, title, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(this, title, getString(R.string.service_notify_text), launchIntent);
		return notification;
	}
	
	private void cancelNotification(){
		notifyManager.cancel(NOTIFY_ID);
	}
	
	
	/* ---------- Connection Threads ---------- */
	
	/**
	 * ConnectThread tries to establish a connection and starts the communication thread
	 */
	private class ConnectThread extends Thread {
		
		//private static final String TAG = "ConnectThread";
		//@SuppressWarnings("unused")
		private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		
		private final RemoteDevice mDevice;
		private BtSocket mSocket;

	    public ConnectThread(RemoteDevice device) {
	        mDevice = device;
	    }

	    public void run() {
	      	try {
	      		String info = getString(R.string.service_connecting_to, mDevice.getAddress());
	      		Logger.d(TAG, info);
	      		notifyManager.notify(NOTIFY_ID, getNotification(info));
	      		
	      		boolean isPaired = false;
	      		
	      		try {
	      			isPaired = mDevice.ensurePaired();
	      		}
	      		catch (RuntimeException re){
		      		re.printStackTrace();
		      	}
	      		
	    		if (!isPaired){
	    			//Log.d(TAG, "not paired!");
	    			sendPairingRequested(mDevice.getAddress());
	    			shutdownServiceIfNecessary();
	    		}
	    		else {
	    			//Log.d(TAG, "is paired!");
	    			// Let main thread do some stuff to render UI immediately
		    		Thread.yield();
		    		// Get a BluetoothSocket to connect with the given BluetoothDevice
		    		//mSocket = mDevice.openSocket(1);
		    		//mSocket = mDevice.openSocket(BluetoothDevice.BluetoothProfiles.UUID_SERIAL_PORT_PROFILE);
		    		mSocket = mDevice.openSocket(SPP_UUID);
		    		
		    		// Do work to manage the connection (in a separate thread)
			        manageConnectedSocket(mSocket);
	    		}
			}
	      	
	    	catch (Exception e) {
	    		sendConnectionFailed(mDevice.getAddress());
				e.printStackTrace();
				if (mSocket != null)
					try {
						mSocket.close();
					} catch (IOException e1) {}
					shutdownServiceIfNecessary();
				return;
			}
	    }

	    /** Will cancel an in-progress connection, and close the socket */
	    @SuppressWarnings("unused")
		public void cancel() {
	        try {
	            if (mSocket != null) mSocket.close();
	            sendConnectionDisconnected(mDevice.getAddress());
	        } 
	        catch (IOException e) { Log.e(TAG, "cannot close socket to " + mDevice.getAddress()); }
	    }
	    
	    private void manageConnectedSocket(BtSocket socket){
	    	Logger.d(TAG, "connection established, about to open sockets.");
	    	// pass the socket to a worker thread
	    	String address = mDevice.getAddress();
	    	ConnectedThread t = new ConnectedThread(socket, address);
	    	connections.put(address, t);
	    	t.start();
	    	
	    	serviceState = ACTIVE_CONNECTIONS;
	    	
	    	// now it is time to enable the plug-ins so that they can use our socket
			informPlugins(address, true);
	    }
	}
	
	/**
	 * ConnectedThread is holding the socket for communication with a Bluetooth device
	 */
	private class ConnectedThread extends Thread {
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
	        } catch (Exception e) { }

	        mInStream = tmpIn;
	        mOutStream = tmpOut;
	    }

	    public void run() {

	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes = 0; // bytes returned from read()
	        String msg;
	        
	        sendConnectionEstablished(mAddress);
	        
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	            	// Read from the InputStream
	                bytes = mInStream.read(buffer);

	                // Send the obtained bytes to the UI Activity
	                msg = new String(buffer, 0, (bytes != -1) ? bytes : 0 );
	                //Log.d(TAG, msg); // raw data with control flags
	                
	                forwardData(msg);

	            } catch (IOException e) {
	            	Logger.d(TAG, "communication to " + mAddress + " halted");
	                break;
	            }
	        }
	    }
	    
	    private void forwardData(String data){
			char c;
			for (int i=0;i<data.length();i++){
				c = data.charAt(i);
				if (c == MessageBuilder.ARDUINO_MSG_FLAG){
					// TODO this could be used to determine the data type
//					if (i+1<data.length()){
//						int dataType = data.charAt(i+1);
//						i++;
					// depending on the dataType we could convert the following data appropriately
//					}
//					else {
//						// wait for the next char to be sent
//					}
				}
				else if (c == MessageBuilder.ACK_FLAG){
					// message complete send the data
					forwardDataToOtherApps(forwardBuffer.toString());
					forwardBuffer = new StringBuffer();
				}
				else {
					forwardBuffer.append(c);
				}
			}
		}
	    
	    private void forwardDataToOtherApps(String msg){
	    	Logger.d(TAG, "Arduino says: " + msg);
	    	Intent intent = new Intent(AmarinoIntent.ACTION_RECEIVED);
            intent.putExtra(AmarinoIntent.EXTRA_DATA, msg);
            intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.STRING_EXTRA);
            intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, mAddress);
            sendBroadcast(intent);
	    }

	    /* Call this from the main Activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mOutStream.write(bytes);
	            Logger.d(TAG, "send to Arduino: " + new String(bytes));
	        } catch (IOException e) { }
	    }

	    /* Call this from the main Activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mSocket.close();
	            sendConnectionDisconnected(mAddress);
	        } catch (IOException e) { Log.e(TAG, "cannot close socket to " + mAddress); }
	    }
	}

	
	
	/* ---------- BroadcastReceiver ---------- */
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action == null) return;
			//Log.d(TAG, action);
			
			if (AmarinoIntent.ACTION_SEND.equals(action)){
				intent.setClass(context, AmarinoService.class);
				startService(intent);
			}
		}
	};
	
	
	
	/* ---------- use setForeground() but be also backward compatible ---------- */
	
	@SuppressWarnings("unchecked")
	private static final Class[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    @SuppressWarnings("unchecked")
	private static final Class[] mStopForegroundSignature = new Class[] {
        boolean.class};
    
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
	
	
	/**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
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
        setForeground(true);
        notifyManager.notify(id, notification);
    }
    
    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
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
        
        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        cancelNotification();
        setForeground(false);
    }
}
