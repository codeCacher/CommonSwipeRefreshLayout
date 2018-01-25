package com.cs.refresh;

import android.content.Context;
import android.support.v4.widget.BaseSwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by CuiShun on 2018/1/16.
 */

public class CommonSwipeRefreshLayout extends BaseSwipeRefreshLayout {

    private static final String TAG = "CommonSwipeRefresh";
    private static final long ANIM_DURATION = 200;


    public CommonSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CommonSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure->" + "widthMeasureSpec:" + widthMeasureSpec + " heightMeasureSpec:" + heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i(TAG, "onNestedScroll");
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);


    }
}
