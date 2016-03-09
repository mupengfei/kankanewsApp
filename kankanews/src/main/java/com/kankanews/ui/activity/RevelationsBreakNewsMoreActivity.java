package com.kankanews.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankanews.base.BaseContentActivity;
import com.kankanews.bean.Keyboard;
import com.kankanews.bean.RevelationsActicityObjBreakNewsList;
import com.kankanews.bean.RevelationsBreaknews;
import com.kankanews.bean.RevelationsNew;
import com.kankanews.bean.SerializableObj;
import com.kankanews.kankanxinwen.R;
import com.kankanews.ui.view.BorderTextView;
import com.kankanews.ui.view.EllipsizingTextView;
import com.kankanews.ui.view.TfTextView;
import com.kankanews.utils.CommonUtils;
import com.kankanews.utils.DebugLog;
import com.kankanews.utils.FontUtils;
import com.kankanews.utils.ImgUtils;
import com.kankanews.utils.JsonUtils;
import com.kankanews.utils.PixelUtil;
import com.kankanews.utils.StringUtils;
import com.kankanews.utils.TimeUtil;
import com.kankanews.utils.ToastUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RevelationsBreakNewsMoreActivity extends BaseContentActivity implements
		OnClickListener {
	private LayoutInflater inflate;
	private View retryView;
	private View loadingView;

	private PullToRefreshListView breaknewsListView;

	private RevelationsActicityObjBreakNewsList revelationsActivityList;
	private String revelationsActivityListJson;
	private BreaknewsListAdapter breaknewsListAdapter;

	private ActivityListTopHolder topHolder;
	private BreaknewsAboutReportHolder aboutReportHolder;

	private RevelationsBreaksListNewsHolder newsHolder;
	private LoadedFinishHolder finishHolder;
	private boolean isLoadEnd = false;

	private Set<Integer> isShowSetTextView = new HashSet<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_revelations_breaknews_more);

	}

	protected void refreshNetDate() {
		// TODO Auto-generated method stub
		isLoadEnd = false;
		if (CommonUtils.isNetworkAvailable(this)) {
			this.mNetUtils.getRevelationsBreaknewsMore("", this.mSuccessListener,
					mErrorListener);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					breaknewsListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		initTitleLeftBar("报料", R.drawable.ic_left_back);
		inflate = LayoutInflater.from(this);
		loadingView = this.findViewById(R.id.breaknews_loading_view);
		retryView = this.findViewById(R.id.breaknews_retry_view);
		breaknewsListView = (PullToRefreshListView) this
				.findViewById(R.id.breaknews_list_view);
		initListView();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		boolean hasLocal = this.initLocalData();
		if (hasLocal) {
			showData(true);
		}
		if (CommonUtils.isNetworkAvailable(this)) {
			// refreshNetDate();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					breaknewsListView.setmCurrentMode(Mode.PULL_FROM_START);
					breaknewsListView.setRefreshing(false);
				}
			}, 100);
		} else {
			if (hasLocal) {
			} else {
				this.loadingView.setVisibility(View.GONE);
				this.retryView.setVisibility(View.VISIBLE);
			}
		}
	}

	protected void initListView() {
		// TODO Auto-generated method stub
		breaknewsListView.setMode(Mode.BOTH);
		breaknewsListView.getLoadingLayoutProxy(true, false).setPullLabel(
				"下拉可以刷新");
		breaknewsListView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"释放后刷新");
		breaknewsListView.getLoadingLayoutProxy(false, true).setPullLabel(
				"上拉加载更多");
		breaknewsListView.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("刷新中…");
		breaknewsListView.getLoadingLayoutProxy(false, true).setReleaseLabel(
				"松开立即加载");
		breaknewsListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						// String time = TimeUtil.getTime(new Date());
						// refreshView.getLoadingLayoutProxy()
						// .setLastUpdatedLabel("最后更新:" + time);
						refreshNetDate();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						loadMoreNetDate();
					}
				});
	}

	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub
		if (isLoadEnd || !CommonUtils.isNetworkAvailable(this)) {
			breaknewsListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					breaknewsListView.onRefreshComplete();
				}
			}, 300);
			return;
		}
		List<RevelationsBreaknews> breaknews = this.revelationsActivityList
				.getBreaknews();
		this.mNetUtils.getRevelationsBreaknewsMore(
				breaknews.get(breaknews.size() - 1).getNewstime(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject jsonObject) {
						breaknewsListView.onRefreshComplete();
						RevelationsActicityObjBreakNewsList moreList = JsonUtils.toObject(
								jsonObject.toString(),
								RevelationsActicityObjBreakNewsList.class);
						if (moreList.getBreaknews().size() == 0) {
							isLoadEnd = true;
						} else {
							isLoadEnd = false;
							revelationsActivityList.getBreaknews().addAll(
									moreList.getBreaknews());
						}
						breaknewsListAdapter.notifyDataSetChanged();
					}
				}, mErrorListener);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		retryView.setOnClickListener(this);
		setOnLeftClickLinester(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.revelations_retry_view:
			refreshNetDate();
			break;
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		}
	}

	@Override
	protected void onSuccessResponse(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		revelationsActivityListJson = jsonObject.toString();
		boolean needRefresh = (revelationsActivityList == null);
		// ToastUtils.Infotoast(getActivity(), jsonObject.toString());
		revelationsActivityList = JsonUtils.toObject(
				revelationsActivityListJson,
				RevelationsActicityObjBreakNewsList.class);
		if (revelationsActivityList != null) {
			loadingView.setVisibility(View.GONE);
			if (revelationsActivityList.getBreaknews().size() == 0)
				isLoadEnd = true;
			saveLocalDate();
			showData(needRefresh);
		}
	}

	private void showData(boolean needRefresh) {
		breaknewsListView.onRefreshComplete();
		if (needRefresh) {
			breaknewsListAdapter = new BreaknewsListAdapter();
			breaknewsListView.setAdapter(breaknewsListAdapter);
		} else {
			breaknewsListAdapter.notifyDataSetChanged();
		}
		loadingView.setVisibility(View.GONE);
		retryView.setVisibility(View.GONE);
	}

	@Override
	protected void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		ToastUtils.Errortoast(this, "获取活动详情失败");
		loadingView.setVisibility(View.GONE);
		if (revelationsActivityList == null)
			retryView.setVisibility(View.VISIBLE);
		else
			breaknewsListView.onRefreshComplete();
	}

	private class ActivityListTopHolder {
		ImageView activityImageView;
		TfTextView activityTitle;
		TfTextView activityIntro;
	}

	private class RevelationsBreaksListNewsHolder {
		RelativeLayout moreContent;
		LinearLayout keyboardIconContent;
		LinearLayout aboutReportContent;
		TfTextView phoneNumText;
		EllipsizingTextView newsText;
		TfTextView allNewsTextBut;
		GridView newsImageGridView;
		ListView aboutReportListView;
		ImageView aboutReportIcon;
		ImageView oneNewsImageView;
	}

	private class LoadedFinishHolder {
		TfTextView loadedTextView;
	}

	private class BreaknewsAboutReportHolder {
		ImageView newsTitilePic;
		TfTextView newsTitile;
	}

	private class BreaknewsListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (isLoadEnd)
				return revelationsActivityList.getBreaknews().size() + 1;
			return revelationsActivityList.getBreaknews().size();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == revelationsActivityList.getBreaknews().size()) {
				return 1;
			} else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);

			// if (convertView == null) {
			if (itemViewType == 0) {
				convertView = inflate.inflate(
						R.layout.item_revelations_list_break, null);
				newsHolder = new RevelationsBreaksListNewsHolder();
				newsHolder.moreContent = (RelativeLayout) convertView
						.findViewById(R.id.revelations_breaknews_more_content);
				newsHolder.keyboardIconContent = (LinearLayout) convertView
						.findViewById(R.id.revelations_breaknews_keyboard_icon_content);
				newsHolder.phoneNumText = (TfTextView) convertView
						.findViewById(R.id.revelations_breaknews_phonenum);
				newsHolder.newsText = (EllipsizingTextView) convertView
						.findViewById(R.id.revelations_breaknews_newstext);
				newsHolder.allNewsTextBut = (TfTextView) convertView
						.findViewById(R.id.revelations_breaknews_alltext_but);
				newsHolder.newsImageGridView = (GridView) convertView
						.findViewById(R.id.revelations_breaknews_image_grid);
				newsHolder.oneNewsImageView = (ImageView) convertView
						.findViewById(R.id.revelations_breaknews_image_one_view);
				newsHolder.aboutReportListView = (ListView) convertView
						.findViewById(R.id.revelations_breaknews_about_report_news_list);
				newsHolder.aboutReportIcon = (ImageView) convertView
						.findViewById(R.id.revelations_breaknews_about_report_icon);
				newsHolder.aboutReportContent = (LinearLayout) convertView
						.findViewById(R.id.revelations_breaknews_about_report_content);
				convertView.setTag(newsHolder);
			} else if (itemViewType == 1) {
				convertView = inflate.inflate(R.layout.item_list_foot_text,
						null);
				finishHolder = new LoadedFinishHolder();
				finishHolder.loadedTextView = (TfTextView) convertView
						.findViewById(R.id.list_has_loaded_item_textview);
				convertView.setTag(finishHolder);
			}
			// } else {
			// if (itemViewType == 0) {
			// newsHolder = (RevelationsBreaksListNewsHolder) convertView
			// .getTag();
			// } else if (itemViewType == 1) {
			// finishHolder = (LoadedFinishHolder) convertView.getTag();
			// }
			// }
			if (itemViewType == 0) {
				int breakLocation = position;
				newsHolder.moreContent.setVisibility(View.GONE);
				final RevelationsBreaknews news = revelationsActivityList
						.getBreaknews().get(breakLocation);
				newsHolder.phoneNumText.setText("网友 "
						+ news.getPhonenum()
						+ " "
						+ TimeUtil.timeStrToString(news.getNewstime(),
								"yyyy-MM-dd"));
				newsHolder.allNewsTextBut.setVisibility(View.GONE);
				if (isShowSetTextView.contains(position)) {
					newsHolder.newsText.setMaxLines(100);
					newsHolder.allNewsTextBut.setVisibility(View.VISIBLE);
					newsHolder.allNewsTextBut.setText("收起");
				} else
					newsHolder.newsText.setMaxLines(3);
				newsHolder.newsText.setText(StringUtils.deleteLastNewLine(news
						.getNewstext()));
				FontUtils.setTextViewFontSize(
						RevelationsBreakNewsMoreActivity.this,
						newsHolder.newsText, R.string.home_news_text_size,
						mSpUtils.getFontSizeRadix());
				newsHolder.allNewsTextBut.setTag(newsHolder.newsText);
				newsHolder.newsText.setTag(newsHolder.allNewsTextBut);
				newsHolder.newsText
						.addEllipsizeListener(new EllipsizingTextView.EllipsizeListener() {

							@Override
							public void ellipsizeStateChanged(
									boolean ellipsized,
									EllipsizingTextView textView) {
								LinearLayout parent = (LinearLayout) (textView
										.getParent());
								TfTextView allBut = (TfTextView) parent
										.findViewById(R.id.revelations_breaknews_alltext_but);
								if (!ellipsized && textView.getMaxLines() == 3)
									allBut.setVisibility(View.GONE);
								else
									allBut.setVisibility(View.VISIBLE);
							}
						});
				newsHolder.allNewsTextBut
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								LinearLayout parent = (LinearLayout) (v
										.getParent());
								EllipsizingTextView textView = (EllipsizingTextView) parent
										.findViewById(R.id.revelations_breaknews_newstext);
								if (textView.getMaxLines() == 3) {
									textView.setMaxLines(100);
									((TfTextView) v).setText("收起");
									((TfTextView) v).postInvalidate();
									isShowSetTextView.add(position);
								} else {
									textView.setMaxLines(3);
									((TfTextView) v).setText("全文");
									((TfTextView) v).postInvalidate();
									isShowSetTextView.remove(position);
								}
								breaknewsListAdapter.notifyDataSetChanged();
							}
						});
				List<Keyboard> keyboardList = news.getKeyboard();
				newsHolder.keyboardIconContent.removeAllViews();
				for (Keyboard keyboard : keyboardList) {
					TextView view = new BorderTextView(
							RevelationsBreakNewsMoreActivity.this,
							keyboard.getColor());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					int px = PixelUtil.dp2px(5);
					params.setMargins(0, px, 0, px);
					view.setLayoutParams(params);
					view.setGravity(Gravity.CENTER);
					int px3 = PixelUtil.dp2px(3);
					view.setPadding(px3, px3, px3, px3);
					view.setText(keyboard.getText());
					FontUtils.setTextViewFontSize(
							RevelationsBreakNewsMoreActivity.this, view,
							R.string.border_text_view_text_size, 1);
					view.setTextColor(Color.parseColor(keyboard.getColor()));
					newsHolder.keyboardIconContent.addView(view);
				}
				newsHolder.oneNewsImageView.setVisibility(View.GONE);
				if (news.getImagegroup() == null
						|| news.getImagegroup().trim().equals(""))
					newsHolder.newsImageGridView.setVisibility(View.GONE);
				else {
					newsHolder.newsImageGridView.setVisibility(View.VISIBLE);
					final String[] imageGroup = news.getImagegroup().split(
							"\\|");
					if (imageGroup.length == 1) {
						newsHolder.oneNewsImageView.setVisibility(View.VISIBLE);
						newsHolder.newsImageGridView.setVisibility(View.GONE);
						ImgUtils.imageLoader.displayImage(
								CommonUtils.doWebpUrl(imageGroup[0]),
								newsHolder.oneNewsImageView,
								ImgUtils.homeImageOptions);
						newsHolder.oneNewsImageView
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(
												RevelationsBreakNewsMoreActivity.this,
												PhotoViewActivity.class);
										intent.putExtra("_IMAGE_GROUP_",
												imageGroup);
										intent.putExtra("_PHOTO_CUR_NUM_", 0);
										startActivity(intent);
									}
								});

					} else {
						int width = RevelationsBreakNewsMoreActivity.this.mScreenWidth
								- PixelUtil.dp2px(60);
						ViewGroup.LayoutParams params = newsHolder.newsImageGridView
								.getLayoutParams();
						int num = (int) Math
								.ceil(((float) (imageGroup.length)) / 3);
						params.height = (int) (width / 3 * 0.75 * num);
						newsHolder.newsImageGridView.setLayoutParams(params);
						ImageGroupGridAdapter gridAdapter = new ImageGroupGridAdapter();
						gridAdapter.setImageGroup(imageGroup);
						newsHolder.newsImageGridView
								.setSelector(new ColorDrawable(
										Color.TRANSPARENT));
						newsHolder.newsImageGridView.setAdapter(gridAdapter);
					}
				}
				if (news.getRelatednews().size() == 0) {
					newsHolder.aboutReportContent.setVisibility(View.GONE);
					newsHolder.aboutReportIcon.setVisibility(View.GONE);
				} else {
					newsHolder.aboutReportIcon.setVisibility(View.VISIBLE);
					newsHolder.aboutReportContent.setVisibility(View.VISIBLE);
					newsHolder.aboutReportListView
							.setAdapter(new AboutReportNewsListAdapter(news
									.getRelatednews()));
				}
			}
			return convertView;
		}
	}

	private class ImageGroupGridAdapter extends BaseAdapter {
		private String[] imageGroup;

		public void setImageGroup(String[] imageGroup) {
			this.imageGroup = imageGroup;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (imageGroup.length == 4)
				return 5;
			return imageGroup.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (imageGroup.length == 4)
				return null;
			return imageGroup[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = null;
			// if (convertView == null) {
			convertView = inflate.inflate(
					R.layout.item_revelations_breaksnews_image_grid_item, null);
			imageView = (ImageView) convertView
					.findViewById(R.id.breaknews_image_item);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView
					.getLayoutParams();
			params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
			params.height = (int) (parent.getWidth() / 3 * 0.75);
			imageView.setLayoutParams(params);
			convertView.setTag(imageView);
			// } else {
			// imageView = (ImageView) convertView.getTag();
			// }

			imageView.setVisibility(View.VISIBLE);
			if (imageGroup.length == 4 && position == 2) {
				// imageView.setVisibility(View.GONE);
				imageView.setBackground(null);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			} else if (imageGroup.length == 4 && position > 2) {
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(imageGroup[position - 1]),
						imageView, ImgUtils.homeImageOptions);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								RevelationsBreakNewsMoreActivity.this,
								PhotoViewActivity.class);
						intent.putExtra("_IMAGE_GROUP_", imageGroup);
						intent.putExtra("_PHOTO_CUR_NUM_", position - 1);
						startActivity(intent);
					}
				});
			} else {
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(imageGroup[position]), imageView,
						ImgUtils.homeImageOptions);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								RevelationsBreakNewsMoreActivity.this,
								PhotoViewActivity.class);
						intent.putExtra("_IMAGE_GROUP_", imageGroup);
						intent.putExtra("_PHOTO_CUR_NUM_", position);
						startActivity(intent);
					}
				});
			}
			return convertView;
		}
	}

	private class AboutReportNewsListAdapter extends BaseAdapter {
		private List<RevelationsNew> revelationsNew;

		public AboutReportNewsListAdapter(List<RevelationsNew> revelationsNew) {
			this.revelationsNew = revelationsNew;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return revelationsNew.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return revelationsNew.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflate.inflate(
						R.layout.item_revelations_breaknews_about_report, null);
				aboutReportHolder = new BreaknewsAboutReportHolder();
				aboutReportHolder.newsTitilePic = (ImageView) convertView
						.findViewById(R.id.about_report_news_titlepic);
				aboutReportHolder.newsTitile = (TfTextView) convertView
						.findViewById(R.id.about_report_news_title);
				convertView.setTag(aboutReportHolder);
			} else {
				aboutReportHolder = (BreaknewsAboutReportHolder) convertView
						.getTag();
			}
			ImgUtils.imageLoader.displayImage(CommonUtils
					.doWebpUrl(revelationsNew.get(position).getTitlepic()),
					aboutReportHolder.newsTitilePic, ImgUtils.homeImageOptions);
			aboutReportHolder.newsTitile.setText(revelationsNew.get(position)
					.getTitle());

			FontUtils.setTextViewFontSize(
					RevelationsBreakNewsMoreActivity.this,
					aboutReportHolder.newsTitile,
					R.string.revelations_aboutreport_news_text_size,
					mSpUtils.getFontSizeRadix());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openNews(revelationsNew.get(position));
				}
			});
			return convertView;
		}
	}

	private void openNews(RevelationsNew news) {
		//
//		final int news_type = Integer.valueOf(news.getType());
//		NewsHomeModuleItem moduleItem = new NewsHomeModuleItem();
//		moduleItem.setId(news.getMid());
//		moduleItem.setO_cmsid(news.getMid());
//		moduleItem.setAppclassid(news.getZtid());
//		moduleItem.setTitle(news.getTitle());
//		moduleItem.setTitlepic(news.getTitlepic());
//		moduleItem.setTitleurl(news.getTitleurl());
//		if (news_type % 10 == 1) {
//			moduleItem.setType("video");
//			this.startAnimActivityByNewsHomeModuleItem(
//					NewsContentActivity.class, moduleItem);
//		} else if (news_type % 10 == 2) {
//			moduleItem.setType("album");
//			this.startAnimActivityByNewsHomeModuleItem(NewsAlbumActivity.class,
//					moduleItem);
//		} else if (news_type % 10 == 5) {
//			return;
//		} else {
//			moduleItem.setType("text");
//			this.startAnimActivityByNewsHomeModuleItem(
//					NewsContentActivity.class, moduleItem);
//		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (FontUtils.isRevelationsBreaknewsFontSizeHasChanged()) {
			changeFontSize();
			FontUtils.setRevelationsBreaknewsFontSizeHasChanged(false);
		}
//		if (!mSpUtils.getIsDayMode())
//			chage2Night();
//		else
//			chage2Day();
	}

	@Override
	public void changeFontSize() {
		// TODO Auto-generated method stub
		int first = breaknewsListView.getFirstVisiblePosition();
		breaknewsListView.setAdapter(breaknewsListAdapter);
		breaknewsListView.setSelection(first);
	}

	@Override
	protected boolean initLocalData() {
		try {
			SerializableObj object = (SerializableObj) this.mDbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=", "RevelationsBreakNewsMoreList"));
			if (object != null) {
				revelationsActivityListJson = object.getJsonStr();
				revelationsActivityList = JsonUtils.toObject(
						revelationsActivityListJson,
						RevelationsActicityObjBreakNewsList.class);
				return true;
			} else {
				return false;
			}
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		try {
			SerializableObj obj = new SerializableObj(UUID.randomUUID()
					.toString(), revelationsActivityListJson,
					"RevelationsBreakNewsMoreList");
			this.mDbUtils.delete(SerializableObj.class, WhereBuilder.b(
					"classType", "=", "RevelationsBreakNewsMoreList"));
			this.mDbUtils.save(obj);
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}

	}
}
