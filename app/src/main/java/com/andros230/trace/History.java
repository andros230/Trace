package com.andros230.trace;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.utils.MapUtil;

import java.util.ArrayList;
import java.util.List;

public class History extends Activity {
    private MapView mMapView;
    private AMap aMap;
    private DbOpenHelper db;

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
        db = new DbOpenHelper(this);
        Spinner spinner = (Spinner) findViewById(R.id.history_spinner);

        History_adapter adapter = new History_adapter(this, getData());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String day = (String) adapterView.getItemAtPosition(i);
                new MapUtil(History.this, aMap, true).ShowTraceThread(db, day);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public List<String> getData() {
        DbOpenHelper db = new DbOpenHelper(this);
        Cursor cur = db.queryHistory();
        List<String> data = new ArrayList<>();
        while (cur.moveToNext()) {
            data.add(cur.getString(0));
        }
        return data;
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
