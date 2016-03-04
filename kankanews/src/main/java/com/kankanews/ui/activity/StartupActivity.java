package com.kankanews.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.kankanews.base.BaseActivity;
import com.kankanews.bean.Advert;
import com.kankanews.config.AndroidConfig;
import com.kankanews.kankanxinwen.R;
import com.kankanews.utils.CommonUtils;
import com.kankanews.utils.DebugLog;
import com.kankanews.utils.ImgUtils;
import com.kankanews.utils.NetUtils;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class StartupActivity extends BaseActivity {
    private NetUtils instance;
    boolean isFirstIn = false;
    // 应用版本号
    private String version;

    private static final int GO_HOME = 1000;
    private static final int AD_GO_HOME = 1001;
    private static final int GO_GUIDE = 1010;
    private static final int AD_GO_GUIDE = 1011;
    private static final int GO_TRANS = 1030;
    private static final int CLICK_GO_HOME = 1020;
    private static final int REMOVE_MESSAGES = 2000;
    private static final int REMOVE_ALL_MESSAGES = 3000;
    // 延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 5000;
    // 延迟3秒
    private static final long AD_HAS_DELAY_MILLIS = 3000;
    // 延迟3秒
    private static final long AD_NO_DELAY_MILLIS = 2000;

    private ImageView adPic;
    private Advert advert;

    private boolean hasCallAds = false;

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                case AD_GO_HOME:
                    goHome();
                    break;
                case AD_GO_GUIDE:
                case GO_GUIDE:
                    // goGuide();
                    goHome();
                    break;
                case GO_TRANS:
                    goTrasition();
                    break;
                case REMOVE_MESSAGES:
                    mHandler.removeMessages(GO_GUIDE);
                    mHandler.removeMessages(GO_HOME);
                    break;
                case REMOVE_ALL_MESSAGES:
                    mHandler.removeMessages(GO_GUIDE);
                    mHandler.removeMessages(GO_TRANS);
                    mHandler.removeMessages(GO_HOME);
                    mHandler.removeMessages(AD_GO_GUIDE);
                    mHandler.removeMessages(AD_GO_HOME);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO 推送
        instance = NetUtils.getInstance(this);

        // 听云 放在友盟之后
        // NBSAppAgent.setLicenseKey("90d48bf7c56d4d5d9071ce32a39644d3")
        // .withLocationServiceEnabled(true).start(this);

        mApplication.setStart(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_startup);
        // 关闭默认的统计方式
        MobclickAgent.openActivityDurationTrack(false);
        // 发送策略
        MobclickAgent.updateOnlineConfig(mContext);
        initAnalytics("");
        setRightFinsh(false);

        init();

        String rId = JPushInterface.getRegistrationID(this);
        DebugLog.e(rId);
        // Bundle bundle = getIntent().getExtras();
        // if (bundle != null) {
        // DebugLog.e(bundle.getString("PUSH_NEWS_ID"));
        //
        // for (String key : bundle.keySet()) {
        // DebugLog.e("key: " + key + "  " + bundle.getString(key));
        // }
        // }

        // createShortcut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasCallAds) {
            int adPicWidth = adPic.getWidth();
            int adPicHeight = adPic.getHeight();
            hasCallAds = true;
            if (CommonUtils.isNetworkAvailableNoToast(this)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("aid", AndroidConfig.ADVERT_ID_DEBUG);
                params.put("ts", new Date().getTime() + "");
                params.put("fmt", "json");
                params.put("ver", "1");
                params.put("aw", adPicWidth + "");
                params.put("ah", adPicHeight + "");
                params.put("m", "1");
                instance.getAdert(params, this.mSuccessListener, this.mErrorListener);
            } else {
                initLocalData();
                mHandler.sendEmptyMessage(REMOVE_MESSAGES);
                if (advert == null) {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null && bundle.containsKey("PUSH_NEWS_ID")) {
                        return;
                    }
                    mHandler.sendEmptyMessageDelayed(AD_GO_HOME,
                            AD_NO_DELAY_MILLIS);
                    return;
                } else {
                    showAdvert();
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null && bundle.containsKey("PUSH_NEWS_ID")) {
                        return;
                    }
                    mHandler.sendEmptyMessageDelayed(AD_GO_HOME,
                            AD_HAS_DELAY_MILLIS);
                    return;
                }
            }
        }
    }

    private void init() {

        version = CommonUtils.getVersionName(this);
        isFirstIn = mSpUtils.isFristComing();

        if (isFirstIn) {
            CommonUtils.deleteDir(CommonUtils.getVideoCachePath(mContext));
            CommonUtils.deleteDir(CommonUtils.getImageCachePath(mContext));
        }
        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PUSH_NEWS_ID")) {
            mHandler.sendEmptyMessageDelayed(GO_TRANS, SPLASH_DELAY_MILLIS);
        } else {
            // if (isFirstIn || !version.equals(mSpUtil.getVersion())) {
            // 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
            // mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
            // } else {
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            // }
        }
    }

    private void goHome() {
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        if ("kkl".equalsIgnoreCase(scheme)) {
            Uri uridata = this.getIntent().getData();
            String liveId = uridata.getQueryParameter("LIVE_ID");
            if (liveId != null && !liveId.trim().equals(""))
                intent.putExtra("LIVE_ID", liveId);
        }
//        intent.setClass(SplashActivity.this, MainActivity.class);
        // intent.setClass(SplashActivity.this, NewsContentActivity.class);
//        SplashActivity.this.startActivity(intent);
        // overridePendingTransition(R.anim.alpha, R.anim.alpha_op);
//        SplashActivity.this.finish();
    }

    private void goTrasition() {
        Intent intent = getIntent();
//        intent.setClass(SplashActivity.this, TransitionLoadingActivity.class);
//        SplashActivity.this.startActivity(intent);
        // overridePendingTransition(R.anim.alpha, R.anim.alpha_op);
//        SplashActivity.this.finish();
    }

    private void goGuide() {
        Bundle bundle = getIntent().getExtras();
        // intent.setClass(SplashActivity.this, GuideActivity.class);

//        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
//        if (bundle != null) {
//            if (bundle.containsKey("LIVE_ID"))
//                intent.putExtra("LIVE_ID", bundle.getString("LIVE_ID"));
//            if (bundle.containsKey("PUSH_NEWS_ID"))
//                intent.putExtra("PUSH_NEWS_ID",
//                        bundle.getString("PUSH_NEWS_ID"));
//        }
//        SplashActivity.this.startActivity(intent);
//        SplashActivity.this.finish();
    }

    /**
     * 创建桌面快捷方式
     */
