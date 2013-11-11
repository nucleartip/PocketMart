package com.techFist.sellMyStuff.LocationProvider;

import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;

public class LocationProvider extends ContentProvider {

	// Private Instance members
    private List<Address> addr;
    private Geocoder geocoder;
    private Context context;	
    // Constants
    private static final String tag = "DynamicLocationProvider";
	public static String AUTHORITY = "com.techFist.sellMyStuff.LocationProvider.LocationProvider";
	private static final int SEARCH_SUGGEST = 0;
	private static final int SHORTCUT_REFRESH = 1;
	private static final UriMatcher sURIMatcher = buildUriMatcher();
	
	// Projection of table Data
	private static final String[] COLUMNS = {
		   "_id", // must include this column
		   SearchManager.SUGGEST_COLUMN_TEXT_1, // First Column, Displays Bold first line in Sugestion cell
		   SearchManager.SUGGEST_COLUMN_TEXT_2, // Second Column, Display secondry small text in Suggestion cell
		   SearchManager.SUGGEST_COLUMN_INTENT_DATA,// represent Data, associated with Intent action
		   SearchManager.SUGGEST_COLUMN_INTENT_ACTION,// Represent action need to be performed on click of cell
		   SearchManager.SUGGEST_COLUMN_SHORTCUT_ID // Shortcut ID
		};

		
	//Custom Methods
	
	// URI Matcher for incoming request
	private static UriMatcher buildUriMatcher()
     {
		UriMatcher matcher =
		new UriMatcher(UriMatcher.NO_MATCH);
		
		matcher.addURI(AUTHORITY,SearchManager.SUGGEST_URI_PATH_QUERY,SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY,SearchManager.SUGGEST_URI_PATH_QUERY +"/*",SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY,SearchManager.SUGGEST_URI_PATH_SHORTCUT,SHORTCUT_REFRESH);
		matcher.addURI(AUTHORITY,SearchManager.SUGGEST_URI_PATH_SHORTCUT +"/*",	SHORTCUT_REFRESH);
		return matcher;
    }
	// returns a Cursor with Rows as suggestions
	private Cursor getSuggestions(String query)
	 {
	    if (query == null) return null;
	       String location = query;
	    
	    MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor = generateLocationSuggestion(cursor,location);
	    return cursor;
	}
	// Formats the Address
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
	// Generates Suggestion based on query String and returns a Cursor
	public MatrixCursor generateLocationSuggestion(MatrixCursor cursor,String location)
	{
		
		try{
		  addr = geocoder.getFromLocationName(location,20);
		  long index  = 0;
		  for(Address addr1: addr){
			 String myLocation = formatAddress(addr1);
			 double latitude = addr1.getLatitude();
			 double longitude = addr1.getLongitude();
			 String cityName = addr1.getLocality();
			 String stateName = addr1.getAdminArea();
			 String countryName = addr1.getCountryName();
			 String area = addr1.getSubLocality();

		     int postalCode = -1;
             if(addr1.getPostalCode() != null && addr1.getPostalCode() != "")
                	postalCode = Integer.valueOf(addr1.getPostalCode());			 
			 
			 String intentData = myLocation + "#t" + String.valueOf(longitude) +"#t" +String.valueOf(latitude)+"#t"+cityName+
					 "#t"+stateName+"#t"+countryName+"#t"+area+"#t"+postalCode;
			 
			 cursor.addRow(columnValuesOfQuery(index,"android.intent.action.VIEW",intentData,myLocation,""));
			 index = index + 1;
		  }
		}
		catch(Exception e){
			
		}
		return cursor;
	}	
	// Returns a Row to be added into Cursor
	
	private Object[] columnValuesOfQuery(long index,String intentAction,String intentData,String primaryText,String secondryText)
	{	
			
			return new String[] {
			String.valueOf(index),// _id
			primaryText, // text1
			secondryText, // text2
			intentData,
			intentAction, //action
			SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT
			};
	}	
	
	// System generated methods
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		    case SEARCH_SUGGEST:
		      return SearchManager.SUGGEST_MIME_TYPE;
		    
		    case SHORTCUT_REFRESH:
		      return SearchManager.SHORTCUT_MIME_TYPE;
		    
		    default:
		      throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		String query = selectionArgs[0];
        context = this.getContext();
        addr = new ArrayList<Address>();
        geocoder = new Geocoder(context);
        
        
		switch (sURIMatcher.match(uri)) {
		  case SEARCH_SUGGEST:
		     Log.d(tag,"Search suggest called");
		     return getSuggestions(query);
		  case SHORTCUT_REFRESH:
		     Log.d(tag,"shortcut refresh called");
		     return null;
		  default:
		     throw
		     new IllegalArgumentException("Unknown URL " + uri);
		  }
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
