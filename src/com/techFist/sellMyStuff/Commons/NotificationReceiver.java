package com.techFist.sellMyStuff.Commons;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

public class NotificationReceiver extends BroadcastReceiver{
	HttpClient PostClient;
	HttpPost httpPost;	
	HttpResponse resp;	
	Bundle formData;
	String registration = null;
	Context context;
    static final String    SERVER    = "http://sms-techfist.appspot.com";	
	public static final String PREFS_NAME = "UserAccount";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
    	httpPost = new HttpPost();
		PostClient = new DefaultHttpClient();		
		formData = new Bundle();
		
		// TODO Auto-generated method stub
	    if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
	        handleRegistration(context, intent);
	    } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	        handleMessage(context, intent);
	     }
	}

	private void handleRegistration(Context context, Intent intent) {
	    registration = intent.getStringExtra("registration_id");
	    this.context = context;
	    if (intent.getStringExtra("error") != null) {
	        // Registration failed, should try again later.
	    } else if (intent.getStringExtra("unregistered") != null) {
	        // unregistration done, new messages from the authorized sender will be rejected
	    	registration = " ";
	    } else if (registration != null) {
	       // Send the registration ID to the 3rd party site that is sending the messages.
	       // This should be done in a separate thread.
	       // When done, remember that all registration is done. 
	        ////System.out.println("##### Registration Id Received from Google :" + registration);	
	        ////System.out.println("##### Initiating Registration");
	       new PushNotification().execute("Register"); 	
	    }
	}
	
	private void handleMessage(Context context,Intent intent){
		 ////System.out.println("###### Message Received from Cloud" + intent.getStringExtra("payload"));
	}
	
   // Async Task for Push Notification
   class PushNotification extends AsyncTask<String,Integer,Integer>{
		String taskType;    
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

		@Override
		protected Integer doInBackground(String... arg0) {
		    
			// Fetching User ID
			String email =  prefs.getString("Email", "");
			boolean status = prefs.getBoolean("Push-Status", false);
			if(status)
				taskType = "Update";
			
			if(email != ""){
			   ////System.out.println("###### Starting regestration foe Email: " + email);
			  ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			  taskType = arg0[0];
	    	  if(taskType.equals("Register"))	{
	 	         httpPost = new HttpPost(SERVER+"/registerForNotification");	
	 	         data.add(new BasicNameValuePair("Email",email));
	 	         data.add(new BasicNameValuePair("RegistrationID", registration));
	    		
	    	  }
	    	  else if(taskType.equals("Update")){
	 	         httpPost = new HttpPost(SERVER+"/updateNotification");	
	 	         data.add(new BasicNameValuePair("Email",formData.getString("Email")));
	 	         data.add(new BasicNameValuePair("RegistrationID", registration));
	    	  }
	    	   ////System.out.println("##### Executing Push Notification Registration");
	    	
		      try {
	            httpPost.setEntity(new UrlEncodedFormEntity(data));
	 	        resp = PostClient.execute(httpPost);
		        if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		    	    return 1; //Registration Successfull
		        }
		        else if(resp.getStatusLine().getStatusCode() == 10000){
		        	return 0; // Datastore Exception
		        }
		        else{
		        	return -1; // Service Unavailable
		        }
		    }
		    catch(Exception e){
		    	return -2;  // Unknown Exception
		    }
		  }   
			return -1;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
	        if(result == 1){
	        	 ////System.out.println("##### Registration Succesfull");
	    		SharedPreferences.Editor editor = prefs.edit();
	    	  	editor.putBoolean("Push-Status", true);
	    	  	editor.commit();
	        }
		}
		   
	   }	
}
