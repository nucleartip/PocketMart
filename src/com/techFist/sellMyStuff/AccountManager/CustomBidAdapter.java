package com.techFist.sellMyStuff.AccountManager;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.Commons.Bid;


public class CustomBidAdapter extends BaseAdapter{

		

		private TextView quote;
		private TextView postedBy;
		private TextView postedDate;
		private TextView bidProduct;
		private ArrayList<Bid> bidList;
		public CustomBidAdapter(ArrayList<Bid> bidList){
           this.bidList = bidList;

		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bidList.size();
		}

		@Override
		public Bid getItem(int position) {
			// TODO Auto-generated method stub
			return bidList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View reuse, ViewGroup parent) {
			// TODO Auto-generated method stub
	        if(reuse != null)
	        {

	          quote = (TextView)reuse.findViewById(R.id.bid_quote);        	   
	          postedBy = (TextView)reuse.findViewById(R.id.bid_qouted_by);
	          postedDate = (TextView)reuse.findViewById(R.id.bid_quoted_date);  
	          bidProduct = (TextView)reuse.findViewById(R.id.bid_product);
 	     	  final Bid obj = bidList.get(position);
 	     	  quote.setText(obj.getQuote());
 	     	  postedBy.setText("Posted By : " + obj.getPostedBy());
 	     	  postedDate.setText("Date : " +obj.getPostedDate().split("UTC")[0]);
              String productName = obj.getProductName();
              productName = (productName== "null")?"":productName;
              
 	     	  bidProduct.setText(productName);

	          return reuse;
	        }
	        else
	        {
	     	  ViewGroup item = getViewGroup(reuse, parent);
	          quote = (TextView)item.findViewById(R.id.bid_quote);        	   
	          postedBy = (TextView)item.findViewById(R.id.bid_qouted_by);
	          postedDate = (TextView)item.findViewById(R.id.bid_quoted_date); 
	          bidProduct = (TextView)item.findViewById(R.id.bid_product);
 	     	  final Bid obj = bidList.get(position);
 	     	  quote.setText(obj.getQuote());
 	     	  postedBy.setText("Posted By : " + obj.getPostedBy());
 	     	  postedDate.setText("Date : " +obj.getPostedDate().split("UTC")[0]);
 	     	  String productName = obj.getProductName();
 	     	  productName = (productName== "null")?"":productName;
 	     	  bidProduct.setText(productName);
	          return item;           
	        }

		}
		
		private ViewGroup getViewGroup(View reuse,ViewGroup parent){
			if(reuse instanceof ViewGroup)
				return (ViewGroup)reuse;
			Context context = parent.getContext();
			LayoutInflater inflater = LayoutInflater.from(context);
			ViewGroup item = (ViewGroup)inflater.inflate(R.layout.bid_detail_cell,null);
			return item;
	        
	    
		}
	 
}