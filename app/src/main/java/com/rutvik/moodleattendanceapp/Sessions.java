package com.rutvik.moodleattendanceapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Sessions extends JSONObject implements Parcelable
{
    private String id;

    private String duration;

    private String studentscanmark;

    private String lasttaken;

    private String description;

    private String descriptionformat;

    private String timemodified;

    private String lasttakenby;

    private String groupid;

    private long sessdate;
    
    public Sessions(Parcel p)
    {
    	id=p.readString();
    	duration=p.readString();
    	studentscanmark=p.readString();
    	lasttaken=p.readString();
    	description=p.readString();
    	descriptionformat=p.readString();
    	timemodified=p.readString();
    	lasttakenby=p.readString();
    	groupid=p.readString();
    	sessdate=p.readLong();
    }
    
    public static final Creator<Sessions> CREATOR = new Creator<Sessions>() {

		@Override
		public Sessions createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Sessions(source);
		}

		@Override
		public Sessions[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Sessions[size];
		}
	};
    
    public Sessions(JSONObject obj) throws JSONException
    {

    		id=obj.getString("id");
    		duration=obj.getString("duration");
    		studentscanmark=obj.getString("studentscanmark");
    		lasttaken=obj.getString("lasttaken");
    		description=obj.getString("description");
    		descriptionformat=obj.getString("descriptionformat");
    		timemodified=obj.getString("timemodified");
    		lasttakenby=obj.getString("lasttakenby");
    		groupid=obj.getString("groupid");
    		sessdate=obj.getLong("sessdate");

    }
    
    public Sessions() {

    	
    	id="";
		duration="";
		studentscanmark="";
		lasttaken="";
		description="";
		descriptionformat="";
		timemodified="";
		lasttakenby="";
		groupid="";
		sessdate=0;
	}
    
    public Sessions emptySession()
    {
    	Sessions s=new Sessions();
    	
    	id="";
		duration="";
		studentscanmark="";
		lasttaken="";
		description="";
		descriptionformat="";
		timemodified="";
		lasttakenby="";
		groupid="";
		sessdate=0;
    	
    	return s;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getDuration ()
    {
        return duration;
    }

    public void setDuration (String duration)
    {
        this.duration = duration;
    }

    public String getStudentscanmark ()
    {
        return studentscanmark;
    }

    public void setStudentscanmark (String studentscanmark)
    {
        this.studentscanmark = studentscanmark;
    }

    public String getLasttaken ()
    {
        return lasttaken;
    }

    public void setLasttaken (String lasttaken)
    {
        this.lasttaken = lasttaken;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getDescriptionformat ()
    {
        return descriptionformat;
    }

    public void setDescriptionformat (String descriptionformat)
    {
        this.descriptionformat = descriptionformat;
    }

    public String getTimemodified ()
    {
        return timemodified;
    }

    public void setTimemodified (String timemodified)
    {
        this.timemodified = timemodified;
    }

    public String getLasttakenby ()
    {
        return lasttakenby;
    }

    public void setLasttakenby (String lasttakenby)
    {
        this.lasttakenby = lasttakenby;
    }

    public String getGroupid ()
    {
        return groupid;
    }

    public void setGroupid (String groupid)
    {
        this.groupid = groupid;
    }

    public long getSessdate ()
    {
        return sessdate;
    }

    public void setSessdate (long sessdate)
    {
        this.sessdate = sessdate;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(id);
		dest.writeString(duration);
		dest.writeString(studentscanmark);
		dest.writeString(lasttaken);
		dest.writeString(description);
		dest.writeString(descriptionformat);
		dest.writeString(timemodified);
		dest.writeString(lasttakenby);
		dest.writeString(groupid);
		dest.writeLong(sessdate);
		
	}
	
	public String getSessionDate()
	{
		long dv = Long.valueOf(sessdate)*1000;		// its need to be in milisecond
		Date df = new Date(dv);
		String vv = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(df);
		return vv;
	}
	
}
