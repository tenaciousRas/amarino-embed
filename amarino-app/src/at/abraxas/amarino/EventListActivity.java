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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.db.AmarinoDbAdapter;
import at.abraxas.amarino.intent.DefaultAmarinoServiceIntentConfig;
import at.abraxas.amarino.log.Logger;
import at.abraxas.amarino.message.DefaultMessageBuilder;
import at.abraxas.amarino.service.AmarinoService;
import at.abraxas.amarino.visualizer.Visualizer;

/**
 * 
 * @author Bonifaz Kaufmann
 *
 * $Id: EventListActivity.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class EventListActivity extends ListActivity {
	
	private static final boolean DEBUG = true;
	private static final String TAG = "EventListActivity";
	private static final int REQUEST_CREATE_EVENT = 1;
	private static final int REQUEST_UPDATE_EVENT = 2;
	
	private static final int MENU_ITEM_REMOVE_DEVICE = 1;
	private static final int MENU_ITEM_ENABLE = 2;
	private static final int MENU_ITEM_DISABLE = 3;
	
	private static final int MENU_ITEM_REMOVE_ALL = 10;
	private static final int MENU_ITEM_DISABLE_ALL = 11;
	
	BTDevice device;
	EventListAdapter eventListAdapter;
	PlugInListAdapter pluginListAdapter;
	AmarinoDbAdapter db;
	
	Plugin selectedPlugin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		
		db = new AmarinoDbAdapter(this);
		
		Intent intent = getIntent();
		if (intent != null){
			device = (BTDevice) intent.getSerializableExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE);
			if (device == null) {
				// there is something wrong, should never happen
			}
			setTitle(device.getName() + " - " + device.getAddress());
		}
		
		buildPluginList();
		
		ArrayList<Event> events = new ArrayList<Event>();
		db.open();
		events = db.fetchEvents(device.getId());
		Logger.d(TAG, "num of events: " + events.size());
		db.close();
		eventListAdapter = new EventListAdapter(this, events);
		setListAdapter(eventListAdapter);
		
		registerForContextMenu(getListView());
	}
	

	private void buildPluginList() {
		PackageManager pm = this.getPackageManager();
		List<ResolveInfo> editActivites = pm.queryIntentActivities(new Intent(DefaultAmarinoServiceIntentConfig.DEFAULT_ACTION_EDIT_PLUGIN), 0);
		Log.d(TAG, "Number of available plugins: " + editActivites.size());
		
		ArrayList<Plugin> plugins = new ArrayList<Plugin>(editActivites.size());
		for (ResolveInfo ri : editActivites){
			Plugin p = new Plugin();
			p.label = ri.loadLabel(pm).toString();
			p.icon = ri.loadIcon(pm);
			p.packageName = ri.activityInfo.packageName;
			p.editClassName = ri.activityInfo.name;
			plugins.add(p);
			if (DEBUG) Log.d(TAG, p.toString());
		}
		pluginListAdapter = new PlugInListAdapter(this, plugins);
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter(DefaultAmarinoServiceIntentConfig.DEFAULT_ACTION_SEND);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
		startService(new Intent(this, AmarinoService.class)
							.setAction(DefaultAmarinoServiceIntentConfig.DEFAULT_ACTION_DISABLE_ALL));
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// Setup the menu header
		AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(eventListAdapter.entries.get(info.position).name);
        
        menu.add(0, MENU_ITEM_REMOVE_DEVICE, 0, R.string.remove);
        menu.add(0, MENU_ITEM_ENABLE, 0, R.string.force_enable);
        menu.add(0, MENU_ITEM_DISABLE, 0, R.string.force_disable);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Event e = eventListAdapter.entries.get(info.position);
		switch (item.getItemId()) {
        case MENU_ITEM_REMOVE_DEVICE:
        	// TODO disable event if it is the last one standing
            db.open();
            db.deleteEvent(e.id);
            eventListAdapter.entries = db.fetchEvents(device.getId());
            eventListAdapter.notifyDataSetChanged();
            db.close();
            return true;
            
        case MENU_ITEM_ENABLE:
        	enablePlugin(e);
        	return true;
        	
        case MENU_ITEM_DISABLE:
        	disablePlugin(e);
        	return true;
	    }
		return false;
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ITEM_REMOVE_ALL, 0, R.string.remove_all);
		menu.add(0, MENU_ITEM_DISABLE_ALL, 0, R.string.force_disable_all);
		
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case MENU_ITEM_REMOVE_ALL:
			// TODO maybe dialog to ask if user is sure
			// TODO disable event if it is the last one standing
			db.open();
			for (Event e : eventListAdapter.entries){
				db.deleteEvent(e.id);
			}
			eventListAdapter.entries = db.fetchEvents(device.getId());
            eventListAdapter.notifyDataSetChanged();
            db.close();
			break;
			
		case MENU_ITEM_DISABLE_ALL:
			for (Event e : eventListAdapter.entries){
				disablePlugin(e);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void enablePlugin(Event e) {
		Intent enableIntent = new Intent(DefaultAmarinoServiceIntentConfig.DEFAULT_ACTION_ENABLE);
		enableIntent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS, device.getAddress());
		enableIntent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, e.pluginId);
		enableIntent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_SERVICE_CLASS_NAME, e.serviceClassName);
		enableIntent.setPackage(e.packageName);
		sendBroadcast(enableIntent);
	}


	private void disablePlugin(Event e) {
		Intent disableIntent = new Intent(DefaultAmarinoServiceIntentConfig.DEFAULT_ACTION_DISABLE);
		disableIntent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS, device.getAddress());
		disableIntent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, e.pluginId);
		disableIntent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_SERVICE_CLASS_NAME, e.serviceClassName);
		disableIntent.setPackage(e.packageName);
		sendBroadcast(disableIntent);
	}
	


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Event event = eventListAdapter.entries.get(position);
		
		Intent intent = new Intent(DefaultAmarinoServiceIntentConfig.DEFAULT_ACTION_EDIT_PLUGIN);
		intent.setClassName(event.packageName, event.editClassName);
		intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_FLAG, event.flag);
		intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS, device.getAddress());
		intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, event.pluginId);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		startActivityForResult(intent, REQUEST_UPDATE_EVENT);
	}

	public void addEventBtnClick(View view){
		if (pluginListAdapter.entries.size() == 0){
			Toast.makeText(EventListActivity.this, R.string.no_plugins_installed, Toast.LENGTH_LONG).show();
		}
		else {
			showPlugins();
		}
	}
	
	private void showPlugins(){
		new AlertDialog.Builder(EventListActivity.this)
			.setTitle("Add Event")
			.setIcon(R.drawable.icon_very_small)
			.setAdapter(pluginListAdapter, new DialogInterface.OnClickListener()  {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// start EditActivity of selected plugin
					selectedPlugin = pluginListAdapter.entries.get(which);
					Intent intent = new Intent(DefaultAmarinoServiceIntentConfig.DEFAULT_ACTION_EDIT_PLUGIN);
					intent.setClassName(selectedPlugin.packageName, selectedPlugin.editClassName);
					
					intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_FLAG, getNextFlag());
					intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS, device.getAddress());
					intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, selectedPlugin.label.hashCode());
	
					startActivityForResult(intent, REQUEST_CREATE_EVENT);
				}
			})
			.create()
			.show();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK){
			int visualizer;
			int pluginId;
			Event event;
			
			db.open();
			
			switch(requestCode){
			
			case REQUEST_CREATE_EVENT:
				// retrieve plugin infos and create new event element
				if (data == null) return;

				String name = data.getStringExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_NAME);
				String desc = data.getStringExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_DESC);
				String serviceClassName = data.getStringExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_SERVICE_CLASS_NAME);
				visualizer = data.getIntExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_VISUALIZER, -1);
				pluginId = data.getIntExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, -1);
				
				event = new Event(name, desc, visualizer, getNextFlag(), 
						selectedPlugin.packageName, selectedPlugin.editClassName, 
						serviceClassName, pluginId, device.getId());
				
				if (visualizer == DefaultAmarinoServiceIntentConfig.VISUALIZER_GRAPH || visualizer == DefaultAmarinoServiceIntentConfig.VISUALIZER_BARS){
					event.visualizerMinValue = data.getFloatExtra(DefaultAmarinoServiceIntentConfig.EXTRA_VISUALIZER_MIN_VALUE, 0f);
					event.visualizerMaxValue = data.getFloatExtra(DefaultAmarinoServiceIntentConfig.EXTRA_VISUALIZER_MAX_VALUE, 1024f);
				}
				
				Logger.d(TAG, event.toString());

				if (!eventListAdapter.entries.contains(event)){
					event.id = db.createEvent(event);
					if (event.id < 0)
						Logger.d(TAG, "Error creating event in database");
					else 
						Logger.d(TAG, "Event added to database");
				}
				else {
					Logger.d(TAG, "duplicate entry: event is alreay in your list");
				}
				
				break;
				
			case REQUEST_UPDATE_EVENT:
				if (data == null) return;
				
				pluginId = data.getIntExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, -1);
				event = db.getEvent(device.getId(), pluginId);
				
				if (event != null){
					event.visualizer = data.getIntExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_VISUALIZER, -1);
					if (event.visualizer == DefaultAmarinoServiceIntentConfig.VISUALIZER_GRAPH || event.visualizer == DefaultAmarinoServiceIntentConfig.VISUALIZER_BARS){
						event.visualizerMinValue = data.getFloatExtra(DefaultAmarinoServiceIntentConfig.EXTRA_VISUALIZER_MIN_VALUE, 0f);
						event.visualizerMaxValue = data.getFloatExtra(DefaultAmarinoServiceIntentConfig.EXTRA_VISUALIZER_MAX_VALUE, 1024f);
					}
					
					int num = db.updateEvent(event);
					Logger.d(TAG, num + " event updated." + event.toString());
				}
				else {
					Log.d(TAG, "Could not update. Event not found in database");
				}
				break;
			}
			
			eventListAdapter.entries = db.fetchEvents(device.getId());
			eventListAdapter.notifyDataSetChanged();
			db.close();
		}
	}
	
	private char getNextFlag(){
		boolean flagInUse = false;
		
		// search for free capital letter
		for (int i=0; i<26; i++){
			flagInUse = false;
			for (Event e : eventListAdapter.entries){
				if (e.flag == (65+i)){
					flagInUse = true;
					break;
				}
			}
			if (!flagInUse){
				return (char) (65+i); // 65 = 'A'
			}
		}
		
		
		// if still no flag found we use small letters, should be extremely rare
		for (int i=0; i<26; i++){
			for (Event e : eventListAdapter.entries){
				flagInUse = false;
				if (e.flag == (97+i)){
					flagInUse = true;
					break;
				}
			}
			if (!flagInUse){
				return (char) (97+i); // 97 = 'a'
			}
		}
		
		// no free flag found
		return '0';
		
	}
	
	private void updatePlugin(Message msg){
		Event e;
		int size = eventListAdapter.getCount();
		for (int i=0; i<size; i++){
			e = eventListAdapter.entries.get(i);
			
			if (e.pluginId == msg.arg1){
				final int dataType = msg.arg2;
				
				switch (e.visualizer){
				case DefaultAmarinoServiceIntentConfig.VISUALIZER_TEXT:
					updateTextView(msg, e, i, dataType);
					break;
					
				case DefaultAmarinoServiceIntentConfig.VISUALIZER_BARS:
				case DefaultAmarinoServiceIntentConfig.VISUALIZER_GRAPH:
					updateVisualizer(msg, e, i, dataType);
					break;

				} // end switch (e.visualizer)
			} // end if
		} // end for loop
	}


	private void updateTextView(Message msg, Event e, int i, final int dataType) {
		TextView tv = (TextView)getListView().getChildAt(i).findViewWithTag(e.pluginId);
		if (msg.obj != null && tv != null){
			if (dataType % 2 != 0){
				// data type is a single value
				tv.setText(String.valueOf(msg.obj));
			}
			else {
				// data type is an array
				String s = new DefaultMessageBuilder().getMessage(dataType, msg.obj);
				try {
					tv.setText(s.subSequence(0, s.length()-1));
				} catch (IndexOutOfBoundsException e1) { /* no data there */ }
			}
		}
	}


	private void updateVisualizer(Message msg, Event e, int i, final int dataType) {
		Visualizer visual = (Visualizer) getListView().getChildAt(i).findViewWithTag(e.pluginId);
		if (msg.obj != null && visual != null){
			try {
				switch(dataType){
				
					case DefaultAmarinoServiceIntentConfig.SHORT_EXTRA: 	visual.setData((Short)msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.INT_EXTRA: 		visual.setData((Integer)msg.obj);	break;
					case DefaultAmarinoServiceIntentConfig.FLOAT_EXTRA: 	visual.setData((Float)msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.DOUBLE_EXTRA: 	visual.setData((Double)msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.BYTE_EXTRA: 		visual.setData((Byte)msg.obj); 		break;
					case DefaultAmarinoServiceIntentConfig.BOOLEAN_EXTRA: 	visual.setData((Boolean)msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.STRING_EXTRA:	visual.setData(Float.parseFloat((String)msg.obj)); break;

					case DefaultAmarinoServiceIntentConfig.SHORT_ARRAY_EXTRA:	visual.setData((short[])msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.INT_ARRAY_EXTRA:		visual.setData((int[])msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.FLOAT_ARRAY_EXTRA:	visual.setData((float[])msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.DOUBLE_ARRAY_EXTRA:	visual.setData((double[])msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.BYTE_ARRAY_EXTRA:	visual.setData((byte[])msg.obj); 	break;
					case DefaultAmarinoServiceIntentConfig.BOOLEAN_ARRAY_EXTRA:	visual.setData((boolean[])msg.obj); break;
					case DefaultAmarinoServiceIntentConfig.STRING_ARRAY_EXTRA:	visual.setData((String[])msg.obj);	break;

				}
			} catch (NumberFormatException e1) {
				Log.d(TAG, "String does not contain numbers, thus not compatible with graph visualizer");
			} catch (ClassCastException e1) { 
				Log.d(TAG, "data are no compatible with graph visualizer");
			}
		}
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			updatePlugin(msg);
		}

	};
	
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action == null) return;
			
			if (DefaultAmarinoServiceIntentConfig.DEFAULT_ACTION_SEND.equals(action)){
				final int pluginId = intent.getIntExtra(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, -1);
				if (pluginId == -1) return; // we are only interested in data sent from plugins
				
				final int dataType = intent.getIntExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA_TYPE, -1);
				if (dataType == -1) return;
				
				Message msg = new Message();
				msg.arg1 = pluginId;
				msg.arg2 = dataType;
				
				switch (dataType){
				case DefaultAmarinoServiceIntentConfig.STRING_EXTRA:
					String s = intent.getStringExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					Log.d(TAG, "received: " + s);
					msg.obj = s;
					break;
				case DefaultAmarinoServiceIntentConfig.FLOAT_EXTRA:
					float f = intent.getFloatExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA, -1);
					Log.d(TAG, "received: " + f);
					msg.obj = f;
					break;
				case DefaultAmarinoServiceIntentConfig.INT_EXTRA:
					int i = intent.getIntExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA, -1);
					Log.d(TAG, "received: " + i);
					msg.obj = i;
					break;
				case DefaultAmarinoServiceIntentConfig.BYTE_EXTRA:
					byte b = intent.getByteExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA, (byte)-1);
					Log.d(TAG, "received: " + b);
					msg.obj = b;
					break;
				case DefaultAmarinoServiceIntentConfig.BOOLEAN_EXTRA:
					boolean bool = intent.getBooleanExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA, false);
					Log.d(TAG, "received: " + bool);
					msg.obj = bool;
					break;
				case DefaultAmarinoServiceIntentConfig.DOUBLE_EXTRA:
					double d = intent.getDoubleExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA, -1);
					Log.d(TAG, "received: " + d);
					msg.obj = d;
					break;
				case DefaultAmarinoServiceIntentConfig.SHORT_EXTRA:
					short shorty = intent.getShortExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA, (short)-1);
					Log.d(TAG, "received: " + shorty);
					msg.obj = shorty;
					break;
				case DefaultAmarinoServiceIntentConfig.LONG_EXTRA:
					long l = intent.getLongExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA, -1l);
					Log.d(TAG, "received: " + l);
					msg.obj = l;
					break;
				case DefaultAmarinoServiceIntentConfig.STRING_ARRAY_EXTRA:
					String[] strings = intent.getStringArrayExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					if (strings != null) {
						StringBuilder sBuilder = new StringBuilder();
						for (String str : strings){
							sBuilder.append(str).append(";");
						}
						Log.d(TAG, "received: " + sBuilder.toString());	
					}
					msg.obj = strings;
					break;
				case DefaultAmarinoServiceIntentConfig.FLOAT_ARRAY_EXTRA:
					float[] floats = intent.getFloatArrayExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					if (floats != null) {
						StringBuilder sBuilder = new StringBuilder();
						for (float fl : floats){
							sBuilder.append(fl).append(";");
						}
						Log.d(TAG, "received: " + sBuilder.toString());	
					}
					msg.obj = floats;
					break;
				case DefaultAmarinoServiceIntentConfig.INT_ARRAY_EXTRA:
					int[] ints = intent.getIntArrayExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					if (ints != null) {
						StringBuilder sBuilder = new StringBuilder();
						for (int in : ints){
							sBuilder.append(in).append(";");
						}
						Log.d(TAG, "received: " + sBuilder.toString());	
					}
					msg.obj = ints;
					break;
				case DefaultAmarinoServiceIntentConfig.BYTE_ARRAY_EXTRA:
					byte[] bytes = intent.getByteArrayExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					if (bytes != null) {
						StringBuilder sBuilder = new StringBuilder();
						for (byte by : bytes){
							sBuilder.append(by).append(";");
						}
						Log.d(TAG, "received: " + sBuilder.toString());	
					}
					msg.obj = bytes;
					break;
				case DefaultAmarinoServiceIntentConfig.BOOLEAN_ARRAY_EXTRA:
					boolean[] booleans = intent.getBooleanArrayExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					if (booleans != null) {
						StringBuilder sBuilder = new StringBuilder();
						for (boolean bo : booleans){
							sBuilder.append(bo).append(";");
						}
						Log.d(TAG, "received: " + sBuilder.toString());	
					}
					msg.obj = booleans;
					break;
				case DefaultAmarinoServiceIntentConfig.DOUBLE_ARRAY_EXTRA:
					double[] doubles = intent.getDoubleArrayExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					if (doubles != null) {
						StringBuilder sBuilder = new StringBuilder();
						for (double dou : doubles){
							sBuilder.append(dou).append(";");
						}
						Log.d(TAG, "received: " + sBuilder.toString());	
					}
					msg.obj = doubles;
					break;
				case DefaultAmarinoServiceIntentConfig.SHORT_ARRAY_EXTRA:
					short[] shorts = intent.getShortArrayExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					if (shorts != null) {
						StringBuilder sBuilder = new StringBuilder();
						for (short sh : shorts){
							sBuilder.append(sh).append(";");
						}
						Log.d(TAG, "received: " + sBuilder.toString());	
					}
					msg.obj = shorts;
					break;
				case DefaultAmarinoServiceIntentConfig.LONG_ARRAY_EXTRA:
					long[] longs = intent.getLongArrayExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
					if (longs != null) {
						StringBuilder sBuilder = new StringBuilder();
						for (long lo : longs){
							sBuilder.append(lo).append(";");
						}
						Log.d(TAG, "received: " + sBuilder.toString());	
					}
					msg.obj = longs;
					break;
				} // end switch
					
				handler.sendMessage(msg);
			}
			
		} // end onReceive()
	};
		

	
}
