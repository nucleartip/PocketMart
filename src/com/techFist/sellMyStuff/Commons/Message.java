package com.techFist.sellMyStuff.Commons;

import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Comparable<Message>,Parcelable{

	private String message;
	private String emailTo;
	private String emailFrom;
	private String fromPhone;
	private boolean fromPhoneOption;
	private String messageState;
	private boolean messageRead;
	private String key;
	private String subject;
	private String postedFrom;
	private String postedDate;
	
	 public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>(){

		@Override
		public Message createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Message(source);
		}

		@Override
		public Message[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Message[size];
		}
		 
	 };		
     private Message(Parcel in) {
    	 readFromParcel(in);
     }	
  	private void readFromParcel(Parcel in) {
		 
 		// We just need to read back each
 		// field in the order that it was
 		// written to the parcel
  		message = in.readString();
  		emailTo = in.readString();
  		emailFrom = in.readString();
  		fromPhone = in.readString();
  		fromPhoneOption =Boolean.valueOf(in.readString());
  		messageState = in.readString();
  		messageRead = Boolean.valueOf(in.readString());
  		key = in.readString();
  		subject = in.readString();
  		postedFrom = in.readString();
  		postedDate = in.readString();
 	     
 	}

 	@Override
  	public void writeToParcel(Parcel arg0, int arg1) {
  		// TODO Auto-generated method stub
  		arg0.writeString(message);
  		arg0.writeString(emailTo);
  		arg0.writeString(emailFrom);
  		arg0.writeString(fromPhone);
  		arg0.writeString(String.valueOf(fromPhoneOption));
  		arg0.writeString(messageState);
  		arg0.writeString(String.valueOf(messageRead));
  		arg0.writeString(key);
  		arg0.writeString(subject);
  		arg0.writeString(postedFrom);
  		arg0.writeString(postedDate);
  		
  	}     
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}     
	public Message(String key,String message,String emailTo,String emailFrom,String fromPhone,String fromPhoneOption,String messageState,
			       String messageRead,String subject,String postedFrom,String postedDate){
		this.key = key;
		this.message = message;
		this.emailTo = emailTo;
		this.emailFrom = emailFrom;
		this.fromPhone = fromPhone;
		boolean po = Boolean.getBoolean(fromPhoneOption);
		this.fromPhoneOption = po;
		this.messageState = messageState;
		boolean mr = Boolean.getBoolean(messageRead);
		this.messageRead = mr;
		this.subject = subject;
		this.postedFrom = postedFrom;
		this.postedDate = postedDate;
		
	}
	
	public String getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}

	public String getPostedFrom() {
		return postedFrom;
	}

	public void setPostedFrom(String postedFrom) {
		this.postedFrom = postedFrom;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	public String getFromPhone() {
		return fromPhone;
	}
	public void setFromPhone(String fromPhone) {
		this.fromPhone = fromPhone;
	}
	public boolean isFromPhoneOption() {
		return fromPhoneOption;
	}
	public void setFromPhoneOption(boolean fromPhoneOption) {
		this.fromPhoneOption = fromPhoneOption;
	}
	public String getMessageState() {
		return messageState;
	}
	public void setMessageState(String messageState) {
		this.messageState = messageState;
	}
	public boolean isMessageRead() {
		return messageRead;
	}
	public void setMessageRead(boolean messageRead) {
		this.messageRead = messageRead;
	}

	@Override
	public int compareTo(Message another) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
	    long date1 = new Date(this.postedDate).getTime();
	    long date2 = new Date(another.getPostedDate()).getTime();
	    
	    if(date1 > date2)
	      return AFTER;
	    if(date1 == date2)
	      return EQUAL;
	    else
	      return BEFORE;

	}
	
	
}
