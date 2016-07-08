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

public class VolleyPost {
    private String TAG = "VolleyPost";
    private Context context;
    private VolleyCallBack callBack;
    private Map<String, String> params;
    private String url;

    //Map<String, String> params = new HashMap<>();
    //params.put("way", "way");
    public VolleyPost(Context context, VolleyCallBack callBack,String url ,Map<String, String> params) {
        this.context = context;
        this.callBack = callBack;
        this.params = params;
        this.url = url;
    }

    public void post() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 返回数据
                Log.i(TAG, response);
                callBack.volleySolve(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                callBack.volleySolve(null);
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
