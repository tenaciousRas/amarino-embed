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

/**
 * AmarinoIntent is a collection of Intents and Extras used by Amarino to
 * perform actions. You can either use these intents directly or you can use the
 * convenient functions in {@link at.abraxas.amarino.Amarino} to communicate
 * with Amarino. The way your application communicates with Amarino happens
 * exclusively via Intents. For more information about Intents in general please
 * refer to the <a
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
 *         </p>
 *         $Id: AmarinoIntent.java 444 2010-06-10 13:11:59Z abraxas $
 */
public interface AmarinoIntent {

	/**
	 * Activity Action: Tell <i>Amarino</i> to connect to a device
	 * <p>
	 * Input:
	 * <em>{@link at.abraxas.amarino.AmarinoIntent#EXTRA_DEVICE_ADDRESS}</em> -
	 * The address of the device <i>Amarino</i> should connect to.
	 * </p>
	 * <p>
	 * Output: one of the following actions will be broadcasted
	 * </p>
	 * <ul>
	 * <li><em>{@link #ACTION_CONNECTED}</em></li>
	 * <li><em>{@link #ACTION_DISCONNECTED}</em></li>
	 * <li><em>{@link #ACTION_CONNECTION_FAILED}</em></li>
	 * <li><em>{@link #ACTION_PAIRING_REQUESTED}</em></li>
	 * </ul>
	 * 
	 * <p>
	 * Example
	 * </p>
	 * 
	 * <pre>
	 * Intent intent = new Intent(AmarinoIntent.ACTION_CONNECT);
	 * intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, DEVICE_ADDRESS);
	 * sendBroadcast(intent);
	 * </pre>
	 */
	public static final String ACTION_CONNECT = "amarino.intent.action.CONNECT";

	/**
	 * Activity Action: Tell <i>Amarino</i> to disconnect from a device
	 * <p>
	 * Input: <em>{@link #EXTRA_DEVICE_ADDRESS}</em> - The address of the device
	 * <i>Amarino</i> should disconnect from.
	 * </p>
	 * <p>
	 * Output: the following action will be broadcasted
	 * </p>
	 * <ul>
	 * <li><em>{@link #ACTION_DISCONNECTED}</em></li>
	 * </ul>
	 * 
	 * <p>
	 * Example
	 * </p>
	 * 
	 * <pre>
	 * Intent intent = new Intent(AmarinoIntent.ACTION_DISCONNECT);
	 * intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, DEVICE_ADDRESS);
	 * sendBroadcast(intent);
	 * </pre>
	 */
	public static final String ACTION_DISCONNECT = "amarino.intent.action.DISCONNECT";

	/**
	 * Activity Action: Tell <i>Amarino</i> to send data to Arduino
	 * <p>
	 * Input: following EXTRAS must be within your intent
	 * </p>
	 * <ul>
	 * <li><em>{@link #EXTRA_DEVICE_ADDRESS}</em> - The address of the device
	 * <i>Amarino</i> should send data to</li>
	 * <li><em>{@link #EXTRA_DATA_TYPE}</em> - The type of data shipped with
	 * this intent</li>
	 * <li><em>{@link #EXTRA_DATA}</em> - The actual data you want to send. Be
	 * sure the format of that data matches the data type you specified in
	 * EXTRA_DATA_TYPE</li>
	 * <li><em>{@link #EXTRA_FLAG}</em> - The flag to which the data correspond
	 * to. If EXTRA_FLAG is not set, 'a' will be used by default.</li>
	 * </ul>
	 * 
	 * <p>
	 * Apart from <em>{@link #EXTRA_FLAG}</em> all EXTRAS are mandatory,
	 * otherwise <i>Amarino</i> will not forward your data to Arduino.
	 * </p>
	 * 
	 * <p>
	 * Output: Amarino forwards the data to the given address if the device is
	 * connected
	 * </p>
	 */
	public static final String ACTION_SEND = "amarino.intent.action.SEND";

