package com.cs.refresh;

import android.content.Context;
import android.support.v4.widget.BaseSwipeRefreshLayout;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by CuiShun on 2018/1/16.
 */

public class CommonSwipeRefreshLayout extends BaseSwipeRefreshLayout {

    private static final String TAG = "CommonSwipeRefresh";
    private static final long ANIM_DURATION = 200;

    private View mTarget;
    private CircleImageView mCircleView;
    private CircularProgressDrawable mProgress;
    private boolean mIsNestedFling;

    public CommonSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CommonSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        createBottomProgressView();
    }

    private void createBottomProgressView() {
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT);
        mProgress = new CircularProgressDrawable(getContext());
        mProgress.setStyle(CircularProgressDrawable.DEFAULT);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.GONE);
        addView(mCircleView);
    }


    public void setLoadingMore(boolean loadingMore) {
        mLoadingMore = loadingMore;
        if (!loadingMore) {
            mCircleView.setVisibility(GONE);
            mProgress.stop();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure->" + "widthMeasureSpec:" + widthMeasureSpec + " heightMeasureSpec:" + heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setTarget();
        final int width = getMeasuredWidth();
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();
        mCircleView.layout((width / 2 - circleWidth / 2), (int) (mTarget.getBottom() - circleWidth * 1.5f),
                (width / 2 + circleWidth / 2), (int) (mTarget.getBottom() + circleHeight - circleWidth * 1.5f));
    }

    private void setTarget() {
        int childCount = getChildCount();
        if (mTarget != null || childCount <= 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof RecyclerView) {
                mTarget = child;
            }
        }
        if (mTarget == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + "的子View必须为RecyclerView或实现IRefreshListView接口的View");
        }
        ((RecyclerView)mTarget).addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.i(TAG, "" + mIsNestedFling + canChildScrollDown() + mLoadingMore);
                    if (mIsNestedFling && !canChildScrollDown() && mListener != null && !mLoadingMore) {
                        Log.i(TAG, "fling to start load more");
                        mLoadingMore = true;
                        mCircleView.setVisibility(VISIBLE);
                        mProgress.start();
                        mListener.onLoadMore();
                    }
                    mIsNestedFling = false;
                }
            }
        });
    }

    public boolean canChildScrollDown() {
        return mTarget != null && mTarget.canScrollVertically(1);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.i(TAG, "onNestedFling");
        mIsNestedFling = true;
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(View target) {
        Log.i(TAG, "onStopNestedScroll");
        super.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i(TAG, "onNestedScroll");
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyUnconsumed > 0 && !canChildScrollDown()) {
            if (mListener != null && !mLoadingMore) {
                mLoadingMore = true;
                mCircleView.setVisibility(VISIBLE);
                mProgress.start();
                mListener.onLoadMore();
            }
        }
    }
}
