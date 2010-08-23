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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import at.abraxas.amarino.log.Logger;

/**
 * $Id: AmarinoDbAdapter.java 444 2010-06-10 13:11:59Z abraxas $
 */
public class AmarinoDbAdapter {
	
	public static final String KEY_DEVICE_ID = "_id";
	public static final String KEY_DEVICE_NAME = "name";
	public static final String KEY_DEVICE_ADDRESS = "device_address";
	
	public static final String KEY_EVENT_ID = "_id";
	public static final String KEY_EVENT_NAME = "event_name";
	public static final String KEY_EVENT_DESC = "desc";
	public static final String KEY_EVENT_VISUALIZER = "visualizer";
	public static final String KEY_EVENT_VISUALIZER_MIN = "minVal";
	public static final String KEY_EVENT_VISUALIZER_MAX = "maxVal";
	public static final String KEY_EVENT_FLAG = "flag";
	public static final String KEY_EVENT_PACKAGE_NAME = "package";
	public static final String KEY_EVENT_EDIT_CLASS_NAME = "edit_class";
	public static final String KEY_EVENT_SERVICE_CLASS_NAME = "service_class";
	public static final String KEY_EVENT_PLUGIN_ID = "plugin_id";
	public static final String KEY_EVENT_DEVICE_ID = "device_id";
	
	private static final boolean DEBUG = true;
	private static final String TAG = "AmarinoDbAdapter";
	private static final int DATABASE_VERSION = 2;
	
	private static final String DATABASE_NAME = "amarino_2.db";
	private static final String DEVICE_TABLE_NAME = "devices_tbl";
	private static final String EVENT_TABLE_NAME = "events_tbl";
	

	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	Log.d(TAG, "create database tables");
        	/* Create Devices Table */
        	db.execSQL("CREATE TABLE " + DEVICE_TABLE_NAME + " ("
                    + KEY_DEVICE_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DEVICE_ADDRESS + " TEXT UNIQUE,"
                    + KEY_DEVICE_NAME  + " TEXT"
                    + ");");
        	
