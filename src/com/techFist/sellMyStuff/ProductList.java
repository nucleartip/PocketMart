package com.techFist.sellMyStuff;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.ads.AdView;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.CustomPoductList;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Commons.Product;


public class ProductList extends PocketActivity {

	ArrayList<String> activityList = new ArrayList<String>();
	private int offset = 0;
	private int limit = PocketTrader.LIST_LIMIT;
	private String searchCategory;
	private String searchType;
    static final String    SERVER    = "http://sms-techfist.appspot.com";	
	private Context context;
	private ListView productList;
	private CustomDialog customDialog;
	private AlertDialog waitDialog;
	private AlertDialog generic;
    private Bundle searchData;
    private Bundle formData;
    private CustomPoductList customAdapter;
    private boolean endofList = false;
	private View footer;
    private int newSize = 0;
    private int oldSize = 0;	
    //private ArrayList<ProductDetail> productListArray;
    private ArrayList<Product> productListArray = new ArrayList<Product>();
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,ProductList.class));
		
	    //setContentView(R.layout.products_list);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.products_list);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
	    context = this;
	    searchData = getIntent().getExtras();
        // Populating form Objects
	    searchCategory = searchData.getString("Category");
	    searchType = searchData.getString("Type");
	    productList = (ListView)findViewById(R.id.productList);	    
		// Linking up a listener
		productList.setOnItemClickListener(new OnItemClickListener(){
	    @Override
	    public void onItemClick(AdapterView<?> adapter, View view,int position,long arg3) {
		  // TODO Auto-generated method stub
		  formData = new Bundle();
		  Product product = (Product)adapter.getItemAtPosition(position);
		  formData.putParcelable("ProductDetail", product);
		  Intent intent = new Intent(context,ProductDetailScreen.class);
		  intent.putExtras(formData);
		  startActivity(intent);
	    }});
		//Setting up onScroll Listener
		productList.setOnScrollListener(new OnScrollListener(){
		@Override
		public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		  if((firstVisibleItem+visibleItemCount) == totalItemCount && totalItemCount != 0)
			{
			       offset = totalItemCount;
			       if(!endofList)
			    	 new FetchProducts().execute("Get More");
			}
		}
	    @Override
	    public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
        }});
		// Adding Loading State
		LayoutInflater inflate = getLayoutInflater();
		footer = inflate.inflate(R.layout.waiting_cell, null);
		productList.addFooterView(footer);	
		productList.refreshDrawableState();

		
	}
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	if(productListArray.size() < 1){
	 	  // Preparing List for First time
	      customDialog = new CustomDialog(context,"","","Working..");
	      waitDialog = customDialog.getWaitDialog("Please Wait",true);
	      waitDialog.show();
	      new FetchProducts().execute("Get Products");
          //new getProducts().execute("");
    	}
    	super.onResume();
    }
    
    
    // Async Task for Fetching product List
    class FetchProducts extends AsyncTask<String,Integer,Integer>{
		@Override
		protected Integer doInBackground(String... arg0) {

			// TODO Auto-generated method stub
			 DefaultHttpClient CLIENT = new DefaultHttpClient();
			 HttpPost post = new HttpPost(SERVER+"/getStuff");	
		     HttpResponse resp;
             try{
 	            ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
 	            data.add(new BasicNameValuePair("Category", searchCategory));
 	            data.add(new BasicNameValuePair("Type",  searchType));  
 	            data.add(new BasicNameValuePair("Offset", String.valueOf(offset)));
 	            data.add(new BasicNameValuePair("Limit",  String.valueOf(limit)));  
		
		        
 	            post.setEntity(new UrlEncodedFormEntity(data));		
	    		resp = CLIENT.execute(post);
	  		    int respStatus = resp.getStatusLine().getStatusCode();
	  		    if(HttpStatus.SC_NOT_FOUND != respStatus)
	  		    {
		  		   BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		  	       StringBuilder sb = new StringBuilder();
		           String line = null;
		           while((line = reader.readLine())!=null){
		             sb = sb.append(line);
		           }
		

		           JSONObject jo = new JSONObject(sb.toString());
	               JSONArray ja = jo.getJSONArray("Data");  
	               
		           for (int i = 0; i < ja.length(); i++) {
		        	 JSONObject jo1 = ja.getJSONObject(i);         
	             
		        	 Product obj = new Product(jo1.getString("StuffName"),jo1.getString("StuffPrice"),jo1.getString("StuffDate"),
		    		   jo1.getString("ImageURL"),jo1.getString("StuffType"),jo1.getString("StuffCategory"),jo1.getString("StuffDescription"),
		    		   jo1.getString("StuffLocation"),jo1.getString("Longitude"),jo1.getString("Latitude"),jo1.getString("Key"),jo1.getString("Owner"),
		    		   jo1.getString("StuffCity"),jo1.getString("StuffState"),jo1.getString("StuffCountry"),jo1.getString("Phone"),Boolean.valueOf(jo1.getString("PostedByPhoneOption")));
		        	   productListArray.add(obj);    // Fresh List
	  		    }
		        return 1;    // Product List fetched
  
             }
	  		 else{
	  		    	   return -1;   // No Products Found
	  		 }
		}catch(Exception e){
			return -404;   // Service Down
		}
	  }// Do in Background Ends       
      @Override
      protected void onPostExecute(Integer result) {
	
    	if(result == 1){
            newSize = productListArray.size();
            if(newSize%limit != 0 || oldSize == newSize)  // No More products Available on Server
            {
           	 endofList = true;
           	 oldSize = newSize;
           	 productList.removeFooterView(footer);
            }
            else{                                         // Still Products are to be fetched from Server
              endofList = false;
              oldSize = newSize;
            }
    	    customAdapter = new CustomPoductList(productListArray,context);            
            customAdapter.notifyDataSetChanged();
            productList.setAdapter(customAdapter);
            productList.setSelection(offset);
            productList.refreshDrawableState();
		    waitDialog.cancel();
		    
		}
    	else if(result == -1 && productListArray.size() > 0){
            endofList = true;
        	oldSize = newSize;
        	productList.removeFooterView(footer);
        	productList.refreshDrawableState();
    	}
    	else if(result == -1 && productListArray.size() <= 0){
			waitDialog.cancel();
			customDialog = new CustomDialog(context,"Okay","","No Products Found, you can be the firt one to Put on Sale.");
		    Intent intent = new Intent(context,PocketTrader.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    generic = customDialog.getInfoDialogForAction("Opps..",intent);
		    generic.show();    		
    	}
    	else if(result == -404 && productListArray.size() <= 0){
			customDialog = new CustomDialog(context,"Okay","","Service in Unavailable, Please try again");
		    Intent intent = new Intent(context,PocketTrader.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    generic = customDialog.getInfoDialogForAction("Oops",intent);
		    generic.show();    		
    	}
    	else if(result == -404 && productListArray.size() > 0){
            endofList = true;
        	oldSize = newSize;
        	productList.removeFooterView(footer);
        	productList.refreshDrawableState();    		
    	}
      } // On Post Execute
  }

     @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	 super.onStart();
    	 //productList.refreshDrawableState();
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
