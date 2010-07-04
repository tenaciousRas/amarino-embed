/*
  MeetAndroid.h - Arduino Library for Amarino
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

  Acknowledgements:
  This library is based on the SerialHandler library from Madrang of The
  Warrent Team. The original library has been modified to address the 
  specific demand to communicate with the Amarino toolkit.
  
  Following changes were made:
	  - ByteBufferLenght has been increased from 16 to 64
	  - FunctionBufferLength has been decreased from 128 to 75
	  - A startFlag and delimiter char has been added.
	  - ack and abord chars has been made private
	  - convenient convertion functions added
	  - constructor simplified
	  - default error handling introduced
	  - some more optimizations
	  - send functions added
	  - names of most functions changed
	  
  last modified by Bonifaz Kaufmann 04 Jul 2010
*/

#ifndef MeetAndroid_h
#define MeetAndroid_h

#include <inttypes.h>
#include "Print.h"


/******************************************************************************
* Definitions
******************************************************************************/

class MeetAndroid : public Print

{
#define ByteBufferLenght 64
#define FunctionBufferLenght 75 // 48-122 (in ascii: 0 - z)
#define FunctionBufferOffset 48  // offset to calc the position in the function buffer ('0' should be stored in intFunc[0])
#define _MEET_ANDROID_VERSION 2 // software version of this library
private:
	// per object data
	uint8_t bufferCount;
	uint8_t buffer[ByteBufferLenght];
	
	int numberOfValues;
	
	char abord;
	char ack;
	char delimiter;
	char startFlag; // used to communicate with Android (leads each msg to Android)
	
	bool customErrorFunc;

	typedef void (*H_voidFuncPtr)(uint8_t, uint8_t);
	H_voidFuncPtr intFunc[FunctionBufferLenght];
	H_voidFuncPtr errorFunc;

	// static data

	// private methods
	void processCommand(void);
	void init(void);
	int getArrayLength();

public: 
	// public methods
	MeetAndroid(H_voidFuncPtr err);
	MeetAndroid(void);
	
	void flush(void);
	bool receive(void);
	void registerFunction(void(*)(uint8_t, uint8_t),uint8_t);
	void unregisterFunction(uint8_t);
	int bufferLength(){return bufferCount;} // buffer withouth ACK
	int stringLength(){return bufferCount;} // string without flag but '/0' at the end
	void getBuffer(uint8_t[]);
	
	void getString(char[]);
	int getInt();
	long getLong();
	float getFloat();
	double getDouble();
	void getIntValues(int[]);
	void getFloatValues(float[]);
	void getDoubleValues(float[]); // in Arduino double and float are the same
	
	void write(uint8_t);

	void send(char);
    void send(const char[]);
    void send(uint8_t);
    void send(int);
    void send(unsigned int);
    void send(long);
    void send(unsigned long);
    void send(long, int);
    void send(double);
    void sendln(void);


	uint16_t waitTime;
	
	static int library_version() { 
		return _MEET_ANDROID_VERSION;} 
};

// Arduino 0012 workaround
#undef int
#undef char
#undef long
#undef byte
#undef float
#undef abs
#undef round 

#endif
