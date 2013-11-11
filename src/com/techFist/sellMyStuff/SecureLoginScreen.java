package com.techFist.sellMyStuff;

import com.google.ads.AdView;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Services.LoginService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SecureLoginScreen extends PocketActivity implements ServiceCallback.Receiver{
	

	private Context context;
	private int signInStatus = -1;
	private int userDataStatus = -1;
	private AlertDialog dialog;
	private CustomDialog customDialog;
	private LoginService localService;	
	Bundle formData;

	private TextView userEmail;
	private TextView userPswd;
	private Button btnLogin;
	private Button btnRegister;
	public ServiceCallback mReceiver;
	public static AsyncTask<String,Integer,Integer> loginTask;	
    public static AsyncTask<String,Integer,Integer> userTask;

    
	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	    	localService = ((LoginService.LocalBinder)service).getService();
            localService.onCreate();
            Intent intent = new Intent();
            intent.putExtras(formData);
            signInStatus = localService.onStartCommand(intent, 0, 3);
            // No Network Present
            if(signInStatus == -404)
            	doUnbindService(mConnection);
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	localService = null;
	    	 

	    }
	};	
	private ServiceConnection mConnectionUser = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	    	localService = ((LoginService.LocalBinder)service).getService();
            localService.onCreate();
            Intent intent = new Intent();
            intent.putExtras(formData);
            userDataStatus = localService.onStartCommand(intent,PocketTrader.SECURE_LOGIN_SCREEN, 4);
            // No Network Present
            if(userDataStatus == -404)
            	doUnbindService(mConnection);
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	localService = null;

	    }
	};	
	
	
	// Bind Service to Activity
	void doBindService(ServiceConnection connection) {
	    bindService(new Intent(SecureLoginScreen.this,LoginService.class), connection, Context.BIND_AUTO_CREATE);
	}
	
	// Un Binding Service
	void doUnbindService(ServiceConnection connection) {
	      
	     unbindService(connection);
		
	     //SignInStatus = 0, User Authenticated
	     //SignInStatus = -1, Datastore or Network Error
	     //SignInStatus = 1, User Not Authenticated
	     if(signInStatus == 0){
	    	 updatePreference(formData.getString("Email"),formData.getString("Password"));
	    	 // Download new User Data
	    	 // Resetting signin Status
	    	 signInStatus = 301;

	    	 doBindService(mConnectionUser);

	     }

	     // UserStatus
	     else if(userDataStatus == PocketTrader.PROCESS_SUCCESS){
	    	 dialog.cancel();
	    	 // Push Settings Pages
	    	 Intent intent = new Intent(context,SettingsActivity.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 startActivity(intent);	    	 
	    	 //Intent intent = new Intent(context,PocketTrader.class);
	    	 //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	    	 //startActivityForResult(intent, 0);	    	 
	     }
	     else if(userDataStatus == PocketTrader.DATASTORE_EXCEPTION){
	    	 dialog.cancel();
	    	 customDialog = new CustomDialog(context,"Okay","Retry","Oops this is Embarassing, Error occued during processing data. Please try Again");	    	
	         dialog = customDialog.getErrorDialog("Datastore Error");
	         dialog.show();		    	 
	     }
	     else if(signInStatus == -1 || signInStatus == -404 || userDataStatus == -404 || userDataStatus == PocketTrader.SERVICE_UNAVAILABLE){
	    	 dialog.cancel();
	    	 customDialog = new CustomDialog(context,"Okay","Retry","Network Error, Please try again");	    	
	         dialog = customDialog.getErrorDialog("Network Error");
	         dialog.show();	    	 
	     }
	     else if(signInStatus == 1){
	    	dialog.cancel();
	    	Toast.makeText(context,"User ID or Password Incorrect, Please try Again !!", Toast.LENGTH_LONG).show(); 
	     }	     
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,SecureLoginScreen.class));
		
	    //setContentView(R.layout.secure_signin_screen);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.secure_signin_screen);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header_lout);
		
		
	    context = this;
	    formData = new Bundle();
	    // Fetching Form Objects
	    btnLogin = (Button)findViewById(R.id.signin_login_user);
	    userEmail = (TextView)findViewById(R.id.signin_user_email);
	    userPswd = (TextView)findViewById(R.id.signin_user_password);
	    btnRegister = (Button)findViewById(R.id.signin_register_user);
	    mReceiver = new ServiceCallback(new Handler());
	    mReceiver.setReceiver(this);

	}
   // Form validation
   private boolean validateSubmission(){
	   String email = userEmail.getText().toString();
	   String pswd =  userPswd.getText().toString();
	   if(email != "" && pswd != "" && email.contains("@") && email.contains(".") && pswd.length() > 0){
		   return true;
	   }else{
		   
		   Toast.makeText(context,R.string.valid_login_msg, Toast.LENGTH_SHORT).show();
		   return false;
	   }
	   
   }
	
	
   @Override
   protected void onStart() {
	// TODO Auto-generated method stub
	super.onStart();
    // Adding Listeners
    btnLogin.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(validateSubmission()){
			  formData.putString("Email", userEmail.getText().toString());
			  formData.putString("Password", userPswd.getText().toString());
			  formData.putParcelable("Receiver", mReceiver);
			  customDialog = new CustomDialog(context,"","","Loggin in..");
			  dialog = customDialog.getWaitDialog("Please Wait",true);
			  dialog.show();
			  doBindService(mConnection);
			}	
		}
    	
    });
    btnRegister.setOnClickListener(new OnClickListener(){
    	

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,UserRegistration.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
    	
    });  
   
   }
   private void updatePreference(String email,String pswd){
   	String PREFS_NAME = "UserAccount";
   	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);    
		SharedPreferences.Editor editor = prefs.edit();
	  	editor.putBoolean("Account-Status", true);
	  	editor.putString("Email", email);
	  	editor.putString("Password", pswd);
	  	editor.commit();
   }   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
      super.onActivityResult(requestCode, resultCode, data);
      if(requestCode == 0){
    	  finish();
      }
   
   }
   @Override
   protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	setResult(0);
	finish();
   }
   @Override
   public void onBackPressed() {
   	// TODO Auto-generated method stub
   	super.onBackPressed();
   	setResult(0);
   	finish();
   }
  @Override
  public void onReceiveResult(int resultCode, Bundle resultData) {
	// TODO Auto-generated method stub
	if(resultCode == 0){
	    signInStatus = resultData.getInt("Result");
        doUnbindService(mConnection);
	 }
   // Returned after downloading user data
   if(resultCode == PocketTrader.FETCH_USER_DATA_REQUEST){

	   userDataStatus = resultData.getInt("Result");
	   doUnbindService(mConnectionUser);
   }
  
  }
   
}
