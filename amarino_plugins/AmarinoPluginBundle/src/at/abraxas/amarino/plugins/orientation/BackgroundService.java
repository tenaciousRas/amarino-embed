package at.abraxas.amarino.plugins.orientation;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.plugins.AbstractPluginService;

public class BackgroundService extends AbstractPluginService 
			implements SensorEventListener, OnSharedPreferenceChangeListener {
	
	private static final String TAG = "Orientation Plugin";
	
	private SensorManager sm;
	private Sensor orientationSensor;
	private int frequency;
	private int ignoreThreshold = 0;
	private int ignoreCounter = 0;
	
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
			}
			else {
				Toast.makeText(this, "Accelerometer sensor is not available on this device!", Toast.LENGTH_SHORT).show();
			}
		}
	}


	@Override
	public void onDestroy() {
		if (pluginEnabled){
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
				
				if (DEBUG) Log.d(TAG, "send: x:" + event.values[0] + " y:" + event.values[1] + " z: " + event.values[2]);
				Amarino.sendDataFromPlugin(this, pluginId, event.values.clone());
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
