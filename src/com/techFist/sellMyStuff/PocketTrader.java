package com.techFist.sellMyStuff;

import com.google.ads.*;

import java.util.ArrayList;
import java.util.List;
import com.techFist.sellMyStuff.AccountManager.AccountScreen;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.AllProducts.AllProducts;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.CustomTextAdapter;
import com.techFist.sellMyStuff.Commons.Details;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Commons.User;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.ListView;

import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;


public class PocketTrader extends PocketActivity {
	

	// Shared references values
	public static final String DETAIL_KEY = "PROMO_PUSH_KEY";
	public static final String PUSH_NOTIF = "PUSH_NOTIF";
	public static final String PUSH_NOTIF_STATUS = "PUSH_NOTIF_STATUS";
	public static final String PROMO = "PROMO";
	public static final String FACEBOOK = "FACEBOOK";
	public static final String PROMO_STATUS = "PROMO_STATUS";	
    public static final String TOKEN = "access_token";
    public static final String EXPIRES = "expires_in";
    public static final String KEY = "facebook-credentials";	
    public static final String APP_ID = "417596758304880";
    public static final String SETTINGS_STAT = "SETTINGS_STAT";
    public static final String SETTINGS_MODIFIED_PUSH = "SETTING_MODIFIED_PUSH";
    public static final String SETTINGS_MODIFIED_PROMO = "SETTING_MODIFIED_PROMO";    
	// Application Level Constants
	public static final String BUY_ACTIVITY = "Buy a Product";
	public static final String SELL_ACTIVITY = "Sell a Product";
	public static final String DEAL_ACTIVITY = "Hot Deals";
	public static final String ACCOUNT_ACTIVITY = "My Account";
	public static final String ALL_ACTIVITY = "All Products";
	
	public static final String CRITERIA_CITY = "In My City";
	public static final String CRITERIA_STATE = "Entire State";
	public static final String CRITERIA_COUNTRY = "Everywhere";
	public static final String CRITERIA_NEARBY = "Nearby Me";

	public static final String DATE_TODAY = "Posted Today";
	public static final String DATE_WEEK = "This Week";
	public static final String DATE_15_DAY = "Last 15 Days";
	public static final String DATE_30_DAY = "Last 30 Days";
	public static final String DATE_NO_MATTER = "Does'nt Matter";
    	
	public static final String PRODUCT_DETAIL = "Product Detail";
	public static final String MY_SAVED_PRODUCT_DETAIL = "Saved Detail";
	public static final String MY_PRODUCT_DETAIL = "My Product Detail";
	public static final String SCREEN_TYPE = "Screen Type";
	public static final String MY_PRODUCT_LIST_DETAIL = "My Product List";
	
	public static final String LOCATION_GENERATED = "LoactionGenerated";
	public static final String LOCATION_GOT_GENERATED = "YES";
	public static final String LOCATION_NOT_GENERATED = "NO";	
	
	public static final String  PRODUCT_CATEGORY = "Please Select Category";
	public static final String PRODUCT_TYPE = "Please Select Type";
	
	
	public static final String CLASS_NAME = "class_name";
	public static final String STACK_TRACE = "stack_trace";
	
	public static final int PRODUCT_DETAIL_SCREEN = 1;
	public static final int MY_PRODUCT_DETAIL_SCREEN = 2;
	public static final int MY_SAVED_PRODUCT_DETAIL_SCREEN = 3;
	public static final int MY_PRODUCT_LIST_SCREEN = 4;
	
	
	public static final int SELL_ACTIVITY_MAIN_FORM = 1;
	public static final int SELL_ACTIVITY_IMAGE_CAPTURE_FORM = 2;
	public static final int SELL_ACTIVITY_REVIEW_FORM = 3;
	public static final int SELL_ACTIVITY_SUBMIT = 4;
   
