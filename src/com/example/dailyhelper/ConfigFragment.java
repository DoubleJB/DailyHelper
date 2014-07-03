package com.example.dailyhelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dailyhelper.ExerciseFragment.StepTimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ConfigFragment extends Fragment{
	private View layoutView;

	private ListView stepConfList;
	
	//��Ҫ�������õ�����
	private float mStepLength;
	private float mBodyWeight;
	private int targetSteps;
	private boolean mIsRunning;
	private float mSensitivity;
	
	private SharedPreferences mSettings;
	private SharedPreferences.Editor editor;
	
	private String[] stepConfData={"�����趨", "�����趨", "Ŀ���趨", "����/�ܲ��л�", "������"};
	
	private final String[] sensitivityStr={"extra_high","very_high","high","higher",
			"medium","lower","low","very_low","extra_low"};
	private final float[] sensitivityValue={(float) 1.9753,(float) 2.9630,(float) 4.4444,(float) 6.6667,
			10,15,(float) 22.5,(float) 33.75,(float) 50.625};
	
	private final int STEPLENGTH = 0;
	private final int BODYWEIGHT = 1;
	private final int TARGETSTEPS = 2;
	private final int ISRUNNING = 3;
	private final int SENSITIVITY = 4;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.config_frag, null); 
		
		mSettings = this.getActivity().getSharedPreferences("dailyHelper", Context.MODE_WORLD_READABLE);
		editor = mSettings.edit();
		
		//��ʼ��steps�������
		stepConfList = (ListView) layoutView.findViewById(R.id.step_conf_list);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for(int i=0; i<stepConfData.length; i++)
		{
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("config_item", stepConfData[i]);
			item.put("config_item_array", R.drawable.conf_item_bg);
			listItems.add(item);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this.getActivity(),
				listItems,R.layout.config_item,
				new String[] {"config_item", "config_item_array"},
				new int[] {R.id.config_item, R.id.config_item_array});
		stepConfList.setAdapter(simpleAdapter);
		//���õ���¼�
		stepConfList.setOnItemClickListener(stepListListener);
		
		
		
		initData();
		
		return layoutView;
	}
	
	private EditText edit;

	private OnItemClickListener stepListListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			edit = new EditText(ConfigFragment.this.getActivity());
			DecimalFormat a = new DecimalFormat("#.##");
			String s;
			int i;
			switch(position)
			{
				case STEPLENGTH:
					edit.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
					
					s = a.format(mStepLength);
					edit.setText(s);
					new AlertDialog.Builder(ConfigFragment.this.getActivity()).setTitle("�����벽��").setView(
							edit).setPositiveButton("ȷ��", 
						    		 new DialogInterface.OnClickListener()
								     {
										@Override
										public void onClick(DialogInterface arg0,
												int arg1) {
											///�޸Ĳ���
											mStepLength = Float.parseFloat(edit.getText().toString());
											editor.putFloat("step_length", mStepLength);
											editor.commit();
											editor.clear();
										}
								    	 
								     })
						     .setNegativeButton("ȡ��", null).show();
					break;
				case BODYWEIGHT:
					edit.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
					s= a.format(mBodyWeight);
					edit.setText(s);
					new AlertDialog.Builder(ConfigFragment.this.getActivity()).setTitle("����������").setView(
							edit).setPositiveButton("ȷ��", 
						    		 new DialogInterface.OnClickListener()
								     {
										@Override
										public void onClick(DialogInterface arg0,
												int arg1) {
											///�޸Ĳ���
											mBodyWeight = Float.parseFloat(edit.getText().toString());
											editor.putFloat("body_weight", mBodyWeight);
											editor.commit();
											editor.clear();
										}
								    	 
								     })
						     .setNegativeButton("ȡ��", null).show();
					break;
				case TARGETSTEPS:
					edit.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
					s= ""+targetSteps;
					edit.setText(s);
					new AlertDialog.Builder(ConfigFragment.this.getActivity()).setTitle("����������").setView(
							edit).setPositiveButton("ȷ��", 
						    		 new DialogInterface.OnClickListener()
								     {
										@Override
										public void onClick(DialogInterface arg0,
												int arg1) {
											///�޸Ĳ���
											targetSteps = Integer.parseInt(edit.getText().toString());
											editor.putInt("target_steps", targetSteps);
											editor.commit();
											editor.clear();
										}
								    	 
								     })
						     .setNegativeButton("ȡ��", null).show();
					break;
				case ISRUNNING:
					int selected = mIsRunning?1:0;
					new AlertDialog.Builder(ConfigFragment.this.getActivity())
				 	.setTitle("��ѡ����·/�ܲ�")
				 	.setIcon(android.R.drawable.ic_dialog_info)                
				 	.setSingleChoiceItems(new String[] {"��·","�ܲ�"}, selected, 
				 	  new DialogInterface.OnClickListener() {
				 	                              
				 	     public void onClick(DialogInterface dialog, int which) {
				 	    	 if(which == 0)
				 	    		 mIsRunning = false;
				 	    	 else
				 	    		 mIsRunning = true;
				 	    	editor.putInt("is_running", which);
				 	    	editor.commit();
							editor.clear();
				 	        dialog.dismiss();
				 	     }
				 	  }
				 	)
				 	.setNegativeButton("ȡ��", null)
				 	.show();
					break;
				case SENSITIVITY:
					for(i=0; i<9; i++)
					{
						if(isSameFloat(sensitivityValue[i] , mSensitivity))
							break;
					}
					new AlertDialog.Builder(ConfigFragment.this.getActivity())
				 	.setTitle("��ѡ��������")
				 	.setIcon(android.R.drawable.ic_dialog_info)                
				 	.setSingleChoiceItems(sensitivityStr, i, 
				 	  new DialogInterface.OnClickListener() {
				 	                              
				 	     public void onClick(DialogInterface dialog, int which) {
				 	    	 Log.v("SENSITIVITY", "click "+which);
				 	    	mSensitivity = sensitivityValue[which];
				 	    	editor.putFloat("sensitivity", sensitivityValue[which]);
				 	    	editor.commit();
							editor.clear();
				 	    	 dialog.dismiss();
				 	     }
				 	  }
				 	)
				 	.setNegativeButton("ȡ��", null)
				 	.show();
					break;
			}
			
		}
		
	};
	
	private void initData()
	{
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String nowTime=format.format(new Date());
		if(mSettings.getInt("init", 0) == 0)
		{//����װ���һ�����У���������س�ʼ����ֵ
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putInt("init", 1);
			editor.putString("date", nowTime);
			
			editor.putInt("steps", 0);
			
			editor.putFloat("step_cal", 0);
			
			editor.putInt("last_step_time", 0);
			
			editor.putString("step_timer", "00:00:00");
			
			editor.putFloat("step_length", (float) 0.5);
			mStepLength = (float) 0.5;
			
			editor.putFloat("body_weight", (float) 60);
			mBodyWeight = (float) 60;
			
			editor.putInt("target_steps", 1000);
			targetSteps = 1000;
			
			editor.putInt("is_running", 0);
			mIsRunning = false;
			
			editor.putFloat("sensitivity", 10);
			mSensitivity = 10;
			Log.v("conf init Data", "create");
			editor.commit();
			editor.clear();
		}
		else if(nowTime.equals(mSettings.getString("date", "")))
		{
			//�������й�����ȡ֮ǰ�����ݼ���ִ��
			mStepLength = mSettings.getFloat("step_length", (float)0.5);
			mBodyWeight = mSettings.getFloat("body_weight", (float)60);
			targetSteps = mSettings.getInt("target_steps", 1000);
			if(mSettings.getInt("is_running", 0) == 0)
				mIsRunning = false;
			mSensitivity = mSettings.getFloat("sensitivity", 10);
			Log.v("conf init Data", "run");
		}
		else
		{
			//����֮ǰ���й����������ǵ�һ������
			//��ʼ���������ݣ���Щ������Ҫ���ļ��ж�ȡ��������Ҫ�޸�		
			mStepLength = mSettings.getFloat("step_length", (float)0.5);
			mBodyWeight = mSettings.getFloat("body_weight", (float)60);
			targetSteps = mSettings.getInt("target_steps", 1000);
			if(mSettings.getInt("is_running", 0) == 0)
				mIsRunning = false;
			mSensitivity = mSettings.getFloat("sensitivity", 10);
			Log.v("conf init Data", "first run");
		}
	}
	
	public boolean isSameFloat(float a, float b)
	{
		if(Math.abs(a-b) < 0.00001)
			return true;
		else
			return false;
	}
}
