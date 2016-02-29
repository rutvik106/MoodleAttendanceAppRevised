package com.rutvik.moodleattendanceapp;

import org.json.JSONException;

import org.json.JSONObject;

public class Host extends JSONObject {
	
	String host;
	
	int id=0;
	
	int timestamp;
	
	int attempt;
	
	int access;
	
	int lastAccess;
	
	Host(JSONObject obj) throws JSONException
	{
		id=obj.getInt("id");		
		host=obj.getString("host");
		timestamp=obj.getInt("timestamp");
		attempt=obj.getInt("attempt");
		access=obj.getInt("access");
		lastAccess=obj.getInt("last_access");
	}

	public int getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(int lastAccess) {
		this.lastAccess = lastAccess;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}	
	
	public Boolean hasAccess()
	{
		if(access==1)
		{
			return true;
		}
		
		return false;
	}
	
}
