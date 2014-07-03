package com.example.dailyhelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;

public class StepService extends Service{

	private int steps;
	private SensorManager mSensorManager;
	private Sensor sensor;
	private StepDetector mStepDetector;
	private WakeLock mWakeLock;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		acquireWakeLock();
		Log.v("service create", "do");
		mStepDetector = new StepDetector();
		mStepDetector.setListener(mListener);
		SharedPreferences SharedPreferences = this.getSharedPreferences("dailyHelper", Context.MODE_WORLD_READABLE);
		mStepDetector.setSensitivity(SharedPreferences.getFloat("sensitivity", 10));
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(mStepDetector, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	@Override
	public void onDestroy()
	{
		Log.v("onDestroy", "ss");
		releaseWakeLock();
		super.onDestroy();
		
	}
	
	public class StepBinder extends Binder{
		public int getSteps()
		{
			return steps;
		}
		
		StepService getService() {
            return StepService.this;
        }
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public interface ICallback {
        public void stepsChanged(int value);
        public void paceChanged(int value);
        public void distanceChanged(float value);
        public void speedChanged(float value);
        public void caloriesChanged(float value);
    }
    
    private ICallback mCallback;

	public void registerCallback(ICallback cb) {
		// TODO Auto-generated method stub
		mCallback = cb;
	}
	
	private StepDetector.Listener mListener = new StepDetector.Listener()
	{

		@Override
		public void passValue(int value) {
			// TODO Auto-generated method stub
			Log.v("server step",""+value);
			steps = value;
			Intent intent  = new Intent();
			intent.setAction("com.example.dailyhelper.STEP_ACTION");
			intent.putExtra("1", value);
			intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			sendBroadcast(intent);
			Log.v("Service", "sendBroadcast");
		}
		
	};
	
	//申请设备电源锁
	    private void acquireWakeLock()
	    {
	        if (null == mWakeLock)
	        {
	            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
	            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE,"");
	            if (null != mWakeLock)
	            {
	                mWakeLock.acquire();
	            }
	        }
	    }

	    //释放设备电源锁
	    private void releaseWakeLock()
	    {
	        if (null != mWakeLock)
	        {
	            mWakeLock.release();
	            mWakeLock = null;
	        }
	    }
}
