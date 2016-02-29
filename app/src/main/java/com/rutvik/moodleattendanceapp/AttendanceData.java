package com.rutvik.moodleattendanceapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.JsonReader;

public class AttendanceData
{
    private TakenBy takenBy;

    private String id;

    private String user_name;

    private String first_name;

    private String acronym;

    private String description;

    private String last_name;

    private String remarks;

    private String time_taken;

    private String status_set;
    
    private String status_id;
    
    private ArrayList<AttendanceData> sortedAttendanceData;
    
    public AttendanceData(JSONObject obj) throws JSONException
    {

	    	id=obj.getString("id");
	    	user_name=obj.getString("user_name");
	    	first_name=obj.getString("first_name");
	    	acronym=obj.getString("acronym");
	    	description=obj.getString("description");
	    	last_name=obj.getString("last_name");
	    	remarks=obj.getString("remarks");
	    	time_taken=obj.getString("time_taken");
	    	status_set=obj.getString("status_set");
	    	takenBy=new TakenBy(obj.getJSONObject("taken_by"));
	    	setStatus_id(obj.getString("status_id"));

    	
	}
    
    public AttendanceData(String id,String sid, String acronym, String remark, String firstName, String lastName)
    {
    	this.id=id;
    	this.status_id=sid;
    	this.acronym=acronym;
    	this.remarks=remark;
    	this.first_name=firstName;
    	this.last_name=lastName;
    }
    
    

    public TakenBy getTaken_by ()
    {
        return takenBy;
    }

    public void setTaken_by (TakenBy takenBy)
    {
        this.takenBy = takenBy;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getUser_name ()
    {
        return user_name;
    }

    public void setUser_name (String user_name)
    {
        this.user_name = user_name;
    }

    public String getFirst_name ()
    {
        return first_name;
    }

    public void setFirst_name (String first_name)
    {
        this.first_name = first_name;
    }

    public String getAcronym ()
    {
        return acronym;
    }

    public void setAcronym (String acronym)
    {
        this.acronym = acronym;
    }

    public String getDescription ()
    {
    	if(description==null)
    	{
    		return "none";
    	}
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

    public String getRemarks ()
    {
    	if(remarks==null)
    	{
    		return "none";
    	}
        return remarks;
    }

    public void setRemarks (String remarks)
    {
        this.remarks = remarks;
    }

    public String getTime_taken (Boolean raw)
    {
    	if(time_taken==null)
    	{
    		return "none";
    	}
    	if(raw)
    	{
    		return time_taken;
    	}
    	else
    	{
    		long dv = Long.valueOf(time_taken)*1000;// its need to be in milisecond
    		Date df = new Date(dv);
    		String vv = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(df);
    		return vv; 
    	}
    }
    
    

    public void setTime_taken (String time_taken)
    {
        this.time_taken = time_taken;
    }

    public String getStatus_set ()
    {
        return status_set;
    }

    public void setStatus_set (String status_set)
    {
        this.status_set = status_set;
    }

    
    public class TakenBy
    {
        private String id;

        private String user_name;

        private String first_name;

        private String last_name;
        
        TakenBy(JSONObject obj) throws JSONException
        {
        	try {
				id=obj.getString("id");
				user_name=obj.getString("user_name");
	        	first_name=obj.getString("first_name");
	        	last_name=obj.getString("last_name");
			} catch (JSONException e) {
				throw e;
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

        public String getUser_name ()
        {
            return user_name;
        }

        public void setUser_name (String user_name)
        {
            this.user_name = user_name;
        }

        public String getFirst_name ()
        {
            return first_name;
        }

        public void setFirst_name (String first_name)
        {
            this.first_name = first_name;
        }

        public String getLast_name ()
        {
            return last_name;
        }

        public void setLast_name (String last_name)
        {
            this.last_name = last_name;
        }
        
        

    }
    
    
    
    public static String toJSON(ArrayList<AttendanceData> data)
	{
		JSONArray jArr=new JSONArray();
		
		for(AttendanceData a:data)
		{
			JSONObject j=new JSONObject();
			try
			{
				j.put("i", a.getId());
				j.put("s", a.getStatus_id());
				j.put("r", a.getRemarks());
				
				jArr.put(j);
				
			}
			catch (JSONException e) {
				// TODO: handle exception
			}
		}
		
		return jArr.toString();
	}

	public String getStatus_id() {
		return status_id;
	}

	public void setStatus_id(String status_id) {
		this.status_id = status_id;
	}
    
}