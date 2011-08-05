package com.longevitysoft.android.rgbledsliders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import at.abraxas.amarino.AmarinoConfigured;
import at.abraxas.amarino.log.Logger;

import com.longevitysoft.android.rgbledsliders.ColorPickerView.OnColorChangedListener;

/**
 * @author fbeachler
 * 
 */
public class Main extends FragmentActivity {

	public static final String TAG = "Main";
	public static final String BT_DEVICE_ADDRESS = "00:06:66:06:BF:36"; // bluesmirf
																		// MAC
																		// addy
	public static final int NUMBER_OF_LEDS = 3; // number of sliders in UI

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

	/**
	 * The embedded Amarino service. If used globally consider moving this to an
	 * instance of {@link android.app.Application}.
	 */
	private AmarinoConfigured embeddedAmarino;

	private ArduinoReceiver receiver;
	private ServiceIntentConfig intentConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout();
		receiver = new ArduinoReceiver();
		intentConfig = new ServiceIntentConfig();
		registerReceiver(receiver,
				new IntentFilter(intentConfig.getIntentNameActionConnect()));
		registerReceiver(receiver,
				new IntentFilter(intentConfig.getIntentNameActionReceived()));
		Log.v(TAG, new StringBuilder("bluetooth receiver intent registered")
				.append(BT_DEVICE_ADDRESS).toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		arduinoConnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if (null != receiver) {
			try {
				unregisterReceiver(receiver);
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	}

	/**
	 * Setup the fragment layout.
	 */
	protected void initLayout() {
		setContentView(R.layout.main);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		int viewIds[] = { R.id.rgbslider01, R.id.rgbslider02, R.id.rgbslider03 };
		for (int i = 0; i < NUMBER_OF_LEDS; i++) {
			Slider slider = new Slider();
			slider.setLedIndex(i);
			slider.setOnChangeListener(new OnColorChangedListener() {

				@Override
				public void onColorChanged(int ledIndex, int color) {
					setArduinoLEDColor(ledIndex, color);
				}
			});
			fragmentTransaction.replace(viewIds[i], slider);
		}
		fragmentTransaction.commit();
	}

	/**
	 * Connect to Arudino device sans error handling + assume success, yay!
	 */
	private void arduinoConnect() {
		// create an instance of embeddedAmarino to wrap BT
		// intent broadcasts to service
		embeddedAmarino = new AmarinoConfigured(this.getApplicationContext());
		embeddedAmarino.setIntentConfig(intentConfig);
		embeddedAmarino.connect(BT_DEVICE_ADDRESS);
	}

	/**
	 * Calls 'c' callback registered by Amarino on the Arduino board, with LED
	 * color value.
	 * 
	 * @param ledIndex
	 * @param value
	 */
	public void setArduinoLEDColor(int ledIndex, int color) {
		// take lower 24 bits
		int color32 = (color << 8) >> 8;
		int vals[] = { ledIndex, color32 };
		embeddedAmarino.sendDataToArduino(BT_DEVICE_ADDRESS, 'c', vals);
	}
}