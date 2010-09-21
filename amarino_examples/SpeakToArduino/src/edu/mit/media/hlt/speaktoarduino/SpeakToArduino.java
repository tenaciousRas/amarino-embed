package edu.mit.media.hlt.speaktoarduino;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;

public class SpeakToArduino extends Activity {
	
	public static final String DEFAULT_DEVICE_ADDRESS = "00:06:66:03:73:7B";
	
	public static final String PREFS = "multicolorlamp";
	public static final String PREF_DEVICE_ADDRESS = "device_address";
	
	private static final int DIALOG_DEVICE_ADDRESS = 1;
	private static final int addressEditTextId = 15;
	
	private static final String TAG = "SpeakToArduino";
	private static final long VIB_TIME = 100;
	

	Button button;
	String deviceAddress;
	SharedPreferences prefs;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button)findViewById(R.id.Button01);
        button.setText("Click and order your color");
        
        // get device address
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        deviceAddress = prefs.getString(PREF_DEVICE_ADDRESS, DEFAULT_DEVICE_ADDRESS);
    }
    
    
    
    @Override
	protected void onStart() {
		super.onStart();
		Amarino.connect(this, deviceAddress);
	}



	@Override
	protected void onStop() {
		super.onStop();
		Amarino.disconnect(this, deviceAddress);
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

					if (result.contains("red") || result.contains("read")){
						Amarino.sendDataToArduino(this, deviceAddress, 'c', new int[]{255,0,0});
						button.setText("red");
						vibrate(VIB_TIME);
					}
					else if (result.contains("green")){
						Amarino.sendDataToArduino(this, deviceAddress, 'c', new int[]{0,255,0});
						button.setText("green");
						vibrate(VIB_TIME);
					}
					else if (result.contains("blue")){
						Amarino.sendDataToArduino(this, deviceAddress, 'c', new int[]{0,0,255});
						button.setText("blue");
						vibrate(VIB_TIME);
					}
					else if (result.contains("white") || result.contains("on")){
						Amarino.sendDataToArduino(this, deviceAddress, 'c', new int[]{255,255,255});
						button.setText("white");
						vibrate(VIB_TIME);
					}
					else if (result.contains("pink")){
						Amarino.sendDataToArduino(this, deviceAddress, 'c', new int[]{255,0,190});
						button.setText("pink");
						vibrate(VIB_TIME);
					}
					else if (result.contains("orange")){
						Amarino.sendDataToArduino(this, deviceAddress, 'c', new int[]{255,170,0});
						button.setText("orange");
						vibrate(VIB_TIME);
					}
					else if (result.contains("yellow")){
						Amarino.sendDataToArduino(this, deviceAddress, 'c', new int[]{255,255,0});
						button.setText("yellow");
						vibrate(VIB_TIME);
					}
					else if (result.contains("dark") || result.contains("off")){
						Amarino.sendDataToArduino(this, deviceAddress, 'c', new int[]{0,0,0});
						button.setText("off");
						vibrate(VIB_TIME);
					}

					break;
				}
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
	    return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
	    switch (item.getItemId()) {
		    case R.id.device_address:
		        showDialog(DIALOG_DEVICE_ADDRESS);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id){
		
			case DIALOG_DEVICE_ADDRESS:
				final EditText addressEditText = new EditText(this);
				addressEditText.setId(addressEditTextId);
				addressEditText.setText(deviceAddress);
				
				return new AlertDialog.Builder(this)
					.setTitle(R.string.device_address)
					.setMessage(R.string.set_device_address)
					.setView(addressEditText)
					.setPositiveButton("Save", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String address = addressEditText.getEditableText().toString();
							if (Amarino.isCorrectAddressFormat(address)){
								prefs.edit()
									.putString(PREF_DEVICE_ADDRESS, address)
								.commit();
							}
							else {
								Toast.makeText(SpeakToArduino.this, R.string.device_address_format_error, Toast.LENGTH_LONG).show();
							}
							
						}
					})
					.setNegativeButton("Discard", null)
					.create();
				
			default:
				return super.onCreateDialog(id);
		}
		
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		EditText addressEditText = (EditText) dialog.findViewById(addressEditTextId);
		addressEditText.setText(prefs.getString(PREF_DEVICE_ADDRESS, DEFAULT_DEVICE_ADDRESS));
		super.onPrepareDialog(id, dialog);
	}
	

	
	private void vibrate(long time){
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(time);
	}
	
	
    
    
}