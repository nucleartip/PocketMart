package com.techFist.sellMyStuff;

import java.util.List;

import com.google.ads.AdView;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/* This activity represents, Sell Product Starting form
 * Here Location details are captured
 * Upon Success, Sell Product Main form is Called, Supplying data captured in Calling Activity
 */

public class SellStartForm extends PocketActivity {

	

	// Member Variables
    private LocationManager myLocationMgr;
    private AlertDialog alertNetworkDialog = null;
    private CustomDialog alert = null;
	private Context context;
    private LocationFinder locationFinder;
    private EditText locationBox;
    private Bundle formData;
    private ViewGroup locationWait;
    private boolean isLocationRequested = false;
    private AsyncTask locationTask;
    private TextView headerInfo;
	// Called through Search Dialog
    
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    } 
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	locationWait.setEnabled(false);
    	locationWait.setVisibility(ViewGroup.INVISIBLE);
    	locationBox.setEnabled(true);
    	if(isLocationRequested){
    		locationTask.cancel(true);
    	}else{
    		finish();
    	}
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        // Adding Custom header
        super.onCreate(savedInstanceState);  
        //Register a Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,SellStartForm.class));
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.start_selling_form);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header_info);
        context = this;
        formData = new Bundle();
	    locationWait = (ViewGroup)findViewById(R.id.sell_location_wait);
        // Fetching Form Objects
	    headerInfo = (TextView)findViewById(R.id.app_title_info);
        Button btnProceed = (Button)findViewById(R.id.proceed_selling);
        locationBox = (EditText)findViewById(R.id.sell_location_box);
        ImageButton btnFindLocation = (ImageButton)findViewById(R.id.sell_location_finder);
        // Checking for Location Providers
        checkNetworkStatus();
        // Search Dialog Call back
         if(getIntent()!=null){
        	 if(getIntent().getStringExtra("AddressValue") != null)
        	 {
        		 String flag = getIntent().getStringExtra("AddressValue");

        		 if(flag.equals("Suggestion")){
        			 

        			 String[] data = getIntent().getDataString().split("#t");
        			 formData.putString("Longitude", data[1]);
        			 formData.putString("Latitude", data[2]);
        			 formData.putString("City",data[3]);
        			 formData.putString("State", data[4]);
        			 formData.putString("Country", data[5]);
        			 formData.putString("Area", data[6]);
        			 formData.putString("PostalCode", data[7]);
        			 locationBox.setText("");
        			 locationBox.setText(data[0]);
        		 }
        		 else{
        			 formData.putString("Longitude", "");
        			 formData.putString("Latitude", ""); 
        			 formData.putString("City", "");
        			 locationBox.setText("");
        		 }  
        	 }
        		 
        		 
         }
        
        
        
        
        //Setting up Search Dialog
        locationBox.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PocketTrader.isSearchRequestedSS = true;
				onSearchRequested();
			}	
        });
        // Fetching and updating location
        btnFindLocation.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//customDialog = new CustomDialog(context,"","","Locating...");
				//waitDialog = customDialog.getWaitDialog("Please Wait");
				//waitDialog.show();
				locationWait.setVisibility(ViewGroup.VISIBLE);
				locationWait.setEnabled(true);
				locationBox.setEnabled(false);
				isLocationRequested = true;
				locationTask = new LocationFinderTask().execute(new Bundle());
			}
        	
        });
        //Proceeding to Next Activity, Sell product Main Form
        btnProceed.setOnClickListener(new OnClickListener(){
        @Override
	    public void onClick(View arg0) {
	            
        	    // Validating form
        	    String locationText = locationBox.getText().toString();

        	    if(locationText != "" && locationText != null && locationText != " " && locationText.length() > 0)
        	     {  	
        	        Intent intent = new Intent(context,SellMainForm.class);
        	        formData.putString("Address", locationText);
			 	    intent.putExtras(formData);
				    startActivityForResult(intent, PocketTrader.SELL_ACTIVITY_MAIN_FORM);
        	     }
        	    // Generating a Alert Toast 
        	    else{
        	    	Toast.makeText(context,"Please fill you Location Detail", Toast.LENGTH_LONG).show();
        	    }
            }
        });

	}
   
    // Supporting Inner Classes
	class LocationFinderTask extends AsyncTask{

		@Override
		protected Bundle doInBackground(Object... params) {
			// TODO Auto-generated method stub]
			Looper.prepare();
            Bundle b = (Bundle) params[0];
			boolean flag = false;
		    locationFinder = new LocationFinder();
		    Location location = null;
		    flag = locationFinder.getLocationScheduled(context);
		    int index = 0;
		    if(flag){
                while(flag){
    	    	     if(this.isCancelled()){
     	    	    	 flag = false;
     	    	     }
                	try {
         	    	     location = locationFinder.getMyLocation();
         	    	     if( location == null){
                         	 Thread.sleep(2000);
                         	 index = index + 1;
                          
                          }
         	    	     else{
         	    	    	 flag = false;
         	    	    	 b.putString("LocationGenerated", "YES");
         	    	    	 b.putParcelable("Location", location);}
         	    	     if(index > 20)
         	    	     {
         	    	    	 flag = false;
         	    	         b.putString("LocationGenerated", "NO");
         	    	     }

				       } 
			         catch(Exception e){
			        	     b.putString("LocationGenerated", "NO");
			         }
                }
		    }
		    return b;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			isLocationRequested = false;
			locationWait.setVisibility(ViewGroup.INVISIBLE);
			locationWait.setEnabled(false);	
			locationBox.setEnabled(true);			
		}
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			isLocationRequested = false;
			locationWait.setVisibility(ViewGroup.INVISIBLE);
			locationWait.setEnabled(false);	
			locationBox.setEnabled(true);
			Bundle b= (Bundle)result;
			Geocoder geocoder;
			List<Address> addresses;
			String status = b.getString("LocationGenerated");
			// Updating Location in text box
			if(status == "YES"){
			
			    Location loc = (Location) b.getParcelable("Location");

			    geocoder = new Geocoder(context);
			    try{
			    	addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(),5);
			    	locationBox.setText("");
			        locationBox.setText(formatAddress(addresses.get(0)));
			        // Setting up Location Parameters
       			    formData.putString("Longitude",String.valueOf(loc.getLongitude()));
       			    formData.putString("Latitude", String.valueOf(loc.getLatitude()));
       			    formData.putString("Area", addresses.get(0).getSubLocality());
       			    formData.putString("City",addresses.get(0).getLocality());
       			    formData.putString("State", addresses.get(0).getAdminArea());
       			    formData.putString("Country", addresses.get(0).getCountryName());
    		        int postalCode = -1;
                    if(addresses.get(0).getPostalCode() != null && addresses.get(0).getPostalCode() != "")
                    	postalCode = Integer.valueOf(addresses.get(0).getPostalCode());       			    
       			    formData.putString("PostalCode", String.valueOf(postalCode));
			        
			    }
			    catch(Exception e){
			    	// Error Occuerd, Popping Error Box
			    	e.printStackTrace();
			    	alert = new CustomDialog(context,getResources().getString(R.string.alert_ok_msg),
						getResources().getString(R.string.alert_cancel_msg),"Location cannot be determined");
			    	alert.getErrorDialog("Unexpected Error").show();
			    }
		    }
			// Popping up info
			else{
				alert = new CustomDialog(context,getResources().getString(R.string.alert_ok_msg),
						getResources().getString(R.string.alert_cancel_msg),"Location cannot be determined");
				alert.getInfoDialog("Unable to Determine").show();	   
			}
	    }
    }	
	// Method for Formatting Address,
	private String formatAddress(Address address){
		 
		String data = "";
		int maxAddressLines = address.getMaxAddressLineIndex();
		for(int i=0;i<maxAddressLines;i++){
			if(i==0)
				data = address.getAddressLine(i);
			else
			data = data + "," + address.getAddressLine(i);
			
		}
		
	    return data;
	}
	

	
	// User Generated Methods 
	void checkNetworkStatus(){
	       

		   myLocationMgr = (LocationManager)this.getSystemService(LOCATION_SERVICE); 
		   
		   if( !myLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
			   !myLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		   {
			   
			   // Popping Up Alert in case of No Network Provider.
			   alert = new CustomDialog(this,getResources().getString(R.string.alert_ok_msg),
			   getResources().getString(R.string.alert_later_msg),getResources().getString(R.string.no_location_provider));
			   alertNetworkDialog = alert.getSettingDialog(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS,"Location Service Diabled");
			   alertNetworkDialog.show();
		   
		   }

	   }    
	
	
   // Life Cycle Callbacks 
   @Override
   protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
    if(alertNetworkDialog != null){
    	alertNetworkDialog.dismiss();
    	alertNetworkDialog.cancel();
    	
    }
   }
   
   @Override
   protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume(); 
	if(PocketTrader.detail != null)
	 headerInfo.setText(PocketTrader.detail.getCity());
	
    if(alertNetworkDialog != null){
    	alertNetworkDialog.dismiss();
    	alertNetworkDialog.cancel();
    	
    }
    alertNetworkDialog = null;
	checkNetworkStatus();
	// Code for Auto populating form Location Details
	if(PocketTrader.detail != null){
		locationBox.setText("");
		locationBox.setText(PocketTrader.detail.getAddress());
		formData.putString("Longitude",String.valueOf(PocketTrader.detail.getLongitude()));
	    formData.putString("Latitude", String.valueOf(PocketTrader.detail.getLatitude()));
		formData.putString("Area", PocketTrader.detail.getArea());
		formData.putString("City",PocketTrader.detail.getCity());
		formData.putString("State", PocketTrader.detail.getState());
		formData.putString("Country", PocketTrader.detail.getCountry());       			    
		formData.putString("PostalCode", String.valueOf(PocketTrader.detail.getPostalCode()));		
	}
	
	
   }
   @Override
   protected void onRestart() {
	// TODO Auto-generated method stub
	super.onRestart();
   }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
   } 
   // Method, Pushes Home Screen
   public void pushHomeScreen(View v){
	   Intent intent = new Intent(context,PocketTrader.class);
	   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	   startActivity(intent);
   }
	 // Push My Profile Screen
	 public void pushMyProfile(View v){
			Intent intent = new Intent(context,MyProfileScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);		 
	 }  

}
