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
package at.abraxas.amarino.intent;


/**
 * DefaultAmarinoServiceIntentConfig is a collection of Intents and Extras used by
 * Amarino to perform actions. You can either use these intents directly or you
 * can use the convenient functions in {@link at.abraxas.amarino.Amarino} to
 * communicate with Amarino. The way your application communicates with Amarino
 * happens exclusively via Intents. For more information about Intents in
 * general please refer to the <a
 * href="http://developer.android.com/reference/android/content/Intent.html"
 * >Android Reference</a> and learn everything about Intents.
 * 
 * <p>
 * The most important intents in Amarino are:
 * </p>
 * 
 * <ul>
 * <li>{@link #ACTION_CONNECT} - connect to a Bluetooth device</li>
 * <li>{@link #ACTION_DISCONNECT} - disconnect from a Bluetooth device</li>
 * <li>{@link #ACTION_SEND} - send data to Arduino</li>
 * <li>{@link #ACTION_RECEIVED} - received data from Arduino</li>
 * </ul>
 * 
 * <p>
 * The {@link #ACTION_CONNECT}, {@link #ACTION_DISCONNECT} and
 * {@link #ACTION_SEND} intents are supposed to be broadcasted by your
 * application in order to use Amarino to fulfill your request.
 * {@link #ACTION_RECEIVED} however is used the other way around. Amarino will
 * broadcast this intent if it has received new data from Arduino. If you want
 * to receive these data, your application has to implement a <a href=
 * "http://developer.android.com/reference/android/content/BroadcastReceiver.html"
 * >BroadcastReceiver</a> catching the {@link #ACTION_RECEIVED} intent.</br>
 * SensorGraph is a neat example demonstrating the use of
 * {@link #ACTION_CONNECT}, {@link #ACTION_DISCONNECT} and
 * {@link #ACTION_RECEIVED}.
 * 
 * <h3>Intents broadcasted by Amarino for feedback</h3>
 * <p>
 * Sometimes it is important to have feedback if an operation was successful or
 * not. Amarino normally provides feedback by broadcasting intents with detailed
 * information. We already have heart about {@link #ACTION_RECEIVED} which is
 * indeed also a feedback intent.
 * </p>
 * <p>
 * Feedback intents are:
 * </p>
 * 
 * <ul>
 * <li>{@link #ACTION_RECEIVED} - Amarino received data from Arduino</li>
 * <li>{@link #ACTION_CONNECTED} - connection has been established</li>
 * <li>{@link #ACTION_DISCONNECTED} - disconnected from a device</li>
 * <li>{@link #ACTION_CONNECTION_FAILED} - connection attempt was not successful
 * </li>
 * <li>{@link #ACTION_PAIRING_REQUESTED} - a notification message to pair the
 * device has popped up</li>
 * <li>{@link #ACTION_CONNECTED_DEVICES} - the list of connected devices is
 * broadcasted after a request was made using
 * {@link #ACTION_GET_CONNECTED_DEVICES}</li>
 * </ul>
 * 
 * <p>
 * To receive feedback your have to register a <a href=
 * "http://developer.android.com/reference/android/content/BroadcastReceiver.html"
 * >BroadcastReceiver</a> for information you are interested in.
 * </p>
 * 
 * <p>
 * 
 * @author Bonifaz Kaufmann
 * @author Free Beachler
 *         </p>
 *         $Id: DefaultAmarinoServiceIntentConfig.java 444 2010-06-10 13:11:59Z abraxas
 *         $
 */
public class DefaultAmarinoServiceIntentConfig implements AmarinoServiceIntentConfig {

	public static final String ACTION_CONNECT = "amarino.intent.action.CONNECT";
	public static final String ACTION_DISCONNECT = "amarino.intent.action.DISCONNECT";
	public static final String ACTION_SEND = "amarino.intent.action.SEND";
	public static final String ACTION_RECEIVED = "amarino.intent.action.RECEIVED";
	public static final String ACTION_CONNECTED = "amarino.intent.action.CONNECTED";
	public static final String ACTION_DISCONNECTED = "amarino.intent.action.DISCONNECTED";
	public static final String ACTION_CONNECTION_FAILED = "amarino.intent.action.CONNECTION_FAILED";
	public static final String ACTION_PAIRING_REQUESTED = "amarino.intent.action.PAIRING_REQUESTED";
	public static final String ACTION_GET_CONNECTED_DEVICES = "amarino.intent.action.ACTION_GET_CONNECTED_DEVICES";
	public static final String ACTION_CONNECTED_DEVICES = "amarino.intent.action.ACTION_CONNECTED_DEVICES";
	public static final String ACTION_ENABLE = "amarino.intent.action.ENABLE";
	public static final String ACTION_DISABLE = "amarino.intent.action.DISABLE";
	public static final String ACTION_DISABLE_ALL = "amarino.intent.action.DISABLE_ALL";
	public static final String ACTION_EDIT_PLUGIN = "amarino.intent.action.EDIT_PLUGIN";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionConnect
	 * ()
	 */
	@Override
	public String getIntentNameActionConnect() {
		return ACTION_CONNECT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionDisconnect
	 * ()
	 */
	@Override
	public String getIntentNameActionDisconnect() {
		return ACTION_DISCONNECT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionSend()
	 */
	@Override
	public String getIntentNameActionSend() {
		return ACTION_SEND;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionReceived
	 * ()
	 */
	@Override
	public String getIntentNameActionReceived() {
		return ACTION_RECEIVED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionConnected
	 * ()
	 */
	@Override
	public String getIntentNameActionConnected() {
		return ACTION_CONNECTED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionDisconnected
	 * ()
	 */
	@Override
	public String getIntentNameActionDisconnected() {
		return ACTION_DISCONNECTED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.abraxas.amarino.intent.AmarinoServiceIntentConfig#
	 * getIntentNameActionConnectionFailed()
	 */
	@Override
	public String getIntentNameActionConnectionFailed() {
		return ACTION_CONNECTION_FAILED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.abraxas.amarino.intent.AmarinoServiceIntentConfig#
	 * getIntentNameActionPairingRequested()
	 */
	@Override
	public String getIntentNameActionPairingRequested() {
		return ACTION_PAIRING_REQUESTED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.abraxas.amarino.intent.AmarinoServiceIntentConfig#
	 * getIntentNameActionGetConnectedDevices()
	 */
	@Override
	public String getIntentNameActionGetConnectedDevices() {
		return ACTION_GET_CONNECTED_DEVICES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.abraxas.amarino.intent.AmarinoServiceIntentConfig#
	 * getIntentNameActionConnectedDevices()
	 */
	@Override
	public String getIntentNameActionConnectedDevices() {
		return ACTION_CONNECTED_DEVICES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionEnable()
	 */
	@Override
	public String getIntentNameActionEnable() {
		return ACTION_ENABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionDisable
	 * ()
	 */
	@Override
	public String getIntentNameActionDisable() {
		return ACTION_DISABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionDisableAll
	 * ()
	 */
	@Override
	public String getIntentNameActionDisableAll() {
		return ACTION_DISABLE_ALL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.abraxas.amarino.intent.AmarinoServiceIntentConfig#getIntentNameActionEditPlugin
	 * ()
	 */
	@Override
	public String getIntentNameActionEditPlugin() {
		return ACTION_EDIT_PLUGIN;
	}

}