	/**
	 * Broadcast Action sent by <i>Amarino</i>: <i>Amarino</i> broadcasts any
	 * received data from Arduino so that your application can receive them.
	 * 
	 * <p>
	 * The Intent will have the following EXTRAS:
	 * </p>
	 * <ul>
	 * <li><em>{@link #EXTRA_DEVICE_ADDRESS}</em></li>
	 * <li><em>{@link #EXTRA_DATA_TYPE}</em></li>
	 * <li><em>{@link #EXTRA_DATA}</em></li>
	 * </ul>
	 * 
	 * <p>
	 * <b>Example</b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 * public class YourReceiver extends BroadcastReceiver {
	 * 
	 * 	&#064;Override
	 * 	public void onReceive(Context context, Intent intent) {
	 * 		String data = null;
	 * 		final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
	 * 		final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
	 * 
	 * 		if (dataType == AmarinoIntent.STRING_EXTRA) {
	 * 			data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
	 * 
	 * 			if (data != null) {
	 * 				// do whatever you want to do with the data
	 * 				mValueTV.setText(data);
	 * 			}
	 * 		}
	 * 	}
	 * }
	 * 
	 *</pre>
	 */
	public static final String ACTION_RECEIVED = "amarino.intent.action.RECEIVED";

	/**
	 * Broadcast Action sent by <i>Amarino</i>: A connection to a device has
	 * been established
	 * 
	 * <p>
	 * The Intent will have the following extra value:
	 * <ul>
	 * <li><em>{@link #EXTRA_DEVICE_ADDRESS}</em> - the address of the connected
	 * device.</li>
	 * </ul>
	 * </p>
	 */
	public static final String ACTION_CONNECTED = "amarino.intent.action.CONNECTED";

	/**
	 * Broadcast Action sent by <i>Amarino</i>: The connection to a device has
	 * been disconnected
	 * 
	 * <p>
	 * The Intent will have the following extra value:
	 * <ul>
	 * <li><em>{@link #EXTRA_DEVICE_ADDRESS}</em> - the address of the
	 * disconnected device.</li>
	 * </ul>
	 * </p>
	 */
	public static final String ACTION_DISCONNECTED = "amarino.intent.action.DISCONNECTED";

	/**
	 * Broadcast Action sent by <i>Amarino</i>: A connection attempt to a device
	 * was not successful
	 * 
	 * <p>
	 * The Intent will have the following extra value:
	 * <ul>
	 * <li><em>{@link #EXTRA_DEVICE_ADDRESS}</em> - the address of the device
	 * which could not be connected</li>
	 * </ul>
	 * <p>
	 * There are many reasons why this might happen.
	 * </p>
	 * <ul>
	 * <li>Check if your Bluetooth module on your Arduino is powered and
	 * properly connected</li>
	 * <li>Due to an unexpected state either Bluetooth device might be in an
	 * undefined state, reset your Bluetooth adapters.</li>
	 * <li>Check the log of your phone using adb to gain more information why
	 * the connection cannot be established.</li>
	 * </ul>
	 * </p>
	 */
	public static final String ACTION_CONNECTION_FAILED = "amarino.intent.action.CONNECTION_FAILED";

	/**
	 * Broadcast Action sent by <i>Amarino</i>: Indicates that a pairing request
	 * has been started.
	 * 
	 * <p>
	 * This action goes along with a pairing request notification in your status
	 * bar.
	 * </p>
	 */
	public static final String ACTION_PAIRING_REQUESTED = "amarino.intent.action.PAIRING_REQUESTED";

	/**
	 * Activity Action: Request the connected devices list from <i>Amarino</i>
	 * <p>
	 * Input: nothing
	 * </p>
	 * <p>
	 * Output: <i>Amarino</i> will broadcast the result as
	 * <em>{@link #ACTION_CONNECTED_DEVICES}</em>
	 * </p>
	 * 
	 * <p>
	 * When you request the connected devices list you should always have a
	 * BroadcastReceiver ready to receive the result from <i>Amarino</i>
	 * </p>
	 */
	public static final String ACTION_GET_CONNECTED_DEVICES = "amarino.intent.action.ACTION_GET_CONNECTED_DEVICES";

