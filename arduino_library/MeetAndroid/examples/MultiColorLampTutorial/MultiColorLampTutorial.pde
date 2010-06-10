/*
  Multicolor Lamp (works with Amarino and the MultiColorLamp Android app)
  
  - based on the Amarino Multicolor Lamp tutorial
  - receives custom events from Amarino changing color accordingly
  
  author: Bonifaz Kaufmann - December 2009
*/
 
#include <MeetAndroid.h>

// declare MeetAndroid so that you can call functions with it
MeetAndroid meetAndroid;

// we need 3 PWM pins to control the leds
int redLed = 9;   
int greenLed = 10;
int blueLed = 11;

void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(57600); 
  
  // register callback functions, which will be called when an associated event occurs.
  meetAndroid.registerFunction(red, 'o');
  meetAndroid.registerFunction(green, 'p');  
  meetAndroid.registerFunction(blue, 'q'); 

  // set all color leds as output pins
  pinMode(redLed, OUTPUT);
  pinMode(greenLed, OUTPUT);
  pinMode(blueLed, OUTPUT);
  
  // just set all leds to high so that we see they are working well
  digitalWrite(redLed, HIGH);
  digitalWrite(greenLed, HIGH);
  digitalWrite(blueLed, HIGH);

}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}

/*
 * Whenever the multicolor lamp app changes the red value
 * this function will be called
 */
void red(byte flag, byte numOfValues)
{
  analogWrite(redLed, meetAndroid.getInt());
}

/*
 * Whenever the multicolor lamp app changes the green value
 * this function will be called
 */
void green(byte flag, byte numOfValues)
{
  analogWrite(greenLed, meetAndroid.getInt());
}

/*
 * Whenever the multicolor lamp app changes the blue value
 * this function will be called
 */
void blue(byte flag, byte numOfValues)
{
  analogWrite(blueLed, meetAndroid.getInt());
}

