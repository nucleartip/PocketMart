package com.techFist.sellMyStuff.Commons;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdView;
import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;

public class ReadMessageActivity extends PocketActivity {

    
	private ListView msgList;
	private EditText replyContent;
	private ImageButton sendMsg;
	private ViewGroup waitBar;
	private Context context;
	private Bundle formData;
	private HttpClient PostClient;
	private HttpPost httpPost;	
	private HttpResponse resp;	
	private AsyncTask<Bundle,Integer,Integer> msgTask;
	private ArrayList<Message> msgListAdapter;
	private MessageReadAdapter adapter;
	static final String    SERVER    = "http://sms-techfist.appspot.com";		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        // Adding Custom header
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);		
	    setContentView(R.layout.message_read_view);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header_profile);
        // Fetching Extras
        //Register a Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,ReadMessageActivity.class));

        // Initializing Objects
        msgList = (ListView)findViewById(R.id.msg_send_list);
        replyContent = (EditText)findViewById(R.id.msg_reply_content);
        sendMsg = (ImageButton)findViewById(R.id.msg_send_reply);
        waitBar = (ViewGroup)findViewById(R.id.app_location_wait);
        this.context = this;
        sendMsg.setEnabled(false);
        // Setting up Listeners
        sendMsg.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String msg = replyContent.getText().toString();
				if(msg != "" && msg != null){
					replyContent.setEnabled(false);
					sendMsg.setEnabled(false);
					MessageHandler hdl = MessageHandler.getInstance(context);
					Bundle b = new Bundle();
					b.putString("EmailTo", formData.getString("EmailFrom"));
					b.putString("EmailFrom", PocketTrader.user.getUserEmail());
					b.putString("Message", msg);
					String subject = "Re:" + formData.getString("Subject");
					b.putString("Subject", subject);
					replyContent.setText("");
					hdl.postMessage(b,replyContent,sendMsg,msgListAdapter,adapter,msgList);
					
					
				}else{
					Toast.makeText(context,"Please enter message.", Toast.LENGTH_SHORT).show();
				}
			}
        	
        });
    }
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		sendMsg.setEnabled(false);
	    if(getIntent()!=null){
	        formData = getIntent().getExtras();
	        waitBar.setVisibility(View.VISIBLE);
	        msgTask = new GetMyMessage().execute(new Bundle());
	        sendMsg.setEnabled(true);
	    }	    
        
	}
   @Override
   protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
    if(msgTask != null)
    	msgTask.cancel(true);
   
   }
	// Method, Pushes Home Screen
	public void pushHomeScreen(View v){
	    if(msgTask != null)
	    msgTask.cancel(true);
	    
		Intent intent = new Intent(context,PocketTrader.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	 // Push My Profile Screen
	 public void pushMyProfile(View v){
		    if(msgTask != null)
			    msgTask.cancel(true);
		 Intent intent = new Intent(context,MyProfileScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);		 
	 
	 }	
   class GetMyMessage extends AsyncTask<Bundle,Integer,Integer>{
		
	     public GetMyMessage(){
	     	 httpPost = new HttpPost();
	 		 PostClient = new DefaultHttpClient();		    	 
	 		 msgListAdapter = new ArrayList<Message>();
	     }
		 
		 @Override
		  protected Integer doInBackground(Bundle... params) {
			   try{		
			     httpPost = new HttpPost(SERVER+"/chainMessage");
			     ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			     data.add(new BasicNameValuePair("EmailTo",PocketTrader.user.getUserEmail()));
			     data.add(new BasicNameValuePair("EmailFrom",formData.getString("EmailFrom")));
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
		             Collections.sort(msgListAdapter);
		            		 
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
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			waitBar.setVisibility(View.INVISIBLE);
		 }
			@Override
			protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			waitBar.setVisibility(View.INVISIBLE);
			// Re populating View's on Dialog;
			    // Service Not Available
			    if(result == -404){
                   // Internal Error
			    	adapter= new MessageReadAdapter(msgListAdapter,context);
			    }
			    // No Messages
			    if(result == -1){
                   // No Messages
			       adapter= new MessageReadAdapter(msgListAdapter,context);
			    }	 
			    
			    if(result == 1 && msgListAdapter.size() >0){
			    	// Messages fetched
			    	// Setting up adapter for Bids
		    		adapter= new MessageReadAdapter(msgListAdapter,context);
		    		msgList.setAdapter(adapter);
		    		msgList.refreshDrawableState();
		    		//msgList.refreshDrawableState();
		    		
			    }
			    	
			}// On Post Execute	
		  } // Get My Messages


} 	 
	

