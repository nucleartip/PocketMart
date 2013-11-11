package com.techFist.sellMyStuff.Commons;



import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import com.techFist.sellMyStuff.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CustomDialog{

   String okMsg = "";
   String cancelMsg = "";
   String infoMsg = "";
   HttpClient PostClient;
   HttpPost httpPost;	
   HttpResponse resp;	
   Bundle formData;
   private static String SERVER    = "http://sms-techfist.appspot.com";	
   Context context;
   private static AsyncTask<Bundle,Integer,Integer> postMessage;	

    
   AlertDialog choiceDialog;
   
   public Context getContext(){
	   return this.context;
   }
   public static void cancelPostMessage(){
	   if(postMessage != null)
		   postMessage.cancel(true);
   }
   
   
   public CustomDialog(Context context,String okMsg,String cancelMsg,String infoMsg) 
        {    // TODO Auto-generated constructor stub
         this.infoMsg = infoMsg;
         this.cancelMsg = cancelMsg;
         this.okMsg = okMsg;
         this.context = context;
     	 this.httpPost = new HttpPost();
 		 this.PostClient = new DefaultHttpClient();
        }

   
   // Simple Dialog Box, with Details about App
   public AlertDialog getAboutAppDialog(final String title){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setTitle(title);
	   
	   dialog.setIcon(R.drawable.alert_info);

	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
		
		}});
	   return dialog.create();
   }   
   
   // Get Native Info Box, with Transparent Background
   // Title = Title to be displayed on Box
   // on Okay = Does Nothing
   
   public AlertDialog getTransparentInfoDialog(final String title){
	   ProgressDialog progressDialog;
	   progressDialog = new ProgressDialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
	   progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	   //progressDialog.setInverseBackgroundForced(true);
	   progressDialog.setCancelable(true);
	   return progressDialog;
  
   }
   
   // Get Native Info Dialog from Android
   // Title = Tile to be displayed on AlertBox
   // On Okay = Does Nothing, Disposes off
   
   public AlertDialog getInfoDialog(final String title){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setTitle(title);
	   
	   dialog.setIcon(R.drawable.alert_info);

	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
		
		}});
	   return dialog.create();
   }
   
   // Get Native Action Dialog from Android
   // Title = Tile to be displayed on AlertBox
   // Intent = Supplied Intent, which need to be Executed
   // On Okay = Executes the Intent.
   public AlertDialog getErrorDialogForAction(final String title,final Intent intent){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setTitle(title);
	   dialog.setIcon(R.drawable.alert_error);
	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
				context.startActivity(intent);
		}});
	   return dialog.create();
   } 
   // Get Native Action Dialog from Android
   // Title = Tile to be displayed on AlertBox
   // Intent = Supplied Intent, which need to be Executed
   // On Okay = Executes the Intent.
   public AlertDialog getInfoDialogForAction(final String title,final Intent intent){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setTitle(title);
	   dialog.setIcon(R.drawable.alert_info);
	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
				context.startActivity(intent);
		}});
	   return dialog.create();
   }   
   
   // Get Native Confirmation Action Dialog from Android
   // Title = Tile to be displayed on AlertBox
   // Intent = Supplied Intent, which need to be Executed
   // On Okay = Executes the Intent.
   // On Cancel = Does nothing, Disposes Off
   public AlertDialog getConfirmationDialogForAction(final String title,final Intent intent,OnClickListener listen){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setTitle(title);
	   dialog.setIcon(R.drawable.alert_info);
	   
	   dialog.setNegativeButton(cancelMsg, listen);
	   /*
	   dialog.setNegativeButton(cancelMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		  
	   });*/
	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
				context.startActivity(intent);
		}});
	   return dialog.create();
   }    
   
   // Get Native Error Dialog from Android
   // Title = Tile to be displayed on AlertBox
   // Action = Action String for Setting Activity to be opened
   // On Okay = Does Nothing Disposes off

   public AlertDialog getErrorDialog(final String title){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setIcon(R.drawable.alert_error);
	   dialog.setTitle(title);
	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub

			
		}});
	   return dialog.create();
   }
   
   // Get Native Settings Dialog from Anroid
   // Title = Tile to be displayed on AlertBox
   // Action = Action String for Setting Activity to be opened
   // On Okay = Takes user to respective setting screen
   // On Cancel = Does Nothing,Disposes Off
   
   public AlertDialog getSettingDialog(final String action,final String title){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setIcon(R.drawable.alert_info);
	   dialog.setTitle(title);
	   dialog.setIcon(R.drawable.alert_info);
	   dialog.setNegativeButton(cancelMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}});
	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			context.startActivity(new Intent(action));
			
		}});
	   return dialog.create();
   }

   // Get Native Settings Dialog with Exit from Anroid
   // Title = Tile to be displayed on AlertBox
   // Action = Action String for Setting Activity to be opened
   // On Okay = Takes user to respective setting screen
   // On Cancel = Does Nothing,Disposes Off
   
   
   public AlertDialog getSettingWithExitDialog(final String action,final String title,OnClickListener negative){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setIcon(R.drawable.alert_info);
	   dialog.setTitle(title);
	   dialog.setIcon(R.drawable.alert_info);
	   dialog.setNegativeButton(cancelMsg, negative);
	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			context.startActivity(new Intent(action));
			
		}});
	   return dialog.create();
   }
   
   // Get AlertBox with a Spinner
   // Title = Tile to be displayed on AlertBox
   // No Buttons, Dispose Manually
   public AlertDialog getWaitDialog(final String title,boolean cancelable){
	   
	   ProgressDialog progressDialog;
	   progressDialog = new ProgressDialog(context);
	   progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	   progressDialog.setMessage(infoMsg);
	   progressDialog.setCancelable(cancelable);
	   progressDialog.setTitle(title);
	   return progressDialog;
   }
   
   // Confirmation Dialog with Custom Listeners both Negative and Positive Button
   public AlertDialog getConfirmationDialogForActionCustomListeners(final String title,OnClickListener positive,OnClickListener negative){
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setTitle(title);
	   dialog.setIcon(R.drawable.alert_info);	   
	   dialog.setPositiveButton(okMsg, positive);
	   dialog.setNegativeButton(cancelMsg, negative);
	   return dialog.create();
   }
   // Confirmation Dialog with Custom Listeners with Positive Button
   public AlertDialog getConfirmationPositiveDialogForActionCustomListeners(final String title,OnClickListener positive){
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setTitle(title);
	   dialog.setIcon(R.drawable.alert_info);	   
	   dialog.setPositiveButton(okMsg, positive);
	   return dialog.create();
   }   
   // Email Dialog, It Pops up a Dialog box with option to send email
   public AlertDialog getSendEmailDialog(String title,final String userName, final String emailId,final Context context){
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
       dialog.setTitle(title);
       dialog.setIcon(R.drawable.detail_email);
       dialog.setCancelable(true);
   	   
       LayoutInflater li = LayoutInflater.from(context);
       final View bidView = li.inflate(R.layout.email_dialog_form, null);
       dialog.setView(bidView); 
       dialog.setNegativeButton("Send", new OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			EditText emailBody = (EditText)bidView.findViewById(R.id.email_body);
			EditText emailSubject = (EditText)bidView.findViewById(R.id.email_subject);
			
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("message/rfc822");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailId});
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,emailSubject.getText().toString());
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,emailBody.getText().toString());
			context.startActivity(Intent.createChooser(emailIntent, "Email to " + userName ));
			
		}});
       return dialog.create();
   }
  // Contacts Preference Dialog, Ask user To Select Either Phone or Email
   public AlertDialog getContactTypeChooserDialog(String title,final String userName,final String emailId,
	   final String phoneNumber,final boolean phoneOption){
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
       dialog.setIcon(R.drawable.alert_info);
       dialog.setTitle(title);
       dialog.setCancelable(true);
       CharSequence[] temp = {""} ;
       CharSequence[] temp1 = {"",""};
       
       if(phoneOption)
       {
    	   temp1[0] = "Email";
    	   temp1[1] = "Phone";
       }else{
    	   temp[0] = "Email";
       }

       final CharSequence[]  choiceList = (phoneOption)?temp1:temp;

       OnClickListener choose = new OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int pos) {
          

		       
			if(choiceList[pos].equals("Email")){
				//getSendEmailDialog("Send Email",userName,emailId,context).show();
				showEmailDialog("Send Email",userName,emailId,phoneNumber,context);
			}
			if(choiceList[pos].equals("Phone")){
				showCallPhoneDialog(phoneNumber,context);
			}
       }
	  };

       dialog.setSingleChoiceItems(choiceList,-1,choose);
       
       choiceDialog = dialog.create();
       return choiceDialog;
   }
   private void showEmailDialog(String title,final String userName,final String emailId,final String phoneNumber,Context context){
	  choiceDialog.cancel();
	  AlertDialog dialog = getSendEmailDialog(title,userName,emailId,context);
	  dialog.show();
   }
   public void showCallPhoneDialog(String phoneNumber,Context context){
	    choiceDialog.cancel();
	    String url = "tel:"+phoneNumber;
	    Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse(url));
	    intent.putExtra(android.content.Intent.EXTRA_PHONE_NUMBER, phoneNumber);
	    context.startActivity(intent);
	    
   }

   
   // Posting Personal Messages..
   public AlertDialog getPostMessageDialog(final Bundle b,final Context context,String title){
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
       dialog.setTitle(title);
       dialog.setIcon(R.drawable.detail_email);
       dialog.setCancelable(true);
       
       LayoutInflater li = LayoutInflater.from(context);
       final View bidView = li.inflate(R.layout.msg_dialog_form, null);
       dialog.setView(bidView);
       
       
       dialog.setPositiveButton(this.okMsg, new OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			EditText emailBody = (EditText)bidView.findViewById(R.id.msg_body);
			EditText emailSubject = (EditText)bidView.findViewById(R.id.msg_subject);
            b.putString("Message",emailBody.getText().toString());
            b.putString("Subject", emailSubject.getText().toString());
            //MessageHandler handler = MessageHandler.getInstance(context);
            //handler.postMessage(b);
            Toast.makeText(context,"Sending Message!", Toast.LENGTH_SHORT).show();
			
		}});
       dialog.setNegativeButton(this.cancelMsg, new OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub

			
		}});       
	   
       
	   return dialog.create();
   }
   
   // Post a Message

      
   
   // End - Posting Message
   @Deprecated
   public AlertDialog getSettingDialog(final String action){
	   
	   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	   dialog.setCancelable(true);
	   dialog.setMessage(infoMsg);
	   dialog.setIcon(R.drawable.alert_info);
	   
	   dialog.setNegativeButton(cancelMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}});
	   dialog.setPositiveButton(okMsg, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			context.startActivity(new Intent(action));
			
		}});
	   return dialog.create();
   }

}
