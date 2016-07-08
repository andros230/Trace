package com.andros230.trace.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class VolleyPost2 {
    private String TAG = "VolleyPost2";
    private Context context;
    private VolleyCallBack2 callBack2;
    private Map<String, String> params;
    private String url;

    //Map<String, String> params = new HashMap<>();
    //params.put("way", "way");
    public VolleyPost2(Context context, VolleyCallBack2 callBack2, String url, Map<String, String> params) {
        this.context = context;
        this.callBack2 = callBack2;
        this.params = params;
        this.url = url;
    }

    public void post() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 返回数据
                Log.i(TAG, response);
                callBack2.volleySolve2(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                callBack2.volleySolve2(null);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

}
