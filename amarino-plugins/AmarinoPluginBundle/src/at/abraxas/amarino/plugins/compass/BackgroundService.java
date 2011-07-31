package at.abraxas.amarino.plugins.compass;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.plugins.AbstractPluginService;

public class BackgroundService extends AbstractPluginService 
			implements SensorEventListener, OnSharedPreferenceChangeListener {
	
	private static final String TAG = "Compass Plugin";

	private SensorManager sm;
	private Sensor orientationSensor;
	private int frequency;
	private int ignoreThreshold = 0;
	private int ignoreCounter = 0;
	private WakeLock wl;

	
	@Override
	public void init() {
		if (!pluginEnabled){
			/* here should be your specific initialization code */
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			frequency = prefs.getInt(EditActivity.PREF_FREQUENCY, 50);
			pluginId = prefs.getInt(EditActivity.KEY_PLUGIN_ID, -1);
			ignoreThreshold = EditActivity.getRate(frequency);
			
			prefs.registerOnSharedPreferenceChangeListener(this);
		
			// make sure not to call it twice
			sm = (SensorManager) getSystemService(SENSOR_SERVICE);
			List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ORIENTATION);
			if (sensors != null && sensors.size() > 0) {
				orientationSensor = sensors.get(0);
				sm.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
				pluginEnabled = true;
				
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Compass_WakeLock");
				// TODO some phones require dim wake lock to keep sensor alive during sleep
				wl.acquire();
			}
			else {
				Toast.makeText(this, "Orientation sensor is not available on this device!", Toast.LENGTH_SHORT).show();
			}
		}
	}


	@Override
	public void onDestroy() {
		if (pluginEnabled){
			wl.release();
			sm.unregisterListener(this);
			PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
		}
		super.onDestroy();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// we don't need this
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			
			if (ignoreCounter >= ignoreThreshold) {
				ignoreCounter = 0;
				int heading = (int)event.values[0];
				
				if (DEBUG) Log.d(TAG, "send: " + heading);
				Amarino.sendDataFromPlugin(this, pluginId, heading);

			}
			else {
				ignoreCounter++;
			}
		}
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {
		if (EditActivity.PREF_FREQUENCY.equals(key)){
			ignoreThreshold = EditActivity.getRate(prefs.getInt(key, 50));
		}
	}


	@Override
	public String getTAG() {
		return TAG;
	}
	
}
