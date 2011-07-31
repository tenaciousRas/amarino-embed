package edu.mit.media.amarino.multicolorlamp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class ChangeColorService extends Service {
	
	String deviceAddress;
	int color;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(connectionStateReceiver);
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
		if (intent != null){
			deviceAddress = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(MultiColorLamp.PREF_DEVICE_ADDRESS, MultiColorLamp.DEFAULT_DEVICE_ADDRESS);
			
			color = intent.getIntExtra(ColorReceiver.EXTRA_COLOR, 0);
			registerReceiver(connectionStateReceiver, new IntentFilter(AmarinoIntent.ACTION_CONNECTED));
			Amarino.connect(this, deviceAddress);
		}
		
		return Service.START_NOT_STICKY;
	}
	
	private BroadcastReceiver connectionStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null){
				String action = intent.getAction();
				if (AmarinoIntent.ACTION_CONNECTED.equals(action)){
					Amarino.sendDataToArduino(ChangeColorService.this, deviceAddress, MultiColorLamp.FLAG_RED, Color.red(color));
					Amarino.sendDataToArduino(ChangeColorService.this, deviceAddress, MultiColorLamp.FLAG_GREEN, Color.green(color));
					Amarino.sendDataToArduino(ChangeColorService.this, deviceAddress, MultiColorLamp.FLAG_BLUE, Color.blue(color));
					Amarino.disconnect(ChangeColorService.this, deviceAddress);
					stopSelf();
				}
			}
		}
	};

}
