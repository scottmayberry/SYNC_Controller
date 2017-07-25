package com.ford.kcooley8.SYNCController;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SYNCViewPager extends ViewPager {
    private boolean enabled = true;

    public SYNCViewPager(Context context) {
        super(context);
    }

    public SYNCViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return enabled && super.onInterceptTouchEvent(event);
    }

    public void setEnabled(boolean enabledIn) {
        enabled = enabledIn;
    }
}
