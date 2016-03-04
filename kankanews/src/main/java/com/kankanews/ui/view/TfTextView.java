package com.kankanews.ui.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kankanews.base.KankanewsApplication;

public class TfTextView extends TextView {

    public TfTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFontStyle();
    }

    public TfTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFontStyle();
    }

    public TfTextView(Context context) {
        super(context);
        initFontStyle();
    }

    private void initFontStyle() {
        setTypeface(KankanewsApplication.getInstance().getTf());// 设置字体
    }

    public int getAvailableWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    public boolean isOverFlowed() {
        Paint paint = getPaint();
        if (getAvailableWidth() == 0 || getLineCount() == 0)
            return false;
        float width = paint.measureText(getText().toString())
                / this.getLineCount();
        if (width > getAvailableWidth())
            return true;
        return false;
    }
}
