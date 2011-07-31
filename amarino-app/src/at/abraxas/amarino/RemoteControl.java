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
package at.abraxas.amarino;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import at.abraxas.amarino.log.Logger;

/**
 * 
 * @author Bonifaz Kaufmann
 *
 * $Id: RemoteControl.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class RemoteControl extends BroadcastReceiver {
	
	private static final String TAG = "RemoteControl";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			String action = intent.getAction();
			if (action == null) return;
			
			if (AmarinoIntent.ACTION_CONNECT.equals(action)){
				Logger.d(TAG, "CONNECT request received");
				Intent i = new Intent(context, AmarinoService.class);
				i.setAction(AmarinoIntent.ACTION_CONNECT);
				i.replaceExtras(intent);
				context.startService(i);
			}
			else if (AmarinoIntent.ACTION_DISCONNECT.equals(action)){
				Logger.d(TAG, "DISCONNECT request received");
				Intent i = new Intent(context, AmarinoService.class);
				i.setAction(AmarinoIntent.ACTION_DISCONNECT);
				i.replaceExtras(intent);
				context.startService(i);
			}
			else if (AmarinoIntent.ACTION_GET_CONNECTED_DEVICES.equals(action)){
				Logger.d(TAG, "GET_CONNECTED_DEVICES request received");
				Intent i = new Intent(context, AmarinoService.class);
				i.setAction(AmarinoIntent.ACTION_GET_CONNECTED_DEVICES);
				context.startService(i);
			}
		}
	}

}
