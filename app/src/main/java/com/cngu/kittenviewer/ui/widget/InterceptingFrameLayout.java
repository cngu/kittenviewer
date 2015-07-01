package com.cngu.kittenviewer.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class InterceptingFrameLayout extends FrameLayout {
    private OnInterceptTouchEventListener mListener;

    public InterceptingFrameLayout(Context context) {
        super(context);
    }

    public InterceptingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mListener != null) {
            return mListener.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnInterceptTouchEventListener {
        boolean onInterceptTouchEvent(MotionEvent ev);
    }
}