	public static final int SECURE_LOGIN_SCREEN = 10;
	public static final int USER_REGEISTRATION_SCREEN = 11;
	public static final int STARTUP_SCREEN = 12;
	
	public static final int DATASTORE_EXCEPTION = -51;
	public static final int SERVICE_UNAVAILABLE = -52;
	public static final int DATA_NOT_FOUND = -53;
	public static final int PROCESS_SUCCESS = 54;
	
	public static final int FETCH_USER_DATA_REQUEST = 101;
	public static final int CAPTURE_IMAGE = 1;
	public static final int PICK_IMAGE = 2;
	public static final int LIST_LIMIT = 20;
	
	
	
	
	
	// Application level static Objects
	public static User user = new User();
    public static Details detail;
    public static boolean isSearchRequestedSS = false;
    public static ArrayList<Bitmap> bannerList = new ArrayList<Bitmap>();
    private LocationFinder locationFinder;	
	private CustomDialog customDialog;
	private AlertDialog dialog;
	private Context context;
	private ImageSwitcher iSwitcher;
	private Handler bannerHandelr;
	private Thread imageSwitcher;
	private ViewGroup locationWait;
	private ViewGroup appMainView;
	private boolean isAllProductClicked = false;
	private boolean isBannerClicked = false;
	private Intent bannerIntent;

	
	private ListView applicationOptions;
	private int[] imageId = {R.drawable.banner1,R.drawable.banner2,R.drawable.banner3,R.drawable.banner4,
			                 R.drawable.banner5,R.drawable.banner6,R.drawable.banner7,R.drawable.banner8,
			                 R.drawable.banner9,R.drawable.banner10,R.drawable.banner11}; 
	private String[] productCat = {"Automobile","Books and Stationery","Computers","Electronics","Clothing,Accessories and Lifestyle",
			                       "House Holds","Sports","Instruments","Movies and Music","Real State","Toys"};
	private Resources res;
	private boolean isLocationRequested = false;
	private AsyncTask<String,Bundle,Bundle> locationTask;
	Point p;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Register a Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,PocketTrader.class));
        
        
        // Auto populating Details, to run into Emulator
        PocketTrader.detail = new Details("bangalore","karnataka","india","b narayanpura",(float)77.681366,(float)12.999023
        		                          ,-1,"Service Rd, B Narayanapura,Bangalore, Karnataka");
        
        
        // Adding Custom header
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.application_main_page);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
        // End
        locationWait = (ViewGroup)findViewById(R.id.app_location_wait);
        appMainView = (ViewGroup)findViewById(R.id.app_main_view);
        context = this;
        res = getResources();
        iSwitcher = (ImageSwitcher) findViewById(R.id.BannerRoller);
        iSwitcher.setFactory(new BannerProvider());
        adView = new AdView(this, AdSize.BANNER,"test");
        // Setting up Application Options
        applicationOptions = (ListView)findViewById(R.id.apllication_menu_list);
        applicationOptions.setAdapter(new CustomTextAdapter(res.getStringArray(R.array.application)));
        
        //Setting up OnClick Listener, for Application Menu Option
        applicationOptions.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long arg3) {
				
				// Calling Sell Product Activity. 
				Intent intent;
				if(adapter.getItemAtPosition(position).equals(PocketTrader.SELL_ACTIVITY)){
					intent = new Intent(context,SellStartForm.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				if(adapter.getItemAtPosition(position).equals(PocketTrader.BUY_ACTIVITY)){
					// Starting fetching details activity
					isAllProductClicked = false;
					if(PocketTrader.detail == null){
					  Toast.makeText(context,R.string.finding_location, Toast.LENGTH_SHORT).show();
				      iSwitcher.setEnabled(false);
					  appMainView.setEnabled(false);	
				      locationWait.setVisibility(ViewGroup.VISIBLE);
					  locationWait.setEnabled(true);
					  applicationOptions.setEnabled(false);
					  isLocationRequested = true;
					  locationTask = new LocationFinderTask().execute("Fetch Location");  
					}
					else{
						intent = new Intent(context,ProductSearchForm.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}	
				if(adapter.getItemAtPosition(position).equals(PocketTrader.ALL_ACTIVITY)){
					// Starting fetching details activity
					isAllProductClicked = true;
					if(PocketTrader.detail == null){ 
					  Toast.makeText(context,R.string.finding_location, Toast.LENGTH_SHORT).show();
					  iSwitcher.setEnabled(false);					  
					  appMainView.setEnabled(false);		
					  locationWait.setVisibility(ViewGroup.VISIBLE);
					  locationWait.setEnabled(true);
					  applicationOptions.setEnabled(false);
					  isLocationRequested = true;
					  locationTask = new LocationFinderTask().execute("Fetch Location"); 
					}
					else{
						intent = new Intent(context,AllProducts.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}
				if(adapter.getItemAtPosition(position).equals(PocketTrader.ACCOUNT_ACTIVITY)){
					intent = new Intent(context,AccountScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				
				

			}
        	
        });
 
       
        // Starting thread for Switching Image
        imageSwitcher = new BannerDownloader();
        imageSwitcher.start();
        // Handler code to switch Image in Switcher, upon Receival of a message
        bannerHandelr = new Handler(){
          public void handleMessage(Message msg) 
            { 
              String switchImage =  msg.getData().getString("Switch");
              int resourceId = msg.getData().getInt("ImageID");
              final String category = msg.getData().getString("Category");
              if(switchImage == "Yes")
              {
           	     iSwitcher.setImageResource(resourceId);
           	     iSwitcher.setOnClickListener(new View.OnClickListener(){
                    
					@Override
					public void onClick(View v) {
					 isBannerClicked = true;	 
					 //Setting Up Data
					 Bundle b = new Bundle();
					 b.putBoolean("FROM_BANNER", true);
				     b.putString("CATEGORY",category);
				     bannerIntent = new Intent(context,AllProducts.class);
				     bannerIntent.putExtras(b);
				     // Checking if Location details are available     
				     if(PocketTrader.detail != null && PocketTrader.user != null){ 		
						  //Raising new Intent
						  startActivity(bannerIntent);
				      }else{
				    	  // First Fetch Location details, then Redirect to All Products
						  Toast.makeText(context,R.string.finding_location, Toast.LENGTH_SHORT).show();
						  iSwitcher.setEnabled(false);					  
						  appMainView.setEnabled(false);		
						  locationWait.setVisibility(ViewGroup.VISIBLE);
						  locationWait.setEnabled(true);
						  applicationOptions.setEnabled(false);
						  isLocationRequested = true;
						  locationTask = new LocationFinderTask().execute("Banner Clicked"); 
				      
				      }
				    
					}
           	    	 
           	     });
              }
            }
        	
        };
        
        // Starting Image uploader Service, to take care of any unfinished Image Uploads;
        startImageUploadService();

        
    }  
    
    public void showMsgDialog(View v){
    
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	// TODO Auto-generated method stub
    	super.onWindowFocusChanged(hasFocus);
    	   //Initialize the Point with x, and y positions
    	   Display display = getWindowManager().getDefaultDisplay();
           p = new Point();
           p.x = (int) display.getWidth()/2;
  	       p.y = (int) display.getHeight()/2;
  	       
    }    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();

 
    }
    @Override
    protected void onNewIntent(Intent intent) {
    	// TODO Auto-generated method stub
    	super.onNewIntent(intent);

        if(intent!=null){
       	 if(intent.getStringExtra("AddressValue") != null)
       	 {
       		 String flag = intent.getStringExtra("AddressValue");
             if(flag.equals("Suggestion")){
            	 
             
               if(isAllProductClicked && !isBannerClicked){
    	          startActivity(new Intent(context,AllProducts.class));	    	   
               }
               else if(!isBannerClicked && isAllProductClicked){
    	          startActivity(new Intent(context,ProductSearchForm.class));		    	   
               }
               else if(!isAllProductClicked && isBannerClicked){
            	   isBannerClicked = false;
            	   if(bannerIntent != null)
            		 startActivity(bannerIntent);
               }
               isBannerClicked = false;               
               
             }
       	 } }    
    
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	Intent intent = getIntent();
        if(intent!=null){
        	 
        	
          	 if(intent.getStringExtra("AddressValue") != null)
          	 {
          		 String flag = intent.getStringExtra("AddressValue");
                if(flag.equals("Suggestion")){
               	 
                
                  if(isAllProductClicked){
       	          startActivity(new Intent(context,AllProducts.class));	    	   
                  }else{
       	          startActivity(new Intent(context,ProductSearchForm.class));		    	   
                  }
                }
          	 }
          	 Log.i("####", "Starting on Resume for: " + PocketTrader.user.getUserEmail());
          	 if(intent.getExtras() != null){
          		if(intent.getExtras().containsKey(PocketTrader.CLASS_NAME)){ 
          	     String className = (String)intent.getExtras().getString(PocketTrader.CLASS_NAME);
          	     String stackTrace = (String)intent.getExtras().getString(PocketTrader.STACK_TRACE);
          	     if(className != null && stackTrace != null && stackTrace != ""){
          		   Toast.makeText(context,R.string.error_msg,Toast.LENGTH_SHORT).show();
          		   restoreData();
          	    }    
          	  }
          	 }
         } 
    }
    // Restoring Data
    public void restoreData(){
    	// Reading User Detail
    	SharedPreferences sharedUserPrefs = getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
    	String userName = sharedUserPrefs.getString("user_name","");
    	String userCountry = sharedUserPrefs.getString("user_country","");
    	String userCity = sharedUserPrefs.getString("user_city","");
        String userEmail = sharedUserPrefs.getString("user_email","");
    	String userPhone = sharedUserPrefs.getString("user_phone","");
    	String userFirstName = sharedUserPrefs.getString("first_name","");
    	String userLastName = sharedUserPrefs.getString("last_name","");
    	String userKey = sharedUserPrefs.getString("user_key","");
    	String userState = sharedUserPrefs.getString("user_state","");
    	boolean phoneCall = Boolean.getBoolean(sharedUserPrefs.getString("phone_call",""));
    	
    	
    	
    	PocketTrader.user = new User(userName,userCountry,userCity,userEmail,userPhone,userFirstName,userLastName,userKey,userState,phoneCall);
    	
    	
    	// Reading Details
    	SharedPreferences sharedDetailPrefs = getApplicationContext().getSharedPreferences("detail_data", Context.MODE_PRIVATE);
    	String city = sharedDetailPrefs.getString("detail_city", "");
    	String area = sharedDetailPrefs.getString("detail_city", "");;
    	String state = sharedDetailPrefs.getString("detail_city", "");;
    	String country = sharedDetailPrefs.getString("detail_city", "");;
    	float longitude = sharedDetailPrefs.getFloat("detail_longitude", (float)0.00);
    	float latitude = sharedDetailPrefs.getFloat("detail_latitude", (float)0.00);
    	int postalCode = sharedDetailPrefs.getInt("detail_postalcode", -1);
    	String address = sharedDetailPrefs.getString("detail_city", "");;
    	
    	PocketTrader.detail = new Details(city,state,country,area,longitude,latitude,postalCode,address);
    	
    	
    }
    
    // Thread, which Runs for Switching Images
    class BannerDownloader extends Thread{
 	   
 		   public void run() {
                try{Looper.prepare();}catch(Exception e){e.printStackTrace();}
                int index = 0;
                while(context != null){
                 	Message msg = bannerHandelr.obtainMessage(); 
     	            Bundle b = new Bundle();
     	            b.putString("Switch", "Yes");
     	            b.putInt("ImageID", imageId[index]);
     	            b.putString("Category", productCat[index]);
     	            msg.setData(b); 
     	            bannerHandelr.sendMessage(msg);                	
                	
                	try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	
 
     	            index = index + 1;
     	            if(index == 11)
     	            	index = 0;
                }
 		   }

    }
    
    // Class. Provides Views to be displayed in Image Switcher
    class BannerProvider implements ViewFactory {

		@Override
		public View makeView() {
			// TODO Auto-generated method stub

            TypedArray attr = context.obtainStyledAttributes(R.styleable.ImageGallery);

            attr.recycle();	
            
            ImageView iView = new ImageView(context);
            //iView.setBackgroundResource(mGalleryItemBackground);
            iView.setScaleType(ImageView.ScaleType.FIT_XY);
            iView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
            iView.setBackgroundColor(0xFF000000);
            return iView;
		}
    	
    }
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	imageSwitcher.interrupt();
    }
    
    // Context Menu
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  boolean supRetVal = super.onCreateOptionsMenu(menu);
	  menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.app_menu1));
	  menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.app_menu2));
	  menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.app_menu3));
	  return supRetVal;
	}
    // Creating Context Menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	  switch (item.getItemId()) {
	    case 0:
        // Logout
		performLogout();	
	    return true;
	    case 1:
        // Setting
	    getSettingWindow();
	    return true;
	    case 2:
        // About APP
	    getAboutApp();
	    return true;

	   }
		return false;
	}
	// Getting Settings Window
	private void getSettingWindow(){
		   Intent intent = new Intent(context,SettingsActivity.class);
		   startActivity(intent);		
	}
	// Getting Info About App
	private void getAboutApp(){
	  String infoMsg = (String)getString(R.string.app_info);
	  String appVersion = (String)getString(R.string.app_version);
	  infoMsg = infoMsg +"\n" + appVersion;
	  customDialog = new CustomDialog(context,"Okay","",infoMsg);
	  dialog = customDialog.getAboutAppDialog("Pocket Trader");
	  dialog.show();
	}
	public void performLogout(){

	    	String PREFS_NAME = "UserAccount";
	    	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);    
			SharedPreferences.Editor editor = prefs.edit();
		  	editor.putBoolean("Account-Status", false);
		  	editor.commit();
		  	// Logout from facebook as well
        	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PocketTrader.KEY, Context.MODE_PRIVATE);  
            editor = sharedPreferences.edit();
        	editor.putString(PocketTrader.TOKEN, null);
        	editor.putLong(PocketTrader.EXPIRES, 0);
        	editor.commit();		  	
		  	
		  	// Clear all the saved setting's
	    	editor = getApplicationContext().getSharedPreferences(PocketTrader.DETAIL_KEY, Context.MODE_PRIVATE).edit();
	    	editor.putBoolean(PocketTrader.PUSH_NOTIF, false);
	    	editor.putBoolean(PocketTrader.PROMO, false);
	    	editor.putBoolean(PocketTrader.FACEBOOK, false);
	    	editor.commit();
	    	
		  	// Log out User
		  	
		  	Toast.makeText(context,"Logged Out", Toast.LENGTH_SHORT).show();
		  	// Raising new intent
		  	Intent intent = new Intent(context,SecureLoginScreen.class);
		  	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		  	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  	startActivity(intent);
		  	setResult(0);
		  	finish();            
	}
   public void performLogin()
   {
	   Intent intent = new Intent(context,SecureLoginScreen.class);
	   startActivity(intent);
   }
   @Override 
   public void onBackPressed() {
	// TODO Auto-generated method stub
	  locationWait.setVisibility(ViewGroup.INVISIBLE);
	  locationWait.setEnabled(false);
	  applicationOptions.setEnabled(true);
	 if(isLocationRequested){
		 if(locationTask != null)
		 locationTask.cancel(true);
	 }
	 else{  
	 customDialog = new CustomDialog(context,"Exit","Return","Do you really want to Exit !!");
	 android.content.DialogInterface.OnClickListener listenPositive = new  android.content.DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
		    setResult(0);
		    PocketTrader.user = null;
		    PocketTrader.detail = null;
		    finish();	
		}
		 
	 };	    	 
	 android.content.DialogInterface.OnClickListener listenNegative = new android.content.DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
		}
		 
	 };
	 
	 dialog = customDialog.getConfirmationDialogForActionCustomListeners("Info",listenPositive,listenNegative);
	 dialog.show();
	 }
   }
   // Method for Image Temp Cleanup
   public static void performImageCacheCleanup()
   {
	   
   }
   //Method Generates a Button with Background
   public static void generateButtonWithBackground(Button b,int defaultImage,int focusedImage){
	   
   }
   // Method for Image Temp Cleanup
   public static boolean performImagePresence()
   {
	   return true;
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
   
   // Start Image Uploading Service
   public void startImageUploadService(){
	   Intent intent = new Intent(context,com.techFist.sellMyStuff.Services.ImageUploaderService.class);
	   context.startService(intent);
	   
	   
   }
   // Fetching user Location details
	class LocationFinderTask extends AsyncTask<String,Bundle,Bundle>{
        private boolean isBannerClicked = false;
		@Override
		protected Bundle doInBackground(String... params) {
			// TODO Auto-generated method stub]
			String s = params[0];
			if(s.equals("Banner Clicked"))
				isBannerClicked = true;
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
			        	 e.printStackTrace();
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

	    applicationOptions.setEnabled(true);
	    isLocationRequested = false;
    	appMainView.setEnabled(true);	  
    	locationWait.setVisibility(ViewGroup.INVISIBLE);
		locationWait.setEnabled(false);
		iSwitcher.setEnabled(true);
	}
    @Override
    protected void onPostExecute(Bundle result) {
    // TODO Auto-generated method stub
      super.onPostExecute(result);
      applicationOptions.setEnabled(true);
      iSwitcher.setEnabled(true);
      isLocationRequested = false;
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
                appMainView.setEnabled(true);	  
		    	locationWait.setVisibility(ViewGroup.INVISIBLE);
				locationWait.setEnabled(false);
		        // Start the Activity
				if(!isAllProductClicked && !isBannerClicked){  // Buy a Product

					startActivity(new Intent(context,ProductSearchForm.class));					
				}
				else if(isAllProductClicked && !isBannerClicked){    

					// All Products
					startActivity(new Intent(context,AllProducts.class));					
				}
				else if(!isAllProductClicked && isBannerClicked){
					// Start All products with relevant search
					if(bannerIntent != null)
					 startActivity(bannerIntent);
				}
		    }
		    catch(Exception e){
		    	// Error Occuerd, Popping Error Box
		    	// Location details has not been fetched, Pop a Box for user to Select a Location                
                e.printStackTrace();
		    	appMainView.setEnabled(true);	  
		    	locationWait.setVisibility(ViewGroup.INVISIBLE);
				locationWait.setEnabled(false);
				Toast.makeText(context,R.string.fill_address, Toast.LENGTH_SHORT).show();
				onSearchRequested();
		    }      
      
      }
      if(b.getString(PocketTrader.LOCATION_GENERATED) == PocketTrader.LOCATION_NOT_GENERATED){
    	// Location details has not been fetched, Pop a Box for user to Select a Location 
	      locationWait.setVisibility(ViewGroup.INVISIBLE);
		  locationWait.setEnabled(false);
		  PocketTrader.isSearchRequestedSS = false;
		  Toast.makeText(context,R.string.fill_address, Toast.LENGTH_SHORT).show();
		  onSearchRequested();
      }
      appMainView.setEnabled(true);	
      locationWait.setVisibility(ViewGroup.INVISIBLE);
	  locationWait.setEnabled(false);
	
	}
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
