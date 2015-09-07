package com.example.mysafer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

    private MyLocationListener mli;
    private LocationManager lm;
    private SharedPreferences sp;


    class MyLocationListener implements LocationListener {
        //位置发生变化
        @Override
        public void onLocationChanged(Location location) {
            //获取经纬度保存在sp中
            String longitude = "" + location.getLongitude();
            String laitude = "" + location.getLatitude();
            sp.edit().putString("location", "经度:"+longitude+";纬度:"+laitude).commit();
            //每次获取一次经纬度，就把服务关闭
            stopSelf();
        }

        //位置提供者状态发生变化
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("位置提供者状态发生变化");
        }

        //位置提供者被打开
        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("位置提供者被打开");
        }

        //位置提供者被关闭
        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("位置提供者被关闭");
        }
    }

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("定位服务开启...");
        sp = getSharedPreferences("config", MODE_PRIVATE);

        mli = new MyLocationListener();
        lm = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        //获取目前最佳的位置提供者
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String best = lm.getBestProvider(criteria, true);

        lm.requestLocationUpdates(best, 0, 0, mli);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("定位服务销毁...");
        lm.removeUpdates(mli);
    }
}
