/*
  This example is more a template rather an example.
  It registers callback functions to
  receive phone state events and time tick events.
  
  When a time tick event happens the LED 13
  will blink the amount of minutes of the actual time
  
  Add first event 'Phone State' then 'Time Tick' in Amarino to use this sketch
*/
 
#include <MeetAndroid.h>

#define IDLE 0
#define RINGING 1
#define OFFHOOK 2

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
  meetAndroid.registerFunction(phoneState, 'A');  
  meetAndroid.registerFunction(timeTick, 'B'); 

  pinMode(onboardLed, OUTPUT);
  digitalWrite(onboardLed, HIGH);

}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}


/*  
 *  Will be called as soon as you receive a phone state event.
 *
 *  Use a switch statement to determine which kind of phone state event you got
 *  note: flag is in this case 'A' and numOfValues is 1 
 */
void phoneState(byte flag, byte numOfValues)
{
  // phone state
  int state = meetAndroid.getInt();
  
  switch (state)
  {
	case IDLE: idle(); break;
	case RINGING: ringing(); break;
	case OFFHOOK: offhook(); break;
  }
}


/*
 * Will be called as soon as you receive a time tick event.
 *
 * note: flag is in this case 'B' and numOfValues is 1 
 */
void timeTick(byte flag, byte numOfValues)
{
  int minutes = meetAndroid.getInt();
  
  if (minutes == 0) 
  {
    digitalWrite(onboardLed, LOW);
    delay(1000);
    digitalWrite(onboardLed, HIGH);
  }
  else 
  {
    for (int i=0; i<minutes; i++)
    {
      digitalWrite(onboardLed, LOW);
      delay(75);
      digitalWrite(onboardLed, HIGH);
      delay(75);
    }
  }
}


void idle()
{
  // phone has changed its state to idle
  // either from state ringing (missed call)
  // or from state offhook (accepted call)
  meetAndroid.send("idle");
}

void ringing()
{
  // phone is ringing
  meetAndroid.send("ringing");
}

void offhook()
{
  // call has been accepted
  meetAndroid.send("call accepted");
}

