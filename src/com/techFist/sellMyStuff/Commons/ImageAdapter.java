package com.techFist.sellMyStuff.Commons;

import java.util.ArrayList;

import com.techFist.sellMyStuff.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private boolean isResource = false;
	private boolean isBitmap = false;
    private int IMAGE_MAX_SIZE = 256;
	private int[] resourceID;
	private ArrayList<Uri> uri;
	private ArrayList<Bitmap> map;
	private Context context;
	
	public ImageAdapter(int[] resouceID,Context context){
		this.isResource = true;
		isBitmap = false;
		this.resourceID = resouceID;
		this.context = context;
        TypedArray attr = context.obtainStyledAttributes(R.styleable.ImageGallery);
        mGalleryItemBackground = attr.getResourceId(
                R.styleable.ImageGallery_android_galleryItemBackground, 0);
        attr.recycle();		
	}
	public ImageAdapter(ArrayList<Uri> uri,Context context){
		this.isResource = false;
		this.isBitmap = false;
		this.uri = uri;
		this.context = context;
        TypedArray attr = context.obtainStyledAttributes(R.styleable.ImageGallery);
        mGalleryItemBackground = attr.getResourceId(
                R.styleable.ImageGallery_android_galleryItemBackground, 0);
        attr.recycle();	
	}
	public ImageAdapter(Context context,ArrayList<Bitmap> map){
		this.isBitmap = true;
		this.isResource = false;
		this.context = context;
		this.map = map;
        TypedArray attr = context.obtainStyledAttributes(R.styleable.ImageGallery);
        mGalleryItemBackground = attr.getResourceId(
                R.styleable.ImageGallery_android_galleryItemBackground, 0);
        attr.recycle();				
	}
	
	@Override
	public int getCount() {
		
		if(isResource && !isBitmap)
			return resourceID.length;
		else if(!isResource && !isBitmap)
			return uri.size();
		else
			return map.size();
		
	}

	@Override
	public Object getItem(int position) {

		if(isResource && !isBitmap)
			return new Integer(resourceID[position]);
		else if(!isResource && !isBitmap)
			return uri.get(position);
		else
			return map.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView view = new ImageView(context);
		if(isResource && !isBitmap){
			view.setImageResource(resourceID[position]);
			view.setLayoutParams(new Gallery.LayoutParams(256, 256));
			view.setScaleType(ImageView.ScaleType.FIT_XY);
	        view.setBackgroundResource(mGalleryItemBackground);
	        return view;
		}
		else if(!isResource && !isBitmap){
			view.setImageBitmap(shrinkImage((Uri)uri.get(position)));
			 ////System.out.println("#### URI of Image: " + uri.get(position).getPath());
			view.setLayoutParams(new Gallery.LayoutParams(256, 256));
			view.setScaleType(ImageView.ScaleType.FIT_XY);
	        view.setBackgroundResource(mGalleryItemBackground);			
			return view;
			
		}
		else{
			view.setImageBitmap(map.get(position));
			view.setLayoutParams(new Gallery.LayoutParams(256, 256));
			view.setScaleType(ImageView.ScaleType.FIT_XY);
	        view.setBackgroundResource(mGalleryItemBackground);			
			return view;			
		}
	}
	   private Bitmap shrinkImage(Uri uri){

	        
		   Bitmap b = null;
	       if(uri != null){ 
		    //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(uri.getPath(),o);
	        int scale = 1;
	        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
	            scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	        }

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        b =    BitmapFactory.decodeFile(uri.getPath(), o2);
	         
	       }
	    return b;


}
}
