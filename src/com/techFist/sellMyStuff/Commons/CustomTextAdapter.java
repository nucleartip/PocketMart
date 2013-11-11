package com.techFist.sellMyStuff.Commons;



import com.techFist.sellMyStuff.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomTextAdapter extends BaseAdapter{
	
	private ImageView ApplicationImageCell;
	private TextView ApplicationTextCell;
	private String[] menu;
	public CustomTextAdapter( String[] strings){
		this.menu = strings;
	    
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return menu.length;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return menu[position];
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
     	   
        	ApplicationImageCell = (ImageView)reuse.findViewById(R.id.application_menu_image);
        	ApplicationTextCell = (TextView)reuse.findViewById(R.id.application_menu_item);  
        	ApplicationTextCell.setText(getItem(position));
        	String option = getItem(position);
        	if(option.equals("Sell a Product"))
        		ApplicationImageCell.setImageResource(R.drawable.app_sell);
        	if(option.equals("Buy a Product"))
        		ApplicationImageCell.setImageResource(R.drawable.app_buy);
        	if(option.equals("All Products"))
        		ApplicationImageCell.setImageResource(R.drawable.app_all);
        	if(option.equals("Hot Deals"))
        		ApplicationImageCell.setImageResource(R.drawable.app_deal);
        	if(option.equals("My Account"))
        		ApplicationImageCell.setImageResource(R.drawable.app_account);        	
            return reuse;
        }
        else
        {
     	  ViewGroup item = getViewGroup(reuse, parent);
     	  ApplicationImageCell = (ImageView)item.findViewById(R.id.application_menu_image);
     	  ApplicationTextCell = (TextView)item.findViewById(R.id.application_menu_item);        	   
      	  ApplicationTextCell.setText(getItem(position));
      	String option = getItem(position);
      	if(option.equals("Sell a Product"))
      		ApplicationImageCell.setImageResource(R.drawable.app_sell);
      	if(option.equals("Buy a Product"))
      		ApplicationImageCell.setImageResource(R.drawable.app_buy);
      	if(option.equals("All Products"))
      		ApplicationImageCell.setImageResource(R.drawable.app_all);
      	if(option.equals("Hot Deals"))
      		ApplicationImageCell.setImageResource(R.drawable.app_deal);
      	if(option.equals("My Account"))
      		ApplicationImageCell.setImageResource(R.drawable.app_account);       	  
          return item;           
        }

	}
	
	private ViewGroup getViewGroup(View reuse,ViewGroup parent){
		if(reuse instanceof ViewGroup)
			return (ViewGroup)reuse;
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup item = (ViewGroup)inflater.inflate(R.layout.application_option_list_item,null);
		return item;
        
    
	}
}
