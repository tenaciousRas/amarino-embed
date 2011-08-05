/*
  This example is more a template rather an example.
  It registers 4 different callback functions to
  receive phone state events.
*/
 
#include <MeetAndroid.h>

#include "WProgram.h"
void setup();
void loop();
void timeTick(byte flag, byte numOfValues);
void ringing(byte flag, byte numOfValues);
void idle(byte flag, byte numOfValues);
void offhook(byte flag, byte numOfValues);
MeetAndroid meetAndroid;
int onboardLed = 13;

void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(57600); 
  
  // register callback functions, which will be called when an associated event occurs.
  // - the first parameter is the name of your function (see below)
  // - match the second parameter ('A', 'B', 'a', etc...) with the flag on your Android application
  meetAndroid.registerFunction(timeTick, 'B');  
  meetAndroid.registerFunction(ringing, 'C');
  meetAndroid.registerFunction(idle, 'D');
  meetAndroid.registerFunction(offhook, 'E');

  pinMode(onboardLed, OUTPUT);
  digitalWrite(onboardLed, HIGH);

}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}

/*
 * Will be called as soon as you receive a time tick event.
 *
 * note: flag is in this case 'B' and numOfValues is 0 
 */
void timeTick(byte flag, byte numOfValues)
{
  digitalWrite(onboardLed, LOW);
  delay(500);
  digitalWrite(onboardLed, HIGH);
}

/*  
 *  Will be called as soon as you receive a ringing event.
 *
 *  If there is data attached to the function call, 
 *  buffer[1] to buffer[meetAndroid.bufferLength()-1] contains the data
 *  note: flag is in this case 'C' and numOfValues is 0 
 */
void ringing(byte flag, byte numOfValues)
{
  int arraySize = meetAndroid.bufferLength();
  byte incomingNumber[arraySize];
  meetAndroid.getBuffer(incomingNumber);
  
  // start with 1, since incomingNumber[0] == 'C'
  for (int i=1; i<arraySize; i++)
  {
    incomingNumber[i];
  }
  
  
  // you could extract the called number
  // and do whatever you want to do with the phone number

}

void idle(byte flag, byte numOfValues)
{

}

void offhook(byte flag, byte numOfValues)
{
  
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

