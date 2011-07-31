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
import it.gerdavax.easybluetooth.ReadyListener;
import it.gerdavax.easybluetooth.RemoteDevice;
import it.gerdavax.easybluetooth.ScanListener;

import java.util.Vector;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This ListActivity initiates a Bluetooth device discovery
 * and shows a list of all discovered devices. 
 * 
 * 
 * Call this Activity using startActivityForResult, 
 * and it will returns the selected device address. 
 * Attached to the intent as an extra called ADDRESS_EXTRA.
 * 
 * @author Bonifaz Kaufmann
 * 
 * $Id: DeviceDiscovery.java 444 2010-06-10 13:11:59Z abraxas $
 * 
 */
public class DeviceDiscovery extends ListActivity {
	
	protected static String ADDRESS_EXTRA = "device_address";
	
	@SuppressWarnings("unused")
	private static final String TAG = "DeviceDiscovery";
	
	private DeviceAdapter adapter;
	private LocalDevice localDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Discovered Devices");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.discovered_devices_list);

		adapter = new DeviceAdapter();
		setListAdapter(adapter);
		
		setProgressBarIndeterminateVisibility(true);
		localDevice = LocalDevice.getInstance();
	}

	@Override
	protected void onStart() {
		super.onStart();

        localDevice.init(this, new ReadyListener(){

        	@Override
			public void ready() {
				localDevice.scan(new ScanListener(){
					
					@Override
					public void deviceFound(RemoteDevice device) {
						
						synchronized(adapter.discoveredDevices){
							Vector<RemoteDevice> addedDevices = adapter.discoveredDevices;
							for (RemoteDevice rd : addedDevices){
								if (rd.getAddress().equals(device.getAddress())){
									Log.d(TAG, "device already in list -> renew");
									adapter.discoveredDevices.remove(rd);
								}
							}
							adapter.discoveredDevices.add(device);
						}
						adapter.notifyDataSetChanged();
					}

					@Override
					public void scanCompleted() {
						setProgressBarIndeterminateVisibility(false);
					}
				});
			} // end ready()
        });
        
	}

	
	@Override
	protected void onStop() {
		super.onStop();
		// TODO change source of AndroidBluetoothLibrary to fix the bug
		// this might run in an exception, because of a race condition
		// see issue 22 at http://code.google.com/p/android-bluetooth/issues/detail?id=22
		localDevice.destroy();
	}


	@Override
	protected void onListItemClick(ListView lv, View view, int position, long id) {
		super.onListItemClick(lv, view, position, id);
		// since user selected already a device we do not need to scan for more devices
		localDevice.stopScan();
		
		final String address = ((RemoteDevice)adapter.getItem(position)).getAddress();
		Intent i = new Intent();
		i.putExtra(ADDRESS_EXTRA, address);
		setResult(RESULT_OK, i);
		finish();
	}
	

	private class DeviceAdapter extends BaseAdapter {
		
		Vector<RemoteDevice> discoveredDevices = new Vector<RemoteDevice>();

		public int getCount() {
			if (discoveredDevices != null) {
				return discoveredDevices.size();
			}
			return 0;
		}

		public Object getItem(int position) {
			return discoveredDevices.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout view = null;

			if (convertView == null) {
				view = new LinearLayout(DeviceDiscovery.this);
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater vi = (LayoutInflater) DeviceDiscovery.this.getSystemService(inflater);
				vi.inflate(R.layout.discovered_devices_list_item, view, true);
			} else {
				view = (LinearLayout) convertView;
			}

			TextView addressTextView = (TextView) view.findViewById(R.id.device_address);
			TextView nameTextView = (TextView) view.findViewById(R.id.device_name);
			
			RemoteDevice device = discoveredDevices.get(position);
			String address = device.getAddress();
			String name = device.getFriendlyName();
			
			addressTextView.setText(address);
			nameTextView.setText((name==null) ? "NONAME" : name);

			return view;
		}
	}
	
}
