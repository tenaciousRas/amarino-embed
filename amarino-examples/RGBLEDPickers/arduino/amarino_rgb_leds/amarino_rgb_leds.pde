// Amarino RGB LEDs w/Android Color Controller by Free Beachler
// 
// Demonstrates control of 1+ RGB LEDs (light emitting diodes)
// using an Arduino PWM pin and a decade counter.
// The RGB LEDs can be set to any color, and the 
// counter permits control of up to 10 LEDs.
// 
// Arudino RGB LEDs w/PWM Decade Counter by Free Beachler 
// is licensed under a Creative Commons Attribution-ShareAlike
// 3.0 Unported License.
// Copies of the license are available at 
// http://creativecommons.org/licenses/by-sa/3.0/
// 
// This software is provided as-is.  No warranty or guarantee
// is provided.  You assume all responsibility from its use.
// #define DEBUG_MODE
#include <MeetAndroid.h>
MeetAndroid meetAndroid;

#define RGB_LED_PIN_R 9  // needs to be a PWM pin
#define RGB_LED_PIN_G 10  // needs to be a PWM pin
#define RGB_LED_PIN_B 11  // needs to be a PWM pin
#define COUNTER_CK 2
#define COUNTER_RESET 4
#define NUMBER_OF_LEDS  3  // number of LEDs attached to decade ic

int ledRGBPins[3] = {RGB_LED_PIN_R, RGB_LED_PIN_G, RGB_LED_PIN_B};
byte ledState[NUMBER_OF_LEDS] = {1, 1, 1};
unsigned long ledColor[NUMBER_OF_LEDS] = {0xFF0000, 0x00CC00, 0x00CC};
byte ledStateIndex;

void setup()  {
  Serial.begin(9600);
  // set the data rate for Amarino
  Serial.begin(19200);
  // send init data
  meetAndroid.send("Arduino connected via bluetooth.");
  meetAndroid.registerFunction(setLEDColor, 'c');
  // setup circuit
  pinMode(RGB_LED_PIN_R, OUTPUT);
  pinMode(RGB_LED_PIN_G, OUTPUT);
  pinMode(RGB_LED_PIN_B, OUTPUT);
  // reset counter
  pinMode(COUNTER_CK, OUTPUT);
  digitalWrite(COUNTER_CK,LOW);
  delayMicroseconds(20);
  pinMode(COUNTER_RESET, OUTPUT);
  digitalWrite(COUNTER_RESET,LOW);
  delayMicroseconds(20);
  reset(COUNTER_RESET);
  ledStateIndex = 0;
}

/*
* We break RGB colors into 3 duty cycles
* white = 0xFFFFFF - meaning each color
* gets 100% brightness during its 1/3 of
* the duty cycle.
*/
void loop() {
  cycleLEDColor();
  meetAndroid.receive(); // receive android events
}

void cycleLEDColor() {
  analogWrite(ledRGBPins[0], 0);
  analogWrite(ledRGBPins[1], 0);
  analogWrite(ledRGBPins[2], 0);
  delayMicroseconds(20);
  if (NUMBER_OF_LEDS == ledStateIndex + 1) {
    ledStateIndex = 0;
    reset(COUNTER_RESET);
  } else {
    ledStateIndex++;
    clock(COUNTER_CK);
  }
  delayMicroseconds(20);
  int currState = ledState[ledStateIndex] % 2;
  boolean isLEDOff = (currState == 0 ? true : false);
  byte brightnessR = 0;
  byte brightnessG = 0;
  byte brightnessB = 0;
  unsigned long color = ledColor[ledStateIndex];
  if (!isLEDOff) {
      brightnessR = ((long) (color & 0xFF0000)) >> 16;
      brightnessG = ((long) (color & 0xFF00)) >> 8;
      brightnessB = ((long) (color & 0xFF));
  }
  analogWrite(ledRGBPins[0], brightnessR);
  analogWrite(ledRGBPins[1], brightnessG);
  analogWrite(ledRGBPins[2], brightnessB);
  delayMicroseconds(20000);
}

void setLEDColor(byte flag, byte numOfValues) {
  char data[40];
  meetAndroid.getString(data);
  // expect to always have two Ints from Android app
  uint8_t b1[2];
  uint8_t b2[38];
  b1[0] = data[0];
  b1[1] = '\0';
  int a = 2;
  for(;a < 40; a++){
    b2[a - 2] = data[a];
    if (b2[a - 2] == 0) {
      break;
    }
  }
  int ledIndex = atoi((char *) b1);
  unsigned long color = atol((char *) b2);
  color = (color << 8) >> 8;
#ifdef DEBUG_MODE
Serial.println(data);
Serial.println(ledIndex);
Serial.println(color);
Serial.println((char *) b1);
Serial.println((char *) b2);
#endif
  ledColor[ledIndex] = color;
}

/*
 * Sends a clock pulse to the counter making it advance.
 */
void clock(int pin) {
  digitalWrite(pin,HIGH);
  delayMicroseconds(20);
  digitalWrite(pin,LOW);
}
 
/*
 * Resets the counter making it start counting from zero.
 */
void reset(int pin) {
  digitalWrite(pin,HIGH);
  delayMicroseconds(20);
  digitalWrite(pin,LOW);
}

long valAsHexColorStep(int val) {
  long ret = 0xFF0000;
  if (val < 338) {
    ret = 0x0000FF;
  } else if (val < 676) {
    ret = 0x00FF00;
  }
  return ret;
}

long valAsHexColorSmooth(int val) {
  long ret = (long) val * 0x4000;
  if (ret > 0xFFFFFF) {
    ret = 0xFFFFFF;
  }
  return ret;
}
