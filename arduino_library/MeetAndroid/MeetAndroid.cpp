/*
  MeetAndroid.cpp - Arduino Library for Amarino
  Copyright (c) 2009 Bonifaz Kaufmann.  All right reserved.

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

// Includes
#include "HardwareSerial.h"
#include "WConstants.h"
#include "MeetAndroid.h"
#include "stdlib.h"


// Private methods
void MeetAndroid::processCommand(){
	if(buffer[0]-FunctionBufferOffset < FunctionBufferLenght){
		void (*H_FuncPtr)(uint8_t, uint8_t) = intFunc[buffer[0]-FunctionBufferOffset];
		H_FuncPtr(buffer[0], getArrayLength());
	}
	else {
		if (customErrorFunc)
			errorFunc(buffer[0], getArrayLength());
		else {
			Serial.print("No func. attached for: ");
			Serial.print(buffer[0]);
		}
	}
}


void MeetAndroid::init()
{
	waitTime = 30;
	startFlag = 18;
	ack = 19;
	abord = 27;
	delimiter = 59; //';'

	numberOfValues = 0;
	
	for(int a = 0;a < FunctionBufferLenght;a++){
		intFunc[a] = errorFunc;
	}
}


// public methods
MeetAndroid::MeetAndroid()
{
    // it is hard to use member function pointer together with normal function pointers.
    customErrorFunc = false;
	init();
}

// Constructur for use with HardwareSerial library
MeetAndroid::MeetAndroid(H_voidFuncPtr err)
{
    customErrorFunc = true;
	errorFunc = err;
	init();
}


void MeetAndroid::registerFunction(void(*userfunction)(uint8_t, uint8_t),uint8_t command){
	intFunc[command-FunctionBufferOffset] = userfunction;
}
void MeetAndroid::unregisterFunction(uint8_t command){
	intFunc[command-FunctionBufferOffset] = errorFunc;
}

bool MeetAndroid::receive(){
	uint8_t lastByte;
	boolean timeout = false;
	while(!timeout)
	{
		while(Serial.available() > 0)
		{
			lastByte = Serial.read();
			
			if(lastByte == abord){
				flush();
			}
			else if(lastByte == ack){
				processCommand();
				flush();
			}
			else if(bufferCount < ByteBufferLenght){
				buffer[bufferCount] = lastByte;
				bufferCount++;
			}
			else return false;
		}
		
		if(Serial.available() <= 0 && !timeout){
			if(waitTime > 0) delayMicroseconds(waitTime);
			if(Serial.available() <= 0) timeout = true;
		}
	}
	return timeout;
}




void MeetAndroid::getBuffer(uint8_t buf[]){

	for(int a = 0;a < bufferCount;a++){
		buf[a] = buffer[a];
	}
}

void MeetAndroid::getString(char string[]){

	for(int a = 1;a < bufferCount;a++){
		string[a-1] = buffer[a];
	}
	string[bufferCount-1] = '\0';
}

int MeetAndroid::getInt()
{
	uint8_t b[bufferCount];
	for(int a = 1;a < bufferCount;a++){
		b[a-1] = buffer[a];
	}

	b[bufferCount-1] = '\0';
	return atoi((char*)b);
}

long MeetAndroid::getLong()
{
	uint8_t b[bufferCount];
	for(int a = 1;a < bufferCount;a++){
		b[a-1] = buffer[a];
	}

	b[bufferCount-1] = '\0';
	return atol((char*)b);
}

float MeetAndroid::getFloat()
{
	return (float)getDouble();
}

int MeetAndroid::getArrayLength()
{
	if (bufferCount == 1) return 0; // only a flag and ack was sent, not data attached
	numberOfValues = 1;
	// find the amount of values we got
	for (int a=1; a<bufferCount;a++){
		if (buffer[a]==delimiter) numberOfValues++;
	}
	return numberOfValues;
}

void MeetAndroid::getFloatValues(float values[])
{
	int t = 0; // counter for each char based array
	int pos = 0;

	int start = 1; // start of first value
	for (int end=1; end<bufferCount;end++){
		// find end of value
		if (buffer[end]==delimiter) {
			// now we know start and end of a value
			char b[(end-start)+1]; // create container for one value plus '\0'
			t = 0;
			for(int i = start;i < end;i++){
				b[t++] = (char)buffer[i];
			}
			b[t] = '\0';
			values[pos++] = atof(b);
			start = end+1;
		}
	}
	// get the last value
	char b[(bufferCount-start)+1]; // create container for one value plus '\0'
	t = 0;
	for(int i = start;i < bufferCount;i++){
		b[t++] = (char)buffer[i];
	}
	b[t] = '\0';
	values[pos] = atof(b);
}

// not tested yet
void MeetAndroid::getDoubleValues(float values[])
{
	getFloatValues(values);
}

// not tested yet
void MeetAndroid::getIntValues(int values[])
{
	int t = 0; // counter for each char based array
	int pos = 0;

	int start = 1; // start of first value
	for (int end=1; end<bufferCount;end++){
		// find end of value
		if (buffer[end]==delimiter) {
			// now we know start and end of a value
			char b[(end-start)+1]; // create container for one value plus '\0'
			t = 0;
			for(int i = start;i < end;i++){
				b[t++] = (char)buffer[i];
			}
			b[t] = '\0';
			values[pos++] = atoi(b);
			start = end+1;
		}
	}
	// get the last value
	char b[(bufferCount-start)+1]; // create container for one value plus '\0'
	t = 0;
	for(int i = start;i < bufferCount;i++){
		b[t++] = (char)buffer[i];
	}
	b[t] = '\0';
	values[pos] = atoi(b);
}


double MeetAndroid::getDouble()
{
	char b[bufferCount];
	for(int a = 1;a < bufferCount;a++){
		b[a-1] = (char)buffer[a];
	}

	b[bufferCount-1] = '\0';
	return atof(b);
	
}


void MeetAndroid::write(uint8_t b){
	Serial.print(b);
}

void MeetAndroid::send(char c ){
	Serial.print(startFlag);
	Serial.print(c);
	Serial.print(ack);
}

void MeetAndroid::send(const char str[]){
	Serial.print(startFlag);
	Serial.print(str);
	Serial.print(ack);
}
void MeetAndroid::send(uint8_t n){
	Serial.print(startFlag);
	Serial.print(n);
	Serial.print(ack);
}
void MeetAndroid::send(int n){
	Serial.print(startFlag);
	Serial.print(n);
	Serial.print(ack);
}
void MeetAndroid::send(unsigned int n){
	Serial.print(startFlag);
	Serial.print(n);
	Serial.print(ack);
}
void MeetAndroid::send(long n){
	Serial.print(startFlag);
	Serial.print(n);
	Serial.print(ack);
}
void MeetAndroid::send(unsigned long n){
	Serial.print(startFlag);
	Serial.print(n);
	Serial.print(ack);
}
void MeetAndroid::send(long n, int base){
	Serial.print(startFlag);
	Serial.print(n, base);
	Serial.print(ack);
}
void MeetAndroid::send(double n){
	Serial.print(startFlag);
	Serial.print(n);
	Serial.print(ack);
}
void MeetAndroid::sendln(void){
	Serial.print(startFlag);
	Serial.println();
	Serial.print(ack);
}

void MeetAndroid::flush(){
	for(uint8_t a=0; a < ByteBufferLenght; a++){
		buffer[a] = 0;
	}
	bufferCount = 0;
	numberOfValues = 0;
}
