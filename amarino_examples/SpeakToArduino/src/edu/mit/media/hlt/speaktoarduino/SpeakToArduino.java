package edu.mit.media.hlt.speaktoarduino;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SpeakToArduino extends Activity {
	
	private static final String TAG = "SpeakToArduino";
	private static final String LIGHT = "light";
	private static final String DARK = "dark";
	
	Button button;
	boolean lightOn = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button)findViewById(R.id.Button01);
        updateButtonText();
    }
    
    
    
    @Override
	protected void onStart() {
		super.onStart();
		Intent setCollection = new Intent("amarino.SET_COLLECTION");
		setCollection.putExtra("COLLECTION_NAME", "SpeakToArduino");
		sendBroadcast(setCollection);
		
		sendBroadcast(new Intent("amarino.CONNECT"));
	}



	@Override
	protected void onStop() {
		super.onStop();
		sendBroadcast(new Intent("amarino.DISCONNECT"));
	}



	public void buttonClick(View v){
    	// when the button was clicked we start the recognizer
    	startActivityForResult(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK && data != null){
			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			if (results != null){
				for (String result : results){
					Log.d(TAG, "recognized words:" + result);
					if (result.contains(LIGHT)){
						lightOn = true;
						// hit! light was recognized
						vibrate(200);
						
						// inform Arduino 
						// make sure Amarino is running, conneted and has a matching custom event
						// in its active collection.
						Intent i;
						// I reuse here custom events from the multicolor lamp tutorial
						if (result.contains("red")){
							i = new Intent("amarino.multicolorlamp.red");
						}
						else if (result.contains("green")){
							i = new Intent("amarino.multicolorlamp.green");
						}
						else if (result.contains("blue")){
							i = new Intent("amarino.multicolorlamp.blue");
						}
						else 
							i = new Intent("arduino.light");
						
						sendBroadcast(i);
						
						updateButtonText();
						break;
					}
					else if (result.contains(DARK)){
						lightOn = false;
						// hit! dark was recognized
						vibrate(200);
						Intent i = new Intent("arduino.dark");
						sendBroadcast(i);
						
						updateButtonText();
						break;
					}
				}
			}
		}
	}
	
	private void updateButtonText(){
		if (lightOn){
			button.setText(getString(R.string.button_text, "dark"));
		}
		else {
			button.setText(getString(R.string.button_text, "light"));
		}
	}
	
	private void vibrate(long time){
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(time);
	}
    
    
}