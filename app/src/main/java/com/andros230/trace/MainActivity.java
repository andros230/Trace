package com.andros230.trace;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.andros230.trace.bean.LatLngKit;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.utils.util;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements LocationSource, AMapLocationListener {
    private MapView mMapView;
    private AMap aMap;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationClientOption;

    private String TAG = "MainActivity";
    private DbOpenHelper db;
    private TextView tv_lat, tv_lng, tv_provider, tv_accuracy;
    boolean aMapLocationBool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.main_map);
        mMapView.onCreate(savedInstanceState);
        init();
        AlarmCPU();

    }

    private void init() {
        tv_lat = (TextView) findViewById(R.id.lat);
        tv_lng = (TextView) findViewById(R.id.lng);
        tv_provider = (TextView) findViewById(R.id.provider);
        tv_accuracy = (TextView) findViewById(R.id.accuracy);

        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setLocationSource(this);  //设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);  //设置默认定位按键是否显示
            aMap.setMyLocationEnabled(true); // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);  // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种//
            //SQLite
            db = new DbOpenHelper(this);

        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);  // 显示系统小蓝点

                double lat = aMapLocation.getLatitude();
                double lng = aMapLocation.getLongitude();

                if (aMapLocationBool) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
                    aMapLocationBool = false;
                }

                tv_lat.setText(lat + "");
                tv_lng.setText(lng + "");
                tv_accuracy.setText(aMapLocation.getAccuracy() + "");
                tv_provider.setText(aMapLocation.getProvider());

                if (aMapLocation.getAccuracy() < 50) {
                    LatLngKit latLng = new LatLngKit();
                    latLng.setLat(lat + "");
                    latLng.setLng(lng + "");
                    db.insert(latLng);
                    ShowTraceThread();
                }
            } else {
                Log.e(TAG, "定位失败,错误代码;" + aMapLocation.getErrorCode() + ",错误信息:" + aMapLocation.getErrorInfo());
            }
        }
    }

    public void ShowTraceThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShowTrace();
            }
        }).start();
    }


    private void ShowTrace() {

        Cursor cur = db.query();
        boolean bool = true;
        String time2 = null;
        List list = new ArrayList();
        aMap.clear(true);
        while (cur.moveToNext()) {
            double lat = cur.getDouble(1);
            double lng = cur.getDouble(2);
            String time = cur.getString(3);

            if (util.compareTime(time2, time)) {
                //绘制路线
                list.add(new LatLng(lat, lng));
                time2 = time;
            } else {
                drawLine(list);
                list = new ArrayList();
                time2 = null;
            }

        }
        drawLine(list);
    }

    public void drawLine(List list) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(20);
        polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
        for (int i = 0; i < list.size(); i++) {
            polylineOptions.add((LatLng) list.get(i));
        }
        aMap.addPolyline(polylineOptions);
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationClientOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //定位时间间隔
            mLocationClientOption.setInterval(1000 * 2);
            mLocationClient.setLocationOption(mLocationClientOption);
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        mMapView.onPause();

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
        if (mLocationClient == null) {
            mLocationClient.onDestroy();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //alarmManager可叫醒CPU,保证关闭屏后还可定位
    public void AlarmCPU() {
        Intent intentRepeat = new Intent(this, MainActivity.class);
        PendingIntent sender = PendingIntent.getService(this, 0, intentRepeat, 0);
        long triggerTime = SystemClock.elapsedRealtime() + 1000; // 第一次时间
        long intervalTime = 1000; // ms
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, intervalTime, sender);
    }
}
