package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;



import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;




import android.os.Parcel;
import android.os.Parcelable;



import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class User extends JSONObject implements Parcelable
{


    private ArrayList<Course> course=new ArrayList<Course>();

    private String id;
    
    private String token;

	private String profile_pic_url;

    private String first_name;

    private String user_name;

    private String role_id;

    private String role_short_name;

    private String last_name;

    private String full_name;
    
    
    
    public User(Parcel p)
    {
    	p.readTypedList(course, Course.CREATOR);
    	id=p.readString();
    	token=p.readString();
    	profile_pic_url=p.readString();
    	first_name=p.readString();
    	user_name=p.readString();
    	role_id=p.readString();
    	role_short_name=p.readString();
    	last_name=p.readString();
    	full_name=p.readString();
    }
    
    public static final Creator<User> CREATOR=new Creator<User>() {

		@Override
		public User createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			// TODO Auto-generated method stub
			return new User[size];
		}
	};
    
    public User(JSONObject obj) throws JSONException
    {

    		id=obj.getString("id");
    		token=obj.getString("token");
    		JSONArray coursesArr=obj.getJSONArray("course");
    		for(int i=0;i<coursesArr.length();i++)
    		{
    			Course a=new Course(coursesArr.getJSONObject(i));
    			course.add(a);
    		}
    		profile_pic_url=obj.getString("profile_pic_url");
    		first_name=obj.getString("first_name").substring(0,1).toUpperCase() + obj.getString("first_name").substring(1);
    		user_name=obj.getString("user_name");
    		role_id=obj.getString("role_id");
    		role_short_name=obj.getString("role_short_name").substring(0,1).toUpperCase() + obj.getString("role_short_name").substring(1);
    		last_name=obj.getString("last_name").substring(0,1).toUpperCase() + obj.getString("last_name").substring(1);
    		full_name=first_name +" "+ last_name;

    }

    public ArrayList<Course> getCourse ()
    {
        return course;
    }

    public void setCourse (ArrayList<Course> course)
    {
        this.course = course;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }
    
    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

    public String getProfile_pic_url ()
    {
        return profile_pic_url;
    }

    public void setProfile_pic_url (String profile_pic_url)
    {
        this.profile_pic_url = profile_pic_url;
    }

    public String getFirst_name ()
    {
        return first_name;
    }

    public void setFirst_name (String first_name)
    {
        this.first_name = first_name;
    }

    public String getUser_name ()
    {
        return user_name;
    }

    public void setUser_name (String user_name)
    {
        this.user_name = user_name;
    }

    public String getRole_id ()
    {
        return role_id;
    }

    public void setRole_id (String role_id)
    {
        this.role_id = role_id;
    }

    public String getRole_short_name (Boolean returnRaw)
    {
    	if(returnRaw)
    	{
    		return role_short_name;
    	}
    	else
    	{
    		if(role_short_name.contains("teacher"))
	    	{
	    		return "Teacher";
	    	}
	    	if(role_short_name.contains("student"))
	    	{
	    		return "Student";
	    	}
	    	return role_short_name;
    		
    	}
    }

    public void setRole_short_name (String role_short_name)
    {
        this.role_short_name = role_short_name;
    }

    public String getLast_name ()
    {
        return last_name;
    }

    public void setLast_name (String last_name)
    {
        this.last_name = last_name;
    }

    public String getFull_name ()
    {
        return full_name;
    }

    public void setFull_name (String full_name)
    {
        this.full_name = full_name;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeTypedList(course);

		dest.writeString(id);
		dest.writeString(token);

		dest.writeString(profile_pic_url);
		dest.writeString(first_name);
		dest.writeString(user_name);
		dest.writeString(role_id);
		dest.writeString(role_short_name);
		dest.writeString(last_name);
		dest.writeString(full_name);
		
	}
	
	public void refreshCourses(JSONObject obj)
	{
		
		
		try {
			JSONArray coursesArr = obj.getJSONArray("course");
			course.clear();
			for(int i=0;i<coursesArr.length();i++)
			{
				Course a=new Course(coursesArr.getJSONObject(i));
				course.add(a);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getSessionsStatus(String type)
	{
		int completed=0;
		int upcomming=0;
		int total=0;
		for (Course c : course) {
			for (Attendance a : c.getAttendance()) {
				for (Sessions s : a.getSessions()) {
					total++;					
					if(s.getLasttaken().equals(null) || s.getLasttaken().isEmpty() || s.getLasttaken().contains("null"))
					{
						upcomming++;
					}
					else
					{
						
						completed++;
					}
				}
			}
		}
		
		if(type.equals("total"))
		{
			return completed+"/"+total;
		}
		if(type.equals("completed"))
		{
			return completed+"";
		}
		if(type.equals("upcomming"))
		{
			return upcomming+"";
		}
		
		return total+"";
		
	}
	
	
	public void setStatusColor(SharedPreferences sp)
	{
		Editor editor=sp.edit();
		for (Course c : course) {
			for (Attendance a : c.getAttendance()) {
				for(Statuses s:a.getStatuses())
				{
					if(!sp.contains(s.getAcronym()))
					{
						editor.putString(s.getAcronym(), "#FF9800");
					}
					
				}
			}
		}
		editor.commit();
	}
	
	public void setStatusColor(SharedPreferences sp,String acronym,String colorHex)
	{
		Editor editor=sp.edit();
		for (Course c : course) {
			for (Attendance a : c.getAttendance()) {
				for(Statuses s:a.getStatuses())
				{
					if(s.getAcronym().equals(acronym))
					{
						editor.putString(acronym, colorHex);
					}
				}
			}
		}
		editor.commit();
		
	}
	
	public ArrayList<String> getAcronymArrayList()
	{
		HashMap<String, String> map=new HashMap<String, String>();
		
		for (Course c : course) {
			for (Attendance a : c.getAttendance()) {
				for(Statuses s:a.getStatuses())
				{
					map.put(s.getAcronym(), s.getAcronym());
				}
			}
		}
		
		ArrayList<String> list=new ArrayList<String>();
		list.addAll(map.values());
		
		return list; 
	}
	
	

}
