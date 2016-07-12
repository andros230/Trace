package com.andros230.trace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andros230.trace.R;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.BaseUIListener;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.Tencent;

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
        registerUser();

    }


    public void close() {
        finish();
        System.exit(0);
        Logs.d(TAG, "关闭登录页");
    }

    public void registerUser() {
        String uid = util.readUid(this);
        if (uid == null) {
            Logs.d(TAG, "新用户");
            String md5 = util.createMD5(this);
            util.writeMD5(this, md5);
            Map<String, String> params = new HashMap<>();
            params.put("md5", md5);

            new VolleyPost(this, util.ServerUrl + "newUser", params, new VolleyCallBack() {
                @Override
                public void volleyResult(String result) {
                    if (result != null) {
                        Logs.d(TAG, "uid: " + result);
                        util.writeUid(getApplicationContext(), result);
                    } else {
                        Toast.makeText(getApplicationContext(), "网络异常,请检查网络", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Logs.d(TAG, "已注册用户");
        }
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


    public void experience(View view) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //以下为微博使用代码
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
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                openID = mAccessToken.getUid();
                //上传openID
                Map<String, String> params = new HashMap<>();
                params.put("openID", openID);
                String uid = util.readUid(Login.this);
                params.put("uid", uid);
                String md5 = util.readMD5(Login.this);
                params.put("md5", md5);

                new VolleyPost(getApplicationContext(), util.ServerUrl + "SaveOpenID", params, new VolleyCallBack() {
                    @Override
                    public void volleyResult(String result) {
                        if (result != null) {
                            util.writeOpenID(Login.this, openID);
                            //保存UID
                            util.writeUid(getApplicationContext(), result);
                            //删除旧数据
                            new DbOpenHelper(getApplicationContext()).dropTable();
                            Toast.makeText(Login.this, "微博帐号登录成功", Toast.LENGTH_LONG).show();
                            Logs.d(TAG, "weibo登录成功");

                            Intent intent = new Intent();
                            intent.setClass(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "网络异常,请检查网络", Toast.LENGTH_LONG).show();
                            Logs.e(TAG, "网络异常, saveOpenID 上传失败");
                        }

                    }
                });


                Logs.d("openID:", openID);
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(mAccessToken.getExpiresTime()));
                Logs.d("有效时间:", date);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                Toast.makeText(Login.this, "授权失败,code：" + code, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }

        @Override
        public void onCancel() {
        }
    }
}



