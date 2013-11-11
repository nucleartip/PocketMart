package com.techFist.sellMyStuff.GraphicsManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ImageUploader extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	   // TODO Auto-generated method stub
	   return super.onStartCommand(intent, flags, startId);
	   
    }
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		 ////System.out.println("####### Image Upload Service is Started");
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
