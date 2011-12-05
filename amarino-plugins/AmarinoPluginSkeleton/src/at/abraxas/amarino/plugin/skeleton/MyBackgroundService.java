package at.abraxas.amarino.plugin.skeleton;

import android.preference.PreferenceManager;
import at.abraxas.amarino.intent.DefaultAmarinoServiceIntentConfig;
import at.abraxas.amarino.plugin.BackgroundService;

public class MyBackgroundService extends BackgroundService{
	
	private static final String TAG = "MyBackgroundService";
	private static final boolean DEBUG = true;

	
	public MyBackgroundService() {
		super(TAG, DEBUG); 
		
	}
	
	
	@Override
	public boolean init() {
		pluginId = PreferenceManager.getDefaultSharedPreferences(this)
			.getInt(DefaultAmarinoServiceIntentConfig.EXTRA_PLUGIN_ID, -1);
		
		/* add your code here */
		
		return false; // note: return true if init was successful
	}

	
	@Override
	public void cleanup() {
		/* add your code here */
		
	}

	

}
