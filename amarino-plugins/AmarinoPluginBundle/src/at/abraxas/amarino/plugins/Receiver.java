package at.abraxas.amarino.plugins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;

public class Receiver extends BroadcastReceiver {
	
	
	public static final String ACTION_DISABLE_ALL = "amarino.plugins.action.DISABLE_ALL";

	private static final String TAG = "AmarinoPluginsReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) return;
		String action = intent.getAction();
		if (action == null) return;
		
		Intent i;
		
		String className = intent.getStringExtra(AmarinoIntent.EXTRA_PLUGIN_SERVICE_CLASS_NAME);
		if (className == null) {
			if (AmarinoIntent.ACTION_DISABLE.equals(action)){
				// disable all plugins
				i = new Intent(ACTION_DISABLE_ALL);
				i.setPackage(context.getPackageName());
				i.replaceExtras(intent);
				context.sendBroadcast(i);
			}
			
			return;
		}
		
		Log.d(TAG, "request for " + className);
		if (className.equals(context.getPackageName() + ".compass.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.compass.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".accelerometer.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.accelerometer.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".orientation.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.orientation.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".timetick.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.timetick.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".lightsensor.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.lightsensor.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".magneticfield.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.magneticfield.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".proximity.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.proximity.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".phonestate.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.phonestate.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".batterylevel.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.batterylevel.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".testevent.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.testevent.BackgroundService.class);
		}
		else if (className.equals(context.getPackageName() + ".sms.BackgroundService")){
			i = new Intent(context, at.abraxas.amarino.plugins.sms.BackgroundService.class);
		}
		else {
			return;
		}
		
		i.setAction(action); // this might be enable or disable, service should decide what to do
		i.replaceExtras(intent);
		context.startService(i);
	}

}
