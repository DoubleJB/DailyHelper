package com.example.dailyhelper;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class StepService extends Service{

	private int steps;
	private SensorManager mSensorManager;
	private Sensor sensor;
	private StepDetector mStepDetector;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.v("service create", "do");
		mStepDetector = new StepDetector();
		mStepDetector.setListener(mListener);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(mStepDetector, sensor, SensorManager.SENSOR_DELAY_FASTEST);
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
			steps = value;
			Intent intent  = new Intent();
			intent.setAction("com.example.dailyhelper.STEP_ACTION");
			intent.putExtra("1", value);
			intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			sendBroadcast(intent);
			Log.v("Service", "sendBroadcast");
		}
		
	};
}
