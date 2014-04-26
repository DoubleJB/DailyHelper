package com.example.dailyhelper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainFragment extends Fragment{
	
	private LocationManager locManager;
	
	private View layoutView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.main_fragment, null); 
		
		return layoutView;
	}
	
	private int getPlaceID()
	{//获得地点编号
		return 0;
	}
	
	private int getPlaceIDFromWifi()
	{//通过周围Wifi信号确定地点
		return 0;
	}
	
	private int getPlaceIDFromeGPS()
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
		return 0;
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
		}
		else
		{
			Toast.makeText(this.getActivity(), "貌似出问题了！", Toast.LENGTH_LONG).show();
		}
	}

	private void readAllPlaces()
	{//读取所有的地点信息
		
	}
}