        	db.execSQL("CREATE TABLE " + EVENT_TABLE_NAME + " ("
        			+ KEY_EVENT_ID + " INTEGER PRIMARY KEY,"
                    + KEY_EVENT_NAME + " TEXT NOT NULL,"
                    + KEY_EVENT_DESC  + " TEXT,"
                    + KEY_EVENT_VISUALIZER  + " INTEGER,"
                    + KEY_EVENT_VISUALIZER_MIN  + " NUMBER,"
                    + KEY_EVENT_VISUALIZER_MAX  + " NUMBER,"
                    + KEY_EVENT_FLAG  + " INTEGER NOT NULL,"
                    + KEY_EVENT_PACKAGE_NAME  + " TEXT NOT NULL,"
                    + KEY_EVENT_EDIT_CLASS_NAME  + " TEXT NOT NULL,"
                    + KEY_EVENT_SERVICE_CLASS_NAME  + " TEXT NOT NULL,"
                    + KEY_EVENT_PLUGIN_ID  + " INTEGER NOT NULL,"
                    + KEY_EVENT_DEVICE_ID  + " INTEGER REFERENCES " + DEVICE_TABLE_NAME + "(_id) "
                    + ");");
        	
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            
            db.execSQL("DROP TABLE IF EXISTS " + DEVICE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
            onCreate(db);
            Log.d(TAG, "upgrade db");
        }
        
	}
	
	/**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public AmarinoDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public AmarinoDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    
    /**
     * Create a new device using the address and name provided. If the device is
     * successfully created return the new rowId for that device, otherwise return
     * a -1 to indicate failure.
     * 
     * @param address the address of the device
     * @param name the name of the device
     * @return rowId or -1 if failed
     */
    public long createDevice(BTDevice device) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DEVICE_ADDRESS, device.address);
        initialValues.put(KEY_DEVICE_NAME, (device.name == null) ? "NONAME" : device.name);

        return mDb.insert(DEVICE_TABLE_NAME, null, initialValues);
    }
    
    /**
     * Delete the device with the given rowId
     * 
     * @param rowId id of device to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteDevice(long deviceId) {
    	int numEvents = deleteEvents(deviceId);
    	if (DEBUG) Logger.d(TAG, "delete device with id " + deviceId + ": " + numEvents + " associated events removed");
        return mDb.delete(DEVICE_TABLE_NAME, KEY_DEVICE_ID + "=" + deviceId, null) > 0;
    }
    
    public BTDevice getDevice(String address){
    	BTDevice device = null;
    	Cursor c = mDb.query(DEVICE_TABLE_NAME, null, KEY_DEVICE_ADDRESS + " like ?", new String[]{address}, null, null, null);

        if (c == null){
        	return null;
        }
        if (c.moveToFirst()){	
    		String name = c.getString(c.getColumnIndex(KEY_DEVICE_NAME));
    		long id = c.getLong(c.getColumnIndex(KEY_DEVICE_ID));
    		device = new BTDevice(id, address, name);
        }
        c.close();
    	return device;
    }
    
    
    /**
     * Return a list of all devices in the database
     * 
     * @return ArrayList over all devices
     */
    public ArrayList<BTDevice> fetchAllDevices() {
    	ArrayList<BTDevice> devices = new ArrayList<BTDevice>();
    	
        Cursor c = mDb.query(DEVICE_TABLE_NAME, null, null, null, null, null, null);

        if (c == null){
        	return devices;
        }
        if (c.moveToFirst()){	
        	do {
        		String address = c.getString(c.getColumnIndex(KEY_DEVICE_ADDRESS));
        		String name = c.getString(c.getColumnIndex(KEY_DEVICE_NAME));
        		long id = c.getLong(c.getColumnIndex(KEY_DEVICE_ID));
        		devices.add(new BTDevice(id, address, name));
        	}
        	while(c.moveToNext());
        }

        c.close();
        return devices;
    }
    
    
    /**
     * Create a new event associated with a specific device. If the event is
     * successfully created return the new rowId for that event, otherwise return
     * a -1 to indicate failure.
     * 
     * @param address the address of the device
     * @param name the name of the device
     * @return rowId or -1 if failed
     */
    public long createEvent(Event event) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_EVENT_NAME, event.name);
        initialValues.put(KEY_EVENT_DESC, event.desc);
        initialValues.put(KEY_EVENT_VISUALIZER, event.visualizer);
        initialValues.put(KEY_EVENT_VISUALIZER_MIN, event.visualizerMinValue);
        initialValues.put(KEY_EVENT_VISUALIZER_MAX, event.visualizerMaxValue);
        initialValues.put(KEY_EVENT_FLAG, (int)event.flag);
        initialValues.put(KEY_EVENT_PACKAGE_NAME, event.packageName);
        initialValues.put(KEY_EVENT_EDIT_CLASS_NAME, event.editClassName);
        initialValues.put(KEY_EVENT_SERVICE_CLASS_NAME, event.serviceClassName);
        initialValues.put(KEY_EVENT_PLUGIN_ID, event.pluginId);
        initialValues.put(KEY_EVENT_DEVICE_ID, event.deviceId);

        return mDb.insert(EVENT_TABLE_NAME, null, initialValues);
    }
    
    
    /**
     * Delete the event with the given rowId
     * 
     * @param rowId id of event to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteEvent(long rowId) {
        return mDb.delete(EVENT_TABLE_NAME, KEY_EVENT_ID + "=" + rowId, null) > 0;
    }
    
    /**
     * Delete all events associated to the given device
     * @param deviceId
     * @return
     */
    public int deleteEvents(long deviceId){
    	return mDb.delete(EVENT_TABLE_NAME, KEY_EVENT_DEVICE_ID + "=" + deviceId, null);
    }
    
    public Event getEvent(long deviceId, int pluginId){
    	Event e = null;
    	Cursor c = mDb.query(EVENT_TABLE_NAME, null, 
    			KEY_EVENT_DEVICE_ID + "=? AND " + KEY_EVENT_PLUGIN_ID + "=?", 
        		new String[]{String.valueOf(deviceId), String.valueOf(pluginId)}, null, null, null);
    	
    	if (c == null){
        	if (DEBUG) Logger.d(TAG, "no event found for device with id: " + deviceId + " and pluginId:" + pluginId);
        	return null;
        }
        if (c.moveToFirst()){
 	    	long id = c.getLong(c.getColumnIndex(KEY_EVENT_ID));
			String name = c.getString(c.getColumnIndex(KEY_EVENT_NAME));
			String desc = c.getString(c.getColumnIndex(KEY_EVENT_DESC));
			int visualizer = c.getInt(c.getColumnIndex(KEY_EVENT_VISUALIZER));
			float minVal = c.getFloat(c.getColumnIndex(KEY_EVENT_VISUALIZER_MIN));
			float maxVal = c.getFloat(c.getColumnIndex(KEY_EVENT_VISUALIZER_MAX));
			char flag = (char) c.getInt(c.getColumnIndex(KEY_EVENT_FLAG));
			String packageName= c.getString(c.getColumnIndex(KEY_EVENT_PACKAGE_NAME));
			String editClassName= c.getString(c.getColumnIndex(KEY_EVENT_EDIT_CLASS_NAME));
			String serviceClassName= c.getString(c.getColumnIndex(KEY_EVENT_SERVICE_CLASS_NAME));

			e = new Event(id, name, desc, visualizer, flag, packageName, editClassName,
					serviceClassName, pluginId, deviceId);
			e.visualizerMinValue = minVal;
			e.visualizerMaxValue = maxVal;
        }
        
        c.close();
		return e;
    }
    
    /**
     * Return a list of all events for a given device
     *  
     * @return ArrayList of all events for the given device
     */
    public ArrayList<Event> fetchEvents(long deviceId) {
    	ArrayList<Event> events = new ArrayList<Event>();
    	
        Cursor c = mDb.query(EVENT_TABLE_NAME, null, KEY_EVENT_DEVICE_ID + "=" + deviceId , 
        		null, null, null, null);

        if (c == null){
        	if (DEBUG) Logger.d(TAG, "no events found for device with id: " + deviceId);
        	return events;
        }
        if (c.moveToFirst()){	
        	do {
        		long id = c.getLong(c.getColumnIndex(KEY_EVENT_ID));
        		String name = c.getString(c.getColumnIndex(KEY_EVENT_NAME));
        		String desc = c.getString(c.getColumnIndex(KEY_EVENT_DESC));
        		int visualizer = c.getInt(c.getColumnIndex(KEY_EVENT_VISUALIZER));
        		float minVal = c.getFloat(c.getColumnIndex(KEY_EVENT_VISUALIZER_MIN));
        		float maxVal = c.getFloat(c.getColumnIndex(KEY_EVENT_VISUALIZER_MAX));
        		char flag = (char) c.getInt(c.getColumnIndex(KEY_EVENT_FLAG));
        		String packageName= c.getString(c.getColumnIndex(KEY_EVENT_PACKAGE_NAME));
        		String editClassName= c.getString(c.getColumnIndex(KEY_EVENT_EDIT_CLASS_NAME));
        		String serviceClassName= c.getString(c.getColumnIndex(KEY_EVENT_SERVICE_CLASS_NAME));
        		int pluginId = c.getInt(c.getColumnIndex(KEY_EVENT_PLUGIN_ID));
        		
        		Event e = new Event(id, name, desc, visualizer, flag, packageName, editClassName,
        				serviceClassName, pluginId, deviceId);
        		e.visualizerMinValue = minVal;
        		e.visualizerMaxValue = maxVal;
        		
        		events.add(e);
        		if (DEBUG) Logger.d(TAG, "event found: " + e.name + " - id=" + e.pluginId);
        	}
        	while(c.moveToNext());
        }
        else {
        	if (DEBUG) Logger.d(TAG, "no events found for device with id: " + deviceId);
        }

        c.close();
        return events;
    }
    
    
    public int updateEvent(Event event){
    	ContentValues values = new ContentValues();

    	values.put(KEY_EVENT_VISUALIZER, event.visualizer);
    	values.put(KEY_EVENT_VISUALIZER_MIN, event.visualizerMinValue);
    	values.put(KEY_EVENT_VISUALIZER_MAX, event.visualizerMaxValue);
        
    	return mDb.update(EVENT_TABLE_NAME, values, KEY_EVENT_ID + "=" + event.id, null);
    }
    


}
