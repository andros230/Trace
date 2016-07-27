package com.andros230.trace.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.andros230.trace.R;
import com.andros230.trace.bean.LatLngKit;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements LocationSource, AMapLocationListener {
    private MapView mMapView;
    private AMap aMap;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationClientOption;

    private String TAG = "MainActivity";
    private String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");

        mMapView = (MapView) findViewById(R.id.main_map);
        mMapView.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
            aMap.setLocationSource(this);  //设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);  //设置默认定位按键是否显示
            aMap.setMyLocationEnabled(true); // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);  // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种//
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);  // 显示系统小蓝点
                double lat = aMapLocation.getLatitude();
                double lng = aMapLocation.getLongitude();
                double acc = aMapLocation.getAccuracy();

                Map<String, String> params = new HashMap<>();
                params.put("uid", util.readUid(this));
                params.put("groupID", groupID);
                params.put("lat", lat + "");
                params.put("lng", lng + "");
                params.put("acc", acc + "");
                new VolleyPost(this, util.ServerUrl + "SaveLatLng", params, new VolleyCallBack() {
                    @Override
                    public void volleyResult(String result) {
                        if (result != null) {
                            aMap.clear(true);
                            Gson gson = new Gson();
                            List<LatLngKit> kits = gson.fromJson(result, new TypeToken<List<LatLngKit>>() {
                            }.getType());
                            for (int i = 0; i < kits.size(); i++) {
                                LatLngKit kit = kits.get(i);
                                double rs_lat = Double.valueOf(kit.getLat());
                                double rs_lng = Double.valueOf(kit.getLng());
                                int rs_acc = Integer.parseInt(kit.getAcc());

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(rs_lat, rs_lng));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                aMap.addMarker(markerOptions);

                                // 绘制一个圆形
                                Circle circle = aMap.addCircle(new CircleOptions().center(new LatLng(rs_lat, rs_lng)).radius(rs_acc).strokeColor(Color.argb(50, 1, 1, 1)).fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(25));
                                circle.setStrokeWidth(1);
                            }

                        } else {
                            Logs.e(TAG, "result is null");
                        }
                    }
                });

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
            mLocationClientOption.setInterval(1000 * 5);
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
        super.onResume();
        mMapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }

}
