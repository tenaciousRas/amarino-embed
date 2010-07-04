/*
  How to convert data received from Android.
  This example has no counterpart on the Android phone.
  It should give you an impression how to use the converting
  functions to convert received data.
*/
 
#include <MeetAndroid.h>

MeetAndroid meetAndroid;


void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(57600);
  
  // register callback functions, which will be called when an associated event occurs.
  // - the first parameter is the name of your function (see below)
  // - match the second parameter ('A', 'B', 'a', etc...) with the flag on your Android application
  //   small letters are custom events, capital letters inbuilt Amarino events
  meetAndroid.registerFunction(intValue, 'c');
  meetAndroid.registerFunction(floatValue, 'd');
  meetAndroid.registerFunction(doubleValue, 'e');
  meetAndroid.registerFunction(longValue, 'f');
  meetAndroid.registerFunction(floatValues, 'g'); // float array
  meetAndroid.registerFunction(intValues, 'i'); // int array
  meetAndroid.registerFunction(stringValue, 'h'); // a string
}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}

/**
 * Be careful about int values.
 * In Arduino int is a 16bit value!
 * In Android int is a 32bit value!
 *
 * If you expect to receive int values which are
 * not in this range (-32,768 to 32,767)
 * you should use meetAndroid.getLong()
 *
 */
void intValue(byte flag, byte numOfValues)
{
  int v = meetAndroid.getInt();
  meetAndroid.send(v);
}

/**
 * Same issue like with getInt(). Long values of
 * Android do not fit into long variables of 
 * Arduino.
 */
void longValue(byte flag, byte numOfValues)
{
  long v = meetAndroid.getLong();
  meetAndroid.send(v);
}

/*
 * Precision of float is currently 1/100th
 */
void floatValue(byte flag, byte numOfValues)
{
  float v = meetAndroid.getFloat();
  meetAndroid.send(v);
}

/*
 * Precision of double is currently 1/100th (same as float)
 */
void doubleValue(byte flag, byte numOfValues)
{
  double v = meetAndroid.getDouble();
  meetAndroid.send(v);
}

/*
 * In this function we extract more than one value of an event
 * Use this technique when you know that more than one value is
 * attached to the event (basically an array of floats)
 */
void floatValues(byte flag, byte numOfValues)
{
  // create an array where all event values should be stored
  // the number of values attached to this event is given by
  // a parameter(numOfValues)
  float data[numOfValues];
  
  // call the library function to fill the array with values
  meetAndroid.getFloatValues(data);
  
  // access the values
  for (int i=0; i<numOfValues;i++)
  {
    meetAndroid.send(data[i]);
  }
}

/*
 * Same procedure as seen for float values but this time for integers
 */
void intValues(byte flag, byte numOfValues)
{
  int data[numOfValues];
  meetAndroid.getIntValues(data);
  
  for (int i=0; i<numOfValues;i++)
  {
    meetAndroid.send(data[i]);
  }
}

/*
 * This function will retrieve a String sent by your Android phone
 */
void stringValue(byte flag, byte numOfValues)
{
  // first we need to know how long the string was in order to prepare an array big enough to hold it.
  // you should know that: (length == 'length of string sent from Android' + 1)
  // due to the '\0' null char added in Arduino
  int length = meetAndroid.stringLength();
  
  // define an array with the appropriate size which will store the string
  char data[length];
  
  // tell MeetAndroid to put the string into your prepared array
  meetAndroid.getString(data);
  
  // go and do something with the string, here we simply send it back to Android
  meetAndroid.send(data);
  
  for (int i=0; i<length-1; i++)
  {
	meetAndroid.send(data[i]);
  }
}


