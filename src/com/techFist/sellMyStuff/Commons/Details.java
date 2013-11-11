package com.techFist.sellMyStuff.Commons;

public class Details {

	private String city;
	private String area;
	private String state;
	private String country;
	private float longitude;
	private float latitude;
	private int postalCode;
	private String address;
	
	public String getArea() {
		return area;
	}
    public void setpostalCode(int postalCode){
       this.postalCode = postalCode;
    }
    
	public void setArea(String area) {
		this.area = area;
	}

	public float getLongitude() {
		return longitude;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}


	
	
	public Details(String city,String state,String country,String area,float longitude,float latitude,int postalCode,String address){
		this.city = city;
		this.state = state;
		this.country = country;
		this.area = area;
		this.longitude = longitude;
		this.latitude = latitude;
		this.postalCode = postalCode;
	    this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}
    public int getPostalCode(){
    	return postalCode;
    }
	public void setCountry(String country) {
		this.country = country;
	}
	
}
