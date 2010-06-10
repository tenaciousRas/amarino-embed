package at.abraxas.amarino.plugins;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;

public abstract class AbstractPluginService extends Service {
	
	protected static final boolean DEBUG = false;
	
	// unique id to identify the plug-in
	protected int pluginId;
	
	protected boolean pluginEnabled = false;
	
	BroadcastReceiver disableReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) return;
			String action = intent.getAction();
			if (Receiver.ACTION_DISABLE_ALL.equals(action)){
				stopSelf();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		if (DEBUG) Log.d(getTAG(), "onCreate");
		
		registerReceiver(disableReceiver, new IntentFilter(Receiver.ACTION_DISABLE_ALL));
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		return handleStart(intent, startId);
    }
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		handleStart(intent, startId);
	}
	
	
	int handleStart(Intent intent, int startId) {
		if (intent == null) {
			// service was restarted after it was killed by the system due to low memory condition
			init();
		}
		else {
			String action = intent.getAction();
			if (DEBUG) Log.d(getTAG(), action + " received");

			if (AmarinoIntent.ACTION_DISABLE.equals(action)){
				stopSelf();
			}
			else if (AmarinoIntent.ACTION_ENABLE.equals(action)) {
				Log.d(getTAG(), "started");
				init();
			}
		}

		return START_STICKY;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(disableReceiver);
		Log.d(getTAG(), "stopped");
	}
	
	abstract public void init();
	
	abstract public String getTAG();

}
