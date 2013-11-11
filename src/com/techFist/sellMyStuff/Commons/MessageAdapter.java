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



import android.content.Context;
import android.os.AsyncTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageAdapter extends BaseAdapter {


	private TextView subject;
	private TextView message;
	private Context mContext;
	private TextView postedBy;
	private TextView postedDate;	
	private ImageView delete;
	private ArrayList<Message> msgList;
	
	public MessageAdapter(ArrayList<Message> msgList,Context context){
		this.mContext = context;
		this.msgList = msgList;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return msgList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View reuse, ViewGroup parent) {
		// TODO Auto-generated method stub
        if(reuse != null)
        {

          subject = (TextView)reuse.findViewById(R.id.msg_subject);        	   
          postedBy = (TextView)reuse.findViewById(R.id.msg_posted_by);
          postedDate = (TextView)reuse.findViewById(R.id.msg_posted_date); 
          message = (TextView)reuse.findViewById(R.id.msg_body);
          delete = (ImageView)reuse.findViewById(R.id.msg_deleteMessage);
          
          final Message msg = msgList.get(position);
	      // Refreshing Values
          String msgSubject = msg.getPostedFrom() +"Wrote on -" +  msg.getPostedDate();
          subject.setText(msgSubject);
          postedBy.setText(msg.getPostedFrom());
          postedDate.setText(msg.getPostedDate());
          message.setText(msg.getMessage());
          
          delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new DeleteMessage().execute(msg.getKey(),String.valueOf(position));
			}
        	  
          }
          );
          
          
          
          return reuse;
        }
        else
        {
     	  ViewGroup item = getViewGroup(reuse, parent);
          subject = (TextView)item.findViewById(R.id.msg_subject);        	   
          postedBy = (TextView)item.findViewById(R.id.msg_posted_by);
          postedDate = (TextView)item.findViewById(R.id.msg_posted_date); 
          message = (TextView)item.findViewById(R.id.msg_body);
          delete = (ImageView)item.findViewById(R.id.msg_deleteMessage);
          
          final Message msg = msgList.get(position);
	      // Refreshing Values
          String msgSubject = msg.getPostedFrom() +" Wrote on -" +  msg.getPostedDate();
          subject.setText(msgSubject);
          postedBy.setText(msg.getPostedFrom());
          postedDate.setText(msg.getPostedDate());
          message.setText(msg.getMessage());
          
          delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new DeleteMessage().execute(msg.getKey(),String.valueOf(position));
			}
        	  
          }
          );
          return item;           
        }
        
	}
	private ViewGroup getViewGroup(View reuse,ViewGroup parent){
		if(reuse instanceof ViewGroup)
			return (ViewGroup)reuse;
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup item = (ViewGroup)inflater.inflate(R.layout.message_detail_cell,null);
		return item;
        
    
	}
	
	class DeleteMessage extends AsyncTask<String,Integer,Integer>{
		private HttpClient PostClient;
		private HttpPost httpPost;	
		private HttpResponse resp;
		private int currentPos;
		static final String    SERVER    = "http://sms-techfist.appspot.com";
		public DeleteMessage(){
	     	 this.httpPost = new HttpPost();
	 		 this.PostClient = new DefaultHttpClient();			
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			String key = params[0];
			currentPos = Integer.valueOf(params[1]);
			try{
			     httpPost = new HttpPost(SERVER+"/deleteMessage");
			     ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			     data.add(new BasicNameValuePair("Key",key));		    
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
			    	 return 1;
			     }
			}catch(Exception e){
				e.printStackTrace();
				return -404;
			}
			return -404;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		if(result != 1){
			Toast.makeText(mContext,"Unable to delete, Please Try Again.", Toast.LENGTH_SHORT).show();
		}
		else{
			msgList.remove(currentPos);
			notifyDataSetChanged();
		}
		}
	}
}
