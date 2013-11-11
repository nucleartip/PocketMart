package com.techFist.sellMyStuff;



import com.google.ads.AdView;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.MessageHandler;
import com.techFist.sellMyStuff.Commons.PocketActivity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SellMainForm extends PocketActivity {

	private Context context;
    protected ArrayAdapter<CharSequence> productType;
    protected ArrayAdapter<CharSequence> productCategory;  
    private String userAddress = "";
    private Bundle formData;
    // Fetching Objects
    Button btnProceed;
    Spinner spnProductCategory;
    Spinner spnProductType;
    EditText productDescription;
    EditText productPrice;
    EditText productName;  
    // Spinner listener support class
    public class myOnItemSelectedListener implements OnItemSelectedListener {


        ArrayAdapter<CharSequence> mLocalAdapter;
        Context mLocalContext;
        Spinner localSpinner;
        protected int mPos;
        protected String mSelection;

 
        public myOnItemSelectedListener(Context c, ArrayAdapter<CharSequence> ad,Spinner s) {

          this.mLocalContext = c;
          //this.mLocalAdapter = ad;
          this.localSpinner = s;
        }


        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

            mPos = pos;
            mSelection = parent.getItemAtPosition(pos).toString();

            // Code for Manupulating Sub category
            Resources res =  getResources();
            int arrayID = getArrayID(res, pos);
            mLocalAdapter = ArrayAdapter.createFromResource(mLocalContext,arrayID,android.R.layout.simple_spinner_item);
            mLocalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		//Spinner subItem = (Spinner)findViewById(R.id.subCategorySpinner);
            localSpinner.setAdapter(mLocalAdapter);
        }
        private int getArrayID(Resources res, int index)   {
    	
        	String [] categoryArray = res.getStringArray(R.array.category);
        	String category = categoryArray[index].replaceAll(",","$");
        	category = category.replaceAll(" ", "_");
            int identifier = res.getIdentifier(
            		category,
        			"array",
        			"com.techFist.sellMyStuff");
     
        	return identifier;
     	   
        }

        public void onNothingSelected(AdapterView<?> parent) {

            // do nothing

        }
    }
    
    public void showMsgDialog(View v){
       	//Log.i////System.out.println("####", "Onclick");
    	MessageHandler.getInstance(context).showMyMessageList(context);
    }    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Register a Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,SellMainForm.class));
        
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.main_selling_form);
	 	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.common_header);
        
        
        context = this;
        formData = new Bundle();
        //Fetching previous form data
        if(getIntent() != null)
        	formData = getIntent().getExtras();
        
        // Fetching Objects
        Button btnProceed = (Button)findViewById(R.id.proceed_main_selling);
        spnProductCategory = (Spinner)findViewById(R.id.sell_item_category);
        spnProductType = (Spinner)findViewById(R.id.sell_item_sub_category);
        productDescription = (EditText)findViewById(R.id.sell_item_description);
        productPrice = (EditText)findViewById(R.id.sell_item_expected_price);
        productName = (EditText)findViewById(R.id.sell_item_name); 
        
        // Populating Spinners 
    	// Fetching Array
        this.productCategory = ArrayAdapter.createFromResource(context,R.array.category,android.R.layout.simple_spinner_item);   	
    	productCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
        this.productType = ArrayAdapter.createFromResource(context,R.array.Electronics,android.R.layout.simple_spinner_item);
        productType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
        // Updating spinner adapters
        spnProductCategory.setAdapter(productCategory);
    	spnProductType.setAdapter(productType);  
        // Handling, Product Type Spinner, when Category is changed
    	OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(context,productCategory,spnProductType);
    	spnProductCategory.setOnItemSelectedListener(spinnerListener);
    	
        // Proceeding to next Activity
        btnProceed.setOnClickListener(new OnClickListener(){
       	
			@Override
			public void onClick(View arg0) {
                
				if(validateForm()){
				  //Bundle data = new Bundle();
				  
					formData.putString("Category", (String)spnProductCategory.getSelectedItem());
					formData.putString("Type", (String)spnProductType.getSelectedItem());
					formData.putString("Description",productDescription.getText().toString() );
					formData.putString("Price", productPrice.getText().toString() );
					formData.putString("Address", userAddress);
					formData.putString("Name", productName.getText().toString());	
                    formData.putString("PostedByPhoneOption", String.valueOf(PocketTrader.user.getPhoneOption()));
				    //Intent intent = new Intent(context,SellCaptureImageForm.class);
				    // Calling review Screen Instead
				    Intent intent = new Intent(context,SellSubmitForm.class);
				    intent.putExtras(formData); 
				    startActivity(intent);
				}
				else{
					Toast.makeText(context,R.string.validation_message, Toast.LENGTH_LONG).show();
				}
			}
        	
        	
        });
    }
    // User Generated Methods
    boolean validateForm(){
    	
    	String category = (String)spnProductCategory.getSelectedItem();
    	String type = (String)spnProductType.getSelectedItem();
    	if(productDescription.getText().toString().length() > 0 && productPrice.getText().toString().length() > 0 
    	  && productName.getText().toString().length() > 0 && !category.equals(PocketTrader.PRODUCT_CATEGORY)
    	  && !type.equals(PocketTrader.PRODUCT_TYPE))
    	  return true;
    	else
    	  return false;
    }
    // Life Cycle Methods
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
        if(getIntent() != null)
        {
        	userAddress = getIntent().getExtras().getString("Address");
        }

    }
    // Method, Pushes Home Screen
    public void pushHomeScreen(View v){
 	   Intent intent = new Intent(context,PocketTrader.class);
 	   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	   startActivity(intent);
    } 
	 // Push My Profile Screen
	 public void pushMyProfile(View v){
			Intent intent = new Intent(context,MyProfileScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);		 
	 }    
	 
}
