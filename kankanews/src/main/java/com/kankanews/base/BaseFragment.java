package com.kankanews.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kankanews.kankanxinwen.R;
import com.kankanews.ui.activity.MainActivity;
import com.kankanews.ui.view.TfTextView;
import com.kankanews.utils.NetUtils;
import com.kankanews.utils.SharePreferenceUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class BaseFragment extends Fragment {
    public SharePreferenceUtils mSpUtil;

    protected KankanewsApplication mApplication;
    public MainActivity mActivity;
    protected NetUtils mNetUtils;

    protected LayoutInflater mInflater;
    protected Handler mHandler;

    protected RelativeLayout titleBarView;
    protected ImageView titleBarLeftImg;
    protected ImageView titleBarLeftImgSecond;
    protected ImageView titleBarContentImg;
    protected TfTextView titleBarContent;
    protected TfTextView titleBarRightText;
    protected ImageView titleBarRightImg;
    protected ImageView titleBarRightImgSecond;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mNetUtils = NetUtils.getInstance(mActivity);
        mApplication = KankanewsApplication.getInstance();
        mHandler = new Handler();
        mSpUtil = this.mActivity.mApplication.getSpUtil();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mInflater = inflater;
        return null;
    }

    /**
     * 加载本地数据
     */
    protected abstract boolean initLocalDate();

    /**
     * 保存数据到本地
     */
    protected abstract void saveLocalDate();

    /**
     * 刷新网络数据
     */
    protected abstract void refreshNetDate();

    /**
     * 加载更多网络数据
     */
    protected abstract void loadMoreNetDate();

    /**
     * http连接成功
     */
    protected abstract void onSuccessResponse(JSONObject jsonObject);

    /**
     * http连接成功
     */
    protected abstract void onSuccessArrayResponse(JSONArray jsonObject);

    /**
     * http连接失败
     */
    protected abstract void onErrorResponse(VolleyError error);


    // 处理网络成功(JsonArray)
    protected Listener<JSONArray> mSuccessArrayListener = new Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray jsonObject) {
            onSuccessArrayResponse(jsonObject);
        }
    };
    // 处理网络出错
    protected ErrorListener mErrorListener = new ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            BaseFragment.this.onErrorResponse(error);
        }
    };
    // 处理网络成功(JsonObject)
    protected Listener<JSONObject> mSuccessListener = new Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            onSuccessResponse(jsonObject);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // 统计页面id
        if (!analyticsId.equals("")) {
            MobclickAgent.onPageStart(analyticsId);
        }
    }

    public void refresh() {
    }

    public void recycle() {
    }

    /**
     * 统计
     */
    protected String analyticsId = "";

    /**
     * 页面统计 fragment
     *
     * @param analyticsId 页面id 不统计页面的 直接写""
     */
    protected void initAnalytics(String analyticsId) {
        this.analyticsId = analyticsId;
    }

    @Override
    public void onPause() {
        if (!analyticsId.equals("")) {
            MobclickAgent.onPageEnd(analyticsId);
        }
        super.onPause();
    }
    /**
     * 设置头部
     *
     * @param view
     *            容器
     * @param content_img_id
     *            中间图标
     * @param left_img_id
     *            左侧图标
     */
    protected void initTitleLeftBar(View view, int left_img_id,
                                    int content_img_id, int right_img_id) {

        initTitleView(view);

        titleBarContent.setVisibility(View.GONE);
        titleBarContentImg.setVisibility(View.VISIBLE);
        titleBarContentImg.setImageResource(content_img_id);
        titleBarLeftImg.setImageResource(left_img_id);
        // com_title_bar_right_bt.setImageResource(right_img_id);

    }

    // 初始化头部组件
    private void initTitleView(View view) {
        titleBarView = (RelativeLayout) view.findViewById(R.id.title_bar_view);
        // 左
        titleBarLeftImg = (ImageView) view
                .findViewById(R.id.title_bar_left_img);
        titleBarLeftImgSecond = (ImageView) view
                .findViewById(R.id.title_bar_left_img_second);
        // 中
        titleBarContentImg = (ImageView) view
                .findViewById(R.id.title_bar_content_img);
        titleBarContent = (TfTextView) view
                .findViewById(R.id.title_bar_content);
        // 右
        titleBarRightImg = (ImageView) view
                .findViewById(R.id.title_bar_right_img);
        titleBarRightImgSecond = (ImageView) view
                .findViewById(R.id.title_bar_right_img);
        titleBarRightText = (TfTextView) view
                .findViewById(R.id.title_bar_right_text);

        titleBarLeftImg.setVisibility(View.GONE);
        titleBarLeftImgSecond.setVisibility(View.GONE);
        titleBarContentImg.setVisibility(View.GONE);
        titleBarContent.setVisibility(View.GONE);
        titleBarRightImgSecond.setVisibility(View.GONE);
        titleBarRightImg.setVisibility(View.GONE);
        titleBarRightText.setVisibility(View.GONE);
    }

    public void initTitleRightLeftBar(View view, String content, int leftImgId,
                                      int rightImgId, int leftImgIdSecond, String rightContent) {

        initTitleView(view);
        titleBarLeftImgSecond.setVisibility(View.VISIBLE);
        titleBarRightText.setVisibility(View.VISIBLE);
        titleBarContent.setVisibility(View.VISIBLE);
        titleBarRightImg.setVisibility(View.VISIBLE);
        titleBarLeftImg.setVisibility(View.VISIBLE);

        titleBarLeftImgSecond.setImageResource(leftImgIdSecond);
        titleBarRightText.setText(rightContent);
        titleBarContent.setText(content);
        titleBarRightImg.setImageResource(rightImgId);
        titleBarLeftImg.setImageResource(leftImgId);

    }

    public void initTitleBar(View view, String content) {

        initTitleView(view);
        titleBarContent.setVisibility(View.VISIBLE);
        titleBarContent.setText(content);

    }

    // 左边按钮的点击事件
    protected void setOnLeftClickLinester(View.OnClickListener clickListener) {
        titleBarLeftImgSecond.setOnClickListener(clickListener);
        titleBarLeftImg.setOnClickListener(clickListener);
    }

    // 右边按钮的点击事件
    protected void setOnRightClickLinester(View.OnClickListener clickListener) {
        titleBarRightText.setOnClickListener(clickListener);
        titleBarRightImg.setOnClickListener(clickListener);
        titleBarRightImgSecond.setOnClickListener(clickListener);
    }
}
