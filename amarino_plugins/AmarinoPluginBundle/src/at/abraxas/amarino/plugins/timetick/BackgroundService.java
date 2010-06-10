package at.abraxas.amarino.plugins.timetick;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;
import at.abraxas.amarino.plugins.AbstractPluginService;

public class BackgroundService extends AbstractPluginService {
	
	private static final String TAG = "TimeTick Plugin";
	
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int minutes = new Date().getMinutes();
			
			Intent i = new Intent(AmarinoIntent.ACTION_SEND);
			i.putExtra(AmarinoIntent.EXTRA_PLUGIN_ID, pluginId);
			i.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_EXTRA);
			i.putExtra(AmarinoIntent.EXTRA_DATA, minutes);
			
			if (DEBUG) Log.d(TAG, "send: " + minutes);
			sendBroadcast(i);
		}
	};
	
	
	@Override
	public void init() {
		if (!pluginEnabled){
			/* here should be your specific initialization code */
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			pluginId = prefs.getInt(EditActivity.KEY_PLUGIN_ID, -1);
			
			IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
			registerReceiver(receiver, filter);

			pluginEnabled = true;
		}
	}


	@Override
	public void onDestroy() {
		if (pluginEnabled){
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}


}
