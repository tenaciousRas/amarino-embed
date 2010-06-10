package at.abraxas.amarino.plugins.proximity;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import at.abraxas.amarino.AmarinoIntent;
import at.abraxas.amarino.plugins.AbstractPluginService;
import at.abraxas.amarino.plugins.Receiver;

public class BackgroundService extends AbstractPluginService 
			implements SensorEventListener {
	
	private static final String TAG = "ProximitySensor Plugin";
	
	private SensorManager sm;
	private Sensor proximitySensor;
	
	@Override
	public void init() {
		if (!pluginEnabled){
			/* here should be your specific initialization code */
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			pluginId = prefs.getInt(EditActivity.KEY_PLUGIN_ID, -1);
		
			// make sure not to call it twice
			sm = (SensorManager) getSystemService(SENSOR_SERVICE);
			List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_PROXIMITY);
			if (sensors != null && sensors.size() > 0) {
				proximitySensor = sensors.get(0);
				sm.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
				pluginEnabled = true;
			}
			else {
				Toast.makeText(this, "Proximity sensor is not available on this device!", Toast.LENGTH_SHORT).show();
			}
		}
	}


	@Override
	public void onDestroy() {
		if (pluginEnabled){
			sm.unregisterListener(this);
		}
		super.onDestroy();
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// we don't need this
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){

			int cm = (int) event.values[0];
			
			Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
			intent.putExtra(AmarinoIntent.EXTRA_PLUGIN_ID, pluginId);
			intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_EXTRA);
			intent.putExtra(AmarinoIntent.EXTRA_DATA, cm);
			
			if (DEBUG) Log.d(TAG, "send: " + cm);
			sendBroadcast(intent);

		}
	}


	@Override
	public String getTAG() {
		return TAG;
	}


	
}
