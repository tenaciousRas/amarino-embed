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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.abraxas.amarino.visualizer.BarsView;
import at.abraxas.amarino.visualizer.GraphView;
import at.abraxas.amarino.visualizer.Visualizer;

/**
 * 
 * @author Bonifaz Kaufmann
 *
 * $Id: EventListAdapter.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class EventListAdapter extends BaseAdapter {
	
	public static final String VISUALIZER_TAG = "visualizer";
	
	private static final String TAG = "AmarinoEventListAdapter";

	ArrayList<Event> entries;
	Context context;
	
	public EventListAdapter(Context context, ArrayList<Event> entries){
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

		view = new LinearLayout(context);
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi.inflate(R.layout.event_entry, view, true);

		LinearLayout stub = (LinearLayout)view.findViewById(R.id.event);
		TextView flag = (TextView) view.findViewById(R.id.event_flag);
		TextView name = (TextView) view.findViewById(R.id.event_name);
		TextView desc = (TextView) view.findViewById(R.id.event_desc);
		
		Event event = entries.get(position);

		flag.setText("ID: " + (char)event.flag);
		name.setText(event.name);
		desc.setText(event.desc);
		
		if (stub.findViewWithTag(VISUALIZER_TAG) == null) {
			switch (event.visualizer){
	
			case AmarinoIntent.VISUALIZER_TEXT:
				Log.d(TAG, "build text visualizer for event: " + event.name);
				TextView tv = new TextView(context);
				
				LinearLayout.LayoutParams params = 
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
				tv.setLayoutParams(params);
				tv.setGravity(Gravity.LEFT);
				tv.setPadding(5, 5, 5, 5);
				tv.setTextSize(14.0f);
				tv.setTextColor(context.getResources().getColor(R.color.light_yellowish_text));
				tv.setTag(event.pluginId);
				tv.setId(event.pluginId);
				stub.addView(tv);
				break;
				
			case AmarinoIntent.VISUALIZER_BARS:
				Log.d(TAG, "build bars visualizer for event:" + event.name);
				BarsView bars = new BarsView(context);
				addVisualizer(stub, event, bars);
				break;
				
			case AmarinoIntent.VISUALIZER_GRAPH:
				Log.d(TAG, "build graph visualizer for event:" + event.name);
				GraphView graph = new GraphView(context);
				addVisualizer(stub, event, graph);
				break;
				
			}
		}
		else {
			Log.d(TAG, "visualizer already built for event: " + event.name);
		}
		return view;
	}

	private void addVisualizer(LinearLayout stub, Event event, Visualizer visualizer) {
		LinearLayout.LayoutParams lp = 
			new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		visualizer.setLayoutParams(lp);
		visualizer.setPadding(5, 5, 5, 5);
		visualizer.setTag(event.pluginId);
		visualizer.setId(event.pluginId);
		visualizer.setBoundaries(event.visualizerMinValue, event.visualizerMaxValue);
		stub.addView(visualizer);
	}
}


