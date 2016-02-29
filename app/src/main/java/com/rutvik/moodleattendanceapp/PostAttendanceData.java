package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PostAttendanceData {
	
	
	String id,status,remark,full_name;
	
	public PostAttendanceData(String id,String status,String remark) {
		setId(id);
		setStatus(status);
		setRemark(remark);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public static String toJSON(ArrayList<PostAttendanceData> data)
	{
		JSONArray jArr=new JSONArray();
		
		for(PostAttendanceData a:data)
		{
			JSONObject j=new JSONObject();
			try
			{
				j.put("i", a.getId());
				j.put("s", a.getStatus());
				j.put("r", a.getRemark());
				
				jArr.put(j);
				
			}
			catch (JSONException e) {
				// TODO: handle exception
			}
		}
		
		return jArr.toString();
	}

}
