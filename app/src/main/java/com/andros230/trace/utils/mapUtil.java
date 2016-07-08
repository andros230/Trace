package com.andros230.trace.utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.andros230.trace.R;
import com.andros230.trace.bean.LatLngKit;
import com.andros230.trace.dao.DbOpenHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MapUtil {
    private AMap aMap;
    private DbOpenHelper db;
    ProgressDialog dialog;
    private Context context;
    public static List<PolylineOptions> MainActivity_lineList = null;
    private String TAG = "MapUtil";

    public MapUtil(Context context, AMap aMap) {
        this.context = context;
        this.aMap = aMap;
    }

    public void ShowTraceThread(DbOpenHelper db) {
        this.db = db;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShowTrace();
            }
        }).start();
    }

    private void ShowTrace() {
        Cursor cur = db.query();
        String time2 = null;
        List<PolylineOptions> list = new ArrayList();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(20);
        polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
        double lat = 0;
        double lng = 0;
        while (cur.moveToNext()) {
            lat = cur.getDouble(1);
            lng = cur.getDouble(2);
            String time = cur.getString(4);
            if (util.compareTime(time2, time)) {
                polylineOptions.add(new LatLng(lat, lng));
                time2 = time;
            } else {
                Logs.d(TAG, "ShowTrace  new  PolylineOptions");
                list.add(polylineOptions);
                polylineOptions = new PolylineOptions();
                polylineOptions.width(20);
                polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
                time2 = null;
            }
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
        list.add(polylineOptions);
        MainActivity_lineList = list;
        drawLine(list);
    }


    public void showHistoryTraceThread(final String json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                showHistoryTrace(json);
            }
        }).start();
    }

    public void showHistoryTrace(String json) {
        String time2 = null;
        List<PolylineOptions> list = new ArrayList();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(20);
        polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
        double lat = 0;
        double lng = 0;
        Gson gson = new Gson();
        List<LatLngKit> kits = gson.fromJson(json, new TypeToken<List<LatLngKit>>() {
        }.getType());
        for (LatLngKit kit : kits) {
            String time = kit.getTime();
            lat = Double.valueOf(kit.getLat());

            lng = Double.valueOf(kit.getLng());
            if (util.compareTime(time2, time)) {
                polylineOptions.add(new LatLng(lat, lng));
                time2 = time;
            } else {
                Logs.d(TAG, "ShowHistoryTrace  new  PolylineOptions");
                list.add(polylineOptions);
                polylineOptions = new PolylineOptions();
                polylineOptions.width(20);
                polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
                time2 = null;
            }
        }

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
        list.add(polylineOptions);
        drawLine(list);
    }

    public void drawLine(List<PolylineOptions> list) {
        aMap.clear(true);
        for (int i = 0; i < list.size(); i++) {
            Logs.d(TAG, "---drawLine");
            aMap.addPolyline(list.get(i));
        }
    }


}
