package com.techFist.sellMyStuff.Commons;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable, Comparable<Product>{
	 private String productName;
	 private String productPrice;
	 private String productPostedDate;
	 private String productUrl;
	 private String productType;
	 private String productCategory;
	 private String productDescription;
	 private String productLocation;
	 private String productLongitude;
	 private String productLatitude;
	 private String parentKey;
	 private String ownerID;
	 private Date date;
	 private String productCity;
	 private String productState;
	 private String productCountry;
	 private String ownerNumber;
	 private boolean postedByPhoneOption = false;
	 private ArrayList<String> productImages = new ArrayList<String>();
	 private boolean isImageAvailable;
	 public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>(){

		@Override
		public Product createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Product(source);
		}

		@Override
		public Product[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Product[size];
		}
		 
	 };
     private Product(Parcel in) {
    	 readFromParcel(in);
     }	 
 	private void readFromParcel(Parcel in) {
 		 
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		productName = in.readString();
		productPrice = in.readString();
		productPostedDate = in.readString();
		productCategory = in.readString();
		productType = in.readString();
		productDescription = in.readString();
		productLocation = in.readString();
		productUrl = in.readString();
		productLongitude = in.readString();
		productLatitude = in.readString();
		parentKey = in.readString();
		ownerID = in.readString();
		productCity = in.readString();
		productState = in.readString();
		productCountry = in.readString();
		this.ownerNumber = in.readString();	
		postedByPhoneOption = Boolean.valueOf(in.readString());
		this.date = new Date(productPostedDate);
	     // Generating Product URL's
	     StringTokenizer tkn = new StringTokenizer(productUrl,"#");
	     while(tkn.hasMoreTokens()){
	    	 String url = tkn.nextToken().trim();
	    	 if(!url.equals("") && !url.equals(null)){
	    	    this.productImages.add(url);
	    	    isImageAvailable = true;
	    	 } 
	     }
 	     ////System.out.println("####### Image available: " + isImageAvailable);
	     
	}

	@Override
 	public void writeToParcel(Parcel arg0, int arg1) {
 		// TODO Auto-generated method stub
 		arg0.writeString(productName);
 		arg0.writeString(productPrice);
 		arg0.writeString(productPostedDate);
 		arg0.writeString(productCategory);
 		arg0.writeString(productType);
 		arg0.writeString(productDescription);
 		arg0.writeString(productLocation);
 		arg0.writeString(productUrl);
 		arg0.writeString(productLongitude);
 		arg0.writeString(productLatitude);
 		arg0.writeString(parentKey);
 		arg0.writeString(ownerID);
 		arg0.writeString(productCity);
 		arg0.writeString(productState);
 		arg0.writeString(productCountry);
 		arg0.writeString(ownerNumber);
  		arg0.writeString(String.valueOf(postedByPhoneOption));
 		
 	}

     public Product(String name,String price,String date,String URL,String type,String category,String description,
    		 String location,String longitude,String latitude,
		        String key,String ownerID,String city,String state,String country,String number,boolean postedByPhoneOption){
	 this.productName = name;
	 this.productPrice = price;
	 this.productPostedDate = date;
	 this.productUrl = URL;
	 this.productType = type;
	 this.productCategory = category;
	 this.productDescription = description;
	 this.productLocation = location;
	 this.productLongitude = longitude;
	 this.productLatitude = latitude;
     this.parentKey = key;
     this.ownerID = ownerID;
     this.productCity = city;
     this.productState = state;
     this.productCountry = country;
     this.date = new Date(date);
     this.ownerNumber = number;
     this.postedByPhoneOption = postedByPhoneOption;
     // Generating Product URL's
     StringTokenizer tkn = new StringTokenizer(productUrl,"#");
     while(tkn.hasMoreTokens()){
    	 String url = tkn.nextToken().trim();
    	 if(!url.equals("") && !url.equals(null)){
    	  this.productImages.add(url);
    	    isImageAvailable = true;
    	 }
      }	
	     ////System.out.println("####### Image available: " + isImageAvailable);
     
   }  
     
     
     
  	public boolean isImageAvailable() {
		return isImageAvailable;
	}
	public void setImageAvailable(boolean isImageAvailable) {
		this.isImageAvailable = isImageAvailable;
	}
	public String getOwnerNumber() {
 		return ownerNumber;
 	}
 	public void setOwnerNumber(String ownerNumber) {
 		this.ownerNumber = ownerNumber;
 	}     
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getProductCity() {
		return productCity;
	}
	public void setProductCity(String productCity) {
		this.productCity = productCity;
	}
	public String getProductState() {
		return productState;
	}
	public void setProductState(String productState) {
		this.productState = productState;
	}
	public String getProductCountry() {
		return productCountry;
	}
	public void setProductCountry(String productCountry) {
		this.productCountry = productCountry;
	}
	public String getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}
	public String getParentKey() {
		return parentKey;
	}
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductPostedDate() {
		String date = ""; 
		date = productPostedDate.split("UTC")[0];
		return date;
	}
	public void setProductPostedDate(String productPostedDate) {
		this.productPostedDate = productPostedDate;
	}

	public ArrayList<String> getProductImages() {
		return productImages;
	}
	public void setProductImages(ArrayList<String> productImages) {
		this.productImages = productImages;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	public String getProductLocation() {
		return productLocation;
	}
	public void setProductLocation(String productLocation) {
		this.productLocation = productLocation;
	}
	
	public String getProductLongitude() {
		return productLongitude;
	}
	public void setProductLongitude(String productLongitude) {
		this.productLongitude = productLongitude;
	}
	public String getProductLatitude() {
		return productLatitude;
	}
	public void setProductLatitude(String productLatitude) {
		this.productLatitude = productLatitude;
	}
	
	public boolean getPostedByPhoneOption() {
		return postedByPhoneOption;
	}
	public void setPostedByPhoneOption(boolean postedByPhoneOption) {
		this.postedByPhoneOption = postedByPhoneOption;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
    @Override
    public int compareTo(Product another) {
	  final int BEFORE = -1;
	  final int EQUAL = 0;
	  final int AFTER = 1;
	  long date1 = new Date(this.date.getTime()).getTime();
	  long date2 = new Date(another.getDate().getTime()).getTime();
		    
	  if(date1 > date2)
		 return BEFORE;
      if(date1 == date2)
		 return EQUAL;
	  else
		 return AFTER;
   }
	

	 
}
