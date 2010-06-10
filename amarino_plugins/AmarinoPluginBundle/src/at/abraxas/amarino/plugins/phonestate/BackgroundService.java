package at.abraxas.amarino.plugins.phonestate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;
import at.abraxas.amarino.plugins.AbstractPluginService;

public class BackgroundService extends AbstractPluginService {
	
	private static final String TAG = "PhoneState Plugin";
	
	TelephonyManager tm;
	private PhoneStateListener phoneStateListener = new PhoneStateListener(){

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state){
			case TelephonyManager.CALL_STATE_IDLE:
			case TelephonyManager.CALL_STATE_RINGING:
			case TelephonyManager.CALL_STATE_OFFHOOK:
				send(state);
				break;
			}
		}
		
	};
	
	private void send(int state){
		Intent i = new Intent(AmarinoIntent.ACTION_SEND);
		i.putExtra(AmarinoIntent.EXTRA_PLUGIN_ID, pluginId);
		i.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_EXTRA);
		i.putExtra(AmarinoIntent.EXTRA_DATA, state);
		
		if (DEBUG) Log.d(TAG, "send: " + state);
		sendBroadcast(i);
	}
	
	
	@Override
	public void init() {
		if (!pluginEnabled){
			/* here should be your specific initialization code */
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			pluginId = prefs.getInt(EditActivity.KEY_PLUGIN_ID, -1);
			pluginEnabled = true;
			
			tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
			tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}


	@Override
	public void onDestroy() {
		if (pluginEnabled){
			tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
			tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		super.onDestroy();
	}


	@Override
	public String getTAG() {
		return TAG;
	}
	
}
