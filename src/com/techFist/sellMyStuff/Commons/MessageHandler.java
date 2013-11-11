package com.techFist.sellMyStuff.Commons;

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
import org.json.JSONArray;
import org.json.JSONObject;

import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MessageHandler {
     private static MessageHandler handler;
	 private HttpClient PostClient;
	 private HttpPost httpPost;	
	 private HttpResponse resp;	
	 static final String    SERVER    = "http://sms-techfist.appspot.com";	
	 private Context context;
	 private static AsyncTask<Bundle,Integer,Integer> postMessage;	
	 private AsyncTask<Bundle,Integer,Integer> getMessages;
	 private Dialog d;
	 private View msgView;
     private EditText text;
     private ImageButton btn;
     private ArrayList<Message> msgList;
     private ArrayList<Message> msgListAdapter;
     private MessageReadAdapter adapter;
     private ListView msgListView;
	 private MessageHandler(Context context){
         this.context = context;
     	 this.httpPost = new HttpPost();
 		 this.PostClient = new DefaultHttpClient();	

	 }
	 public static MessageHandler getInstance(Context context){
		 
		 if(handler != null){
			 handler.setContext(context);
			 return handler;
		 }else{
			 handler = new MessageHandler(context);
			 return handler;
		 }
		 
	 }
	 private void setContext(Context context){
		 this.context = context;
	 }
	 
	 public boolean postMessage(Bundle b,EditText text,ImageButton btn,ArrayList<Message> msgList,
			 MessageReadAdapter adapter,ListView msgListView){
		 this.text = text;
		 this.btn = btn;
		 this.msgList = msgList;
		 this.adapter = adapter;
		 this.msgListView = msgListView;
		 postMessage = new PostMessage().execute(b);
	     return true;
	 }
	 public void showMyMessageList(Context context){
	  // Populating  a Dialog Box, for Temp Display
	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	  dialog.setTitle("Messages");
	  dialog.setCancelable(true);
	  dialog.setIcon(R.drawable.header_msg_light);
	  LayoutInflater li;
	  View msgView;
	  li = LayoutInflater.from(context);
	  msgView = li.inflate(R.layout.popup_layout, null);	
	  this.msgView = msgView;
	  ListView msgList = (ListView)msgView.findViewById(R.id.messages_list);
	  msgList.setLayoutParams(new  LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	  msgList.setVisibility(View.INVISIBLE);
	  
      dialog.setView(msgView);
      dialog.setOnCancelListener(new OnCancelListener(){

		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			
			if(getMessages != null){
				getMessages.cancel(true);
			}
		}
    	  
      });
      d = dialog.create();
      d.show();
      getMessages = new GetMyMessage().execute(new Bundle());
     }
	 
	 
	 class GetMyMessage extends AsyncTask<Bundle,Integer,Integer>{
		
	     @Override
		  protected Integer doInBackground(Bundle... params) {
			   try{	
				 msgListAdapter = new ArrayList<Message>();
			     httpPost = new HttpPost(SERVER+"/getMessages");
			     ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			     data.add(new BasicNameValuePair("EmailTo",PocketTrader.user.getUserEmail()));		    
			     data.add(new BasicNameValuePair("Type","GET"));
		         httpPost.setEntity(new UrlEncodedFormEntity(data));	
		         resp = PostClient.execute(httpPost);
		         // Datastore Exception, Product not Saved Ask user to Try again
		    	 if(resp.getStatusLine().getStatusCode() == 10000){
		    			return -500; // Error
		    	 }
		    	 // Service Error
		    	 if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
		    			return -404; // Error
		    	 }         
			     if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
				           return -1; // No Messages Found
				 }     
			     if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				     BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		  	         StringBuilder sb = new StringBuilder();
		             String line = null;
		             while((line = reader.readLine())!=null){
		             	sb = sb.append(line);
		             }	
		             String emailTO = PocketTrader.user.getUserEmail();
		             JSONObject jo = new JSONObject(sb.toString());
		             JSONArray ja = jo.getJSONArray("Data"); 
		             for (int i = 0; i < ja.length(); i++) {
		      	       JSONObject jo1 = ja.getJSONObject(i);                    
		               Message obj = new Message((String)jo1.getString("Key"),(String)jo1.getString("Message"),emailTO,
		    		           (String)jo1.getString("EmailFrom"),(String)jo1.getString("FromPhone"),(String)jo1.getString("FromPhoneOption"),
		    		           (String)jo1.getString("MessageState"),(String)jo1.getString("Read"),(String)jo1.getString("Subject"),
		    		           (String)jo1.getString("PostedBy"),(String)jo1.getString("Date"));  
		             
		               msgListAdapter.add(obj);

		             }
		             return 1;             
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
			// Re populating View's on Dialog;
			if(msgView != null && d.isShowing()){
			   ListView msgList = (ListView)msgView.findViewById(R.id.messages_list);
			   ProgressBar pbar = (ProgressBar)msgView.findViewById(R.id.msg_progress_bar);
			   ViewGroup msgGroup = (ViewGroup)msgView.findViewById(R.id.msg_progressGroup);
			   TextView msgText = (TextView)msgView.findViewById(R.id.msg_no_msg_found);
			   ImageView msgImg = (ImageView)msgView.findViewById(R.id.msg_indicator);
				
			    // Service Not Available
			    if(result == -404){
                    // Internal Error
			    	pbar.setVisibility(View.INVISIBLE);
			    	pbar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
			    	pbar.setEnabled(false);
			    	msgImg.setImageResource(R.drawable.network_error);
			    	msgText.setText("Unable to Fetch List");
			    	msgText.setLayoutParams(new  LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));			    	
			    }
			    // No Messages
			    if(result == -1){
                    // No Messages
			    	pbar.setVisibility(View.INVISIBLE);
			    	pbar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
			    	pbar.setEnabled(false);
			    	msgImg.setImageResource(R.drawable.no_data_found);
			    	msgText.setText("No Messages yet");
			    	msgText.setLayoutParams(new  LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));			    	
			    }	 
			    
			    if(result == 1 && msgListAdapter.size() >0){
			    	// Messages fetched
		    		pbar.setVisibility(View.INVISIBLE);
		    		pbar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
		    		pbar.setEnabled(false);
		    		msgImg.setLayoutParams(new LinearLayout.LayoutParams(0,0));
		    		msgImg.setEnabled(false);
		    		msgImg.setVisibility(View.INVISIBLE);		    	
		    		msgText.setVisibility(View.INVISIBLE);
		    		msgText.setText("No Messages yet");
		    		msgText.setLayoutParams(new  LinearLayout.LayoutParams(0,0));	
		    		msgGroup.setLayoutParams(new  LinearLayout.LayoutParams(0,0));

			    	// Setting up adapter for Bids
		    		final MessageAdapter adapter= new MessageAdapter(msgListAdapter,context);
		    		msgList.setAdapter(adapter);
		    		msgList.setLayoutParams(new  LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		    		msgList.setVisibility(View.VISIBLE);
		    		msgList.setEnabled(true);
		    		msgList.refreshDrawableState();
		    		//msgList.refreshDrawableState();
		    		msgList.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> adapter, View view,
								int position, long arg3) {
							// TODO Auto-generated method stub
							Intent intent  = new Intent(context,ReadMessageActivity.class);
							Bundle b = new Bundle();
							Message msg = (Message)adapter.getItemAtPosition(position);
							b.putString("EmailFrom", msg.getEmailFrom());
							b.putString("Subject", msg.getSubject());
							intent.putExtras(b);
							context.startActivity(intent);
							d.dismiss();
				}
		    			
		    		});
			    }
			    	
			}else{
				d.dismiss();
				Toast.makeText(context,"Unable to Fetch Messages. Please Try Again Later", Toast.LENGTH_SHORT).show();
			}
		  } // On Post Execute	
	} // Get My Messages	 

	  
	 class PostMessage extends AsyncTask<Bundle,Integer,Integer>{
        private Message msg;
		@Override
	    protected Integer doInBackground(Bundle... arg0) {
		   // TODO Auto-generated method stub
		   Bundle formData = arg0[0];
		    try{
	   	        httpPost = new HttpPost(SERVER+"/getMessages");

			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			    data.add(new BasicNameValuePair("EmailTo",formData.getString("EmailTo")));		    
			    data.add(new BasicNameValuePair("EmailFrom",formData.getString("EmailFrom")));
			    data.add(new BasicNameValuePair("Message",formData.getString("Message")));
			    data.add(new BasicNameValuePair("Subject",formData.getString("Subject")));
			    msg = new Message("",formData.getString("Message"),formData.getString("EmailTo"),
			    		formData.getString("EmailFrom"),PocketTrader.user.getUserPhone(),String.valueOf(PocketTrader.user.getPhoneOption()),
			    		"","",formData.getString("Subject"),PocketTrader.user.getUserName(),"");
			    data.add(new BasicNameValuePair("Type","POST"));
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
	      	  Toast.makeText(context, "Personalized Message Posted Succesfully !", Toast.LENGTH_SHORT).show();
	      	  if(msgList != null && adapter!= null){
	      		  msgList.add(msg);
	      	      adapter.notifyDataSetChanged();
	      	      msgListView.setSelection(msgList.size()-1);
	      	  }
	          if(text != null){
	        	  text.setEnabled(true);
	        	  text.setText("");
	          }
	        	  
	          if(btn != null)
	        	  btn.setEnabled(true);
	        }else
	      	  Toast.makeText(context, "Opps this is embarissing, an error occured !", Toast.LENGTH_SHORT).show();		
			}
	   } // Post Message
}
