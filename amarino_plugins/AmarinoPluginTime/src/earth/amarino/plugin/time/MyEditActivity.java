package earth.amarino.plugin.time;

import android.view.View;
import android.view.ViewStub;

public class MyEditActivity extends EditActivity {


	@Override
	public void init() {
		ViewStub stub = (ViewStub) findViewById(R.id.plugin_edit);
		stub.inflate();
		
		/* add your code here */
		
		// since we inflated the stub layout (plugin_edit.xml)
		// you can get references to your widgets in plugin_edit layout by calling "stub.findViewById(R.id.id_of_your_widget)"
		
		
	}

	@Override
	public void onSaveBtnClick(View v) {
		
		// TODO set visualizer bounds according to your expected data, otherwise it won't be displayed correctly
		setVisualizerBounds(0, 59);
		
		
		/* add your code here */

		

		// sendResult sends the information Amarino needs to display your plug-in properly
		// hint: as you can see in the super class EditActivity there is some more information passed to Amarino
		sendResult(R.string.plugin_name, R.string.plugin_desc, R.string.service_class_name);
	}

	
	@Override
	public void onCancelBtnClick(View v) {
		/* add your code here */

	}

}
