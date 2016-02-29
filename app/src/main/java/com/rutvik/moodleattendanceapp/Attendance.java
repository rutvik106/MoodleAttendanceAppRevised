package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Attendance extends JSONObject implements Parcelable
{
    private String id;   

    private ArrayList<Sessions> sessions=new ArrayList<Sessions>();
    
    private ArrayList<StudentSession> studentSessions=new ArrayList<StudentSession>();

    private String name;

    private String grade;
    
    
    private ArrayList<Statuses> statuses=new ArrayList<Statuses>();

    
    public Attendance(Parcel p)
    {
    	id=p.readString();
    	
    	p.readTypedList(sessions,Sessions.CREATOR);
    	
    	name=p.readString();
    	grade=p.readString();
    	
    	p.readTypedList(statuses,Statuses.CREATOR);

    }
    
    public static final Creator<Attendance> CREATOR = new Creator<Attendance>() {

		@Override
		public Attendance createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Attendance(source);
		}

		@Override
		public Attendance[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Attendance[size];
		}
	};
	


    
    public Attendance(JSONObject obj) throws JSONException
    {

    		id=obj.getString("id");
    		name=obj.getString("name");
    		grade=obj.getString("grade");
    		
    		Log.i("moodle", "OKOKOK");
    		
    		JSONArray sessionsArr;
    		

    		
    		sessionsArr=obj.getJSONArray("sessions");
    		

    		
    		
    		JSONArray statusesArr=obj.getJSONArray("statuses");
    		
    		for(int i=0;i<statusesArr.length();i++)
    		{
    			Statuses s=new Statuses(statusesArr.getJSONObject(i));
    			statuses.add(s);
    		}
    		
    		
    		//sessions=new Sessions[sessionsArr.length()];
    		
    		//statuses=new Statuses[statusesArr.length()];
    		if(sessionsArr.length()>0)
    		{
	    		try
	    		{
		    		for(int i=0;i<sessionsArr.length();i++)
		    		{
		    			Sessions s=new Sessions(sessionsArr.getJSONObject(i));
		    			sessions.add(s);
		    		}
		    		
		    		
		    		
	    		}
	    		catch (Exception e) {
					
	    			for(int i=0;i<sessionsArr.length();i++)
		    		{
		    			StudentSession s=new StudentSession(sessionsArr.getJSONObject(i));
		    			studentSessions.add(s);
		    		}
	    			
				}
    		}
    		else
    		{
    			//sessions.add(new Sessions());
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
    
    public ArrayList<Sessions> sortSessions(ArrayList<Sessions> sessions)
	{
		ArrayList<Sessions> tmpLst=new ArrayList<Sessions>();
		
		List<Long> list = new ArrayList<Long>();

		
		for(Sessions s:sessions)
		{
			list.add(s.getSessdate());
		}
		
		Collections.sort(list);
		
		for(int i=list.size()-1;i>=0;i--)
		{
			long s=list.get(i);
			tmpLst.add(getSessionBySessdate(s));
		}
		
		return tmpLst;
		
	}
    
    public ArrayList<StudentSession> sorStudenttSessions(ArrayList<StudentSession> studentSessions)
	{
		ArrayList<StudentSession> tmpLst=new ArrayList<StudentSession>();
		
		List<Long> list = new ArrayList<Long>();

		
		for(StudentSession s:studentSessions)
		{
			list.add(s.getSession_date());
		}
		
		Collections.sort(list);
		
		for(int i=list.size()-1;i>=0;i--)
		{
			long s=list.get(i);
			tmpLst.add(getStudentSessionBySessdate(s));
		}
		
		return tmpLst;
		
	}

    public ArrayList<Sessions> getSessions ()
    {
    	
    	
    	
        return sortSessions(sessions);
    }

    public void setSessions (ArrayList<Sessions> sessions)
    {
        this.sessions = sessions;
    }
    
    public void refreshSessions (JSONObject sessionObj)
    {
    	
		try {
			
			
			
			JSONArray sessionsArr = sessionObj.getJSONArray("sessions");
			
			sessions.clear();
			
			for(int i=0;i<sessionsArr.length();i++)
			{
				Log.i("MAA", "adding sessions "+i);
				Sessions s=new Sessions(sessionsArr.getJSONObject(i));
				sessions.add(s);
			}
			
			Log.i("MAA", "from attendance class size is "+sessions.size());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getGrade ()
    {
        return grade;
    }

    public void setGrade (String grade)
    {
        this.grade = grade;
    }

    public ArrayList<Statuses> getStatuses ()
    {
        return statuses;
    }

    public void setStatuses (ArrayList<Statuses> statuses)
    {
        this.statuses = statuses;
    }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(id);
		dest.writeTypedList(sessions);
		dest.writeString(name);
		dest.writeString(grade);
		dest.writeTypedList(statuses);
		
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public String setStatusPresent()
	{
		for(Statuses s:statuses)
		{
			if(s.getAcronym().equals("P") || s.getDescription().equals("Present"))
			{
				return s.getId();
			}
		}
		return null;
	}
	
	
	public String setStatusAbsent()
	{
		for(Statuses s:statuses)
		{
			if(s.getAcronym().equals("A") || s.getDescription().equals("Absent"))
			{
				return s.getId();
			}
		}
		return null;
	}
	
	
	public String getStatusSet()
	{
		String set="";
		
		for(Statuses s:statuses)
		{
			set=set+s.getId()+",";
		}
		set=set.substring(0, set.length()-1);
		
		return set;
	}
	
	
	public String getAcronymId(String acronym)
	{
		
		for(Statuses s:statuses)
		{
			if(acronym.equals(s.getAcronym()))
			{
				return s.getId();
			}
		}
		
		
		return "0";
	}

	public ArrayList<StudentSession> getStudentSessions() {
		return sorStudenttSessions(studentSessions);
	}

	public void setStudentSessions(ArrayList<StudentSession> studentSessions) {
		this.studentSessions = studentSessions;
	}
	
	public Sessions getSessionBySessdate(long date)
	{
		for(Sessions s:sessions)
		{
			if(s.getSessdate()==date)
			{
				return s;
			}
		}
		return null;
	}
	
	public StudentSession getStudentSessionBySessdate(long date)
	{
		for(StudentSession s:studentSessions)
		{
			if(s.getSession_date()==date)
			{
				return s;
			}
		}
		return null;
	}
	
	
	
	public String getTotalPresentAbsent()
	{
		int present=0,absent=0;
		for(StudentSession s:studentSessions)
		{
			
			if(s.getAcronym().equals("P"))
			{
				present++;
			}
			if(s.getAcronym().equals("A"))
			{
				absent++;
			}
			
		}
		return "Present: "+present+" Absent: "+absent;
	}
	
	
	
	
}
