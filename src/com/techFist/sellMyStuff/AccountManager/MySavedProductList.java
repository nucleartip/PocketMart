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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MySavedProductList extends PocketActivity {


	private Context context;
	private ListView myProductList;
	private Bundle formData;
	private CustomDialog customDialog;
	private AlertDialog dialog;
    private CustomPoductList customProductList;
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
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,MySavedProductList.class));

		
		// Initializing Form Objects
		context = this;
		myProductList = (ListView)findViewById(R.id.my_account_productList);
		formData = new Bundle();		
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
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
				if(AccountScreen.mySavedList != null){
					for(Product product:AccountScreen.mySavedList){
						
						if(key.equals(product.getParentKey())){
							AccountScreen.mySavedList.remove(index);
							break;
						}
						index = index + 1;
					}
				}
			}
		}
		// Check if Size of List is Zero, if true Push Account Screen Displaying no Products Found Message
		if(AccountScreen.mySavedList.size() < 1){
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
		if(AccountScreen.mySavedList != null){
		   customProductList = new CustomPoductList(AccountScreen.mySavedList,context);
	       myProductList.setAdapter(customProductList);
	       myProductList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				try{
				 Product product = (Product)adapter.getItemAtPosition(position);
				 formData.putParcelable("Product", product);
				 formData.putString(PocketTrader.SCREEN_TYPE, PocketTrader.MY_SAVED_PRODUCT_DETAIL);
				 Intent intent = new Intent(context,MySavedProductScreen.class);
				 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				 intent.putExtras(formData);
				 startActivity(intent);}catch(Exception e){e.printStackTrace();}				
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
					 
}
