package com.techFist.sellMyStuff.MapManager;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.techFist.sellMyStuff.LoginScreen;
import com.techFist.sellMyStuff.PocketTrader;
import com.techFist.sellMyStuff.R;
import com.techFist.sellMyStuff.AccountManager.MyProfileScreen;
import com.techFist.sellMyStuff.AccountManager.PocketHandler;
import com.techFist.sellMyStuff.Commons.Product;
public class BusinessMap extends MapActivity{

    String TAG = "#######";
    private MapView currentMap;
    private MapController currentMapController;
	private Bundle formData;
	private Context context;
    private Drawable drawable;
    private Product product;
    private ArrayList<Product> productList;
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    protected void onCreate(Bundle icicle) {
	  // TODO Auto-generated method stub
	  super.onCreate(icicle);
      requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
      setContentView(R.layout.business_map);
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_search_header);
      //Register a Exception Handler
      Thread.setDefaultUncaughtExceptionHandler(new PocketHandler(this,BusinessMap.class));
	  
      context = this;
      //setContentView(R.layout.business_map);
      // Initializing Objects
      formData = new Bundle();
      currentMap = (MapView)findViewById(R.id.businees_map);
      currentMapController = currentMap.getController();
      drawable = this.getResources().getDrawable(R.drawable.marker_default);
      if(getIntent()!=null)
        formData = getIntent().getExtras();
      product = (Product)formData.getParcelable("ProductDetail");
      final List<Overlay> mOverlays = currentMap.getOverlays();
      productList = new ArrayList<Product>();
      productList.add(product);
      final BusinessOverlays itemizedoverlay = new BusinessOverlays(drawable,this,productList);
      //currentMapOverlay = new MyLocationOverlay(this, currentMap);
      
      GeoPoint point = new GeoPoint(formData.getInt("IntLatitude"),formData.getInt("IntLongitude"));
      OverlayItem overlayitem = new OverlayItem(point, "Woo hooo", "My product is on Sale");
      itemizedoverlay.addOverlay(overlayitem);
      mOverlays.add(itemizedoverlay);
      
      // Setting up Map attributes
      currentMapController.animateTo(point);
      currentMapController.setCenter(point);
      currentMapController.setZoom(15);
      currentMap.setBuiltInZoomControls(true);
      currentMap.setClickable(true);
      currentMap.setEnabled(true);
      currentMap.setSatellite(false);
      currentMap.setTraffic(false);
      currentMap.setStreetView(false);
      
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
