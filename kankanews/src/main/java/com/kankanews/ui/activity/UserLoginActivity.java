package com.kankanews.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kankanews.base.BaseContentActivity;
import com.kankanews.kankanxinwen.R;
import com.kankanews.utils.DebugLog;
import com.kankanews.utils.JsonUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class UserLoginActivity extends BaseContentActivity implements View.OnClickListener {
    private Button qqLogin;
    private Button weiboLogin;
    private Button weixinLogin;
    private UMShareAPI mShareAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
    }

    @Override
    protected void initView() {
        super.initView();
        mShareAPI = UMShareAPI.get(this);
        qqLogin = (Button) this.findViewById(R.id.QQ_login);
        weixinLogin = (Button) this.findViewById(R.id.weixin_login);
        weiboLogin = (Button) this.findViewById(R.id.weibo_login);
    }

    @Override
    protected void setListener() {
        super.setListener();
        qqLogin.setOnClickListener(this);
        weixinLogin.setOnClickListener(this);
        weiboLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        SHARE_MEDIA platform ;
        switch (id) {
            case R.id.QQ_login:
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(this, platform, umAuthListener);
                break;
            case R.id.weibo_login:
                platform = SHARE_MEDIA.SINA;
                mShareAPI.doOauthVerify(this, platform, umAuthListener);
                break;
            case R.id.weixin_login:
                platform = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(this, platform, umAuthListener);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
//            mShareAPI.getPlatformInfo(UserLoginActivity.this, platform, umAuthListener);
            DebugLog.e( JsonUtils.toString(data));
            mShareAPI.getPlatformInfo(UserLoginActivity.this, platform, umAuthListener2);
            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    private UMAuthListener umAuthListener2 = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            DebugLog.e(JsonUtils.toString(map));
            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

        }
        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };
}
