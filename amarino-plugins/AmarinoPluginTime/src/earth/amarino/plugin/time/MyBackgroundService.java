package earth.amarino.plugin.time;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.plugin.BackgroundService;

public class MyBackgroundService extends BackgroundService{
	
	private static final String TAG = "MyBackgroundService";
	private static final boolean DEBUG = true;

	private Timer timer;
	private TimerTask task = new TimerTask(){

		@Override
		public void run() {
			Date now = new Date();
			int[] data = { now.getHours(), now.getMinutes(), now.getSeconds() };
			if (DEBUG) Log.d(TAG, "h:m:s - " + data[0] + ":" + data[1] + ":" + data[2]);
			
			Amarino.sendDataFromPlugin(MyBackgroundService.this, pluginId, data);
		}
		
	};
	
	public MyBackgroundService() {
		super(TAG, DEBUG); 
	}
	
	
	@Override
	public boolean init() {
		/* add your code here */
		
		// create a new timer instance
		timer = new Timer();
		
		// start the time to execute the task every 1000ms and start it with a delay of 1000ms
		timer.scheduleAtFixedRate(task, 1000, 1000);
		
		return true;
	}

	
	@Override
	public void cleanup() {
		/* add your code here */
		
		// stop the timer
		timer.cancel();
	}

	

}
