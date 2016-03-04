package com.kankanews.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	protected long lastThreadTimeMillis;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
		mNetUtils = NetUtils.getInstance(mActivity);
		mApplication = KankanewsApplication.getInstance();
		handler = new Handler();
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
	protected abstract void onSuccessObject(JSONObject jsonObject);

	/**
	 * http连接成功
	 */
	protected abstract void onSuccessArray(JSONArray jsonObject);

	/**
	 * http连接失败
	 */
	protected abstract void onFailure(VolleyError error);

	// 处理网络出错
	protected ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			onFailure(error);
		}
	};
	// 处理网络成功(JsonObject)
	protected Listener<JSONObject> mListenerObject = new Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject jsonObject) {
			onSuccessObject(jsonObject);
		}
	};

	// 处理网络成功(JsonArray)
	protected Listener<JSONArray> mListenerArray = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonObject) {
			onSuccessArray(jsonObject);
		}
	};
	protected Handler handler;

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
		titleBarContent = (MyTextView) view
				.findViewById(R.id.title_bar_content);
		// 右
		titleBarRightImg = (ImageView) view
				.findViewById(R.id.title_bar_right_img);
		titleBarRightImgSecond = (ImageView) view
				.findViewById(R.id.title_bar_right_img);
		titleBarRightText = (MyTextView) view
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
	protected void setOnLeftClickLinester(OnClickListener clickListener) {
		titleBarLeftImgSecond.setOnClickListener(clickListener);
		titleBarLeftImg.setOnClickListener(clickListener);
	}

	// 右边按钮的点击事件
	protected void setOnRightClickLinester(OnClickListener clickListener) {
		titleBarRightText.setOnClickListener(clickListener);
		titleBarRightImg.setOnClickListener(clickListener);
		titleBarRightImgSecond.setOnClickListener(clickListener);
	}

	// 只带id和bean跳转界面 若只有单个新闻，
	public void startAnimActivityById(Class<?> cla, int position, String key,
			int[] bean) {
		Intent intent = new Intent(mActivity, cla);
		intent.putExtra("position", position);
		intent.putExtra(key, bean);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void startAnimActivityByBean(Class<?> cla, String key, BaseBean bean) {
		Intent intent = new Intent(mActivity, cla);
		intent.putExtra(key, bean);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void startAnimActivityByNewsHomeModule(Class<?> cla,
			NewsHomeModule module) {
		Intent intent = new Intent(mActivity, cla);
		intent.putExtra("_NEWS_HOME_MODEULE_", module);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void startAnimActivityByNewsHomeModuleItem(Class<?> cla,
			NewsHomeModuleItem moduleItem) {
		Intent intent = new Intent(mActivity, cla);
		intent.putExtra("_NEWS_HOME_MODEULE_ITEM_", moduleItem);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void startAnimActivityByAppClassId(Class<?> cla, String appClassId) {
		Intent intent = new Intent(mActivity, cla);
		intent.putExtra("_NEWS_HOME_APP_CLASS_ID_", appClassId);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	// public void startAnimActivityByParameter(Class<?> cla, String mid,
	// String type, String titleurl, String newstime, String titlepiclist,String
	// titlelist) {
	// Intent intent = new Intent(mActivity, cla);
	// intent.setAction("com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY");
	// intent.addCategory(Intent.CATEGORY_DEFAULT);
	// intent.putExtra("mid", mid);
	// intent.putExtra("type", type);
	// intent.putExtra("titleurl", titleurl);
	// intent.putExtra("newstime", newstime);
	// intent.putExtra("titlepiclist", titlepiclist);
	// intent.putExtra("titlelist", titlelist);
	//
	// // intent.putExtra(key, bean);
	// mActivity.startActivity(intent);
	// mActivity.overridePendingTransition(R.anim.in_from_right,
	// R.anim.out_to_left);
	// }

	// public void startSubjectActivityByParameter(Class<?> cla, String ztid,
	// String title) {
	// Intent intent = new Intent(mActivity, cla);
	// intent.putExtra("ztid", ztid);
	// intent.putExtra("title", title);
	// mActivity.startActivity(intent);
	// mActivity.overridePendingTransition(R.anim.in_from_right,
	// R.anim.out_to_left);
	// }

	@Override
	public void onResume() {
		super.onResume();
		// 统计页面id
		if (!analyticsId.equals("")) {
			MobclickAgent.onPageStart(analyticsId);
		}
	}

	public void refresh() {
	};

	public void recycle() {
	};

	/**
	 * 统计
	 */
	protected String analyticsId = "";
	protected long currentThreadTimeMillis;

	/**
	 * 页面统计 fragment
	 * 
	 * @param analyticsId
	 *            页面id 不统计页面的 直接写""
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

	public void chage2Day() {
		// TODO Auto-generated method stub
		this.mActivity.chage2Day();
	}

	public void chage2Night() {
		// TODO Auto-generated method stub
		this.mActivity.chage2Night();
	}
}
