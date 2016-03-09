package com.kankanews.ui.view.filesel;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kankanews.bean.VideoUpload;
import com.kankanews.kankanxinwen.R;

import java.util.List;

public class VideoSelectedGridAdapter extends CommonAdapter<VideoUpload> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	private VideoUpload mSelectedVideo;

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	private TextView selectedNum;
	private VideoSelectedMainActivity selectedMainActivity;

	public VideoSelectedGridAdapter(Context context, List<VideoUpload> mDatas,
			int itemLayoutId, String dirPath,
			VideoSelectedMainActivity selectedMainActivity) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.selectedMainActivity = selectedMainActivity;
		this.selectedNum = selectedMainActivity.getSelectedNum();
	}

	@Override
	public void convert(final ViewHolder helper, final VideoUpload item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.ic_pictures_no);
		// 设置no_selected
		helper.setImageResource(R.id.id_item_select,
				R.drawable.ic_picture_unselected);
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, item.getPath());

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {

				// 已经选择过该图片
				if (mSelectedVideo == item) {
					mSelectedVideo = null;
					mSelect.setImageResource(R.drawable.ic_picture_unselected);
					mImageView.setColorFilter(null);
				} else
				// 未选择该图片
				{
					if (mSelectedVideo == null) {
						mSelectedVideo = item;
						mSelect.setImageResource(R.drawable.ic_pictures_selected);
						mImageView.setColorFilter(Color.parseColor("#77000000"));
					}
				}
				selectedNum.setText(mSelectedVideo == null ? "0" : "1" + "/1段");
			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedVideo == item) {
			mSelect.setImageResource(R.drawable.ic_pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}
		selectedNum.setText(mSelectedVideo == null ? "0" : "1" + "/1段");

	}
}
