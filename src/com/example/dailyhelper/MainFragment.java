package com.example.dailyhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainFragment extends Fragment{
	
	private LocationManager locManager;
	
	private View layoutView;
	
	//�����ļ���ַ
	private final String PLACE_FILE = "place.bin";
	private final String PROMPT_FILE = "prompt.bin";
	private final String PLACE_TYPE_FILE = "place_type.bin";
	//����list
	private List<Place> placeData = new ArrayList<Place>();
	private String[] placeItem = {"id", "longitud", "latitude", "placeType"};
	
	private List<PlaceType> placeTypeData = new ArrayList<PlaceType>();
	private String[] placeTypeItem = {"id", "name",	"radio", "prompt"};
	private String[][] placeTypeInitData = 
		{
			{"1", "home", "15", "�ڼ���Ҫ�ú���Ϣ�~"},
			{"2", "classroom", "30", "�Ͽ�Ҫ����������~"}
		};
	
	private List<Prompt> promptData = new ArrayList<Prompt>();
	
	private void initData()
	{
		try {
			//��ʼ���ص��б�
			Log.v("1", "1");
			File tmp = new File(this.getActivity().getFilesDir().getPath().toString() + "/" + PLACE_FILE);
			if(tmp.exists()){//������ֱ�Ӷ�ȡ��Ϣ
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
			else{//�������򴴽����������ʼֵ
				tmp.createNewFile();
			}
			//��ʼ���ص������б�
			tmp = new File(this.getActivity().getFilesDir().getPath().toString() + "/" + PLACE_TYPE_FILE);
			if(tmp.exists()){//������ֱ�Ӷ�ȡ��Ϣ
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
			else{//�������򴴽����������ʼֵ
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
			//��ʼ������
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.main_fragment, null); 
		initData();
		return layoutView;
	}
	
	private int getPlaceID()
	{//��õص���
		return 0;
	}
	
	private int getPlaceIDFromWifi()
	{//ͨ����ΧWifi�ź�ȷ���ص�
		return 0;
	}
	
	private int getPlaceIDFromeGPS()
	{//ͨ��GPS��λ��Ϣȷ���ص�
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
		return 0;
	}
	
	private void updateView(Location location) {
		// TODO Auto-generated method stub
		if(location!=null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("ʵʱ��λ����Ϣ��\n");
			sb.append("���ȣ�");
			sb.append(location.getLongitude());
			sb.append("\nγ�ȣ�");
			sb.append(location.getLatitude());
			Toast.makeText(this.getActivity(), sb.toString(), Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(this.getActivity(), "ò�Ƴ������ˣ�", Toast.LENGTH_LONG).show();
		}
	}

	private void readAllPlaces()
	{//��ȡ���еĵص���Ϣ
		
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
}
