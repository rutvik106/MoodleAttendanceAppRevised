package com.rutvik.moodleattendanceapp;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorObj
{
	String message,comment;
	
	public ErrorObj(String data) throws JSONException
	{

			JSONObject obj=new JSONObject(data).getJSONObject("error");
			message=obj.getString("message");
			comment=obj.getString("comment");

	}

	public String getMessage() {
		return message;
	}

	public String getComment() {
		return comment;
	}
	
}

