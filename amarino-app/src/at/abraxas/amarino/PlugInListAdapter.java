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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Bonifaz Kaufmann
 *
 * $Id: PlugInListAdapter.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class PlugInListAdapter extends BaseAdapter {

	ArrayList<Plugin> entries;
	Context context;
	
	public PlugInListAdapter(Context context, ArrayList<Plugin> entries){
		this.context = context;
		this.entries = entries;
	}
	
	public int getCount() {
		if (entries != null) {
			return entries.size();
		}
		return 0;
	}

	public Object getItem(int position) {
		return entries.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout view = null;

		//((AbsListView)parent).setCacheColorHint(R.color.background);
		if (convertView == null) {
			view = new LinearLayout(context);
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi.inflate(R.layout.plugin_entry, view, true);
		} else {
			view = (LinearLayout) convertView;
		}

		TextView name = (TextView) view.findViewById(R.id.name);
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		
		Plugin plugin = entries.get(position);
		name.setText(plugin.label);
		icon.setImageDrawable(plugin.icon);
		
		return view;
	}
}


