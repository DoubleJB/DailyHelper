package com.example.dailyhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dailyhelper.MainFragment.Prompt;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TaskManagerActivity extends ListActivity {
	private final String PROMPT_FILE = "prompt.bin";
	private ListView list;
	List<Map<String, Object>> listItems;
	ArrayList<MyTask> taskList;
	ArrayList<CheckBox> checkBoxes;
	SimpleAdapter adapter;
	LinearLayout layoutDelete;
	Button deleteSubmit;
	Button deleteCancel;
	
	
	public Handler handler = new Handler(){
		public void handleMessage(Message msg){
			//adapter.notifyDataSetChanged();
			//adapter.notifyDataSetInvalidated();
			list.setAdapter(adapter);
			//list.refreshDrawableState();
			//list.invalidate();
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist);
		
		listItems = new ArrayList<Map<String, Object>>();
		taskList = new ArrayList<MyTask>();
		checkBoxes = new ArrayList<CheckBox>();
		File tmp = new File(getFilesDir().getPath().toString() + "/" + PROMPT_FILE);
		if(tmp.exists()){//存在则直接读取信息
			try {
				FileInputStream fis;
				fis = new FileInputStream(tmp);
			
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String placeStr;
				while((placeStr = br.readLine())!=null)
				{
					String[] strs = placeStr.split(" ");
					MyTask aTask = new MyTask(strs);
					taskList.add(aTask);
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("content", aTask.content);
					listItem.put("time", aTask.data+" "+aTask.time);
					listItems.add(listItem);
				}
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		adapter = new SimpleAdapter(this, listItems, 
				R.layout.tasklist_item,
				new String[] {"content","time", "visible"},
				new int[] {R.id.task_content, R.id.task_time, R.id.item_check});
		list = this.getListView();
		list.setAdapter(adapter);
		
		layoutDelete = (LinearLayout) findViewById(R.id.delete_layout);
		deleteSubmit = (Button) findViewById(R.id.delete_submit);
		deleteCancel = (Button) findViewById(R.id.delete_cancel);
		deleteSubmit.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				
				for(int i=adapter.getCount()-1; i>=0; i--)
	    		{
					LinearLayout layout = (LinearLayout) list.getChildAt(i);
	    			//LinearLayout layout = (LinearLayout) adapter.getView(i, null, getListView());
	    			((CheckBox)layout.findViewById(R.id.item_check)).setVisibility(CheckBox.INVISIBLE);
	    			if(checkBoxes.get(i).isChecked())
	    			{
	    				listItems.remove(i);
	    				taskList.remove(i);
	    			}
	    		}
	    		layoutDelete.setVisibility(LinearLayout.GONE);
	    		handler.sendMessage(new Message());
			}
		});
		deleteCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for(int i=0; i<adapter.getCount(); i++)
	    		{
	    			LinearLayout layout = (LinearLayout) list.getChildAt(i);
	    			((CheckBox)layout.findViewById(R.id.item_check)).setVisibility(CheckBox.INVISIBLE);
	    		}
	    		layoutDelete.setVisibility(LinearLayout.GONE);
	    		handler.sendMessage(new Message());
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        menu.clear();
        menu.add("ADD").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("DELETE").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
	@Override
	public void onStop()
	{
		super.onStop();
		File tmp = new File(getFilesDir().getPath().toString() + "/" + PROMPT_FILE);
		if(tmp.exists()){//存在则直接读取信息

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(tmp);
				for(int i=0; i<taskList.size(); i++)
				{
					String wr = taskList.get(i).id+" "+taskList.get(i).time+" "+taskList.get(i).content+" "+taskList.get(i).data;
					wr+="\n";
					fos.write(wr.getBytes());
				}
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 添加task或者删除task
    	Log.v(""+item.getTitle(), "clicked");
    	if(item.getTitle() == "DELETE")
    	{	//首先要重新载入新的列表视图，及在原来的视图的每一项前加checkbox，同时下面或actionbar上显示两个按钮，一个删除，一个取消，
    		//若dianji 删除，则删除对应得项并恢复原actionbar和列表,否则直接恢复
    		//Log.v("layout", (String) ((TextView)layout.findViewById(R.id.task_content)).getText());
    		//显示所有的checkbox
    		checkBoxes.clear();
    		for(int i=0; i<adapter.getCount(); i++)
    		{
    			LinearLayout layout = (LinearLayout) list.getChildAt(i);
    			//LinearLayout layout = (LinearLayout) adapter.getView(i, null, getListView());
    			Log.v("layout", (String) ((TextView)layout.findViewById(R.id.task_content)).getText());
    			//不能工作啊
    			((CheckBox)layout.findViewById(R.id.item_check)).setVisibility(CheckBox.VISIBLE);
    			checkBoxes.add((CheckBox)layout.findViewById(R.id.item_check));
    		}
    		layoutDelete.setVisibility(LinearLayout.VISIBLE);
    		layoutDelete.setFocusable(true);
    		//handler.sendMessage(new Message());

    	}
    	else if(item.getTitle() == "ADD")
    	{
    		TableLayout taskForm = (TableLayout)getLayoutInflater().inflate(R.layout.task_config, null);
        	//设置各控件内容和属性
        	//设置显示内容
        	final EditText taskText = (EditText)taskForm.findViewById(R.id.task_content);
        	//设置提醒类型
        	final LinearLayout weekLayout = (LinearLayout)taskForm.findViewById(R.id.week_layout);
        	//weekLayout.setVisibility(LinearLayout.GONE);
        	final DatePicker dataPicker = (DatePicker)taskForm.findViewById(R.id.data_picker);
        	//dataPicker.setVisibility(DatePicker.GONE);
        	final RadioGroup date = (RadioGroup) taskForm.findViewById(R.id.data_type);
        	final CheckBox CheckBoxes[] = new CheckBox[7];
        	//初始化一周7天的checkbox
        	CheckBoxes[0] = (CheckBox) taskForm.findViewById(R.id.week_sun);
        	CheckBoxes[1] = (CheckBox) taskForm.findViewById(R.id.week_mon);
        	CheckBoxes[2] = (CheckBox) taskForm.findViewById(R.id.week_tues);
        	CheckBoxes[3] = (CheckBox) taskForm.findViewById(R.id.week_wed);
        	CheckBoxes[4] = (CheckBox) taskForm.findViewById(R.id.week_Thur);
        	CheckBoxes[5] = (CheckBox) taskForm.findViewById(R.id.week_fri);
        	CheckBoxes[6] = (CheckBox) taskForm.findViewById(R.id.week_sat);
        	
        	weekLayout.setVisibility(LinearLayout.GONE);
    	    dataPicker.setVisibility(DatePicker.GONE);
        	
        	//设置时间
        	final TimePicker timePicker = (TimePicker)taskForm.findViewById(R.id.time_picker);
        	timePicker.setIs24HourView(true);
        	
        	date.setOnCheckedChangeListener(new OnCheckedChangeListener()
        	{

    			@Override
    			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
    				// 设置显示或不显示时间和星期选择框
    				switch(checkedId)
    				{
    				case R.id.data_daily:
    			    	weekLayout.setVisibility(LinearLayout.GONE);
    			    	dataPicker.setVisibility(DatePicker.GONE);
    			    	break;
    				case R.id.data_once:
    					weekLayout.setVisibility(LinearLayout.GONE);
    			    	dataPicker.setVisibility(DatePicker.VISIBLE);
    			    	break;
    				case R.id.data_week:
    					weekLayout.setVisibility(LinearLayout.VISIBLE);
    			    	dataPicker.setVisibility(DatePicker.GONE);
    			    	break;
    				}
    			}
        		
        	});
        	
        	//显示对话框
        	new AlertDialog.Builder(this)
    		.setTitle("设置任务")
    		.setView(taskForm)
    		.setPositiveButton("Submit", new OnClickListener()
    		{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// 修改re的内容
					MyTask aTask = new MyTask();
					aTask.id = ""+(taskList.size()+1);
					aTask.content = taskText.getText().toString();
					
					switch(date.getCheckedRadioButtonId())
					{
					case R.id.data_daily:
						aTask.data = "daily";
				    	break;
					case R.id.data_once:
						aTask.data = "once:"+dataPicker.getYear()+"/"+dataPicker.getMonth()+"/"+dataPicker.getDayOfMonth();
				    	break;
					case R.id.data_week:
						aTask.data = "week";
						for(int i=0; i<7; i++)
						{
							if(CheckBoxes[i].isChecked())
								aTask.data = aTask.data +":" + CheckBoxes[i].getText();
						}
						break;
					}
					
					aTask.time = "";
					if(timePicker.getCurrentHour()<10)
						aTask.time = aTask.time + "0";
					aTask.time = aTask.time + timePicker.getCurrentHour() + ":";
					if(timePicker.getCurrentMinute()<10)
						aTask.time = aTask.time + "0";
					aTask.time = aTask.time + timePicker.getCurrentMinute();
					//跟新列表
					taskList.add(aTask);
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("content", aTask.content);
					listItem.put("time", aTask.data+" "+aTask.time);
					listItems.add(listItem);

					handler.sendMessage(new Message());
				}
    		})
    		.setNegativeButton("Cancel", new OnClickListener()
    		{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// 什么也不做
				}
    		})
    		.create()
    		.show();
    	}
        return super.onOptionsItemSelected(item);
    }
	
    @Override   
    protected void onListItemClick(ListView l, View v, int position, long id) {  
    	//新建一个对话框来设置任务，同时返回设置内容，修改的task内容
        //Toast.makeText(this, "You click: " + position, Toast.LENGTH_SHORT).show();
    	//((TextView)v.findViewById(R.id.task_content)).setVisibility(View.INVISIBLE);
    	TableLayout taskForm = (TableLayout)getLayoutInflater().inflate(R.layout.task_config, null);
    	final int clickedPosition = position;
    	//设置各控件内容和属性
    	MyTask clickItem = taskList.get(position);
    	//设置显示内容
    	final EditText taskText = (EditText)taskForm.findViewById(R.id.task_content);
    	taskText.setText(clickItem.content);
    	//设置提醒类型
    	final LinearLayout weekLayout = (LinearLayout)taskForm.findViewById(R.id.week_layout);
    	//weekLayout.setVisibility(LinearLayout.GONE);
    	final DatePicker dataPicker = (DatePicker)taskForm.findViewById(R.id.data_picker);
    	//dataPicker.setVisibility(DatePicker.GONE);
    	final RadioGroup date = (RadioGroup) taskForm.findViewById(R.id.data_type);
    	final CheckBox CheckBoxes[] = new CheckBox[7];
    	//初始化一周7天的checkbox
    	CheckBoxes[0] = (CheckBox) taskForm.findViewById(R.id.week_sun);
    	CheckBoxes[1] = (CheckBox) taskForm.findViewById(R.id.week_mon);
    	CheckBoxes[2] = (CheckBox) taskForm.findViewById(R.id.week_tues);
    	CheckBoxes[3] = (CheckBox) taskForm.findViewById(R.id.week_wed);
    	CheckBoxes[4] = (CheckBox) taskForm.findViewById(R.id.week_Thur);
    	CheckBoxes[5] = (CheckBox) taskForm.findViewById(R.id.week_fri);
    	CheckBoxes[6] = (CheckBox) taskForm.findViewById(R.id.week_sat);
    	if(clickItem.data.startsWith("daily"))
    	{
    		date.check(R.id.data_daily);
    		weekLayout.setVisibility(LinearLayout.GONE);
	    	dataPicker.setVisibility(DatePicker.GONE);
    	}
    	else if(clickItem.data.startsWith("once")){
    		date.check(R.id.data_once);
			weekLayout.setVisibility(LinearLayout.GONE);
	    	dataPicker.setVisibility(DatePicker.VISIBLE);
	    	String tmp1 = clickItem.data.substring(5);
	    	String[] tmp2 = tmp1.split("/");
	    	dataPicker.init(Integer.parseInt(tmp2[0]), Integer.parseInt(tmp2[1]), Integer.parseInt(tmp2[2]), null);
    	}
    	else	
    	{
    		date.check(R.id.data_week);
			weekLayout.setVisibility(LinearLayout.VISIBLE);
	    	dataPicker.setVisibility(DatePicker.GONE);
	    	for(int i=0; i<7; i++)
	    	{
	    		if(clickItem.data.contains(CheckBoxes[i].getText()))
	    			CheckBoxes[i].setChecked(true);
	    		else
	    			CheckBoxes[i].setChecked(false);
	    	}
    	}
    	//设置时间
    	final TimePicker timePicker = (TimePicker)taskForm.findViewById(R.id.time_picker);
    	String[] time = clickItem.time.split(":");
    	timePicker.setIs24HourView(true);
    	timePicker.setCurrentHour(Integer.parseInt(time[0]));
    	timePicker.setCurrentMinute(Integer.parseInt(time[1]));
    	
    	date.setOnCheckedChangeListener(new OnCheckedChangeListener()
    	{

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// 设置显示或不显示时间和星期选择框
				switch(checkedId)
				{
				case R.id.data_daily:
			    	weekLayout.setVisibility(LinearLayout.GONE);
			    	dataPicker.setVisibility(DatePicker.GONE);
			    	break;
				case R.id.data_once:
					weekLayout.setVisibility(LinearLayout.GONE);
			    	dataPicker.setVisibility(DatePicker.VISIBLE);
			    	break;
				case R.id.data_week:
					weekLayout.setVisibility(LinearLayout.VISIBLE);
			    	dataPicker.setVisibility(DatePicker.GONE);
			    	break;
				}
			}
    		
    	});
    	
    	new AlertDialog.Builder(this)
    		.setTitle("设置任务")
    		.setView(taskForm)
    		.setPositiveButton("Submit", new OnClickListener()
    		{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// 修改re的内容
					Log.v("submit clicked", "list item "+clickedPosition);
					MyTask clickItem = taskList.get(clickedPosition);
					clickItem.content = taskText.getText().toString();
					
					switch(date.getCheckedRadioButtonId())
					{
					case R.id.data_daily:
						clickItem.data = "daily";
				    	break;
					case R.id.data_once:
						clickItem.data = "once:"+dataPicker.getYear()+"/"+dataPicker.getMonth()+"/"+dataPicker.getDayOfMonth();
				    	break;
					case R.id.data_week:
						clickItem.data = "week";
						for(int i=0; i<7; i++)
						{
							if(CheckBoxes[i].isChecked())
								clickItem.data = clickItem.data +":" + CheckBoxes[i].getText();
						}
						break;
					}
					
					clickItem.time = "";
					if(timePicker.getCurrentHour()<10)
						clickItem.time = clickItem.time + "0";
					clickItem.time = clickItem.time + timePicker.getCurrentHour() + ":";
					if(timePicker.getCurrentMinute()<10)
						clickItem.time = clickItem.time + "0";
					clickItem.time = clickItem.time + timePicker.getCurrentMinute();
					//跟新列表
					listItems.get(clickedPosition).clear();
					listItems.get(clickedPosition).put("content", clickItem.content);
					listItems.get(clickedPosition).put("time", clickItem.data+" "+clickItem.time);
					handler.sendMessage(new Message());
				}
    		})
    		.setNegativeButton("Cancel", new OnClickListener()
    		{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// 什么也不做
				}
    		})
    		.create()
    		.show();
    	
        super.onListItemClick(l, v, position, id);  
    }

	class MyTask
	{
		String time;
		String id;
		String data;
		String content;
		
		public MyTask()
		{
			this.time = "";
			this.id = "";
			this.data = "";
			this.content = "";
		}
		
		public MyTask(String id, String time,  String content, String data)
		{
			this.time = time;
			this.id = id;
			this.data = data;
			this.content = content;
		}
		
		public MyTask(String[] strs)
		{
			this.id = strs[0];
			this.time = strs[1];
			this.content = strs[2];
			this.data = strs[3];
		}
	}
}
