/*
  Amarino - A prototyping software toolkit for Android and Arduino
  Copyright (c) 2010 Bonifaz Kaufmann.  All right reserved.
  
  This application and its library is free software; you can redistribute
  it and/or modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package at.abraxas.amarino.plugin;

import android.app.Activity;
import android.os.Bundle;
import at.abraxas.amarino.R;

/**
 * <p>Since an Amarino plug-in is not a standalone application,
 * people might get confused that it does not start when they hit open.
 * Instead, an info screen will be shown explaining the fact that this plug-in is designed to run within Amarino 2.0.</p>
 * 
 * <p>You might want to write your own InfoActivity,
 * but for the sake of simplicity this InfoActivity provides you a basic info screen.</p>
 * 
 * <p>You need to add this activity to your AndroidManifest.xml file as shown below.</p>
 * 
 * <p>
 * <pre>
 * {@code
 * <activity android:name="at.abraxas.amarino.plugin.InfoActivity" >
 *      <intent-filter>
 *          <action android:name="android.intent.action.MAIN" />
 *          <category android:name="android.intent.category.INFO" />
 *      </intent-filter>
 * </activity>
 * }
 * </pre>
 * </p>
 * <p>
 * @author Bonifaz Kaufmann
 * </p>
 *
 * $Id$
 */
public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
	}

}
