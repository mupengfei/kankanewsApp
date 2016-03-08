package com.kankanews.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kankanews.kankanxinwen.R;
import com.kankanews.utils.NetUtils;
import com.kankanews.utils.SharePreferenceUtils;
import com.lidroid.xutils.DbUtils;
import com.umeng.analytics.MobclickAgent;
//import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

public abstract class BaseActivity extends FragmentActivity {

    public DbUtils mDbUtils;
    public KankanewsApplication mApplication;
    public SharePreferenceUtils mSpUtils;
    public int mScreenWidth;
    public int mScreenHeight;
    protected BaseActivity mContext;
    protected NetUtils mNetUtils;
    protected boolean isLoadMore;

    private GestureDetector gestureDetector;
    protected boolean isRightFinsh = true;

    public boolean isFinsh;

    public void setRightFinsh(boolean isRightFinsh) {
        this.isRightFinsh = isRightFinsh;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            gestureDetector.onTouchEvent(ev);
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mApplication = KankanewsApplication.getInstance();
        super.onCreate(savedInstanceState);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mContext = this;
        mApplication.addActivity(this);
        mNetUtils = NetUtils.getInstance(this);
        mDbUtils = mApplication.getDbUtils();
        mSpUtils = mApplication.getSpUtil();

        gestureDetector = new GestureDetector(this,
                new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                    }

                    // 右滑手势
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        if (isRightFinsh) {
                            // 右滑动
                            if (e2.getX() - e1.getX() > 200
                                    && Math.abs(e2.getY() - e1.getY()) < Math
                                    .abs(e2.getX() - e1.getX())) {
                                onBackPressed();
                                isFinsh = true;
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }
                });
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
        setListener();
    }

    // 处理网络出错
    protected Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            BaseActivity.this.onErrorResponse(error);
        }
    };
    // 处理网络成功
    protected Response.Listener<JSONObject> mSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            onSuccessResponse(jsonObject);
        }
    };

    public void AnimFinsh() {
        this.finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.out_to_right);
    }

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 设置监听事件
     */
    protected abstract void setListener();

    /**
     * http连接成功
     */
    protected abstract void onSuccessResponse(JSONObject jsonObject);

    /**
     * http连接失败
     */
    protected abstract void onErrorResponse(VolleyError error);

    @Override
    public void onBackPressed() {
        AnimFinsh();
    }

    /**
     * 统计
     */
    protected String analyticsId = "";

    /**
     * 页面统计 activity
     *
     * @param analyticsId 页面id 不统计页面的 直接写""
     */
    protected void initAnalytics(String analyticsId) {
        this.analyticsId = analyticsId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!analyticsId.equals("")) {
            MobclickAgent.onPageStart(analyticsId);
        }
        MobclickAgent.onResume(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!analyticsId.equals("")) {
            MobclickAgent.onPageEnd(analyticsId);
        }
        MobclickAgent.onPause(mContext);
    }

    public void refresh() {
    }

//    public void Commit_Share(SHARE_MEDIA platform) {
//    }

    public void shareReBack() {
    }

    public void finish() {
        this.mApplication.removeActivity(this);
        super.finish();
    }

    public void finishNoRemove() {
        super.finish();
    }

    public void changeFontSize() {
    }

    public void copy2Clip() {
    }

    protected boolean initLocalData() {
        return true;
    }

    protected void saveLocalData() {
    }
}
