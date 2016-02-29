package com.rutvik.moodleattendanceapp;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
	
	
	String message;
	
	String comment;
	
	public Response(JSONObject obj) throws JSONException {
		
		message=obj.getString("message");
		comment=obj.getString("comment");
		
	}
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}	

}
