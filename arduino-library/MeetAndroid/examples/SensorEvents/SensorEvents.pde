/*
  Receives compass sensor events from your phone.
  
*/
 
#include <MeetAndroid.h>

// MeetAndroid meetAndroid();
// you can define your own error function to catch messages
// where not fuction has been attached for
MeetAndroid meetAndroid(error);

void error(uint8_t flag, uint8_t values){
  Serial.print("ERROR: ");
  Serial.print(flag);
}

int onboardLed = 13;

void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(57600); 
  
  // register callback functions, which will be called when an associated event occurs.
  // - the first parameter is the name of your function (see below)
  // - match the second parameter ('A', 'B', 'a', etc...) with the flag on your Android application
  meetAndroid.registerFunction(compass, 'A');  

  pinMode(onboardLed, OUTPUT);
  digitalWrite(onboardLed, HIGH);

}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}

/*
 * This method is called constantly.
 * Compass events are sent several times a second.
 *
 * note: flag is in this case 'A' and numOfValues is 1 
 * since compass event sends exactly one single int value for heading
 */
void compass(byte flag, byte numOfValues)
{
  // we use getInt(), since we know only data between 0 and 360 will be sent
  int heading = meetAndroid.getInt(); 
  flushLed(heading); // silly, you should have better ideas
}

void flushLed(int time)
{
  digitalWrite(onboardLed, LOW);
  delay(time);
  digitalWrite(onboardLed, HIGH);
}

