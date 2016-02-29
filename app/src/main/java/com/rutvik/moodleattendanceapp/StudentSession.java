package com.rutvik.moodleattendanceapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentSession {
	
	private String timetaken;

    private String desc;

    private String acronym;

    private String lasttaken;

    private String timemodified;

    private String remarks;

    private String statusid;

    private String id;

    private long session_date;

    private String first_name;

    private String duration;

    private String description;

    private String last_name;

    private String lasttakenby;
    
    StudentSession(JSONObject j) throws JSONException
    {
    	  timetaken=j.getString("timetaken");

		  desc=j.getString("desc");
		
		  acronym=j.getString("acronym");
		
		  lasttaken=j.getString("lasttaken");
		
		  timemodified=j.getString("timemodified");
		
		  remarks=j.getString("remarks");
		
		  statusid=j.getString("statusid");
		
		  id=j.getString("id");
		
		  session_date=j.getLong("session_date");
		
		  first_name=j.getString("first_name");
		
		  duration=j.getString("duration");
		
		  description=j.getString("description");
		
		  last_name=j.getString("last_name");
		
          lasttakenby=j.getString("lasttakenby");
    }

    public String getTimetaken ()
    {
        return timetaken;
    }

    public void setTimetaken (String timetaken)
    {
        this.timetaken = timetaken;
    }

    public String getDesc ()
    {
        return desc;
    }

    public void setDesc (String desc)
    {
        this.desc = desc;
    }

    public String getAcronym ()
    {
        return acronym;
    }

    public void setAcronym (String acronym)
    {
        this.acronym = acronym;
    }

    public String getLasttaken ()
    {
        return lasttaken;
    }

    public void setLasttaken (String lasttaken)
    {
        this.lasttaken = lasttaken;
    }

    public String getTimemodified ()
    {
        return timemodified;
    }

    public void setTimemodified (String timemodified)
    {
        this.timemodified = timemodified;
    }

    public String getRemarks ()
    {
        return remarks;
    }

    public void setRemarks (String remarks)
    {
        this.remarks = remarks;
    }

    public String getStatusid ()
    {
        return statusid;
    }

    public void setStatusid (String statusid)
    {
        this.statusid = statusid;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public long getSession_date ()
    {
        return session_date;
    }

    public void setSession_date (long session_date)
    {
        this.session_date = session_date;
    }

    public String getFirst_name ()
    {
        return first_name;
    }

    public void setFirst_name (String first_name)
    {
        this.first_name = first_name;
    }

    public String getDuration ()
    {
        return duration;
    }

    public void setDuration (String duration)
    {
        this.duration = duration;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getLast_name ()
    {
        return last_name;
    }

    public void setLast_name (String last_name)
    {
        this.last_name = last_name;
    }

    public String getLasttakenby ()
    {
        return lasttakenby;
    }

    public void setLasttakenby (String lasttakenby)
    {
        this.lasttakenby = lasttakenby;
    }
    
    public String getSessionDate()
	{
		long dv = Long.valueOf(session_date)*1000;// its need to be in milisecond
		Date df = new Date(dv);
		String vv = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(df);
		return vv;
	}

}
