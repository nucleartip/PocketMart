package com.techFist.sellMyStuff.Commons;


import java.util.ArrayList;
import com.techFist.sellMyStuff.ImageLoader;
import com.techFist.sellMyStuff.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomPoductList extends BaseAdapter {


	
	private int mGalleryItemBackground;
	private TextView ApplicationTextCell;
	private TextView ApplicationPriceCell;
	private TextView ApplicationDateCell;
	@SuppressWarnings("unused")
	private Context mContext;
    public ImageLoader imageLoader; 
    	
	private ArrayList<Product> productList;
	public CustomPoductList(ArrayList<Product> productList,Context context){
        this.productList = productList;
        mContext = context;
        imageLoader=new ImageLoader(context.getApplicationContext());
        
	}
	public void refreshData(ArrayList<Product> productList){
		this.productList = productList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return productList.size();
	}

	@Override
	public Product getItem(int position) {
		// TODO Auto-generated method stub
		return productList.get(position);
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

          final ImageView ApplicationImageCell = (ImageView)reuse.findViewById(R.id.product_list_image);
	     	  ApplicationTextCell = (TextView)reuse.findViewById(R.id.product_list_title);        	   
	     	  ApplicationPriceCell = (TextView)reuse.findViewById(R.id.product_list_price);
	     	  ApplicationDateCell = (TextView)reuse.findViewById(R.id.product_list_date);  
	     	  final Product obj = productList.get(position);
	     	  ApplicationTextCell.setText(obj.getProductName());
	     	  ApplicationPriceCell.setText(obj.getProductPrice());
	     	  ApplicationDateCell.setText("Posted : " +obj.getProductPostedDate());
	     	  ApplicationImageCell.setBackgroundResource(mGalleryItemBackground);
	     	  // Checking for Available Image Url
	     	  if (!obj.isImageAvailable())
	     		ApplicationImageCell.setImageResource(R.drawable.image_temp); 
	     	  else{
	     	        imageLoader.DisplayImage(obj.getProductImages().get(0), ApplicationImageCell);	     		  
	     	  }
 
          return reuse;
        }
        else
        {
     	  ViewGroup item = getViewGroup(reuse, parent);
     	  final ImageView ApplicationImageCell = (ImageView)item.findViewById(R.id.product_list_image);
     	  ApplicationTextCell = (TextView)item.findViewById(R.id.product_list_title);        	   
     	  ApplicationPriceCell = (TextView)item.findViewById(R.id.product_list_price);
     	  ApplicationDateCell = (TextView)item.findViewById(R.id.product_list_date);
	     	  final Product obj = productList.get(position);
	     	  ApplicationTextCell.setText(obj.getProductName());
	     	  ApplicationPriceCell.setText(obj.getProductPrice());
	     	  ApplicationDateCell.setText("Posted : "+ obj.getProductPostedDate());
	     	  ApplicationImageCell.setBackgroundResource(mGalleryItemBackground);
	     	  // Checking for Available Image Url
	     	  if (!obj.isImageAvailable())
	     		ApplicationImageCell.setImageResource(R.drawable.image_temp); 
	     	  else{
	     		  imageLoader.DisplayImage(obj.getProductImages().get(0), ApplicationImageCell);	     		  
	     	  }
           return item;   
        }

	}
	
	private ViewGroup getViewGroup(View reuse,ViewGroup parent){
		if(reuse instanceof ViewGroup)
			return (ViewGroup)reuse;
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup item = (ViewGroup)inflater.inflate(R.layout.product_list_cell,null);
		return item;
        
    
	}

}
