package com.techFist.sellMyStuff.AccountManager;
import com.google.ads.AdView;
import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.CustomPoductList;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Commons.Product;
import com.techFist.sellMyStuff.Services.ProductsService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;


public class MyProductListScreen extends PocketActivity implements ServiceCallback.Receiver{



	private Context context;
	private ListView myProductList;
	private Bundle formData;
	private CustomDialog customDialog;
	private AlertDialog dialog;
	private int offset = 0;
	private int limit = PocketTrader.LIST_LIMIT;
	public ServiceCallback mReceiver;
	private ProductsService localService;	
    private boolean isServiceRequested;
	private boolean isProductListRequested;
	private int processStatus = -404;
	private View footer;
    private boolean endofList;
    private int newSize = 0;
    private int oldSize = 0;
    private CustomPoductList customProductList;
	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	     localService = ((ProductsService.LocalBinder)service).getService();
	     isServiceRequested = true;
	     localService.onCreate();
         Intent intent = new Intent();
         formData.putParcelable("Receiver", mReceiver);
         formData.putString("Email", PocketTrader.user.getUserEmail());
         intent.putExtras(formData);
         if(isProductListRequested){
        	
            processStatus = localService.onStartCommand(intent,0, 3);
            if(processStatus == -404)
           	  doUnbindService();            
         }         
	   }

	    public void onServiceDisconnected(ComponentName className) {
	    	
	    	isProductListRequested = false;
	    	isServiceRequested = false;
	    	localService = null;

	    }
	};         
	// Bind Service to Activity
	void doBindService() {
	    bindService(new Intent(MyProductListScreen.this,ProductsService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	// Check if My Service is Running or Not
    private boolean isMyServiceRunning() {
		    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		        if ("com.techFist.sellMyStuff.Services.ProductsService".equals(service.service.getClassName())) {
		            return true;
		        }
		    }
		    return false;
     }	
    private synchronized boolean disconnectMyService(){
   	 try{
      if(isServiceRequested && localService != null){ 
         isServiceRequested = false; 
         localService.cancelRunningTask();
         if(isMyServiceRunning())
   	     unbindService(mConnection);
   	   } 
   	 }catch(Exception e){
   		 e.printStackTrace();
   	 }
   	 return true;
    }
	// UnBind Binding Service
	void doUnbindService() {
      disconnectMyService();

      if(isProductListRequested){
    	  isProductListRequested = false;
 		 if(processStatus == 1){
             newSize = AccountScreen.myProductList.size();
             
             if(newSize%limit != 0 || oldSize == newSize)  // No More products Available on Server
             {
            	 endofList = true;
            	 oldSize = newSize;
            	 myProductList.removeFooterView(footer);
             }
             else{                                          // Still Products are to be fetched from Server
               endofList = false;
               oldSize = newSize;
             } 	 
		     customProductList.refreshData(AccountScreen.myProductList);
		     customProductList.notifyDataSetChanged();
             myProductList.setSelection(offset);
             myProductList.refreshDrawableState();
 		}
 		 else{
        	endofList = true;
        	oldSize = newSize;
        	myProductList.removeFooterView(footer);          	 

 		}
    	  
      }// Product List Requested If
		
	}		
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		disconnectMyService();
		AccountScreen.myProductList.removeAll(AccountScreen.myProductList);
		finish();
		super.onBackPressed();
	}    
    
    public void showMsgDialog(View v){
       	
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.my_product_list_screen);
		
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.my_product_list_screen);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,MyProductListScreen.class));
		
		// Fetching form Objects
		context = this;
		myProductList = (ListView)findViewById(R.id.my_account_productList);
		formData = new Bundle();
		LayoutInflater inflate = getLayoutInflater();
		footer = inflate.inflate(R.layout.waiting_cell, null);
		myProductList.addFooterView(footer);	
		mReceiver = new ServiceCallback(new Handler());
		mReceiver.setReceiver(this);		
		// Attaching Listeners
		//Setting up onScroll Listener
		myProductList.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.i("####","First Item Visible: " + firstVisibleItem);
				Log.i("####","Visible Item Count: " + visibleItemCount);
				Log.i("####","Total Item Visible: " + totalItemCount);
				
				if((firstVisibleItem+visibleItemCount) == totalItemCount && totalItemCount != 0)
				{
				   offset = totalItemCount;
			       if(!endofList){
			    	   isProductListRequested = true;
			    	   //disconnectMyService();
			    	   doBindService();
			       }else{
			    	   endofList = true;
			    	   try{myProductList.removeFooterView(footer);}catch(Exception e){e.printStackTrace();};
			    	   newSize=oldSize;
			       }
			    	  
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
			}
			
		});		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// Refreshing List, Only Executes when User has Deleted Saved Product from Saved Product Detail Screen
		Intent intent = getIntent();
		if(intent != null){
			String flag = "";
			String key = "";
			int index = 0;
			if(intent.hasExtra("Flag"))
               flag = intent.getStringExtra("Flag");
			if(flag.equals("Refresh List")){
				key = intent.getStringExtra("Key");
				if(AccountScreen.myProductList.size() > 0){
					for(Product product:AccountScreen.myProductList){
						
						if(key.equals(product.getParentKey())){
							AccountScreen.myProductList.remove(index);
							break;
						}
						index = index + 1;
					}
				}
			}
		}
		// Check if Size of List is Zero, if true Push Account Screen Displaying no Products Found Message
		if(AccountScreen.myProductList.size() < 1){
			customDialog = new CustomDialog(context,"Okay","",getString(R.string.no_saved_product_yet));
			final Intent intent1 = new Intent(context,AccountScreen.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			android.content.DialogInterface.OnClickListener listen = new android.content.DialogInterface.OnClickListener(){
			  @Override
			  public void onClick(DialogInterface arg0, int arg1) {
				startActivity(intent1);	
			  }
			};
			dialog = customDialog.getConfirmationPositiveDialogForActionCustomListeners(getString(R.string.no_products_found), listen);
			dialog.show();			
		}
		// If List Contains Data then, Retain Current Screen
		if(AccountScreen.myProductList.size() > 0){
		   // Check for End of List
		   if(AccountScreen.myProductList.size() < PocketTrader.LIST_LIMIT)	  
			   endofList = true;
		   
		   customProductList = new CustomPoductList(AccountScreen.myProductList,context);
	       myProductList.setAdapter(customProductList);
	       myProductList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Product product = (Product)adapter.getItemAtPosition(position);
				formData.putParcelable("Product", product);
				formData.putString(PocketTrader.SCREEN_TYPE, PocketTrader.MY_PRODUCT_DETAIL);
				Intent intent = new Intent(context,MyProductScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtras(formData);
				startActivity(intent);				
			}
	    	
	    });}
	    myProductList.refreshDrawableState(); 

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
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		if(resultCode == 0){

			processStatus = resultData.getInt("Result");
			doUnbindService();
		}		
	}
	
}
