package com.oneonone.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AlarmClockDbAdapter {
	public static final String KEY_TITLE = "title";
	public static final String KEY_START = "start";
	public static final String KEY_WEEKDAYS = "weekdays";
	public static final String KEY_REPEAT = "repeat";
	public static final String KEY_END = "end";
	public static final String KEY_ROWID = "_id";
	
	private static final String TAG = "AlarmsDbAdapter";
	private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "alarms";
	private static final String DATABASE_CREATE = String.format( 
			"create table %1$s (%2$s integer primary key autoincrement, "
	        + "%3$s text not null,"
	        + "%4$s text not null,"
	        + "%5$s text not null,"
	        + "%6$s text not null,"
	        + "%7$s text not null);", DATABASE_TABLE, KEY_ROWID, KEY_TITLE, KEY_START, KEY_WEEKDAYS, KEY_REPEAT, KEY_END);
    private static final int DATABASE_VERSION = 3;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
			
		}
		
		@Override
		public void onCreate(SQLiteDatabase db){
			String create = DATABASE_CREATE;
			db.execSQL(create);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			 Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	                    + newVersion + ", which will destroy all old data");
			 db.execSQL(String.format("DROP TABLE IF EXISTS %s", DATABASE_TABLE));
			 onCreate(db);
		}
	}
	
	AlarmClockDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	public AlarmClockDbAdapter open() throws SQLException{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		mDbHelper.close();
	}
	
	public long createAlarm(String title, String start, String[] weekdays, Long repeat, String end){
		long ret = -1;
		ContentValues initialValues = new ContentValues();
		try{
			initialValues.put(KEY_TITLE, title);
			initialValues.put(KEY_START, start);
			initialValues.put(KEY_WEEKDAYS, arrayToJson(weekdays));
			initialValues.put(KEY_REPEAT, repeat);
			initialValues.put(KEY_END, end);
			ret = mDb.insert(DATABASE_TABLE, null, initialValues);
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
		return ret;
	}
	
	private String arrayToJson(String[] arr) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("days", arr);
		return json.toString();
	}
	
	public boolean deleteAlarm(long rowId){
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public Cursor fetchAllAlarms(){
		return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_START, KEY_WEEKDAYS, KEY_REPEAT, KEY_END}, null, null, null, null, null);
	}
	
	public Cursor fetchAlarm(long rowId) throws SQLException{
		Cursor mCursor = 
			mDb.query(true, 
					DATABASE_TABLE, 
					new String[]{KEY_TITLE, KEY_START, KEY_WEEKDAYS, KEY_REPEAT, KEY_END}, 
					KEY_ROWID +"="+ rowId, 
					null, null, null, null, null);
		if(mCursor!=null)
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public boolean updateAlarm(long rowId, String title, String start, String[] weekdays, Long repeat, String end){
		boolean ret = false;
		ContentValues args = new ContentValues();
		try{
			args.put(KEY_TITLE, title);
			args.put(KEY_START, start);
			args.put(KEY_WEEKDAYS, arrayToJson(weekdays));
			args.put(KEY_REPEAT, repeat);
			args.put(KEY_END, end);
			ret = mDb.update(DATABASE_TABLE, args, KEY_ROWID +"="+rowId, null) > 0;
		}catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return ret;
	}
}
