package com.example.dailyhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dailyhelper.MainFragment.Prompt;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.Toast;

public class TaskManagerActivity extends ListActivity {
	private final String PROMPT_FILE = "prompt.bin";
	private ListView list;
	List<Map<String, Object>> listItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist);
		
		listItems = new ArrayList<Map<String, Object>>();
		
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
		
		SimpleAdapter adapter = new SimpleAdapter(this, listItems, 
				R.layout.tasklist_item,
				new String[] {"content","time"},
				new int[] {R.id.task_content, R.id.task_time});
		list = this.getListView();
		list.setAdapter(adapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // 添加task或者删除task
    	
        return super.onOptionsItemSelected(item);
    }
	
    @Override   
    protected void onListItemClick(ListView l, View v, int position, long id) {  
    	//新建一个对话框来设置任务，同时返回设置内容，修改的task内容
        //Toast.makeText(this, "You click: " + position, Toast.LENGTH_SHORT).show();  
    	TableLayout taskForm = (TableLayout)getLayoutInflater().inflate(R.layout.task_config, null);
    	new AlertDialog.Builder(this)
    		.setTitle("设置任务")
    		.setView(taskForm)
    		.setPositiveButton("Submit", new OnClickListener()
    		{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// 修改re的内容
					
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
