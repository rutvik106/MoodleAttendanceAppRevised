package com.rutvik.moodleattendanceapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Statuses extends JSONObject implements Parcelable
{
    private String id;

    private String visible;

    private String acronym;

    private String description;

    private String grade;

    private String deleted;
    
    public Statuses(Parcel p)
    {
    	id=p.readString();
    	visible=p.readString();
    	acronym=p.readString();
    	description=p.readString();
    	grade=p.readString();
    	deleted=p.readString();
    }
    
    public static final Creator<Statuses> CREATOR = new Creator<Statuses>() {

		@Override
		public Statuses createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Statuses(source);
		}

		@Override
		public Statuses[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Statuses[size];
		}
	};
    
    public Statuses(JSONObject obj) throws JSONException
    {

	    	id=obj.getString("id");
	    	visible=obj.getString("visible");
	    	acronym=obj.getString("acronym");
	    	description=obj.getString("description");
	    	grade=obj.getString("deleted");

    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getVisible ()
    {
        return visible;
    }

    public void setVisible (String visible)
    {
        this.visible = visible;
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
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getGrade ()
    {
        return grade;
    }

    public void setGrade (String grade)
    {
        this.grade = grade;
    }

    public String getDeleted ()
    {
        return deleted;
    }

    public void setDeleted (String deleted)
    {
        this.deleted = deleted;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", visible = "+visible+", acronym = "+acronym+", description = "+description+", grade = "+grade+", deleted = "+deleted+"]";
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(id);
		dest.writeString(visible);
		dest.writeString(acronym);
		dest.writeString(description);
		dest.writeString(grade);
		dest.writeString(deleted);
		
	}
	
	
}
