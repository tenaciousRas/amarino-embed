/**
 * 
 */
package at.abraxas.amarino.intent;

/**
 * @author Bonifaz Kaufmann
 * @author Free Beachler
 */
public interface AmarinoServiceIntentConfig {

	/**
	 * Activity Action: Tell <i>Amarino</i> to connect to a device
	 * <p>
	 * Input:
	 * <em>{@link at.abraxas.amarino.intent.DefaultAmarinoServiceIntentConfig#EXTRA_DEVICE_ADDRESS}</em>
	 * - The address of the device <i>Amarino</i> should connect to.
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
	 * Intent intent = new Intent(DefaultAmarinoServiceIntentConfig.ACTION_CONNECT);
	 * intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS, DEVICE_ADDRESS);
	 * sendBroadcast(intent);
	 * </pre>
	 */
	public abstract String getIntentNameActionConnect();

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
	 * Intent intent = new Intent(DefaultAmarinoServiceIntentConfig.ACTION_DISCONNECT);
	 * intent.putExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS, DEVICE_ADDRESS);
	 * sendBroadcast(intent);
	 * </pre>
	 */
	public abstract String getIntentNameActionDisconnect();

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
	public abstract String getIntentNameActionSend();

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
	 * 		final String address = intent
	 * 				.getStringExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DEVICE_ADDRESS);
	 * 		final int dataType = intent.getIntExtra(
	 * 				DefaultAmarinoServiceIntentConfig.EXTRA_DATA_TYPE, -1);
	 * 
	 * 		if (dataType == DefaultAmarinoServiceIntentConfig.STRING_EXTRA) {
	 * 			data = intent.getStringExtra(DefaultAmarinoServiceIntentConfig.EXTRA_DATA);
	 * 
	 * 			if (data != null) {
	 * 				// do whatever you want to do with the data
	 * 				mValueTV.setText(data);
	 * 			}
	 * 		}
	 * 	}
	 * }
	 * 
	 * </pre>
	 */
	public abstract String getIntentNameActionReceived();

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
	public abstract String getIntentNameActionConnected();

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
	public abstract String getIntentNameActionDisconnected();

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
	public abstract String getIntentNameActionConnectionFailed();

	/**
	 * Broadcast Action sent by <i>Amarino</i>: Indicates that a pairing request
	 * has been started.
	 * 
	 * <p>
	 * This action goes along with a pairing request notification in your status
	 * bar.
	 * </p>
	 */
	public abstract String getIntentNameActionPairingRequested();

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
	public abstract String getIntentNameActionGetConnectedDevices();

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
	public abstract String getIntentNameActionConnectedDevices();

	/**
	 * used to enaable a specific plug-in, needs EXTRA_PLUGIN_ID to be set
	 */
	public abstract String getIntentNameActionEnable();

	/**
	 * used to disable a specific plug-in, needs EXTRA_PLUGIN_ID to be set
	 */
	public abstract String getIntentNameActionDisable();

	/**
	 * disables all plug-ins if there is no active connection
	 */
	public abstract String getIntentNameActionDisableAll();

	/**
	 * Calls the edit activity of a plug-in.
	 */
	public abstract String getIntentNameActionEditPlugin();

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

	// FIXME - move to Amarino_2_App
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
