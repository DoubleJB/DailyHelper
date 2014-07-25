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
	
	private boolean isFirstLoc = true;// �Ƿ��״ζ�λ
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
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
	    mLocationClient.registerLocationListener( myListener );    //ע���������
		setContentView(R.layout.placelist);
		
		listItems = new ArrayList<Map<String, Object>>();
		placeList = new ArrayList<Place>();
		checkBoxes = new ArrayList<CheckBox>();
		//��ȡ�ص��б�
		File tmp = new File(getFilesDir().getPath().toString() + "/" + PLACE_FILE);
		if(tmp.exists()){//������ֱ�Ӷ�ȡ��Ϣ
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
		//�����б�������
		adapter = new SimpleAdapter(this, listItems, 
				R.layout.tasklist_item,
				new String[] {"content","time", "visible"},
				new int[] {R.id.task_content, R.id.task_time, R.id.item_check});
		list = this.getListView();
		list.setAdapter(adapter);
		//����ɾ����ť����
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
//		option.setOpenGps(true);// ��gps
//		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
//		option.setScanSpan(1000);//���÷���λ����ļ��ʱ��Ϊ5000ms
//		mLocationClient.setLocOption(option);
//		mLocationClient.start();
		// ������λͼ��

		
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
		if(tmp.exists()){//������ֱ�Ӷ�ȡ��Ϣ

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
        // ���task����ɾ��task
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
			// ��λ��ʼ��
			mLocationClient = new LocationClient(this);
			mLocationClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// ��gps
			option.setCoorType("bd09ll"); // ������������
			option.setScanSpan(1000);
			mLocationClient.setLocOption(option);
			mLocationClient.start();

    		
    		placeLayout.setVisibility(View.GONE);
    		//���õ�ͼ�����¼����������õ����
    		OnMapLongClickListener listener = new OnMapLongClickListener() {  
    		    /** 
    		    * ��ͼ�����¼������ص����� 
    		    * @param point �����ĵ������� 
    		    */  
    		    public void onMapLongClick(LatLng point){  
    		    	clickedPoint = point;
    		    }  
    		};
    		mMapView.getMap().setOnMapLongClickListener(listener);
    		//���õ��������ص�ͼ��ͼ��ť
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
    		//�����ύ��ť��ȷ�������ص�ͼ�����µص㣬ȡ�������ص�ͼ
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
    				placeButton.setText("����ص㣨������ģ�");
    				if(clickedPoint != null){
    					latText.setText("���ȣ�" + clickedPoint.latitude);
    					lngText.setText("γ�ȣ�" + clickedPoint.longitude);
    				}
    			}
    		});
    		
    		isFirstLoc = true;
    		placeButton.setText("��ǰ��λ�ص㣨������ģ�");
    		
    		//���õ�ͼ��ͼΪ����
    		mMapView.setVisibility(View.GONE);
    		clickedPoint = null;
    		if(!mLocationClient.isStarted()){
    			mLocationClient.start();
    		}
			if (mLocationClient != null && mLocationClient.isStarted()){
				Log.v("mLocationClient", "requestLocation");
				mLocationClient.requestLocation();
			}
    		//latText.setText("���ȣ�" + nowLocation.latitude);
    		//lngText.setText("���ȣ�" + nowLocation.longitude);
    		//��ʾ�Ի���
        	new AlertDialog.Builder(this)
    		.setTitle("���õص�")
    		.setView(taskForm)
    		.setPositiveButton("Submit", new DialogInterface.OnClickListener()
    		{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// �½����ѵص�

				}
    		})
    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
    		{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// ʲôҲ����
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
	 * ��λSDK��������
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			Log.v("MyLocationListener", "in MyLocationListener");
			if (location == null || mMapView == null)
				return;
//			location.getAltitude();
//			MyLocationData locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
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
//	    		latText.setText("���ȣ�" + nowLocation.latitude);
//	    		lngText.setText("���ȣ�" + nowLocation.longitude);
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
