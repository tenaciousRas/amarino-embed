package at.abraxas.amarino.plugins.testevent;

import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.plugins.AbstractPluginService;

public class BackgroundService extends AbstractPluginService {
	
	private static final String TAG = "TestEvent Plugin";
	Timer tt;

	private void sendTestEvent(){
		int random = (int) (Math.random() * 255.0);
		if (DEBUG) Log.d(TAG, "send: " + random);
		Amarino.sendDataFromPlugin(this, pluginId, random);
	}

	@Override
	public void init() {
		if (!pluginEnabled){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			pluginId = prefs.getInt(EditActivity.KEY_PLUGIN_ID, -1);
			pluginEnabled = true;
			
			/* here should be your specific initialization code */
			tt = new Timer();
			tt.scheduleAtFixedRate(new TimerTask() {
				
				@Override
				public void run() {
					sendTestEvent();
				}
			}, 2000, 3000); // start after 2 sec, repeat execution every 3 seconds
		}
	}


	@Override
	public void onDestroy() {
		if (pluginEnabled){
			/* clean up here */
			tt.cancel();
		}
		super.onDestroy();
	}

	@Override
	public String getTAG() {
		return TAG;
	}
	
	


}
