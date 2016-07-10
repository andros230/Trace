package com.andros230.trace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.andros230.trace.R;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyCallBack2;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.network.VolleyPost2;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.MapUtil;
import com.andros230.trace.utils.util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History extends Activity implements VolleyCallBack, VolleyCallBack2 {
    private MapView mMapView;
    private AMap aMap;
    private Spinner spinner;
    private String TAG = "History";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mMapView = (MapView) findViewById(R.id.history_map);
        mMapView.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        }
        spinner = (Spinner) findViewById(R.id.history_spinner);

        Map<String, String> params = new HashMap<>();
        params.put("uid", util.readUid(this));
        new VolleyPost(this, this, util.ServerUrl + "History", params).post();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String date = (String) adapterView.getItemAtPosition(i);
                Map<String, String> params = new HashMap<>();
                params.put("uid", util.readUid(History.this));
                params.put("date", date);
                new VolleyPost2(History.this, History.this, util.ServerUrl + "HistoryTrace", params).post();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    @Override
    public void volleySolve(String result) {
        if (result != null) {
            Gson gson = new Gson();
            List<Map<String, Object>> list = gson.fromJson(result, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            History_adapter adapter = new History_adapter(this, list);
            spinner.setAdapter(adapter);
            spinner.setSelection(list.size() - 1, true);
        } else {
            Logs.e(TAG, "网络异常");
        }

    }

    @Override
    public void volleySolve2(String result) {
        Logs.d(TAG, result);
        if (result != null) {
            new MapUtil(this, aMap).showHistoryTraceThread(result);
        } else {
            Logs.e(TAG, "网络异常");
        }
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
