package com.techFist.sellMyStuff.Commons;

import java.util.ArrayList;
import com.techFist.sellMyStuff.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class MessageReadAdapter extends BaseAdapter {




	private TextView message;
	@SuppressWarnings("unused")
	private Context mContext;
	private TextView postedBy;
	private ArrayList<Message> msgList;
	public MessageReadAdapter(ArrayList<Message> msgList,Context context){
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

          message = (TextView)reuse.findViewById(R.id.msg_read_body);        	   
          postedBy = (TextView)reuse.findViewById(R.id.msg_read_posted_by);
          //postedDate = (TextView)reuse.findViewById(R.id.msg_read_posted_date);
          
          final Message msg = msgList.get(position);
         
	      // Refreshing Values

          postedBy.setText(msg.getPostedFrom());
          //postedDate.setText(msg.getPostedDate());
          message.setText(msg.getMessage());
          
          return reuse;
        }
        else
        {
     	  ViewGroup item = getViewGroup(reuse, parent);
     	  message = (TextView)item.findViewById(R.id.msg_read_body);        	   
          postedBy = (TextView)item.findViewById(R.id.msg_read_posted_by);
          //postedDate = (TextView)item.findViewById(R.id.msg_read_posted_date); 

          
          final Message msg = msgList.get(position);
          
	      // Refreshing Values
          postedBy.setText(msg.getPostedFrom());
          //postedDate.setText(msg.getPostedDate());
          message.setText(msg.getMessage());
          return item;

        }   
	}
	private ViewGroup getViewGroup(View reuse,ViewGroup parent){
		if(reuse instanceof ViewGroup)
			return (ViewGroup)reuse;
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup item = (ViewGroup)inflater.inflate(R.layout.message_read_detail_cell,null);
		return item;
        
   }

}
