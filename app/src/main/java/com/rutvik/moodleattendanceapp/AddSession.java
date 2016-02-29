package com.rutvik.moodleattendanceapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.rutvik.moodleattendanceapp.R;

public class AddSession extends AppCompatActivity implements OnClickListener {

	TextView tvStartDate,tvDuration,tvTime;
	
	int year,month,day;
	
	String SelectedStartDate="";
	
	EditText etDesc;
	
	final Calendar c=Calendar.getInstance();
			
	
	
	
	// Variable for storing current date and time
    private int mYear, mMonth, mDay, mHour, mMinute;
    
    long mSecondDuration,mSecond,date_timestamp=0,time_mod=0;
    
    String description,AttendanceTypeid;
    Menu menu;
    Context context;
    Toolbar mActionBar;
    ProgressDialog progressDialog;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_session);


        SetActionBar();
		
		
		c.set(Calendar.MILLISECOND, 0);
		
		
		tvStartDate = (TextView) findViewById(R.id.tvStartDate2);	
		tvDuration=(TextView)findViewById(R.id.tvDuration2);
		tvTime=(TextView) findViewById(R.id.tvStartTime2);
		etDesc=(EditText)findViewById(R.id.etDescription2);
		etDesc.setText("Regular Session");
		//setCurrentDateOnView();
		
		progressDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
		progressDialog.setMessage("Please wait...");
		// Set Cancelable as False
		progressDialog.setCancelable(false);
		AttendanceTypeid=getIntent().getExtras().getString("AttendanceTypeid");
		context=this;
		
		tvStartDate.setOnClickListener(this);
		tvTime.setOnClickListener(this);

		
		tvDuration.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTime();
				
				
			}
		});
	
	}
	

	
	
	public void setTime()
	{
		// Process to get Current Time
        final Calendar c2 = Calendar.getInstance();
        mHour = c2.get(Calendar.HOUR_OF_DAY);
        mMinute = c2.get(Calendar.MINUTE);
        
        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                            int minute) {
                        // Display Selected time in textbox
                    	tvDuration.setText(hourOfDay + ":" + minute);
                        mHour=view.getCurrentHour();
                        mMinute=view.getCurrentMinute();
                        mSecond = (((mHour*60)+mMinute)*60);
                    }
                }, mHour, mMinute, true);
        
        tpd.show();
		
	}
	
	
	private void closeActivity() {
		
		
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
			dlgAlert.setMessage("Are you sure you want to go back?");
			dlgAlert.setTitle("Alert!");
			dlgAlert.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					AddSession.this.finish();
					
				}
	
				
			});
			
			dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.dismiss();
					
				}
			});
	
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
		

		
	}
	
	
	@Override
	public void onBackPressed() {
		
		closeActivity();
	}
	
	
	
	public void setCurrentDateOnView()
	{
	
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		SelectedStartDate = year + "-" + checkDigit(month + 1) + "-"
				+ checkDigit(day);
		SelectedStartDate = ChangeDateFormat(SelectedStartDate);
		tvStartDate.setText(SelectedStartDate);
	}

	public String checkDigit(int number)
	{
		return number <= 9 ? "0" + number : String.valueOf(number);
	}
	
	public String ChangeDateFormat(String date)
	{
		date = date.substring(0, 10);
		SimpleDateFormat InputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date parseDate = null;
		try {
			parseDate = InputFormat.parse(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		String newDateFormat = formatter.format(parseDate);
		return newDateFormat;
	}


public void makeServiceCall(String attendanceTypeId,String ssn_date,String duration,String time_mod,String desc)
{
	final Context c=context;
	
	final String attendanceId=attendanceTypeId;
	final String s_date=ssn_date;
	final String dur=duration;
	final String t_mod=time_mod;
	final String descp=desc;
	
	new AsyncTask<Void, Void, Void>(){
		
		String response="";		

		@Override
		protected void onPreExecute() {
			progressDialog=null;
			progressDialog=ProgressDialog.show(c, "Connecting...", "Please Wait...",true);
			progressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			ServiceHandler sh=new ServiceHandler();
			response=sh.addSessions(attendanceId, s_date, dur, t_mod,descp);
			Log.i("doback res","do : "+response);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			try {
				//Log.i("MAA", response);
				Log.i("post res","post : "+response);
				JSONObject obj=new JSONObject(response).getJSONObject("response");							
				Response resp=new Response(obj);
				Toast.makeText(c, resp.getComment(), Toast.LENGTH_LONG).show();
				
				progressDialog.dismiss();
				
				
				
				Log.i("Done","Add Session!!!");
				
				Intent returnIntent = new Intent();
				setResult(RESULT_OK,returnIntent);
				AddSession.this.finish();
				
				
			} catch (JSONException e) {

				try {
					ErrorObj errObj=new ErrorObj(response);
					Toast.makeText(c, errObj.getComment(), Toast.LENGTH_SHORT).show();
				} catch (JSONException e1) {
					Log.e("MAA", "error in processing ERROR JSON");
					e1.printStackTrace();
				}

				Log.e("MAA", "error in processing JSON");
				e.printStackTrace();
				
			}				
			
			
			
			progressDialog.dismiss();
			
		}
	
	}.execute();
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.fill_attendance_menu, menu);
		
		this.menu=menu;
		
		menu.findItem(R.id.action_save_attendance).setTitle("Save New Session");
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId())
		{
		
			case android.R.id.home:
				closeActivity();
				return true;
		
			case R.id.action_save_attendance:
				
				if(date_timestamp==0)
				{
					Toast.makeText(getApplicationContext(), "Select Session Date", Toast.LENGTH_LONG).show();
				}
				else if(mSecond==0)
				{
					Toast.makeText(getApplicationContext(), "Select Duration", Toast.LENGTH_LONG).show();
				}
				else if (etDesc.getText().toString().equals("") || etDesc.getText().toString().equals(null)) {
					description="";
				}
				else {
					description=etDesc.getText().toString();
					time_mod=(System.currentTimeMillis()/1000);
					Log.i("MAA", "ok ssn");
					if(ServiceHandler.hasActiveInternetConnection(this))
					{
						Log.i("MAA", "date_timestamp: "+date_timestamp+" mSecond: "+mSecond+" time_mod: "+time_mod+" description: "+description);
						makeServiceCall(AttendanceTypeid,String.valueOf(date_timestamp),String.valueOf(mSecond),String.valueOf(time_mod),String.valueOf(description));
					}
				}
				Log.i("MAA", "Session Added");
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void SetActionBar() {
		mActionBar = (Toolbar) findViewById(R.id.app_bar);

		mActionBar.setTitle("Add Session");

        setSupportActionBar(mActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onClick(View v) {
		
		if (v == tvStartDate) {
			 
            // Process to get Current Date
            
            //c.set(Calendar.MILLISECOND, 0);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

 
            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
 
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                        	
                        	 mYear = year;
                             mMonth = monthOfYear;
                             mDay = dayOfMonth;
                        	
                        	c.set(Calendar.YEAR, year);
                        	c.set(Calendar.MONTH, monthOfYear);
                        	c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                			
                			int dateInSeconds = (int)(c.getTimeInMillis()/1000);
                			
                			
                        	
                        	Log.i("MAA", "DATE IN UNIX IS: "+dateInSeconds);
                        	
                        	tvStartDate.setText(dayOfMonth + "-"
                                    + (monthOfYear + 1) + "-" + year);
 
                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
        if (v == tvTime) {
 
            // Process to get Current Time
            //final Calendar c = Calendar.getInstance();
//            c.set(Calendar.MILLISECOND, 0);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
 
            // Launch Time Picker Dialog
            TimePickerDialog tpd = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
 
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                int minute) {
                        	
                        	
                        	
                        	Log.i("MAA", "Hour is: "+hourOfDay);
                        	
                        	Log.i("MAA", "Minute IS: "+minute);
                        	
                        	c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        	c.set(Calendar.MINUTE, minute);
                        	c.set(Calendar.SECOND, 0);
                        	
                        	Log.i("MAA", "SET Hour is: "+c.get(Calendar.HOUR_OF_DAY));
                        	
                        	Log.i("MAA", "SET Minute IS: "+c.get(Calendar.MINUTE));
                        	
                        	
                        	int dateInSeconds = (int)(c.getTimeInMillis()/1000);
                        	
                        	date_timestamp=dateInSeconds;
                        	
                        	Log.i("MAA", "FINAL TIME STAMP IS: "+dateInSeconds);
                        	
                        	tvTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            tpd.show();
        }
    
		
	}
	
}
