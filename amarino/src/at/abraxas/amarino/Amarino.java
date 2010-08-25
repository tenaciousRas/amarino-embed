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

import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;

/**
 * This class is part of the Amarino Library and bundles some very useful methods to communicate
 * with Arduino.
 * <p>
 * @author Bonifaz Kaufmann
 * </p>
 * $Id: Amarino.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class Amarino {
	
	/**
	 * Only static methods, no need to get an instance of Amarino
	 */
	private Amarino(){};
	
	/**
	 * Establish a connection to the Bluetooth device with the given address.
	 * This method uses {@link at.abraxas.amarino.AmarinoIntent#ACTION_CONNECT} to initiate a connection.
	 * 
	 * You might want to know if your connection was successful or not.
	 * To get feedback from Amarino register a
	 * <a href="http://developer.android.com/reference/android/content/BroadcastReceiver.html">BroadcastReceiver</a>
	 * for the following intents.
	 * <ul>
     *   <li><em>{@link at.abraxas.amarino.AmarinoIntent#ACTION_CONNECTED}</em></li>
     *   <li><em>{@link at.abraxas.amarino.AmarinoIntent#ACTION_DISCONNECTED}</em></li>
     *   <li><em>{@link at.abraxas.amarino.AmarinoIntent#ACTION_CONNECTION_FAILED}</em></li>
     *   <li><em>{@link at.abraxas.amarino.AmarinoIntent#ACTION_PAIRING_REQUESTED}</em></li>
     * </ul>
	 * 
	 * Amarino will broadcast one of the four intents after your have called {@link #connect(Context, String)}
	 * 
	 * @param context the context
	 * @param address address of your Arduino Bluetooth module
	 * <pre>e.g. "00:06:54:4B:31:7E"</pre>
	 */
	public static void connect(Context context, String address){
		Intent intent = new Intent(AmarinoIntent.ACTION_CONNECT);
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Disconnect from a Bluetooth device
	 * 
	 * For feedback register a
	 * <a href="http://developer.android.com/reference/android/content/BroadcastReceiver.html">BroadcastReceiver</a>
	 * for the {@link at.abraxas.amarino.AmarinoIntent#ACTION_DISCONNECTED} intent.
	 * 
	 * @param context the context
	 * @param address address of your Arduino Bluetooth module, should be the same
	 * which you used to connect to the device
	 */
	public static void disconnect(Context context, String address){
		Intent intent = new Intent(AmarinoIntent.ACTION_DISCONNECT);
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a boolean value to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, boolean data) {
		Intent intent = getSendIntent(address, AmarinoIntent.BOOLEAN_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a byte value to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, byte data) {
		Intent intent = getSendIntent(address, AmarinoIntent.BYTE_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a char value to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, char data) {
		Intent intent = getSendIntent(address, AmarinoIntent.CHAR_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a short value to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, short data) {
		Intent intent = getSendIntent(address, AmarinoIntent.SHORT_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends an int value to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, int data) {
		Intent intent = getSendIntent(address, AmarinoIntent.INT_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a long value to Arduino
	 * 
	 * <p><i>If you do not exactly know what you do, you absolutely shouldn't really use this method,
	 * since Arduino cannot receive Android's 32-bit long values.</i></p>
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, long data) {
		Intent intent = getSendIntent(address, AmarinoIntent.LONG_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a float value to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, float data) {
		Intent intent = getSendIntent(address, AmarinoIntent.FLOAT_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a double value to Arduino
	 * 
	 * <p><i>If you do not exactly know what you do, you absolutely shouldn't really use this method,
	 * since Arduino cannot receive Android's 32-bit double values.</i></p>
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, double data) {
		Intent intent = getSendIntent(address, AmarinoIntent.DOUBLE_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a String to Arduino
	 * 
	 * <p><i>The buffer of an Arduino is small, your String should not be longer than 62 characters</i></p>
	 * @assertion: (data.length() <= 62)
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, String data) {
		Intent intent = getSendIntent(address, AmarinoIntent.STRING_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends an boolean array to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, boolean[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.BOOLEAN_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends an byte array to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, byte[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.BYTE_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a char array to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, char[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.CHAR_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a short array to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, short[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.SHORT_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends an int array to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, int[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.INT_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a long array to Arduino
	 * 
	 * <p><i>If you do not exactly know what you do, you absolutely shouldn't really use this method,
	 * since Arduino cannot receive Android's 32-bit long values.</i></p>
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, long[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.LONG_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a float array to Arduino
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, float[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.FLOAT_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a double array to Arduino
	 * 
	 * <p><i>If you do not exactly know what you do, you absolutely shouldn't really use this method,
	 * since Arduino cannot receive Android's 32-bit double values.</i></p>
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, double[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.DOUBLE_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Sends a String array to Arduino
	 * 
	 * <p><i>The buffer of an Arduino is small, your String should not be longer than 62 characters.</i></p>
	 * @assertion: for each (String s : data) { assert(s.length() <= 62); }
	 * 
	 * @param context the context
	 * @param address the Bluetooth device you want to send data to
	 * @param flag the flag Arduino has registered a function for to receive this data
	 * @param data your data you want to send
	 */
	public static void sendDataToArduino(Context context, String address, char flag, String[] data) {
		Intent intent = getSendIntent(address, AmarinoIntent.STRING_ARRAY_EXTRA, flag);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	
	/* methods normally used by plug-in developers */
	
	/**
	 * Used by plug-in developers to send a boolean value.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, boolean data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, boolean data){
		Intent intent = getPluginSendIntent(AmarinoIntent.BOOLEAN_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a byte value.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, byte data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, byte data){
		Intent intent = getPluginSendIntent(AmarinoIntent.BYTE_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a char value.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, char data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, char data){
		Intent intent = getPluginSendIntent(AmarinoIntent.CHAR_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a int value.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, int data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, int data){
		Intent intent = getPluginSendIntent(AmarinoIntent.INT_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a long value.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, long data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, long data){
		Intent intent = getPluginSendIntent(AmarinoIntent.LONG_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a float value.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, float data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, float data){
		Intent intent = getPluginSendIntent(AmarinoIntent.FLOAT_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a double value.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, double data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, double data){
		Intent intent = getPluginSendIntent(AmarinoIntent.DOUBLE_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a String value.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, String data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, String data){
		Intent intent = getPluginSendIntent(AmarinoIntent.STRING_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a boolean array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, boolean[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, boolean[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.BOOLEAN_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a byte array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, byte[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, byte[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.BYTE_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a char array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, char[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, char[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.CHAR_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a short array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, short[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, short[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.SHORT_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a int array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, int[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, int[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.INT_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a long array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, long[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, long[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.LONG_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a float array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, float[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, float[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.FLOAT_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a double array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, double[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, double[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.DOUBLE_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Used by plug-in developers to send a String array.
	 * 
	 * <p>This method can only be used within a plugin!
	 * If you want to send data from your own standalone application, use 
	 * {@link #sendDataToArduino(Context context, String address, char flag, String[] data)} instead.</p>
	 * 
	 * @param context the context
	 * @param pluginId you received this id when 
	 * @param data your data you want to send
	 */
	public static void sendDataFromPlugin(Context context, int pluginId, String[] data){
		Intent intent = getPluginSendIntent(AmarinoIntent.STRING_ARRAY_EXTRA, pluginId);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, data);
		context.sendBroadcast(intent);
	}
	
	
	/**
	 * Convenient method to check if a given Bluetooth address is in proper format.
	 * 
	 * <p>A correct Bluetooth address has 17 charaters and the following format: xx:xx:xx:xx:xx:xx</p>
	 * 
	 * @param address the address to prove
	 * @return true if the address is in proper format, otherwise false
	 */
	public static boolean isCorrectAddressFormat(String address){
		if (address.length() != 17) return false;
		// TODO use regular expression to check format needs more specific regex
		return Pattern.matches("[[A-F][0-9][:]]+", address.toUpperCase());
	}
	
	

	private static Intent getPluginSendIntent(int dataType, int pluginId){
		Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
		intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, dataType);
		intent.putExtra(AmarinoIntent.EXTRA_PLUGIN_ID, pluginId);
		return intent;
	}

	private static Intent getSendIntent(String address, int dataType, char flag){
		Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, address);
		intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, dataType);
		intent.putExtra(AmarinoIntent.EXTRA_FLAG, flag);
		return intent;
	}
}
