package com.rutvik.moodleattendanceapp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.rutvik.moodleattendanceapp.R;

public class SessionActivity extends AppCompatActivity {
	
	Toolbar mActionBar;
	
	ListView sessionList;
	
	SessionListAdapter sessionListAdapter;
	
	ArrayList<Sessions> sessionArrayList;
	
	int coursePosition,attendancePosition;
	
	private SwipeRefreshLayout swipeContainer;
	
 	SharedPreferences mSharedPreferences;
	
	//ProgressDialog progressDialog;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sessions_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId())
		{
		
			case android.R.id.home:
				
				closeActivity();
				
				return true;
		
			case R.id.action_add_session:
				Intent i=new Intent(getApplicationContext(),AddSession.class);
				i.putExtra("AttendanceTypeid", GlobalJSONObjects.getInstance()
						.getUser()
						.getCourse().get(coursePosition).getAttendance()
						.get(attendancePosition).getId());
				startActivityForResult(i,1);
				
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	            try
	            {
	            	refreshSessions();
	            }
	            catch (Exception e) {
					// TODO: handle exception
				}
	        }
	        if (resultCode == RESULT_CANCELED) {
	            
	        }
	    }
		if (requestCode == 2) {
	        if(resultCode == RESULT_OK){
	            try
	            {
	            	Log.i("MAA", "Refreshing Sessions");
	            	refreshSessions();
	            }
	            catch (Exception e) {
					
	            	Log.i("MAA", "Cannot refresh sessions");
				}
	        }
	        if (resultCode == RESULT_CANCELED) {
	            
	        }
	    }
	}

	
	public void refreshSessions()
	{
		
		new AsyncTask<Void, Void, Void>(){
			
			String response="";
			
			Boolean success=false;

			@Override
			protected void onPreExecute() {
				
				
				//progressDialog=null;
				//progressDialog=ProgressDialog.show(SessionActivity.this, "Connecting...", "Please Wait...",true);
				//progressDialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				
				ServiceHandler sh=new ServiceHandler();
				
				response=sh.getSessions(GlobalJSONObjects.getInstance()
						.getUser()
						.getCourse().get(coursePosition).getId(),
						GlobalJSONObjects.getInstance()
						.getUser()
						.getCourse().get(coursePosition).getAttendance()
						.get(attendancePosition).getId());
				
				return null;
				
			}
			
			@Override
			protected void onPostExecute(Void result) {
				
				try {
					Log.i("MAA", response);
					JSONObject obj=new JSONObject(response);							
					GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).refreshSessions(obj);
					//Log.i("MAA",GlobalJSONObjects.getInstance().getUser().getFull_name());
					success=true;
					
				} catch (JSONException e) {

					try {
						ErrorObj errObj=new ErrorObj(response);
						Toast.makeText(SessionActivity.this, errObj.getComment(), Toast.LENGTH_SHORT).show();
					} catch (JSONException e1) {
						Log.e("MAA", "error in processing ERROR JSON");
						e1.printStackTrace();
					}

					Log.e("MAA", "error in processing JSON");
					e.printStackTrace();
					
				}				
				
				if(success)
				{
					
					Log.i("MAA", GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions().get(0).getSessionDate());
					
											
					//sessionArrayList.addAll(GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions());
					
					Log.i("MAA", "size is "+GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions().size()+"");
					
					sessionArrayList.clear();
					
					sessionArrayList.addAll(GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions());
					
					
					sessionListAdapter.notifyDataSetChanged();
					
					
					
				}
				
				//progressDialog.dismiss();
				
				swipeContainer.setRefreshing(false);

				
			}
		
		}.execute();
		
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);
		
		GlobalJSONObjects.getInstance().restoreJSONObjects(mSharedPreferences);
		

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session);

        SetActionBar();
		
		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.sessionSwipeContainer);

		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				
				if(ServiceHandler.hasActiveInternetConnection(SessionActivity.this))
				{
				
					refreshSessions();
				
				}
				
			}
		});
		
		swipeContainer.setColorScheme(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);

		
		sessionList=(ListView) findViewById(R.id.lvSessionList);
		
		coursePosition=getIntent().getIntExtra("course_position",0);
		
		attendancePosition=getIntent().getIntExtra("attendance_position",0);
		
		sessionArrayList=GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getSessions();
		
		
		
		sessionListAdapter=new SessionListAdapter(this, sessionArrayList);
		
		sessionList.setAdapter(sessionListAdapter);
		
		sessionList.setOnItemClickListener(sessionListAdapter);
		
		sessionList.setOnItemLongClickListener(sessionListAdapter);
		
		TextView emptyText = (TextView)findViewById(android.R.id.empty);
		
		sessionList.setEmptyView(emptyText);
		
		
		
		
	}
	
	
	public void deleteSession(Context context,String sessionId)
	{
		final Context c=context;
		
		final String s=sessionId;
		
		new AsyncTask<Void, Void, Void>()
		{
			
			ProgressDialog progressDialog;
			
			String response="";
			
			@Override
			protected void onPreExecute() {
				progressDialog=null;
				progressDialog=ProgressDialog.show(c, "Please Wait...", "Deleting Session...",true);
				progressDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				
				ServiceHandler sh=new ServiceHandler();
				response=sh.deleteSession(GlobalSettings.getInstance().getmSharedPreferences().getString("tmp_username", "null"), GlobalSettings.getInstance().getmSharedPreferences().getString("tmp_password", "null"), s);
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				
				try
				{
					JSONObject obj=new JSONObject(response);
					Response r=new Response(obj.getJSONObject("response"));
					Toast.makeText(c, r.getComment(), 3000).show();
					refreshSessions();
				}
				catch (JSONException e) {
					
					try
					{
						ErrorObj err=new ErrorObj(response);
						Toast.makeText(c, err.getComment(), 3000).show();
					}
					catch (JSONException e2) {
						
						
						
					}
					
				}
				
				progressDialog.dismiss();
				
			}
			
		}.execute();
	}
	
	
	
	public void SetActionBar() {
		mActionBar = (Toolbar) findViewById(R.id.app_bar);
		//mActionBar.setDisplayShowTitleEnabled(true);

		mActionBar.setTitle("Sessions");
		setSupportActionBar(mActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	
	
	
	
	
	class SessionListAdapter extends BaseAdapter implements OnItemClickListener,OnItemLongClickListener {

		/*********** Declare Used Variables *********/
		Context context;
		   private ArrayList<Sessions> sessionArrayList;
		   private LayoutInflater inflater=null;
		   

		   int i=0;
		    
		   public SessionListAdapter(Context c,ArrayList<Sessions> d) {
		        context=c;
		        sessionArrayList=d;
		          
		        
		           /***********  Layout inflator to call external xml layout () ***********/
		            inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        
		   }

		   /******** What is the size of Passed Arraylist Size ************/
		   public int getCount() {
			   
		       return sessionArrayList.size();
		   }

		   public Object getItem(int position) {
		       return sessionArrayList.get(position);
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
		           vi = inflater.inflate(R.layout.single_session_row, null);
		            
		           /****** View Holder Object to contain tabitem.xml file elements ******/

		           holder = new ViewHolder();
		           holder.tvSessionDate = (TextView) vi.findViewById(R.id.tvSessionDate);
		           holder.tvDescription = (TextView) vi.findViewById(R.id.tvSessionDescription);
		           holder.ivSessionStatus=(ImageView) vi.findViewById(R.id.ivSessionStatus);
		           
		           
		           //holder.tvDescription.setTextColor(Color.WHITE);
		
		          /************  Set holder with LayoutInflater ************/
		           vi.setTag( holder );
		       }
		       else 
		       {
		           holder=(ViewHolder)vi.getTag();
		       }
		        
		       
		       if(sessionArrayList.size()>0)
		       {
		    	   Log.i("else Data size check","ok");
		           /***** Get each Model object from Arraylist ********/

		           
		    	   
		    	   
		           /************  Set Model values in Holder elements ***********/

		            holder.tvSessionDate.setText(sessionArrayList.get(position).getSessionDate());
		            holder.tvDescription.setText(sessionArrayList.get(position).getDescription());
		            
		            
		            
		           if(sessionArrayList.get(position).getLasttaken().equals(null) || sessionArrayList.get(position).getLasttaken().isEmpty() || sessionArrayList.get(position).getLasttaken().contains("null"))
		           {
		        	   
		        	   holder.ivSessionStatus.setImageResource(R.drawable.ocheckmark_128);
		           }
		           else{
		        	   
		        	   holder.ivSessionStatus.setImageResource(R.drawable.checkmark_128);
		           
		           }
	                
		            /******** Set Item Click Listner for LayoutInflater for each row *******/

		       }
		       return vi;
		   }

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			
			
			Intent i=new Intent(context,FillAttendanceActivity.class);
			i.putExtra("course_position", coursePosition);
			i.putExtra("attendance_position", attendancePosition);
			i.putExtra("session_id", sessionArrayList.get(position).getId());
			i.putExtra("title", sessionArrayList.get(position).getSessionDate());
			i.putExtra("sub_title", sessionArrayList.get(position).getDescription());
			
			
			startActivityForResult(i,2);
			
			
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			
			final int mArg2=arg2;
			
			AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
			
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.select_dialog_item);
            
            arrayAdapter.add("Delete Session");

            
            builderSingle.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                        	
                            String strName = arrayAdapter.getItem(position);
                            
                            if(position==0)
                            {
                            	
                            	AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
                				dlgAlert.setMessage("Delete Session: "+sessionArrayList.get(mArg2).getSessionDate());
                				dlgAlert.setTitle("Alert!");
                				dlgAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                					
                					@Override
                					public void onClick(DialogInterface dialog, int which) {
                						
                						deleteSession(context, sessionArrayList.get(mArg2).getId());
                						
                					}
                				});
                				dlgAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                					
                					@Override
                					public void onClick(DialogInterface dialog, int which) {
                						
                						dialog.dismiss();
                						
                					}
                				});
                				
                				dlgAlert.setCancelable(true);
                				dlgAlert.create().show();
                				
                				
                		
                            	
                            }
                            
                            Toast.makeText(context, strName, Toast.LENGTH_LONG).show();
                        }
                    });
            builderSingle.show();
		
            
            return true;
			
		}
	
		
	
	
	}
	
	static class ViewHolder{
        
	       public TextView tvSessionDate;
	       public TextView tvDescription;
	       public ImageView ivSessionStatus;

	   }
	
	
	@Override
	public void onBackPressed() {
		closeActivity();
	}
	
	public void closeActivity()
	{
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("course_position", coursePosition);
		setResult(RESULT_OK,returnIntent);
		this.finish();
		
	}
	
	

	
}
