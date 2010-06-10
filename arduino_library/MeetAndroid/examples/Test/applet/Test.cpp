/*
  Receives Test Events from your phone.
  After it gets a test message the led 13 will blink
  for one second.
*/
 
#include <MeetAndroid.h>

#include "WProgram.h"
void setup();
void loop();
void testEvent(byte flag, byte numOfValues);
void flushLed(int time);
MeetAndroid meetAndroid;
int onboardLed = 13;
char start = 18; // arduino msg
char end = 19; // ack

void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(57600); 
  
  // register callback functions, which will be called when an associated event occurs.
  // - the first parameter is the name of your function (see below)
  // - match the second parameter ('A', 'B', 'a', etc...) with the flag on your Android application
  meetAndroid.registerFunction(testEvent, 'A');  

  pinMode(onboardLed, OUTPUT);
  digitalWrite(onboardLed, HIGH);
  
  pinMode(5, INPUT);
  digitalWrite(5, HIGH);
}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
  meetAndroid.print(start);
  meetAndroid.print(analogRead(5));
  meetAndroid.print(end);
  delay(500);
}

/*
 * This method is called constantly.
 * note: flag is in this case 'A' and numOfValues is 0 (since test event doesn't send any data)
 */
void testEvent(byte flag, byte numOfValues)
{
  flushLed(300);
  flushLed(300);
}

void flushLed(int time)
{
  digitalWrite(onboardLed, LOW);
  delay(time);
  digitalWrite(onboardLed, HIGH);
  delay(time);
}


int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

