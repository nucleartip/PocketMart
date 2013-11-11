package com.techFist.sellMyStuff.Commons;

public class User {

	private String userName;
	private String userCountry;
	private String userCity;
	private String userEmail;
	private String userPhone;
	private String userFirstName;
	private String userLastName;
	private String userKey;
	private String userState;
	private boolean phoneCall;
	
	public boolean getPhoneOption() {
		return phoneCall;
	}

	public void setPhoneOption(boolean phoneCall) {
		this.phoneCall = phoneCall;
	}
    public User(){
    	
    }
	public User(String name,String country,String city,String email,String phone,String firstName,String lastName,
			    String key,String state,boolean phoneCall){
		this.userName = name;
		this.userCity = city;
		this.userCountry = country;
		this.userEmail = email;
		this.userPhone = phone;
		this.userFirstName = firstName;
		this.userLastName = lastName;
		this.userKey = key;
		this.userState = state;
		
		this.phoneCall = phoneCall;
	}

	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}

	public String getUserCity() {
		return userCity;
	}

	public void setUserCity(String userCity) {
		this.userCity = userCity;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
}
