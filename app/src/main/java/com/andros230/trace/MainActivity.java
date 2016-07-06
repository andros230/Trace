package com.andros230.trace;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.andros230.trace.bean.LatLngKit;
import com.andros230.trace.bmob.BmobDao;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.MapUtil;
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
    private TextView tv_lat, tv_lng, tv_provider, tv_accuracy, tv_status;

    private Chronometer chronometer;
    boolean bool = true;
    private List<LatLng> latLngList;
    private BmobDao bmobDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.main_map);
        mMapView.onCreate(savedInstanceState);

        init();
        AlarmCPU();
    }


    public void History(View view) {
        Intent intent = new Intent();
        intent.setClass(this, History.class);
        startActivity(intent);
    }

    private void init() {
        tv_lat = (TextView) findViewById(R.id.lat);
        tv_lng = (TextView) findViewById(R.id.lng);
        tv_provider = (TextView) findViewById(R.id.provider);
        tv_accuracy = (TextView) findViewById(R.id.accuracy);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        tv_status = (TextView) findViewById(R.id.tv_status);
        chronometer.start();


        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setLocationSource(this);  //设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);  //设置默认定位按键是否显示
            aMap.setMyLocationEnabled(true); // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);  // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种//
        }

        db = new DbOpenHelper(this);
        latLngList = new ArrayList();
        bmobDao = new BmobDao(db);
    }


    private double temp_lat;
    private double temp_lng;

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);  // 显示系统小蓝点
                chronometer.setBase(SystemClock.elapsedRealtime());
                double lat = aMapLocation.getLatitude();
                double lng = aMapLocation.getLongitude();
                tv_status.setText("");

                if (temp_lat != lat && temp_lng != lng) {
                    tv_lat.setText(lat + "");
                    tv_lng.setText(lng + "");
                    tv_accuracy.setText(aMapLocation.getAccuracy() + "");
                    tv_provider.setText(aMapLocation.getProvider());

                    if (aMapLocation.getAccuracy() < 50) {
                        LatLngKit kit = new LatLngKit();
                        kit.setLat(lat + "");
                        kit.setLng(lng + "");
                        kit.setDate(util.getNowTime(false));
                        kit.setTime(util.getNowTime(true));
                        //保存到服务器
                        bmobDao.save(kit);
                        //绘制路线
                        drawLine(lat, lng);
                    }
                    temp_lat = lat;
                    temp_lng = lng;
                } else {
                    Logs.d(TAG, "坐标没变动");
                }

            } else {
                tv_status.setText("定位失败," + aMapLocation.getErrorInfo());
                Logs.e(TAG, "定位失败,错误代码;" + aMapLocation.getErrorCode() + ",错误信息:" + aMapLocation.getErrorInfo());
            }
        }
    }

    boolean lineBool = true;

    public void drawLine(double lat, double lng) {
        latLngList.add(new LatLng(lat, lng));
        if (bool) {
            if (lineBool) {
                new MapUtil(this, aMap, false).ShowTraceThread(db, util.getNowTime(false));
                lineBool = false;
            } else {
                List<PolylineOptions> list = MapUtil.lineList;
                if (list != null) {
                    aMap.clear(true);
                    for (int i = 0; i < list.size(); i++) {
                        aMap.addPolyline(list.get(i));
                    }
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.width(20);
                    polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
                    for (int i = 0; i < latLngList.size(); i++) {
                        polylineOptions.add(latLngList.get(i));
                    }
                    aMap.addPolyline(polylineOptions);
                }
            }
        }
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
        Logs.d(TAG, "onResume");
        super.onResume();
        mMapView.onResume();
        bool = true;
    }

    @Override
    protected void onPause() {
        Logs.d(TAG, "onPause");
        super.onPause();
        mMapView.onPause();
        bool = false;
    }

    @Override
    protected void onDestroy() {
        Logs.d(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
        if (mLocationClient == null) {
            mLocationClient.onDestroy();
        }
        bmobDao.insertBatch();
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
