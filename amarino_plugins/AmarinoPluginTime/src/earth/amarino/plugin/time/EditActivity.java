package earth.amarino.plugin.time;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import at.abraxas.amarino.AmarinoIntent;

public abstract class EditActivity extends Activity {

	static final String KEY_VISUALIZER = "visualizer";
	
	private static final String TAG = "EditActivity";
	private static final boolean DEBUG = true;
	
	/**
	 * Unique identifier given by Amarino for this plug-in instance.
	 */
	protected int pluginId;
	
	private Button saveBtn;
	private Button discardBtn;
	private Spinner visualizer;
	private boolean cancelled = false;
	private float visualizerMin = 0f;
	private float visualizerMax = 1f;
	
	/**
	 * <p>This is where you should do your initialization.</p>
	 * 
	 * <p>Called as last statement in onCreate().</p>
	 */
	public abstract void init();
	
	/**
	 * Method gets called when the user clicks on the save button
	 * 
	 * @param v the button view
	 */
	public abstract void onSaveBtnClick(View v);
	
	/**
	 * Method gets called when the user clicks on the cancel button
	 * 
	 * @param v the button view
	 */
	public abstract void onCancelBtnClick(View v);
	
	

	private OnClickListener saveBtnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onSaveBtnClick(v);
			finish();
		}
	};
	
	private OnClickListener cancelBtnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			cancelled = true;
			onCancelBtnClick(v);
			finish();
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit);
        
        visualizer =(Spinner)findViewById(R.id.visualizer);
        // init as text visualizer, this is the most common one
        visualizer.setSelection(PreferenceManager.getDefaultSharedPreferences(this).getInt(KEY_VISUALIZER, 0));
        
        Intent intent = getIntent();
        if (intent != null){
        	pluginId = intent.getIntExtra(AmarinoIntent.EXTRA_PLUGIN_ID, -1);
	        if (DEBUG) Log.d(TAG, "pluginId: " + pluginId);
	        // we need to know the ID Amarino has assigned to this plugin
	        // in order to identify sent data
	        PreferenceManager.getDefaultSharedPreferences(EditActivity.this)
				.edit()
				.putInt(AmarinoIntent.EXTRA_PLUGIN_ID, pluginId)
				.commit();
        }

        saveBtn = (Button)findViewById(R.id.saveBtn);
        discardBtn = (Button)findViewById(R.id.discardBtn);

        saveBtn.setOnClickListener(saveBtnClick);
        discardBtn.setOnClickListener(cancelBtnClick);
        
        init();
        
    }

	@Override
	public void finish() {
		if (cancelled) {
			setResult(RESULT_CANCELED);
		}
		super.finish();
	}
	
	
	/**
	 * <p>Most Amarino visualizers need lower and upper bounds to display correctly.
	 * You have to set them depending on which values you expect to send over to Amarino.</p>
	 * 
	 * <p>For example the graph visualizer is written to display the min value at the bottom of the graph,
	 * whether the max value is drawn at the very top (of the y-axis)</p>
	 * 
	 * @param min minimum value you expect
	 * @param max maximum value you expect
	 */
	public void setVisualizerBounds(float min, float max) {
		visualizerMin = min;
		visualizerMax = max;
	}
	
	
	public void sendResult(int pluginNameRes, int descriptionRes, int serviceClassNameRes){
		sendResult(getString(pluginNameRes), getString(descriptionRes), getString(serviceClassNameRes));
	}
	
	public void sendResult(String pluginName, String description, String serviceClassName){
		
		final Intent returnIntent = new Intent();
		
		returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_NAME, pluginName);
		returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_DESC, description);
		returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_SERVICE_CLASS_NAME, serviceClassName);
		returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_ID, pluginId);	
		returnIntent.putExtra(AmarinoIntent.EXTRA_VISUALIZER_MIN_VALUE, visualizerMin);
		returnIntent.putExtra(AmarinoIntent.EXTRA_VISUALIZER_MAX_VALUE, visualizerMax);
		
		int selectedVisualizer = visualizer.getSelectedItemPosition();
		PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(KEY_VISUALIZER, selectedVisualizer).commit();
		
		switch(selectedVisualizer){
			case 0:
				returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_VISUALIZER, AmarinoIntent.VISUALIZER_TEXT);
				break;
			case 1:
				returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_VISUALIZER, AmarinoIntent.VISUALIZER_GRAPH);
				break;
			case 2:
				returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_VISUALIZER, AmarinoIntent.VISUALIZER_BARS);
				break;
		}

		setResult(RESULT_OK, returnIntent);
	}
	
    
}