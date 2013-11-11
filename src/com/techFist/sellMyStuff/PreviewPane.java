package com.techFist.sellMyStuff;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class PreviewPane extends SurfaceView  implements SurfaceHolder.Callback{
    private Camera camera;
    private SurfaceHolder sHolder;
    private Context context;
    public PreviewPane(Context context,AttributeSet set){
    	super(context,set);
    }
    
	public void setPreviewPane(Context context, Camera camera) {
	
		this.context = context;
		this.camera = camera;
		sHolder = getHolder();
		sHolder.addCallback(this);
		sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		// Code for handling Mode Change..e.g when changed from layout to potrait and vice versa
		if (sHolder == null){
			return;
			
		}
		try {
			camera.stopPreview();
			camera.setPreviewDisplay(sHolder);
			camera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context,"Camera Cannot be Started at this Moment", Toast.LENGTH_LONG);
		}
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			camera.setPreviewDisplay(sHolder);
			camera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context,"Camera Cannot be Started at this Moment", Toast.LENGTH_LONG);
		}
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	camera.release();	
	}


}