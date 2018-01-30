package com.cs.refresh.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

/**
 * Created by CuiShun on 2018/1/20.
 */

public class BaseProgressViewController implements IRefreshProgressViewController {

    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    static final int CIRCLE_DIAMETER = 40;

    private final Context mContext;
    private CircleImageView mTopCircleView;
    private CircularProgressDrawable mTopProgress;
    private CircleImageView mBottomCircleView;
    private CircularProgressDrawable mBottomProgress;

    private int mCircleDiameter;

    private boolean mIsRefreshing;
    private boolean mIsLoadingMore;

    public BaseProgressViewController(Context context) {
        this.mContext = context;
        mCircleDiameter = (int) (context.getResources().getDisplayMetrics().density * CIRCLE_DIAMETER);
    }

    @Override
    public void createTopProgressView() {
        mTopCircleView = new CircleImageView(mContext, CIRCLE_BG_LIGHT);
        mTopProgress = new CircularProgressDrawable(mContext);
        mTopProgress.setStyle(CircularProgressDrawable.DEFAULT);
        mTopCircleView.setImageDrawable(mTopProgress);
    }

    @Override
    public void createBottomProgressView() {
        mBottomCircleView = new CircleImageView(mContext, CIRCLE_BG_LIGHT);
        mBottomProgress = new CircularProgressDrawable(mContext);
        mBottomProgress.setStyle(CircularProgressDrawable.DEFAULT);
        mBottomCircleView.setImageDrawable(mBottomProgress);
        mBottomCircleView.setVisibility(View.GONE);
    }

    @Override
    public View getTopProgressView() {
        return mTopCircleView;
    }

    @Override
    public View getBottomProgressView() {
        return mBottomCircleView;
    }

    @Override
    public void onMeasureTopView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        mTopCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
    }

    @Override
    public void onMeasureBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        mBottomCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
    }

    @Override
    public void layoutTopView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        final int width = parent.getMeasuredWidth();
        int circleWidth = mTopCircleView.getMeasuredWidth();
        int circleHeight = mTopCircleView.getMeasuredHeight();
        mTopCircleView.layout((width / 2 - circleWidth / 2), refreshListView.getTop() - circleHeight,
                (width / 2 + circleWidth / 2), refreshListView.getTop());
    }

    @Override
    public void layoutBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        int width = parent.getMeasuredWidth();
        int circleWidth = mBottomCircleView.getMeasuredWidth();
        int circleHeight = mBottomCircleView.getMeasuredHeight();
        mBottomCircleView.layout((width / 2 - circleWidth / 2), (int) (refreshListView.getBottom() - circleHeight * 1.5f),
                (width / 2 + circleWidth / 2), (int) (refreshListView.getBottom() - circleHeight * 0.5f));
    }

    @Override
    public void onTopDragScroll(int translationY, int style) {
        if (mIsRefreshing) {
            return;
        }
        mTopCircleView.setTranslationY(translationY);
        mTopProgress.setArrowEnabled(true);
        mTopProgress.setStartEndTrim(0, 0.8f);
        mTopProgress.setProgressRotation(1f * translationY / RefreshCalculateHelper.MAX_TOP_DRAG_LENGTH / mContext.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBottomDragScroll(int translationY, int style) {

    }

    @Override
    public void onTopTranslationAnimation(final int desPosition, long duration) {
        mTopProgress.setArrowEnabled(false);
        final ObjectAnimator animator = ObjectAnimator.ofFloat(mTopCircleView, "translationY", mTopCircleView.getTranslationY(), desPosition);
        animator.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator.removeAllListeners();
                if (desPosition == 0) {
                    mTopProgress.stop();
                } else {
                    mTopProgress.start();
                }
            }
        });
        animator.start();
    }

    @Override
    public void onBottomTranslationAnimation(int desPosition, long duration) {

    }

    @Override
    public void onStartRefresh() {
        mIsRefreshing = true;
    }

    @Override
    public void onFinishRefresh() {
        mIsRefreshing = false;
    }

    @Override
    public void onStartLoadMore() {
        mIsLoadingMore = true;
        mBottomCircleView.setVisibility(View.VISIBLE);
        mBottomProgress.start();
    }

    @Override
    public void onFinishLoadMore() {
        mIsLoadingMore = false;
        mBottomCircleView.setVisibility(View.GONE);
        mBottomProgress.stop();
    }
}
