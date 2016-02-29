package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;
import java.util.zip.Inflater;

import org.json.JSONException;
import org.json.JSONObject;

import com.rutvik.moodleattendanceapp.R;
import com.rutvik.moodleattendanceapp.SessionActivity.ViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StudentAttendanceActivity extends AppCompatActivity {
	
	
	Toolbar mActionBar;
	
	String actionBarTitle,actionBarSubTitle;
	
	int coursePosition,attendancePosition;
	
	SwipeRefreshLayout swipeRefreshLayout;
	
	ListView listView;
	
	StudentAttendanceAdapter adapter;
	
	ArrayList<StudentSession> arrayListStudentSessions;
	
	SharedPreferences mSharedPreferences;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.sessions_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId())
		{
		
			case android.R.id.home:
				
				closeActivity();
				
				return true;
				
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	public void refreshStudentAttendance()
	{
		if(mSharedPreferences.contains("tmp_username") && mSharedPreferences.contains("tmp_password"))
    	{
			new AsyncTask<Void, Void, Void>(){
				
				String response="";
				
				Boolean success=false;
	
										
				@Override
				protected Void doInBackground(Void... params) {
					
					ServiceHandler sh=new ServiceHandler();
					try {
						response=sh.login(mSharedPreferences.getString("tmp_username", null), mSharedPreferences.getString("tmp_password", null));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
					
				}
				
				@Override
				protected void onPostExecute(Void result) {
					
					try {
						Log.i("MAA", response);
						JSONObject obj=new JSONObject(response).getJSONObject("user");							
						GlobalJSONObjects.getInstance().setUser(new User(obj));
						Log.i("MAA",GlobalJSONObjects.getInstance().getUser().getFull_name());
						success=true;
						
					} catch (JSONException e) {
	
						try {
							ErrorObj errObj=new ErrorObj(response);
							Toast.makeText(StudentAttendanceActivity.this, errObj.getComment(), Toast.LENGTH_SHORT).show();
						} catch (JSONException e1) {
							Log.e("MAA", "error in processing ERROR JSON");
							e1.printStackTrace();
						}
	
						Log.e("MAA", "error in processing JSON");
						e.printStackTrace();
						
					}				
					
					if(success)
					{
						
						//Log.i("MAA", GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions().get(0).getSessionDate());
						
						arrayListStudentSessions.clear();		
						
						arrayListStudentSessions.addAll(GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStudentSessions());
						
						//Log.i("MAA", "size is "+GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions().size()+"");
						
						adapter.notifyDataSetChanged();
						
						
						
					}
					
					//progressDialog.dismiss();
					
					swipeRefreshLayout.setRefreshing(false);
	
					
				}
			
			}.execute();
    	}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);
		
		GlobalJSONObjects.getInstance().restoreJSONObjects(mSharedPreferences);
		
		actionBarTitle=getIntent().getStringExtra("title");
		
		coursePosition=getIntent().getIntExtra("course_position",0);
		
		attendancePosition=getIntent().getIntExtra("attendance_position",0);
		

		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_student_attendance);

        SetActionBar();
		
		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);
		
		
		listView=(ListView) findViewById(R.id.lvStudentAttendance2);
		
		
		
		swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.studentAttendanceSwipeContainer);
		
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				
				if(ServiceHandler.hasActiveInternetConnection(StudentAttendanceActivity.this))
				{
				
					refreshStudentAttendance();
				
				}
			}
		});
		
		swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
		
		
		arrayListStudentSessions=GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStudentSessions();
		
		for(StudentSession s:arrayListStudentSessions)
		{
			Log.i("MAA", s.getSession_date()+"");
		}
		
		adapter=new StudentAttendanceAdapter(this, arrayListStudentSessions);
		
		
		
		Log.i("MAA", "NOT NULL");
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(adapter);
		
		
		
		
	}
	
	
	public void SetActionBar() {
		mActionBar = (Toolbar) findViewById(R.id.app_bar);


		mActionBar.setTitle(actionBarTitle);
		mActionBar.setSubtitle("Session");
		setSupportActionBar(mActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	
	
	class StudentAttendanceAdapter extends BaseAdapter implements OnItemClickListener
	{
		
		ArrayList<StudentSession> arrayListStudentSessions;
		
		Context context;
		
		StudentAttendanceAdapter(Context context,ArrayList<StudentSession> arrayListStudentSessions)
		{
			this.context=context;
			this.arrayListStudentSessions=arrayListStudentSessions;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayListStudentSessions.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrayListStudentSessions.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return arrayListStudentSessions.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			
			final ViewHolder holder;
		        
		       if(convertView==null){ 
		            
		           /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
		    	   LayoutInflater inflater = (LayoutInflater)getSystemService(
						     Context.LAYOUT_INFLATER_SERVICE);
		           convertView = inflater.inflate(R.layout.single_student_attendancce_row, null);
		            
		           /****** View Holder Object to contain tabitem.xml file elements ******/

		           holder = new ViewHolder();
		           holder.tvSessionDate = (TextView) convertView.findViewById(R.id.tvSessionDate2);
		           holder.tvDescription = (TextView) convertView.findViewById(R.id.tvSessionDescription2);
		           holder.tvAcronym=(TextView) convertView.findViewById(R.id.tvAcronym);
		           
		           
		           //holder.tvDescription.setTextColor(Color.WHITE);
		
		          /************  Set holder with LayoutInflater ************/
		           convertView.setTag(holder);
		       }
		       else 
		       {
		           holder=(ViewHolder)convertView.getTag();
		       }
		        
		       
		       if(arrayListStudentSessions.size()>0)
		       {
		    	 
		           /***** Get each Model object from Arraylist ********/

		           
		    	   
		    	   
		           /************  Set Model values in Holder elements ***********/

		            holder.tvSessionDate.setText(arrayListStudentSessions.get(position).getSessionDate());
		            holder.tvDescription.setText(arrayListStudentSessions.get(position).getDescription());
		            holder.tvAcronym.setText(arrayListStudentSessions.get(position).getAcronym());
		            
		            convertView.setBackgroundColor(Color.parseColor(GlobalSettings.getInstance().getStatusColor(mSharedPreferences, arrayListStudentSessions.get(position).getAcronym())));
		           
		            Log.i("MAA","Acronym is: "+ arrayListStudentSessions.get(position).getAcronym() +"COLOR IS: "+GlobalSettings.getInstance().getStatusColor(mSharedPreferences, arrayListStudentSessions.get(position).getAcronym()));
		            
		            /******** Set Item Click Listner for LayoutInflater for each row *******/

		       }
		       
		       return convertView;
			

		}
		
		private class ViewHolder{
	        
		       public TextView tvSessionDate;
		       public TextView tvDescription;
		       public TextView tvAcronym;		       

		   }

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			
			String status="Status: "+arrayListStudentSessions.get(pos).getDesc();
			
			
			String tb="Taken By: "+arrayListStudentSessions.get(pos).getFirst_name()+" "+arrayListStudentSessions.get(pos).getLast_name();

			String rem="Remark: "+arrayListStudentSessions.get(pos).getRemarks();
			
			
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
			dlgAlert.setMessage(status+"\n\n"+rem+"\n\n"+tb);
			dlgAlert.setTitle("Attendance Information");
			dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.dismiss();
					
				}
			});
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
			
		}

		
	}
	
	@Override
	public void onBackPressed() {
		closeActivity();
	}
	
	public void closeActivity()
	{
		Log.i("MAA", "CLOSING ACTIVITY");
		Intent returnIntent = new Intent();
		returnIntent.putExtra("course_position", coursePosition);
		setResult(RESULT_OK,returnIntent);
		this.finish();
		
	}
	

}
