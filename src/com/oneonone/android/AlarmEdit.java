package com.oneonone.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AlarmEdit extends Activity {
	Long mRowId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.alarms_edit);
		setTitle(R.string.edit_alarm);
		TextView labelText = (TextView) findViewById(R.id.title);
		TextView weekdaysText = (TextView) findViewById(R.id.weekdays);
		TextView startText = (TextView) findViewById(R.id.start);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			String title = extras.getString(AlarmClockDbAdapter.KEY_TITLE);
			String start = extras.getString(AlarmClockDbAdapter.KEY_START);
			String[] weekdays = extras.getStringArray(AlarmClockDbAdapter.KEY_WEEKDAYS);
			Long repeat = extras.getLong(AlarmClockDbAdapter.KEY_REPEAT);
			String end = extras.getString(AlarmClockDbAdapter.KEY_END);
			mRowId = extras.getLong(AlarmClockDbAdapter.KEY_ROWID);
			
			labelText.setText(title);
			startText.setText(start);
			//weekdaysText.setText(weekdays.toString());
		}
		confirmButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				/*bundle.putString(AlarmClockDbAdapter.KEY_TITLE, mTitleText.getText().toString());
				bundle.putString(AlarmClockDbAdapter.KEY_START, mStartText.getText().toString());
				bundle.putStringArray(AlarmClockDbAdapter.KEY_WEEKDAYS, new String[]{"sun","mon"});
				bundle.putLong(AlarmClockDbAdapter.KEY_REPEAT, 45);
				bundle.putString(AlarmClockDbAdapter.KEY_END, mEndText.getText().toString());
				if(mRowId!=null){
					bundle.putLong(AlarmClockDbAdapter.KEY_ROWID, mRowId);
				}
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK,mIntent);
				finish();
				*/
			}
		});
		
		super.onCreate(savedInstanceState);
	}

}
