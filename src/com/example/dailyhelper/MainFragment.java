package com.example.dailyhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainFragment extends Fragment{
	
	private LocationManager locManager;
	
	private View layoutView;
	
	//各个控件
	private TextView statusText;
	private TextView promptText;
	private ListView taskList;
	
	//根据经纬度算距离的公式中常量
	private final double EARTH_RADIUS = 6378137.0;
	//跳转到新activity，添加新的提醒
	private Button addTask;
	
	//数据文件地址
	private final String PLACE_FILE = "place.bin";
	private final String PROMPT_FILE = "prompt.bin";
	private final String PLACE_TYPE_FILE = "place_type.bin";
	//数据list
	private List<Place> placeData = new ArrayList<Place>();
	private String[] placeItem = {"id", "longitud", "latitude", "placeType"};
	
	private List<PlaceType> placeTypeData = new ArrayList<PlaceType>();
	private String[] placeTypeItem = {"id", "name",	"radio", "prompt"};
	private String[][] placeTypeInitData = 
		{
			{"1", "home", "15", "在家里要好好休息喔~"},
			{"2", "classroom", "30", "上课要认真听讲呦~"}
		};
	
	private List<Prompt> promptData = new ArrayList<Prompt>();
	private String[] promptItem = {"id", "time", "promptText", "type"};
	private String[][] promptInitData = 
		{
			{"1", "9:00", "上午一杯谁有益健康", "daily"},
			{"2", "12:00", "该吃午饭了", "daily"}
		};
	
	//根据接收信息，更新显示条目
	private Handler mHandler = new Handler(){  
		  
         @Override  
         public void handleMessage(Message msg) {  
        	 int index = msg.what;
        	 if(msg.what != -1)
        	 {
        		 statusText.setText(placeTypeData.get(index).name);
        		 promptText.setText(placeTypeData.get(index).prompt);
        	 }
        	 else
        	 {
        		 statusText.setText("你在哪啊？请添加地点");
        		 promptText.setText("陌生地点或者路上请注意安全！");
        	 }
         }  
     };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.main_fragment, null); 
		initData();
		initList();
		return layoutView;
	}
	
	private void initList()
	{
		taskList = (ListView) layoutView.findViewById(R.id.task_list);
		updateListView();
		addTask = (Button) layoutView.findViewById(R.id.add_task);
	}
	
	private void updateListView()
	{
		
	}
	
	private void initData()
	{
		try {
			//初始化地点列表
			Log.v("1", "1");
			File tmp = new File(this.getActivity().getFilesDir().getPath().toString() + "/" + PLACE_FILE);
			if(tmp.exists()){//存在则直接读取信息
				FileInputStream fis = new FileInputStream(tmp);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String placeStr;
				while((placeStr = br.readLine())!=null)
				{
					String[] strs = placeStr.split(" ");
					placeData.add(new Place(strs));
				}
				fis.close();
			}
			else{//不存在则创建，并输入初始值
				tmp.createNewFile();
			}
			//初始化地点类型列表
			tmp = new File(this.getActivity().getFilesDir().getPath().toString() + "/" + PLACE_TYPE_FILE);
			if(tmp.exists()){//存在则直接读取信息
				Log.v("PLACE_TYPE_FILE", "exists");
				FileInputStream fis = new FileInputStream(tmp);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String placeStr;
				while((placeStr = br.readLine())!=null)
				{
					String[] strs = placeStr.split(" ");
					placeTypeData.add(new PlaceType(strs));
				}
				br.close();
				fis.close();
			}
			else{//不存在则创建，并输入初始值
				tmp.createNewFile();
				Log.v("PLACE_TYPE_FILE", "create");
				FileOutputStream fos = new FileOutputStream(tmp);
				for(int i=0; i<placeTypeInitData.length; i++)
				{
					placeTypeData.add(new PlaceType(placeTypeInitData[i]));
					String wr = placeTypeInitData[i][0]+" "+placeTypeInitData[i][1]+" "+placeTypeInitData[i][2]+" "+placeTypeInitData[i][3];
					wr+="\n";
					fos.write(wr.getBytes());
				}
				fos.close();
			}
			//初始化提醒
			tmp = new File(this.getActivity().getFilesDir().getPath().toString() + "/" + PROMPT_FILE);
			if(tmp.exists()){//存在则直接读取信息
				FileInputStream fis = new FileInputStream(tmp);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String placeStr;
				while((placeStr = br.readLine())!=null)
				{
					String[] strs = placeStr.split(" ");
					promptData.add(new Prompt(strs));
				}
				fis.close();
			}
			else{//不存在则创建，并输入初始值
				tmp.createNewFile();
				FileOutputStream fos = new FileOutputStream(tmp);
				for(int i=0; i<promptInitData.length; i++)
				{
					promptData.add(new Prompt(promptInitData[i]));
					String wr = promptInitData[i][0]+" "+promptInitData[i][1]+" "+promptInitData[i][2]+" "+promptInitData[i][3];
					wr+="\n";
					fos.write(wr.getBytes());
				}
				fos.close();
			}
			Collections.sort(promptData, new SortByTime());
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int getPlaceID()
	{//获得地点编号
		return 0;
	}
	
	private int getPlaceIDFromWifi()
	{//通过周围Wifi信号确定地点
		return 0;
	}
	
	private void getPlaceIDFromeGPS()
	{//通过GPS定位信息确定地点
		locManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		updateView(location);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 8, new LocationListener()
		{
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				updateView(location);
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				updateView(null);
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				updateView(locManager.getLastKnownLocation(provider));
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void updateView(Location location) {
		// TODO Auto-generated method stub
		if(location!=null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("实时的位置信息：\n");
			sb.append("经度：");
			sb.append(location.getLongitude());
			sb.append("\n纬度：");
			sb.append(location.getLatitude());
			Toast.makeText(this.getActivity(), sb.toString(), Toast.LENGTH_LONG).show();
			//找到所处地点
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			for(int i=0; i<placeData.size(); i++)
			{
				double radio=0.0;
				int index = 0;
				for(int j=0; j<placeTypeData.size(); j++)
				{
					if(placeData.get(i).placeType.equals(placeTypeData.get(j).id))
					{
						radio = Double.parseDouble(placeTypeData.get(j).radio);
						index = j;
						break;
					}
				}
				if(gps2m(longitude, latitude, Double.parseDouble(placeData.get(i).longitud),
						Double.parseDouble(placeData.get(i).latitude))<=radio)
				{//匹配到输入地点，跟新控件
					mHandler.sendEmptyMessage(index);
					return;
				}
			}
			mHandler.sendEmptyMessage(-1);
		}
		else
		{
			Toast.makeText(this.getActivity(), "暂时无法获得定位信息！", Toast.LENGTH_LONG).show();
		}
	}
	
	private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
	       double radLat1 = (lat_a * Math.PI / 180.0);
	       double radLat2 = (lat_b * Math.PI / 180.0);
	       double a = radLat1 - radLat2;
	       double b = (lng_a - lng_b) * Math.PI / 180.0;
	       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
	              + Math.cos(radLat1) * Math.cos(radLat2)
	              * Math.pow(Math.sin(b / 2), 2)));
	       s = s * EARTH_RADIUS;
	       s = Math.round(s * 10000) / 10000;
	       return s;
	    }
	
	class Place
	{
		public String id;
		public String longitud;
		public String latitude;
		public String placeType;
		
		public Place(String i, String lo, String la, String pl)
		{
			id = i;
			longitud = lo;
			latitude = la;
			placeType = pl;
		}
		
		public Place(String[] i)
		{
			id = i[0];
			longitud = i[1];
			latitude = i[2];
			placeType = i[3];
		}
	}
	
	class PlaceType
	{
		public String id;
		public String name;
		public String radio;
		public String prompt;
		
		public PlaceType(String i, String na, String ra, String pr)
		{
			id = i;
			name = na;
			radio = ra;
			prompt = pr;
		}
		
		public PlaceType(String[] i)
		{
			id = i[0];
			name = i[1];
			radio = i[2];
			prompt = i[3];
		}
	}
	
	public class Prompt
	{
		public String id;
		public String time;
		public String promptText;
		public String type;
		
		public Prompt(String i, String ti, String pr, String ty)
		{
			id = i;
			time = ti;
			promptText = pr;
			type = ty;
		}
		
		public Prompt(String i[])
		{
			id = i[0];
			time = i[1];
			promptText = i[2];
			type = i[3];
		}
	}
	
	class SortByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			Prompt a = (Prompt)o1;
			Prompt b = (Prompt)o2;
			String[] timeA = a.time.split(":");
			String[] timeB = b.time.split(":");
			Log.v("timeA", timeA[0]);
			Log.v("timeB", timeB[0]);
			if (Integer.parseInt(timeA[0])>Integer.parseInt(timeB[0]))
				return 1;
			else if (Integer.parseInt(timeA[0])<Integer.parseInt(timeB[0]))
				return -1;
			else{
				if(Integer.parseInt(timeA[1])>Integer.parseInt(timeB[1]))
					return 1;
				else if(Integer.parseInt(timeA[1])<Integer.parseInt(timeB[1]))
					return -1;
				else
					return 0;
			}
		}
	}
}
