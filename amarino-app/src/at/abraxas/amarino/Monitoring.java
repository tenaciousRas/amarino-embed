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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.log.LogListener;
import at.abraxas.amarino.log.Logger;

/**
 * 
 * @author Bonifaz Kaufmann
 *
 * $Id: Monitoring.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class Monitoring extends Activity implements LogListener, View.OnClickListener {
	
	private static final int DIALOG_FLAGS = 1;
	private static final int DIALOG_DEVICES = 2;
	
	private static final String KEY_FLAG_PREF = "flag_pref";
	private static final int MAX_ENTRIES = 400;
	
	private Button monitoringBtn;
	private Button flagBtn;
	private EditText dataToSendET;
	private ScrollView logScrollView;
	private TextView logTV;
	private Handler handler;
	
	private int logEntries = 0;
	private boolean monitoring;
	private boolean userTouch = false;
	String[] addresses; // connected devices
	String[] flags;
	char selectedFlag;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.monitoring_title);
		setContentView(R.layout.monitoring);

		handler = new Handler();

		monitoringBtn = (Button)findViewById(R.id.monitoring_btn);
		flagBtn = (Button)findViewById(R.id.flag_btn);
		dataToSendET = (EditText)findViewById(R.id.data_to_send);
		logScrollView = (ScrollView)findViewById(R.id.log_scroll);
		logTV = (TextView)findViewById(R.id.log);
		logTV.setText("========== Logging Window ==========\n");
		
		monitoring = PreferenceManager.getDefaultSharedPreferences(Monitoring.this)
			.getBoolean(Logger.KEY_IS_LOG_ENABLED, true);
		
		updateMonitoringState();
		
		findViewById(R.id.send_btn).setOnClickListener(this);
		findViewById(R.id.clear_btn).setOnClickListener(this);
		monitoringBtn.setOnClickListener(this);
		flagBtn.setOnClickListener(this);
		
		logScrollView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!userTouch && event.getAction() == MotionEvent.ACTION_DOWN){
					userTouch = true;
				}
				else if (userTouch && event.getAction() == MotionEvent.ACTION_UP){
					userTouch = false;
				}
				return false;
			}
		});
		
		setupFlagsArray();
		selectedFlag = (char)PreferenceManager.getDefaultSharedPreferences(this).getInt(KEY_FLAG_PREF, 65);  // default 'A'
		flagBtn.setText(getString(R.string.flag_btn, selectedFlag));
		
		// we want to know which devices are connected in order to send data to a device
		IntentFilter intentFilter = new IntentFilter(AmarinoIntent.ACTION_CONNECTED_DEVICES);
	    registerReceiver(receiver, intentFilter);
	    
	    Intent intent = new Intent(Monitoring.this, AmarinoService.class);
		intent.setAction(AmarinoIntent.ACTION_GET_CONNECTED_DEVICES);
		Monitoring.this.startService(intent);
	}

	private void setupFlagsArray() {
		flags = new String[52];
		for (int i=0;i<52;i++){
			char flag;
			if (i<26)
				flag = (char) ('A' + i);
			else
				flag = (char) ('a' + i-26); 
			flags[i] = String.valueOf(flag);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		logScrollView.fullScroll(View.FOCUS_DOWN);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// remember last selected flag
		PreferenceManager.getDefaultSharedPreferences(this).edit()
			.putInt(KEY_FLAG_PREF, selectedFlag).commit();
		Logger.unregisterLogListener(this);
		unregisterReceiver(receiver);
	}
	
	
	
	private void clearLogClickHandler(View target){
		logTV.setText("========== Logging Window ==========\n");
		Logger.clear();
		updateMonitoringState();
	}
	
	
	private void updateMonitoringState(){
		if (monitoring) {
			monitoringBtn.setText("Disable Monitoring");

			logTV.append("Monitoring enabled!\n");
			logTV.append(Logger.getLog());
			logScrollView.smoothScrollBy(0, logTV.getHeight());
			Logger.registerLogListener(this);
			Logger.enabled = true;
		}
		else {
			monitoringBtn.setText("Enable Monitoring");
			Logger.enabled = false;
			Logger.unregisterLogListener(this);
			logTV.append("Monitoring disabled!\n");
		}
	}
	
	private void sendData(String address, String msg){
		Intent intent = new Intent(Monitoring.this, AmarinoService.class);
		intent.setAction(AmarinoIntent.ACTION_SEND);
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address);
		intent.putExtra(AmarinoIntent.EXTRA_FLAG, selectedFlag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.STRING_EXTRA);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, msg);
		Monitoring.this.startService(intent);
	}
	
	

	@Override
	public void logChanged(final String lastAddedMsg) {
		logEntries++;

		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if (logEntries > MAX_ENTRIES){
					int size = logTV.getText().length();
					logTV.setText(logTV.getText().subSequence(size/2, size));
					logEntries /= 2;
				}
				logTV.append(lastAddedMsg + "\n");
				if (!userTouch){
					logScrollView.post(new Runnable() {
						
						@Override
						public void run() {
							logScrollView.smoothScrollBy(0, 60);
						}
					});
				}
			}
		});
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
		 switch (id) {
	        case DIALOG_FLAGS:
	        	return new AlertDialog.Builder(Monitoring.this)
	        		.setTitle("Choose your flag")
	        		.setItems(flags, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String flag = flags[which];
							flagBtn.setText(Monitoring.this.getString(R.string.flag_btn, flag));
							selectedFlag = flag.charAt(0);
						}
					})
					.create();
	        	
	        case DIALOG_DEVICES:
	        	return new AlertDialog.Builder(Monitoring.this)
        		.setTitle("Send data to:")
        		.setItems(addresses, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendData(addresses[which], dataToSendET.getText().toString());
					}
				})
				.create();
		 }
		 return null;
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.monitoring_btn:
			monitoring = !monitoring;
			PreferenceManager.getDefaultSharedPreferences(Monitoring.this)
				.edit()
				.putBoolean(Logger.KEY_IS_LOG_ENABLED, monitoring)
				.commit();
			updateMonitoringState();
			break;
		case R.id.flag_btn:
			showDialog(DIALOG_FLAGS);
			break;
			
		case R.id.send_btn:
			if (addresses == null){
				// no device is connected
				Toast.makeText(Monitoring.this, "No connected device found!\n\nData not sent.", Toast.LENGTH_SHORT).show();
			}
			else if (addresses.length == 1){
				// no need to ask just send data to the connected device
				sendData(addresses[0], dataToSendET.getText().toString());
			}
			else {
				// several connected devices, we need to show a dialog and ask where to send the data
				showDialog(DIALOG_DEVICES);
			}
			break;
			
		case R.id.clear_btn:
			clearLogClickHandler(v);
			break;
		}
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			if (action == null) return;
			
			if (AmarinoIntent.ACTION_CONNECTED_DEVICES.equals(action)){
				addresses = intent.getStringArrayExtra(AmarinoIntent.EXTRA_CONNECTED_DEVICE_ADDRESSES);
			}
		}
	};
	
}