	/**
	 * Broadcast Action sent by <i>Amarino</i>: The list of currently connected
	 * devices
	 * 
	 * <p>
	 * The Intent will have the following extra value:
	 * </p>
	 * <ul>
	 * <li><em>{@link #EXTRA_CONNECTED_DEVICES}</em></li>
	 * </ul>
	 * 
	 * <p>
	 * This action is only broadcasted if you have requested the list of device
	 * before by sending {@link #ACTION_GET_CONNECTED_DEVICES}
	 * </p>
	 */
	public static final String ACTION_CONNECTED_DEVICES = "amarino.intent.action.ACTION_CONNECTED_DEVICES";

	/**
	 * used to enaable a specific plug-in, needs EXTRA_PLUGIN_ID to be set
	 */

	public static final String ACTION_ENABLE = "amarino.intent.action.ENABLE";

	/**
	 * used to disable a specific plug-in, needs EXTRA_PLUGIN_ID to be set
	 */
	public static final String ACTION_DISABLE = "amarino.intent.action.DISABLE";

	/**
	 * disables all plug-ins if there is no active connection
	 */
	static final String ACTION_DISABLE_ALL = "amarino.intent.action.DISABLE_ALL";

	/**
	 * calls the edit activity of a plug-in
	 */
	public static final String ACTION_EDIT_PLUGIN = "amarino.intent.action.EDIT_PLUGIN";

	/**
	 * Type: BTDevice
	 */
	static final String EXTRA_DEVICE = "amarino.intent.extra.DEVICE";

	/**
	 * Type: String
	 * 
	 * <pre>
	 * e.g. "00:06:54:4B:31:7E"
	 * </pre>
	 */
	public static final String EXTRA_DEVICE_ADDRESS = "amarino.intent.extra.DEVICE_ADDRESS";

	/**
	 * Type: String[] - an array containing the addresses of all connected
	 * devices
	 */
	public static final String EXTRA_CONNECTED_DEVICE_ADDRESSES = "amarino.intent.extra.CONNECTED_DEVICE_ADDRESSES";

	/**
	 * Type: int
	 * <p>
	 * either <em>{@link #CONNECTED}</em>, <em>{@link #DISCONNECTED}</em> or
	 * <em>{@link #CONNECTING}</em>
	 * </p>
	 * 
	 */
	public static final String EXTRA_DEVICE_STATE = "amarino.intent.extra.DEVICE_STATE";
	
	/**
	 * Describes the state in {@link #EXTRA_DEVICE_STATE}
	 */
	public static final int CONNECTED = 1001;
	
	/**
	 * Describes the state in {@link #EXTRA_DEVICE_STATE}
	 */
	public static final int DISCONNECTED = 1002;
	
	/**
	 * Describes the state in {@link #EXTRA_DEVICE_STATE}
	 */
	public static final int CONNECTING = 1003;

	/**
	 * Type: char
	 * <p>
	 * the flag is the identifier for your data used by Arduino to determine
	 * which function to call. Relates to
	 * 
	 * <pre>
	 * registerFunction(flag, functionPointer);
	 * </pre>
	 * 
	 * in your Arduino sketch.
	 * </p>
	 */
	public static final String EXTRA_FLAG = "amarino.intent.extra.FLAG";

