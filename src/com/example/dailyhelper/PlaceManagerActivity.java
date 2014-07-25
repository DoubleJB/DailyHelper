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

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

public class PlaceManagerActivity extends ListActivity{
	private final String PLACE_FILE = "place.bin";
	private ListView list;
	private List<Map<String, Object>> listItems;
	private ArrayList<CheckBox> checkBoxes;
	private ArrayList<Place> placeList;
	private SimpleAdapter adapter;
	private LinearLayout layoutDelete;
	private Button deleteSubmit;
	private Button deleteCancel;

	
//	private TableLayout taskForm;
//	private EditText nameText;
//	private EditText radioText;
//	private EditText promptText;
//	private Button placeButton;
	private MapView mMapView;
//	private Button placeSub;
//	private Button placeCal;
//	private TextView latText;
//	private TextView lngText;
//	private LinearLayout placeLayout;
	
	private boolean isFirstLoc = true;// 是否首次定位
	private LatLng clickedPoint;
	private LatLng nowLocation;
	private BaiduMap mBaiduMap;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	private Handler handler = new Handler(){
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
		
		SDKInitializer.initialize(getApplicationContext());
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener( myListener );    //注册监听函数
		setContentView(R.layout.placelist);
		
		listItems = new ArrayList<Map<String, Object>>();
		placeList = new ArrayList<Place>();
		checkBoxes = new ArrayList<CheckBox>();
		//读取地点列表
		File tmp = new File(getFilesDir().getPath().toString() + "/" + PLACE_FILE);
		if(tmp.exists()){//存在则直接读取信息
			try {
				FileInputStream fis;
				fis = new FileInputStream(tmp);
			
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String placeStr;
				while((placeStr = br.readLine())!=null)
				{
					String[] strs = placeStr.split(" ");
					Place aPlace = new Place(strs);
					placeList.add(aPlace);
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("name", aPlace.name);
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
		//设置列表适配器
		adapter = new SimpleAdapter(this, listItems, 
				R.layout.tasklist_item,
				new String[] {"content","time", "visible"},
				new int[] {R.id.task_content, R.id.task_time, R.id.item_check});
		list = this.getListView();
		list.setAdapter(adapter);
		//设置删除按钮布局
		layoutDelete = (LinearLayout) findViewById(R.id.delete_place_layout);
		deleteSubmit = (Button) findViewById(R.id.delete_place_submit);
		deleteCancel = (Button) findViewById(R.id.delete_place_cancel);
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
	    				placeList.remove(i);
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
		
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
//		option.setScanSpan(1000);//设置发起定位请求的间隔时间为5000ms
//		mLocationClient.setLocOption(option);
//		mLocationClient.start();
		// 开启定位图层

		
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
		File tmp = new File(getFilesDir().getPath().toString() + "/" + PLACE_FILE);
		if(tmp.exists()){//存在则直接读取信息

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(tmp);
				for(int i=0; i<placeList.size(); i++)
				{
					String wr = placeList.get(i).toString();
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
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // 添加task或者删除task
    	if(item.getTitle() == "DELETE")
    	{
    		checkBoxes.clear();
    		for(int i=0; i<adapter.getCount(); i++)
    		{
    			LinearLayout layout = (LinearLayout) list.getChildAt(i);
    			((CheckBox)layout.findViewById(R.id.place_item_check)).setVisibility(CheckBox.VISIBLE);
    			checkBoxes.add((CheckBox)layout.findViewById(R.id.place_item_check));
    		}
    		layoutDelete.setVisibility(LinearLayout.VISIBLE);
    		layoutDelete.setFocusable(true);
    	}
    	else if(item.getTitle() == "ADD")
    	{
    		final TableLayout taskForm = (TableLayout)getLayoutInflater().inflate(R.layout.place_config, null);
    		final EditText nameText = (EditText)taskForm.findViewById(R.id.place_config_name);
    		final EditText radioText = (EditText)taskForm.findViewById(R.id.place_config_radio);
    		final EditText promptText = (EditText)taskForm.findViewById(R.id.place_config_content);
    		final Button placeButton = (Button)taskForm.findViewById(R.id.place_config_place);
    		final LinearLayout placeLayout = (LinearLayout)taskForm.findViewById(R.id.place_layout);
    		mMapView  = (MapView) taskForm.findViewById(R.id.bmapView);
    		final Button placeSub = (Button) taskForm.findViewById(R.id.place_bmap_submit);
    		final Button placeCal = (Button) taskForm.findViewById(R.id.place_bmap_cancel);
    		final TextView latText = (TextView) taskForm.findViewById(R.id.latText);
    		final TextView lngText = (TextView) taskForm.findViewById(R.id.lngText);
    		mBaiduMap = mMapView.getMap();
    		
			mBaiduMap.setMyLocationEnabled(true);
			// 定位初始化
			mLocationClient = new LocationClient(this);
			mLocationClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(1000);
			mLocationClient.setLocOption(option);
			mLocationClient.start();

    		
    		placeLayout.setVisibility(View.GONE);
    		//设置地图长按事件，长按设置点击点
    		OnMapLongClickListener listener = new OnMapLongClickListener() {  
    		    /** 
    		    * 地图长按事件监听回调函数 
    		    * @param point 长按的地理坐标 
    		    */  
    		    public void onMapLongClick(LatLng point){  
    		    	clickedPoint = point;
    		    }  
    		};
    		mMapView.getMap().setOnMapLongClickListener(listener);
    		//设置弹出和隐藏地图视图按钮
    		placeButton.setOnClickListener(new View.OnClickListener(){

    			@Override
    			public void onClick(View view) {
    				// TODO Auto-generated method stub
    				if(mMapView.getVisibility() == View.GONE)
    				{
    					mMapView.setVisibility(View.VISIBLE);
    					placeLayout.setVisibility(View.VISIBLE);
    				}
    				else
    				{
    					mMapView.setVisibility(View.GONE);
    					placeLayout.setVisibility(View.GONE);
    				}
    			}
    			
    		});
    		//设置提交按钮，确认则隐藏地图并更新地点，取消则隐藏地图
    		placeCal.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				mMapView.setVisibility(View.GONE);
    				placeLayout.setVisibility(View.GONE);
    			}
    		});
    		
    		placeSub.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				mMapView.setVisibility(View.GONE);
    				placeLayout.setVisibility(View.GONE);
    				placeButton.setText("点击地点（点击更改）");
    				if(clickedPoint != null){
    					latText.setText("经度：" + clickedPoint.latitude);
    					lngText.setText("纬度：" + clickedPoint.longitude);
    				}
    			}
    		});
    		
    		isFirstLoc = true;
    		placeButton.setText("当前定位地点（点击更改）");
    		
    		//设置地图视图为隐藏
    		mMapView.setVisibility(View.GONE);
    		clickedPoint = null;
    		if(!mLocationClient.isStarted()){
    			mLocationClient.start();
    		}
			if (mLocationClient != null && mLocationClient.isStarted()){
				Log.v("mLocationClient", "requestLocation");
				mLocationClient.requestLocation();
			}
    		//latText.setText("经度：" + nowLocation.latitude);
    		//lngText.setText("经度：" + nowLocation.longitude);
    		//显示对话框
        	new AlertDialog.Builder(this)
    		.setTitle("设置地点")
    		.setView(taskForm)
    		.setPositiveButton("Submit", new DialogInterface.OnClickListener()
    		{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// 新建提醒地点

				}
    		})
    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
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
        super.onListItemClick(l, v, position, id);  
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			Log.v("MyLocationListener", "in MyLocationListener");
			if (location == null || mMapView == null)
				return;
//			location.getAltitude();
//			MyLocationData locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360
//					.direction(100).latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//			mBaiduMap.setMyLocationData(locData);
//			if (isFirstLoc) {
//				isFirstLoc = false;
//				LatLng ll = new LatLng(location.getLatitude(),
//						location.getLongitude());
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//				mBaiduMap.animateMapStatus(u);
//			}
			nowLocation = new LatLng(location.getLatitude(), location.getLongitude());
			Log.v("MyLocationListener", "get nowLocation");
			if (isFirstLoc) {
				isFirstLoc = false;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(nowLocation);
				mBaiduMap.animateMapStatus(u);
//	    		latText.setText("经度：" + nowLocation.latitude);
//	    		lngText.setText("经度：" + nowLocation.longitude);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	class Place{
		String ID;
		String longitud;
		String latitude;
		String name;
		String radio;
		String prompt;
		
		public Place(String s[])
		{
			ID = s[0];
			longitud = s[1];
			latitude = s[2];
			name = s[3];
			radio = s[4];
			prompt = s[5];
		}
		
		public Place(String ID, String longitud, String latitude, String name, String radio, String prompt)
		{
			this.ID = ID;
			this.longitud = longitud;
			this.latitude = latitude;
			this.name = name;
			this.radio = radio;
			this.prompt = prompt;
		}
		
		public String toString()
		{
			return ID + " " + longitud + " " + latitude + " " + name + " " + radio
					+ " " + prompt;
		}
	}
}
