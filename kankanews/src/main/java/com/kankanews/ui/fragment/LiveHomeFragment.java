package com.kankanews.ui.fragment;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.kankanews.base.BaseActivity;
import com.kankanews.base.BaseFragment;
import com.kankanews.interfaz.CanBePlay;
import com.kankanews.interfaz.CanBeShared;
import com.kankanews.interfaz.CanSharedObject;
import com.kankanews.kankanxinwen.R;
import com.kankanews.utils.CommonUtils;
import com.kankanews.utils.DebugLog;
import com.kankanews.utils.NetUtils;
import com.kankanews.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.widget.VideoView;

public class LiveHomeFragment extends BaseFragment implements OnInfoListener,
		OnCompletionListener, OnErrorListener, OnClickListener,
		OnPreparedListener, OnPageChangeListener, CanBeShared {
	private View inflate;
	private ViewPager mLiveHomeViewPager;
	private FragmentStatePagerAdapter mLiveHomeViewPagerAdapter;
	private List<BaseFragment> fragments;
	private View mVideoRootView;
	private View mVideoContentRootView;
	private VideoView mLiveVideoView;
	private ImageView mLiveVideoImage;
	private ImageView mLiveReturnListBut;
	private ImageView mLiveVideoPlayBut;
	private ImageView mLiveVideoShareBut;
	private TextView mLiveContentTitle;
	private LinearLayout mLiveBufferingIndicator;
	private boolean isPlayStat;
	private ImageView mLiveHomeSelectLive;
	private ImageView mLiveHomeSelectChannel;

	private static final int _HIDE_VIDEO_CONTROLLER_ = 1;

	private static final int _HIDE_TIME_OUT_ = 3000;
	private static final int _CLOSE_AUDIO_PLAY_ = 4000;
	private static final int _NET_CHANGE_ = 5000;

	private boolean mIsShowContent = false;

	private CanSharedObject mSharedObj;

	public Handler mLiveHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case _HIDE_VIDEO_CONTROLLER_:
				mVideoContentRootView.setVisibility(View.GONE);
				mIsShowContent = false;
				break;
			case _CLOSE_AUDIO_PLAY_:
				((LiveChannelListFragment) fragments.get(1)).cleanAudioPlay();
				break;
			case _NET_CHANGE_:
				ToastUtils.Errortoast(mActivity, "网络环境发生变化,当前无WIFI环境");
				break;
			}
		};
	};

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (this.isPlayStat()) {
			this.mLiveVideoView.pause();
			updateFullStartBut(false);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				((LiveChannelListFragment) fragments.get(1)).cleanAudioPlay();
			}
		}) {
		}.start();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mActivity.curTouchTab == mActivity.tabLive) {
			if (this.isPlayStat()) {
//				if (!CommonUtils.isWifi(this.mActivity)) {
//					this.closePlay();
//					mLiveHandler.sendEmptyMessage(_CLOSE_AUDIO_PLAY_);
//					mLiveHandler.sendEmptyMessage(_NET_CHANGE_);
//				} else {
					this.mLiveVideoView.start();
					updateFullStartBut(true);
//				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		initView();
		initData();
		initLinsenter();
		return inflate;
	}

	private void initView() {
		inflate = inflater.inflate(R.layout.fragment_live_home, null);
		mLiveHomeViewPager = (ViewPager) inflate
				.findViewById(R.id.live_home_view_pager);
		mVideoRootView = inflate.findViewById(R.id.video_root_view);
		mVideoContentRootView = inflate
				.findViewById(R.id.live_content_root_view);
		mLiveVideoView = (VideoView) inflate.findViewById(R.id.live_video_view);
		mLiveVideoImage = (ImageView) inflate
				.findViewById(R.id.live_video_image);
		mLiveHomeSelectLive = (ImageView) inflate
				.findViewById(R.id.live_home_select_live);
		mLiveHomeSelectChannel = (ImageView) inflate
				.findViewById(R.id.live_home_select_channel);
		mLiveBufferingIndicator = (LinearLayout) inflate
				.findViewById(R.id.live_buffering_indicator);
		mLiveContentTitle = (TextView) inflate
				.findViewById(R.id.live_content_title);
		mLiveReturnListBut = (ImageView) inflate
				.findViewById(R.id.return_list_but);
		mLiveVideoPlayBut = (ImageView) inflate
				.findViewById(R.id.live_video_play_but);
		mLiveVideoShareBut = (ImageView) inflate
				.findViewById(R.id.live_video_share_but);

		initViewPager();
	}

	private void initLinsenter() {
		mLiveVideoView.setOnPreparedListener(this);
		mLiveVideoView.setOnInfoListener(this);
		mLiveVideoView.setOnErrorListener(this);
		mLiveHomeSelectLive.setOnClickListener(this);
		mLiveHomeSelectChannel.setOnClickListener(this);
		mVideoRootView.setOnClickListener(this);
		mLiveReturnListBut.setOnClickListener(this);
		mLiveVideoPlayBut.setOnClickListener(this);
		mLiveVideoShareBut.setOnClickListener(this);
	}

	@Override
	public void refresh() {
		if (mLiveHomeViewPager != null) {
			int positions = mLiveHomeViewPager.getCurrentItem();
			fragments.get(positions).refresh();
		}
		// initViewPager();
	};

	private void initViewPager() {
		mLiveHomeViewPager.setOffscreenPageLimit(0);
		fragments = new ArrayList<BaseFragment>();
		LiveLiveListFragment live = new LiveLiveListFragment();
		LiveChannelListFragment channel = new LiveChannelListFragment();
		live.setHomeFragment(this);
		channel.setHomeFragment(this);
		fragments.add(live);
		fragments.add(channel);

		mLiveHomeViewPagerAdapter = new FragmentStatePagerAdapter(
				mActivity.getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}

			@Override
			public int getItemPosition(Object object) {
				return POSITION_NONE;
			}

			@Override
			public Object instantiateItem(ViewGroup arg0, int arg1) {
				Object obj = super.instantiateItem(arg0, arg1);
				return obj;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				super.destroyItem(container, position, object);
				fragments.get(position).recycle();

			}

		};
		mLiveHomeViewPager.setDrawingCacheEnabled(false);
		mLiveHomeViewPager.setOnPageChangeListener(this);
		mLiveHomeViewPager.setAdapter(mLiveHomeViewPagerAdapter);
		mLiveHomeViewPager.setCurrentItem(0, false);
	}

	public void playLive(final CanBePlay playTarget) {
		mLiveContentTitle.setText(playTarget.getTitle());
		this.mActivity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mActivity.bottomBarVisible(View.GONE);
		WindowManager.LayoutParams attrs = mActivity.getWindow()
				.getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		mActivity.getWindow().setAttributes(attrs);
		mActivity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		mVideoRootView.setVisibility(View.VISIBLE);
		// liveVideoView.stopPlayback();
		mLiveVideoView.requestFocus();
		mLiveVideoView.setmRootViewHeight((int) (this.mActivity.mScreenWidth));
		mLiveVideoImage.setVisibility(View.VISIBLE);
		mLiveBufferingIndicator.setVisibility(View.VISIBLE);
		NetUtils.getInstance(this.mActivity).getAnalyse(this.mActivity, "live",
				playTarget.getTitle(), playTarget.getTitleurl());

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mLiveVideoView.setVideoPath(playTarget.getStreamurl());
			}
		}, 500);
		mLiveHandler.sendEmptyMessageDelayed(this._HIDE_VIDEO_CONTROLLER_,
				_HIDE_TIME_OUT_);
		this.setPlayStat(true);
	}

	public void closePlay() {
		this.mActivity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mActivity.bottomBarVisible(View.VISIBLE);
		WindowManager.LayoutParams attrs = mActivity.getWindow()
				.getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mActivity.getWindow().setAttributes(attrs);
		// 取消全屏设置
		mActivity.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		mLiveVideoView.stopPlayback();
		mVideoRootView.setVisibility(View.GONE);
		fragments.get(mLiveHomeViewPager.getCurrentItem()).refresh();
		this.setPlayStat(false);
	}

	private void initData() {
		mLiveVideoView.setUserAgent("KKApp");
	}

	@Override
	protected boolean initLocalDate() {
		return false;
	}

	@Override
	protected void saveLocalDate() {

	}

	@Override
	protected void refreshNetDate() {
	}

	@Override
	protected void loadMoreNetDate() {

	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {

	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		mLiveVideoView.start();
		updateFullStartBut(true);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mLiveVideoImage.setVisibility(View.GONE);
				mLiveBufferingIndicator.setVisibility(View.GONE);
			}
		}, 1000);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.live_video_play_but:
			if (mLiveVideoView.isPlaying()) {
				mLiveVideoView.pause();
				updateFullStartBut(false);
			} else {
				mLiveVideoView.start();
				updateFullStartBut(true);
			}
			break;
		case R.id.live_video_share_but:
			// nowLiveNew.setSharedPic(null);
			// this.mActivity
			// .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			// 一键分享
