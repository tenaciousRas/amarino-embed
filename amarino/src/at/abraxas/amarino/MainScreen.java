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

import it.gerdavax.easybluetooth.LocalDevice;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.log.Logger;

/**
 * 
 * @author Bonifaz Kaufmann
 *
 * $Id: MainScreen.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class MainScreen extends ListActivity implements OnClickListener{
	
	private static final String TAG = "AmarinoMainScreen";
	
	public static final int REQUEST_DISCOVERY = 2;
	
	private static final int MENU_ITEM_SHOW_EVENTS = 1;
	private static final int MENU_ITEM_CONNECT = 2;
	private static final int MENU_ITEM_REMOVE_DEVICE = 3;
	private static final int MENU_ITEM_MOVE_UP = 4;
	private static final int MENU_ITEM_MOVE_DOWN = 5;
	
	private static final int MENU_ABOUT = 10;
	
	private static final int DIALOG_ABOUT = 1;
	
	private static final String PREF_VERSION = "at.abraxas.amarino.version";
	
	
	AmarinoDbAdapter db;
	DeviceListAdapter devices;

	boolean isBound = false;
	MyHandler handler = new MyHandler();


	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			if (action == null) return;
			Logger.d(TAG, action + " received");
			
			if (AmarinoIntent.ACTION_CONNECTED_DEVICES.equals(action)){
				updateDeviceStates(intent.getStringArrayExtra(AmarinoIntent.EXTRA_CONNECTED_DEVICE_ADDRESSES));
				return;
			}
			
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			if (address == null) return;

			Message msg = new Message();
			
			if (AmarinoIntent.ACTION_CONNECTED.equals(action)){
				msg.what = MyHandler.CONNECTED;
			}
			else if (AmarinoIntent.ACTION_DISCONNECTED.equals(action)){
				msg.what = MyHandler.DISCONNECTED;
			}
			else if (AmarinoIntent.ACTION_CONNECTION_FAILED.equals(action)){
				msg.what = MyHandler.CONNECTION_FAILED;
			}
			else if (AmarinoIntent.ACTION_PAIRING_REQUESTED.equals(action)){
				msg.what = MyHandler.PAIRING_REQUESTED;
			}
			else return;
			
			msg.obj = address;
			handler.sendMessage(msg);
		}
	};	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");
        setContentView(R.layout.main);

        findViewById(R.id.add_device_btn).setOnClickListener(this);
        findViewById(R.id.add_device_btn_text).setOnClickListener(this);
        
        findViewById(R.id.monitoring_btn).setOnClickListener(this);
        findViewById(R.id.monitoring_btn_text).setOnClickListener(this);
        
        findViewById(R.id.settings_btn).setOnClickListener(this);
        findViewById(R.id.settings_btn_text).setOnClickListener(this);
        
        /* Since the settings button is not used so far, I hide it */
        findViewById(R.id.settings_btn_layout).setVisibility(View.GONE);
        
        
        db = new AmarinoDbAdapter(this);
        db.open();
        devices = new DeviceListAdapter(this, db.fetchAllDevices());
        db.close();
        
        setListAdapter(devices);
        registerForContextMenu(getListView());
        
        showReleaseNotes();
    }


	private void showReleaseNotes() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        int versionCode = getVersionCode();
        if (prefs.getInt(PREF_VERSION, 0) != versionCode){
        	showDialog(DIALOG_ABOUT);
        	prefs.edit().putInt(PREF_VERSION, versionCode).commit();
        }
	}
    
    
    @Override
	protected void onStart() {
		super.onStart();
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		// listen for device state changes
		IntentFilter intentFilter = new IntentFilter(AmarinoIntent.ACTION_CONNECTED_DEVICES);
		//intentFilter.addAction(AmarinoIntent.ACTION_CONNECTED);
	    //intentFilter.addAction(AmarinoIntent.ACTION_DISCONNECTED);
	    intentFilter.addAction(AmarinoIntent.ACTION_CONNECTION_FAILED);
	    intentFilter.addAction(AmarinoIntent.ACTION_PAIRING_REQUESTED);
	    registerReceiver(receiver, intentFilter);
	    
	    // request state of devices
	    Intent intent = new Intent(this, AmarinoService.class);
		intent.setAction(AmarinoIntent.ACTION_GET_CONNECTED_DEVICES);
		startService(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	

	private void updateDeviceStates(String[] connectedDevices){
		if (connectedDevices == null) {
			Logger.d(TAG, "no connected devices");
			for (BTDevice device : devices.deviceEntries){
				Message msg = new Message();
				msg.what = MyHandler.DISCONNECTED;
				msg.obj = device.address;
				handler.sendMessage(msg);
			}
			return;
		}

		Logger.d(TAG, "connected devices detected: " + connectedDevices.length);
		for (BTDevice device : devices.deviceEntries){
			boolean connected = false;
			Message msg = new Message();
			// this is normally a very short list, not matter that this is in O(n^2)
			for (int i=0; i<connectedDevices.length; i++){
				if (connectedDevices[i].equals(device.address)){
					msg.what = MyHandler.CONNECTED;
					connected = true;
					break;
				}
			}
			if (!connected){
				msg.what = MyHandler.DISCONNECTED;
			}
			msg.obj = device.address;
			handler.sendMessage(msg);
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ABOUT, Menu.FIRST, R.string.menu_about)
			.setIcon(R.drawable.about_icon);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case MENU_ABOUT:
			showDialog(DIALOG_ABOUT);
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
			case DIALOG_ABOUT:
				dialog = getAboutBox();
				break;

			default:
		        dialog = null;
		}
		return dialog;
	}
	
	private AlertDialog getAboutBox() {
		String title = getString(R.string.app_name) + " build " + getVersion(this);
		
		return new AlertDialog.Builder(MainScreen.this)
			.setTitle(title)
			.setView(View.inflate(this, R.layout.about, null))
			.setIcon(R.drawable.icon_small)
			.setPositiveButton("OK", null)
			.create();
		
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// Setup the menu header
		AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        BTDevice device = devices.deviceEntries.get(info.position);
        menu.setHeaderTitle(device.name);
        
        menu.add(0, MENU_ITEM_CONNECT, 0, 
        		device.state == AmarinoIntent.DISCONNECTED ? R.string.connect : R.string.disconnect);
        menu.add(0, MENU_ITEM_SHOW_EVENTS, 0, R.string.show_events);
        menu.add(0, MENU_ITEM_REMOVE_DEVICE, 0, R.string.remove_device);
        
        //menu.add(0, MENU_ITEM_MOVE_UP, 0, "Move up");
        //menu.add(0, MENU_ITEM_MOVE_DOWN, 0, "Move down");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        BTDevice device = devices.deviceEntries.get(info.position);
        
		switch (item.getItemId()) {
        case MENU_ITEM_REMOVE_DEVICE:
            // before we remove the device, we disconnect it if connected
            if (device.state == AmarinoIntent.CONNECTED){
            	Toast.makeText(this, "Please disconnect the device before removing it!", Toast.LENGTH_SHORT).show();
            }
            else {
 	            db.open();
	            db.deleteDevice(device.id);
	            devices.deviceEntries = db.fetchAllDevices();
	            db.close();
				devices.notifyDataSetChanged();
            }
            
            return true;
        case MENU_ITEM_SHOW_EVENTS:
        	onEventListBtnClick(info.position);
        	return true;
        case MENU_ITEM_CONNECT:
        	Button btn = (Button) info.targetView.findViewById(R.id.connect_btn);
        	onConnectBtnClick(btn, info.position);
        	return true;
        case MENU_ITEM_MOVE_UP: 
        	// TODO move this selected device one position up
        	return true;
        case MENU_ITEM_MOVE_DOWN: 
        	// TODO move this selected device one position down
        	return true;
	    }
		return false;
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		openContextMenu(v);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.connect_btn:
				Button btn = (Button)v;
				int postion = (Integer) v.getTag();
				onConnectBtnClick(btn, postion);
				break;
				
			case R.id.list_btn:
				int pos = (Integer) v.getTag();
				onEventListBtnClick(pos);
				break;
				
			case R.id.add_device_btn:
			case R.id.add_device_btn_text:
				startActivityForResult(new Intent(MainScreen.this, DeviceDiscovery.class), REQUEST_DISCOVERY);
				break;
				
			case R.id.monitoring_btn:
			case R.id.monitoring_btn_text:
				startActivity(new Intent(MainScreen.this, Monitoring.class));
				break;
				
			case R.id.settings_btn:
			case R.id.settings_btn_text:
				// TODO Perhaps we need a settings button in the future
				Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
				break;
		}
	}


	private void onConnectBtnClick(Button btn, int postion) {
		Intent i = new Intent(MainScreen.this, AmarinoService.class);
		i.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, devices.deviceEntries.get(postion).address);
		
		if ( btn.getText().equals(getString(R.string.connect)) )
			i.setAction(AmarinoIntent.ACTION_CONNECT);
		else
			i.setAction(AmarinoIntent.ACTION_DISCONNECT);

		btn.setEnabled(false);
		btn.setText(R.string.connecting);
		
		startService(i);
	} 
	
	private void onEventListBtnClick(int pos){
		Intent intent = new Intent(MainScreen.this, EventListActivity.class);
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE, devices.deviceEntries.get(pos));
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case REQUEST_DISCOVERY:
			if (resultCode == RESULT_OK){
				String address = data.getStringExtra(DeviceDiscovery.ADDRESS_EXTRA);
				// add only if device is not already in the list
				if (!devices.deviceEntries.contains(new BTDevice(address))){
					db.open();
					db.createDevice(new BTDevice(LocalDevice.getInstance().getRemoteForAddr(address)));
					devices.deviceEntries = db.fetchAllDevices();
					db.close();
					devices.notifyDataSetChanged();
				}
				else {
					Logger.d(TAG, "Duplicate entry: device already added");
					Toast.makeText(this, "Selected device is already in your list", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}
	
	private class MyHandler extends Handler {
		
		protected static final int CONNECTED = 1;
		protected static final int DISCONNECTED = 2;
		protected static final int CONNECTION_FAILED = 3;
		protected static final int PAIRING_REQUESTED = 4;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final int what = msg.what;
			final String address = (String)msg.obj;

			for (int i=0; i<devices.getCount();i++){
				BTDevice device = (BTDevice)devices.getItem(i);
				
				if (address.equals(device.address)){
					View view = getListView().getChildAt(i);
					Button btn = (Button)view.findViewById(R.id.connect_btn);
					btn.setEnabled(true);
					
					switch (what) {
						case CONNECTED:
							setDeviceConnected(device, view, btn);
							break;
						case CONNECTION_FAILED:
							Toast.makeText(MainScreen.this, "Connection failed!", Toast.LENGTH_SHORT).show();
						case DISCONNECTED:
							setDeviceDisconnected(device, view, btn);
							break;
						case PAIRING_REQUESTED:
							Toast.makeText(MainScreen.this, 
									"Device is not paired!\n\nPlease pull-down the notification bar to pair your device.\n\n", Toast.LENGTH_LONG).show();
							setDeviceDisconnected(device, view, btn);
							break;
					}
					view.invalidate();
				}
			} // end for
			
		} // end handleMessage()

		private void setDeviceConnected(BTDevice device, View view, Button btn) {
			device.state = AmarinoIntent.CONNECTED;
			view.findViewById(R.id.connected).setBackgroundResource(R.color.connected_on);
			view.findViewById(R.id.disconnected).setBackgroundResource(R.color.disconnected_off);
			btn.setText(R.string.disconnect);
		}

		private void setDeviceDisconnected(BTDevice device, View view, Button btn) {
			device.state = AmarinoIntent.DISCONNECTED;
			view.findViewById(R.id.connected).setBackgroundResource(R.color.connected_off);
			view.findViewById(R.id.disconnected).setBackgroundResource(R.color.disconnected_on);
			btn.setText(R.string.connect);
		}
		
		
	}
	
	public static String getVersion(Context context) {
		String version = "1.0"; 
		try { 
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0); 
		    version = pi.versionName; 
		} catch (PackageManager.NameNotFoundException e) { 
		    Log.e(TAG, "Package name not found", e); 
		} 
		return version;
	}
	
	private int getVersionCode() {
		int code = 1; 
		try { 
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0); 
			code = pi.versionCode; 
		} catch (PackageManager.NameNotFoundException e) { 
		    Log.e(TAG, "Package name not found", e); 
		} 
		return code;
	}
	
	
	private class DeviceListAdapter extends BaseAdapter {

		ArrayList<BTDevice> deviceEntries;
		Context context;
		
		@SuppressWarnings("unused")
		public DeviceListAdapter(Context context){
			this.context = context;
		}
		
		public DeviceListAdapter(Context context, ArrayList<BTDevice> deviceEntries){
			this.context = context;
			this.deviceEntries = deviceEntries;
		}
		
		public int getCount() {
			if (deviceEntries != null) {
				return deviceEntries.size();
			}
			return 0;
		}

		public Object getItem(int position) {
			return deviceEntries.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout view = null;
			BTDevice device = deviceEntries.get(position);

			if (convertView == null) {
				view = new LinearLayout(context);
				LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vi.inflate(R.layout.devices_list_item, view, true);
			} else {
				view = (LinearLayout) convertView;
			}

			TextView name = (TextView) view.findViewById(R.id.device_name);
			TextView address = (TextView) view.findViewById(R.id.device_address);
			Button eventListBtn = (Button) view.findViewById(R.id.list_btn);
			Button connectBtn = (Button) view.findViewById(R.id.connect_btn);
			
			// lets remember which address is associated with our buttons
			connectBtn.setTag(position);
			eventListBtn.setTag(position);
			
			connectBtn.setOnClickListener(MainScreen.this);
			eventListBtn.setOnClickListener(MainScreen.this);
			
			name.setText((name==null) ? "NONAME" : device.name);
			address.setText(device.address);
			
			return view;
		}
		
	}

}
