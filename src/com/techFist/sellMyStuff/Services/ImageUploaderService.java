package com.techFist.sellMyStuff.Services;



/* Task 1, Write a compression Algo, Compress the Selected pics
 * Once images are compressed, Upload them sequence wise,
 * is a image is uploaded, Delete it. When App is started
 * Start the image uploader servie to check pending images for upload
 * If images are pending for upload, then upload them and delete.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;

public class ImageUploaderService extends Service {
	private static final int HELLO_ID = 1;
	private Context context;
    private ArrayList<Uri> imageList;
	private DefaultHttpClient GetCLIENT;
	private HttpClient PostClient;
	private HttpGet get;
	private HttpPost httpPost;	
	private MultipartEntity entity;	
	private HttpResponse resp;	
    static final String    SERVER    = "http://sms-techfist.appspot.com";		
	private ConnectivityManager connectivityManager;	
	
	
    @Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
  
	
  @Override
  public void onCreate() {
	// TODO Auto-generated method stub
	super.onCreate();
	// Initializing Form Objects
	context = this;
	GetCLIENT = new DefaultHttpClient();
	get = new HttpGet();
	httpPost = new HttpPost();
	PostClient = new DefaultHttpClient();	
    //entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	
	// Checking Network Status
     if(checkNetworkStatus(context) == 0){  // Active Network Start Image Upload
	   // Spawning Thread for Compressing and Resaving Images
	   new ImageCompression().execute("Refresh Images");
     }
     else{  // No Active Network present, Push a Failed Notification
    	 publishNotification("Upload Failed","No Active Network, Please Check you Network Connection",true,true,true,-1,1,1);
     }
     
  }
 @Override
 public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
     ////System.out.println("######  Image Compression Service Destroye");
 }	

  // Method for generating Notification 
  /* Type = 0, For Uploading
   * Type = 1, For Success
   * Type = -1, For Failed
   */
 
  public boolean  publishNotification(String tickerText,String text,boolean vibrate,boolean sound,boolean lcd,int type,int maxCount,int doneCount){
	  // Getting reference of notification Manager
	  NotificationManager notifMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	  // Getting a reference of Notification
	  int notif_icon = -1;
	  if(type == -1)
		  notif_icon= R.drawable.notification_download_stopped;
	  else if(type == 1)
		  notif_icon= R.drawable.notification_download_complete;
	  else
		  notif_icon= R.drawable.notification_download_progress;
		  
	  
	  long when = System.currentTimeMillis();
	  Notification notif = new Notification(notif_icon,tickerText,when);
	  
	  // Setting up Task type.
	  
	  // Setting Notification Properties
	  if(sound)   // Adding sound to notification
		  notif.defaults |= Notification.DEFAULT_SOUND;
	  if(vibrate)  // Adding vibration to Notification
		  notif.defaults |= Notification.DEFAULT_VIBRATE;
      if(lcd)		// Adding Lights to Notification  
    	  notif.defaults |= Notification.DEFAULT_LIGHTS;
	  
      // Setting up Image
      int notifImage = -1;
      if(type == 0)
    	  notifImage = R.drawable.notification_refresh;
      else if(type == 1)
    	  notifImage = R.drawable.notification_download_complete;
      else
    	  notifImage = R.drawable.notification_download_stopped;

      // Setting Custom layout
      RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
      contentView.setImageViewResource(R.id.notification_image,notifImage);
      contentView.setTextViewText(R.id.notification_title, "Pocket Trader");
      contentView.setTextViewText(R.id.notification_text, text);
      contentView.setProgressBar(R.id.notification_progressbar, maxCount, doneCount, false);
      notif.contentView = contentView;
      
      // Setting up Pending Intent
      Intent notificationIntent;
      if(type == -1)   // Uploading has failed, Supply a Pending intent to resume Remainings Upload
    	  notificationIntent = new Intent(getApplicationContext(), ImageUploaderService.class);
      else            // Uploading was a success, Supply a Pending intent to Start Pocket Trader
    	  notificationIntent = new Intent(getApplicationContext(), PocketTrader.class);
    	  
      PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
      notif.contentIntent = contentIntent;      
      
      notifMgr.notify(HELLO_ID, notif);	  
	  return true;
  }
	
  //Method for Compressing a Image
  public boolean compressMyImage(Uri uri,int targetWidth,int targetHeight){
	        Bitmap bitMapImage = null;
	        // First, get the dimensions of the image
	        Options options = new Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(uri.getPath(), options);
	        double sampleSize = 0;
	        // Only scale if we need to
	        // (16384 buffer for img processing)
	        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math
	                .abs(options.outWidth - targetWidth);

	        if (options.outHeight * options.outWidth * 2 >= 1638) {
	            // Load, scaling to smallest power of 2 that'll get it <= desired
	            // dimensions
	            sampleSize = scaleByHeight ? options.outHeight / targetHeight
	                    : options.outWidth / targetWidth;
	            sampleSize = (int) Math.pow(2d,
	                    Math.floor(Math.log(sampleSize) / Math.log(2d)));
	        }

	        // Do the actual decoding
	        options.inJustDecodeBounds = false;
	        options.inTempStorage = new byte[128];
	        while (true) {
	            try {
	                options.inSampleSize = (int) sampleSize;
	                bitMapImage = BitmapFactory.decodeFile(uri.getPath(), options);

	                break;
	            } catch (Exception ex) {
	                try {
	                    sampleSize = sampleSize * 2;
	                } catch (Exception ex1) {

	                }
	            }
	        }
		    
	        File file = new File(uri.getPath());
	          
	          if(file.exists() && bitMapImage != null)
	           {
	        	  file.delete();
	        	  String path = uri.getPath();
	        	  path = path.replaceFirst("-", "-comp-");
	        	  file = new File(path);
	        	  try{
	        	    FileOutputStream stm = new FileOutputStream(file);
	        	    bitMapImage.compress(Bitmap.CompressFormat.JPEG,50,stm);
	        	  }
	        	  catch(Exception e){
	        		return false;
	        	  }
	        
	           }
	  return true;
  }
  // Method for Checking Pending Images for Uplaod	
  public ArrayList<Uri> imagesForUpload(){
	  
	  File file = new File(Environment.getExternalStorageDirectory() + "/PocketTrader/Images/");
	  if(file.isDirectory())
	  {
		   // Directory exist check for Files present
		   File []  fileArr;
		   ArrayList<Uri> fileList = new ArrayList<Uri>();
		   fileArr = file.listFiles();
		   if(fileArr != null){    // Files has been added into array
		     if(fileArr.length > 0){    // File Exist Start Adding Files into List
			    
		    	 for(int i=0;i<fileArr.length;i++){
		    		 String path = fileArr[i].getPath();
		    		     Uri uri = Uri.parse(path);
		    		     fileList.add(uri);
   
		    	 }
		    	 return fileList;
		      }
		     else
		       return null; // No Files added into list	 
		   }
		   else{
			   return null;   // No files added into List
		   }
		   
		   
	  }
	  else
        return null;  // No Files added into list
  }
 
  // Async Task for Complressing and resaving Image in proper formst
  class ImageCompression extends AsyncTask<String,Integer,Integer>{
	    private ArrayList<Integer> uploads = new ArrayList<Integer>();
	  int succesfullUploads = 0;
	private boolean uploadImage(Uri uri) {
		   // Check for network Status 
		   int networkStatus = checkNetworkStatus(context);
		   if(networkStatus == 0){   // Active Network Present Start Image upload
	          ////System.out.println("##### Uploadin File :" + uri.getPath());
		     try{
			   // Fetching Upload URL
		       get.setURI(new URI(SERVER + "/GetUploadUrl"));
		       resp = GetCLIENT.execute(get);
			   StringBuilder sb = new StringBuilder();
			   if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			    {           
		           BufferedReader reader = new BufferedReader(new InputStreamReader( resp.getEntity().getContent()));
		           sb = new StringBuilder();
		           String line = null;            
		           while((line = reader.readLine())!=null){
			            sb = sb.append(line);
		           }
		            ////System.out.println("###### Fetched Upload URL :" + sb.toString());
			    }
	             File file = new File(uri.getPath());

		         String fileName = file.getName();
		         String urlServer = sb.toString();
		          ////System.out.println("##### File Name:" + fileName);
		        httpPost = new HttpPost(urlServer);	 
		        entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		        httpPost.setEntity(entity);
		        entity.addPart("name",new StringBody(fileName));
		        entity.addPart("type", new StringBody("image/jpeg"));
		        entity.addPart("myFile", new FileBody(file));
		        
		        HttpResponse response = PostClient.execute(httpPost);
		        
		        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			         ////System.out.println("###### File has Been uploaded");
			        
		        	file.delete();
			        return true;
		        	
		        }
		        
	         }
	         catch(Exception e){
	    	   e.printStackTrace();
              return false;
	         }
		     return false;
		   }
		   else
		   {
		         return false;
		   }
		   
	}
	  
	  
	@Override
	protected Integer doInBackground(String... arg0) {
		imageList = imagesForUpload();
		if(imageList != null) {
		     // Publish file Compression Notification
			String text = "Uploading " + imageList.size() + " Images";
			publishNotification("Starting Upload",text,false,false,false,0,imageList.size(),0);

	         for(Uri uri: imageList){
	        	 String path = uri.getPath();
	        	 if(path.contains("comp")){ //  Image is already compressed, start direct uploading
	        		  boolean status = uploadImage(uri);
	        		  if(status) /// File Succesfully Uploaded
	        		   {
	        			  uploads.add(succesfullUploads);
	        			  succesfullUploads = succesfullUploads + 1;
	        			  // Update Notification
	        			  text = "Uploading " + (imageList.size() - succesfullUploads) + " Images";
	        			  publishNotification("",text,false,false,false,0,imageList.size(),succesfullUploads);
          
	        			  //publishNotification("Uploading " + (imageList.size() - succesfullUploads) + " Images");
	        			
	        		   } 	        		  
	        	  }
	        	  else if(compressMyImage(uri,1600,1200)){ // getting a 2 Megapixels image resolution
	        		  // File has been Succesfully Compressed and Overwritten, Start uploading now
	        		  boolean status = uploadImage(Uri.parse(uri.getPath().replaceFirst("-","-comp-")));
	        		  if(status) /// File Succesfully Uploaded
	        		   {
	        			  uploads.add(succesfullUploads);
	        			  succesfullUploads = succesfullUploads + 1;
	        			  // Update Notification
	        			  text = "Uploading " + (imageList.size() - succesfullUploads) + " Images";
	        			  publishNotification("",text,false,false,false,0,imageList.size(),succesfullUploads);
	        			
	        		   }   
	         	   }
	         }
	 		if((imageList.size() - succesfullUploads) <1)
				return 1;          // All Images Succesfully Uploaded
			else
				return -1;         // Some images are not uploaded
		     
		}
		return 0;
	}
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		String text = "";
		if( result == -1){
			text = "Uploading Failed for " + (imageList.size() - succesfullUploads) + " Images";
			publishNotification("Uploading Failed",text,true,true,true,-1,imageList.size(),succesfullUploads);
		
		}
	    if(result ==1){
			text = "All Images Succesfully Uploaded";
	    	publishNotification("Images Uploaded",text,true,true,true,1,imageList.size(),succesfullUploads);
			
	    }
	    stopSelf();
		
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

