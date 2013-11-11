package com.techFist.sellMyStuff;


import java.util.List;
import com.google.android.maps.GeoPoint;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class BMSLocationListner implements LocationListener{

	Activity myLocalActivity;
	int myLocalResourcesID;
	Geocoder geocoder;
	GeoPoint geopoint;
	List<Address> addresses;
	String TAG = "###BMSLocationListner";
	public BMSLocationListner(Activity c,int r)
	{
		this.myLocalActivity = c;
		this.myLocalResourcesID = r;
		geocoder = new Geocoder(myLocalActivity);
		
	}
	
	
	public void setEditTextView()
	{
		EditText localTextView = (EditText)myLocalActivity.findViewById(myLocalResourcesID);
	     for (Address address : addresses) {
	    	 localTextView.setText("hello");
	    	  
		      }		
	}
	
	@Override
	public void onLocationChanged(Location location) {
	    try {
	      
	      // Getting Address Using Geocoder	
	      
	      addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),5); //<10>
	       
          int latitude = (int)(location.getLatitude() * 1000000);
	      int longitude = (int)(location.getLongitude() * 1000000);
          // Getting Geo-Point
	      geopoint = new GeoPoint(latitude,longitude);
	      setEditTextView();
	    } catch (Exception e) {
	      Log.e("LocateMe", "Could not get Geocoder data", e);
	    }		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
