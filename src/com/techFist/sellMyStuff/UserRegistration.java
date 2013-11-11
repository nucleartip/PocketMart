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
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class UserRegistration extends PocketActivity implements ServiceCallback.Receiver{


	private Context context;
	private LoginService localService;
	private int registerStatus = -1;	
	private int userDataStatus = -1;
	private Bundle userData;
	private TextView userFirstName;
	private TextView userLastName;
	private Spinner userCountry;
	private Spinner userState;
	private TextView userPswd;
	private TextView userCnfPswd;
	private TextView userEmail;
	private Button btnRegister;
	private Button btnLogin;
	private TextView userCity;
	private CheckBox phoneOption;
	private EditText userPhone;
	private AlertDialog waitDialog;
	private AlertDialog dialog;
	private CustomDialog customDialog;	
    private ArrayAdapter<CharSequence> country;	
    private ArrayAdapter<CharSequence> state;    
	public ServiceCallback mReceiver;
	public static AsyncTask<String,Integer,Integer> registerTask;
    public static AsyncTask<String,Integer,Integer> userTask;
    private boolean isServiceRequested = false;
    
    
	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	    	isServiceRequested = true;
	    	localService = ((LoginService.LocalBinder)service).getService();
            localService.onCreate();
            registerStatus = localService.onStartCommand(new Intent().putExtras(userData), 0, 2);
            if(registerStatus == -404)
               doUnbindService(mConnection);
            
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	localService = null;
	    	isServiceRequested = true;

	    }
	};
	// Service Connection Manager
	private ServiceConnection mConnectionUser = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	    	localService = ((LoginService.LocalBinder)service).getService();
            localService.onCreate();
            registerStatus = localService.onStartCommand(new Intent().putExtras(userData), PocketTrader.USER_REGEISTRATION_SCREEN, 4);
            if(registerStatus == -404)
               doUnbindService(mConnectionUser);
            
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	localService = null;

	    }
	};	
	// Bind Service to Activity
	void doBindService(ServiceConnection connection) {
	    bindService(new Intent(UserRegistration.this,LoginService.class), connection, Context.BIND_AUTO_CREATE);
	}

	// Un Binding Service
	void doUnbindService(ServiceConnection connection) {
		unbindService(connection);
		isServiceRequested = false;
        // Register Status = 0, User is Confirmed registered Push to PocketTrader Screen
        // Register status = -1,DataStore Exception ask user to start registration again
        // Register Status = 1, Suspecious ask user to Sing In.
        // Register Status = 3, User Already Exist, ask him to sign in again
        // Register status = 4, same as status 1
	    if(registerStatus == 0){
	    	 updatePreference(userData.getString("Email"),userData.getString("Password"));
	    	 // resetting register status
	    	 registerStatus = 301;
	    	 doBindService(mConnectionUser);
	    	 
	     }

	    
	    if(userDataStatus == PocketTrader.PROCESS_SUCCESS){
	    	 
	    	 waitDialog.cancel(); 
	    	 // Push Settings Pages
	    	 Intent intent = new Intent(context,SettingsActivity.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 startActivityForResult(intent,0);
	    	 
	    	 // User has been Registered
	    	 //Intent intent = new Intent(context,PocketTrader.class);
	    	 //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 //startActivityForResult(intent,0);	    	
	    }
	    
	    
	    if(registerStatus == -1){
	    	 waitDialog.cancel();  
	    	 
	    	 customDialog = new CustomDialog(context,"Exit","Retry",getString(R.string.network_error));
	    	 final Intent intent = new Intent(context,UserRegistration.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 AlertDialog.Builder infoDialog = new AlertDialog.Builder(context);

	    	 
	    	 android.content.DialogInterface.OnClickListener listenPositive = new  android.content.DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					startActivity(intent);
				}
	    		 
	    	 };	    	 
	    	 android.content.DialogInterface.OnClickListener listenNegative = new android.content.DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
	    		 
	    	 };
	    	 
	    	 infoDialog.setPositiveButton("Exit", listenNegative);
	    	 infoDialog.setNegativeButton("Retry", listenPositive);
	    	 infoDialog.setTitle("Network Error");
	    	 infoDialog.setMessage("Network Error, Please try again");
	    	 //dialog = customDialog.getConfirmationDialogForAction("Network Error", intent,listen);
	    	 infoDialog.show();
   	
	    }
	    if(registerStatus == 1 || userDataStatus == PocketTrader.DATASTORE_EXCEPTION || userDataStatus == PocketTrader.SERVICE_UNAVAILABLE){
	    	 waitDialog.cancel();  
	    	 Toast.makeText(context,"Please Login",Toast.LENGTH_LONG).show();	    	
	    	 Intent intent = new Intent(context,SecureLoginScreen.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    }	    
	    if(registerStatus == 3 ){
	    	 waitDialog.cancel(); 
	    	 Toast.makeText(context,"User Already Exist, Please Login",Toast.LENGTH_LONG).show();	    	
	    	 Intent intent = new Intent(context,SecureLoginScreen.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    }		    
	    if(registerStatus == 4){
	    	 waitDialog.cancel(); 
	    	 Toast.makeText(context,"Please Try Again",Toast.LENGTH_LONG).show();	 
	    	 Intent intent = new Intent(context,SecureLoginScreen.class);
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    }
	    if(registerStatus == -404){
	    	 waitDialog.cancel(); 
	    	 customDialog = new CustomDialog(context,"Okay","Retry","Network Error, Please try again");	    	
	         dialog = customDialog.getErrorDialog("Network Error");
	         dialog.show();
	    }
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.register_user_screen);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header_lout);
		
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,UserRegistration.class));
		
	    //setContentView(R.layout.register_user_screen);
	    context = this;
		userData = new Bundle();
        btnRegister = (Button)findViewById(R.id.register_user);
        userFirstName = (TextView)findViewById(R.id.register_user_name);
        userLastName = (TextView)findViewById(R.id.register_user_last_name);
        userCountry = (Spinner)findViewById(R.id.register_user_country);
        userState = (Spinner)findViewById(R.id.register_user_state);
        userPswd = (TextView)findViewById(R.id.register_user_password);
        userCnfPswd = (TextView)findViewById(R.id.register_user_confirm_password);
        userEmail = (TextView)findViewById(R.id.register_user_email);
        userCity = (TextView)findViewById(R.id.register_user_city);
        userPhone = (EditText)findViewById(R.id.register_user_phone);
        btnLogin = (Button)findViewById(R.id.register_signin_user);
        phoneOption = (CheckBox)findViewById(R.id.register_phone_option);
        mReceiver = new ServiceCallback(new Handler());
        mReceiver.setReceiver(this); 	    
	}
    private boolean validateForm(){
    	String ufn = userFirstName.getText().toString();
    	String uemail = userEmail.getText().toString();
    	String pswd = userPswd.getText().toString();
    	String pswdCnf = userCnfPswd.getText().toString();
    	String uCity = userCity.getText().toString();
    	String uPhone = userPhone.getText().toString();
    	
    	// Confirm if password matches
    	if(!pswd.equals(pswdCnf) || pswd == "" || pswd.length() == 0)
    	{
    		Toast.makeText(context, R.string.valid_pswd, Toast.LENGTH_SHORT).show();
    	    return false;
    	}
    	// Validate email
    	if(uemail == "" || !uemail.contains("@") || !uemail.contains(".")){
    		Toast.makeText(context, R.string.valid_email, Toast.LENGTH_SHORT).show();
    	    return false;
    	}
    	// Validate rest
    	if(ufn == "" || uCity == "" || uPhone == "" ||ufn == " " || uCity == " " || uPhone == " "
    	   || ufn.length() == 0 || uCity.length() < 3 || uPhone.length() < 10)
    	{
    		Toast.makeText(context, R.string.valid_missing_field, Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	
    	return true;
    }
	@Override   
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
        // Populating Spinner Content
		country = ArrayAdapter.createFromResource(context,R.array.country_list,android.R.layout.simple_spinner_item);   	
		country.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        		
		state = ArrayAdapter.createFromResource(context,R.array.state_list,android.R.layout.simple_spinner_item);   	
		state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        		

		userCountry.setAdapter(country);
		userState.setAdapter(state);
		
		btnRegister.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
			   if(validateForm()){	
				// TODO Auto-generated method stub
				userData.putParcelable("Receiver", mReceiver);
				userData.putString("FirstName",userFirstName.getText().toString());
				userData.putString("LastName", userLastName.getText().toString());
				userData.putString("Email", userEmail.getText().toString());
				userData.putString("Password", userPswd.getText().toString());
				userData.putString("Country", userCountry.getSelectedItem().toString());	
                userData.putString("City", userCity.getText().toString());
                userData.putString("State", userState.getSelectedItem().toString());
                userData.putString("Phone", userPhone.getText().toString());
                userData.putString("PhoneOption", String.valueOf(phoneOption.isChecked()));
				customDialog = new CustomDialog(context,"","","Registering..");
				waitDialog = customDialog.getWaitDialog("Please Wait",true);
				waitDialog.show();	
				doBindService(mConnection);}			}
        	
        });
		btnLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                Intent intent = new Intent(context,SecureLoginScreen.class);
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
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	if(isServiceRequested)
    		doUnbindService(mConnection);
    	setResult(0);
    	finish();
    }
    
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		if(resultCode == 0){

		    registerStatus = resultData.getInt("Result");
			doUnbindService(mConnection);
		}
	  if(resultCode == PocketTrader.FETCH_USER_DATA_REQUEST){

		  userDataStatus = resultData.getInt("Result");
		  doUnbindService(mConnectionUser);  
	  }
	}
	
}
