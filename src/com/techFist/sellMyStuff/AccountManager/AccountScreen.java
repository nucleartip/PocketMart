package com.techFist.sellMyStuff.AccountManager;

import java.util.ArrayList;


import com.google.ads.AdView;
import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.Commons.Bid;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Commons.Product;
import com.techFist.sellMyStuff.Commons.QuickActionHandler;
import com.techFist.sellMyStuff.Services.ProductsService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.ResultReceiver;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


 class ServiceCallback extends ResultReceiver implements Parcelable{
	private Receiver mReceiver;
	public ServiceCallback(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }	
   
    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }	
    }

	
    public class AccountScreen extends PocketActivity implements ServiceCallback.Receiver{
  
	private Context context;
	private TextView welcomeMsg;
	private ListView accountOptionList;
	private Resources res;
	public static AsyncTask<String,Integer,Integer> productFetchTask;	
	public static ArrayList<Product> myProductList = new ArrayList<Product>();
	public static ArrayList<Product> mySavedList;
	public ServiceCallback mReceiver;
	private ProductsService localService;	
	private Bundle formData;
	private int processStatus = -404;
	private int bidListStatus = -404;
	private int savedListStatus = -404;
	private CustomDialog customDialog;
	private AlertDialog dialog;
	private AlertDialog waitDialog;
	private boolean isProductListRequested = false;
	private boolean isSavedProductListRequested = false;
	public static ArrayList<Bid> bidList;
	private ProgressBar bidFetchProgreesBar;
	private ViewGroup progressGroup;
	private TextView noBidFoundMsg;
	private ListView bidListView;
	private ImageView bidIndicator;
	private boolean isServiceRequested = false;
	private int offset = 0;
	private int limit = PocketTrader.LIST_LIMIT;

	
	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	     localService = ((ProductsService.LocalBinder)service).getService();
	     isServiceRequested = true;
	     localService.onCreate();
          ////System.out.println("####### Service Connected");
         Intent intent = new Intent();
         intent.putExtras(formData);
         
         if(isSavedProductListRequested){
 	    	isProductListRequested = false;
        	 
        	 savedListStatus = localService.onStartCommand(intent, 0, 10);
        	  ////System.out.println("#### Fetching");
             if(savedListStatus == -404)
            	 doUnbindService();          	 
         }

         else if(isProductListRequested){
             ////System.out.println("###### Request has been fired with Offset/Limit" + offset + "/" + limit);
 	    	isSavedProductListRequested = false;
            processStatus = localService.onStartCommand(intent, 0, 3);
            if(processStatus == -404)
           	 doUnbindService();            
         }
         else{
  	    	isSavedProductListRequested = false;
 	    	isProductListRequested = false;  	    	
        	 bidListStatus = localService.onStartCommand(intent, 0, 5);        	 
             if(bidListStatus == -404)
               	 doUnbindService();         
         }
         
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	isProductListRequested = false;
	    	isServiceRequested = false;
	    	isSavedProductListRequested = false;
	    	localService = null;

	    }
	};
	
	public void onBackPressed() {
		disconnectMyService();
	    finish();
		super.onBackPressed();
		
	}
	
	// Bind Service to Activity
	void doBindService() {
	    bindService(new Intent(AccountScreen.this,ProductsService.class), mConnection, Context.BIND_AUTO_CREATE);
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
    
	// UnBind Binding Service
	void doUnbindService() {
		
		// status 1, List retrived
		// status -1, No Product Found
		// status -500, Datastore Exception
		// status -404, Network Error
    if(mConnection !=null){
  	 if(isProductListRequested)	
	  {	
		 disconnectMyService();
		 isServiceRequested = false;
		 isProductListRequested = false;
		 if(processStatus == 1){
			waitDialog.cancel();
			// Push My Product List Screen
			Intent intent = new Intent(context,MyProductListScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		if(processStatus == -1){
			waitDialog.cancel();
			
			customDialog = new CustomDialog(context,"Okay","",getString(R.string.no_products_yet));
			dialog = customDialog.getInfoDialog("No Products Found");
			dialog.show();
		}
		if(processStatus == -500){
			waitDialog.cancel();
			customDialog = new CustomDialog(context,"Okay","",getString(R.string.datastore_error));
			dialog = customDialog.getErrorDialog("Datastore Error");
			dialog.show();
		}
		if(processStatus == -404){
			waitDialog.cancel();
			customDialog = new CustomDialog(context,"Okay","",getString(R.string.network_error));
			dialog = customDialog.getErrorDialog("Network Error");	
			dialog.show();
		}
	  }
	 else if(isSavedProductListRequested){
		 disconnectMyService();
		 isServiceRequested = false;

		 isSavedProductListRequested = false;
		  ////System.out.println("##### Saved list Requested");
		 if(savedListStatus == 1){
			waitDialog.cancel();
			// Push My Product List Screen
			Intent intent = new Intent(context,MySavedProductList.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		if(savedListStatus == -1){
			waitDialog.cancel();
			customDialog = new CustomDialog(context,"Okay","",getString(R.string.no_saved_product_yet));
			dialog = customDialog.getInfoDialog("No Products Found");
			dialog.show();
		}

		if(savedListStatus == -404){
			waitDialog.cancel();
			customDialog = new CustomDialog(context,"Okay","",getString(R.string.network_error));
			dialog = customDialog.getErrorDialog("Network Error");	
			dialog.show();
		}		 
	 }
	 else
	 {
		 disconnectMyService();
		 isServiceRequested = false;

		    // Service Not Available
		    if(bidListStatus == -404){
		    	bidFetchProgreesBar.setVisibility(View.INVISIBLE);
		    	bidFetchProgreesBar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
		    	bidFetchProgreesBar.setEnabled(false);
		    	bidIndicator.setImageResource(R.drawable.network_error);
		    	noBidFoundMsg.setText("Unable to Fetch List");
		    	noBidFoundMsg.setLayoutParams(new  LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		    	
		    	//bidIndicator.setMaxHeight(LayoutParams.WRAP_CONTENT);
		    }
		    // No Present Bids on Product
		    if(bidListStatus == -1){
		    	bidFetchProgreesBar.setVisibility(View.INVISIBLE);
		    	bidFetchProgreesBar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
		    	bidFetchProgreesBar.setEnabled(false);
		    	bidIndicator.setImageResource(R.drawable.no_data_found);
		    	noBidFoundMsg.setText("No Bids yet");
		    	noBidFoundMsg.setLayoutParams(new  LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));	    	
		    	
		    	//bidIndicator.setMaxHeight(LayoutParams.WRAP_CONTENT);
		    
		    }
		    // Bids fetched
		    if(bidListStatus == 1){
		    	if(bidList != null){
			    	bidFetchProgreesBar.setVisibility(View.INVISIBLE);
			    	bidFetchProgreesBar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
			    	bidFetchProgreesBar.setEnabled(false);
			    	bidIndicator.setLayoutParams(new LinearLayout.LayoutParams(0,0));
			    	bidIndicator.setEnabled(false);
			    	bidIndicator.setVisibility(View.INVISIBLE);		    	
			    	noBidFoundMsg.setVisibility(View.INVISIBLE);
			    	noBidFoundMsg.setText("No Bids yet");
			    	noBidFoundMsg.setLayoutParams(new  LinearLayout.LayoutParams(0,0));	
			    	progressGroup.setLayoutParams(new  LinearLayout.LayoutParams(0,0));
			    	// Setting up adapter for Bids
			    	bidListView.setAdapter(new CustomBidAdapter(bidList));
		    	}
		    	
		    }		 
	      }
       }// Main If
    }
	
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    //setContentView(R.layout.my_account_form);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.my_account_form);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,AccountScreen.class));
	    
	    // Fetching form Objetcs
	    welcomeMsg = (TextView) findViewById(R.id.account_welcome_message);
	    accountOptionList = (ListView)findViewById(R.id.account_option_list);
	    context = this;
	    res = getResources();
		progressGroup = (ViewGroup)findViewById(R.id.account_progressGroup);
		noBidFoundMsg = (TextView)findViewById(R.id.account_no_bid_found);
		bidListView = (ListView)findViewById(R.id.account_bidList);
		bidFetchProgreesBar = (ProgressBar)findViewById(R.id.account_progress_bar);
		bidIndicator = (ImageView) findViewById(R.id.account_indicator);
		
	    formData = new Bundle();
        mReceiver = new ServiceCallback(new Handler());
        mReceiver.setReceiver(this); 

    	bidListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bid bid = (Bid)parent.getItemAtPosition(position);
				if(bid.isPostedByPhoneOption()){
                	
                	//action = quickHdlr.getQuickActionWithPhone(context, currentBidSelection);
                	
                }else{
                	//action = quickHdlr.getQuickActionWithoutPhone(context,currentBidSelection);
                	
                }
				//action.show(view);
			}
		});
    	
    	// Registering for Context Menu
    	 registerForContextMenu(bidListView);
    		
	}
	
	
	// Creating Context Menu
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			super.onCreateContextMenu(menu, v, menuInfo);
			if(v.getId() == bidListView.getId()){
				// This Menu is getting triggered on Tapping a Bid Item
				// This tells Which Item was Clicked Indeed
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
				CustomBidAdapter adapter = (CustomBidAdapter)bidListView.getAdapter();
				Bid bid = adapter.getItem(info.position);
				if(bid.isPostedByPhoneOption()){
					// Pop up Menu With Call Option
				    menu.setHeaderTitle("Action");
				    String[] menuItems = getResources().getStringArray(R.array.callmenu);
				    for (int i = 0; i<menuItems.length; i++) {
				      menu.add(Menu.NONE, i, i, menuItems[i]);
				    }
				    
				}else{
					// Menu Without Call Option
				    menu.setHeaderTitle("Action");
				    String[] menuItems = getResources().getStringArray(R.array.noncallmenu);
				    for (int i = 0; i<menuItems.length; i++) {
				      menu.add(Menu.NONE, i, i, menuItems[i]);
				    }					
				}
				
			}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		  int menuItemIndex = item.getItemId();
		  QuickActionHandler quickHdlr = QuickActionHandler.getInstance(context);
		  CustomBidAdapter adapter = (CustomBidAdapter)bidListView.getAdapter();
		  Bid bid = adapter.getItem(info.position);
		  String menuItemName = "";
		  if(bid.isPostedByPhoneOption()){
			  String[] menuItems = getResources().getStringArray(R.array.callmenu);
			  menuItemName = menuItems[menuItemIndex];
			  
		  }else{
			  String[] menuItems = getResources().getStringArray(R.array.noncallmenu);
			  menuItemName = menuItems[menuItemIndex];			  
		  }
		   ////System.out.println("#### Menu Item Selected/Posted by Option:" + menuItemName +"/"+bid.isPostedByPhoneOption());
		  quickHdlr.quickActionHandler(bid, menuItemName);
		  return true;
    }
	
	
	@Override
		protected void onPause() {
			// TODO Auto-generated method stub
		    disconnectMyService();
			super.onPause();
		}
	@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			AccountScreen.myProductList = null;
			AccountScreen.mySavedList = null;			
		}
	@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
			if(isServiceRequested){

			 try{
					unbindService(mConnection);
					mConnection = null;
					
				 }
				 catch(Exception e){
					 
				 }
			}
		}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
	    super.onStart();		
		welcomeMsg.setText("Welcome  "+PocketTrader.user.getUserName());
        // Doing Service Call for fetching Bids made for Products posted by Current User
		isProductListRequested = false;
		formData.putString("Owner", PocketTrader.user.getUserEmail());	
		formData.putParcelable("Receiver", mReceiver);
		doBindService();
		// Setting up Listener for Fetching My Products List
		accountOptionList.setAdapter(new AccountTextAdapter(res.getStringArray(R.array.my_account_options)));
	    accountOptionList.setOnItemClickListener(new OnItemClickListener(){
		@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
                // Push My Products Screen
				if(adapter.getItemAtPosition(position).equals("My Products")){
				   formData.putString("Email", PocketTrader.user.getUserEmail());
				   formData.putString("Offset", String.valueOf(offset));
				   formData.putString("Limit", String.valueOf(limit));
				   formData.putParcelable("Receiver", mReceiver);
				   customDialog = new CustomDialog(context,"","","Working..");
				   waitDialog = customDialog.getWaitDialog("Fetching Product List",true);
				   waitDialog.show();
				   isProductListRequested = true;
				   disconnectMyService();
				   doBindService();
				    ////System.out.println("####### Firing Request");
				}
				if(adapter.getItemAtPosition(position).equals("My Profile")){
					Intent intent = new Intent(context,MyProfileScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				if(adapter.getItemAtPosition(position).equals("Saved Products")){
					formData.putString("Email", PocketTrader.user.getUserEmail());	
					formData.putParcelable("Receiver", mReceiver);
					customDialog = new CustomDialog(context,"","","Working..");
					waitDialog = customDialog.getWaitDialog("Fetching Product List",true);
					waitDialog.show();					
					isSavedProductListRequested = true;
					disconnectMyService();
					doBindService();
				}
			}
	    	
	    });

	}
	
	class AccountTextAdapter extends BaseAdapter{
		
		private ImageView ApplicationImageCell;
		private TextView ApplicationTextCell;
		private String[] menu;

		public AccountTextAdapter( String[] strings){
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
	     	   
		     	ApplicationImageCell = (ImageView)reuse.findViewById(R.id.my_account_option_image);
		     	ApplicationTextCell = (TextView)reuse.findViewById(R.id.my_account_option_item);    
	        	ApplicationTextCell.setText(getItem(position));
	        	if(getItem(position).equals("My Products"))
	        		ApplicationImageCell.setImageResource(R.drawable.my_product);
	        	if(getItem(position).equals("Saved Products"))
	        		ApplicationImageCell.setImageResource(R.drawable.saved_products);
	        	if(getItem(position).equals("My Profile"))
	        		ApplicationImageCell.setImageResource(R.drawable.my_account);
	        	return reuse;
	        }
	        else
	        {
	     	  ViewGroup item = getViewGroup(reuse, parent);
	     	  ApplicationImageCell = (ImageView)item.findViewById(R.id.my_account_option_image);
	     	  ApplicationTextCell = (TextView)item.findViewById(R.id.my_account_option_item);        	   
	      	  ApplicationTextCell.setText(getItem(position));
	        	if(getItem(position).equals("My Products"))
	        		ApplicationImageCell.setImageResource(R.drawable.my_product);
	        	if(getItem(position).equals("Saved Products"))
	        		ApplicationImageCell.setImageResource(R.drawable.saved_products);
	        	if(getItem(position).equals("My Profile"))
	        		ApplicationImageCell.setImageResource(R.drawable.my_account);	      	  
	          return item;           
	        }

		}
		
		private ViewGroup getViewGroup(View reuse,ViewGroup parent){
			if(reuse instanceof ViewGroup)
				return (ViewGroup)reuse;
			Context context = parent.getContext();
			LayoutInflater inflater = LayoutInflater.from(context);
			ViewGroup item = (ViewGroup)inflater.inflate(R.layout.my_account_option_cell,null);
			return item;
	        
	    
		}
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		if(resultCode == 0){

			processStatus = resultData.getInt("Result");
			doUnbindService();
		}
		if(resultCode == 1){
			bidListStatus = resultData.getInt("Result");
			doUnbindService();
		}
        if(resultCode == 2){
        	savedListStatus = resultData.getInt("Result");
        	 ////System.out.println("##### Saved List Status" + savedListStatus);
        	doUnbindService();
        }
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
		
} // Class Close
