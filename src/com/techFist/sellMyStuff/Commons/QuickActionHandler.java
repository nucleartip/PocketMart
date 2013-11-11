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
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.QuickAction.ActionItem;
import com.techFist.sellMyStuff.QuickAction.QuickAction;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class QuickActionHandler {
  
	private static QuickActionHandler quickHandler;
	private Context context;
	private QuickAction mQuickAction;
    private CustomDialog customDialog;
	private HttpClient PostClient;
	private HttpPost httpPost;	
	private HttpResponse resp;	
	private AsyncTask<Bundle,Integer,Integer> deleteTask;
	static final String    SERVER    = "http://sms-techfist.appspot.com";
	 
	private QuickActionHandler(Context context){
		this.context = context;
        this.context = context;
    	this.httpPost = new HttpPost();
		this.PostClient = new DefaultHttpClient();			
	}	
	public static  QuickActionHandler getInstance(Context context){
		 if(quickHandler != null){
			 quickHandler.setContext(context);
			 return quickHandler;
		 }else{
			 quickHandler = new QuickActionHandler(context);
			 return quickHandler;
		 }		
	}
	 private void setContext(Context context){
		 this.context = context;
	 }
	
	 public QuickAction getQuickActionWithoutPhone(final Context context,final Bid currentBidSelection){
		 this.context = context;
		 // Initializing Quick Action
		 mQuickAction = new QuickAction(context);
		 // Initializing Action Items
		 Resources r = context.getResources();
	     final ActionItem messageQuick = new ActionItem(0, "Message", r.getDrawable(R.drawable.quick_message));
	     final ActionItem mailQuick 	= new ActionItem(1, "Mail", r.getDrawable(R.drawable.quick_email)); 		
	     final ActionItem deleteQuick 	= new ActionItem(3, "Delete", r.getDrawable(R.drawable.quick_delete)); 
	     // Adding Action Item		 
	     mQuickAction.addActionItem(messageQuick);
	     mQuickAction.addActionItem(mailQuick);
	     mQuickAction.addActionItem(deleteQuick);	

	  	 //setup the action item click listener
	  	 mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
	  	 @Override
	  	 public void onItemClick(QuickAction quickAction, int pos, int actionId) {
	  		Toast.makeText(context, "Action Id:" + actionId, Toast.LENGTH_SHORT).show();		
	  		 if (actionId == 0) { //Add item selected
	  			 
	 				 if(currentBidSelection != null){  
	  			   Intent intent  = new Intent(context,ReadMessageActivity.class);
	 			       Bundle b = new Bundle();
	 				
	 				   b.putString("EmailFrom", currentBidSelection.getPostedBy());
	 				   b.putString("Subject", "");
	 				   intent.putExtras(b);
	 				   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 				   context.startActivity(intent);  
	 				 }else{
	 					 Toast.makeText(context,"Unable to Process.", Toast.LENGTH_SHORT).show();
	 				 }
	  		 }
	  		 if(actionId == 1){
	  			 //Toast.makeText(getApplicationContext(), "PT Email", Toast.LENGTH_SHORT).show();   
	  			 customDialog = new CustomDialog(context,"Send","","");
	  			 customDialog.getSendEmailDialog("Send Email", currentBidSelection.getPostedBy(), currentBidSelection.getEmail(), context).show();
	  			 
	  		 }
	  		if(actionId == 3) {
	  			 //Toast.makeText(getApplicationContext(), "PT Delete", Toast.LENGTH_SHORT).show(); 
	  			 Bundle b = new Bundle();
	  			 b.putString("Key", currentBidSelection.getKey());
	  			 deleteTask = new DeleteBid().execute(b);	  			 
	  		  }	
	  		}
	  		}); 
	  	
		 
		 return mQuickAction;	     
	 }
	 
	 public QuickAction getQuickActionWithPhone(final Context context,final Bid currentBidSelection){
		 this.context = context;
		 // Initializing Quick Action
		 mQuickAction = new QuickAction(context);
		 // Initializing Action Items
		 Resources r = context.getResources();
	     final ActionItem messageQuick = new ActionItem(0, "Message", r.getDrawable(R.drawable.quick_message));
	     final ActionItem mailQuick 	= new ActionItem(1, "Mail", r.getDrawable(R.drawable.quick_email)); 		
	     final ActionItem phoneQuick 	= new ActionItem(2, "Call",  r.getDrawable(R.drawable.quick_phone));
	     final ActionItem deleteQuick 	= new ActionItem(3, "Delete", r.getDrawable(R.drawable.quick_delete)); 
	     // Adding Action Item		 
	     mQuickAction.addActionItem(messageQuick);
	     mQuickAction.addActionItem(mailQuick);
	     mQuickAction.addActionItem(deleteQuick);	
	     mQuickAction.addActionItem(phoneQuick);		 

	  	 //setup the action item click listener
	  	 mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
	  	 @Override
	  	 public void onItemClick(QuickAction quickAction, int pos, int actionId) {

	  		 if (actionId == 0) { //Add item selected
	  			 
	 				 if(currentBidSelection != null){  
	  			   Intent intent  = new Intent(context,ReadMessageActivity.class);
	 			       Bundle b = new Bundle();
	 				
	 				   b.putString("EmailFrom", currentBidSelection.getPostedBy());
	 				   b.putString("Subject", "");
	 				   intent.putExtras(b);
	 				   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 				   context.startActivity(intent);  
	 				 }else{
	 					 Toast.makeText(context,"Unable to Process.", Toast.LENGTH_SHORT).show();
	 				 }
	  		 }
	  		 if(actionId == 1){
	  			 //Toast.makeText(getApplicationContext(), "PT Email", Toast.LENGTH_SHORT).show();   
	  			 customDialog = new CustomDialog(context,"Send","","");
	  			 customDialog.getSendEmailDialog("Send Email", currentBidSelection.getPostedBy(), currentBidSelection.getEmail(), context).show();
	  			 
	  		 }
	  		 if(actionId == 2){
	  			 //Toast.makeText(getApplicationContext(), "PT Call", Toast.LENGTH_SHORT).show(); 
	  			 String url = "tel:"+currentBidSelection.getPostedByPhone();
	  			 Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse(url));
	  			 intent.putExtra(android.content.Intent.EXTRA_PHONE_NUMBER, currentBidSelection.getPostedByPhone());
	  			 context.startActivity(intent);
	  			 
	  		 }
	  		 if(actionId == 3) {
	  			 //Toast.makeText(getApplicationContext(), "PT Delete", Toast.LENGTH_SHORT).show(); 
	  			 Bundle b = new Bundle();
	  			 b.putString("Key", currentBidSelection.getKey());
	  			 deleteTask = new DeleteBid().execute(b);
	  		  }	
	  		}
	  		}); 
	  	
		 
		 return mQuickAction;
	 }
	public void quickActionHandler(Bid bid,String action){
 		
	  if(bid != null && action != ""){	
		if (action.equals("Message")) { //Add item selected
			 Intent intent  = new Intent(context,ReadMessageActivity.class);
			 Bundle b = new Bundle();
			 b.putString("EmailFrom", bid.getEmail());
			 b.putString("Subject", "");
			 intent.putExtras(b);
			 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 context.startActivity(intent);  
		 }
		 if(action.equals("Mail")){
			 //Toast.makeText(getApplicationContext(), "PT Email", Toast.LENGTH_SHORT).show();   
			 customDialog = new CustomDialog(context,"Send","","");
			 customDialog.getSendEmailDialog("Send Email", bid.getPostedBy(), bid.getEmail(), context).show();
			 
		 }
		 if(action.equals("Call")){
			 //Toast.makeText(getApplicationContext(), "PT Call", Toast.LENGTH_SHORT).show(); 
			 String url = "tel:"+bid.getPostedByPhone();
			 Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse(url));
			 intent.putExtra(android.content.Intent.EXTRA_PHONE_NUMBER, bid.getPostedByPhone());
			 context.startActivity(intent);
			 
		 }
		 if(action.equals("Delete")) {
			 //Toast.makeText(getApplicationContext(), "PT Delete", Toast.LENGTH_SHORT).show(); 
			 Bundle b = new Bundle();
			 b.putString("Key", bid.getKey());
			 deleteTask = new DeleteBid().execute(b);
		  }	
	  }else{
		  Toast.makeText(context,R.string.valid_process, Toast.LENGTH_SHORT).show();
	  }
		 
		
	}
	 
    protected class DeleteBid extends AsyncTask<Bundle,Integer,Integer>{
 		
    	 @Override
	    protected Integer doInBackground(Bundle... arg0) {
		   // TODO Auto-generated method stub
		   Bundle formData = arg0[0];
		    try{
	   	        httpPost = new HttpPost(SERVER+"/deleteBid");

			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			    data.add(new BasicNameValuePair("Key",formData.getString("Key")));		    
		        httpPost.setEntity(new UrlEncodedFormEntity(data));	
		        resp = PostClient.execute(httpPost);
	   		// Product has Been Deleted
	   		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	   			return 1;
	   		}	    		
	   		// Datastore Exception, Product not Saved Ask user to Try again
	   		if(resp.getStatusLine().getStatusCode() == 10000){
	   			return -500;
	   		}
	   		// Service Error
	   		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
	   			return -404;
	   		}	        
		        
			}
	       catch(Exception e){
	    	   e.printStackTrace();
	     	  return -404;
	       }
	       return -404; 			

		 }
		@Override
	    protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
	        if(result == 1){
	      	  Toast.makeText(context, "Bid deleted", Toast.LENGTH_SHORT).show();
            }else
	      	Toast.makeText(context, "Unable to Process.", Toast.LENGTH_SHORT).show();		
		
		}// Delete Bid	 
  } // Async Task
	
} // Main Class