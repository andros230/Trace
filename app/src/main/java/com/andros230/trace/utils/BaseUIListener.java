package com.andros230.trace.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.andros230.trace.activity.Login;
import com.andros230.trace.activity.MainActivity;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BaseUIListener implements IUiListener {
    private Context context;
    private String TAG = "BaseUIListener";
    private String openID;

    public BaseUIListener(Context context) {
        this.context = context;
    }

    @Override
    public void onComplete(Object response) {
        doComplete((JSONObject) response);
        JSONObject json = (JSONObject) response;
        try {
            if (json.getInt("ret") == 0) {
                String token = json.getString(Constants.PARAM_ACCESS_TOKEN);
                String expires = json.getString(Constants.PARAM_EXPIRES_IN);
                openID = json.getString(Constants.PARAM_OPEN_ID);
                Logs.d("---token", token);
                Logs.d("---expires", expires);
                Logs.d("---openID", openID);

                //上传openID
                Map<String, String> params = new HashMap<>();
                params.put("openID", openID);
                String uid = util.readUid(context);
                params.put("uid", uid);
                String md5 = util.readMD5(context);
                params.put("md5", md5);
                new VolleyPost(context, util.ServerUrl + "SaveOpenID", params, new VolleyCallBack() {
                    @Override
                    public void volleyResult(String result) {

                        if (result != null) {
                            util.writeOpenID(context, openID);
                            //保存UID
                            util.writeUid(context, result);
                            //删除旧数据
                            new DbOpenHelper(context).dropTable();
                            Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
                            Logs.d(TAG, "QQ登录成功");

                            Intent intent = new Intent();
                            intent.setClass(context, MainActivity.class);
                            context.startActivity(intent);
                            new Login().close();
                        } else {
                            Toast.makeText(context, "网络异常,请检查网络", Toast.LENGTH_LONG).show();
                            Logs.e(TAG, "网络异常, saveOpenID 上传失败");
                        }

                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    protected void doComplete(JSONObject values) {
    }

    @Override
    public void onError(UiError e) {
    }

    @Override
    public void onCancel() {
    }


}