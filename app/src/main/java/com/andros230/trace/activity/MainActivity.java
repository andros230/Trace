package com.andros230.trace.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.andros230.trace.R;
import com.andros230.trace.bean.LatLngKit;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.MapUtil;
import com.andros230.trace.utils.util;
import com.google.gson.Gson;

import java.util.ArrayList;
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
    private DbOpenHelper db;
    private TextView tv_lat, tv_lng, tv_provider, tv_accuracy, tv_status;

    private Chronometer chronometer;
    boolean bool = true;
    private List<LatLng> latLngList;
    private PopupWindow mPopWindow;

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
        showPopupWindow();
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
            aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
            aMap.setLocationSource(this);  //设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);  //设置默认定位按键是否显示
            aMap.setMyLocationEnabled(true); // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);  // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种//
        }

        db = new DbOpenHelper(this);
        latLngList = new ArrayList();
    }


    private double temp_lat;
    private double temp_lng;

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);  // 显示系统小蓝点
                double lat = aMapLocation.getLatitude();
                double lng = aMapLocation.getLongitude();

                if (bool) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    tv_status.setTextColor(Color.DKGRAY);
                    tv_status.setText("记录轨迹中");
                    tv_lat.setText(lat + "");
                    tv_lng.setText(lng + "");
                    tv_accuracy.setText(aMapLocation.getAccuracy() + "米");
                    tv_provider.setText(aMapLocation.getProvider());
                }


                if (aMapLocation.getAccuracy() < 50) {
                    if (temp_lat != lat && temp_lng != lng) {
                        LatLngKit kit = new LatLngKit();
                        kit.setLat(lat + "");
                        kit.setLng(lng + "");
                        kit.setDate(util.getNowTime(false));
                        kit.setTime(util.getNowTime(true));
                        db.saveLatLng(kit);
                        //绘制路线
                        drawLine(kit);
                        temp_lat = lat;
                        temp_lng = lng;
                    } else {
                        Logs.d(TAG, "坐标没变动");
                    }

                } else {
                    tv_status.setTextColor(Color.RED);
                    tv_status.setText("定位误差过大");
                }


            } else {
                tv_status.setTextColor(Color.RED);
                tv_status.setText("定位失败");
                Logs.e(TAG, "定位失败,错误代码;" + aMapLocation.getErrorCode() + ",错误信息:" + aMapLocation.getErrorInfo());
            }
        }
    }

    boolean lineBool = true;

    public void drawLine(LatLngKit kit) {
        if (kit != null) {
            latLngList.add(new LatLng(Double.valueOf(kit.getLat()), Double.valueOf(kit.getLng())));
        }
        if (bool) {
            if (lineBool) {
                new MapUtil(this, aMap).ShowTraceThread(db);
                lineBool = false;
            } else {
                List<PolylineOptions> list = MapUtil.MainActivity_lineList;
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
        Logs.d(TAG, "onResume");
        super.onResume();
        mMapView.onResume();
        //绘制路线
        bool = true;
        drawLine(null);
        updateDate();
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
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
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

    public void updateDate() {
        String uid = util.readUid(this);
        Cursor cur = db.updateDataToServer();
        List<LatLngKit> kits = new ArrayList<>();
        while (cur.moveToNext()) {
            String lat = cur.getString(1);
            String lng = cur.getString(2);
            String date = cur.getString(3);
            String time = cur.getString(4);

            LatLngKit kit = new LatLngKit();
            kit.setLat(lat);
            kit.setLng(lng);
            kit.setDate(date);
            kit.setTime(time);
            kit.setUid(uid);
            kits.add(kit);
        }

        if (kits.size() != 0) {
            Gson gson = new Gson();
            String json = gson.toJson(kits);
            Map<String, String> params = new HashMap<>();
            params.put("json", json);
            new VolleyPost(this, util.ServerUrl + "SaveLatLng", params, new VolleyCallBack() {
                @Override
                public void volleyResult(String result) {
                    if (result != null) {

                        if (result.equals("YES")) {
                            db.changeStatus();
                        }

                    } else {
                        Logs.e(TAG, "网络异常,上传同步数据失败");
                    }
                }
            });

        }
    }


    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_layout, null);
        mPopWindow = new PopupWindow(contentView);
        mPopWindow.setFocusable(true);


        ListView list = (ListView) contentView.findViewById(R.id.listView);
        popup_adapter adapter = new popup_adapter(this, getdata());
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("----", getdata().get(i));
                mPopWindow.dismiss();
                if (getdata().get(i).equals("历史轨迹")) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, History.class);
                    startActivity(intent);
                } else if (getdata().get(i).equals("退出帐号")) {
                    mPopWindow.dismiss();
                    dialog();
                } else if (getdata().get(i).equals("意见反馈")) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Feedback.class);
                    startActivity(intent);
                }
            }
        });

        list.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopWindow.setWidth(list.getMeasuredWidth());
        mPopWindow.setHeight((ViewGroup.LayoutParams.WRAP_CONTENT));
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(0));

        mPopWindow.setContentView(contentView);
        //显示PopupWindow
        View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);


    }

    public List<String> getdata() {
        List<String> data = new ArrayList<>();
        data.add("历史轨迹");
        data.add("意见反馈");
        String openID = util.readOpenID(getApplicationContext());
        if (openID != null) {
            data.add("退出帐号");
        }
        return data;
    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("确定退出当前帐号？");

        builder.setTitle("退出帐号");

        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                util.Logout(getApplicationContext());
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Login.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