//			this.mSharedObj.setSharedPic(null);
			this.mSharedObj.setSharedTitle("正在直播 | " + this.mSharedObj.getTitle());
			CustomShareBoardRight shareBoard = new CustomShareBoardRight(
					(BaseActivity) this.mActivity, this.mSharedObj);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(this.getActivity().getWindow()
					.getDecorView(), Gravity.RIGHT, 0, 0);
			break;
		case R.id.return_list_but:
			this.closePlay();
			break;
		case R.id.live_home_select_live:
			this.mLiveHomeViewPager.setCurrentItem(0);
			break;
		case R.id.live_home_select_channel:
			this.mLiveHomeViewPager.setCurrentItem(1);
			break;
		case R.id.video_root_view:
			if (mIsShowContent) {
				this.mVideoContentRootView.setVisibility(View.GONE);
			} else {
				this.mVideoContentRootView.setVisibility(View.VISIBLE);
				mLiveHandler.removeMessages(_HIDE_VIDEO_CONTROLLER_);
				mLiveHandler.sendEmptyMessageDelayed(_HIDE_VIDEO_CONTROLLER_,
						_HIDE_TIME_OUT_);
			}
			break;
		}
	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		ToastUtils.Errortoast(this.mActivity, "抱歉当前内容不能播放,请稍后重试");
		this.closePlay();
		return false;
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {

	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		switch (what) {
		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			mLiveVideoView.pause();
			updateFullStartBut(false);
			mLiveBufferingIndicator.setVisibility(View.VISIBLE);
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			mLiveVideoView.start();
			updateFullStartBut(true);
			mLiveBufferingIndicator.setVisibility(View.GONE);
			break;
		}
		return true;
	}

	@Override
	protected void onFailure(VolleyError error) {
		DebugLog.e(error.getLocalizedMessage());

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		fragments.get(position).refresh();
		if (position == 0) {
			this.mLiveHomeSelectLive
					.setImageResource(R.drawable.ic_live_select_live_live);
			this.mLiveHomeSelectChannel
					.setImageResource(R.drawable.ic_live_select_live_channel);
			new Thread(new Runnable() {
				@Override
				public void run() {
					((LiveChannelListFragment) fragments.get(1))
							.cleanAudioPlay();
				}
			}) {
			}.start();
		}
		if (position == 1) {
			this.mLiveHomeSelectLive
					.setImageResource(R.drawable.ic_live_select_channel_live);
			this.mLiveHomeSelectChannel
					.setImageResource(R.drawable.ic_live_select_channel_channel);
		}
	}

	public boolean isPlayStat() {
		return isPlayStat;
	}

	public void setPlayStat(boolean isPlayStat) {
		this.isPlayStat = isPlayStat;
	}

	public void updateFullStartBut(boolean isplaying) {
		if (isplaying)
			mLiveVideoPlayBut.setImageResource(R.drawable.ic_live_pause_button);
		else
			mLiveVideoPlayBut.setImageResource(R.drawable.ic_live_play_button);
	}

	@Override
	public void copy2Clip() {
		// TODO Auto-generated method stub

	}

	public CanSharedObject getSharedObj() {
		return mSharedObj;
	}

	public void setSharedObj(CanSharedObject mSharedObj) {
		this.mSharedObj = mSharedObj;
	}

	public void netChange() {
		if (this.isPlayStat) {
			if (!CommonUtils.isWifi(this.mActivity)) {
				this.closePlay();
				mLiveHandler.sendEmptyMessage(_CLOSE_AUDIO_PLAY_);
				mLiveHandler.sendEmptyMessage(_NET_CHANGE_);
			}
		}
	}
}
