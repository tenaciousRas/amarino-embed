package at.abraxas.amarino.plugins.lightsensor;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import at.abraxas.amarino.AmarinoIntent;
import at.abraxas.amarino.plugins.Constants;
import at.abraxas.amarino.plugins.R;

public class EditActivity extends Activity {
	
	static final String PREF_FREQUENCY = "at.abraxas.amarino.plugins.lightsensor.frequency";
	static final String KEY_PLUGIN_ID = "at.abraxas.amarino.plugins.lightsensor.id";
	static final String KEY_VISUALIZER = "at.abraxas.amarino.plugins.lightsensor.visualizer";
	
	private static final String TAG = "LightSensor EditActivity";
	
	Spinner visualizer;
	int pluginId;
	
	private boolean cancelled = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.lightsensor_edit);
        
        Intent intent = getIntent();
        if (intent != null){
        	pluginId = intent.getIntExtra(AmarinoIntent.EXTRA_PLUGIN_ID, -1);
	        
	        // we need to know the ID Amarino has assigned to this plugin
	        // in order to identify sent data
	        PreferenceManager.getDefaultSharedPreferences(EditActivity.this)
				.edit()
				.putInt(KEY_PLUGIN_ID, pluginId)
				.commit();
        }

        visualizer =(Spinner)findViewById(R.id.visualizer);
        // init as text visualizer, this is the most common one
        visualizer.setSelection(PreferenceManager.getDefaultSharedPreferences(this).getInt(KEY_VISUALIZER, 0));
        
        findViewById(R.id.saveBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelled = false;
				finish();
			}
		});
        
        findViewById(R.id.discardBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }

	@Override
	public void finish() {
		if (cancelled) {
			setResult(RESULT_CANCELED);
		}
		else {
			final Intent returnIntent = new Intent();
			
			returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_NAME, getString(R.string.lightsensor_plugin_name));
			returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_DESC, getString(R.string.lightsensor_plugin_desc));
			returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_SERVICE_CLASS_NAME, "at.abraxas.amarino.plugins.lightsensor.BackgroundService"); 
			returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_ID, pluginId);	
			
			int selectedVisualizer = visualizer.getSelectedItemPosition();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(KEY_VISUALIZER, selectedVisualizer).commit();
			
			switch(selectedVisualizer){
				case Constants.TEXT:
					returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_VISUALIZER, AmarinoIntent.VISUALIZER_TEXT);
					break;
				case Constants.GRAPH:
					returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_VISUALIZER, AmarinoIntent.VISUALIZER_GRAPH);
					break;
				case Constants.BARS:
					returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_VISUALIZER, AmarinoIntent.VISUALIZER_BARS);
					break;
			}
			
		
			returnIntent.putExtra(AmarinoIntent.EXTRA_VISUALIZER_MIN_VALUE, 0f);
			returnIntent.putExtra(AmarinoIntent.EXTRA_VISUALIZER_MAX_VALUE, 
					Constants.getMaxSensorRange(this, Sensor.TYPE_LIGHT, 11000f));
			
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}

}