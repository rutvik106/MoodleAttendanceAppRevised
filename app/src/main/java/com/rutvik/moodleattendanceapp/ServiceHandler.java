package com.rutvik.moodleattendanceapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
 
@SuppressWarnings("deprecation")
public class ServiceHandler {
	
	static String response ="{\"error\":{\"message\":\"Cannot Connect\",\"comment\":\"Not Connected\"}}";
	
	
	//Context context;
    
    //private String host="http://192.168.1.100/webservice.php?";		//Local
    
    //private String host="http://rutvik.ddns.net/webservice.php?";		//Internet

    private String host="http://"+GlobalSettings.getInstance().getHost()+"/webservice.php?";		//Dynamic
    

	public String checkHost()
	{
		String host=GlobalSettings.getInstance().getmSharedPreferences().getString("host", "moodle");		
		return executeCall("http://rutvik.comlu.com/moodleapp/webservice.php?method=check_host&host_name="+URLEncoder.encode(host));
	}
    
	public String login(String userName, String password) throws JSONException
	{
		String resp=checkHost();
		
		JSONObject obj=new JSONObject(resp);
		
		Host _host=new Host(obj.getJSONObject("host"));
		
		if(_host.hasAccess())
		{
			return executeCall(host+"method=login&user_name="+userName+"&password="+password);
		}
		else
		{
			String r ="{\"error\":{\"message\":\"access denied\",\"comment\":\"Host Blocked, Please Contact App Provider\"}}";
			return r;
		}
		
		
	}
	
	public String getCourses(String toke, String userId)
	{
		return executeCall(host+"method=get_courses&token="+toke+"&user_id="+userId);
	}
	
	public String getAttendanceType(String courseId)
	{
		Log.i("MAA", "called course_id="+courseId);
		return executeCall(host+"method=get_attendance_type&course_id="+courseId);
	}
	
	public String getSessions(String courseId, String attendanceTypeId)
	{
		return executeCall(host+"method=get_sessions&course_id="+courseId+"&attendance_type_id="+attendanceTypeId);
	}
	
	public String getAttendanceData(String token,String sessionId)
	{
		return executeCall(host+"method=get_attendance&token="+token+"&session_id="+sessionId);
	}
 
	public String uploadAttendanceData(String sessionId,String statusSet,String takenBy,String time,String data)
	{
		String d="{\"d\":"+data+"}";
		return executeCall(host+"method=add_attendance&session_id="+sessionId+"&status_set="+statusSet+"&taken_by="+takenBy+"&time="+time+"&data="+URLEncoder.encode(d));
	}
	
	public String updateAttendanceData(String sessionId,String statusSet,String takenBy,String time,String data)
	{
		String d="{\"d\":"+data+"}";
		return executeCall(host+"method=update_attendance&session_id="+sessionId+"&status_set="+statusSet+"&taken_by="+takenBy+"&time="+time+"&data="+URLEncoder.encode(d));
	}
	
	public String addSessions(String attendanceTypeId,String ssn_date,String duration,String time_mod,String desc)
	{
		return executeCall(host+"method=add_session&attendance_id="+attendanceTypeId+"&session_date="+ssn_date+"&duration="+duration+"&time_modified="+time_mod+"&description="+URLEncoder.encode(desc));
	}
	
	public String deleteSession(String userName,String password,String sessionId)
	{
		return executeCall(host+"method=delete_session&user_name="+userName+"&password="+password+"&session_id="+sessionId);
	}
    		
	public String executeCall(String url)
	{
			try
			{
				
				HttpGet httpGet = new HttpGet(url);
				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is established.
				// The default value is zero, that means the timeout is not used. 
				int timeoutConnection = 10000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 10000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				
	            // http client
				
//	            HttpClient httpClient = new DefaultHttpClient();
	            HttpEntity httpEntity = null;
//	            HttpResponse httpResponse = null;
//	            
//	            HttpGet httpGet = new HttpGet(url);
//	            
//	            
//	 
//	            httpResponse = httpClient.execute(httpGet);
	 
	            httpEntity = httpResponse.getEntity();
	            response = EntityUtils.toString(httpEntity);
	 
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	         
	        return response;

	}
	
	
	
	private static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	         = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	
	public static boolean hasActiveInternetConnection(Context context) {
		
		
		
		return isNetworkAvailable(context);
	}



}