//    private void createShortcut() {
//        SharedPreferences setting = getSharedPreferences("silent.preferences",
//                0);
//        // 判断是否第一次启动应用程序（默认为true）
//        boolean firstStart = setting.getBoolean("FIRST_START", true);
//        // 第一次启动时创建桌面快捷方式
//        if (firstStart) {
//            Intent shortcut = new Intent(
//                    "com.android.launcher.action.INSTALL_SHORTCUT");
//            // 快捷方式的名称
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
//                    getString(R.string.app_name));
//            // 不允许重复创建
//            shortcut.putExtra("duplicate", false);
//            // 指定快捷方式的启动对象
//            // ComponentName comp = new ComponentName(this.getPackageName(), "."
//            // + this.getLocalClassName());
//            // shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
//            // Intent.ACTION_MAIN).setComponent(comp));
//
//            Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
//            shortcutIntent.setClassName(this, this.getClass().getName());
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//
//            // 快捷方式的图标
//            ShortcutIconResource iconRes = Intent.ShortcutIconResource
//                    .fromContext(this, R.drawable.icon_launch);
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
//            // 发出广播
//            sendBroadcast(shortcut);
//            // 将第一次启动的标识设置为false
//            Editor editor = setting.edit();
//            editor.putBoolean("FIRST_START", false);
//            // 提交设置
//            editor.commit();
//        }
//    }
    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        adPic = (ImageView) this.findViewById(R.id.ad_pic);

        adPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!CommonUtils.isNetworkAvailableNoToast(StartupActivity.this))
                    return;
                if (advert != null && advert.getValue() != null
                        && !advert.getValue().equals("")) {
                    String advertValue = advert.getValue();
                    if (advertValue.split("\\/\\/").length == 2) {
                        String keyValue = advertValue.split("\\/\\/")[1];
                        if (keyValue.split("=").length == 2) {
                            String key = keyValue.split("=")[0];
                            String value = keyValue.split("=")[1];
                            Intent intent = getIntent();
                            mHandler.sendEmptyMessage(REMOVE_ALL_MESSAGES);
                            if (key.equalsIgnoreCase("infoid"))
                                intent.putExtra("PUSH_NEWS_ID", value);
                            if (key.equalsIgnoreCase("liveid"))
                                intent.putExtra("LIVE_ID", value);
                            // if (isFirstIn
                            // || !version.equals(mSpUtil.getVersion())) {
                            // 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
                            // goGuide();
                            // goHome();
                            // } else {
                            // mHandler.sendEmptyMessage(CLICK_GO_HOME);
                            if (key.equalsIgnoreCase("infoid")) {
                                goTrasition();
                            } else {
                                goHome();
                            }
                            // }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setListener() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onSuccessResponse(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        mHandler.sendEmptyMessage(REMOVE_MESSAGES);
        Bundle bundle = getIntent().getExtras();
        if (jsonObject.optString("status").equals("failure")) {
            clearLocalAdvert();
            if (bundle != null && bundle.containsKey("PUSH_NEWS_ID")) {
                return;
            }
            // if (isFirstIn || !version.equals(mSpUtil.getVersion())) {
            // 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
            // mHandler.sendEmptyMessageDelayed(AD_GO_GUIDE,
            // AD_NO_DELAY_MILLIS);
            // } else {
            mHandler.sendEmptyMessageDelayed(AD_GO_HOME, AD_NO_DELAY_MILLIS);
            // }
            return;
        }
        if (advert == null)
            advert = new Advert();
        advert.parseJSON(jsonObject);
        if (advert != null) {
            showAdvert();
            saveLocalData();
        } else {
            initLocalData();
            showAdvert();
        }
        if (bundle != null && bundle.containsKey("PUSH_NEWS_ID")) {
            return;
        }
        // if (isFirstIn || !version.equals(mSpUtil.getVersion())) {
        // 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
        // mHandler.sendEmptyMessageDelayed(AD_GO_GUIDE, AD_HAS_DELAY_MILLIS);
        // } else {
        mHandler.sendEmptyMessageDelayed(AD_GO_HOME, AD_HAS_DELAY_MILLIS);
        // }
    }

    @Override
    protected void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        initLocalData();
        showAdvert();
    }

    protected void saveLocalData() {
        try {
            if (this.mDbUtils.tableIsExist(Advert.class)) {
                this.mDbUtils.deleteAll(Advert.class);
            }
            if (advert != null)
                this.mDbUtils.save(advert);
        } catch (DbException e) {
            Log.e("StartupActivity", e.getLocalizedMessage());
        }
    }

    protected boolean initLocalData() {
        try {
            advert = this.mDbUtils.findFirst(Advert.class);
            return true;
        } catch (DbException e) {
            Log.e("StartupActivity", e.getLocalizedMessage());
            return false;
        }
    }

    private void clearLocalAdvert() {
        try {
            this.mDbUtils.deleteAll(Advert.class);
        } catch (DbException e) {
            Log.e("StartupActivity", e.getLocalizedMessage());
        }
    }

    private void showAdvert() {
        if (advert != null)
            ImgUtils.imageLoader.displayImage(advert.getUrl(), adPic,
                    ImgUtils.liveImageOptions);
    }
}
