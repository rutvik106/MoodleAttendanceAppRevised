package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Course extends JSONObject implements Parcelable
{
	
	private String id;

    private ArrayList<Attendance> attendance=new ArrayList<Attendance>();

    private String short_name;

    private ArrayList<EnrolledStudents> enrolledStudents=new ArrayList<EnrolledStudents>();

    private String full_name;
    
    
    public Course()
    {
    	
    }
    
    public Course(Parcel p)
    {
    	id=p.readString();
    	p.readTypedList(attendance, Attendance.CREATOR);
    	short_name=p.readString();
    	p.readTypedList(enrolledStudents, EnrolledStudents.CREATOR);
    	full_name=p.readString();
    }
    
    public static final Creator<Course> CREATOR=new Creator<Course>() {

		@Override
		public Course createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Course(source);
		}

		@Override
		public Course[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Course[size];
		}
	};
    
    public Course(JSONObject obj) throws JSONException
    {

    		id=obj.getString("id");
    		short_name=obj.getString("short_name");
    		full_name=obj.getString("full_name");
    		try
    		{

    			JSONArray attendanceArr=obj.getJSONArray("attendance");
    			Log.i("MAA", "Attendance Size in JSON is: "+attendanceArr.length());
	    		for(int i=0;i<attendanceArr.length();i++)
	    		{
	    			Attendance a=new Attendance(attendanceArr.getJSONObject(i));
	    			attendance.add(a);

	    		}
    		}
    		catch(Exception e)
    		{
    			
    		}
    		
    		try
    		{

    			JSONArray enrolledStudentsArr=obj.getJSONArray("enrolled_students");
	    		for(int i=0;i<enrolledStudentsArr.length();i++)
	    		{
	    			EnrolledStudents a=new EnrolledStudents(enrolledStudentsArr.getJSONObject(i));
	    			enrolledStudents.add(a);

	    		}
    		}
    		catch(Exception e)
    		{
    			
    		}
    		
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public ArrayList<Attendance> getAttendance ()
    {
    	Log.i("MAA", "Size of Attendance is: "+attendance.size());
        return attendance;
    }

    public void setAttendance (ArrayList<Attendance> attendance)
    {
        this.attendance = attendance;
    }

    public String getShort_name ()
    {
        return short_name;
    }

    public void setShort_name (String short_name)
    {
        this.short_name = short_name;
    }

    public ArrayList<EnrolledStudents> getEnrolledStudents ()
    {
        return enrolledStudents;
    }

    public void setEnrolled_students (ArrayList<EnrolledStudents> enrolledStudents)
    {
        this.enrolledStudents = enrolledStudents;
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
		
		dest.writeString(id);
		dest.writeTypedList(attendance);
		dest.writeString(short_name);
		dest.writeTypedList(enrolledStudents);
		dest.writeString(full_name);
		
	}
	
	
	public void refreshAttendance(JSONObject obj)
	{
		
		try {
			JSONArray attendanceArr = obj.getJSONArray("attendance");
			attendance.clear();
			for(int i=0;i<attendanceArr.length();i++)
			{
				Attendance a=new Attendance(attendanceArr.getJSONObject(i));
				attendance.add(a);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

}
