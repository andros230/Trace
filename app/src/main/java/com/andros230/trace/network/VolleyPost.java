package com.andros230.trace.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.andros230.trace.utils.Logs;

import java.util.Map;

public class VolleyPost {
    private String TAG = "VolleyPost";

    //Map<String, String> params = new HashMap<>();
    //params.put("way", "way");
    public VolleyPost(Context context, String url, final Map<String, String> params, final VolleyCallBack callBack) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 返回数据
                Log.i(TAG, response);
                callBack.volleyResult(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                Logs.e(TAG, error.toString());
                callBack.volleyResult(null);
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