	/**
	 * <p>the type of data attached to an intent</p>
	 * 
	 * <p>
	 * You have to pass in the type of extra you going to send;
	 * for example: BOOLEAN_EXTRA, BYTE_EXTRA, STRING_EXTRA, INTEGER_EXTRA, etc.
	 * </p>
	 */
	public static final String EXTRA_DATA_TYPE = "amarino.intent.extra.DATA_TYPE";
	/**
	 * boolean in Android is in Arduino 0=false, 1=true
	 */
	public static final int BOOLEAN_EXTRA = 1;
	public static final int BOOLEAN_ARRAY_EXTRA = 2;
	/**
	 * byte is byte. In Arduino a byte stores an 8-bit unsigned number, from 0
	 * to 255.
	 */
	public static final int BYTE_EXTRA = 3;
	public static final int BYTE_ARRAY_EXTRA = 4;
	/**
	 * char is char. In Arduino stored in 1 byte of memory
	 */
	public static final int CHAR_EXTRA = 5;
	public static final int CHAR_ARRAY_EXTRA = 6;
	/**
	 * double is too large for Arduinos, better not to use this datatype
	 */
	public static final int DOUBLE_EXTRA = 7;
	public static final int DOUBLE_ARRAY_EXTRA = 8;
	/**
	 * float in Android is float in Arduino (4 bytes)
	 */
	public static final int FLOAT_EXTRA = 9;
	public static final int FLOAT_ARRAY_EXTRA = 10;
	/**
	 * int in Android is long in Arduino (4 bytes)
	 */
	public static final int INT_EXTRA = 11;
	public static final int INT_ARRAY_EXTRA = 12;
	/**
	 * long in Android does not fit in Arduino data types, better not to use it
	 */
	public static final int LONG_EXTRA = 13;
	public static final int LONG_ARRAY_EXTRA = 14;
	/**
	 * short in Android is like int in Arduino (2 bytes) 2^15
	 */
	public static final int SHORT_EXTRA = 15;
	public static final int SHORT_ARRAY_EXTRA = 16;
	/**
	 * String in Android is char[] in Arduino
	 */
	public static final int STRING_EXTRA = 17;
	public static final int STRING_ARRAY_EXTRA = 18;

	/**
	 * Type: depends on EXTRA_DATA_TYPE
	 */
	public static final String EXTRA_DATA = "amarino.intent.extra.DATA";

	/**
	 * Type: Integer - the id of your plug-in given to your EditActivity when it
	 * is called
	 */
	public static final String EXTRA_PLUGIN_ID = "amarino.intent.extra.PLUGIN_ID";

	/**
	 * Type: String - the name of your plug-in which is displayed to the user
	 */
	public static final String EXTRA_PLUGIN_NAME = "amarino.intent.extra.PLUGIN_NAME";

	/**
	 * Type: String - the description what your plug-in does
	 */
	public static final String EXTRA_PLUGIN_DESC = "amarino.intent.extra.PLUGIN_DESC";

	/**
	 * Type: String - the name of your plug-in service class (fully qualified)
	 * e.g. at.abraxas.amarino.plugins.compass.BackgroundService
	 */
	public static final String EXTRA_PLUGIN_SERVICE_CLASS_NAME = "amarino.intent.extra.PLUGIN_CLASS_NAME";

	/**
	 * Type: Integer - the type of visualizer you want to use to show data sent
	 * by a plug-in
	 * <p>
	 * must be one of
	 * </p>
	 * <ul>
	 * <li><em>{@link #VISUALIZER_TEXT}</em></li>
	 * <li><em>{@link #VISUALIZER_BARS}</em></li>
	 * <li><em>{@link #VISUALIZER_GRAPH}</em></li>
	 * </ul>
	 */
	public static final String EXTRA_PLUGIN_VISUALIZER = "amarino.intent.extra.PLUGIN_VISUALIZER";

	/**
	 * Displays the data coming from a plug-in as text
	 */
	public static final int VISUALIZER_TEXT = 100;

	/**
	 * Displays the data coming from a plug-in using bars.
	 * 
	 * <i>Upper and lower bounds need to be specified when this visualizer is used.</i>
	 */
	public static final int VISUALIZER_BARS = 101;

	/**
	 * Displays the data coming from a plug-in within a graph.
	 * 
	 * <i>Upper and lower bounds need to be specified when this visualizer is used.</i>
	 */
	public static final int VISUALIZER_GRAPH = 102;

	/**
	 * Type: Float - the minimum value to be expected
	 * <p>
	 * when using a graphical visualizer (em>{@link #VISUALIZER_BARS}</em> or
	 * em>{@link #VISUALIZER_GRAPH}</em>) this value must be set to initialize
	 * the visualizer
	 * </p>
	 */
	public static final String EXTRA_VISUALIZER_MIN_VALUE = "amarino.intent.extra.VISUALIZER_MIN_VALUE";

	/**
	 * Type: Float - the maximum value to be expected
	 * <p>
	 * when using a graphical visualizer this value must be set to initialize
	 * the visualizer
	 * </p>
	 */
	public static final String EXTRA_VISUALIZER_MAX_VALUE = "amarino.intent.extra.VISUALIZER_MAX_VALUE";

}
