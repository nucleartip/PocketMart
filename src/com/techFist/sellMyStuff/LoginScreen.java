package com.techFist.sellMyStuff;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.Details;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Services.LoginService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.content.DialogInterface.OnClickListener;
import android.provider.Settings.Secure;

public class LoginScreen extends Activity implements ServiceCallback.Receiver{


	private LoginService localService;
	private Context context;
	private int logInStatus = -1;
	private AlertDialog dialog;
	private CustomDialog customDialog;
	public static final String PREFS_NAME = "UserAccount";
	private Bundle formData;
	public ServiceCallback mReceiver;
	public static AsyncTask<String,Integer,Integer> loginTask;
	private boolean isServiceRequested = false;
	private String deviceID;
    private LocationFinder locationFinder;
    private AlertDialog alertNetworkDialog = null;
    private LocationManager myLocationMgr;
	private AsyncTask<String,Bundle,Bundle> locationTask;
	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	    	localService = ((LoginService.LocalBinder)service).getService();
	    	isServiceRequested = true;
	    	localService.onCreate();
            Intent intent = new Intent();
            intent.putExtras(formData);
            logInStatus = localService.onStartCommand(intent, 0, 1);
            if(logInStatus == -404)
            	doUnbindService();
            
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	localService = null;
	    	isServiceRequested = false;

	    }
	};

	// Bind Service to Activity
	void doBindService() {
	    bindService(new Intent(LoginScreen.this,LoginService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	// Un Binding Service
	void doUnbindService() {
	     // Detach our existing connection.
	     unbindService(mConnection);
	     isServiceRequested = false;
	     // LoginStatus = 0,  User Match Log in
	     // LoginStatus = 1,  User ID Match, but Password Dont, Push Secure Log In Screen
	     // LoginStatus = 2,  User ID not Found, Push Registration Screen
	     // LoginStatus = -1, Network Error Push Pop Up Box and Exit.
	     
	     if(logInStatus == 0){
	    	 
            // Dummy Details
	    	 /*
             PocketTrader.detail = new Details("bangalore","karnataka","india","b narayanapura",
            		 (float)12.9954939,(float)77.6840222,-1,"Bangalore,Karnataka,India"); */
	    	 
	    	 Intent intent = new Intent(context,PocketTrader.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 promoPushRegistrationUpdate();
	         startActivityForResult(intent, 0);
	    	 
	    	 //locationTask = new LocationFinderTask().execute("Get Location");

	     }
	     if(logInStatus == 1){
	    	 Intent intent = new Intent(context,SecureLoginScreen.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 startActivityForResult(intent, 1);
	     }
	     if(logInStatus == 2){
	    	 Intent intent = new Intent(context,UserRegistration.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 startActivityForResult(intent,2);
	     }
	     if(logInStatus == -1 || logInStatus == -404){
	    	 
	    	 customDialog = new CustomDialog(context,"Retry","Exit",getString(R.string.network_error));
	    	 Intent intent = new Intent(context,LoginScreen.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 OnClickListener listen = new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
	    		 
	    	 };
	    	 dialog = customDialog.getConfirmationDialogForAction("Network Error", intent,listen);
	    	 dialog.show();
             	    	 
	    	 
	     }

	}
	// Check if My Service is Running or Not
    private boolean isMyServiceRunning() {
		    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		        if ("com.techFist.sellMyStuff.Services.LoginService".equals(service.service.getClassName())) {
		            return true;
		        }
		    }
		    return false;
     }	
    
    private synchronized boolean disconnectMyService(){
      	 try{  
       	if(isServiceRequested && localService != null) {
      	       localService.cancelRunningTask();
      	       isServiceRequested = false;
      	       if(isMyServiceRunning())
      		   unbindService(mConnection);
      	   }
      	 }catch(Exception e){
      		 e.printStackTrace();
      	 }
      	   return true;
       }

    
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
		disconnectMyService();
	    finish();
	    if(locationTask!=null && !locationTask.isCancelled())
	    	locationTask.cancel(true);
	    
		super.onBackPressed();
    }
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
	protected void onDestroy() {

		super.onDestroy();
	    
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	    context = this;
        formData = new Bundle();
        mReceiver = new ServiceCallback(new Handler());
        mReceiver.setReceiver(this); 		    
    	deviceID = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID); 
    	
     // Creating required Folder Structure	
    	createFolderStructure();
     // Regestering for Push Service
    	//registerForPushNotification();
    	
     //Register a Exception Handler
     //Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,LoginScreen.class));
    	
	}
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
	    setContentView(R.layout.login_screen);
    	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
    	if (prefs.contains("Account-Status"))
    	  {
     	  	if(prefs.getBoolean("Account-Status",false)){
    		  formData.putString("Email", prefs.getString("Email", ""));
    	  	  formData.putString("Password",prefs.getString("Password", ""));
    	  	  formData.putString("DeviceId", deviceID);
    	  	  formData.putParcelable("Receiver", mReceiver);
     	  	} 
     	  	else{
      		  formData.putString("Email", prefs.getString("Email", ""));
    	  	  formData.putString("Password","");  
    	  	  formData.putString("DeviceId", deviceID);
    	  	  formData.putParcelable("Receiver", mReceiver);
     	  	}
     	  }
    	else
    	{  
    		SharedPreferences.Editor editor = prefs.edit();
    	  	editor.putBoolean("Account-Status", false);
    	  	editor.putBoolean("First-Time", true);
    	  	editor.putString("Email", "");
    	  	editor.putString("Password", "");
    	  	editor.putString("DeviceId", deviceID);
    	  	formData.putString("Email", "");
    	  	formData.putString("Password", "");
    	  	formData.putString("DeviceId", deviceID);
    	  	formData.putParcelable("Receiver", mReceiver);
    	    editor.commit();
    	}
    
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
        if(alertNetworkDialog != null){
         	alertNetworkDialog.dismiss();
         	alertNetworkDialog.cancel();
         	
         }
         alertNetworkDialog = null;
     	checkNetworkStatus();

    	
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data); 
	
	if(requestCode == 0 || requestCode == 1 || requestCode == 2) {
		finish();
	}
   
   }

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		if(resultCode == 0){

		    logInStatus = resultData.getInt("Result");
            doUnbindService();
		}
	}

	void checkNetworkStatus(){
	       

		   myLocationMgr = (LocationManager)this.getSystemService(LOCATION_SERVICE); 
		   
		   if( !myLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
			   !myLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		   {
			   
			   // Popping Up Alert in case of No Network Provider.
			   customDialog = new CustomDialog(this,getResources().getString(R.string.alert_ok_msg),
			   getResources().getString(R.string.alert_exit_msg),getResources().getString(R.string.no_location_provider_login));
		       OnClickListener listenNegative = new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
		    		 
		    	 };
			   alertNetworkDialog = customDialog.getSettingWithExitDialog(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS,"Location Service Diabled",listenNegative);
			   alertNetworkDialog.show();
		   
		   }
		   else{
		    	doBindService();
		   }

	   }	

	   
	void createFolderStructure(){
		 // Creating Directory's
		 File file = new File(Environment.getExternalStorageDirectory() + "/PocketTrader/Images/");
		 if(!file.isDirectory())
		   file.mkdirs();
		
	}
	
	// Logic for registering for Push and Promo Registration.
	void promoPushRegistrationUpdate(){

       SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PocketTrader.DETAIL_KEY, Context.MODE_PRIVATE);
	   if(sharedPreferences.contains(PocketTrader.PUSH_NOTIF)){
		   // Shared Preference Exist, Perform Registration Process
		   SharedPreferences prefs = getApplicationContext().getSharedPreferences(PocketTrader.SETTINGS_STAT, Context.MODE_PRIVATE);
		   if(prefs.getBoolean(PocketTrader.SETTINGS_MODIFIED_PROMO, false)){
			   // Perform Register/Unregister for Promo

			   boolean flag = sharedPreferences.getBoolean(PocketTrader.PROMO, false);
			   Bundle b = new Bundle();
			   b.putString("Email", PocketTrader.user.getUserEmail());
			   b.putString("Status", String.valueOf(flag));
			   
			   new PromoRegistration().execute(b);
		   }
		   
		   if(prefs.getBoolean(PocketTrader.SETTINGS_MODIFIED_PUSH, false)){

			   boolean flag =  sharedPreferences.getBoolean(PocketTrader.PUSH_NOTIF, false);
			   // Perform Register/Unregister for Promo
			   if(flag){
				   registerForPushNotification();
			   }else{
				   unregisterForPushNotification();
			   }
		   }		   
	   }
       // If shared preference does not exist then Simply do Nothing
	}
	// Thread for Registering for Promo's
	private class PromoRegistration extends AsyncTask<Bundle,Integer,Integer>{
   	    private HttpClient PostClient;
   	    private HttpPost httpPost;	
   	    private HttpResponse resp;	
   	    static final String    SERVER    = "http://sms-techfist.appspot.com";	
        public PromoRegistration(){
        	 this.httpPost = new HttpPost();
     		 this.PostClient = new DefaultHttpClient();	        	        	
        }
        @Override
	    protected Integer doInBackground(Bundle... arg0) {
		   // TODO Auto-generated method stub
		   Bundle formData = arg0[0];
		    try{
	   	        httpPost = new HttpPost(SERVER+"/updatePromo");

			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			    data.add(new BasicNameValuePair("Email",formData.getString("Email")));		    
			    data.add(new BasicNameValuePair("Status",formData.getString("Status")));
		        httpPost.setEntity(new UrlEncodedFormEntity(data));	
		        resp = PostClient.execute(httpPost);
	   		    // Operation was Successful
	   		    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	   			  return 1;
	   		    }	    		
	   		    // Datastore Exception, Product not Saved Ask user to Try again
	   		    if(resp.getStatusLine().getStatusCode() == 10000){
	   			  return -500;
	   		    }
	   		    // Service Error
	   		    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
	   			  return -404;
	   		    }	        
		        
			}
	       catch(Exception e){
	    	   e.printStackTrace();
	     	  return -404;
	       }
	       return -404; 			

		 }
	
	}
	
	
	
	// Perform registration for Push Notification
	void registerForPushNotification(){
    	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
    	
        boolean status = prefs.getBoolean("Push-Status", false);
         if(!status){

		  Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		  registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
		  registrationIntent.putExtra("sender", "123manis@gmail.com");
		  startService(registrationIntent);	
         }  
	}
	// Perform un-register for Push Notification
	void unregisterForPushNotification(){
		Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
		unregIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		startService(unregIntent);		
	}
	
	
	// Fetching user Location details
    class LocationFinderTask extends AsyncTask<String,Bundle,Bundle>{

			@Override
			protected Bundle doInBackground(String... params) {
				// TODO Auto-generated method stub]
				 try{Looper.prepare();}catch(Exception e){e.printStackTrace();}
	            Bundle b = new Bundle();
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
	         	    	    	 b.putString(PocketTrader.LOCATION_GENERATED, PocketTrader.LOCATION_GOT_GENERATED);
	         	    	    	 b.putParcelable("Location", location);
	         	    	         
	         	    	     }
	         	    	     if(index > 20)
	         	    	     {
	         	    	    	 flag = false;
	         	    	         b.putString(PocketTrader.LOCATION_GENERATED,PocketTrader.LOCATION_NOT_GENERATED);
	         	    	     }

					       } 
				         catch(Exception e){
				        	 b.putString(PocketTrader.LOCATION_GENERATED, PocketTrader.LOCATION_NOT_GENERATED);
				         }
	                }
			    }
			    return b;
			} 
	   @Override
	   protected void onCancelled() {
	   // TODO Auto-generated method stub
			super.onCancelled();

    
		}
	    @Override
	    protected void onPostExecute(Bundle result) {
	    // TODO Auto-generated method stub
	      super.onPostExecute(result);
	      Bundle b = (Bundle) result;
	      Geocoder geocoder;
		  List<Address> addresses;      
	      if(b.getString(PocketTrader.LOCATION_GENERATED) == PocketTrader.LOCATION_GOT_GENERATED){
	    	// Location details has been fetched, Process details  
			    Location loc = (Location) b.getParcelable("Location");

			    geocoder = new Geocoder(context);
			    try{
			    	addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(),5);
	                
			        // Setting up Location Parameters
			        int postalCode = -1;
	                if(addresses.get(0).getPostalCode() != null && addresses.get(0).getPostalCode() != "")
	                	postalCode = Integer.valueOf(addresses.get(0).getPostalCode());
	                String addressStr = formatAddress(addresses.get(0));
	                PocketTrader.detail = new Details(addresses.get(0).getLocality(),addresses.get(0).getAdminArea(),addresses.get(0).getCountryName(),addresses.get(0).getSubLocality(),
	                		              (float)loc.getLongitude(),(float)loc.getLatitude(),postalCode,addressStr);

			        // Start the Activity
	   	    	 Intent intent = new Intent(context,PocketTrader.class);
		    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		         startActivityForResult(intent, 0);
			    }
			    catch(Exception e){
			    	// Error Occuerd, Popping Error Box
			    	// Location details has not been fetched, Pop a Box for user to Select a Location                

			    }      
	      
	      }
	    	 Intent intent = new Intent(context,PocketTrader.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	         startActivityForResult(intent, 0);	      
       }
	  }
    public boolean checkSettingsStatus(){
    	
    	return false;
    }
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
}
