package com.rutvik.moodleattendanceapp;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.rutvik.moodleattendanceapp.R;


public class InfoSettingsActivity extends AppCompatActivity {
	
	Toolbar mActionBar;
	
	EditText etMoodleHost;
	
	int count=1;
	
	SharedPreferences mSharedPreferences;
	
	TextView tv,info;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MoodleAttendanceApp.getInstance().trackScreenView("Info and Settings Page");
	}
	
	@Override
	public void onBackPressed() {
		if(!TextUtils.isEmpty(etMoodleHost.getText()))
		{
			GlobalSettings.getInstance().setHost(mSharedPreferences, etMoodleHost.getText().toString());
		}
		
		super.onBackPressed();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home)
		{
			if(!TextUtils.isEmpty(etMoodleHost.getText()))
			{
				GlobalSettings.getInstance().setHost(mSharedPreferences, etMoodleHost.getText().toString());
			}
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		
		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);
		
		super.onCreate(savedInstanceState);
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		setContentView(R.layout.activity_info_settings);

        SetActionBar();
		
		etMoodleHost=(EditText) findViewById(R.id.etMoodleHost);
		
		tv=(TextView) findViewById(R.id.tvText);
		
		info=(TextView) findViewById(R.id.tvInfo);
		
		info.setMovementMethod(new ScrollingMovementMethod());
		info.setMovementMethod(LinkMovementMethod.getInstance());
		
		if(mSharedPreferences.contains("host"))
		{
			etMoodleHost.setText(mSharedPreferences.getString("host", "moodle"));
			etMoodleHost.setEnabled(false);
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					count=count+1;
					
					if(count>2)
					{
						etMoodleHost.setEnabled(true);
					}
					
				}
			});
		}
		
		
	}
	
	public void SetActionBar() {
		mActionBar = (Toolbar)findViewById(R.id.app_bar);
		//mActionBar.setDisplayShowTitleEnabled(true);

		mActionBar.setTitle("Moodle Host and Info");
		
		setSupportActionBar(mActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}


}
