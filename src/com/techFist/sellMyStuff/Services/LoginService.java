package com.techFist.sellMyStuff.Services;

import java.io.BufferedReader;


import java.io.InputStreamReader;
import java.util.ArrayList;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.SecureLoginScreen;
import com.techFist.sellMyStuff.UserRegistration;
import com.techFist.sellMyStuff.Commons.User;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import android.os.ResultReceiver;


public class LoginService extends Service {

    Context context;
	HttpClient PostClient;
	HttpPost httpPost;	
	HttpResponse resp;	
	Bundle formData;
    static final String    SERVER    = "http://sms-techfist.appspot.com";	
	private ConnectivityManager connectivityManager;	
	// Binder which receives Notifications from Activity
	private final IBinder mBinder = new LocalBinder();
    private  AsyncTask<String,Integer,Integer> loginTask;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
    	httpPost = new HttpPost();
		PostClient = new DefaultHttpClient();		
		formData = new Bundle();
		
	}
	
	
	// Returns Mapping for Activity
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
    public class LocalBinder extends Binder {
    	public LoginService getService() {
            return LoginService.this;
        }
    }	
	
    // Distroy Service
    public void onDestroy() {
        // Tell the user we stopped.
         ////System.out.println("##### Service Distroyed");
    }	
    
    
   // Starting Service	
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
	   formData = intent.getExtras();
	   // TODO Auto-generated method stub
   	    ////System.out.println("##### Login Service Started");
	   // Login User RPC
   	   int status = checkNetworkStatus(context);
   	    if(status == 0){
   	     if(startId == 1){
	   	    if(intent != null)
                loginTask = new PerformLogin().execute("Login User"); 
                LoginScreen.loginTask = loginTask;
                return 300;
	     }
	     // User Registration RPC
	     if(startId == 2){
		    if(intent != null)
               loginTask = new PerformRegistration().execute("Register User"); 
		       UserRegistration.registerTask = loginTask;
		       return 300;
	     }
   	     // User Login RPC
	     if(startId == 3){

	    	 loginTask = new LoginUser().execute("User Login");
	    	 SecureLoginScreen.loginTask = loginTask;
	    	 return 300;
   	     }
	     // Fetch User Details.
	     if(startId == 4){
	       if(flags == PocketTrader.SECURE_LOGIN_SCREEN)	 
	       {
	    	   loginTask = new DownloadUserData().execute("Fetch User Details");
	    	   SecureLoginScreen.userTask = loginTask;
	    	   return 300;
	       }	   
	       if(flags == PocketTrader.USER_REGEISTRATION_SCREEN){
	    	   loginTask = new DownloadUserData().execute("Fetch User Details");
	    	   UserRegistration.userTask = loginTask;
	    	   return 300;
	       }
	     
	     }
	     // Update User Details
	     if(startId == 5){
	    	 loginTask = new UpdateUserDetail().execute("Update Details");
	    	 return 300;
	     }
	     // Register for Push Notification
	     if(startId == 6){
	    	 loginTask = new PushNotification().execute("Register");
	    	 return 300;
	     }
	     // Update Registration for push Notification
	     if(startId == 7){
	    	 
	    	 loginTask = new PushNotification().execute("Update"); 
	    	 return 300;
	     }
	     
   	    }
   	    else{
   	    	return -404;
   	    }
        return -404;
   }
   
   
   public boolean cancelRunningTask(){
     try{	
   	if(loginTask != null)
   		if(!loginTask.isCancelled()){ 
   			loginTask.cancel(true);
   			 ////System.out.println("###### A running Login Task has been Cancelled");
   		    return true;
   		}		
     }catch(Exception e){
        return false;}
     return false;
   }
   
   // Async Task for Push Notification
   class PushNotification extends AsyncTask<String,Integer,Integer>{
    final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");  
	String taskType;    
	@Override
	protected Integer doInBackground(String... arg0) {
	    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
		taskType = arg0[0];
    	if(taskType.equals("Register"))	{
 	       httpPost = new HttpPost(SERVER+"/registerForNotification");	
 	       data.add(new BasicNameValuePair("Email",formData.getString("Email")));
 	       data.add(new BasicNameValuePair("Registration", formData.getString("RegistrationID")));
    		
    	}
    	else if(taskType.equals("Update")){
 	       httpPost = new HttpPost(SERVER+"/updateNotification");	
 	       data.add(new BasicNameValuePair("Email",formData.getString("Email")));
 	       data.add(new BasicNameValuePair("Registration", formData.getString("RegistrationID")));
    	}
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
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		 Bundle b = new Bundle();
	    	if(taskType.equals("Register"))	{
	   		 b.putInt("Result", result);
			 receiver.send(0, b);		

	    	}
	     	else if(taskType.equals("Update")){
	   		 b.putInt("Result", result);
			 receiver.send(1, b);		

	     	}		
	}
	   
   }
   
   
   
   
   // Async Task for Updating user Details
   
   class UpdateUserDetail extends AsyncTask<String,Integer,Integer>{
	final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");  
    ArrayList<NameValuePair> data;
	   
	   
	@Override
	protected Integer doInBackground(String... params) {
		httpPost = new HttpPost(SERVER+"/getUpdateuser");	
	    data = new ArrayList<NameValuePair>();
	    data.add(new BasicNameValuePair("Key",formData.getString("Key")));
        data.add(new BasicNameValuePair("FirstName", formData.getString("FirstName")));       
        data.add(new BasicNameValuePair("LastName", formData.getString("LastName")));
        data.add(new BasicNameValuePair("Email",formData.getString("Email")));
        data.add(new BasicNameValuePair("Phone",formData.getString("Phone")));       
        data.add(new BasicNameValuePair("Country",formData.getString("Country")));  
        data.add(new BasicNameValuePair("City",formData.getString("City")));
        data.add(new BasicNameValuePair("State",formData.getString("State")));
        data.add(new BasicNameValuePair("PhoneOption",formData.getString("PhoneOption")));
        
        try{
  	      httpPost.setEntity(new UrlEncodedFormEntity(data));	 
  	      resp = PostClient.execute(httpPost);        	
		  // Successfully Updated
  	      if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	    	 return 1;
	      }
		  // Data Store Exception, Ask User to Try Again
  	      else if(resp.getStatusLine().getStatusCode() == 10000){
		   return -1;
	      }
  	      else{
  	    	  return -404;
  	      }
        }catch(Exception e){
        	return -404;
        }

	}
	 
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		 Bundle b = new Bundle();
		 b.putInt("Result", result);
		 receiver.send(0, b);	
	}
   }
   
   
   // Async Task for Performing Login Screen Activity;
   class PerformLogin extends AsyncTask<String,Integer,Integer> {
	final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");  
	@Override
	protected Integer doInBackground(String... params) {
	       httpPost = new HttpPost(SERVER+"/signInUser");	
	       ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
	       data.add(new BasicNameValuePair("Email",formData.getString("Email")));
	       data.add(new BasicNameValuePair("Password", formData.getString("Password")));
	       try {
	           httpPost.setEntity(new UrlEncodedFormEntity(data));
	    	   resp = PostClient.execute(httpPost);
		        if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		           // User logged In, Download User Details
		        	httpPost = new HttpPost(SERVER+"/getmyuser");
		 	        data = new ArrayList<NameValuePair>();
			        data.add(new BasicNameValuePair("Email",formData.getString("Email")));		        	
		            httpPost.setEntity(new UrlEncodedFormEntity(data));
		    	    resp = PostClient.execute(httpPost);
	         	    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                       // Valid Response, Start reading Data from Buffer
	 	  		       BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		  	           StringBuilder sb = new StringBuilder();
		               String line = null;
		               while((line = reader.readLine())!=null){
		             	     sb = sb.append(line);
		               }
	                   JSONObject jo = new JSONObject(sb.toString());
	                   
	                   User user = new User(jo.getString("Name"),jo.getString("Country"),jo.getString("City"),jo.getString("Email"),jo.getString("Phone"),
	                		   jo.getString("FirstName"),jo.getString("LastName"),jo.getString("Key"),jo.getString("State"),
	                		   Boolean.valueOf(jo.getString("PhoneOption")));
	                   // Setting up Response
	                   PocketTrader.user = user;
	         	    }		    	   
	                return 0;
	           }
	           if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
	 	          // User Login Screen	
		            httpPost = new HttpPost(SERVER+"/checkUser");	
		            ArrayList<NameValuePair> data1 = new ArrayList<NameValuePair>();
		            data1.add(new BasicNameValuePair("Email",formData.getString("Email")));
		            try{
		                httpPost.setEntity(new UrlEncodedFormEntity(data1));	 
		         	    resp = PostClient.execute(httpPost);
		         	    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		         	    	// User Exist Pop up Secure log In Screen
		         	    	return 1;
		         	    }
		         	    else if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
		         	    	// User do not exist Pop up Registration Screen
		         	    	 ////System.out.println("##### User Does Not Exist");
		         	    	return 2;
		         	    }
		            }
		        	catch(Exception e){
		        		return -1;
		        	}
	           }
	       }    
	       catch(Exception e){
	        	   return -1;
	       }
		   return -1; 	       
	}
    @Override
    protected void onPostExecute(Integer result) {
	 // TODO Auto-generated method stub
	 super.onPostExecute(result);
	 Bundle b = new Bundle();
	 b.putInt("Result", result);
	 receiver.send(0, b);
    }  	

   }

   // Async Task for Performing User registration.
   class PerformRegistration extends AsyncTask<String,Integer,Integer>{
    final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");  
	ArrayList<NameValuePair> data;
	@Override
	protected Integer doInBackground(String... params) {
		int userPresentStatus = -1;
	    // Checking Present status of user
		httpPost = new HttpPost(SERVER+"/checkUser");	
	    data = new ArrayList<NameValuePair>();
	    data.add(new BasicNameValuePair("Email",formData.getString("Email")));
	    try{
	      httpPost.setEntity(new UrlEncodedFormEntity(data));	 
	      resp = PostClient.execute(httpPost);
	      if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	    	  userPresentStatus = 1;
	      }
	      else if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
	    	  userPresentStatus = 2;
	       }
	     }
	   	 catch(Exception e){
	   		userPresentStatus = -1;
	   	  }	   
	    // Performing actualy Registration Process    
		   if(userPresentStatus == 2){
		          httpPost = new HttpPost(SERVER+"/registerUser");	
		          data = new ArrayList<NameValuePair>();
		          data.add(new BasicNameValuePair("FirstName", formData.getString("FirstName")));       
		          data.add(new BasicNameValuePair("LastName", formData.getString("LastName")));
		          data.add(new BasicNameValuePair("Email",formData.getString("Email")));
		          data.add(new BasicNameValuePair("Password", formData.getString("Password")));
		          data.add(new BasicNameValuePair("Country",formData.getString("Country")));  
		          data.add(new BasicNameValuePair("City",formData.getString("City")));
		          data.add(new BasicNameValuePair("Phone",formData.getString("Phone")));
		          data.add(new BasicNameValuePair("State",formData.getString("State")));
		          data.add(new BasicNameValuePair("PhoneOption",formData.getString("PhoneOption")));
		          try{
		              httpPost.setEntity(new UrlEncodedFormEntity(data));
		    	      resp = PostClient.execute(httpPost);  
		    	      if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		    		   // Registration is Confirmed, Push User to Main Screen
		    		   return 0;
		    	      }
		    	      if(resp.getStatusLine().getStatusCode() == 10000){
		    		   // Data Store Exception, Ask User to Try Again
		    		   return -1;
		    	      }
		    	      else{
		    		   // Ask User to Sign In
		    		   return 1;
		    	      }
		          }
		          catch(Exception e){
		    	       return 1;
		          }
			   }
			   if(userPresentStatus == 1){
				   // User already Exist, Sign In
				   return 3;
			   }
			   else{
				   // Ask, user to try again
				   return 4;
			   }		
	}
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		 Bundle b = new Bundle();
		 b.putInt("Result", result);
		 receiver.send(0, b);
	} 
   }
   
   // Async Task for Downloading User Data
   class DownloadUserData extends AsyncTask<String,Integer,Integer>{
	final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
    ArrayList<NameValuePair> data;
	@Override
	protected Integer doInBackground(String... params) {
        // User logged In, Download User Details
     	try{
		  httpPost = new HttpPost(SERVER+"/getmyuser");
	      data = new ArrayList<NameValuePair>();
	      data.add(new BasicNameValuePair("Email",formData.getString("Email")));		        	
          httpPost.setEntity(new UrlEncodedFormEntity(data));
 	      resp = PostClient.execute(httpPost);
  	      if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            // Valid Response, Start reading Data from Buffer
		       BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
	           StringBuilder sb = new StringBuilder();
               String line = null;
               while((line = reader.readLine())!=null){
          	     sb = sb.append(line);
               }
               JSONObject jo = new JSONObject(sb.toString());
               User user = new User(jo.getString("Name"),jo.getString("Country"),jo.getString("City"),jo.getString("Email"),jo.getString("Phone"),
            		   jo.getString("FirstName"),jo.getString("LastName"),jo.getString("Key"),jo.getString("State"),
            		   Boolean.getBoolean(jo.getString("PhoneOption")));
               // Setting up Response
               PocketTrader.user = user;
               // Succesfull
               return PocketTrader.PROCESS_SUCCESS;

	      }
  	      else if(resp.getStatusLine().getStatusCode() == 10000){
  	    	  // Datastore Exception 
  	    	  return PocketTrader.DATASTORE_EXCEPTION;
  	      }
  	      else{
  	    	  // Service Unavailable
  	    	  return PocketTrader.SERVICE_UNAVAILABLE;
  	      }
      }
      catch(Exception e){
    	  // Servie Unavailable
    	  return PocketTrader.SERVICE_UNAVAILABLE;
      }
       
	}
    @Override
    protected void onPostExecute(Integer result) {
	// TODO Auto-generated method stub
	super.onPostExecute(result);

	 Bundle b = new Bundle();
	 b.putInt("Result", result);
	 receiver.send(PocketTrader.FETCH_USER_DATA_REQUEST, b);
   }
   } 
   
   // AsyncTask for Loggin in User
   class LoginUser extends AsyncTask<String,Integer,Integer>{
		final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver"); 
	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
	       httpPost = new HttpPost(SERVER+"/signInUser");	
	       ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
	       data.add(new BasicNameValuePair("Email",formData.getString("Email")));
	       data.add(new BasicNameValuePair("Password", formData.getString("Password")));
	       try {
	           httpPost.setEntity(new UrlEncodedFormEntity(data));
	    	   resp = PostClient.execute(httpPost);
		        if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		           // Login User	
	               return 0;
	           }
	           if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
	        	   // User not Authenticated
	        	   return 1;
	           }
	       }    
	       catch(Exception e){
	        	   return -1;
	       }
		   return -1; 
	}
	  @Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		 Bundle b = new Bundle();
		 b.putInt("Result", result);
		 receiver.send(0, b);
	} 
   }
   
   
   // Checking Network Availibility
   public int checkNetworkStatus(Context context){
      
	   connectivityManager = (ConnectivityManager) this.getSystemService(context.CONNECTIVITY_SERVICE);
	   NetworkInfo info = connectivityManager.getActiveNetworkInfo();
	   
	   if(info == null)
          return -1;
	   else 
		   return 0;

   }
   


 
   
}
