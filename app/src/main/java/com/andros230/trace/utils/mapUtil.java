package com.andros230.trace.utils;


import android.app.ProgressDialog;
import android.database.Cursor;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.andros230.trace.R;
import com.andros230.trace.dao.DbOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MapUtil {
    private AMap aMap;
    private DbOpenHelper db;
    private String day;

    public MapUtil(AMap aMap) {
        this.aMap = aMap;
    }

    public void ShowTraceThread(DbOpenHelper db, String day) {
        this.db = db;
        this.day = day;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShowTrace();
            }
        }).start();
    }

    private void ShowTrace() {
        Cursor cur = db.query(day);
        String time2 = null;
        List list = new ArrayList();
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
                list.add(polylineOptions);
                polylineOptions = new PolylineOptions();
                polylineOptions.width(20);
                polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
                time2 = null;
            }
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
        list.add(polylineOptions);
        drawLine(list);
    }

    public void drawLine(List list) {
        aMap.clear(true);
        for (int i = 0; i < list.size(); i++) {
            aMap.addPolyline((PolylineOptions) list.get(i));
        }
    }
}
