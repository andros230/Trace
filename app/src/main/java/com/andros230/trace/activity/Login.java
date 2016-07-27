package com.andros230.trace.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andros230.trace.R;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Login extends Activity {
    private String TAG = "Login";
    private String APP_KEY = "305471104";
    private String REDIRECT_URL = "http://www.sina.com";
    private String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private String openID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void weibo_Button(View view) {
        AuthInfo mAuthInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
        mSsoHandler = new SsoHandler(Login.this, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

    public void qq_button(View view) {
        Tencent mTencent = Tencent.createInstance("1105460525", this.getApplicationContext());
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", new BaseUIListener(this));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //qq
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUIListener(this));
        //weibo
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    //微博认证授权回调类
    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                openID = mAccessToken.getUid();
                saveOpenID(openID);
            } else {
                Toast.makeText(Login.this, "授权失败,code：" + values.getString("code"), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }

        @Override
        public void onCancel() {
        }
    }


    //QQ帐号登录授权回调类
    class BaseUIListener implements IUiListener {
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
                    openID = json.getString(Constants.PARAM_OPEN_ID);
                    saveOpenID(openID);
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


    public void saveOpenID(String openID) {
        String md5 = util.createMD5(Login.this);
        //上传openID
        Map<String, String> params = new HashMap<>();
        params.put("openID", openID);
        params.put("md5", md5);

        new VolleyPost(getApplicationContext(), util.ServerUrl + "SaveOpenID", params, new VolleyCallBack() {
            @Override
            public void volleyResult(String result) {
                if (result != null) {
                    //保存UID
                    util.writeUid(getApplicationContext(), result);
                    Logs.d(TAG, util.readUid(getApplicationContext()));
                    Toast.makeText(Login.this, "帐号登录成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(Login.this, GroupList.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "网络异常,请检查网络", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}



