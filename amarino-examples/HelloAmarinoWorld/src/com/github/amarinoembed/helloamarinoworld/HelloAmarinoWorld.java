package com.github.amarinoembed.helloamarinoworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoConfigured;
import at.abraxas.amarino.log.Logger;

public class HelloAmarinoWorld extends Activity {

	public static final String TAG = "HelloAmarinoWorld";

	public static final String DEFAULT_DEVICE_ADDRESS = "00:06:66:03:73:7B";

	public static final String PREFS = "helloamarinoworld";
	public static final String PREF_DEVICE_ADDRESS = "device_address";

	private static final int DIALOG_DEVICE_ADDRESS = 1;
	private static final int addressEditTextId = 15;

	public static class ArduinoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (action == null)
					return;
				ServiceIntentConfig configuredIntents = new ServiceIntentConfig();
				if (configuredIntents.getIntentNameActionConnect().equals(
						action)) {
					Logger.d(TAG, "CONNECT request received");
					Intent i = new Intent(context, BTService.class);
					i.setAction(configuredIntents.getIntentNameActionConnect());
					i.replaceExtras(intent);
					context.startService(i);
				} else if (configuredIntents.getIntentNameActionReceived()
						.equals(action)) {
					Logger.d(TAG, "DATA_RECEIVED request received");
					@SuppressWarnings("unused")
					char[] chData = null;
					@SuppressWarnings("unused")
					String strData = null;

					// the device address from which the data was sent, we don't
					// need it
					// here but to demonstrate how you retrieve it
					@SuppressWarnings("unused")
					final String address = intent
							.getStringExtra(ServiceIntentConfig.EXTRA_DEVICE_ADDRESS);

					// the type of data which is added to the intent
					final int dataType = intent.getIntExtra(
							ServiceIntentConfig.EXTRA_DATA_TYPE, -1);
					Log.v(TAG,
							new StringBuilder()
									.append("data received from Arduino with type: ")
									.append(dataType).toString());
					if (dataType == ServiceIntentConfig.CHAR_ARRAY_EXTRA) {
						chData = intent
								.getCharArrayExtra(ServiceIntentConfig.EXTRA_DATA);
					}
					if (dataType == ServiceIntentConfig.STRING_EXTRA) {
						strData = intent
								.getStringExtra(ServiceIntentConfig.EXTRA_DATA);
					}
				} else if (configuredIntents.getIntentNameActionDisconnect()
						.equals(action)) {
					Logger.d(TAG, "DISCONNECT request received");
					Intent i = new Intent(context, BTService.class);
					i.setAction(configuredIntents
							.getIntentNameActionDisconnect());
					i.replaceExtras(intent);
					context.startService(i);
				} else if (configuredIntents
						.getIntentNameActionConnectedDevices().equals(action)) {
					Logger.d(TAG, "GET_CONNECTED_DEVICES request received");
					Intent i = new Intent(context, BTService.class);
					i.setAction(configuredIntents
							.getIntentNameActionGetConnectedDevices());
					context.startService(i);
				}
			}
		}
	}

	private String deviceAddress;
	private SharedPreferences prefs;

	/**
	 * The embedded Amarino service. If used globally consider moving this to an
	 * instance of {@link android.app.Application}.
	 */
	private AmarinoConfigured embeddedAmarino;

	private ArduinoReceiver receiver;
	private ServiceIntentConfig intentConfig;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// get device address
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		deviceAddress = prefs.getString(PREF_DEVICE_ADDRESS,
				DEFAULT_DEVICE_ADDRESS);
		receiver = new ArduinoReceiver();
		intentConfig = new ServiceIntentConfig();
		registerReceiver(receiver,
				new IntentFilter(intentConfig.getIntentNameActionConnect()));
		registerReceiver(receiver,
				new IntentFilter(intentConfig.getIntentNameActionReceived()));
		Log.v(TAG, new StringBuilder("bluetooth receiver intent registered")
				.append(deviceAddress).toString());
	}

	@Override
	protected void onStart() {
		super.onStart();
		embeddedAmarino = new AmarinoConfigured(this.getApplicationContext());
		embeddedAmarino.setIntentConfig(intentConfig);
		embeddedAmarino.connect(deviceAddress);
	}

	@Override
	protected void onPause() {
		super.onPause();
		embeddedAmarino.disconnect(deviceAddress);
		if (null != receiver) {
			try {
				unregisterReceiver(receiver);
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
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
		switch (id) {

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
							String address = addressEditText.getEditableText()
									.toString();
							if (Amarino.isCorrectAddressFormat(address)) {
								prefs.edit()
										.putString(PREF_DEVICE_ADDRESS, address)
										.commit();
							} else {
								Toast.makeText(HelloAmarinoWorld.this,
										R.string.device_address_format_error,
										Toast.LENGTH_LONG).show();
							}

						}
					}).setNegativeButton("Discard", null).create();

		default:
			return super.onCreateDialog(id);
		}

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		EditText addressEditText = (EditText) dialog
				.findViewById(addressEditTextId);
		addressEditText.setText(prefs.getString(PREF_DEVICE_ADDRESS,
				DEFAULT_DEVICE_ADDRESS));
		super.onPrepareDialog(id, dialog);
	}

	/**
	 * Send a blank data packet with the 'x' callback to Arduino.
	 * 
	 * @param v
	 */
	public void buttonClickLEDOff(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 'x', "");
	}

	/**
	 * Send a blank data packet with the 'o' callback to Arduino.
	 * 
	 * @param v
	 */
	public void buttonClickLEDOn(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 'o', "");
	}

}