package com.example.dailyhelper;

import java.util.List;
import java.util.Map;

import com.example.dailyhelper.ExerciseFragment.StepReceiver;
import com.example.dailyhelper.StepService.ICallback;

import android.os.Bundle;
import android.os.IBinder;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SpinnerAdapter;

public class MainActivity extends FragmentActivity {

	private FragmentTabHost mTabHost;
	private RadioGroup m_radioGroup;
	private ActionBar actionBar;
	private String tabs[] = {"Home","Exercise","Config"};
	private Class cls[] = {MainFragment.class,ExerciseFragment.class,ConfigFragment.class};
	
	private StepReceiver stepReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actionBar = getActionBar();
		init();
		
		Log.v("startService", "do");
		
		startService(new Intent(this, StepService.class));
		

	}
	
	
	private void init()
	{
		mTabHost = (FragmentTabHost)this.findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.getTabWidget().setVisibility(View.GONE);
		for(int i=0;i<tabs.length;i++){
//			View tabView = this.getLayoutInflater().inflate(R.layout.tab_indicator, null);
			mTabHost.addTab(mTabHost.newTabSpec(tabs[i]).setIndicator(tabs[i]),cls[i], null);
		}
		m_radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
		m_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.RadioButton0:
					mTabHost.setCurrentTabByTag(tabs[0]);
					actionBar.setTitle("0");
					
					break;
				case R.id.RadioButton1:
					mTabHost.setCurrentTabByTag(tabs[1]);
					actionBar.setTitle("1");
					break;
				case R.id.RadioButton2:
					mTabHost.setCurrentTabByTag(tabs[2]);
					actionBar.setTitle("2");
					break;
				}
			}
		});

		((RadioButton) m_radioGroup.getChildAt(0)).toggle();
	}
	
}
