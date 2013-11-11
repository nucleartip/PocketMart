package com.techFist.sellMyStuff;


import java.math.BigDecimal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.facebook.android.*;
import com.facebook.android.Facebook.*;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PayPalInstance;
import com.techFist.sellMyStuff.Commons.PocketActivity;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal; import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalReceiverDetails;
import com.paypal.android.MEP.PayPalPayment;

public class SettingsActivity extends PocketActivity {


	private Context context;
	 private CheckBox pushRegistration;
	 private CheckBox facebookRegistration;
	 private CheckBox promotionRegistration;
	 private Button donate;
	 private boolean initialPush,initialPromo;
	 private Facebook facebook;
     private PayPal ppObj; 
     private ViewGroup viewGroup;
     
     private static final String[] PERMISSIONS = new String[] {"publish_stream"};
     
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
        // Setting up Custom 
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);		
		setContentView(R.layout.settings_page);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
        //Register a Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,SettingsActivity.class));
        // Setting up PayPal Credentials
        //ppObj = PayPalInstance.getInstance(this);
        
        
        // Fetching form Objects
	    this.context = this;
	    this.viewGroup = (ViewGroup)findViewById(R.id.setting_group);
	    this.pushRegistration = (CheckBox)findViewById(R.id.settings_notification);
	    this.facebookRegistration = (CheckBox)findViewById(R.id.settings_facebook);
	    this.promotionRegistration = (CheckBox)findViewById(R.id.settings_promotion);
	    //this.donate = (Button)findViewById(R.id.setting_donate);
	    facebook = new Facebook(PocketTrader.APP_ID);		
	    
	    // Addign Checkout Button
	    //CheckoutButton launchPayPalButton = ppObj.getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_PAY);	
	    ////launchPayPalButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	    //viewGroup.addView(launchPayPalButton);
	    /*
	    // Payment Listener
	    launchPayPalButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				new PurchaseTask().execute("Pay");
			}
	    	
	    });
	    */
	    // Setting up Onclick Listener
	    facebookRegistration.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
			   // If checked then Store FB Credential's, and Whenever user posts a Deals post that Into FB Automatically
			  boolean flag = facebookRegistration.isChecked();
			  savePreferences();
			  if(flag){
				  if(!restoreCredentials(facebook))
				  loginAndSaveSredential();
			  }else{
				  deleteCredential();
			  }
				
			}
	    	
	    });
	    /*
	   // Setting up Donate Listener
	    donate.setOnClickListener(new OnClickListener(){
            
			@Override
			public void onClick(View v) {
				
			}
	    	
	    });*/
	    
	  // Setting up Push Notification Listener
	    pushRegistration.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				savePreferences();
			}
	    	
	    });
	 // Setting up Promotions Listeneres
	    promotionRegistration.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// If checked then Store Push Registration Status, and register it While Logging in
				savePreferences();
		    }	    	
	    });
	 
	 }
	 
	 public class PurchaseTask extends AsyncTask <String, Void, String> {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				PayPalPayment newPayment = new PayPalPayment();
				newPayment.setSubtotal(BigDecimal.valueOf(1));
				newPayment.setCurrencyType("USD");
				newPayment.setRecipient("dev.te_1349629880_biz@gmail.com");
				newPayment.setMerchantName("Techfist Inc's Test Store");
				Intent paypalIntent = PayPal.getInstance().checkout(newPayment, context);
				startActivityForResult(paypalIntent, 1);
				return null;
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
	 
	 @Override
	 protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// Loading Initial values
    	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PocketTrader.DETAIL_KEY, Context.MODE_PRIVATE);
    	initialPush = sharedPreferences.getBoolean(PocketTrader.PUSH_NOTIF, false);
    	pushRegistration.setChecked(initialPush);
    	initialPromo = sharedPreferences.getBoolean(PocketTrader.PROMO, false);
    	promotionRegistration.setChecked(initialPromo);
    	facebookRegistration.setChecked(sharedPreferences.getBoolean(PocketTrader.FACEBOOK, false));
	 }
	 @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	 }
	 @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		savePreferences();

		saveModifiedStatus(initialPush,initialPromo);
   	    Intent intent = new Intent(context,PocketTrader.class);
   	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   	    startActivity(intent);	
   	    setResult(0);
   	    finish();	
	 }
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		savePreferences();
		saveModifiedStatus(initialPush,initialPromo);	
   	    setResult(0);
   	    finish();		
	}
	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    switch(resultCode) {
	      case Activity.RESULT_OK:
	      //The payment succeeded
	         String payKey = data.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
	      //Tell the user their payment succeeded
	         Toast.makeText(context, "Payment Success", Toast.LENGTH_SHORT).show();
	      break;
	      case Activity.RESULT_CANCELED:
	      //The payment was canceled
	      //Tell the user their payment was canceled
	    	  Toast.makeText(context, "Payment Cancelled", Toast.LENGTH_SHORT).show();
	      break;
	      case PayPalActivity.RESULT_FAILURE:
	      //The payment failed -- we get the error from the EXTRA_ERROR_ID and EXTRA_ERROR_MESSAGE
	      String errorID = data.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
	      String errorMessage = data.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
	      
	      //Tell the user their payment was failed.
	      Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
	      default:
	      facebook.authorizeCallback(requestCode, resultCode, data);
	    }
        
	 }

	 
	 // Save Promo,Push and Facebook status
	 public void savePreferences(){
	    	Editor editor = getApplicationContext().getSharedPreferences(PocketTrader.DETAIL_KEY, Context.MODE_PRIVATE).edit();
	    	editor.putBoolean(PocketTrader.PUSH_NOTIF, pushRegistration.isChecked());
	    	editor.putBoolean(PocketTrader.PROMO, promotionRegistration.isChecked());
	    	editor.putBoolean(PocketTrader.FACEBOOK, facebookRegistration.isChecked());
	    	editor.commit();
	 }
	 // Save modified status for Promo and Push, this will be used to Fire request upon Login
	public void saveModifiedStatus(boolean promoModified,boolean pushModified){
		   // Getting latest Status;
    	   SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PocketTrader.DETAIL_KEY, Context.MODE_PRIVATE);

    	   if(sharedPreferences.getBoolean(PocketTrader.PUSH_NOTIF, false) == pushModified)
			   pushModified = false;
		   else {
			   pushModified = true;

		   }if(sharedPreferences.getBoolean(PocketTrader.PROMO, false) == promoModified)
			   promoModified = false;
		   else{
			   promoModified = true;

		   }
		   // Setting Modified Status
		   Editor editor = getApplicationContext().getSharedPreferences(PocketTrader.SETTINGS_STAT, Context.MODE_PRIVATE).edit();
		   if(promoModified)
		   editor.putBoolean(PocketTrader.SETTINGS_MODIFIED_PROMO, true);
		   if(pushModified)
		   editor.putBoolean(PocketTrader.SETTINGS_MODIFIED_PUSH, true);
		   editor.commit();
	}
	 
	// Save Credential
    public boolean saveCredentials(Facebook facebook) {
        	Editor editor = getApplicationContext().getSharedPreferences(PocketTrader.KEY, Context.MODE_PRIVATE).edit();
        	editor.putString(PocketTrader.TOKEN, facebook.getAccessToken());
        	editor.putLong(PocketTrader.EXPIRES, facebook.getAccessExpires());
        	return editor.commit();
    }
    // Delete Credential
    public  void deleteCredential(){
        	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PocketTrader.KEY, Context.MODE_PRIVATE);  
            Editor editor = sharedPreferences.edit();
        	editor.putString(PocketTrader.TOKEN, null);
        	editor.putLong(PocketTrader.EXPIRES, 0);
        	editor.commit();
            
    }
    // Restore Credential
    public boolean restoreCredentials(Facebook facebook) {
        	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PocketTrader.KEY, Context.MODE_PRIVATE);
        	facebook.setAccessToken(sharedPreferences.getString(PocketTrader.TOKEN, null));
        	facebook.setAccessExpires(sharedPreferences.getLong(PocketTrader.EXPIRES, 0));
        	return facebook.isSessionValid();
   }	    

    	
   // Method for Saving FB Auth. 	
   public void loginAndSaveSredential(){
   	   facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
   }



   	class LoginDialogListener implements DialogListener {
   	    public void onComplete(Bundle values) {
   	    	saveCredentials(facebook);
   	    	// Post start using Pocket Trader Message
   	    	new PostToFacebook().execute(new Bundle());
   	    	
   	    }
   	    public void onFacebookError(FacebookError error) {
   	    	//Log.i////System.out.println("####" , error.getMessage().toString());
   	    	showToast("Authentication with Facebook failed!");
   	        //finish();
   	    }
   	    public void onError(DialogError error) {
   	    	//Log.i////System.out.println("####" , error.getMessage().toString());
   	    	showToast("Authentication with Facebook failed!");
   	        //finish();
   	    }
   	    public void onCancel() {
   	    	
   	    	showToast("Authentication with Facebook cancelled!");
   	        //finish();
   	    }
   	}

   	private void showToast(String message){
   		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
   	} 
	class PostToFacebook extends AsyncTask<Bundle,Integer,Integer>{
        private Facebook facebook;
		public PostToFacebook(){
		  facebook = new Facebook(PocketTrader.APP_ID);	
		}
		
		@Override
		protected Integer doInBackground(Bundle... params) {
			// TODO Auto-generated method stub
			
			boolean flag = restoreCredentials(facebook);
			if(flag){  // Session is Valid Post Deal in facebook
		   		Bundle parameters = new Bundle();
                String message = getString(R.string.started_using);
		   		
		        parameters.putString("message", message);
		        parameters.putString("link", "http://pockettrader.wix.com/pocket-trader");
                try {
           	      facebook.request("me");
   			      String response = facebook.request("me/feed", parameters, "POST");
   	   			  if (response == null || response.equals("") ||
   	   			        response.equals("false")) {
   	   				return -1;
   	   			  }
   	   			  else {
   	   				return 1;
   	   			  }
   		          }catch (Exception e){
   			       e.printStackTrace();
                   return -404;
   		         }		        
				
			}
			return -1;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		    if(result == 1){
		    	//Log.i////System.out.println("####", "Posted to you wall");
		    }else{
		    	//Log.i////System.out.println("####", "Some Stupid Error Occured");
		    }
		    	
		}
	}	
    public void showMsgDialog(View v){
        
    	MessageHandler.getInstance(context).showMyMessageList(context);
    } 
    
  }
