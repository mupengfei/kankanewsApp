/**
 * 
 */

package com.kankanews.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.kankanews.base.BaseActivity;
import com.kankanews.kankanxinwen.R;
import com.kankanews.ui.activity.RevelationsActivity;

/**
 * 
 */
public class RevelationsChoiceBottomBoard extends PopupWindow implements
		OnClickListener {
	private LayoutInflater inflater;
	private BaseActivity activity;
	private String aId;
	private View goVideo;
	private View goPhoto;
	private View cancelBut;
	private View backView;

	public RevelationsChoiceBottomBoard(BaseActivity activity, String aId) {
		super(activity);
		this.activity = activity;
		this.aId = aId;
		initView(activity);
		initData();
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(
				R.layout.popup_revelations_choice_bottom, null);
		goVideo = rootView.findViewById(R.id.go_video_revelations);
		goPhoto = rootView.findViewById(R.id.go_photo_revelations);
		cancelBut = rootView.findViewById(R.id.cancel_but);
		backView = rootView.findViewById(R.id.choice_back_view);

		goVideo.setOnClickListener(this);
		goPhoto.setOnClickListener(this);
		cancelBut.setOnClickListener(this);
		backView.setOnClickListener(this);
		setContentView(rootView);
		setFocusable(true);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
	}

	public void initData() {
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.choice_back_view || v.getId() == R.id.cancel_but) {
			dismiss();
			return;
		}
		Intent intent = new Intent(this.activity, RevelationsActivity.class);
		intent.putExtra("_AID_", this.aId);
		switch (v.getId()) {
		case R.id.go_photo_revelations:
			intent.putExtra("_REVELATIONS_TYPE_",
					RevelationsActivity._REVELATIONS_PHOTO_);
			break;
		case R.id.go_video_revelations:
			intent.putExtra("_REVELATIONS_TYPE_",
					RevelationsActivity._REVELATIONS_VIDEO_);
			break;
		}
		this.activity.startActivity(intent);
		dismiss();
	}

}
