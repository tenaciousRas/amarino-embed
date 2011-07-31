/*
  SpeakToArduino (works with Amarino 2.0 and the SpeakToArduino Android app)
  
  - counterpart of the Amarino 2.0 SpeakToArduino app
  - receives events from Amarino turning lights on and off and changes
    its color using speech recognision
  
  author: Bonifaz Kaufmann - September 2010
*/
 
#include <MeetAndroid.h>

// declare MeetAndroid so that you can call functions with it
MeetAndroid meetAndroid;

// we need 3 PWM pins to control the leds
int redLed = 9;   
int greenLed = 10;
int blueLed = 11;

boolean red = false;
boolean green = false;
boolean blue = false;

void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(57600); 
  
  // register callback functions, which will be called when an associated event occurs.
  meetAndroid.registerFunction(color, 'c');

  // set all color leds as output pins
  pinMode(redLed, OUTPUT);
  pinMode(greenLed, OUTPUT);
  pinMode(blueLed, OUTPUT);
  
  // just set all leds to high so that we see they are working well
  digitalWrite(redLed, LOW);
  digitalWrite(greenLed, LOW);
  digitalWrite(blueLed, LOW);

}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}

void color(byte flag, byte numOfValues)
{
  int rgb[3];
  meetAndroid.getIntValues(rgb);
  
  analogWrite(redLed, rgb[0]);
  analogWrite(greenLed, rgb[1]);
  analogWrite(blueLed, rgb[2]);
}

