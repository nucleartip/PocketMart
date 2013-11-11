package com.techFist.sellMyStuff.AccountManager;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

import com.google.ads.AdView;
import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.ServiceCallback;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;
import com.techFist.sellMyStuff.Commons.User;
import com.techFist.sellMyStuff.Services.LoginService;

public class MyProfileScreen extends PocketActivity implements ServiceCallback.Receiver{


	private Context context;
	private Bundle formData;
	private EditText userFirstName;
	private EditText userLastName;
	private EditText userEmail;
	private EditText userPhone;
	private EditText userCity;
	private Spinner userCountry;
	private Spinner userState;
	private CheckBox phoneOption;
	private Button editProfile;
	private Button cancelProfile;
	private User user;
	private CustomDialog customDialog;
	private LoginService localService;
	private int updateStatus = -404;
	public ServiceCallback mReceiver;	

    private ArrayAdapter<CharSequence> country;	
    private ArrayAdapter<CharSequence> state; 	
	private AlertDialog dialog;
	// Service Connection Manager
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	    	localService = ((LoginService.LocalBinder)service).getService();
            localService.onCreate();
            // Popping up Wait Dialog
            updateStatus = localService.onStartCommand(new Intent().putExtras(formData), 0, 5);
            if(updateStatus == -404)
              doUnbindService();
            
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	localService = null;

	    }
	};	
	// Bind Service to Activity
	void doBindService() {
	    bindService(new Intent(MyProfileScreen.this,LoginService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	// Un Binding Service
	void doUnbindService() {
	     // Detach our existing connection.
	    unbindService(mConnection);	
	    if(updateStatus == 1){
	    	dialog.cancel();
	    	Toast.makeText(context,"Details Updated", Toast.LENGTH_LONG).show();
	    	// Update new user detail to session
	    	PocketTrader.user.setUserCity(formData.getString("City"));
	    	PocketTrader.user.setUserFirstName(formData.getString("FirstName"));
	    	PocketTrader.user.setUserLastName(formData.getString("LastName"));
	    	PocketTrader.user.setUserPhone(formData.getString("Phone"));
	    	PocketTrader.user.setUserEmail(formData.getString("Email"));
	    	PocketTrader.user.setUserCountry(formData.getString("Country"));
	    	PocketTrader.user.setUserState(formData.getString("State"));
	    	PocketTrader.user.setPhoneOption(Boolean.valueOf(formData.getString("PhoneOption")));
	    	refreshFormData();
	    }
	    if(updateStatus == -1){
	    	dialog.cancel();
	    	customDialog = new CustomDialog(context,"Okay","Home",getString(R.string.saving_data_error));
	       	dialog = customDialog.getErrorDialog("Datastore Error");
	       	dialog.show();
	       	
	    } 	    
	    if(updateStatus == -404){
	    	dialog.cancel();
	    	customDialog = new CustomDialog(context,"Okay","",getString(R.string.unknown_error));
	    	dialog = customDialog.getErrorDialog("Unknown Excecption");
	    	dialog.show();
	    }
	
	}    
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    //setContentView(R.layout.my_profile_edit_screen);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.my_profile_edit_screen);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header_msg);
		
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,MyProfileScreen.class));
		
	    // Initializing form Objects
	    context = this;
	    formData = new Bundle();
	    phoneOption = (CheckBox)findViewById(R.id.my_profile_phone_option);
	    userFirstName = (EditText)findViewById(R.id.my_profile_first_name);
	    userLastName = (EditText)findViewById(R.id.my_profile_last_name);
	    userEmail = (EditText)findViewById(R.id.my_profile_email);
	    userPhone = (EditText)findViewById(R.id.my_profile_phone);
	    userCity = (EditText)findViewById(R.id.my_profile_city);
	    userCountry = (Spinner)findViewById(R.id.my_profile_country);
	    userState = (Spinner)findViewById(R.id.my_profile_state);
	    
	    editProfile = (Button)findViewById(R.id.my_profile_edit);
	    cancelProfile = (Button)findViewById(R.id.my_profile_cancel);
	    mReceiver = new ServiceCallback(new Handler());
	    mReceiver.setReceiver(this);	    

	}
   @Override
   protected void onStart() {
	// TODO Auto-generated method stub
	super.onStart();
    // Populating Spinners
	country = ArrayAdapter.createFromResource(context,R.array.country_list,android.R.layout.simple_spinner_item);   	
	country.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        		
	state = ArrayAdapter.createFromResource(context,R.array.state_list,android.R.layout.simple_spinner_item);   	
	state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        		

	userCountry.setAdapter(country);
	userState.setAdapter(state);
	// Setting up Form values
    if(getIntent()!=null){
	      //user = (User)getIntent().getExtras().getParcelable("User");
	      user = PocketTrader.user;
	      userFirstName.setText(user.getUserFirstName());
	      userLastName.setText(user.getUserLastName());
	      userEmail.setText(user.getUserEmail());
	      userCity.setText(user.getUserCity());
	      userPhone.setText(user.getUserPhone());
	      phoneOption.setChecked(user.getPhoneOption());
	      userCountry.setSelection(country.getPosition(user.getUserCountry()));
	      userState.setSelection(state.getPosition(user.getUserState()));
	    }	
	editProfile.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
		 if(validateForm()){	
			formData.putParcelable("Receiver", mReceiver);
			formData.putString("Key", user.getUserKey());
			formData.putString("FirstName",userFirstName.getText().toString());
			formData.putString("LastName", userLastName.getText().toString());
			formData.putString("Email", userEmail.getText().toString());
			formData.putString("Country", userCountry.getSelectedItem().toString());	
			formData.putString("City", userCity.getText().toString());
			formData.putString("Phone", userPhone.getText().toString());
			formData.putString("State", userState.getSelectedItem().toString());
			formData.putString("PhoneOption", String.valueOf(phoneOption.isChecked()));
			customDialog = new CustomDialog(context,"","","Updating..");
			dialog = customDialog.getWaitDialog("Please Wait",false);
			dialog.show();	
			doBindService();
		 }

		}
    	
    });
    cancelProfile.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AccountScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
    	
    });
	
   }
   private boolean validateForm(){
   	String uCity = userCity.getText().toString();
   	String uPhone = userPhone.getText().toString();
   	
   	// Validate rest
   	if(uCity == "" || uPhone == "" || uCity.length() < 3 || uPhone.length() < 10)
   	{
   		Toast.makeText(context, R.string.valid_missing_field, Toast.LENGTH_SHORT).show();
   		return false;
   	}
   	
   	
   	return true;
   }
   @Override
   public void onReceiveResult(int resultCode, Bundle resultData) {
	// TODO Auto-generated method stub
	if(resultCode == 0){
		updateStatus = resultData.getInt("Result");
		doUnbindService();
	}
   
   }
   private void refreshFormData(){
	      user = PocketTrader.user;
	      userFirstName.setText(user.getUserFirstName());
	      userLastName.setText(user.getUserLastName());
	      userEmail.setText(user.getUserEmail());
	      userCity.setText(user.getUserCity());
	      userPhone.setText(user.getUserPhone());	
	      phoneOption.setChecked(user.getPhoneOption());
   }
   // Method, Pushes Home Screen
   public void pushHomeScreen(View v){
	   Intent intent = new Intent(context,PocketTrader.class);
	   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	   startActivity(intent);
   }


}
