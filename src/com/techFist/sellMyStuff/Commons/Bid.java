package com.techFist.sellMyStuff.Commons;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Bid implements Parcelable,Comparable<Bid>{

	private String quote;
	private String postedBy;
	private String email;
	private String postedDate;
	private String postedByPhone;
	private String productName;
	private String key;
	private boolean postedByPhoneOption = false;
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}


	



	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public static final Parcelable.Creator<Bid> CREATOR = new Parcelable.Creator<Bid>(){

		@Override
		public Bid createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Bid(source);
		}

		@Override
		public Bid[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Bid[size];
		}
		 
	 };
	 
	 
	public Bid(String quote,String postedBy,String email,String postedDate,String postedByPhone,boolean postedByPhoneOption,
			String productName,String key){
		this.quote = quote;
		this.email = email;
		this.postedBy = postedBy;
		this.postedDate = postedDate;
		this.postedByPhone = postedByPhone;
		this.postedByPhoneOption = postedByPhoneOption;
		this.productName = productName;
	    this.key = key;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}
	public String getPostedByPhone() {
		return postedByPhone;
	}

	public void setPostedByPhone(String postedByPhone) {
		this.postedByPhone = postedByPhone;
	}

	public boolean isPostedByPhoneOption() {
		return postedByPhoneOption;
	}

	public void setPostedByPhoneOption(boolean postedByPhoneOption) {
		this.postedByPhoneOption = postedByPhoneOption;
	}
    // Parcelable Function
 	private void readFromParcel(Parcel in) {
		 
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
 		quote = in.readString();
 		email = in.readString();
 		postedBy = in.readString();
 		postedDate = in.readString();
		postedByPhone = in.readString();
	    postedByPhoneOption = Boolean.valueOf(in.readString());
	    productName = in.readString();
	    key = in.readString(); 		
	}	
	private Bid(Parcel in) {
   	  readFromParcel(in);
    }	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(quote);
		dest.writeString(email);
		dest.writeString(postedBy);
		dest.writeString(postedDate);
		dest.writeString(postedByPhone);
		dest.writeString(String.valueOf(postedByPhoneOption));
		dest.writeString(productName);
		dest.writeString(key);
	}

	@Override
	public int compareTo(Bid another) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
	    long date1 = new Date(this.postedDate).getTime();
	    long date2 = new Date(another.getPostedDate()).getTime();
	    
	    if(date1 > date2)
	      return BEFORE;
	    if(date1 == date2)
	      return EQUAL;
	    else
	      return AFTER;
	}
	
}
