package com.techFist.sellMyStuff.LocationProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.SellStartForm;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.Details;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends ListActivity {

   // Private Instance Members
    private List<Address> addr;
    private Geocoder geocoder;
    private Context context;	
	private ArrayList<String> result;
	private String query = "";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.search);
	     //Register a Exception Handler
	     Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,SearchActivity.class));
	    
		result = new ArrayList<String>();
        context = this;
        addr = new ArrayList<Address>();
        geocoder = new Geocoder(this);
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      query = intent.getStringExtra(SearchManager.QUERY);
	      findMyLocation(query);
	    }
	    if(Intent.ACTION_VIEW.equals(intent.getAction())){
	    	doView(intent);
	    }
    }
	public void doView(Intent intent1){
		// Logic for Returning to calling screen
		Intent intent;
		if(PocketTrader.isSearchRequestedSS){
		  intent = new Intent(this,SellStartForm.class);
		}else{
			// Populating Details Object
			String[] data = intent1.getDataString().split("#t");
			Details detail = new Details(data[3],data[4],data[5], data[6],Float.valueOf(data[1]),Float.valueOf(data[2]),Integer.valueOf(data[7]),data[0]);
			PocketTrader.detail = detail; 
			intent = new Intent(this,PocketTrader.class);
		}


		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(intent1.getData());
        intent.putExtra("AddressValue", "Suggestion");
        startActivity(intent);
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		 ////System.out.println("##### New Intent Called");
	
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
          Intent intent = new Intent(this,SellStartForm.class);
	      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	      intent.putExtra("AddressValue", l.getItemAtPosition(position).toString());
		  startActivity(intent);	
	}
	private String formatAddress(Address address){
		 
		String data = "";
		int maxAddressLines = address.getMaxAddressLineIndex();
		for(int i=0;i<maxAddressLines;i++){
			if(i==0)
				data = address.getAddressLine(i);
			else
			data = data + "," + address.getAddressLine(i);
		}
		
	    return data;
	}
	public void findMyLocation(String query){
		
		try {
			addr = geocoder.getFromLocationName(query,20);
			for(Address location: addr){
				String myLocation = formatAddress(location);
				result.add(myLocation);
			}
			setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context,"Cannot determine location", Toast.LENGTH_LONG).show();
		}
				
	}	
	
}
