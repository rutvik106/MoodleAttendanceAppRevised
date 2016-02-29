package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class CourseAttendanceActivity extends AppCompatActivity {

    Toolbar mActionBar;
	
	ListView attendanceList;
	
	SwipeRefreshLayout swipeContainer;
	
	AttendanceListAdapter attendanceListAdapter;
	
	//ArrayList<Attendance> attendanceArrayList;
	
	int coursePosition;
	
	SharedPreferences mSharedPreferences;
	
	public static final int THREE=3;
	
	public static final int TWO=2;
	
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
							Toast.makeText(CourseAttendanceActivity.this, errObj.getComment(), Toast.LENGTH_SHORT).show();
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
						
												
						//sessionArrayList.addAll(GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions());
						
						//Log.i("MAA", "size is "+GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions().size()+"");
						
						attendanceListAdapter.notifyDataSetChanged();
						
						
						
					}
					
					//progressDialog.dismiss();
					
					swipeContainer.setRefreshing(false);
	
					
				}
			
			}.execute();
    	}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home)
		{
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void refreshAttendance()
	{
		new AsyncTask<Void, Void, Void>(){

			String response="";
			
			Boolean success=false;

									
			@Override
			protected Void doInBackground(Void... params) {
				
				ServiceHandler sh=new ServiceHandler();
				response=sh.getAttendanceType(GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getId());
				return null;
				
			}
			
			@Override
			protected void onPostExecute(Void result) {
				
				try {
					Log.i("MAA", response);
					JSONObject obj=new JSONObject(response);							
					GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).refreshAttendance(obj);
					Log.i("MAA",GlobalJSONObjects.getInstance().getUser().getFull_name());
					success=true;
					
				} catch (JSONException e) {

					try {
						ErrorObj errObj=new ErrorObj(response);
						Toast.makeText(CourseAttendanceActivity.this, errObj.getComment(), Toast.LENGTH_SHORT).show();
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
					
											
					//sessionArrayList.addAll(GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions());
					
					//Log.i("MAA", "size is "+GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions().size()+"");
					
					attendanceListAdapter.notifyDataSetChanged();
					
					
					
				}
				
				//progressDialog.dismiss();
				
				swipeContainer.setRefreshing(false);

				
			}
		
		}.execute();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		//super.onActivityResult(requestCode, resultCode, data);
		
		Log.i("MAA", "inside on activity result");
		if (requestCode == THREE) {
			Log.i("MAA", "request code 1 ok");
	        if(resultCode == RESULT_OK){
	        	Log.i("MAA", "result set to ok");
	            try
	            {
	            	Log.i("MAA", "course_position from return intent is: "+data.getIntExtra("course_position", 0));
	            	coursePosition=data.getIntExtra("course_position", 0);
	            }
	            catch (Exception e) {
	            	Log.i("MAA", "exception!!!");
				}
	        }
	        if (resultCode == RESULT_CANCELED) {
	            
	        }
	    }
		if (requestCode == TWO) {
	        if(resultCode == RESULT_OK){
	            try
	            {
	            	Log.i("MAA", "course_position from return intent is: "+data.getIntExtra("course_position", 0));
	            	coursePosition=data.getIntExtra("course_position", 0);
	            }
	            catch (Exception e) {
					
	            	
				}
	        }
	        if (resultCode == RESULT_CANCELED) {
	            
	        }
	    }
		
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_attendance);

        SetActionBar();
		
		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);
		
		GlobalJSONObjects.getInstance().restoreJSONObjects(mSharedPreferences);

		
		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.attendanceSwipeContainer);

		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				
				
				
				if(GlobalJSONObjects.getInstance().getUser().getRole_short_name(false).equals("Student"))
				{
					if(ServiceHandler.hasActiveInternetConnection(CourseAttendanceActivity.this))
					{
						refreshStudentAttendance();
					}
				}
				else
				{
					if(ServiceHandler.hasActiveInternetConnection(CourseAttendanceActivity.this))
					{
						refreshAttendance();
					}
				}
				
				
				
			}
		});
		
		swipeContainer.setColorScheme(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);

		
		attendanceList=(ListView) findViewById(R.id.lvCourseAttendanceList);
		
		coursePosition=getIntent().getIntExtra("position",0);
		
		Log.i("MAA", "course position is: "+coursePosition);
		
		//attendanceArrayList=GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance();
		
		//Log.i("MAA", "Attendance for Course "+coursePosition+"is "+attendanceArrayList.size());
		
		attendanceListAdapter=new AttendanceListAdapter(this, GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance());
		
		attendanceList.setAdapter(attendanceListAdapter);
		
		attendanceList.setOnItemClickListener(attendanceListAdapter);
		
		TextView emptyText = (TextView)findViewById(android.R.id.empty);
		
		attendanceList.setEmptyView(emptyText);
		
	}

	public void SetActionBar() {

        mActionBar =(Toolbar) findViewById(R.id.app_bar);
		mActionBar.setTitle("Attendance");
        setSupportActionBar(mActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	
	
	class AttendanceListAdapter extends BaseAdapter implements OnItemClickListener {

		/*********** Declare Used Variables *********/
		Context context;
		   private ArrayList<Attendance> data;//=new ArrayList<>();
		   private LayoutInflater inflater=null;
		   

		   int i=0;
		    
		   public AttendanceListAdapter(Context c,ArrayList<Attendance> d) {
		        context=c;
		        	//data.clear();
		           data=d;
		          
		        
		           /***********  Layout inflator to call external xml layout () ***********/
		            inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        
		   }

		   /******** What is the size of Passed Arraylist Size ************/
		   public int getCount() {
			   
		       return data.size();
		   }

		   public Object getItem(int position) {
		       return position;
		   }

		   public long getItemId(int position) {
		       return position;
		   }
		    
		   /********* Create a holder Class to contain inflated xml file elements *********/
		   

		   /****** Depends upon data size called for each row , Create each ListView row *****/
		   public View getView(int position, View convertView, ViewGroup parent) {
		        
		       View vi = convertView;
		       
		       ViewHolder holder;
		        
		       if(convertView==null){ 
		            
		           /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
		    	   if(GlobalJSONObjects.getInstance().getUser().getRole_short_name(false).equals("Student"))
			       {
		    		   vi = inflater.inflate(R.layout.single_student_session_row, null);
		    		   holder = new ViewHolder();
		    		   holder.tvTotalSessions=(TextView)vi.findViewById(R.id.tvTotalSession);
		    		   //holder.tvTotalSessions.setTextColor(Color.WHITE);
		    		   holder.tvCourseName = (TextView) vi.findViewById(R.id.tvAttendanceTypeName);
			           holder.tvCourseName.setTextColor(Color.WHITE);
			       }
		    	   else
		    	   {
		    		   vi = inflater.inflate(R.layout.course_list_cell_design, null);
		    		   holder = new ViewHolder();
		    		   holder.tvCourseName = (TextView) vi.findViewById(R.id.tvCourseFullName);
			           holder.tvCourseName.setTextColor(Color.WHITE);
		    	   }
		           /****** View Holder Object to contain tabitem.xml file elements ******/

		           
		           
		
		          /************  Set holder with LayoutInflater ************/
		           vi.setTag( holder );
		       }
		       else 
		       {
		           holder=(ViewHolder)vi.getTag();
		       }
		        
		       
		       if(data.size()>0)
		       {
		    	   Log.i("else Data size check","ok");
		           /***** Get each Model object from Arraylist ********/

		           
		    	   
		    	   
		           /************  Set Model values in Holder elements ***********/

		            holder.tvCourseName.setText(data.get(position).getName());
		            
		            try{
		            	
		            	holder.tvTotalSessions.setText(data.get(position).getTotalPresentAbsent());
		            }
		            catch (Exception e) {
						// TODO: handle exception
					}

		              
		            /******** Set Item Click Listner for LayoutInflater for each row *******/

		       }
		       return vi;
		   }

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			
			
			Log.i("MAA", data.get(position).getName());
			
			Intent i;
			
			
			
			if(GlobalJSONObjects.getInstance().getUser().getRole_short_name(false).equals("Student"))
			{
				i=new Intent(context,StudentAttendanceActivity.class);
				i.putExtra("course_position", coursePosition);
				i.putExtra("attendance_position", position);
				i.putExtra("title", data.get(position).getName());
				Log.i("MAA", "opening--------- StudentAttendanceActivity");
				startActivityForResult(i,THREE);
			}
			else
			{
			
				i=new Intent(context,SessionActivity.class);	
				i.putExtra("course_position", coursePosition);
				i.putExtra("attendance_position", position);
				Log.i("MAA", "opening--------- SessionActivity");
				startActivityForResult(i,TWO);
			
			}
			
			
			
			
			
		}

	}
	
	static class ViewHolder{
        
	       public TextView tvCourseName;
	       public TextView tvTotalSessions;

	   }
	

	
}
