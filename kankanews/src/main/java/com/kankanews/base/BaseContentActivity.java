package com.kankanews.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.kankanews.interfaz.CanBeShared;
import com.kankanews.kankanxinwen.R;
import com.kankanews.ui.view.TfTextView;

import org.json.JSONObject;

public class BaseContentActivity extends BaseActivity implements
        CanBeShared {
    protected RelativeLayout titleBarView;
    protected ImageView titleBarLeftImg;
    protected ImageView titleBarLeftImgSecond;
    protected ImageView titleBarContentImg;
    protected TfTextView titleBarContent;
    protected TfTextView titleBarRightText;
    protected ImageView titleBarRightImg;
    protected ImageView titleBarRightImgSecond;

    // init头部view
    protected void initTitleBarView() {
        titleBarView = (RelativeLayout) findViewById(R.id.title_bar_view);
        // 左
        titleBarLeftImg = (ImageView) findViewById(R.id.title_bar_left_img);
        titleBarLeftImgSecond = (ImageView) findViewById(R.id.title_bar_left_img_second);
        // 中
        titleBarContentImg = (ImageView) findViewById(R.id.title_bar_content_img);
        titleBarContent = (TfTextView) findViewById(R.id.title_bar_content);
        // 右
        titleBarRightImgSecond = (ImageView) findViewById(R.id.title_bar_right_second_img);
        titleBarRightImg = (ImageView) findViewById(R.id.title_bar_right_img);
        titleBarRightText = (TfTextView) findViewById(R.id.title_bar_right_text);

        titleBarLeftImg.setVisibility(View.GONE);
        titleBarLeftImgSecond.setVisibility(View.GONE);
        titleBarContentImg.setVisibility(View.GONE);
        titleBarContent.setVisibility(View.GONE);
        titleBarRightImgSecond.setVisibility(View.GONE);
        titleBarRightImg.setVisibility(View.GONE);
        titleBarRightText.setVisibility(View.GONE);
    }

    // 左图标 中文字 右图标
    protected void initTitle_Left_bar(int left_img, String mid_text,
                                      int right_img) {
        initTitleBarView();
        titleBarLeftImg.setVisibility(View.VISIBLE);
        titleBarContent.setVisibility(View.VISIBLE);
        titleBarRightImg.setVisibility(View.VISIBLE);
        titleBarLeftImg.setImageResource(left_img);
        titleBarContent.setText(mid_text);
        titleBarRightImg.setImageResource(right_img);
    }

    // 只有左侧和中间的 图标
    protected void initTitleLeftBar(int left_img_id, int mid_img_id) {
        initTitleBarView();

        titleBarContentImg.setVisibility(View.VISIBLE);
        titleBarLeftImg.setVisibility(View.VISIBLE);
        titleBarContentImg.setImageResource(mid_img_id);
        titleBarLeftImg.setImageResource(left_img_id);
    }

    protected void initTitleLeftBar(String content, int leftImgId) {
        initTitleBarView();
        titleBarContent.setVisibility(View.VISIBLE);
        titleBarLeftImg.setVisibility(View.VISIBLE);

        titleBarContent.setText(content);
        titleBarLeftImg.setImageResource(leftImgId);

    }

    protected void initTitleBar(String content, int leftImgId, int rightImgId) {
        initTitleBarView();
        titleBarContent.setVisibility(View.VISIBLE);
        titleBarLeftImg.setVisibility(View.VISIBLE);
        titleBarRightImg.setVisibility(View.VISIBLE);

        titleBarContent.setText(content);
        titleBarLeftImg.setImageResource(leftImgId);
        titleBarRightImg.setImageResource(rightImgId);

    }

    protected void initTitleBar(String content) {
        initTitleBarView();
        titleBarContent.setVisibility(View.VISIBLE);

        titleBarContent.setText(content);
    }

    protected void initTitleRightBar(String content, String rightContent,
                                     int right_img_id) {
        initTitleBarView();
        titleBarRightText.setVisibility(View.VISIBLE);
        titleBarContent.setVisibility(View.VISIBLE);
        titleBarRightImg.setVisibility(View.VISIBLE);

        titleBarRightText.setText(rightContent);
        titleBarContent.setText(content);
        titleBarRightImg.setImageResource(right_img_id);
    }

    public void initTitleBarContent(String content, int leftImgSecondId,
                                    String rightContent, int right_img_id, int left_img_id) {
        initTitleBarView();
        titleBarLeftImgSecond.setVisibility(View.VISIBLE);
        titleBarRightText.setVisibility(View.VISIBLE);
        titleBarContent.setVisibility(View.VISIBLE);
        titleBarRightImg.setVisibility(View.VISIBLE);
        titleBarLeftImg.setVisibility(View.VISIBLE);

        titleBarLeftImgSecond.setImageResource(leftImgSecondId);
        titleBarRightText.setText(rightContent);
        titleBarContent.setText(content);
        titleBarRightImg.setImageResource(right_img_id);
        titleBarLeftImg.setImageResource(left_img_id);
    }

    public void initTitleBarIcon(int contentId, int leftImgId,
                                 int leftImgSecondId, int rightImgId, int rightImgSecondId) {
        initTitleBarView();
        titleBarContentImg.setVisibility(View.VISIBLE);
        titleBarRightImgSecond.setVisibility(View.VISIBLE);
        titleBarLeftImg.setVisibility(View.VISIBLE);
        if (rightImgId == 0) {
            titleBarRightImg.setVisibility(View.GONE);
        } else {
            titleBarRightImg.setVisibility(View.VISIBLE);
            titleBarRightImg.setImageResource(rightImgId);
        }
        titleBarRightImgSecond.setImageResource(rightImgSecondId);
        titleBarLeftImg.setImageResource(leftImgId);
        if (contentId == 0) {
            titleBarContentImg.setBackground(null);
            // titleBarContentImg.setVisibility(View.GONE);
        } else {
            titleBarContentImg.setImageResource(contentId);
        }
        titleBarLeftImgSecond.setImageResource(leftImgSecondId);
    }

    // 左边按钮的点击事件
    protected void setOnLeftClickLinester(View.OnClickListener clickListener) {
        titleBarLeftImgSecond.setOnClickListener(clickListener);
        titleBarLeftImg.setOnClickListener(clickListener);
    }

    // 右边按钮的点击事件
    protected void setOnRightClickLinester(View.OnClickListener clickListener) {
        titleBarRightImg.setOnClickListener(clickListener);
        titleBarRightImgSecond.setOnClickListener(clickListener);
        titleBarRightText.setOnClickListener(clickListener);
    }

    // 右边按钮的点击事件
    protected void setOnContentClickLinester(View.OnClickListener clickListener) {
        titleBarContent.setOnClickListener(clickListener);
        titleBarContentImg.setOnClickListener(clickListener);
    }

    protected void showLeftBarTv() {
        titleBarLeftImgSecond.setVisibility(View.VISIBLE);
    }

    protected void setContentTextView(String title) {
        titleBarContent.setText(title);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onSuccessResponse(JSONObject jsonObject) {

    }

    @Override
    protected void onErrorResponse(VolleyError error) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void copy2Clip() {

    }

    protected boolean initLocalData() {
        // TODO Auto-generated method stub
        return true;
    }

    protected void saveLocalDate() {
        // TODO Auto-generated method stub

    }
}
