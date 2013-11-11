package com.techFist.sellMyStuff;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.ads.AdView;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.ImageAdapter;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Commons.Product;
import com.techFist.sellMyStuff.MapManager.BusinessMap;
import com.techFist.sellMyStuff.Services.ProductsService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailScreen extends PocketActivity implements ServiceCallback.Receiver{
   

	private Context context; 
	private Bundle formData;
	private Product product;
	private TextView productName;
	private TextView productPrice;
	private TextView productDate;

    private ImageView favStar;
	private boolean starStatus = false;
	private Gallery gallery;
	private ArrayList<Bitmap> imageList = new ArrayList<Bitmap>();
	private CustomDialog customDialog;
	private AlertDialog waitDialog;
	private TextView productCategory;
	private TextView productType;
	private TextView productDescription;
	private TextView productLocation;

	private Button productMap;
	private Button productBid;
	private Button contactBtn;
	private Button sendMessage;
    private static final int DIALOG_BID_ID = 1;	
	private Activity activity;
	private View bidView;
	private TextView bidProductLabel;

	private ProductsService localService;
	private int postStatus = -404;
	private int saveStatus = -404;
	private int deleteStatus = -404;
	private int checkFavStatus = -404;
	public ServiceCallback mReceiver;	

	
	private AlertDialog dialog;
	
	private boolean isBidServiceRequest = false;
	private boolean isSaveProductServiceRequested = false;
	private boolean isDeleteProductServiceRequested = false;
	private boolean isFavProduct = false;
	private boolean isServiceRequested = false;
	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	    	localService = ((ProductsService.LocalBinder)service).getService();
	    	isServiceRequested = true;
	    	localService.onCreate();

            if(isBidServiceRequest) {
              // Popping up Wait Dialog
              waitDialog  = new CustomDialog(context,"","","Working").getWaitDialog("Please Wait Posting",true);
              waitDialog.show();            	
              postStatus = localService.onStartCommand(new Intent().putExtras(formData), 0, 2);
              if(postStatus == -404)
                doUnbindService();
            }
            if(isSaveProductServiceRequested) {
            	saveStatus = localService.onStartCommand(new Intent().putExtras(formData), 0, 7);
                if(saveStatus == -404)
                  doUnbindService();
              }  
            if(isDeleteProductServiceRequested){
            	deleteStatus = localService.onStartCommand(new Intent().putExtras(formData), 0, 8);
                if(deleteStatus == -404)
                  doUnbindService();            	
            }
            if(isFavProduct){
            	checkFavStatus = localService.onStartCommand(new Intent().putExtras(formData), 0, 9);
                if(checkFavStatus == -404)
                  doUnbindService();            	
            }
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	isSaveProductServiceRequested = false;
	    	isBidServiceRequest  = false;
	    	isDeleteProductServiceRequested = false;
	    	isFavProduct = false;
	    	localService = null;
	    	isServiceRequested = false;
	    }
	};
	
	// Bind Service to Activity
	void doBindService() {
	    bindService(new Intent(ProductDetailScreen.this,ProductsService.class), mConnection, Context.BIND_AUTO_CREATE);
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
    
	// Un Binding Service
	void doUnbindService() {
	     // Detach our existing connection.
		disconnectMyService();
	    isServiceRequested = false;
	    /* returns 1, if product is saved for user
	     * returns -1, if product is not saved for user
	     * return -500, if datastore error
	     * return -404, if Service is Down
	     */	    
	    if(isFavProduct){
	    	isFavProduct = false;
	    	
	    	if(checkFavStatus == 1){
				  starStatus = true;
				  favStar.setImageResource(R.drawable.five_star);
				  favStar.refreshDrawableState();
				  favStar.setEnabled(true);
				  favStar.setVisibility(View.VISIBLE);				  
	    	}		    	
	    	
	    	else if(checkFavStatus == -1){
				  starStatus = false;
				  favStar.setImageResource(R.drawable.empty_star);
				  favStar.refreshDrawableState();	
				  favStar.setEnabled(true);
				  favStar.setVisibility(View.VISIBLE);
	    	}	 
	    	else{
				  starStatus = false;
				  favStar.setEnabled(false);
				  favStar.setVisibility(View.INVISIBLE);
	    	}
	    }
	    
	    
	    /* returns 1 of product has been saved succesfuly
	     * returns -500, if datastore exception occurs, product hasnt been saved succesfully
	     * return -404, if service is down.
	     */
	    if(isDeleteProductServiceRequested){
	    	isDeleteProductServiceRequested = false;
	    	if(deleteStatus == 1){
				  starStatus = false;
				  favStar.setImageResource(R.drawable.empty_star);
				  favStar.refreshDrawableState();	    		
	    	}
	    	else if(deleteStatus == -500){
	    		Toast.makeText(context,"Please try again,Datastore Error", Toast.LENGTH_LONG).show();
				  starStatus = true;
				  favStar.setImageResource(R.drawable.five_star);
				  favStar.refreshDrawableState();
	    	}
	    	else if(deleteStatus == -1){
	    		
	    	}
	    	else{
				  starStatus = false;
				  favStar.setImageResource(R.drawable.empty_star);
				  favStar.refreshDrawableState();	    		
	    	}	    	
	    }

	    /* returns 1 of product has been saved succesfuly
	     * returns -500, if datastore exception occurs, product hasnt been saved succesfully
	     * return -1, if save limit has been excceded cannot save any more products
	     * return -404, if service is down.
	     */	    
	    if(isSaveProductServiceRequested){
	    	isSaveProductServiceRequested = false;
	    	if(saveStatus == 1){
				  starStatus = true;
				  favStar.setImageResource(R.drawable.five_star);
				  favStar.refreshDrawableState();	    		
	    	}
	    	else if(saveStatus == -1){
	    		Toast.makeText(context,"Oops,You can Save only 20 Products", Toast.LENGTH_SHORT).show();
	    	}
	    	else if(saveStatus == -500){
	    		Toast.makeText(context,"Please try again,Datastore Error", Toast.LENGTH_LONG).show();
				  starStatus = false;
				  favStar.setImageResource(R.drawable.empty_star);
				  favStar.refreshDrawableState();	    		
	    	}
	    	else{
				  starStatus = true;
				  favStar.setImageResource(R.drawable.five_star);
				  favStar.refreshDrawableState();	    		
	    	}
	    }
	    //postStatus = 0, Success take user back to Home Screen
	    //postStatus = -1, DataStore exception, ask user to resubmit
	    //postStatus = -404, Popup Unknown Error exception and take user to Home Screen.
	    
	    if(isBidServiceRequest){
	    	isBidServiceRequest = false;
	      if(postStatus == 0){
	    	 waitDialog.cancel();
	    	 Toast.makeText(context,"Hurray!! your Bid has been posted.", Toast.LENGTH_LONG).show();
	      }
	      if(postStatus == -1){
	    	waitDialog.cancel();
	    	customDialog = new CustomDialog(context,"Okay","Home","Oops,We Experienced Problem in Saving your Data pelease Retry !!");
	       	dialog = customDialog.getErrorDialog("Datastore Error");
	       	dialog.show();
	       	
	      }   
	      if(postStatus == -404){
	    	waitDialog.cancel();
	    	customDialog = new CustomDialog(context,"Okay","","Oops, Network Error Please check if you have Active Network Connection ..");
	    	dialog = customDialog.getErrorDialog("Unknown Excecption");
	    	dialog.show();
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,ProductDetailScreen.class));
		
	    //setContentView(R.layout.product_detail_screen);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.product_detail_screen);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
	    
		
		context = this;
	    activity = this;
	    mReceiver = new ServiceCallback(new Handler());
	    mReceiver.setReceiver(this);
	    if(getIntent() != null){
	    	formData = getIntent().getExtras();
	    	product = (Product)formData.getParcelable("ProductDetail");
	    }
	    // Fetching Form Objects
	    productName = (TextView)findViewById(R.id.product_detail_title);
	    productPrice = (TextView)findViewById(R.id.product_detail_price);
	    productDate = (TextView)findViewById(R.id.product_detail_date);
	    
        favStar = (ImageView)findViewById(R.id.product_detail_fav_start);
        gallery = (Gallery)findViewById(R.id.product_detail_image_crousal);
        
        productCategory = (TextView)findViewById(R.id.product_detail_Category);
        productDescription = (TextView)findViewById(R.id.product_detail_Description);
        productLocation = (TextView)findViewById(R.id.product_detail_Location);
        productMap = (Button)findViewById(R.id.detail_direction);
        productBid = (Button)findViewById(R.id.detail_bid);
        contactBtn = (Button)findViewById(R.id.detail_contact);
        sendMessage = (Button)findViewById(R.id.detail_message);
        
        // Inflating layout bid dialog box
        LayoutInflater li = LayoutInflater.from(this);
        bidView = li.inflate(R.layout.bid_dialog_form, null);
        bidProductLabel = (TextView)bidView.findViewById(R.id.bid_product_name);
        // Setting Listeners
        // Sendind Personal Message
        sendMessage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				customDialog = new CustomDialog(context,"Send","Cancel","");
				Bundle b = new Bundle();
				b.putString("EmailTo", product.getOwnerID());
				b.putString("EmailFrom", PocketTrader.user.getUserEmail());
			
				dialog = customDialog.getPostMessageDialog(b, context, "Send Message");
				dialog.show();
			}
        	
        });
        
        // Logic for Calling Map
        productMap.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			  Intent intent = new Intent(context,BusinessMap.class);
			  int latitude = (int) (Double.valueOf(product.getProductLatitude()).doubleValue() * 1E6);
			  int longitude = (int) (Double.valueOf(product.getProductLongitude()).doubleValue() * 1E6); 
			  formData.putInt("IntLatitude",latitude);
			  formData.putInt("IntLongitude", longitude);
			  intent.putExtras(formData);
			  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			  startActivity(intent);
			
			}
        	
        });
        // Submitting a Bid Listener
        productBid.setOnClickListener(new OnClickListener(){

        	@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.showDialog(DIALOG_BID_ID);
			}
        	
        });
        
	    // Fav Start on CLick
	    favStar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(starStatus == false){
					isSaveProductServiceRequested = true;
			   		formData.putString("Email",PocketTrader.user.getUserEmail());
			   		//Log.i////System.out.println("####", PocketTrader.user.getUserEmail());
                    formData.putString("Key", product.getParentKey());
			   		formData.putParcelable("Receiver", mReceiver);		
			   		favStar.setImageResource(R.drawable.five_star);
					doBindService();
                }
				else{
					isDeleteProductServiceRequested = true;
			   		formData.putString("Email",PocketTrader.user.getUserEmail());
                    formData.putString("Key", product.getParentKey());
			   		formData.putParcelable("Receiver", mReceiver);
			   		favStar.setImageResource(R.drawable.empty_star);
					doBindService();
					
				}
			}
	    });
        // Login for Contact
        contactBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				customDialog = new CustomDialog(context,"Okay","Cancel","");
				dialog = customDialog.getContactTypeChooserDialog("Choose you Contact Option",PocketTrader.user.getUserName(),product.getOwnerID(),
						product.getOwnerNumber(),product.getPostedByPhoneOption());
				dialog.show();
			}
        	
        });	

     	
        // Spawning Thread to Refresh, Gallery with Downloaded Images
        imageList = new ArrayList<Bitmap>();

        
	 }
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
	  quote.setText("");
	  //change something about this dialog
	}	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
		// Populating Objects
	    productCategory.setText(product.getProductCategory()+","+product.getProductType());
		productDescription.setText(product.getProductDescription());
		productLocation.setText(product.getProductLocation());
		productName.setText(product.getProductName());
		productPrice.setText(product.getProductPrice());
		productDate.setText(product.getProductPostedDate());
		bidProductLabel.setText(product.getProductName());
      
        // Enabling Diabling Buttons
        if(product.getOwnerID().equals(PocketTrader.user.getUserEmail())){
          productBid.setEnabled(false);
          contactBtn.setEnabled(false);
          sendMessage.setEnabled(false);
        }
        
        // Setting up Adapter for Gallery
        int[] id = {R.drawable.refresh,R.drawable.refresh,R.drawable.refresh};
        gallery.setAdapter(new ImageAdapter(id,context));
       
 
        
	    // Check fav Start Staus
	    isFavProduct = true;
	    favStar.setVisibility(View.INVISIBLE);
   		formData.putString("Email",PocketTrader.user.getUserEmail());
        formData.putString("Key", product.getParentKey());
   		formData.putParcelable("Receiver", mReceiver);		    
	    doBindService();
	    
	    
	    super.onStart();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// Starting Images Download
		if(imageList.size() == 0 && product.getProductImages().size() > 0){ 
		  ArrayList<String> url = product.getProductImages();
		  new DownloadImages().execute(url);
		}  
		  super.onResume();
	}
 
    // Class for Downloading Images and populating Bitmaps
    class DownloadImages extends AsyncTask<ArrayList<String>,Integer,Integer>{

		@Override
		protected Integer doInBackground(ArrayList<String>... arg0) {
			ArrayList<String> url = arg0[0];
			for(String urls : url){
				URL ulrn;
				try{
					ulrn = new URL(urls.replace("=s128", "=s256"));
					HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
					InputStream is = con.getInputStream();
					Bitmap map = BitmapFactory.decodeStream(is);
					if(map != null){
						imageList.add(map);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
            if(imageList.size() > 0)
            	return 1;   // Images has been downloaded
            else
            	return -1;  // No images has been downloaded
            
		}
        @Override
        protected void onPostExecute(Integer result) {
	       // TODO Auto-generated method stub
	       super.onPostExecute(result);
	       if(result == 1){  // refresh the Gallery
	    	   gallery.setAdapter(new ImageAdapter(context,imageList));
	    	   gallery.refreshDrawableState();
	       }
	       if(result == -1){
	           int[] id = {R.drawable.refresh,R.drawable.refresh,R.drawable.refresh};
	           gallery.setAdapter(new ImageAdapter(id,context));
	    	    	   
	       }
        }
    }
    
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }    
	// Method return Bid dialog
    private Dialog createBidDialog()
	{
	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  builder.setTitle("Post you BID");
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
	   		doBindService();
	   		isBidServiceRequest = true;
	   	}
	   	else{
	   		
	   	}
	}
    	
    }
    // Callback called from Service.
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		
		if(resultCode == 0){

			//formData.putString("Key", callBackBundle.getString("Key"));
			postStatus = resultData.getInt("Result");
			doUnbindService();			
		}
		if(resultCode == 1){

			//formData.putString("Key", callBackBundle.getString("Key"));
			saveStatus = resultData.getInt("Result");
			doUnbindService();			
		}
		if(resultCode == 2){

			//formData.putString("Key", callBackBundle.getString("Key"));
			saveStatus = resultData.getInt("Result");
			doUnbindService();			
		}	
		if(resultCode == 3){

			//formData.putString("Key", callBackBundle.getString("Key"));
			checkFavStatus = resultData.getInt("Result");
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
