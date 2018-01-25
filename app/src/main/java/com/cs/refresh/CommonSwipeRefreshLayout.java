package com.cs.refresh;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.BaseSwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by CuiShun on 2018/1/16.
 */

public class CommonSwipeRefreshLayout extends BaseSwipeRefreshLayout {

    private static final String TAG = "CommonSwipeRefresh";
    private static final long ANIM_DURATION = 200;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private int mTranslationY;
    private RefreshCalculateHelper mCalculateHelper;
    private boolean mIsDraggingTop;
    private TimeInterpolator mDecelerateInterpolator;


    public CommonSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CommonSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCalculateHelper = new RefreshCalculateHelper(this);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
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
        Log.i(TAG, "onNestedScroll->" + "dyConsumed:" + dyConsumed + " dyUnconsumed:" + dyUnconsumed);
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        boolean isUp = dyUnconsumed < 0 && !canChildScrollUp();
        if (isUp) {
            mIsDraggingTop = true;
        }
        if (mIsDraggingTop && !isRefreshing()) {
            int newY = mTranslationY - dyUnconsumed;
            if (!mCalculateHelper.isSameSymbol(mTranslationY, newY)) {
                mIsDraggingTop = false;
                mTranslationY = 0;
            } else if (mTranslationY > 0 || (mTranslationY == 0 && !canChildScrollUp())) {
                mTranslationY = mTranslationY - dyUnconsumed;
            }
            target.setTranslationY(mTranslationY);
            return;
        }
        if (mTranslationY != 0 && !isRefreshing()) {
            mTranslationY = 0;
            target.setTranslationY(mTranslationY);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        Log.i(TAG, "onNestedPreFling");
        return mIsDraggingTop;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);
        if (mIsDraggingTop && dy > 0) {
            consumed[1] = dy;
        }
        if (mIsDraggingTop && !isRefreshing() && dy > 0) {
            int newY = mTranslationY - dy;
            if (!mCalculateHelper.isSameSymbol(mTranslationY, newY)) {
                mIsDraggingTop = false;
                mTranslationY = 0;
            } else if (mTranslationY > 0 || (mTranslationY == 0 && !canChildScrollUp())) {
                mTranslationY = mTranslationY - dy;
            }
            mTarget.setTranslationY(mTranslationY);
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull View child) {
        super.onStopNestedScroll(child);
        if (mTranslationY > mCalculateHelper.getDefaultRefreshTrigger()) {
            startGoToRefreshingPositionAnimation(child);
        } else {
            startResetAnimation(child);
        }
        mIsDraggingTop = false;
    }

    private void startResetAnimation(View target) {
        if (mTranslationY == 0) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationY", mTranslationY, 0);
        animator.setDuration(ANIM_DURATION);
        animator.start();
        mTranslationY = 0;
    }

    private void startGoToRefreshingPositionAnimation(View target) {
        int position = mCalculateHelper.getDefaultRefreshTrigger() + mCalculateHelper.getDefaultRefreshSpace();
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationY", mTranslationY, position);
        animator.setDuration(ANIM_DURATION);
        animator.setInterpolator(mDecelerateInterpolator);
        animator.start();
        mTranslationY = position;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
        if (!refreshing) {
            startResetAnimation(mTarget);
        }
    }
}
