package at.abraxas.amarino.plugins.magneticfield;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import at.abraxas.amarino.AmarinoIntent;
import at.abraxas.amarino.plugins.Constants;
import at.abraxas.amarino.plugins.R;

public class EditActivity extends Activity {
	
	static final String PREF_FREQUENCY = "at.abraxas.amarino.plugins.magneticfield.frequency";
	static final String KEY_PLUGIN_ID = "at.abraxas.amarino.plugins.magneticfield.id";
	static final String KEY_VISUALIZER = "at.abraxas.amarino.plugins.magneticfield.visualizer";
	
	private static final String TAG = "MagneticField EditActivity";
	
	Spinner visualizer;
	TextView frequency; 
	SeekBar frequencySB;
	Button saveBtn;
	Button discardBtn;
	int pluginId;
	
	private boolean cancelled = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.magneticfield_edit);
        
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
        
        frequencySB = (SeekBar)findViewById(R.id.seekBar);
        frequency = (TextView)findViewById(R.id.seekBar_value);
        saveBtn = (Button)findViewById(R.id.saveBtn);
        discardBtn = (Button)findViewById(R.id.discardBtn);
        
        int lastValue = PreferenceManager.getDefaultSharedPreferences(this).getInt(PREF_FREQUENCY, 50);
        frequencySB.setProgress(lastValue);
        int rate = EditActivity.getRate(lastValue);
		frequency.setText(getRateText(rate));
        
        frequencySB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				int rate = EditActivity.getRate(progress);
				frequency.setText(getRateText(rate));
			}
		});
        
        saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PreferenceManager.getDefaultSharedPreferences(EditActivity.this)
					.edit()
					.putInt(PREF_FREQUENCY, frequencySB.getProgress())
					.commit();
				cancelled = false;
				finish();
			}
		});
        
        discardBtn.setOnClickListener(new OnClickListener() {
			
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
			
			returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_NAME, getString(R.string.magneticfield_plugin_name));
			returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_DESC, getString(R.string.magneticfield_plugin_desc));
			returnIntent.putExtra(AmarinoIntent.EXTRA_PLUGIN_SERVICE_CLASS_NAME, "at.abraxas.amarino.plugins.magneticfield.BackgroundService"); 
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
			
			float range = Constants.getMaxSensorRange(this, Sensor.TYPE_MAGNETIC_FIELD, 2200f);
			returnIntent.putExtra(AmarinoIntent.EXTRA_VISUALIZER_MIN_VALUE, -range);
			returnIntent.putExtra(AmarinoIntent.EXTRA_VISUALIZER_MAX_VALUE, range);
			
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}
	
	private String getRateText(int rate){
		String text = new String();
		
		switch(rate){
		case 8: text = getString(R.string.very_slow); break;
		case 4: text = getString(R.string.slow); break;
		case 2: text = getString(R.string.medium); break;
		case 1: text = getString(R.string.fast); break;
		case 0: text = getString(R.string.very_fast); break;
		}
		return text;
	}
	
	protected static int getRate(int frequency) {
		int rate = 0;
		if (frequency < 20) 		rate = 8;
		else if (frequency < 40) 	rate = 4;
		else if (frequency < 60) 	rate = 2;
		else if (frequency < 80) 	rate = 1;
		else 						rate = 0;
		return rate;
	}
    
    
}