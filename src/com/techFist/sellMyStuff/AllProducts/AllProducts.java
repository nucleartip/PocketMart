package com.techFist.sellMyStuff.AllProducts;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.ProductDetailScreen;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.CustomPoductList;
import com.techFist.sellMyStuff.Commons.Details;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.Product;
import com.techFist.sellMyStuff.Commons.User;
import com.techFist.sellMyStuff.MapManager.BusinessOverlays;
import com.techFist.sellMyStuff.Services.ProductsService;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Process;
import android.os.ResultReceiver;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


class ServiceCallback extends ResultReceiver implements Parcelable{
	private Receiver mReceiver;
	public ServiceCallback(Handler handler) {
		super(handler);
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

public class AllProducts extends MapActivity implements ServiceCallback.Receiver{
    private CustomPoductList customProductList;
	private ArrayAdapter<CharSequence> sort;
	private ArrayAdapter<CharSequence> freshness;
	private Context context;
	private ViewGroup listGroup;
	private ViewGroup mapGroup;
	private View footer;
	private AlertDialog dialog;
	private CustomDialog customDialog;
	private ProductsService localService;
	private boolean isServiceRequested = false;
	private boolean isAllProductRequested = false;
	private int productFetchStatus = -404;
	public static ArrayList<Product> productList = new ArrayList<Product>();
	public ServiceCallback mReceiver;
	public Bundle formData;
	private ListView productListView;
	private MapView productMapView;
    private MapController currentMapController;
    private Drawable drawable;
    private Product product;
    private String criteria; 
    private String freshnessData = PocketTrader.DATE_WEEK;
    private boolean viewModeList  = true;
    private Spinner sortOption;
    private Spinner filterOption;
    private static final int CATEGORY_DIALOG = 10;
    private ListView categoriesList;
    private ArrayAdapter<CharSequence> categories;
    private ArrayList<String> savedCategories = new ArrayList<String>();
    private int offset = 0;
    private int limit = PocketTrader.LIST_LIMIT;
    private boolean endofList = false;
    private int newSize = 0;
    private int oldSize = 0;
    private boolean calledFromBanner = false;
    private int spinnerTriggercount  = 0;
    private TextView headerInfo;
	private ServiceConnection  mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName classname, IBinder binder) {
			Intent intent = new Intent();
			intent.putExtras(formData);

			localService = ((ProductsService.LocalBinder)binder).getService();
			isServiceRequested = true;
			localService.onCreate();		
			if(isAllProductRequested){
				productFetchStatus = localService.onStartCommand(intent,0, 6);
				if(productFetchStatus == -404)
					doUnbindService();
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName classname) {
			isServiceRequested = false;
			localService = null;
			
			
		}
	}; 
	 
	 // Bind Service to Activity
	 void doBindService() {
		
		bindService(new Intent(AllProducts.this,ProductsService.class), mConnection, Context.BIND_AUTO_CREATE);
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
	 isServiceRequested = false;
	 dialog.cancel();
     /* Returns 1, for successfully fetching product
      * Returns -1, on No Data Found
      * returns -404, on Service Unavailable
      * returns -500, on Datastore Error
      */	 
	 if(isAllProductRequested){
		 isAllProductRequested = false;
		 
		 
		 if(productFetchStatus == 1){
                 newSize = AllProducts.productList.size();
                  ////System.out.println("#### Old Size :" + oldSize);
                  ////System.out.println("#### new Size :" + newSize);
                 if(newSize%limit != 0 || oldSize == newSize)  // No More products Available on Server
                 {
                	 endofList = true;
                	 oldSize = newSize;
                	 productListView.removeFooterView(footer);
                 }
                 else{                                          // Still Products are to be fetched from Server
                   endofList = false;
                   oldSize = newSize;
                 } 	 
			       customProductList.refreshData(AllProducts.productList);
			       customProductList.notifyDataSetChanged();
			        ////System.out.println("##### Adapter Size :" + customProductList.getCount());
			        ////System.out.println("##### List Size Count:" + AllProducts.productList.size());
				   productListView.setSelection(offset);
			       productListView.refreshDrawableState();
			       if(!viewModeList){
			    	   populateMapView();
			       }
			 
		 }	
		 else if(productFetchStatus == -1){
			 endofList = true;
         	 oldSize = newSize;
         	 productListView.removeFooterView(footer);          	 			 
			 customProductList.refreshData(AllProducts.productList);
			 customProductList.notifyDataSetChanged();
			 productListView.refreshDrawableState();
             if(AllProducts.productList.isEmpty()){
			   if(criteria.equals(PocketTrader.CRITERIA_CITY)){
				  customDialog = new CustomDialog(context,"Okay","",getResources().getString(R.string.criteria_city));
				  dialog = customDialog.getInfoDialog("No Product Found");
				  dialog.show();
			   }
			   if(criteria.equals(PocketTrader.CRITERIA_STATE)){
				  customDialog = new CustomDialog(context,"Okay","",getResources().getString(R.string.criteria_state));
				  dialog = customDialog.getInfoDialog("No Product Found");
				  dialog.show();
			   }
			   if(criteria.equals(PocketTrader.CRITERIA_NEARBY)){
				  customDialog = new CustomDialog(context,"Okay","",getResources().getString(R.string.criteria_nearby));
				  dialog = customDialog.getInfoDialog("No Product Found");
				  dialog.show();
		 	   }
			   if(criteria.equals(PocketTrader.CRITERIA_COUNTRY)){
				  customDialog = new CustomDialog(context,"Okay","",getResources().getString(R.string.criteria_everything));
				  dialog = customDialog.getInfoDialog("No Product Found");
				  dialog.show();
			   }			 
            }
            else{
           	   endofList = true;
           	   oldSize = newSize;
           	   productListView.removeFooterView(footer);          	 
            }
			 
		 }
		 else{
             if(AllProducts.productList.isEmpty()){
  
			    customProductList.refreshData(AllProducts.productList);
			    productListView.refreshDrawableState();
			    customDialog = new CustomDialog(context,"Okay","",getResources().getString(R.string.criteria_everything));
		    	Intent intent = new Intent(context,PocketTrader.class);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  		    	dialog = customDialog.getInfoDialogForAction("No Product Found",intent);
  		    	dialog.show();
             }
             else{
            	 endofList = true;
            	 oldSize = newSize;
            	 productListView.removeFooterView(footer);        	 
             }             
		 }
	   } 
	 }
	
	
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		disconnectMyService();
		finish();}
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Custom Tile Bar
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.all_products_screen);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header_info);
        //Register a Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,AllProducts.class));

		//setContentView(R.layout.all_products_screen);
		context = this;

		// Fetching Form Objects
		headerInfo = (TextView)findViewById(R.id.app_title_info);
		listGroup = (ViewGroup)findViewById(R.id.list_group);
		mapGroup = (ViewGroup)findViewById(R.id.map_group);
		mReceiver = new ServiceCallback(new Handler());
		mReceiver.setReceiver(this);
		formData = new Bundle();	
		productListView = (ListView)findViewById(R.id.product_list_view);
		productMapView = (MapView)findViewById(R.id.product_map_view);
	    currentMapController = productMapView.getController();
	    drawable = this.getResources().getDrawable(R.drawable.marker_default);
	    sortOption = (Spinner)findViewById(R.id.sort_option);
	    filterOption = (Spinner)findViewById(R.id.freshness_filter);
	    categoriesList = new ListView(context);
	    categoriesList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	    //Initializing Objects
	    freshness = ArrayAdapter.createFromResource(context,R.array.freshness_filter,android.R.layout.simple_spinner_item);   
	    freshness.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    filterOption.setAdapter(freshness);
	    sort = ArrayAdapter.createFromResource(context,R.array.sorting_list,android.R.layout.simple_spinner_item);   	
		sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        		
		sortOption.setAdapter(sort);
	    categories = ArrayAdapter.createFromResource(context,R.array.menu_category,android.R.layout.simple_list_item_multiple_choice);   
		categoriesList.setAdapter(categories);
		// Preparing Multiselct Categories List
		categoriesList.setItemsCanFocus(false);
		categoriesList.setBackgroundColor(Color.BLACK);
		categoriesList.setCacheColorHint(0);
		categoriesList.setDividerHeight(1);
		categoriesList.setAlwaysDrawnWithCacheEnabled(true);
		categoriesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		categoriesList.setCacheColorHint(0);
		LayoutInflater inflate = getLayoutInflater();
		footer = inflate.inflate(R.layout.waiting_cell, null);
    	productListView.addFooterView(footer);	

		// Setting up Product List Adapter
		customProductList = new CustomPoductList(AllProducts.productList,context);
		productListView.setAdapter(customProductList);
        
		// Setting up Initial values in FormData
		
		//Setting up onScroll Listener
		productListView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if((firstVisibleItem+visibleItemCount) == totalItemCount && totalItemCount != 0 && visibleItemCount != totalItemCount)
				{
			       offset = totalItemCount;
			       if(!endofList)
			         performLookUp(false);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
			}
			
		});
		
		// Setting Up Listeners, Filter Listner
		filterOption.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View arg1,
					int position, long id) {
		     
			if(spinnerTriggercount >= 2){	
				String itemSelected = adapter.getItemAtPosition(position).toString();
				 ////System.out.println("####### Filter Selected : " + itemSelected);
				if(itemSelected.equals(PocketTrader.DATE_TODAY)){
					//AllProducts.productList.removeAll(AllProducts.productList);
					//offset = 0;
					prepareListRefresh();
					freshnessData = PocketTrader.DATE_TODAY;
					performLookUp(true);
				}
				if(itemSelected.equals(PocketTrader.DATE_WEEK)){
					//AllProducts.productList.removeAll(AllProducts.productList);
					//offset = 0;
					prepareListRefresh();
					freshnessData = PocketTrader.DATE_WEEK;
					performLookUp(true);
				}				
				if(itemSelected.equals(PocketTrader.DATE_15_DAY)){
					//AllProducts.productList.removeAll(AllProducts.productList);
					//offset = 0;
					prepareListRefresh();
					freshnessData = PocketTrader.DATE_15_DAY;
					performLookUp(true);
				}	
				if(itemSelected.equals(PocketTrader.DATE_30_DAY)){
					//AllProducts.productList.removeAll(AllProducts.productList);
					//offset = 0;
					prepareListRefresh();
					freshnessData = PocketTrader.DATE_30_DAY;
					performLookUp(true);
				}
				if(itemSelected.equals(PocketTrader.DATE_NO_MATTER)){
					
					//AllProducts.productList.removeAll(AllProducts.productList);
					//offset = 0;
					prepareListRefresh();
					freshnessData = PocketTrader.DATE_NO_MATTER;
					performLookUp(true);
				}
			  }
			else
				spinnerTriggercount = spinnerTriggercount + 1;
			     ////System.out.println("#### Increasing trigger count:" + spinnerTriggercount);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

				
			}
			
		});
		
		//Search Listener
		sortOption.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> adapter, View arg1,
					int position, long id) {
		    if(spinnerTriggercount >= 2){		
				  // Sorting Options
				  String itemSelected = adapter.getItemAtPosition(position).toString();
				   ////System.out.println("####### Item Selected : " + itemSelected);
				  if(itemSelected.equals(PocketTrader.CRITERIA_CITY)){
					// City Related Search
					//AllProducts.productList.removeAll(AllProducts.productList);
					//offset = 0;
					prepareListRefresh();
					criteria = itemSelected;
					performLookUp(true);
				}
				if(itemSelected .equals(PocketTrader.CRITERIA_STATE)){
					// State Related Search
					//AllProducts.productList.removeAll(AllProducts.productList);
					//offset = 0;
					prepareListRefresh();
					criteria = itemSelected;
					performLookUp(true);
				}
				if(itemSelected.equals(PocketTrader.CRITERIA_NEARBY)){
					// Search Nearby
					prepareListRefresh();
					criteria = itemSelected;
					performLookUp(true);
				}
				if(itemSelected.equals(PocketTrader.CRITERIA_COUNTRY))
				{
					// Search Everything
					//AllProducts.productList.removeAll(AllProducts.productList);
					//offset = 0;
					prepareListRefresh();
					criteria = itemSelected;
					performLookUp(true);
				}				
		      }
		    else
		    	spinnerTriggercount = spinnerTriggercount + 1;
		     ////System.out.println("#### Increasing trigger count:" + spinnerTriggercount);
		    }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		// Product List Selected Listener
		productListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,int position,long arg3) {

			   disconnectMyService();
			   formData = new Bundle();
			   product = (Product)adapter.getItemAtPosition(position);
			   formData.putParcelable("ProductDetail", product);
			   Intent intent = new Intent(context,ProductDetailScreen.class);
			   intent.putExtras(formData);
			   startActivity(intent);				
			}
	  });	
	}		
	private void prepareListRefresh(){
		AllProducts.productList.removeAll(AllProducts.productList);
		offset = 0;
	    newSize = 0;
	    oldSize = 0;
        productListView.removeFooterView(footer);	
        productListView.addFooterView(footer);	
	}

	private String getCategoriesQuery(){
		if(savedCategories != null){
			String query = "";
			int i = 0;
			for(String data: savedCategories)
			{
				if(i == 0)
					query = data;
				else
					query = query + "-" + data;
				i++;
			}
			return query;
		}
		else{
			return "";
		}
	}
   @Override
   protected void onStart() {
     
	 // View Switching Mechanism
	 super.onStart();
	 headerInfo.setText(PocketTrader.detail.getCity());
	 calledFromBanner = false;
	 Intent intent = getIntent();
	 if(intent != null){
		 Bundle b = intent.getExtras();
		 if(b != null){
			 
			 if(b.containsKey("FROM_BANNER")){
				  ////System.out.println("#### Calling from Baner");
				 calledFromBanner = true;
				 pefromLookUpfromBanner(b);
			 }else{
				 calledFromBanner = false;
				 
			 }
			 
	
		 }
	 }
	 
   }
   @Override
   protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();

	if(AllProducts.productList.isEmpty() && !calledFromBanner){

		   ////System.out.println("######## On Resume Called for All Product");
		  formData.putParcelable("Receiver", mReceiver);
		  formData.putString("City", PocketTrader.detail.getCity());
		  formData.putString("State","");
		  formData.putString("Country","");
		  formData.putString("Category","");
		  formData.putString("Nearby","");
		  formData.putString("Filter",PocketTrader.DATE_WEEK);
	      formData.putString("Offset",String.valueOf(offset));
		  formData.putString("Limit",String.valueOf(limit));			
		  
		  criteria = PocketTrader.CRITERIA_CITY;
		  isAllProductRequested = true;
		  if(PocketTrader.user == null)
			  PocketTrader.user = new com.techFist.sellMyStuff.Commons.User();
	      customDialog = new CustomDialog(context,"","","Working..");
		  String msg = getString(R.string.wait);
	      dialog = customDialog.getWaitDialog(msg,true);
		  dialog.show(); 	  
		  doBindService();
}    

	
   }
   @Override
   protected void onDestroy() {
	// TODO Auto-generated method stub
    // Cleanup
	 ////System.out.println("##### Activity Distroyed");	
	AllProducts.productList.removeAll(productList);
	super.onDestroy();

   }
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
	// TODO Auto-generated method stub
    if(viewModeList) 
	   menu.add(0,1,0,"Map");
    else
    	menu.add(0,2,0,"List");
    
    menu.addSubMenu(0,3,1,"Categories");
    return true;
    
   }
   @Override
   public boolean onMenuOpened(int featureId, Menu menu) {
	// TODO Auto-generated method stub
	   menu.clear();
	    if(viewModeList) 
	 	   menu.add(0,1,0,"Map");
	     else
	     	menu.add(0,2,0,"List");
	     
    menu.addSubMenu(0,3,1,"Categories");
    return true;
   }
   @Override
   public boolean onMenuItemSelected(int featureId, MenuItem item) {
	// TODO Auto-generated method stub
	int flag = item.getItemId();
	 ////System.out.println("##### Selected Item ID :" + flag);
	 if(flag == 1){
	    viewModeList = false;
		mapGroup.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		listGroup.setLayoutParams(new LinearLayout.LayoutParams(0,0));
		
		populateMapView();
	    
	 }
	 if(flag == 2){
		 viewModeList = true;
	     listGroup.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	     mapGroup.setLayoutParams(new LinearLayout.LayoutParams(0,0));
		 populateListView();
		 
	 }
	if(flag == 3){
		showDialog(CATEGORY_DIALOG);
	}
	   
	return super.onMenuItemSelected(featureId, item);
   }
   // Preparing Category Selection Dialog Box
   @Override
   protected Dialog onCreateDialog(int id) {
      switch(id){
      case CATEGORY_DIALOG:
    	  return createCategoryDialog();
    	  
      }
	   
	   return null;
   }
   
   @Override
   protected void onPrepareDialog(int id, Dialog dialog) {
    
	   switch(id){
	   case CATEGORY_DIALOG:
		   prepareCategoryDialog(dialog);
		   break;
	   }
   
   }
   private Dialog createCategoryDialog() {
	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	   builder.setTitle("Select Categories");	   
	   builder.setView(categoriesList);
	   android.content.DialogInterface.OnClickListener saveCategories= new android.content.DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			savedCategories = new ArrayList<String>();
			saveCheckItems();
			//AllProducts.productList.removeAll(AllProducts.productList);			
			//offset = 0;
			prepareListRefresh();
			performLookUp(true);
		}
		   
	   };
	   
	   builder.setPositiveButton("Okay", saveCategories);
	   
	   AlertDialog ad = builder.create();
	   return ad; 
  }
  private void prepareCategoryDialog(Dialog d) { 
     //change something about this dialog
     AlertDialog ad = (AlertDialog)d;
     loadCheckedItems();
     ad.setTitle("Select Categories");	   
     ad.setView(categoriesList);  	 
	 
  }   
   private void saveCheckItems(){
	   int count  = this.categoriesList.getAdapter().getCount();
	   for(int i=0;i<count;i++){
		   if(this.categoriesList.isItemChecked(i))
			   savedCategories.add((String)this.categoriesList.getAdapter().getItem(i));
	   }
   }
  private void loadCheckedItems(){
	   int count  = this.categoriesList.getAdapter().getCount();
       for(int i=0;i<count;i++){
    	  if(savedCategories.contains(categoriesList.getAdapter().getItem(i)))
    		  this.categoriesList.setItemChecked(i, true);
       }
  }
  private void pefromLookUpfromBanner(Bundle b){
	   ////System.out.println("#### All products called from Banners");
	  formData.putParcelable("Receiver", mReceiver);
	  formData.putString("City", PocketTrader.detail.getCity());
	  formData.putString("State","");
	  formData.putString("Country","");
      formData.putString("Nearby","");
	  formData.putString("Category", b.getString("CATEGORY"));
	  formData.putString("Filter",PocketTrader.DATE_15_DAY);
      formData.putString("Offset",String.valueOf(offset));
	  formData.putString("Limit",String.valueOf(limit));			
	  
	  criteria = PocketTrader.CRITERIA_CITY;
	  isAllProductRequested = true;
	  if(PocketTrader.user == null)
		  PocketTrader.user = new com.techFist.sellMyStuff.Commons.User();
      customDialog = new CustomDialog(context,"","",getString(R.string.working));
      dialog = customDialog.getWaitDialog(getString(R.string.wait),true);
	  dialog.show(); 
	  filterOption.setSelection(2);
	  doBindService();	  
  }
  
  private void performLookUp(boolean displayWaitDialog){
	  
	  if(displayWaitDialog){
			customDialog = new CustomDialog(context,"","",getString(R.string.working));
			dialog = customDialog.getWaitDialog(getString(R.string.wait),true);
			dialog.show(); 			  
	  } 
        //System.out.println("##### Performing Lookup"); 
        Log.i("####","Criteria Is: " + criteria);
        Log.i("####","Freshness Is: " + freshnessData);
        Log.i("####","Category's are: " + getCategoriesQuery());
	  
	    if(criteria.equals(PocketTrader.CRITERIA_CITY)){
			// City Related Search
             ////System.out.println("###### Criteria Selected is:" + criteria);
			formData.putParcelable("Receiver", mReceiver);
			formData.putString("City", PocketTrader.detail.getCity());
			formData.putString("State","");
			formData.putString("Country","");
			formData.putString("Nearby","");
			formData.putString("Filter",freshnessData);
			formData.putString("Category", getCategoriesQuery());
		    formData.putString("Offset",String.valueOf(offset));
			formData.putString("Limit",String.valueOf(limit));			
			
			criteria = PocketTrader.CRITERIA_CITY;
			isAllProductRequested = true;
			doBindService();					
		}
		else if(criteria .equals(PocketTrader.CRITERIA_STATE)){
			// State Related Search
			formData.putParcelable("Receiver", mReceiver);
			formData.putString("City", "");
			formData.putString("Nearby","");
			formData.putString("State",PocketTrader.detail.getState());
			formData.putString("Country","");
			formData.putString("Filter",freshnessData);
			formData.putString("Category", getCategoriesQuery());
		    formData.putString("Offset",String.valueOf(offset));
			formData.putString("Limit",String.valueOf(limit));			
			
			criteria = PocketTrader.CRITERIA_STATE;
			isAllProductRequested = true;
			doBindService();					
		}
		else if(criteria.equals(PocketTrader.CRITERIA_NEARBY)){
			// Search Nearby
			formData.putParcelable("Receiver", mReceiver);
			formData.putString("City", "");
			formData.putString("State","");
			formData.putString("Country","");
			
			formData.putString("Nearby", String.valueOf(PocketTrader.detail.getLongitude()));
			formData.putString("Filter",freshnessData);
			formData.putString("Category", getCategoriesQuery());
		    formData.putString("Offset",String.valueOf(offset));
			formData.putString("Limit",String.valueOf(limit));			
			
			criteria = PocketTrader.CRITERIA_STATE;
			isAllProductRequested = true;
			doBindService();
		}
		else if(criteria.equals(PocketTrader.CRITERIA_COUNTRY))
		{
			// Search Everything
			formData.putParcelable("Receiver", mReceiver);
			formData.putString("City", "");
			formData.putString("State","");
			formData.putString("Nearby","");
			formData.putString("Filter",freshnessData);
			formData.putString("Country",PocketTrader.detail.getCountry());
			formData.putString("Category", getCategoriesQuery());
		    formData.putString("Offset",String.valueOf(offset));
			formData.putString("Limit",String.valueOf(limit));			
			
			criteria = PocketTrader.CRITERIA_COUNTRY;
			isAllProductRequested = true;
			doBindService();					
		} 
	    
   }
   private void populateListView(){
	   
   }
   private Drawable getDrawableIcon(String type){
	   
	    ////System.out.println("#### Product Drawable Type:" + type);
	   Drawable drabl;
	   if(type.equalsIgnoreCase("Toys")){
		   drabl = getResources().getDrawable(R.drawable.marker_toys);
	   }else if(type.equalsIgnoreCase("Sports")){
		   drabl = getResources().getDrawable(R.drawable.marker_sports);		   
	   }
	   else if(type.equalsIgnoreCase("Instruments")){
		   drabl = getResources().getDrawable(R.drawable.marker_instrument);
	   }else if(type.equalsIgnoreCase("Movies and Music")){
		   drabl = getResources().getDrawable(R.drawable.marker_moviesnmusic);
	   }else if(type.equalsIgnoreCase("Real State")){
		   drabl = getResources().getDrawable(R.drawable.marker_realestate);
	   }else if(type.equalsIgnoreCase("House Holds")){
		   drabl = getResources().getDrawable(R.drawable.marker_household);
	   }else if(type.equalsIgnoreCase("Electronics")){
		    ////System.out.println("#### Getting Drawable");
		   drabl = getResources().getDrawable(R.drawable.marker_phones);
	   }else if(type.equalsIgnoreCase("Computers")){
		   drabl = getResources().getDrawable(R.drawable.marker_computers);
	   }else if(type.equalsIgnoreCase("Clothing,Accessories and Lifestyle")){
		   drabl = getResources().getDrawable(R.drawable.marker_apprales);
	   }else if(type.equalsIgnoreCase("Baby Care")){
		   drabl = getResources().getDrawable(R.drawable.marker_babycare); 
	   }else if(type.equalsIgnoreCase("Beauty,Health and Cosmetics")){
	       drabl = getResources().getDrawable(R.drawable.marker_beautinhealth);
       }else if(type.equalsIgnoreCase("Books and Stationery")){
    	   drabl = getResources().getDrawable(R.drawable.marker_booksnstationary);
       }else if(type.equalsIgnoreCase("Automobile")){
    	   drabl = getResources().getDrawable(R.drawable.marker_byke);
       }else
    	   drabl = getResources().getDrawable(R.drawable.marker_default);
	   drabl.setBounds(-drabl.getIntrinsicWidth() / 2, -drabl.getIntrinsicHeight(), drabl.getIntrinsicWidth() / 2, 0);
	   
	   return drabl;
	   
	   
   }
   private void populateMapView(){
	  
	  // Drawing Map Overlays
	  final List<Overlay> mOverlays = productMapView.getOverlays();
	  BusinessOverlays itemizedoverlay = new BusinessOverlays(drawable,this,productList);
	  GeoPoint point = null;	    
	  for(Product product : productList){
		int latitude = (int) (Double.valueOf(product.getProductLatitude()).doubleValue() * 1E6);
		int longitude = (int) (Double.valueOf(product.getProductLongitude()).doubleValue() * 1E6); 

		point = new GeoPoint(latitude,longitude);
        OverlayItem overlayitem = new OverlayItem(point, "Hurry", "Product is on Sale");
        Drawable drw = getDrawableIcon(product.getProductCategory());
        overlayitem.setMarker(drw);
        itemizedoverlay.addOverlay(overlayitem);
        mOverlays.add(itemizedoverlay);
	  }
	  // Setting up Map Parameters
      currentMapController.animateTo(point);
      currentMapController.setCenter(point);
      // Setting up Default ZOOM based on Criteria
      if(criteria.equals(PocketTrader.CRITERIA_CITY))
         currentMapController.setZoom(14);
      else if(criteria.equals(PocketTrader.CRITERIA_COUNTRY))
    	  currentMapController.setZoom(8);
      else if(criteria.equals(PocketTrader.CRITERIA_STATE))
    	  currentMapController.setZoom(10);
      else
    	  currentMapController.setZoom(12);
      
      productMapView.setBuiltInZoomControls(true);
      productMapView.setClickable(true);
      productMapView.setEnabled(true);
      productMapView.setSatellite(false);
      productMapView.setTraffic(false);	  
  }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		if(resultCode == 0){
			productFetchStatus = resultData.getInt("Result");
			 ////System.out.println("##### Fetch Status :" + productFetchStatus);
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
