package com.techFist.sellMyStuff;

import com.google.ads.AdView;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Services.ProductsService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SellSubmitForm extends PocketActivity implements  ServiceCallback.Receiver{
 
	boolean expandableListStatus = false;
    ImageView productReviewMore;
    View tempview;
    Context context;
    ViewGroup reviewGroup;
    Bundle formData;
    private TextView sellingReviewCategory;
    private TextView sellingReviewDescription;
    private TextView sellingReviewPrice;
    private TextView sellingReviewLocation;
    private TextView sellingReviewProductName;
    //private ImageView sellProductSubmitImage;
    private Button sellSubmitDeal;
    private Button sellEditDeal;
	private AlertDialog waitDialog;
	private CustomDialog customDialog;
	private AlertDialog dialog;
    static final String    SERVER    = "http://sms-techfist.appspot.com";	
	public static final String PREFS_NAME = "UserAccount";
	private ProductsService localService;
	private int postStatus = -1;
	public ServiceCallback mReceiver;	
	private Bundle callBackBundle;

	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	    	localService = ((ProductsService.LocalBinder)service).getService();
            localService.onCreate();

            postStatus = localService.onStartCommand(new Intent().putExtras(formData), 0, 1);
            //doUnbindService();
            
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	localService = null;

	    }
	};
	
	// Bind Service to Activity
	void doBindService() {
	    bindService(new Intent(SellSubmitForm.this,ProductsService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	// Un Binding Service
	void doUnbindService() {
	     // Detach our existing connection.
	    unbindService(mConnection);	
	    
	    //postStatus = 0, Success take user back to Home Screen
	    //postStatus = -1, DataStore exception, ask user to resubmit
	    //postStatus = -404, Popup Unknown Error exception and take user to Home Screen.
	    
	    if(postStatus == 0){
	    	waitDialog.cancel();
	    	Toast.makeText(context,"Hurray!! your Deal has been posted.", Toast.LENGTH_LONG).show();
	    	Intent intent = new Intent(context,SellCaptureImageForm.class);
	    	intent.putExtras(formData);
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	    }
	    if(postStatus == -1){
	    	waitDialog.cancel();
	    	customDialog = new CustomDialog(context,"Okay","Home","Oops,We Experienced Problem in Saving your Data pelease Retry !!");
	       	dialog = customDialog.getErrorDialog("Datastore Error");
	       	dialog.show();
	       	
	    }   
	    if(postStatus == -404){
	    	waitDialog.cancel();
	    	customDialog = new CustomDialog(context,"Okay","","Oops, Unkown Error this is Embarassing ..");
	    	Intent intent = new Intent(context,PocketTrader.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	dialog = customDialog.getErrorDialogForAction("Unknown Error",intent);
	    }
	}    
    
    
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }    
    @Override
    public void onCreate(Bundle savedInstanceState) {
		

	    
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.selling_submit_form);
	    
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.selling_submit_form);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,SellSubmitForm.class));
        
        context = this;       
        // Setting up Service Callback
        mReceiver = new ServiceCallback(new Handler());
        mReceiver.setReceiver(this);        
 
        // Getting Form Objects
        sellSubmitDeal =  (Button)findViewById(R.id.submit_selling);
        sellingReviewCategory = (TextView)findViewById(R.id.selling_review_category);
        sellingReviewDescription = (TextView)findViewById(R.id.selling_review_description);
        sellingReviewPrice = (TextView)findViewById(R.id.selling_review_price);
        sellingReviewLocation = (TextView)findViewById(R.id.selling_review_location);
        sellingReviewProductName = (TextView)findViewById(R.id.selling_product_name);
        sellEditDeal = (Button)findViewById(R.id.submit_edit);
        //sellProductSubmitImage = (ImageView)findViewById(R.id.sell_product_submit_image);

        //Adding Listeners Submitting Deals
	    sellSubmitDeal.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
			  if(getUserStatus())	
			  {
				// TODO Auto-generated method stub
				customDialog = new CustomDialog(context,"","","Posting");
				waitDialog = customDialog.getWaitDialog("Please Wait",true);
				
				waitDialog.show();
				//new submitDeal().execute("Post");
				formData.putParcelable("Receiver", mReceiver);
				formData.putString("Phone", PocketTrader.user.getUserPhone());
				doBindService();
				//new UploadImage().execute("Upload");
			  	//startActivity(new Intent(context,PostDeals.class));
			  }
			  else{
				  customDialog = new CustomDialog(context,"Okay","","You are not logged in, please log in to Continue Submission");
				  dialog = customDialog.getInfoDialog("Please Login");
				  dialog.show();
			  }
		  }
	    	
	    });
	    
	   //Adding Listener, Edit
	    sellEditDeal.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
	    });
	}
	
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
 
        if(requestCode == 0){
        	if(resultCode == 1){
        		Toast.makeText(context,"Deal Posted", Toast.LENGTH_LONG).show();
        	}
        }
    }
    
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
	    super.onStart();
	    if(getIntent()!=null){
	        formData = getIntent().getExtras();
	    }	    
        populateSubmitForm(formData);


	}
	// User Generated Custom Method

	void populateSubmitForm(Bundle formData){
	    
		String type = "";
		String category = "";
		if(formData.containsKey("Name")){
			sellingReviewProductName.setText(formData.getString("Name"));
		}
		if(formData.containsKey("Category")){
		    category = formData.getString("Category");
		}
		if(formData.containsKey("Type")){
			type = formData.getString("Type");
			sellingReviewCategory.setText(category+","+type);
		}
		  
		if(formData.containsKey("Description"))
		  sellingReviewDescription.setText(formData.getString("Description"));
		if(formData.containsKey("Price"))
		  sellingReviewPrice.setText(formData.getString("Price"));
		if(formData.containsKey("Address"))
		 sellingReviewLocation.setText(formData.getString("Address"));		
 	    if(formData.containsKey("Path")){
 	    }
	    
	}
    

    class CustomReviewAdapterMore extends BaseAdapter{

    		

    		private String[] menu;
    		public CustomReviewAdapterMore( String[] strings){
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
    	     	   
    	        	TextView productTitle = (TextView)reuse.findViewById(R.id.product_review_title);
    	        	TextView productSubTitle = (TextView)reuse.findViewById(R.id.product_review_subtitle);  
    	        	  productTitle.setText(getItem(position).split("#t")[0]);
      	        	  productSubTitle.setText(getItem(position).split("#t")[1]);
    	        	tempview = reuse;
    	            return reuse;
    	        }
    	        else
    	        {
    	     	  ViewGroup item = getViewGroup(reuse, parent);
  	        	  TextView productTitle = (TextView)item.findViewById(R.id.product_review_title);
  	        	  TextView productSubTitle = (TextView)item.findViewById(R.id.product_review_subtitle);  
  	        	  productTitle.setText(getItem(position).split("#t")[0]);
  	        	  productSubTitle.setText(getItem(position).split("#t")[1]);
  	        	  tempview = item;
    	          return item;           
    	        }

    		}
    		
    		private ViewGroup getViewGroup(View reuse,ViewGroup parent){
    			if(reuse instanceof ViewGroup)
    				return (ViewGroup)reuse;
    			Context context = parent.getContext();
    			LayoutInflater inflater = LayoutInflater.from(context);
    			ViewGroup item = (ViewGroup)inflater.inflate(R.layout.product_review_list_cell,null);
    			return item;
    	        
    	    
    		}    	
    }


    private boolean getUserStatus(){
    	String PREFS_NAME = "UserAccount";
    	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);    
        if(prefs.contains("Account-Status")){
        	
        	formData.putString("Email", prefs.getString("Email", ""));
        	return prefs.getBoolean("Account-Status", false);
        }
        else
        	return false;
    }
    // Callback called from Service.
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		callBackBundle = resultData;
		formData.putString("Key", callBackBundle.getString("Key"));
		postStatus = resultCode;
		doUnbindService();
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

