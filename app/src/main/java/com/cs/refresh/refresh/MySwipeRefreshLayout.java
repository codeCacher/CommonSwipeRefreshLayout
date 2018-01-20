package com.cs.refresh.refresh;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MySwipeRefreshLayout extends ViewGroup implements NestedScrollingParent,
        NestedScrollingChild {

    private static final int REFRESH_STYPE_INTRUSIVE = 0;
    private static final int REFRESH_STYPE_NON_INTRUSIVE = 1;

    private View mTarget;
    private IRefreshProgressViewController mProgressController;

    private View mTopView;
    private View mBottomView;

    private int mTopStyle;
    private int mBottomStyle;

    private int mTranslationY;

    public MySwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setTarget();
        layoutTargetList();
        layoutTopView();
        layoutBottomView();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyUnconsumed > 0 && !canTargetScrollDown()) {
            mTranslationY += -dyUnconsumed;
//            if (Math.abs(mTranslationY) > DEFAULT_REFRESH_HEIGHT) {
//                mTranslationY = -DEFAULT_REFRESH_HEIGHT;
//            }
            mTarget.setTranslationY(mTranslationY);
//            mBottomView.setTranslationY(mTranslationY);

//            if (mListener != null && !mIsLoadingMore) {
//                mIsLoadingMore = true;
//                mCircleView.setVisibility(VISIBLE);
//                mProgress.start();
//                mListener.onLoadMore();
//            }
        }
    }

    public void setRefreshProgressController(IRefreshProgressViewController controller) {
        if (controller == null) {
            return;
        }
        this.mProgressController = controller;
        mTopView = controller.createTopProgressView();
        mBottomView = controller.createBottomProgressView();
        if (mTopView != null) {
            addView(mTopView);
        }
        if (mBottomView != null) {
            addView(mBottomView);
        }
        postInvalidate();
    }

    private void layoutTargetList() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();
        mTarget.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    }

    private void layoutTopView() {
        if(mTopView == null) {
            return;
        }
        mProgressController.layoutTopView(this, mTarget, mTopView);
    }

    private void layoutBottomView() {
        if(mBottomView == null) {
            return;
        }
        mProgressController.layoutBottomView(this, mTarget, mBottomView);
    }

    private void setTarget() {
        int childCount = getChildCount();
        if (mTarget != null || childCount <= 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof RecyclerView || child instanceof IRefreshListView) {
                mTarget = child;
                break;
            }
        }
        if (mTarget == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + "的子View必须为RecyclerView或实现IRefreshListView接口的View");
        }
    }

    private boolean canTargetScrollUp() {
        return false;
    }

    private boolean canTargetScrollDown() {
        return false;
    }
}
