package com.rutvik.moodleattendanceapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.rutvik.moodleattendanceapp.R;

public class UserCourseActivity extends AppCompatActivity {

	ListView CourseList;
	RoundedImageView imgProPic;
	TextView tvUserFullName,tvSessions,tvEnrolledCourses;
	//String response = "";
	
	String user_id,user_fullname,user_role_name,user_propic_url,token;
	
	Boolean isInternetPresent = false, flagResponse = false;
	//Course c[];
	public UserCourseActivity CustomListView = null;
	CourseListAdapter adapter;
	Resources res;
	
	//RoundImage roundedImage;
	// Connection detector class
	//ConnectionDetector cd;
	
	SharedPreferences mSharedPreferences;
	
	Toolbar mActionBar;
	
	SwipeRefreshLayout swipeContainer;
	
	//SharedPreferences mSharedPreferences;
	//Editor mEditor;

	
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<Course> courses;
	
	
	Menu menu;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.user_course_menu, menu);
		
		this.menu=menu;
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId())
		{
			case R.id.action_show_settings:
				
				Intent i=new Intent(this,SettingsActivity.class);
				
				startActivity(i);
				
				return true;
				
			
		}
		
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		

		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);
		
		GlobalJSONObjects.getInstance().restoreJSONObjects(mSharedPreferences);
		

		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_user_course);

        SetActionBar();
		
		imgProPic=(RoundedImageView)findViewById(R.id.imgUserProPic);
		CourseList=(ListView)findViewById(R.id.lvCourseList);
		tvUserFullName=(TextView)findViewById(R.id.tvUserFullName);
		tvSessions=(TextView)findViewById(R.id.tvSessions);
		tvEnrolledCourses=(TextView) findViewById(R.id.tvEnrolledCourses);
		
		swipeContainer=(SwipeRefreshLayout) findViewById(R.id.coursesSwipeContainer);
		
		 swipeContainer.setOnRefreshListener(new OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	                
	            	if(mSharedPreferences.contains("tmp_username") && mSharedPreferences.contains("tmp_password"))
	            	{
	            		
		            	if(ServiceHandler.hasActiveInternetConnection(UserCourseActivity.this))
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
											Toast.makeText(UserCourseActivity.this, errObj.getComment(), Toast.LENGTH_SHORT).show();
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
										
										adapter.notifyDataSetChanged();
										
										
										
									}
									
									//progressDialog.dismiss();
									
									swipeContainer.setRefreshing(false);
		
									
								}
							
							}.execute();
							
		            	}
		            	else
		            	{
		            		Toast.makeText(UserCourseActivity.this, "Check Network Connection", Toast.LENGTH_SHORT).show();
		            	}
					
	            	}
	            	
	            	
	            } 
	        });
	        // Configure the refreshing colors
	        swipeContainer.setColorScheme(android.R.color.holo_blue_bright, 
	                android.R.color.holo_green_light, 
	                android.R.color.holo_orange_light, 
	                android.R.color.holo_red_light);

		
		CustomListView = this;
		res = getResources();
	
		 Log.i("get pass ok","ok");
			//User u=getIntent().getParcelableExtra("user");
		 	//Bundle b=getIntent().getExtras();
		 	
		 	
			
			//courses=b.getParcelableArrayList("courses");
			
		 	courses=GlobalJSONObjects.getInstance().getUser().getCourse();
		 	
			//user_propic_url=b.getString("user_propic_url");
			user_propic_url=GlobalJSONObjects.getInstance().getUser().getProfile_pic_url();

			user_id=GlobalJSONObjects.getInstance().getUser().getId();
			token=GlobalJSONObjects.getInstance().getUser().getToken();
			user_fullname=GlobalJSONObjects.getInstance().getUser().getFull_name();
			user_role_name=GlobalJSONObjects.getInstance().getUser().getRole_short_name(false);

			Log.i("get pass aftr","ok");			
			Log.i("moodle",""+ courses.size());
			Log.i("get pass print","ok");
			
			
			
		 
		// user_id=getArguments().getString("user_id");
			
			//user_fullname=getArguments().getString("user_fullname");
			//user_role_name = getArguments().getString("user_role_name");
			
			tvUserFullName.setText(GlobalJSONObjects.getInstance().getUser().getFull_name());//+" ("+GlobalJSONObjects.getInstance().getUser().getRole_short_name(true)+")");
			
			tvEnrolledCourses.setText(tvEnrolledCourses.getText() +""+ GlobalJSONObjects.getInstance().getUser().getCourse().size());
			
			
			if(GlobalJSONObjects.getInstance().getUser().getRole_short_name(false).equals("Student"))
			{
				tvSessions.setText("");
			}
			else
			{
				tvSessions.setText("Sessions: "+GlobalJSONObjects.getInstance().getUser().getSessionsStatus("total"));
				
			}
			//user_propic_url = getArguments().getString("user_propic_url");
			
			 //ImageLoader class instance
	      //  ImageLoader imgLoader = new ImageLoader(getApplicationContext());
	        
	        // whenever you want to load an image from url
	        // call DisplayImage function
	        // url - image url to load
	        // loader - loader image, will be displayed before getting image
	        // image - ImageView 
	       //imgLoader.DisplayImage(user_propic_url, loader, imgProPic);
	       
	       //Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.f1);
           //roundedImage = new RoundImage(bm);
           imgProPic.setImageResource(R.drawable.f1);
			
			
			loadBitmap(imgProPic, GlobalJSONObjects.getInstance().getUser().getProfile_pic_url());
			
	       
	     //  AsyncCallWS task = new AsyncCallWS();
	      // task.execute("");
	       
	       // mPlanetTitles = getResources().getStringArray(R.array.CountryArray);
	        
			Log.i("call course","course frag");
			
		//	CourseList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
			//		android.R.layout.simple_list_item_1, mPlanetTitles));
		 
			/*for(int i=0;i<courses.size();i++)
			{
				list.add(courses.get(i).getFull_name());				
			}
			CourseList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
							android.R.layout.simple_list_item_1, list));*/
			
			adapter = new CourseListAdapter(CustomListView, courses, res);
			
			CourseList.setAdapter(adapter);
			
			CourseList.setOnItemClickListener(adapter);
	}
	
	
	
	
	@Override
	public void onBackPressed() {
		
AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	    
		alertDialogBuilder.setTitle("Logout");
 
		alertDialogBuilder
				.setMessage("Are you sure you want to Logout?")
				.setCancelable(false)
				.setPositiveButton("Logout",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					
								
						GlobalJSONObjects.getInstance().clean();
						
						UserCourseActivity.this.finish();
				
						
					}
					
				
				  })
				  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							
							dialog.cancel();
								
							}
							
						
						  });   
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		 
		alertDialog.show();

		
	}
	
	public void loadBitmap(RoundedImageView imageView,String url) {
	    BitmapWorkerTask task = new BitmapWorkerTask(imageView,url);
	    task.execute();
	}
	
	
	class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
	    private final WeakReference<RoundedImageView> imageViewReference;
	    //private int data = 0;
	    private final String url;

	    public BitmapWorkerTask(RoundedImageView imageView,String url) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	    	this.url=url;
	        imageViewReference = new WeakReference<RoundedImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Void... params) {
	        //data = params[0];
	        return download_Image(url);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final RoundedImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	            	//roundedImage = new RoundImage(bitmap);
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}
	
	
	
	private Bitmap download_Image(String url) {
	    //---------------------------------------------------
	    Bitmap bm = null;
	    try {
	        URL aURL = new URL(url);
	        URLConnection conn = aURL.openConnection();
	        conn.connect();
	        InputStream is = conn.getInputStream();
	        BufferedInputStream bis = new BufferedInputStream(is);
	        bm = BitmapFactory.decodeStream(bis);
	        bis.close();
	        is.close();
	    } catch (IOException e) {
	        Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
	    } 
	    return bm;
	    //---------------------------------------------------
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		
	}
	
	
	public void SetActionBar() {
		mActionBar = (Toolbar) findViewById(R.id.app_bar);

		mActionBar.setTitle(GlobalJSONObjects.getInstance().getUser().getFirst_name());
		mActionBar.setSubtitle(GlobalJSONObjects.getInstance().getUser().getRole_short_name(false));

        setSupportActionBar(mActionBar);

	}
	
	
	
}
