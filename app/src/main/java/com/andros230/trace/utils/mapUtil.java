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
import com.andros230.trace.dao.DbOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MapUtil {
    private AMap aMap;
    private DbOpenHelper db;
    private String day;
    ProgressDialog dialog;
    private Context context;
    private boolean bool;
    public static List<PolylineOptions> lineList = null;

    public MapUtil(Context context, AMap aMap, boolean bool) {
        this.context = context;
        this.aMap = aMap;
        this.bool = bool;
    }

    public void ShowTraceThread(DbOpenHelper db, String day) {
        if (bool) {
            dialog = ProgressDialog.show(context, "加载路线中", "加载中,请稍后...", false);
        }
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
        List<PolylineOptions> list = new ArrayList();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(20);
        polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
        double lat = 0;
        double lng = 0;
        while (cur.moveToNext()) {
            lat = cur.getDouble(1);
            lng = cur.getDouble(2);
            String date = cur.getString(3);
            String time = cur.getString(4);
            if (util.compareTime(time2, date + " " + time)) {
                polylineOptions.add(new LatLng(lat, lng));
                time2 = date + " " + time;
            } else {
                list.add(polylineOptions);
                polylineOptions = new PolylineOptions();
                polylineOptions.width(20);
                polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
                time2 = null;
            }
        }
        if (bool) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
        }
        list.add(polylineOptions);
        lineList = list;
        drawLine(list);
    }

    public void drawLine(List<PolylineOptions> list) {
        aMap.clear(true);
        for (int i = 0; i < list.size(); i++) {
            aMap.addPolyline(list.get(i));
        }
        if (bool) {
            dialog.dismiss();
        }
    }


}
