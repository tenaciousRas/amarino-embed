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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class PluginReceiver extends BroadcastReceiver {

	public Class<?> serviceClass;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null)
			return;
		
		String action = intent.getAction();
		if (action == null)
			return;

		Intent i = new Intent(context, serviceClass);
		i.setAction(action); // this might be ACTION_ENABLE or ACTION_DISABLE,
								// but your service should decide what to do
		i.replaceExtras(intent);
		context.startService(i);
	}

}
