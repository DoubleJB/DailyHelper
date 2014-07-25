package com.example.dailyhelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ExerciseFragment extends Fragment{
	private View layoutView;
	
	private static TextView stepText;
	private static TextView stepTimeText;
	private static TextView stepCalText;
	private static TextView stepEncourText;
	private TextView stepToday;
	private ImageView clockImg;
	private ImageView fireImg;
	private static ProgressBar bar;
	private static ImageView runManImg;
	
	private int phoneHeight;
	private static int phoneWidth;
	
	private static int steps;
	private static double stepCal;
	private static long lastStepTime;
	private static StepTimer mStepTimer;
	
	private static float mStepLength;
	private static float mBodyWeight;
	private static int targetSteps;
	private static boolean mIsRunning;
	
	private static String[] encourageText = 
	{
		"要开始呢呦，加油吧！",
		"不错不错，开了个好头，继续加油~",
		"不错不错，开了个好头，继续加油~",
		"嗯嗯，已经进行了三分之一了，要继续保持这种良好势头哦~",
		"马上要完成一半的任务了，很不错！",
		"很好，稳步前进吧！",
		"已经通过及格线了，但是作为优秀人士，怎么能满足于及格呢，向着满分进发吧~",
		"劳逸结合，适当休息有助于更好的完成任务！",
		"要进入最后阶段了，准备冲刺了~",
		"就要完成任务了~加把劲啊~",
		"OK！顺利完成任务，要适当休息喔，不要太累了",
	};
	
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
    
    private SharedPreferences mSettings;
    private LocalBroadcastManager broadcastManager = null;
    private Boolean serIsRunning = false;
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.exercise_frag, null); 
		stepText = (TextView)layoutView.findViewById(R.id.steps_text);
		stepTimeText = (TextView)layoutView.findViewById(R.id.step_time);
		stepCalText = (TextView)layoutView.findViewById(R.id.step_cal);
		stepEncourText = (TextView)layoutView.findViewById(R.id.step_encourage);
		stepToday = (TextView)layoutView.findViewById(R.id.step_today);
		clockImg = (ImageView)layoutView.findViewById(R.id.clock_img);
		fireImg = (ImageView)layoutView.findViewById(R.id.fire_img);
		bar = (ProgressBar)layoutView.findViewById(R.id.bar);
		runManImg = (ImageView)layoutView.findViewById(R.id.run_man);
		
		if(!serIsRunning)
			initData();
		serIsRunning = true;
		setViewPre();
		updateView();
		
		//注册广播收听
		if(broadcastManager == null){
			broadcastManager = LocalBroadcastManager.getInstance(getActivity());
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.example.dailyhelper.STEP_ACTION");
			Log.v("exFrag", "regis");
			broadcastManager.registerReceiver(new StepReceiver(),filter);
		}
		
		return layoutView;
	}

	@Override 
	public void onDestroyView()
	{
		super.onDestroyView();
		SharedPreferences.Editor editor = mSettings.edit();
		
		editor.putInt("steps", steps);	
		editor.putFloat("step_cal", (float)stepCal);
		editor.putInt("last_step_time", (int)lastStepTime);
		editor.putString("step_timer", mStepTimer.toString());
		editor.commit();
		editor.clear();
	}
    
	private static Handler mHandler = new Handler()
	{
		@Override 
		public void handleMessage(Message msg) {
			//stepText.setText("steps: " + (int)msg.arg1);
			
			if(stepText != null){
				updateData((int)msg.arg1);
				updateView();
			}	
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
		//bar.setProgress(steps/targetSteps);
		bar.setProgress(100*steps/targetSteps);
		Log.v("left", ""+((phoneWidth-20)*steps/targetSteps-140));
		runManImg.setTranslationX((phoneWidth-20)*steps/targetSteps-140);
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
		mSettings = this.getActivity().getSharedPreferences("dailyHelper", Context.MODE_WORLD_READABLE);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String nowTime=format.format(new Date());
		if(mSettings.getInt("init", 0) == 0)
		{//程序安装后第一次运行，则设置相关初始化数值
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putInt("init", 1);
			editor.putString("date", nowTime);
			
			editor.putInt("steps", 0);
			steps = 0;
			
			editor.putFloat("step_cal", 0);
			stepCal = 0;
			
			editor.putInt("last_step_time", 0);
			lastStepTime = 0;
			
			mStepTimer = new StepTimer();
			editor.putString("step_timer", mStepTimer.toString());
			
			editor.putFloat("step_length", (float) 0.5);
			mStepLength = (float) 0.5;
			
			editor.putFloat("body_weight", (float) 60);
			mBodyWeight = (float) 60;
			
			editor.putInt("target_steps", 1000);
			targetSteps = 1000;
			
			editor.putInt("is_running", 0);
			mIsRunning = false;
			
			editor.putFloat("sensitivity", 10);
			
			editor.commit();
			editor.clear();
		}
		else if(nowTime.equals(mSettings.getString("date", "")))
		{
			//今天运行过，读取之前得数据继续执行
			steps = mSettings.getInt("steps", 0);
			stepCal = mSettings.getFloat("step_cal", 0);
			lastStepTime = mSettings.getInt("last_step_time", 0);
			mStepTimer = new StepTimer(mSettings.getString("step_timer", "0:0:0"));
			mStepLength = mSettings.getFloat("step_length", (float)0.5);
			mBodyWeight = mSettings.getFloat("body_weight", (float)60);
			targetSteps = mSettings.getInt("target_steps", 1000);
			if(mSettings.getInt("is_running", 0) == 0)
				mIsRunning = false;
		}
		else
		{
			//程序之前运行过，但今天是第一次运行
			//初始化各个数据，有些数据需要从文件中读取，这里需要修改
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putString("date", nowTime);
			editor.commit();
			editor.clear();
			steps = 0;
			stepCal = 0;
			lastStepTime = 0;
			mStepTimer = new StepTimer();
			
			mStepLength = mSettings.getFloat("step_length", (float)0.5);
			mBodyWeight = mSettings.getFloat("body_weight", (float)60);
			targetSteps = mSettings.getInt("target_steps", 1000);
			if(mSettings.getInt("is_running", 0) == 0)
				mIsRunning = false;
		}
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
		
		public StepTimer(String time)
		{
			String[] tmp = time.split(":");
			hours = Integer.parseInt(tmp[0]);
			minutes = Integer.parseInt(tmp[1]);
			seconds = Integer.parseInt(tmp[2]);
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
