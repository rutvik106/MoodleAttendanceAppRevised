package com.rutvik.moodleattendanceapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GlobalSettings {
	
	private static GlobalSettings instance=null;
	
	private String host;
	
	private SharedPreferences mSharedPreferences;
	
	private Boolean useIdAsPrefix,showStudentFullName,sortStudentByName;
	
	public synchronized static GlobalSettings getInstance()
	{
		
		if(instance==null)
		{
			instance=new GlobalSettings();
		}
		
		return instance;
		
	}
	
	public void useIdAsPrefix(Boolean flag)
	{
		useIdAsPrefix=flag;
		if(mSharedPreferences!=null)
		{
			Editor e=mSharedPreferences.edit();
			e.putBoolean("useIdAsPrefix", useIdAsPrefix);
			e.commit();
		}
	}
	
	public void showStudentFullName(Boolean flag)
	{
		showStudentFullName=flag;
		if(mSharedPreferences!=null)
		{
			Editor e=mSharedPreferences.edit();
			e.putBoolean("showStudentFullName", showStudentFullName);
			e.commit();
		}
	}
	
	public void sortStudentByName(Boolean flag)
	{
		sortStudentByName=flag;
		if(mSharedPreferences!=null)
		{
			Editor e=mSharedPreferences.edit();
			e.putBoolean("sortStudentByName", sortStudentByName);
			e.commit();
		}
	}
	
	
	public Boolean showStudentFullName()
	{
		return showStudentFullName;
	}
	
	
	public Boolean useIdAsPrefix()
	{
		return useIdAsPrefix;
	}
	
	public Boolean sortStudentByName(){
		return sortStudentByName;
	}
	
	public String getStatusColor(SharedPreferences sp,String acronym)
	{
		return sp.getString(acronym, "#FF9800");
	}


	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host=host;
	}

	public void setHost(SharedPreferences sp,String host) {
		
		
			Editor editor=sp.edit();
			editor.putString("host", host);
			editor.commit();
			
			
		
	}


	public SharedPreferences getmSharedPreferences() {
		return mSharedPreferences;
	}


	public void setmSharedPreferences(SharedPreferences mSharedPreferences) {
		this.mSharedPreferences = mSharedPreferences;
		useIdAsPrefix=mSharedPreferences.getBoolean("useIdAsPrefix", false);
		showStudentFullName=mSharedPreferences.getBoolean("showStudentFullName", false);
		sortStudentByName=mSharedPreferences.getBoolean("sortStudentByName", false);
	}
	
	
	

}
