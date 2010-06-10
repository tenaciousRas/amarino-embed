/*
  Amarino - A prototyping software toolkit for Android and Arduino
  Copyright (c) 2010 Bonifaz Kaufmann.  All right reserved.
  
  This application and its library is free software; you can redistribute
  it and/or modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package at.abraxas.amarino;

import it.gerdavax.easybluetooth.RemoteDevice;

import java.io.Serializable;
import java.util.HashMap;


/**
 * $Id: BTDevice.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class BTDevice implements Serializable {
	
	private static final long serialVersionUID = -6041931825295548358L;
	
	long id = -1;
	String address;
	String name;
	int state = AmarinoIntent.DISCONNECTED;
	// <pluginID, event>
	HashMap<Integer, Event> events;
	
	public BTDevice(String address){
		this.address = address;
	}
	
	public BTDevice(long id, String address, String name){
		this.id = id;
		this.address = address;
		this.name = name;
	}
	
	public BTDevice(RemoteDevice rd){
		this.address = rd.getAddress();
		this.name = rd.getFriendlyName();
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public String getName(){
		return this.name;
	}
	
	public boolean equals(Object o){
		if (this == o)
			return true;
		
		if (o == null || (o.getClass() != this.getClass()))
			return false;
		
		BTDevice other = (BTDevice)o;
		if (this.id == other.id && this.id != -1) {
			return true;
		}
		if (this.address.equals(other.address)){
			return true;
		}
		return false;
	}
	
	public BTDevice clone(){
		BTDevice device = new BTDevice(this.id, this.address, this.name);
		device.state = this.state;
		return device;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		for (int i=0;i<address.length();i++)
			hash += address.charAt(i);
		return hash;
	}

	@Override
	public String toString() {
		return address + " - " + name;
	}

}
