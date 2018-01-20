package com.cs.refresh;

import android.content.Context;
import android.support.v4.widget.BaseSwipeRefreshLayout;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.cs.refresh.refresh.IRefreshListView;

/**
 * Created by Administrator on 2018/1/16.
 */

public class CommonSwipeRefreshLayout extends BaseSwipeRefreshLayout {

    private static final long ANIM_DURATION = 200;
    private static final String TAG = "CommonSwipeRefresh";

    private View mTarget;
    private boolean mIsLoadingMore;
    private int mTranslationY;
    private CircleImageView mCircleView;
    private CircularProgressDrawable mProgress;
//    private View mBottomView;

    public CommonSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CommonSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
//        mBottomView = LayoutInflater.from(context).inflate(R.layout.bottom_refresh_layout, this, false);
//        addView(mBottomView);
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
        this.mIsLoadingMore = loadingMore;
        if (!loadingMore) {
//            mTarget.setTranslationY(0);
//            mTranslationY = 0;
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
            if (child instanceof RecyclerView || child instanceof IRefreshListView) {
                mTarget = child;
            }
        }
        if (mTarget == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + "的子View必须为RecyclerView或实现IRefreshListView接口的View");
        }
    }

    public boolean canChildScrollDown() {
        return mTarget != null && mTarget.canScrollVertically(1);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyUnconsumed > 0 && !canChildScrollDown()) {
//            mTranslationY += -dyUnconsumed;
//            if (Math.abs(mTranslationY) > DEFAULT_REFRESH_HEIGHT) {
//                mTranslationY = -DEFAULT_REFRESH_HEIGHT;
//            }
//            mTarget.setTranslationY(mTranslationY);
//            mBottomView.setTranslationY(mTranslationY);

            if (mListener != null && !mIsLoadingMore) {
                mIsLoadingMore = true;
                mCircleView.setVisibility(VISIBLE);
                mProgress.start();
                mListener.onLoadMore();
            }
        }
    }

//    private void startResetAnim() {
//        TranslateAnimation anim = new TranslateAnimation(0, 0, mTranslationY, 0);
//        anim.setDuration(ANIM_DURATION);
//        mTarget.startAnimation(anim);
//        mTarget.setTranslationY(0);
//        mTranslationY = 0;
//    }

//    @Override
//    public void onStopNestedScroll(View target) {
//        super.onStopNestedScroll(target);
//        if (mTarget == null) {
//            return;
//        }
//        startResetAnim();
//    }
}
