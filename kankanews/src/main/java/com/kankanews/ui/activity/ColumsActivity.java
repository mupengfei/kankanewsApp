package com.kankanews.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankanews.base.BaseContentActivity;
import com.kankanews.bean.NewsColums;
import com.kankanews.kankanxinwen.R;
import com.kankanews.utils.CommonUtils;
import com.kankanews.utils.ImageOptions;
import com.kankanews.utils.ImgUtils;
import com.kankanews.utils.PixelUtil;
import com.kankanews.utils.TimeUtil;
import com.lidroid.xutils.exception.DbException;
import com.umeng.socialize.utils.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ColumsActivity extends BaseContentActivity implements OnClickListener {

	private PullToRefreshListView mColumsListView;
	private View mLoadingView;
	private View mRetryView;
	private ColumsAdapter mColumsAdapter;

	private ArrayList<NewsColums> mNewColums = new ArrayList<NewsColums>();

	private ArrayList<ArrayList<NewsColums>> mColumsList;

	private HolderViewOne mColumsViewOne;
	private HolderViewTwo mColumsViewTwo;

	protected Listener<JSONArray> mListenerArray = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonObject) {
			onSuccessArray(jsonObject);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_colums);

	}

	@Override
	protected void initView() {
		mColumsListView = (PullToRefreshListView) this
				.findViewById(R.id.listview);
		mLoadingView = this.findViewById(R.id.activity_loading_view);
		mRetryView = this.findViewById(R.id.activity_retry_view);
		initTitleLeftBar("栏目", R.drawable.ic_left_back);
	}

	@Override
	protected void initData() {
		boolean hasLocal = initLocalData();
		if (hasLocal) {
			showData();
		}
		if (CommonUtils.isNetworkAvailable(this)) {
			refreshNetDate();
		} else {
			if (hasLocal) {

			} else {
				this.mLoadingView.setVisibility(View.GONE);
				this.mRetryView.setVisibility(View.VISIBLE);
			}
		}
	}

	private void refreshNetDate() {
		if (CommonUtils.isNetworkAvailable(this)) {
			mNetUtils.getNewColumsSecondData(this.mListenerArray, mErrorListener);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mColumsListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	@Override
	protected void setListener() {
		mRetryView.setOnClickListener(this);
		setOnLeftClickLinester(this);
		mColumsListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						String time = TimeUtil.getTime(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新:" + time);
						refreshNetDate();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onSuccessResponse(JSONObject jsonObject) {

	}

	@Override
	protected void onErrorResponse(VolleyError error) {
		this.mColumsListView.onRefreshComplete();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.activity_retry_view:
			refreshNetDate();
			break;
		}
	}

	@Override
	protected boolean initLocalData() {

		try {
			mNewColums = (ArrayList<NewsColums>) this.mDbUtils
					.findAll(NewsColums.class);
			if (mNewColums != null && mNewColums.size() > 0) {
				mColumsList = getList(mNewColums);
				return true;
			}
		} catch (DbException e) {
			Log.e(this.getClass().getName(), e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		try {
			this.mDbUtils.saveAll(mNewColums);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void showData() {
		this.mRetryView.setVisibility(View.GONE);
		this.mLoadingView.setVisibility(View.GONE);
		if (mColumsAdapter == null) {
			mColumsAdapter = new ColumsAdapter(this);
			mColumsListView.setAdapter(mColumsAdapter);
		} else {
			mColumsAdapter.notifyDataSetChanged();
		}
	}

	protected void onSuccessArray(JSONArray jsonObject) {
		mNewColums = new ArrayList<NewsColums>();
		JSONArray jsonArray = jsonObject;
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					JSONObject jsonObject2 = jsonArray.optJSONObject(i);
					NewsColums colums = new NewsColums();
					colums = colums.parseJSON(jsonObject2);
					mNewColums.add(colums);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (mNewColums != null && mNewColums.size() > 0) {
				mColumsList = getList(mNewColums);
			}
			saveLocalDate();
			showData();
		}
		mColumsListView.onRefreshComplete();
	}

	public class ColumsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ColumsAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mColumsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			if (mColumsList.get(position).size() == 1
					&& mColumsList.get(position).get(0).getType().equals("1")) {
				return 0;
			} else {
				return 1;
			}

		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int itemViewType = getItemViewType(position);
			if (convertView == null) {
				if (itemViewType == 0) {
					mColumsViewOne = new HolderViewOne();
					convertView = mInflater.inflate(
							R.layout.item_colums_one, null);
					mColumsViewOne.img = (ImageView) convertView
							.findViewById(R.id.colums_img);
					mColumsViewOne.img
							.setLayoutParams(new LinearLayout.LayoutParams(
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 2))),
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 2)) / 4)));
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0,
								PixelUtil.dp2px(10));
					}
					convertView.setTag(mColumsViewOne);
				} else if (itemViewType == 1) {
					mColumsViewTwo = new HolderViewTwo();
					convertView = mInflater.inflate(
							R.layout.item_colums_two, null);
					mColumsViewTwo.v1 = (ImageView) (convertView
							.findViewById(R.id.colums_img1));
					mColumsViewTwo.v2 = (ImageView) (convertView
							.findViewById(R.id.colums_img2));
					mColumsViewTwo.v1
							.setLayoutParams(new LinearLayout.LayoutParams(
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 3)) / 2),
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 2)) / 4)));
					mColumsViewTwo.v2
							.setLayoutParams(new LinearLayout.LayoutParams(
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 3)) / 2),
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 2)) / 4)));
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0, 0);
					}
					convertView.setTag(mColumsViewTwo);
				}

			} else {

				if (itemViewType == 0) {
					mColumsViewOne = (HolderViewOne) convertView.getTag();
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0,
								PixelUtil.dp2px(10));
					} else {
						convertView.setPadding(0, 0, 0, PixelUtil.dp2px(10));
					}
				} else if (itemViewType == 1) {
					mColumsViewTwo = (HolderViewTwo) convertView.getTag();
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0, 0);
					} else {
						convertView.setPadding(0, 0, 0, PixelUtil.dp2px(10));
					}
				}
			}

			if (itemViewType == 0) {
				final NewsColums colum = mColumsList.get(position).get(0);
				colum.setTitlePic(CommonUtils.doWebpUrl(colum.getTitlePic()));
				ImgUtils.imageLoader
						.displayImage(colum.getTitlePic(), mColumsViewOne.img,
								ImageOptions.getSmallImageOptions(false));

				mColumsViewOne.img.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						columsClick(colum);
					}
				});

			} else if (itemViewType == 1) {
				final ArrayList<NewsColums> colums = mColumsList.get(position);
				colums.get(0).setTitlePic(
						CommonUtils.doWebpUrl(colums.get(0).getTitlePic()));
				ImgUtils.imageLoader.displayImage(colums.get(0).getTitlePic(),
						mColumsViewTwo.v1, ImageOptions.getSmallImageOptions(false));

				mColumsViewTwo.v1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						columsClick(colums.get(0));
					}
				});
				if (colums.size() == 2) {

					colums.get(1).setTitlePic(
							CommonUtils.doWebpUrl(colums.get(1).getTitlePic()));
					ImgUtils.imageLoader.displayImage(colums.get(1)
							.getTitlePic(), mColumsViewTwo.v2, ImageOptions
							.getSmallImageOptions(false));

					mColumsViewTwo.v2.setVisibility(View.VISIBLE);
					mColumsViewTwo.v2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							columsClick(colums.get(1));
						}
					});
				} else {
					mColumsViewTwo.v2.setVisibility(View.INVISIBLE);
				}
			}

			return convertView;
		}

	}

	public class HolderViewOne {
		ImageView img;
	}

	public class HolderViewTwo {
		ImageView v1;
		ImageView v2;
	}

	public ArrayList<ArrayList<NewsColums>> getList(ArrayList<NewsColums> nc) {
		ArrayList<ArrayList<NewsColums>> listss = new ArrayList<ArrayList<NewsColums>>();

		boolean isFirst = true;

		ArrayList<NewsColums> ncp = null;

		for (int i = 0; i < nc.size(); i++) {
			NewsColums new_Colums = new NewsColums();
			new_Colums = nc.get(i);
			if (new_Colums.getType().equals("0") && isFirst) {
				ncp = new ArrayList<NewsColums>();
				isFirst = false;
				ncp.add(new_Colums);
				if (i >= nc.size() - 1) {
					listss.add(ncp);
				}
			} else if (new_Colums.getType().equals("0") && !isFirst) {
				isFirst = true;
				ncp.add(new_Colums);
				listss.add(ncp);
				ncp = new ArrayList<NewsColums>();
			} else if (new_Colums.getType().equals("1")) {
				if (!isFirst) {
					listss.add(ncp);
					ncp = new ArrayList<NewsColums>();
				}
				ncp = new ArrayList<NewsColums>();
				ncp.add(new_Colums);
				listss.add(ncp);
				isFirst = true;
			}
		}

		return listss;
	}

	public void refresh() {
		if (mColumsListView != null)
			mColumsListView.setRefreshing(false);
	}

	public void columsClick(NewsColums colum) {
//		if (colum.getSecondNum() == 1) {
//			startAnimActivityByBean(NewsActivityColumsInfo.class, "colums",
//					colum);
//		} else {
//			if (CommonUtils.isNetworkAvailable(this)) {
//				SecondColumsBoard board = new SecondColumsBoard(this, colum);
//				// board.setWidth(this.mActivity.mScreenWidth * 95 / 100);
//				board.setAnimationStyle(R.style.popwin_anim_style);
//				board.showAtLocation(mContext.getWindow().getDecorView(),
//						Gravity.BOTTOM, 0, 0);
//			}
//		}
	}
}
