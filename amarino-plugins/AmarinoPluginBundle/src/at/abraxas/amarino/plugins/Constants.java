package at.abraxas.amarino.plugins;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Constants {
	
	public static final int TEXT = 0;
	public static final int GRAPH = 1;
	public static final int BARS = 2;
	
	public static float getMaxSensorRange(Context context, int sensorType, float defaultValue){
		float max = defaultValue; 
		SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sm.getSensorList(sensorType);
		
		if (sensors != null && sensors.size() > 0)
			max = sensors.get(0).getMaximumRange();
		
		return max;
	}

}
