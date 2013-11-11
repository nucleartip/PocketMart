package com.techFist.sellMyStuff;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.ImageAdapter;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Services.ImageService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.android.*;
import com.google.ads.AdView;

public class SellCaptureImageForm extends PocketActivity {
 
	private Context context;
    private Bundle formData;
    private Gallery gallery;
	private ArrayList<Uri> imageList = new ArrayList<Uri>();
    private Button btnProceed; 
    private Button btnCaptureImage; 
    private Button btnPickImage; 
    private Button btnSkipUpload; 
    private Button btnReset;
	private String fileName;
	private int fileNumber  = 1;
	private Uri uriSavedImage;
    
    public void showMsgDialog(View v){

    	MessageHandler.getInstance(context).showMyMessageList(context);
    }	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        //Register a Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,SellCaptureImageForm.class));
    	
        //setContentView(R.layout.selling_image_capture_form);  
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.selling_image_capture_form);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
        
        
        
        formData = new Bundle();
        if(getIntent() != null){
        	formData = getIntent().getExtras();
        }
        
        
        // Fecthing Objects
        context = this;
        gallery = (Gallery)findViewById(R.id.capture_image_crousal);
        btnProceed = (Button)findViewById(R.id.capture_image_selling);
        btnCaptureImage = (Button)findViewById(R.id.capture_image);
        btnPickImage = (Button)findViewById(R.id.select_image);
        btnSkipUpload = (Button)findViewById(R.id.capture_skip_image_selling);
        btnReset = (Button)findViewById(R.id.select_image_reset);
        // Invoking Image Capture Activity
        btnCaptureImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 // Create the view using FirstGroup's LocalActivityManager
				//Intent intent = new Intent(context,CaptureImage.class);
				//intent.putExtras(formData);
				//startActivityForResult(intent,1);  
				// Starting default Camera, and Saving Image with a Specific file name
                /// Getting a Unique File Name
				fileName = formData.getString("Key") +"-" + formData.getString("Email") + "-"+formData.getString("Category") + "-"+fileNumber+".jpg";
	            Intent camera= new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	            uriSavedImage=Uri.fromFile(new File("/sdcard/PocketTrader/Images/" + fileName));
	            camera.putExtra("output", uriSavedImage);
	            startActivityForResult(camera, PocketTrader.CAPTURE_IMAGE);
			}
        
        });
        btnPickImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
			    intent.setAction(Intent.ACTION_PICK);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"),PocketTrader.PICK_IMAGE);
				
			}
        	
        });
        	
        
        // Proceeding to Next Activity
        btnProceed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 // Create the view using FirstGroup's LocalActivityManager
                 if(PocketTrader.performImagePresence()){
             		Intent intent = new Intent(context, ImageService.class);
            		intent.putExtras(formData);
            		startService(intent);       
            		Intent intent1 = new Intent(context,PocketTrader.class);
            		intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            		Toast.makeText(context,"Image Will Be uploaded", Toast.LENGTH_LONG).show();
            		new PostToFacebook().execute(formData);
            		startActivity(intent1);
                 }
				 else{
					 
				 }
			}
        	
        	
        });
       // Return to Main Screen
        btnSkipUpload.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,PocketTrader.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PocketTrader.performImageCacheCleanup();
				startActivity(intent);
				
			}
        	
        });
      // Reset Image Selection
        btnReset.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
		        int[] id = {R.drawable.refresh,R.drawable.refresh,R.drawable.refresh};
		        gallery.setAdapter(new ImageAdapter(id,context));
        		btnCaptureImage.setEnabled(true);
        		btnPickImage.setEnabled(true);
        		fileNumber = 1;
        		// Cleaning pre Saved files
        		for(Uri uri: imageList){
        			File file = new File(uri.getPath());
        			if(file.exists()){
        				file.delete();
        			}
        		}
        		imageList.removeAll(imageList);
   	        
			}
        });  
        
      // Setting up Initial gallery look
        // Setting up Adapter for Gallery
        int[] id = {R.drawable.refresh,R.drawable.refresh,R.drawable.refresh};
        gallery.setAdapter(new ImageAdapter(id,context));
         
    }

	
	
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	formData = savedInstanceState;
    	super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	// TODO Auto-generated method stub
    	outState.putAll(formData);
    	super.onSaveInstanceState(outState);
    }
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
   
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PocketTrader.CAPTURE_IMAGE){
        	
        	Toast.makeText(context, fileName, Toast.LENGTH_LONG).show();
        	imageList.add(uriSavedImage);
        	gallery.setAdapter(new ImageAdapter(imageList,context));
         	gallery.refreshDrawableState();
       	    fileNumber = fileNumber + 1;
        	if(fileNumber > 3){
        		btnCaptureImage.setEnabled(false);
        		btnPickImage.setEnabled(false);
        	}
        }
       if(requestCode == PocketTrader.PICK_IMAGE){
    	   
    	 try{  
    	   
    	   Uri selectedImage = data.getData();
           String[] filePathColumn = {MediaStore.Images.Media.DATA};
           Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
           cursor.moveToFirst();
           int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
           String filePath = cursor.getString(columnIndex);

           cursor.close();

           fileName = formData.getString("Key") +"-" + formData.getString("Email") + "-"+formData.getString("Category") + "-"+fileNumber+".jpg";
           uriSavedImage=Uri.fromFile(new File("/sdcard/PocketTrader/Images/" + fileName));
           File oldFile = new File(filePath);
           File newFile = new File(uriSavedImage.getPath());
           copyFile(oldFile,newFile);
           imageList.add(uriSavedImage);
       	   gallery.setAdapter(new ImageAdapter(imageList,context));

           fileNumber = fileNumber + 1;
       	   if(fileNumber > 3){
       		btnCaptureImage.setEnabled(false);
       		btnPickImage.setEnabled(false);
       	   }
    	 }// Try
    	 catch(Exception e){
    		 e.printStackTrace();
    		 Toast.makeText(context,"Unable to Process, Please try Again", Toast.LENGTH_SHORT).show();
    	 }
       }
    	
    }
    private void copyFile(File oldFile,File newFile) throws Exception{
    	try {
 

    	        if (oldFile.exists()) {
    	            FileChannel src = new FileInputStream(oldFile).getChannel();
    	            FileChannel dst = new FileOutputStream(newFile).getChannel();
    	            dst.transferFrom(src, 0, src.size());
    	            src.close();
    	            dst.close();
    	            gallery.refreshDrawableState();
    	        }
    	} catch (Exception e) { throw e;}    	
    }

    // Method, Pushes Home Screen
    public void pushHomeScreen(View v){
 	   Intent intent = new Intent(context,PocketTrader.class);
 	   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	   startActivity(intent);
    }
	 // Push My Profile Screen
	 public void pushMyProfile(View v){
			Intent intent = new Intent(context,MyProfileScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);		 
	 }    
	class PostToFacebook extends AsyncTask<Bundle,Integer,Integer>{
        private Facebook facebook;
		public PostToFacebook(){
		  facebook = new Facebook(PocketTrader.APP_ID);	
		}
		
		@Override
		protected Integer doInBackground(Bundle... params) {
			// TODO Auto-generated method stub
			Bundle data = params[0];
			boolean flag = restoreCredentials(facebook);
			if(flag){  // Session is Valid Post Deal in facebook
		   		Bundle parameters = new Bundle();
                String message = "Posted a Pocket Deal, Product:" + data.getString("Name") + ",priced at Rs." + data.getString("Price");
		   		
		        parameters.putString("message", message);
		        parameters.putString("link", "http://pockettrader.wix.com/pocket-trader");
                try {
           	      facebook.request("me");
   			      String response = facebook.request("me/feed", parameters, "POST");
   	   			  if (response == null || response.equals("") ||
   	   			        response.equals("false")) {
   	   				return -1;
   	   			  }
   	   			  else {
   	   				return 1;
   	   			  }
   		          }catch (Exception e){
   			       e.printStackTrace();
                   return -404;
   		         }		        
				
			}
			return -1;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		    if(result == 1){
		    	//Log.i////System.out.println("####", "Posted to you wall");
		    }else{
		    	//Log.i////System.out.println("####", "Some Stupid Error Occured");
		    }
		    	
		}
        // Restore Credential
    	public boolean restoreCredentials(Facebook facebook) {
        	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PocketTrader.KEY, Context.MODE_PRIVATE);
        	facebook.setAccessToken(sharedPreferences.getString(PocketTrader.TOKEN, null));
        	facebook.setAccessExpires(sharedPreferences.getLong(PocketTrader.EXPIRES, 0));
        	return facebook.isSessionValid();
    	}		
	}

}
