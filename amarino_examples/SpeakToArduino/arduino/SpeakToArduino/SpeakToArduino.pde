/*
  SpeakToArduino (works with Amarino and the SpeakToArduino Android app)
  
  - based on the Amarino SpeakToArduino
  - receives custom events from Amarino turning lights on and off
    using speech recognision
  - reuses Multicolor lamp custom events to turn individual lights on/off
  
  author: Bonifaz Kaufmann - December 2009
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
  meetAndroid.registerFunction(redCB, 'a');
  meetAndroid.registerFunction(greenCB, 'b');  
  meetAndroid.registerFunction(blueCB, 'c');
  meetAndroid.registerFunction(onCB, 'r');  
  meetAndroid.registerFunction(offCB, 's');

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

void redCB(byte flag, byte numOfValues)
{
  if (red)
    digitalWrite(redLed, LOW);
  else
    digitalWrite(redLed, HIGH);
  red = !red;
}

void greenCB(byte flag, byte numOfValues)
{
  if (green)
    digitalWrite(greenLed, LOW);
  else
    digitalWrite(greenLed, HIGH);
  green = !green;
}

void blueCB(byte flag, byte numOfValues)
{
  if (blue)
    digitalWrite(blueLed, LOW);
  else
    digitalWrite(blueLed, HIGH);
  blue = !blue;
}

void onCB(byte flag, byte numOfValues)
{
  digitalWrite(redLed, HIGH);
  digitalWrite(greenLed, HIGH);
  digitalWrite(blueLed, HIGH);
  
  red = true;
  green = true;
  blue = true;
}

void offCB(byte flag, byte numOfValues)
{
  digitalWrite(redLed, LOW);
  digitalWrite(greenLed, LOW);
  digitalWrite(blueLed, LOW);
  
  red = false;
  green = false;
  blue = false;
}

