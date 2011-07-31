package edu.mit.media.amarino.multicolorlamp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ColorReceiver extends BroadcastReceiver {
	
	public static final String ACTION_SET_COLOR = "amarino.multicolorlamp.SET_COLOR";
	public static final String EXTRA_COLOR = "value";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) return;

		if (ACTION_SET_COLOR.equals(intent.getAction())){
			Log.d(MultiColorLamp.TAG, "Received set color intent");
			Intent i = new Intent(context, ChangeColorService.class);
			i.replaceExtras(intent);
			context.startService(i);
		}
	}

}
