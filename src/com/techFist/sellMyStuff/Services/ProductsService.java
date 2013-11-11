package com.techFist.sellMyStuff.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.AccountManager.AccountScreen;
import com.techFist.sellMyStuff.AccountManager.MyProductScreen;
import com.techFist.sellMyStuff.AccountManager.MySavedProductScreen;
import com.techFist.sellMyStuff.AllProducts.AllProducts;
import com.techFist.sellMyStuff.Commons.Bid;
import com.techFist.sellMyStuff.Commons.Product;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class ProductsService extends Service {

    Context context;
	HttpClient PostClient;
	HttpPost httpPost;	
	HttpResponse resp;	
	Bundle formData;
    static final String    SERVER    = "http://sms-techfist.appspot.com";	
	private ConnectivityManager connectivityManager;	
	// Binder which receives Notifications from Activity
	private final IBinder mBinder = new LocalBinder();
	private static int submitDataStatus = -2;
	private String submitDealKey = "";
	private AsyncTask<String,Integer,Integer> productTask;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
    	httpPost = new HttpPost();
		PostClient = new DefaultHttpClient();		
		formData = new Bundle();
		 ////System.out.println("##### Product Service Started");
	}
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	 ////System.out.println("##### Product Service Distroyed");
    }
	// Returns Mapping for Activity
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
    public class LocalBinder extends Binder {
    	public ProductsService getService() {
            return ProductsService.this;
        }
    }		
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
 
    	// TODO Auto-generated method stub
    	   int status = checkNetworkStatus(context);
      	    if(status == 0){
                // Submit a Deal
      	    	if(startId == 1){
                	if(intent != null)
                	formData = intent.getExtras();
                	productTask = new submitDeal().execute("SubmitDeal");
                	return 300;
      	    	}
      	    	// Submit a Bid
      	    	if(startId == 2){
      	    		if(intent != null)
      	    	    formData = intent.getExtras();
      	    		if(flags == PocketTrader.MY_PRODUCT_DETAIL_SCREEN)
      	    			productTask = new SubmitBid().execute(PocketTrader.MY_PRODUCT_DETAIL);
      	    		else if(flags == PocketTrader.MY_SAVED_PRODUCT_DETAIL_SCREEN)
      	    			productTask = new SubmitBid().execute(PocketTrader.MY_SAVED_PRODUCT_DETAIL);
      	    	    else		
      	    	    	productTask = new SubmitBid().execute("SubmitBid");
      	    		return 300;
      	    	}
      	    	// Fetch Products for a User
      	    	if(startId == 3){
      	    		formData = intent.getExtras();
      	    		productTask = new FetchProducts().execute("Fetch Product");
     	  	        AccountScreen.productFetchTask = productTask;
      	    		return 300;
      	    	}
      	    	// Fetch Bid for a Product
      	    	if(startId == 4){
      	    		formData = intent.getExtras();
      	    		if(flags == 1) // My Saved Product Detail Screen
      	    			productTask = new FetchBids().execute(PocketTrader.MY_SAVED_PRODUCT_DETAIL);
      	    		else if(flags == 0) // My Product Detail Screen
      	    			productTask = new FetchBids().execute("Fetch Bids");
      	            return 300;
      	    	}
      	        // Fetch all Bids, made on products posted by a User 	
      	    	if(startId == 5){
      	    		formData = intent.getExtras();
      	    		productTask = new FetchBids().execute("Fetch All Bids");
      	            return 300;
      	    	}      	    
      	        // Fetch all Products, 20 at a time;
      	    	if(startId == 6){
      	    		formData = intent.getExtras();
      	    		productTask = new FetchAllProducts().execute("Fetch All Products");
      	    		return 300;
      	    	}
      	        // Save Product
      	    	if(startId == 7){
      	    		formData = intent.getExtras();
      	    		productTask = new SaveMyProduct().execute("Save Product");
      	    		return 300;
      	    	}
      	    	// Deletes a Saved product
      	    	if(startId == 8){
      	    		formData = intent.getExtras();
      	    		// Requesting from My Saved Product Detail Screen
      	    		if(flags == 1){
      	    			productTask = new DeleteMySavedProduct().execute(PocketTrader.MY_SAVED_PRODUCT_DETAIL);
      	    			
      	    		}
      	    		// Requesting from Product Detail Screen
      	    		else{
      	    			productTask = new DeleteMySavedProduct().execute("Delete Saved Product");
      	    		}
      	    		
      	    		return 300;
      	    	}
      	    	// Checks if Product is a Saved product for User
      	    	if(startId == 9){
      	    		formData = intent.getExtras();
      	    		productTask = new CheckSavedProduct().execute("Check Saved Status");
      	    		return 300;
      	    	}
      	    	// Fetches Saved product list for user
      	    	if(startId == 10){
      	    		formData = intent.getExtras();
      	    		productTask = new GetSavedProductsForUser().execute("Fetch Saved Products");
      	    		return 300;
      	    	}
      	    	// Deletes a Product Posted by User
      	    	if(startId == 11){
      	    		formData = intent.getExtras();
      	    		if(flags == PocketTrader.PRODUCT_DETAIL_SCREEN)  
      	    			productTask = new DeleteProduct().execute(PocketTrader.PRODUCT_DETAIL);
      	    		if(flags == PocketTrader.MY_PRODUCT_DETAIL_SCREEN)
      	    			productTask = new DeleteProduct().execute(PocketTrader.MY_PRODUCT_DETAIL);
      	    		return 300;
      	    	}
                // Post New Message
      	       if(startId == 12){
      	    	   formData = intent.getExtras();
      	    	   
      	       }
      	       // Get all Messages for User
      	       if(startId== 13){
      	    	   formData = intent.getExtras();
      	       }
      	       // Get Specific Message
      	       if(startId == 14){
      	    	   formData = intent.getExtras();
      	       }
      	    }
      	    else{
      	    	return -404;
      	    }
    	    return -404;
    }
    
    public boolean cancelRunningTask(){
      try{	
    	if(productTask != null)
    		if(!productTask.isCancelled()){ 
    			productTask.cancel(true);
    			 ////System.out.println("###### A running Product Task has been Completed");
    		    return true;
    		}		
      }catch(Exception e){
         return false;}
      return false;
    }
    
    // Checking Network Availibility
    public int checkNetworkStatus(Context context){
       
 	   connectivityManager = (ConnectivityManager) this.getSystemService(context.CONNECTIVITY_SERVICE);
 	   NetworkInfo info = connectivityManager.getActiveNetworkInfo();
 	   if(info == null)
           return -1;
 	   else 
 		   return 0;

    }   
    
    // Post a Message
    
    class PostMessage extends AsyncTask<String,Integer,Integer>{
    	final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
		@Override
		protected Integer doInBackground(String... arg0) {
			// TODO Auto-generated method stub
		try{
    	    httpPost = new HttpPost(SERVER+"/getMessages");
		    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
		    data.add(new BasicNameValuePair("EmailTo",formData.getString("EmailTo")));		    
		    data.add(new BasicNameValuePair("EmailFrom",formData.getString("EmailFrom")));
		    data.add(new BasicNameValuePair("Message",formData.getString("Message")));
		    data.add(new BasicNameValuePair("Type","POST"));
	        httpPost.setEntity(new UrlEncodedFormEntity(data));	
	        resp = PostClient.execute(httpPost);
    		// Product has Been Deleted
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
      	  return -404;
        }
        return -404; 			

		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
	  		Bundle b = new Bundle();
	  		b.putInt("Result", result);
            receiver.send(10, b);	      		 
	   }			

		
   }
    
    
    //Deletes a  Product

    /* returns 1 of product has been Deleted succesfuly
     * returns -500, if datastore exception occurs, Product is Not deleted
     * return -404, if service is down.
     */
    class DeleteProduct extends AsyncTask<String,Integer,Integer>{
        final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
        int screenType = -1;
 
		@Override
		protected Integer doInBackground(String... arg0) {
            if(arg0[0].equals(PocketTrader.PRODUCT_DETAIL))
            	screenType = 1;   // Product Detail Screen
            else if(arg0[0].equals(PocketTrader.MY_PRODUCT_DETAIL))
            	screenType = 2;   // My Product Detail Screen
			try{
	        	    httpPost = new HttpPost(SERVER+"/deleteProduct");
				    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
				    data.add(new BasicNameValuePair("Key",formData.getString("Key")));
			        httpPost.setEntity(new UrlEncodedFormEntity(data));		
		    		resp = PostClient.execute(httpPost);
		    		// Product has Been Deleted
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
	        	  return -404;
	          }
	          return -404;            
		}
	    protected void onPostExecute(Integer result) {
	  		super.onPostExecute(result);
	  		Bundle b = new Bundle();
	  		b.putInt("Result", result);
	  	    if(screenType == 1)  // Send to Product Detail Screen
	  		  receiver.send(2,b);
	  	    if(screenType == 2) // Send to My Product Detail Screen
	  	      receiver.send(1, b);
	      		 
	       }    	
    }
    
    class GetSavedProductsForUser extends AsyncTask<String,Integer,Integer>{
    	final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
    	ArrayList<Product> productListArray = new ArrayList<Product>();
		@Override
		protected Integer doInBackground(String... params) {
			try{
				httpPost = new HttpPost(SERVER+"/getSaveProduct");
			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			    data.add(new BasicNameValuePair("Email",formData.getString("Email")));	
			     ////System.out.println("###### Email Id : " + formData.getString("Email"));
		        httpPost.setEntity(new UrlEncodedFormEntity(data));		
	    		resp = PostClient.execute(httpPost);
         	    
	    		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
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
	    		             jo1.getString("StuffLocation"),jo1.getString("Longitude"),jo1.getString("Latitude"),jo1.getString("Key"),
	    		             jo1.getString("Owner"),jo1.getString("StuffCity"),jo1.getString("StuffState"),jo1.getString("StuffCountry"),
	    		             jo1.getString("Phone"),Boolean.valueOf(jo1.getString("PostedByPhoneOption")));
	        	     
	        	     
	        	     
	        	     productListArray.add(obj);
	        	  
	               }
	              
	               Collections.sort(productListArray);
	               AccountScreen.mySavedList = productListArray;
                   // Products Succesfullt Fetched
	               return 1;
         	    }
         	    // Servlet Error
         	    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
         	    	return -404;
         	    }
                // No Products Found
         	    if(resp.getStatusLine().getStatusCode() == 10001) {
                    return -1;
         	    }
			}
			catch (ClientProtocolException e) {
                e.printStackTrace();
				return -404;
			} catch (IOException e) {
                e.printStackTrace();
				return -404;
			} catch (JSONException e) {
				e.printStackTrace();
				return -404;
			} catch(Exception e){
				e.printStackTrace();
			}
			return -404;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		    Bundle b = new Bundle();
		    b.putInt("Result", result);
			receiver.send(2,b);
		}   	
    }
    
    //Check's if product is a Saved product for User
    /* returns 1, if product is saved for user
     * returns -1, if product is not saved for user
     * return -500, if datastore error
     * return -404, if Service is Down
     */
    class CheckSavedProduct extends AsyncTask<String,Integer,Integer>{
        final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
        

		@Override
		protected Integer doInBackground(String... arg0) {
          try{
        	  httpPost = new HttpPost(SERVER+"/isMySavedProduct");
			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			    data.add(new BasicNameValuePair("Key",formData.getString("Key")));
			    data.add(new BasicNameValuePair("Email",formData.getString("Email")));
		        httpPost.setEntity(new UrlEncodedFormEntity(data));		
	    		resp = PostClient.execute(httpPost);
	    		// Product has Been Deleted
	    		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	    			return 1;
	    		}
	    		if(resp.getStatusLine().getStatusCode() == 10001){
	    			return -1;
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
        	  return -404;
          }
          return -404;
    	
      }
    @Override
    protected void onPostExecute(Integer result) {
		Bundle b = new Bundle();
		b.putInt("Result", result);
	    receiver.send(3,b);
    }
    
    }
    
    //Deletes a Saved Product

    /* returns 1 of product has been Deleted succesfuly
     * returns -500, if datastore exception occurs, Product is Not deleted
     * return -404, if service is down.
     */
    class DeleteMySavedProduct extends AsyncTask<String,Integer,Integer>{
        final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
        int screenType = -1;
        
		@Override
		protected Integer doInBackground(String... arg0) {
            if(arg0[0].equals(PocketTrader.MY_SAVED_PRODUCT_DETAIL))
            	screenType = 1;   // My Saved Product Screen
            else
            	screenType = 2;   // Product Detail Screen
			
			try{
        	    //System.out.println("#### Connecting to Server");
				httpPost = new HttpPost(SERVER+"/deleteSaveProduct");
			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			    data.add(new BasicNameValuePair("Key",formData.getString("Key")));
			    data.add(new BasicNameValuePair("Email",formData.getString("Email")));
		        httpPost.setEntity(new UrlEncodedFormEntity(data));		
	    		resp = PostClient.execute(httpPost);
	    		// Product has Been Deleted
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
        	  return -404;
          }
          return -404;
      }
      protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		Bundle b = new Bundle();
		b.putInt("Result", result);
	    if(screenType == 2)  // Send to Product Detail Screen
		  receiver.send(2,b);
	    if(screenType == 1) // Send to Saved Product Detail Screen
	      receiver.send(1, b);
    		 
     }
    }
    //Saves a Product
    /* returns 1 of product has been saved succesfuly
     * returns -500, if datastore exception occurs, product hasnt been saved succesfully
     * return -1, if save limit has been excceded cannot save any more products
     * return -404, if service is down.
     */
    class SaveMyProduct extends AsyncTask<String,Integer,Integer>{
        final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
        
		@Override
		protected Integer doInBackground(String... arg0) {
          try{
        	  httpPost = new HttpPost(SERVER+"/saveProduct");
			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			    data.add(new BasicNameValuePair("Key",formData.getString("Key")));
			    data.add(new BasicNameValuePair("Email",formData.getString("Email")));
			    //Log.i////System.out.println("####",formData.getString("Email"));
		        httpPost.setEntity(new UrlEncodedFormEntity(data));		
	    		resp = PostClient.execute(httpPost);  
	    		
	    		// Product has Been Saved
	    		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	    			return 1;
	    		}
	    		// Datastore Exception, Product not Saved Ask user to Try again
	    		if(resp.getStatusLine().getStatusCode() == 10000){
	    			return -500;
	    		}
	    		// Save limit Excedded, Cannot save further
	    		if(resp.getStatusLine().getStatusCode() == 10002){
	    			return -1;
	    		}
	    		// Service Error
	    		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
	    			return -404;
	    		}
	    		
          }
          catch(Exception e){
        	  return -404;
          }
          return -404;
		}
    	@Override
    	protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
		    Bundle b = new Bundle();
		    b.putInt("Result", result);
			receiver.send(1,b);
    		 
    	}
    }
    
    // Fetching All Products
     /* Returns 1, for successfully fetching product
      * Returns -1, on No Data Found
      * returns -404, on Service Unavailable
      * returns -500, on Datastore Error
      */
    class FetchAllProducts extends AsyncTask<String,Integer,Integer>{
    	final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
    	ArrayList<Product> productListArray = new ArrayList<Product>();   	
		@Override
		protected Integer doInBackground(String... params) {
			try{
				httpPost = new HttpPost(SERVER+"/getAllproduct");
			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("UserCity",formData.getString("City")));
				data.add(new BasicNameValuePair("UserState",formData.getString("State")));
				data.add(new BasicNameValuePair("UserCountry",formData.getString("Country")));
				data.add(new BasicNameValuePair("UserNearby",formData.getString("Nearby")));
				data.add(new BasicNameValuePair("UserCategory",formData.getString("Category")));
				data.add(new BasicNameValuePair("UserFilter",formData.getString("Filter")));
				data.add(new BasicNameValuePair("Offset",formData.getString("Offset")));
				data.add(new BasicNameValuePair("Limit",formData.getString("Limit")));
				
				
		        httpPost.setEntity(new UrlEncodedFormEntity(data));		
				resp = PostClient.execute(httpPost);
         	    
	    		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
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
	    		             jo1.getString("StuffLocation"),jo1.getString("Longitude"),jo1.getString("Latitude"),jo1.getString("Key"),
	    		             jo1.getString("Owner"),jo1.getString("StuffCity"),jo1.getString("StuffState"),jo1.getString("StuffCountry"),
	    		             jo1.getString("Phone"),Boolean.valueOf(jo1.getString("PostedByPhoneOption")));
	        	     
	        	       productListArray.add(obj);
	        	  
	               }
	               Collections.sort(productListArray);
	               if(AllProducts.productList.size() > 0)
	                AllProducts.productList.addAll(productListArray);
	               else 
	                AllProducts.productList = productListArray;
               
	               return 1;
         	    }
         	    // Servlet Error
         	    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
         	    	return -404;
         	    }
         	    // Datastore Error
         	    if(resp.getStatusLine().getStatusCode() == 10000){
         	    	return -500;
         	    }
         	    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
                    return -1;
         	    }
			}
			catch (ClientProtocolException e) {

				return -404;
			} catch (IOException e) {

				return -404;
			} catch (JSONException e) {
				return -404;
			} 
			return -404;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		    Bundle b = new Bundle();
		    b.putInt("Result", result);
			receiver.send(0,b);
		}     	
    }
    
    
    // Fetching Bid for a Product
    class FetchBids extends AsyncTask<String,Integer,Integer>{
    	final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
    	ArrayList<Bid> bidArray = new ArrayList<Bid>();
    	int requestTypeProduct = 0;
		@Override
		protected Integer doInBackground(String... arg0) {
		 try{	
			if(arg0[0].equals("Fetch All Bids")){
			  // My Account Screen
			  httpPost = new HttpPost(SERVER+"/getAllbid");
			  requestTypeProduct = 1;
			}
			else if(arg0[0].equals(PocketTrader.MY_SAVED_PRODUCT_DETAIL)){
				// My Saved product Detail Screen
				httpPost = new HttpPost(SERVER+"/getmybid");
				requestTypeProduct = 2;
			}
			else{
				// My product Detail Screen  
				httpPost = new HttpPost(SERVER+"/getmybid");
			    requestTypeProduct = 3;
			}
		    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
		    if(requestTypeProduct !=1)
		       data.add(new BasicNameValuePair("Key",formData.getString("Key")));			
		    else
			       data.add(new BasicNameValuePair("Owner",formData.getString("Owner")));	
		    
		    httpPost.setEntity(new UrlEncodedFormEntity(data));		
    		resp = PostClient.execute(httpPost);
    		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
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
                     Bid obj = new Bid((String)jo1.getString("Quote"),(String)jo1.getString("PostedBy"),(String)jo1.getString("Email"),
                    		           (String)jo1.getString("Date"),(String)jo1.getString("PostedByPhone"),
                    		           Boolean.valueOf(jo1.getString("PostedByPhoneOption")),(String)jo1.getString("ProductName"),
                    		           (String)jo1.getString("Key"));
	        	     
                     bidArray.add(obj);

                     
                      ////System.out.println("##### Bids has been Fetched." + bidArray.size());
	               }
	               Collections.sort(bidArray);
                   if(requestTypeProduct == 3)
	        	        MyProductScreen.bidList = bidArray;
                   else if(requestTypeProduct == 2)
                  	 MySavedProductScreen.bidList = bidArray;
                   else 
                  	AccountScreen.bidList = bidArray;
                   
	               return 1;
    		}      
	        if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
	           return -1;
	        } 	    	
	        if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
	           return -404;
	        }	         	    
    		
		 }catch(Exception e){
			 e.printStackTrace();
			 return -404;
		 }
		 return -404;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		    Bundle b = new Bundle();
		    b.putInt("Result", result);
		    if(requestTypeProduct == 1)
		    	receiver.send(1,b);	 // My Account Screen  
		    else if(requestTypeProduct == 2)
		    	receiver.send(0, b); // My Saved Product Detail Screen
		    else if(requestTypeProduct == 3)
		    	receiver.send(0, b); // My Product Detail Screen
		    	
		}     	
    }
    
    
    //Fetching Products for a User
    class FetchProducts extends AsyncTask<String,Integer,Integer>{
    	final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");
    	ArrayList<Product> productListArray = new ArrayList<Product>();
		@Override
		protected Integer doInBackground(String... params) {
			try{
				httpPost = new HttpPost(SERVER+"/getmyproduct");
			    ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			    data.add(new BasicNameValuePair("Email",formData.getString("Email")));			
			    data.add(new BasicNameValuePair("Offset",formData.getString("Offset")));			
			    data.add(new BasicNameValuePair("Limit",formData.getString("Limit")));			

			    httpPost.setEntity(new UrlEncodedFormEntity(data));		
	    		resp = PostClient.execute(httpPost);
         	    
	    		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
 	  		       BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
	  	           StringBuilder sb = new StringBuilder();
	               String line = null;
	               while((line = reader.readLine())!=null){
	             	     sb = sb.append(line);
	               }
	                ////System.out.println("#### Result has been Received");
	                ////System.out.println("#### Json String"+sb.toString());
	               
                   JSONObject jo = new JSONObject(sb.toString());
                   JSONArray ja = jo.getJSONArray("Data"); 	
	                ////System.out.println("#### Json Length :"+ja.length());
                   
	               for (int i = 0; i < ja.length(); i++) {
	        	     JSONObject jo1 = ja.getJSONObject(i);         
	        	     Product obj = new Product(jo1.getString("StuffName"),jo1.getString("StuffPrice"),jo1.getString("StuffDate"),
	    		             jo1.getString("ImageURL"),jo1.getString("StuffType"),jo1.getString("StuffCategory"),jo1.getString("StuffDescription"),
	    		             jo1.getString("StuffLocation"),jo1.getString("Longitude"),jo1.getString("Latitude"),jo1.getString("Key"),
	    		             jo1.getString("Owner"),jo1.getString("StuffCity"),jo1.getString("StuffState"),jo1.getString("StuffCountry"),
	    		             jo1.getString("Phone"),Boolean.valueOf(jo1.getString("PostedByPhoneOption")));
	        	     productListArray.add(obj);
		              ////System.out.println("#### Product Added to List");

	        	  
	               }
	               Collections.sort(productListArray);
	               //AccountScreen.myProductList = productListArray;
	                ////System.out.println("####### Object is null" + AccountScreen.myProductList == null );
	               if(AccountScreen.myProductList == null)
	            	   AccountScreen.myProductList =  new ArrayList<Product>();
	               if(AccountScreen.myProductList.size() > 0)
	            	   AccountScreen.myProductList.addAll(productListArray);
		               else 
		            	   AccountScreen.myProductList = productListArray;
	                ////System.out.println("#### Result has been Processed" + AccountScreen.myProductList.size());
	               return 1;
         	    }
         	    // Servlet Error
         	    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
         	    	return -404;
         	    }
         	    // Datastore Error
         	    if(resp.getStatusLine().getStatusCode() == 10000){
         	    	return -500;
         	    }
         	    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
                    return -1;
         	    }
			}
			catch (ClientProtocolException e) {
                e.printStackTrace();
				return -404;
			} catch (IOException e) {
                e.printStackTrace();
				return -404;
			} catch (JSONException e) {
				e.printStackTrace();
				return -404;
			} 
			return -404;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		    Bundle b = new Bundle();
		    b.putInt("Result", result);
			receiver.send(0,b);
		}    
    }
    
    //Performing Product Submittion
    public int submitProduct(){
    	new submitDeal().execute("SubmitDeal");
        return submitDataStatus;
    }
    //Perform Submitting of a Bid
    class SubmitBid extends AsyncTask<String,Integer,Integer>{
	    final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");  
        private int requestType = -1;
		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			    if(params[0].equals(PocketTrader.MY_PRODUCT_DETAIL)){
				  // My Product Detail Screen
				  requestType = 1;
				}
				else if(params[0].equals(PocketTrader.MY_SAVED_PRODUCT_DETAIL)){
					// My Saved product Detail Screen
					requestType = 2;
				}
				else{
					// Product Detail Screen  
					requestType = 3;
				}			
			
			
			try{
				  ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
	              data.add(new BasicNameValuePair("Key", formData.getString("Key")));
	              data.add(new BasicNameValuePair("ProductName",formData.getString("ProductName")));
	              data.add(new BasicNameValuePair("Email", formData.getString("Email")));
	              data.add(new BasicNameValuePair("Quote",  formData.getString("Quote")));   
	              data.add(new BasicNameValuePair("Owner",formData.getString("Owner")));
	              data.add(new BasicNameValuePair("Name",formData.getString("Name")));
	              data.add(new BasicNameValuePair("PostedByPhone",formData.getString("PostedByPhone")));
	              data.add(new BasicNameValuePair("PostedByPhoneOption",formData.getString("PostedByPhoneOption")));
	              httpPost.setURI(new URI(SERVER + "/savemybid"));
	              httpPost.setEntity(new UrlEncodedFormEntity(data));
	    		  resp = PostClient.execute(httpPost);
	              if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	              {
		               return 0;
	              }
	              if(resp.getStatusLine().getStatusCode() == 10000){
	            	  return -500;
	              }				
				
			}
			catch(Exception e){
				return -404;
			}
			return -404;
		}
	    @Override
	    protected void onPostExecute(Integer result) {
	    	// TODO Auto-generated method stub
	    	super.onPostExecute(result);
	        Bundle b = new Bundle();
	        b.putInt("Result", result);
	        if(requestType == 3)
        	  receiver.send(0, b);	  // Dispatch to Product Detail Screen
	        if(requestType == 2)
	         receiver.send(2, b);     // Dispatch to My Saved product Detail Screen
	        if(requestType == 1)
	         receiver.send(2, b);     // Dispatch to My Product Detail Screen

	    }
   	
    }
    //Submits a Deal
	class submitDeal extends AsyncTask{

	    final ResultReceiver receiver = (ResultReceiver)formData.getParcelable("Receiver");  
		@Override
      protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
     	
        	try
            {
    		  
              ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
              data.add(new BasicNameValuePair("StuffName", formData.getString("Name")));
              data.add(new BasicNameValuePair("StuffCategory", formData.getString("Category")));
              data.add(new BasicNameValuePair("StuffType",  formData.getString("Type")));                    		
              data.add(new BasicNameValuePair("StuffDescription", formData.getString("Description")));
              data.add(new BasicNameValuePair("StuffPrice",  formData.getString("Price")));
              data.add(new BasicNameValuePair("StuffLocation", formData.getString("Address"))); 
              data.add(new BasicNameValuePair("Email", formData.getString("Email")));
              data.add(new BasicNameValuePair("Longitude",formData.getString("Longitude")));
              data.add(new BasicNameValuePair("Latitude",formData.getString("Latitude")));
              data.add(new BasicNameValuePair("StuffCity",formData.getString("City")));
              data.add(new BasicNameValuePair("StuffState",formData.getString("State")));
              data.add(new BasicNameValuePair("StuffCountry",formData.getString("Country")));
              data.add(new BasicNameValuePair("StuffArea",formData.getString("Area")));
              data.add(new BasicNameValuePair("StuffPostalCode",formData.getString("PostalCode")));              
              data.add(new BasicNameValuePair("Phone",formData.getString("Phone")));
              data.add(new BasicNameValuePair("PostedByPhoneOption",formData.getString("PostedByPhoneOption")));
              
              httpPost.setURI(new URI(SERVER + "/savestuff"));
              httpPost.setEntity(new UrlEncodedFormEntity(data));
    		  resp = PostClient.execute(httpPost);
		
              if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
              {
	  		       BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
	  	           StringBuilder sb = new StringBuilder();
	               String line = null;
	               while((line = reader.readLine())!=null){
	             	     sb = sb.append(line);
	               }                  
	               submitDealKey = sb.toString();
	               return "Success";
              }
              if(resp.getStatusLine().getStatusCode() == 10000){
            	  return "Datastore Exception";
              }
			
		}
		catch(Exception e){
			     return "Unknown Exception";
		}
        return "Unknown Exception";
	}
    @Override
    protected void onPostExecute(Object result) {
    	// TODO Auto-generated method stub
    	super.onPostExecute(result);
        String res = (String) result;
        if(res == "Success"){
        	// Deal Posted, Take User to Capture Image Screen
        	submitDataStatus = 0;
        	Bundle b = new Bundle();
        	b.putString("Key", submitDealKey);
        	receiver.send(submitDataStatus, b);
        }
        else if(res == "Datastore Exception")
        {
        	// Datastore Exception, Ask user to resubmit Deal
        	submitDataStatus = -404;
        	receiver.send(submitDataStatus, new Bundle());
        }
        else{
        	// Pop Up, Unknow Error box and take user to Home Screen
        	submitDataStatus = -1;
        	receiver.send(submitDataStatus, new Bundle());
        }
    }
	
}	
    
    class Monitor extends Thread{
    	int index = 0;
    	public void run(){
    		while(ProductsService.submitDataStatus == -2 && index <=30)
    		{
    			index = index + 1;
    		  try {
					sleep(1000);
				  } 
    			catch (InterruptedException e) {
                   
				}
    		   
    		}
    	}
    }
}
