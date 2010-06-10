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

import android.graphics.drawable.Drawable;

/**
 * 
 * @author Bonifaz Kaufmann
 *
 * $Id: Plugin.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class Plugin {
	public String label;
	public Drawable icon;
	public String packageName;
	public String editClassName;
	
	public String toString(){
		return "label: " + label
				+ " - packageName: " + packageName
				+ " - className: " + editClassName;
	}
}
