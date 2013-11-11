package com.techFist.sellMyStuff.MapManager;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.Commons.Product;
public class BusinessOverlays extends ItemizedOverlay{

	private Context context;
	private Product product;
	private ArrayList<OverlayItem> mOverlays;
	private ArrayList<Product> productList;
	public BusinessOverlays(Drawable defaultMarker) {
		super(defaultMarker);
		// TODO Auto-generated constructor stub
	}
	public BusinessOverlays(Drawable defaultMarker, Context context,ArrayList<Product> productList) {
		  super(boundCenterBottom(defaultMarker));
		  this.context = context;
		  this.productList = productList;
		  mOverlays = new ArrayList<OverlayItem>();
		}	
	
	public BusinessOverlays(Drawable defaultMarker, Context context,Product product) {
		  super(boundCenterBottom(defaultMarker));
		  this.context = context;
		  this.product = product;
		  mOverlays = new ArrayList<OverlayItem>();
		}	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}	
	
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}
    @Override
    protected boolean onTap(int index) {

      if(index < size()){	  
    	Product product = productList.get(index);
		   //Bundle formData = new Bundle();
		   //formData.putParcelable("ProductDetail", product);
		   //Intent intent = new Intent(context,ProductDetailScreen.class);
		   //intent.putExtras(formData);
		   //context.startActivity(intent);
    	AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.test_layout_dummy, null);
        dialog.setView(v);
        dialog.setTitle("Send Notification");
        dialog.setPositiveButton("Send", null);
        dialog.create().show();
        
    	
      }	    	
    	
    	return true;
        
    }
}
