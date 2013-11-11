package com.techFist.sellMyStuff.AccountManager;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.Commons.CustomDialog;
import com.techFist.sellMyStuff.Commons.Details;
import com.techFist.sellMyStuff.Commons.User;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PocketHandler implements
java.lang.Thread.UncaughtExceptionHandler{
	// Shared references values
	private final static String USER_KEY = "user_data";
	private final static String DETAIL_KEY = "detail_data";
	private final static String userName = "user_name";
	private final static String userCountry = "user_country";
	private final static String userCity = "user_city";
	private final static String userEmail = "user_email";
	private final static String userPhone = "user_phone";
	private final static String userFirstName = "first_name";
	private final static String userLastName = "last_name";
	private final static String userKey = "user_key";
	private final static String userState = "user_state";
	private final static String phoneCall = "phone_call";
	

	private static String detailCity = "detail_city";
	private static String detailArea = "detail_area";
	private static String detailState = "detail_state";
	private static String detailCountry = "detail_country";
	private static String detailLongitude = "detail_longitude";
	private static String detailLatitude = "detail_latitude";
	private static String detailPostalCode = "detail_postalcode";
	private static String detailAddress = "detail_address";	
	
	
	
	
	
    private final  Context myContext;
    private User user;
    private Details detail;
    private CustomDialog dialog;
    private Class c;
    public PocketHandler(Context context,Class<?> c) {
       this.c = c;
       myContext = context;
       dialog = new CustomDialog(myContext,"Okay","Exit","Recovered from Unexpected error, we have sent message to admin");
    }

    public void uncaughtException(Thread thread, Throwable exception) {
    	
    	if(PocketTrader.user != null){
    	  saveCriticalData();
    	  StringWriter stackTrace = new StringWriter();
          exception.printStackTrace(new PrintWriter(stackTrace));
          System.err.println(stackTrace);
          //Restarting App
          Intent intent = new Intent(myContext, PocketTrader.class);
          String s = stackTrace.toString();
          //you can use this String to know what caused the exception and in which Activity
        
          intent.putExtra(PocketTrader.CLASS_NAME, c.getName());
          intent.putExtra(PocketTrader.STACK_TRACE, s);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          myContext.startActivity(intent);
          //for restarting the Activity
          Process.killProcess(Process.myPid());
          //System.exit(0);
    	}
    	else{

      	    StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));
            System.err.println(stackTrace);
            //Restarting App
            Intent intent = new Intent(myContext, LoginScreen.class);
            String s = stackTrace.toString();
            //you can use this String to know what caused the exception and in which Activity
          
            intent.putExtra(PocketTrader.CLASS_NAME, c.getName());
            intent.putExtra(PocketTrader.STACK_TRACE, s);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //rmyContext.startActivity(intent);
            //for restarting the Activity
            Process.killProcess(Process.myPid());
            //System.exit(0);    		
    	}
    }
    

    
    private void saveCriticalData(){
      
    	
    	Editor userEditor = myContext.getSharedPreferences(USER_KEY, Context.MODE_PRIVATE).edit();
    	Editor DetailEditor = myContext.getSharedPreferences(DETAIL_KEY, Context.MODE_PRIVATE).edit();
        // User Detail    	
    	userEditor.putString(userName, PocketTrader.user.getUserName() );
    	userEditor.putString(userCountry, PocketTrader.user.getUserCountry());
    	userEditor.putString(userCity, PocketTrader.user.getUserCity());
    	userEditor.putString(userFirstName, PocketTrader.user.getUserFirstName());
    	userEditor.putString(userLastName, PocketTrader.user.getUserLastName());
    	userEditor.putString(userEmail,  PocketTrader.user.getUserEmail());
    	userEditor.putString(userPhone, PocketTrader.user.getUserPhone());
    	userEditor.putString(userKey,PocketTrader.user.getUserKey());
    	userEditor.putString(userState,PocketTrader.user.getUserState());
    	userEditor.putString(phoneCall,PocketTrader.user.getUserPhone());
    	userEditor.commit();    	
        // Location Detail
    	DetailEditor.putString(detailCity, PocketTrader.detail.getCity());
    	DetailEditor.putString(detailArea, PocketTrader.detail.getArea());
    	DetailEditor.putString(detailState, PocketTrader.detail.getState());
    	DetailEditor.putString(detailCountry, PocketTrader.detail.getCountry());
    	DetailEditor.putFloat(detailLongitude, PocketTrader.detail.getLongitude());
    	DetailEditor.putFloat(detailLatitude,  PocketTrader.detail.getLatitude());
    	DetailEditor.putInt(detailPostalCode, PocketTrader.detail.getPostalCode());
    	DetailEditor.putString(detailAddress,PocketTrader.detail.getAddress());
    	DetailEditor.commit();
    	
    	//user = PocketTrader.user;
        //detail = PocketTrader.detail;
    }
    
}
