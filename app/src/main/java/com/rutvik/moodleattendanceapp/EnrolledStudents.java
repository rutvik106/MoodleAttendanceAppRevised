package com.rutvik.moodleattendanceapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class EnrolledStudents extends JSONObject implements Parcelable
{
    private String profile_pic_url;

    private String user_name;

    private String first_name;

    private String last_name;

    private String user_id;

    private String full_name;
    
    public EnrolledStudents(Parcel p)
    {
    	profile_pic_url=p.readString();
    	user_name=p.readString();
    	first_name=p.readString();
    	last_name=p.readString();
    	user_id=p.readString();
    	full_name=p.readString();
    }
    
    public static final Creator<EnrolledStudents> CREATOR = new Creator<EnrolledStudents>() {

		@Override
		public EnrolledStudents createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new EnrolledStudents(source);
		}

		@Override
		public EnrolledStudents[] newArray(int size) {
			// TODO Auto-generated method stub
			return new EnrolledStudents[size];
		}
	};
    
    public EnrolledStudents(JSONObject obj) throws JSONException {

			profile_pic_url=obj.getString("profile_pic_url");
			user_name=obj.getString("user_name");
			first_name=obj.getString("first_name");
			last_name=obj.getString("last_name");
			user_id=obj.getString("user_id");
			full_name=obj.getString("full_name");

	
	}

    public String getProfile_pic_url ()
    {
        return profile_pic_url;
    }

    public void setProfile_pic_url (String profile_pic_url)
    {
        this.profile_pic_url = profile_pic_url;
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

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
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
		
		dest.writeString(profile_pic_url);
		dest.writeString(user_name);
		dest.writeString(first_name);
		dest.writeString(last_name);
		dest.writeString(user_id);
		dest.writeString(full_name);
		
	}
}
