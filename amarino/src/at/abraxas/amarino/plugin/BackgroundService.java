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
package at.abraxas.amarino.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;

public abstract class BackgroundService extends Service {
	
	private static String TAG;
	private static boolean DEBUG;
	
	/**
	 * Unique id assigned by Amarino to identify this plug-in instance 
	 */
	public int pluginId;
	
	
	/**
	 * true if the plug-in has been enabled, false otherwise
	 */
	public boolean pluginEnabled = false;
	
	public BackgroundService(String tag, boolean debug){
		super();
		TAG = tag;
		DEBUG = debug;
		
	}
	

	private void initInternal() {
		if (!pluginEnabled){
			pluginEnabled = init();
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (DEBUG) Log.d(TAG, "onCreate");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (pluginEnabled){
			cleanup();
		}
		Log.d(TAG, "stopped");
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
	
	private int handleStart(Intent intent, int startId) {
		if (intent == null) {
			if (DEBUG) Log.d(TAG, "service restarted");
			// service was restarted after it was killed by the system due to low memory condition
			initInternal();
		}
		else {
			String action = intent.getAction();
			if (DEBUG) Log.d(TAG, action + " received");

			if (AmarinoIntent.ACTION_DISABLE.equals(action)){
				if (DEBUG) Log.d(TAG, "stop requested");
				stopSelf();
			}
			else if (AmarinoIntent.ACTION_ENABLE.equals(action)) {
				Log.d(TAG, "started");
				initInternal();
			}
		}

		return START_STICKY;
	}
	
	
	/**
	 * This method should implement your initialization sequence (register and start sensors, etc).
	 * The init method is called directly after receiving the ENABLE message from Amarino.
	 * 
	 * Return true if your plug-in could be initialized without errors and is up and running.
	 * Return false if your initialization failed.
	 * 
	 * @return true if plug-in could be enabled successfully, otherwise false
	 */
	public abstract boolean init();
	
	/**
	 * When this service receives a DISABLE message from Amarino it calls cleanup().
	 * But only if your init() method returned true, otherwise cleanup is omitted.
	 */
	public abstract void cleanup();
}
