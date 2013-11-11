package com.techFist.sellMyStuff.Commons;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;
import com.techFist.sellMyStuff.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public abstract class PocketActivity extends Activity implements AdListener{
	
	public AdView adView;
	
	public PocketActivity(){

	}
	
	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub
		showCommonFooters();
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		// TODO Auto-generated method stub
		showAdViews();
	}
	

	public void showCommonFooters() {
		 // TODO Auto-generated method stub
		 // Code to enable and diasble common footer view
		 TextView fView = (TextView)findViewById(R.id.footerTextView);
		 fView.setVisibility(View.VISIBLE);
		 fView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		 adView.setVisibility(View.INVISIBLE);
	}

	public void showAdViews(){
		 TextView fView = (TextView)findViewById(R.id.footerTextView);
		 fView.setVisibility(View.INVISIBLE);
		 fView.setLayoutParams(new LayoutParams(0,0));
		 adView.setVisibility(View.VISIBLE);    	
	}
}
