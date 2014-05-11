package com.example.dailyhelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ConfigFragment extends Fragment{
	private View layoutView;

	private ListView stepConfList;
	
	//需要进行设置的数据
	private static float mStepLength;
	private static float mBodyWeight;
	private static int targetSteps;
	private static boolean mIsRunning;
	
	private String[] stepConfData={"步长设定", "体重设定", "目标设定", "步行/跑步切换"};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.config_frag, null); 
		
		//初始化steps设置类表
		stepConfList = (ListView) layoutView.findViewById(R.id.step_conf_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
				R.layout.config_item,stepConfData);
		stepConfList.setAdapter(adapter);
		
		return layoutView;
	}
}
