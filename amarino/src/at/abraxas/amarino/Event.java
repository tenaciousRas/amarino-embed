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

/**
 * $Id: Event.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class Event {
	
	public long id;
	public String name;
	public String desc;
	public char flag;
	public int visualizer;
	public String packageName;
	public String serviceClassName;
	public String editClassName;
	public int pluginId;
	public long deviceId;
	public String address;
	public float visualizerMinValue = 0f;
	public float visualizerMaxValue = 1024f;
	
	
	public Event(String name, String desc, int visualizer, char flag, 
			String packageName, String editClassName, String serviceClassName, int pluginId, long deviceId){
		this.flag = flag;
		this.name = name;
		this.desc = desc;
		this.visualizer = visualizer;
		this.packageName = packageName;
		this.serviceClassName = serviceClassName;
		this.editClassName = editClassName;
		this.pluginId = pluginId;
		this.deviceId = deviceId;
	}
	
	public Event(long id, String name, String desc, int visualizer, char flag, 
			String packageName, String editClassName, String serviceClassName, int pluginId, long deviceId){
		
		this(name, desc, visualizer, flag, packageName, editClassName, serviceClassName, pluginId, deviceId);
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o){
		if (this == o) 
			return true;
		
		if (o == null || (o.getClass() != this.getClass())) 
			return false;
		
		Event e = (Event) o;
		if (e.pluginId == this.pluginId && e.name.equals(this.name))
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode(){
		return pluginId;
	}
	
	public String toString(){
		return "Name: " + name + 
			   "\nPackage: " + packageName + 
			   "\nFlag: " + flag + 
			   "\nId: " + pluginId + 
			   "\ndeviceId: " + deviceId + 
			   "\nVisualizer: " + visualizer;
	}

}
