package com.techFist.sellMyStuff.Commons;

import android.content.Context;

import com.paypal.android.MEP.PayPal;

public class PayPalInstance {

    private static PayPal ppObj;
	private Context context;  
	
    public PayPalInstance(Context context){
    	this.context = context;
    }
    public static PayPal getInstance(Context context){
		 
		 if(ppObj != null){
			 //ppObj.setContext(context);
			 return ppObj;
		 }else{
			 ppObj = PayPal.initWithAppID(context, "APP-80W284485P519543T", PayPal.ENV_SANDBOX);;
			 return ppObj;
		 }
		 
	 }	
}
