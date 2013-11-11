package com.techFist.sellMyStuff.Services;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

public class ImageService extends Service {

	DefaultHttpClient GetCLIENT;
	HttpClient PostClient;
	HttpGet get;
	HttpPost httpPost;	
	MultipartEntity entity;	
	Bundle formData;
	HttpResponse resp;	
	File file;	
    static final String    SERVER    = "http://sms-techfist.appspot.com";	
    String path;
	
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	 ////System.out.println("#####:Upload Service Started");
    	super.onDestroy();
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// Http Connection Initialization
		  GetCLIENT = new DefaultHttpClient();
		  PostClient = new DefaultHttpClient();
		  get = new HttpGet();
		  httpPost = new HttpPost();
		  PostClient = new DefaultHttpClient();	
	      entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);		
		  formData = new Bundle();
		   ////System.out.println("#####:Upload Service Started");
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		   formData = intent.getExtras();
		   path = formData.getString("Path");
		   new ImageUploader().execute("Upload");
		   return 0;      
		
	}
	
	class ImageUploader extends AsyncTask{

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			try{
			   // Fetching Upload URL
		       get.setURI(new URI(SERVER + "/GetUploadUrl"));
		       resp = GetCLIENT.execute(get);
			   int respStatus = resp.getStatusLine().getStatusCode();	       
			    ////System.out.println("#### Response Status : " + respStatus); 
			   StringBuilder sb = new StringBuilder();
			   if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			    {           
		           BufferedReader reader = new BufferedReader(new InputStreamReader( resp.getEntity().getContent()));
		           sb = new StringBuilder();
		           String line = null;            
		           while((line = reader.readLine())!=null){
			            sb = sb.append(line);
		           }
			    }
		         ////System.out.println("### Upload URL : " + sb.toString());		   
		        String pathToOurFile = formData.getString("Path");
	        
		        file = new File(pathToOurFile);
		        String urlServer = sb.toString();
		        httpPost = new HttpPost(urlServer);	  
		        //entity.addPart("file", new FileBody(file));
		        httpPost.setEntity(entity);
		        
		        HttpResponse response = PostClient.execute(httpPost);
		        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		        	stopSelf();
		        	
		        }
		       }
		       catch(Exception e){
		    	   stopSelf();
		       }		        
			
			return "Done";
		}
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}

}
