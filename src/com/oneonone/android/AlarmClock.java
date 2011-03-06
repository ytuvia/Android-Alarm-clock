package com.oneonone.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AlarmClock extends ListActivity {
	private static final String TAG = "AlarmClock";
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int DELETE_ID = Menu.FIRST + 1;
	public static final int INSERT_ID = Menu.FIRST;
	private AlarmClockDbAdapter mDbHelper;
	private Cursor mAlarmsCursor;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarms_list);
        mDbHelper = new AlarmClockDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0,INSERT_ID,0,R.string.menu_insert);
        return result;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case INSERT_ID:
    		createAlert();
    		return true;
    	}
        return super.onOptionsItemSelected(item);
    }
    
    private void createAlert(){
    	Intent i = new Intent(this, AlarmEdit.class);
    	startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void fillData(){
    	mAlarmsCursor = mDbHelper.fetchAllAlarms();
    	startManagingCursor(mAlarmsCursor);
    	String[] from = new String[]{AlarmClockDbAdapter.KEY_TITLE, AlarmClockDbAdapter.KEY_WEEKDAYS};
    	int[] to = new int[]{R.id.start, R.id.weekdays};
    	SimpleCursorAdapter alerts = new SimpleCursorAdapter(this,R.layout.alarms_row, mAlarmsCursor, from, to);
    	setListAdapter(alerts);
    }
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			mDbHelper.deleteAlarm(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor c = mAlarmsCursor;
        c.moveToPosition(position);
        try{
        Intent i = new Intent(this, AlarmEdit.class);
        i.putExtra(AlarmClockDbAdapter.KEY_ROWID, id);
        i.putExtra(AlarmClockDbAdapter.KEY_TITLE,
        		c.getString(c.getColumnIndex(AlarmClockDbAdapter.KEY_TITLE)));
        i.putExtra(AlarmClockDbAdapter.KEY_START,
        		c.getString(c.getColumnIndex(AlarmClockDbAdapter.KEY_START)));
        i.putExtra(AlarmClockDbAdapter.KEY_WEEKDAYS,
        		jsonToArray(c.getString(c.getColumnIndex(AlarmClockDbAdapter.KEY_WEEKDAYS))));
        i.putExtra(AlarmClockDbAdapter.KEY_REPEAT,
        		c.getLong(c.getColumnIndex(AlarmClockDbAdapter.KEY_REPEAT)));
        i.putExtra(AlarmClockDbAdapter.KEY_END,
                c.getString(c.getColumnIndex(AlarmClockDbAdapter.KEY_END)));
        startActivityForResult(i, ACTIVITY_EDIT);
        }catch(Exception e){
        	Log.e(TAG, e.getMessage());
        }
    }
    
    private String[] jsonToArray(String json) throws JSONException{
    	String[] ret = null;
    	JSONObject obj = new JSONObject(json);
    	JSONArray names = obj.names();
    	for(int i=0; i<names.length(); i++){
    		ret[i] = names.getString(i);
    	}
    	return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        String title = extras.getString(AlarmClockDbAdapter.KEY_TITLE);
    	String start = extras.getString(AlarmClockDbAdapter.KEY_START);
    	String weekdays[] = extras.getStringArray(AlarmClockDbAdapter.KEY_WEEKDAYS);
    	Long repeat = extras.getLong(AlarmClockDbAdapter.KEY_REPEAT);
    	String end = extras.getString(AlarmClockDbAdapter.KEY_END);
        switch(requestCode){
        case ACTIVITY_CREATE:
        	mDbHelper.createAlarm(title,start, weekdays, repeat, end);
        	fillData();
        	break;
        case ACTIVITY_EDIT:
        	Long mRowId = extras.getLong(AlarmClockDbAdapter.KEY_ROWID);
        	if(mRowId!=null){
        		mDbHelper.updateAlarm(mRowId, title, start, weekdays,repeat,end);
        	}
        	fillData();
        	break;
        }
        
    }
}
