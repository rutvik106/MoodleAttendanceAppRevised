package com.rutvik.moodleattendanceapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.rutvik.moodleattendanceapp.R;

public class MainActivity extends AppCompatActivity {

	Toolbar mActionBar;
	EditText etUserName, etPassword;
	Button btnUserLogin;
	String uname = "", pwd = "";
	
	ProgressDialog progressDialog;

	SharedPreferences mSharedPreferences;
	Editor mEditor;

	CheckBox cbRememberMe;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_main);


        SetActionBar();

		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);
		
		GlobalSettings.getInstance().setmSharedPreferences(mSharedPreferences);

		etUserName = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);

		cbRememberMe = (CheckBox) findViewById(R.id.cbRememberMe);

		if ((mSharedPreferences.contains("Username") && mSharedPreferences
				.contains("Password"))) {
			Log.i("data in pref", "ok");

			etUserName.setText(mSharedPreferences.getString("Username", ""));
			etPassword.setText(mSharedPreferences.getString("Password", ""));
			cbRememberMe.setChecked(true);

		}


		btnUserLogin = (Button) findViewById(R.id.btnLogin);

		btnUserLogin.setOnClickListener(new OnClickListener() {
			
			

			@Override
			public void onClick(View view) { // TODO Auto-generated method stub
				
				if(mSharedPreferences.contains("host"))
				{
					GlobalSettings.getInstance().setHost(mSharedPreferences.getString("host", "moodle"));
					UserLogin(view);
				}
				else
				{
					AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(view.getContext());
					dlgAlert.setMessage("Moodle Host not set. Please set a valid Host first.");
					dlgAlert.setTitle("Alert");
					dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.dismiss();
							
						}
					});
					dlgAlert.setCancelable(false);
					dlgAlert.create().show();
				}
				
				
			}
		});
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu) ;
		
		
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId()==R.id.action_settings_and_info)
		{
			Intent i=new Intent(this,InfoSettingsActivity.class);
			startActivity(i);
		}
		
		return super.onOptionsItemSelected(item);
	}

	public void SetActionBar() {
		mActionBar =(Toolbar) findViewById(R.id.app_bar);
		//mActionBar.setsetDisplayShowTitleEnabled(true);

		mActionBar.setTitle("Moodle Attendance");

        setSupportActionBar(mActionBar);
	}

	public void UserLogin(View v) {

		if (!isEmpty(etUserName) && !isEmpty(etPassword)) {

			if (ServiceHandler.hasActiveInternetConnection(v.getContext())) {

				uname = etUserName.getText().toString();
				pwd = etPassword.getText().toString();

				
				
				
					makeServiceCall(v.getContext(),uname,pwd);


			} else {

				openAlert("No Internet Connection",
						"You don't have internet connection.");
			}


			Log.i("orgid save", "ok");

		} else {
			if (isEmpty(etUserName)) {
				etUserName.setHint("Enter Username");
				openAlert("Message", "Please Enter Username");
			} else if (isEmpty(etPassword)) {
				etPassword.setHint("Enter Password");
				openAlert("Message", "Please Enter Password");
			}
		}
	}

	private boolean isEmpty(EditText etText) {
		return etText.getText().toString().trim().length() == 0;
	}

	private void openAlert(String mTitle, String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MainActivity.this);

		alertDialogBuilder.setTitle(mTitle);
		alertDialogBuilder.setMessage(msg);
		// set positive button: Yes message
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();
	}

	

	
	public void makeServiceCall(Context context,String UserName,String password)
	{
		final Context c=context;
		
		final String u=UserName;
		final String p=password;
		
		new AsyncTask<Void, Void, Void>(){
			
			String response="";
			
			Boolean success=false;

			@Override
			protected void onPreExecute() {
				progressDialog=null;
				progressDialog=ProgressDialog.show(c, "Connecting...", "Please Wait...",true);
				progressDialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				ServiceHandler sh=new ServiceHandler();
				try {
					response=sh.login(u, p);
					GlobalJSONObjects.getInstance().saveJSONObjects(response);
					MoodleAttendanceApp.getInstance().trackEvent("Login", "Login Click", "User Clicked Login");
				} catch (JSONException e) {
					MoodleAttendanceApp.getInstance().trackException(e);
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				
				try {
					//Log.i("MAA", response);
					JSONObject obj=new JSONObject(response).getJSONObject("user");							
					GlobalJSONObjects.getInstance().setUser(new User(obj));
					Log.i("MAA",GlobalJSONObjects.getInstance().getUser().getFull_name());
					success=true;
					
				} catch (JSONException e) {

					try {
						MoodleAttendanceApp.getInstance().trackException(e);
						ErrorObj errObj=new ErrorObj(response);
						Toast.makeText(c, errObj.getComment(), Toast.LENGTH_SHORT).show();
					} catch (JSONException e1) {
						MoodleAttendanceApp.getInstance().trackException(e1);
						Log.e("MAA", "error in processing ERROR JSON   "+response);
						e1.printStackTrace();
					}

					Log.e("MAA", "error in processing JSON  "+response);
					e.printStackTrace();
					
				} catch (NullPointerException e){
					
				}
				
				if(success)
				{
					
					mEditor = mSharedPreferences.edit();

					mEditor.putString("tmp_username", uname);
					mEditor.putString("tmp_password", pwd);
					
					mEditor.commit();
					
					if (cbRememberMe.isChecked()) {

						mEditor = mSharedPreferences.edit();

						mEditor.putString("Username", uname);
						mEditor.putString("Password", pwd);
						
						mEditor.commit();
					}
					else
					{
						mEditor = mSharedPreferences.edit();

						mEditor.remove("Username");
						mEditor.remove("Password");
						
						mEditor.commit();
					}
					
					GlobalJSONObjects.getInstance().getUser().setStatusColor(mSharedPreferences);
					
					progressDialog.dismiss();
					
					Intent i = new Intent(getApplicationContext(),UserCourseActivity.class);
					
					startActivity(i);
					
				}
				
				progressDialog.dismiss();
				
			}
		
		}.execute();
	}
	
}
