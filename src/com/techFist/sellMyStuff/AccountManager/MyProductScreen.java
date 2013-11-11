package com.techFist.sellMyStuff.AccountManager;

import java.util.ArrayList;

import com.google.ads.AdView;
import com.techFist.sellMyStuff.ImageLoader;
import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.ServiceCallback;
import com.techFist.sellMyStuff.Commons.Bid;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Commons.Product;
import com.techFist.sellMyStuff.Commons.QuickActionHandler;
import com.techFist.sellMyStuff.Services.ProductsService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyProductScreen extends PocketActivity implements ServiceCallback.Receiver {
    

	public ImageLoader imageLoader;
	private Context context;
	private Bundle formData;
	private TextView productName;
	private TextView productDate;
	private TextView productPrice;
	private ImageView productImage;
	private Button productRemover;
	private Button submitBid;
	private ProgressBar bidFetchProgreesBar;
	private ViewGroup progressGroup;
	private TextView noBidFoundMsg;
	private ListView bidListView;
	private ImageView bidIndicator;
	private Product product;
	private CustomDialog customDialog;
	private AlertDialog dialog;
	private int bidListStatus = -404;
	private int productDeleteStatus = -404;
	private int bidPostStatus = -404;
	public static ArrayList<Bid> bidList;
	private ProductsService localService;
	public ServiceCallback mReceiver;	
	boolean isServiceRequested = false;
	boolean isBidListRequested = false;
	boolean isProductDeleteRequested = false;
	boolean isBidPostRequested = false;
	
    private static final int DIALOG_BID_ID = 1;	
	private Activity activity;
	private View bidView;
	
	String screenType = "";
	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	 	    localService = ((ProductsService.LocalBinder)service).getService();

            localService.onCreate();
	    	isServiceRequested = true;
            if(isBidListRequested){ 
            	isProductDeleteRequested = false; 
            	isBidPostRequested = false;
                bidListStatus = localService.onStartCommand(new Intent().putExtras(formData), 0, 4);
                if(bidListStatus == -404)
            	doUnbindService();
	    
            }
            if(isProductDeleteRequested){
            	isBidListRequested = false;
            	isBidPostRequested = false;
            	productDeleteStatus = localService.onStartCommand(new Intent().putExtras(formData), PocketTrader.MY_PRODUCT_DETAIL_SCREEN, 11);
                if(productDeleteStatus == -404)
            	doUnbindService();            	
            }
            if(isBidPostRequested){
 
            	isBidListRequested = false;
            	isProductDeleteRequested = false;            	
            	bidPostStatus = localService.onStartCommand(new Intent().putExtras(formData), PocketTrader.MY_PRODUCT_DETAIL_SCREEN, 2);

            	if(bidPostStatus == -404)
            	   doUnbindService();             	
            }
            
            
            
            
	    }
	public void onServiceDisconnected(ComponentName className) {
	    	localService = null;
	    	isServiceRequested = false;
	    	isProductDeleteRequested = false;
	    	isBidListRequested = false;
	    	isBidPostRequested = false;
	    }
	};	
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
	// Bind Service to Activity
	void doBindService() {
	    bindService(new Intent(MyProductScreen.this,ProductsService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	// Un Binding Service
	void doUnbindService() {
	     // Detach our existing connection.
     
	if(mConnection != null){

	    if(isBidListRequested && isServiceRequested){
			isServiceRequested = false;
			disconnectMyService();
	    	isBidListRequested = false;
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
	    if(isProductDeleteRequested){
	    	disconnectMyService();
	    	isProductDeleteRequested = false;
	    	dialog.cancel();
	        /* deleteProductStatus, 1 if product has been Deleted succesfuly
	         * deleteProductStatus, -500, if datastore exception occurs, Product is Not deleted
	         * deleteProductStatus, -404, if service is down.
	         */
	    	if(productDeleteStatus == -404){
				customDialog = new CustomDialog(context,"Okay","",getString(R.string.network_error));
				dialog = customDialog.getErrorDialog("Network Error");	
				dialog.show();	    		
	    	}
			if(productDeleteStatus == -500){
				customDialog = new CustomDialog(context,"Okay","",getString(R.string.datastore_error));
				dialog = customDialog.getErrorDialog("Datastore Error");
				dialog.show();
			}	
			if(productDeleteStatus == 1){
				customDialog = new CustomDialog(context,"Okay","",getString(R.string.product_deleted));
				final Intent intent = new Intent(context,MyProductListScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("Flag", "Refresh List");
				intent.putExtra("Key",product.getParentKey());
				
				android.content.DialogInterface.OnClickListener listen = new android.content.DialogInterface.OnClickListener(){
				  @Override
				  public void onClick(DialogInterface arg0, int arg1) {
					startActivity(intent);	
				  }
				};
				dialog = customDialog.getConfirmationPositiveDialogForActionCustomListeners("Product Deleted", listen);
				dialog.show();
			}
	    }	
	    if(isBidPostRequested){
	    	disconnectMyService();
	    	isBidPostRequested = false;
	    	dialog.cancel();
		    if(bidPostStatus == 0){
			   Toast.makeText(context,"Hurray!! your Bid has been posted.", Toast.LENGTH_LONG).show();
			   refreshBidData();
			}
			if(bidPostStatus == -500){
			  customDialog = new CustomDialog(context,"Okay","Home",getString(R.string.bid_post_error));
			  dialog = customDialog.getErrorDialog("Datastore Error");
			  dialog.show();
			       	
			}   
			if(bidPostStatus == -404){
			   customDialog = new CustomDialog(context,"Okay","",getString(R.string.network_error));
			   dialog = customDialog.getErrorDialog("Unknown Excecption");
			   dialog.show();
			}    	
	    }
     }
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
    
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		disconnectMyService();
		finish();
		 
	}
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    //setContentView(R.layout.my_product_detail_screen);
	    
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.my_product_detail_screen);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,MyProductScreen.class));
	    
	    
	    // Initializing form Objects
		context = this;
	    activity = this;		
		formData = new Bundle();
		imageLoader=new ImageLoader(context.getApplicationContext());
		
		productName = (TextView)findViewById(R.id.my_product_detail_title);
		productDate = (TextView)findViewById(R.id.my_product_detail_date);
		productPrice = (TextView)findViewById(R.id.my_product_detail_price);
		productImage = (ImageView)findViewById(R.id.my_product_detail_image);
		productRemover = (Button)findViewById(R.id.my_product_remove);	
		submitBid = (Button)findViewById(R.id.my_product_submit_bid);
		progressGroup = (ViewGroup)findViewById(R.id.my_product_progressGroup);
		noBidFoundMsg = (TextView)findViewById(R.id.my_product_no_bid_found);
		bidListView = (ListView)findViewById(R.id.my_product_bidList);
		bidFetchProgreesBar = (ProgressBar)findViewById(R.id.my_product_progress_bar);
		bidIndicator = (ImageView) findViewById(R.id.my_product_bid_indicator);
	    mReceiver = new ServiceCallback(new Handler());
	    mReceiver.setReceiver(this);	    
        // Inflating layout bid dialog box
        LayoutInflater li = LayoutInflater.from(this);
        bidView = li.inflate(R.layout.bid_dialog_form, null);
	    // fetching form Intent
		if(getIntent() != null){
		   formData = getIntent().getExtras();
		   product = (Product)formData.getParcelable("Product");
		   screenType = formData.getString(PocketTrader.SCREEN_TYPE);
		   
		} 
	    
	    // Setting up Listeners
		// Remove producy
	    productRemover.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				formData.putString("Key",product.getParentKey());
				formData.putParcelable("Receiver", mReceiver);
				customDialog = new CustomDialog(context,"","",getString(R.string.working));
				dialog = customDialog.getWaitDialog(getString(R.string.wait),true);
				dialog.show();
				disconnectMyService();
				isProductDeleteRequested = true;
				doBindService();				
			}
	    	
	    });
        // Submit Bid
	    submitBid.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				activity.showDialog(DIALOG_BID_ID);					
			}
	    	
	    });
       // Bid View Listener
    	bidListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bid bid = (Bid)parent.getItemAtPosition(position);
				//Bid currentBidSelection = bid;
				//QuickAction action;
				//QuickActionHandler quickHdlr = QuickActionHandler.getInstance(context);
				//Toast.makeText(context,"Posted by phone Option:" + bid.isPostedByPhoneOption(),Toast.LENGTH_SHORT).show();
				if(bid.isPostedByPhoneOption()){
                	
                	//action = quickHdlr.getQuickActionWithPhone(context, currentBidSelection);
                	
                }else{
                	//action = quickHdlr.getQuickActionWithoutPhone(context,currentBidSelection);
                	
                }
				//action.show(view);
			}
		});	    
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
		  // ////System.out.println("#### Menu Item Selected/Posted by Option:" + menuItemName +"/"+bid.isPostedByPhoneOption());
		  quickHdlr.quickActionHandler(bid, menuItemName);
		  return true;
    }
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(product!=null){
		  productName.setText(product.getProductName());
		  productPrice.setText(product.getProductPrice());
		  productDate.setText(product.getProductPostedDate());
     	  // Checking for Available Image Url
     	  if (!product.isImageAvailable())
     		productImage.setImageResource(R.drawable.image_temp); 
     	  else{
     	      imageLoader=new ImageLoader(context.getApplicationContext());  
     		  imageLoader.DisplayImage(product.getProductImages().get(0), productImage);	     		  
     	  }
		}

		// Code for fetching Bid's on this Product
		isBidListRequested = true;
		formData.putString("Key",product.getParentKey());
		formData.putParcelable("Receiver", mReceiver);
		doBindService();
	}
	
	// Method for Reloading Bid Posts
	private void refreshBidData(){
		isBidListRequested = true;
		formData.putString("Key",product.getParentKey());
		formData.putParcelable("Receiver", mReceiver);
		doBindService();		
	}
	
    // Code for Executing Bid Submission
	@Override
	protected Dialog onCreateDialog(int id) {
	   switch (id) {
	    case DIALOG_BID_ID:
	    return createBidDialog();
 	   }
	   return null;
	}	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
	  switch (id) {
	    case DIALOG_BID_ID:
	    prepareAlertDialog(dialog);
	  }
	}
	private void prepareAlertDialog(Dialog d) {

	  TextView quote = (TextView)bidView.findViewById(R.id.bid_product_quote);
	  TextView productName = (TextView)bidView.findViewById(R.id.bid_product_name);
	  productName.setText(product.getProductName());
	  quote.setText("");
	  //change something about this dialog
	}
    private Dialog createBidDialog()
	{
	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  builder.setTitle("Post your BID");
	  builder.setView(bidView);   
	  BidListener listener = new BidListener(bidView);
	  builder.setPositiveButton("Bid", listener);
	  builder.setNegativeButton("Cancel", listener);
	  AlertDialog ad = builder.create();
	  return ad;
	}    
    // Bid Post Listener, Takes argument as view it is operating upon
    class BidListener implements android.content.DialogInterface.OnClickListener{
    

    private TextView quote;
    
    public BidListener(View v){

        quote = (TextView)v.findViewById(R.id.bid_product_quote);
        
    }
	
    @Override
	public void onClick(DialogInterface v, int buttonId) {
		// TODO Auto-generated method stub
	   	if(Dialog.BUTTON_POSITIVE == buttonId){
	   		
	   		formData.putString("ProductName", product.getProductName());
	   		formData.putString("Email",PocketTrader.user.getUserEmail());
	   		formData.putString("Quote", quote.getText().toString());
	   		formData.putString("Key", product.getParentKey());
	   		formData.putString("Owner",product.getOwnerID());
	   		formData.putString("Name", PocketTrader.user.getUserName());
	   		formData.putString("PostedByPhone", PocketTrader.user.getUserPhone());
	   		formData.putString("PostedByPhoneOption",String.valueOf(PocketTrader.user.getPhoneOption()));
	   		formData.putParcelable("Receiver", mReceiver);
			customDialog = new CustomDialog(context,"","",getString(R.string.working));
			dialog = customDialog.getWaitDialog("Please Wait",true);
			dialog.show();
	   		isBidPostRequested = true;
	   		doBindService();	   		
	   	}
	   	else{
	   		
	   	}
	}
    	
    }	
	
	
	
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		// Fetching Bid List
		if(resultCode == 0){
			bidListStatus = resultData.getInt("Result");
			doUnbindService();
		}
		// Deleting Product
		if(resultCode == 1){
			productDeleteStatus =  resultData.getInt("Result");
			doUnbindService();
		}
		// Posting a Bid
		if(resultCode == 2){
			bidPostStatus =  resultData.getInt("Result");
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

}
