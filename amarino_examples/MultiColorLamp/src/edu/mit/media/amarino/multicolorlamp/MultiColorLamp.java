/*
  MultiColorLamp - Example to use with Amarino
  Copyright (c) 2009 Bonifaz Kaufmann. 
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package edu.mit.media.amarino.multicolorlamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class MultiColorLamp extends Activity implements OnSeekBarChangeListener{
	
	public static final String TAG = "MultiColorLamp";
	
	/* TODO: change the address to the address of your Bluetooth module
	 * and ensure your device is added to Amarino
	 */
	public static final String DEFAULT_DEVICE_ADDRESS = "00:06:66:03:73:7B";
	
	public static final String PREFS = "multicolorlamp";
	public static final String PREF_DEVICE_ADDRESS = "device_address";
	
	private static final int DIALOG_DEVICE_ADDRESS = 1;
	private static final int addressEditTextId = 15;
	
	
	final int DELAY = 150;
	SeekBar redSB;
	SeekBar greenSB;
	SeekBar blueSB;
	View colorIndicator;
	
	SharedPreferences prefs;
	
	String deviceAddress;
	int red, green, blue;
	long lastChange;
	
	/* We want to know when the connection has been established, so that we can send the last lamp state upon start up */
	private BroadcastReceiver connectionStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null){
				String action = intent.getAction();
				if (AmarinoIntent.ACTION_CONNECTED.equals(action)){
					updateAllColors();
				}
			}
		}
	};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // get references to views defined in our main.xml layout file
        redSB = (SeekBar) findViewById(R.id.SeekBarRed);
        greenSB = (SeekBar) findViewById(R.id.SeekBarGreen);
        blueSB = (SeekBar) findViewById(R.id.SeekBarBlue);
        colorIndicator = findViewById(R.id.ColorIndicator);

        // register listeners
        redSB.setOnSeekBarChangeListener(this);
        greenSB.setOnSeekBarChangeListener(this);
        blueSB.setOnSeekBarChangeListener(this);
        
        // get device address
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        deviceAddress = prefs.getString(PREF_DEVICE_ADDRESS, DEFAULT_DEVICE_ADDRESS);
        
        // load last state
        red = prefs.getInt("red", 0);
        green = prefs.getInt("green", 0);
        blue = prefs.getInt("blue", 0);
        
        // set seekbars and feedback color according to last state
        redSB.setProgress(red);
        greenSB.setProgress(green);
        blueSB.setProgress(blue);
        colorIndicator.setBackgroundColor(Color.rgb(red, green, blue));
        
        
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		
        registerReceiver(connectionStateReceiver, new IntentFilter(AmarinoIntent.ACTION_CONNECTED));
        Amarino.connect(this, deviceAddress);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// save state
		prefs.edit()
			.putInt("red", red)
			.putInt("green", green)
			.putInt("blue", blue)
		.commit();
		
		// stop Amarino's background service, we don't need it any more 
		Amarino.disconnect(this, deviceAddress);
		unregisterReceiver(connectionStateReceiver);
	}



	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// do not send to many updates, Arduino can't handle so much
		if (System.currentTimeMillis() - lastChange > DELAY ){
			updateState(seekBar);
			lastChange = System.currentTimeMillis();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		lastChange = System.currentTimeMillis();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		updateState(seekBar);
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
	    return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
	    switch (item.getItemId()) {
		    case R.id.device_address:
		        showDialog(DIALOG_DEVICE_ADDRESS);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id){
		
			case DIALOG_DEVICE_ADDRESS:
				final EditText addressEditText = new EditText(this);
				addressEditText.setId(addressEditTextId);
				addressEditText.setText(deviceAddress);
				
				return new AlertDialog.Builder(this)
					.setTitle(R.string.device_address)
					.setMessage(R.string.set_device_address)
					.setView(addressEditText)
					.setPositiveButton("Save", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String address = addressEditText.getEditableText().toString();
							if (Amarino.isCorrectAddressFormat(address)){
								prefs.edit()
									.putString(PREF_DEVICE_ADDRESS, address)
								.commit();
							}
							else {
								Toast.makeText(MultiColorLamp.this, R.string.device_address_format_error, Toast.LENGTH_LONG).show();
							}
							
						}
					})
					.setNegativeButton("Discard", null)
					.create();
				
			default:
				return super.onCreateDialog(id);
		}
		
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		EditText addressEditText = (EditText) dialog.findViewById(addressEditTextId);
		addressEditText.setText(prefs.getString(PREF_DEVICE_ADDRESS, DEFAULT_DEVICE_ADDRESS));
		super.onPrepareDialog(id, dialog);
	}

	private void updateState(final SeekBar seekBar) {
		
		switch (seekBar.getId()){
			case R.id.SeekBarRed:
				red = seekBar.getProgress();
				updateRed();
				break;
			case R.id.SeekBarGreen:
				green = seekBar.getProgress();
				updateGreen();
				break;
			case R.id.SeekBarBlue:
				blue = seekBar.getProgress();
				updateBlue();
				break;
		}
		// provide user feedback
		colorIndicator.setBackgroundColor(Color.rgb(red, green, blue));
	}
	
	private void updateAllColors() {
		// send state to Arduino
        updateRed();
        updateGreen();
        updateBlue();
	}
	
	private void updateRed(){
		// I have chosen random small letters for the flag 'o' for red, 'p' for green and 'q' for blue
		// you could select any small letter you want
		// however be sure to match the character you register a function for your in Arduino sketch
		Amarino.sendDataToArduino(this, deviceAddress, 'o', red);
	}
	
	private void updateGreen(){
		Amarino.sendDataToArduino(this, deviceAddress, 'p', green);
	}
	
	private void updateBlue(){
		Amarino.sendDataToArduino(this, deviceAddress, 'q', blue);
	}
	
}