package com.baunAndroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	private static final String TAG = "Compass";
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SampleView mView;
	private float[] mValues;
	private double lonMosque;
	private double latMosque;
	private LocationManager lm;
	private LocationListener loclistenD;
	//for find north direction
	private final SensorEventListener mListener = new SensorEventListener(){
		public void onSensorChanged(SensorEvent event){
			if (Config.DEBUG) Log.d(TAG, 
					"sensorChanged (" +event.values[0] +","+ event.values[1]+", "+ event.values[2]+")");
			mValues = event.values;
			if (mView != null){
				mView.invalidate();
			}
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy){
			
		}
	};
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mView = new SampleView(this);
        setContentView(mView);
        //calling gps
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location loc= lm.getLastKnownLocation("gps");
        //ask the location manager to send us location updates.
        loclistenD = new DispLocListener();
        lm.requestLocationUpdates("gps",30000l,10.0f,loclistenD);
        loclistenD = new DispLocListener();
        lm.requestLocationUpdates("gps",30000l,10.0f,loclistenD);
    }
    //finding ka'bah location
    
    private double QiblaCount(double lngMasjid,double latMasjid){
    	double lngKabah= 39.82616111;
    	double latKabah= 21.42250833;
    	double lKlM= (lngKabah - lngMasjid);
    	double sinLKLM= Math.sin(lKlM*2.0*Math.PI/360);
    	double cosLKLM= Math.cos(lKlM*2.0*Math.PI/360);
    	double sinLM = Math.sin(latMasjid*2.0*Math.PI/360);
    	double cosLM = Math.cos(latMasjid*2.0*Math.PI/360);
    	double tanLK = Math.tan(latKabah*2*Math.PI/360);
    	double denominator = (cosLM*tanLK)-sinLM*cosLKLM;
    	double Qibla;
    	double direction;
    	Qibla = Math.atan2(sinLKLM, denominator)*180/Math.PI;
    	direction = Qibla < 0 ? Qibla+360 : Qibla;
    	return direction;
    }
    //resume location update when we are resume
    @Override
    protected void onResume(){
    	if (Config.DEBUG) Log.d(TAG,"onResume");
    	super.onResume();
    	mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onStop(){
    	if (Config.DEBUG) Log.d(TAG, "onStop");
    	super.onStop();
    }
    private class SampleView extends View {
    	private Paint mPaint = new Paint();
    	private Path mPath = new Path();
    	private boolean mAnimate;
		public SampleView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			mPath.moveTo(0, -50);
			mPath.lineTo(20, 60);
			mPath.lineTo(0, 50);
			mPath.lineTo(-20, 60);
			mPath.close();
		}
		//Make arrow for pointing direction
		@Override
		protected void onDraw(Canvas canvas){
			Paint paint = mPaint;
			canvas.drawColor(Color.WHITE);
			paint.setAntiAlias(true);
			paint.setColor(Color.DKGRAY);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			
			int w = canvas.getWidth();
			int h = canvas.getHeight();
			int cx = w/2;
			int cy = h/2;
			float Qibla = (float) QiblaCount(lonMosque,latMosque);
			// float Qibla = mValues[0] + Qibla;
			canvas.translate(cx, cy);
			if (mValues != null){
				canvas.rotate(-(mValues[0]+ Qibla));
			}
			canvas.drawPath(mPath, mPaint);
		}
		@Override
		protected void onAttachedToWindow(){
			mAnimate = true;
			if (Config.DEBUG) Log.d(TAG, "onAttachedToWindow.mAnimate=" +mAnimate);
			super.onAttachedToWindow();
		}
		@Override
		protected void onDetachedFromWindow(){
			mAnimate = false;
			if (Config.DEBUG) Log.d(TAG, "onDetachedFromWindow. mAnimate="+ mAnimate);
			super.onDetachedFromWindow();
		}
    }
    private class DispLocListener implements LocationListener{
    	@Override
    	public void onLocationChanged(Location loc){
    		//update TextViews
    		latMosque = loc.getLatitude();
    		lonMosque = loc.getLongitude();
    	}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		}
}