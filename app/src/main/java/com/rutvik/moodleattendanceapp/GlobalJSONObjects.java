package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GlobalJSONObjects {
	
	private User user=null;
	
	private HashMap<String, ArrayList<PostAttendanceData>> cloneAttendanceDataMap=new HashMap<>();
	
	private static GlobalJSONObjects instance=null;
	
	public synchronized static GlobalJSONObjects getInstance()
	{
		
		if(instance==null)
		{
			instance=new GlobalJSONObjects();
		}
		
		return instance;
		
	}

	public void setUser(User user)
	{
		this.user=user;
	}
	
	public User getUser()
	{
		return this.user;
	}
	
	public void clean()
	{
		user=null;
		Editor e=GlobalSettings.getInstance().getmSharedPreferences().edit();
		e.remove("tmp_username");
		e.remove("tmp_password");
		e.commit();
	}
	
	public void addToAttendanceDataMap(String sessionId,ArrayList<PostAttendanceData> attendanceData)
	{
		cloneAttendanceDataMap.put(sessionId, attendanceData);
	}
	
	public void restoreJSONObjects(SharedPreferences mSharedPreferences)
	{
		if(user==null)
		{
			GlobalSettings.getInstance().setmSharedPreferences(mSharedPreferences);
			JSONObject obj;
			try {
				obj = new JSONObject(mSharedPreferences.getString("response", "")).getJSONObject("user");
				GlobalJSONObjects.getInstance().setUser(new User(obj));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
	public void saveJSONObjects(String responseData)
	{
		Editor e=GlobalSettings.getInstance().getmSharedPreferences().edit();
		e.putString("response", responseData);
		e.commit();
	}
	
	
	
}
