package com.example.dailyhelper;

import java.text.DecimalFormat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ExerciseFragment extends Fragment{
	private View layoutView;
	
	private static TextView stepText;
	private static TextView stepTimeText;
	private static TextView stepCalText;
	private static TextView stepEncourText;
	private TextView stepToday;
	private ImageButton stepConfig;
	private ImageView clockImg;
	private ImageView fireImg;
	
	private int phoneHeight;
	private int phoneWidth;
	
	private static int steps;
	private static double stepCal;
	private static long lastStepTime;
	private static StepTimer mStepTimer;
	
	private static float mStepLength;
	private static float mBodyWeight;
	private static int targetSteps;
	private static boolean mIsRunning;
	
	private static String[] encourageText = {"还没开始呢吧，要加油啊！"};
	
	private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
	
    private static double METRIC_RUNNING_FACTOR = 1.02784823;
    private static double IMPERIAL_RUNNING_FACTOR = 0.75031498;

    private static double METRIC_WALKING_FACTOR = 0.708;
    private static double IMPERIAL_WALKING_FACTOR = 0.517;
    
    private static long MAX_DELTA_TIME = 10000;
    
	private static Handler mHandler = new Handler()
	{
		@Override 
		public void handleMessage(Message msg) {
			//stepText.setText("steps: " + (int)msg.arg1);
			updateData((int)msg.arg1);
			updateView();
		}
	};
	
	private static void updateView()
	{
		//更新组件内容
		
		stepText.setText(""+steps+"步");
		stepTimeText.setText(mStepTimer.toString());
		DecimalFormat a = new DecimalFormat("#.##");
		String s= a.format(stepCal);
		stepCalText.setText(s);
		int index;
		index = (int)(((double)steps)/((double)targetSteps)*10);
		if(index>10)
			index = 10;
		stepEncourText.setText(encourageText[index]);
	}
	
	private static void updateData(int st)
	{
		//更新卡路里
		stepCal += 
                (mBodyWeight * (mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                // Distance:
                * mStepLength // centimeters
                / 100000.0; // centimeters/kilometer
		//更新计时
		if(lastStepTime == 0)
			lastStepTime = System.currentTimeMillis();
		else
		{
			long nowTime = System.currentTimeMillis();
			if(nowTime-lastStepTime<MAX_DELTA_TIME)
			{
				lastStepTime += mStepTimer.addTime(nowTime-lastStepTime);			
			}
			else
			{
				lastStepTime = nowTime;
			}
		}
		//更新步数
		Log.v("step",""+st);
		steps = st;
	}
	
	private void initData() {
		//初始化各个数据，有些数据需要从文件中读取，这里需要修改
		steps = 0;
		stepCal = 0;
		lastStepTime = 0;
		mStepTimer = new StepTimer();
		
		mStepLength = (float) 0.5;
		mBodyWeight = 60;
		targetSteps = 10000;
		mIsRunning = false;
	}
	
	private void setViewPre()
	{//在这里获得不同设备的宽高，设置各组件的大小和字体的大小，此处需要修改
		Display display = this.getActivity().getWindowManager().getDefaultDisplay(); 
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		phoneHeight = outMetrics.heightPixels;
		phoneWidth = outMetrics.widthPixels;
		Log.v("h&w", ""+phoneHeight+" "+phoneWidth);
		
		
		stepToday.setTextSize(40);
		stepText.setTextSize(60);
		stepTimeText.setTextSize(40);
		stepCalText.setTextSize(40);
		stepEncourText.setTextSize(20);
		Log.v("height", ""+stepTimeText.getHeight());
		clockImg.setMaxHeight((int)(40*2));
		fireImg.setMaxHeight((int)(40*2));
		stepConfig.setMaxHeight(10);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.exercise_frag, null); 
		stepText = (TextView)layoutView.findViewById(R.id.steps_text);
		stepTimeText = (TextView)layoutView.findViewById(R.id.step_time);
		stepCalText = (TextView)layoutView.findViewById(R.id.step_cal);
		stepEncourText = (TextView)layoutView.findViewById(R.id.step_encourage);
		stepConfig = (ImageButton)layoutView.findViewById(R.id.step_config);
		stepToday = (TextView)layoutView.findViewById(R.id.step_today);
		clockImg = (ImageView)layoutView.findViewById(R.id.clock_img);
		fireImg = (ImageView)layoutView.findViewById(R.id.fire_img);
		
		if(stepText == null)
			Log.v("stepText", "null");
		else
			Log.v("stepText", "not null");
		initData();
		setViewPre();
		updateView();
		
		
		
		//注册广播收听
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.dailyhelper.STEP_ACTION");
		Log.v("exFrag", "regis");
		broadcastManager.registerReceiver(new StepReceiver(),filter);
		
		return layoutView;
	}

	static public class StepReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			Log.v("exFrag", "onReceive");
			int steps = intent.getIntExtra("1", 0);
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, steps, 0));
		}
		
	}
	
	public class StepTimer {
		private long hours;
		private long minutes;
		private long seconds;
		public StepTimer()
		{
			hours = 0;
			minutes = 0;
			seconds = 0;
		}
		
		public long addTime(long timeP)
		{
			if(timeP<1000)
				return 0;
			hours+=timeP/1000/60/60;
			minutes+=(timeP%(1000*60*60))/(1000*60);
			seconds+=(timeP%(1000*60))/1000;
			
			if(seconds>=60){
				minutes+=seconds/60;
				seconds%=60;
			}
			if(minutes>=60){
				hours+=minutes/60;
				minutes%=60;
			}
			return timeP/1000*1000;
		}
		
		public String toString()
		{
			StringBuffer r = new StringBuffer(8);
			if(hours<10 )
				r.append('0');
			r.append(hours);
			r.append(':');
			if(minutes<10 )
				r.append('0');
			r.append(minutes);
			r.append(':');
			if(seconds<10 )
				r.append('0');
			r.append(seconds);
			return r.toString();
		}
	}
}
