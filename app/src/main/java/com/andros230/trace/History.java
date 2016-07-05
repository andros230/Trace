package com.andros230.trace;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.utils.MapUtil;

import java.util.ArrayList;
import java.util.List;

public class History extends Activity {
    private MapView mMapView;
    private AMap aMap;
    private ListView list;
    private DbOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mMapView = (MapView) findViewById(R.id.history_map);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        db = new DbOpenHelper(this);

        list = (ListView) findViewById(R.id.history_listView);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,getData()));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new MapUtil(aMap).ShowTraceThread(db,getData().get(i));
            }
        });
    }

    public List<String> getData(){
        DbOpenHelper db = new DbOpenHelper(this);
        Cursor cur = db.queryHistory();
        List<String> data = new ArrayList<>();
        while (cur.moveToNext()){
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
