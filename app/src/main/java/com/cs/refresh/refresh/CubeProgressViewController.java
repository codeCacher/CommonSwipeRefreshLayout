package com.cs.refresh.refresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.refresh.R;

/**
 * Created by CuiShun on 2018/1/20.
 * 自定义progressView的Controller
 */

public class CubeProgressViewController implements IRefreshProgressViewController {

    private static final int VIEW_SIZE = 50;

    private final Context mContext;
    private ImageView mTopView;
    private View mBottomView;

    private int mViewSize;

    private boolean mIsRefreshing;
    private boolean mIsLoadingMore;

    private boolean mHasMoreData;

    private AnimationDrawable mAnimationDrawable;
    private RotateAnimation mLoadingAnimation;
    private ImageView ivLoading;
    private TextView tvLoading;

    public CubeProgressViewController(Context context) {
        this.mContext = context;
        mHasMoreData = true;
        mViewSize = (int) (context.getResources().getDisplayMetrics().density * VIEW_SIZE);
    }

    @Override
    public void createTopProgressView(int style) {
        if (style == CommonSwipeRefreshLayout.REFRESH_STYPE_INTRUSIVE) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " only support none intrusive style.");
        }
        mTopView = new ImageView(mContext);
        mTopView.setImageResource(R.drawable.pulling_frame1);
    }

    @Override
    public void createBottomProgressView(int style) {
        if (style == CommonSwipeRefreshLayout.REFRESH_STYPE_INTRUSIVE) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " only support none intrusive style.");
        }
        mBottomView = LayoutInflater.from(mContext).inflate(R.layout.bottom_loading_layout, null, false);
        ivLoading = mBottomView.findViewById(R.id.iv_loading);
        tvLoading = mBottomView.findViewById(R.id.tv_loading);
    }

    @Override
    public void setBottomNoMoreDataView(int style) {
        this.mHasMoreData = false;
        ivLoading.setVisibility(View.GONE);
        tvLoading.setText(R.string.no_more_data);
    }

    @Override
    public void setBottomLoadingView(int style) {
        this.mHasMoreData = true;
        ivLoading.setVisibility(View.VISIBLE);
        tvLoading.setText(R.string.loading);
    }

    @Override
    public View getTopProgressView() {
        return mTopView;
    }

    @Override
    public View getBottomProgressView() {
        return mBottomView;
    }

    @Override
    public void onMeasureTopView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        mTopView.measure(MeasureSpec.makeMeasureSpec(mViewSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mViewSize, MeasureSpec.EXACTLY));
    }

    @Override
    public void onMeasureBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        mBottomView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    @Override
    public void layoutTopView(@NonNull ViewGroup parent, @NonNull View refreshListView, int style) {
        int width = parent.getMeasuredWidth();
        int viewWidth = mTopView.getMeasuredWidth();
        int viewHeight = mTopView.getMeasuredHeight();
        mTopView.layout((width / 2 - viewWidth / 2), (int) (refreshListView.getTop() - 1.1f * viewHeight),
                (width / 2 + viewWidth / 2), (int) (refreshListView.getTop() - 0.1f * viewHeight));
    }

    @Override
    public void layoutBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView, int style) {
        int width = parent.getMeasuredWidth();
        int viewWidth = mBottomView.getMeasuredWidth();
        int viewHeight = mBottomView.getMeasuredHeight();
        mBottomView.layout((width / 2 - viewWidth / 2), refreshListView.getBottom(),
                (width / 2 + viewWidth / 2), refreshListView.getBottom() + viewHeight);
    }

    @Override
    public void onTopDragScroll(int translationY, int style) {
        if (mIsRefreshing) {
            return;
        }
        int rate = (int) (10f * translationY / RefreshCalculateHelper.MAX_TOP_DRAG_LENGTH / mContext.getResources().getDisplayMetrics().density);
        switch (rate) {
            case 0:
            case 1:
                mTopView.setImageResource(R.drawable.pulling_frame1);
                break;
            case 2:
                mTopView.setImageResource(R.drawable.pulling_frame2);
                break;
            case 3:
                mTopView.setImageResource(R.drawable.pulling_frame3);
                break;
            case 4:
                mTopView.setImageResource(R.drawable.pulling_frame4);
                break;
            case 5:
                mTopView.setImageResource(R.drawable.pulling_frame5);
                break;
            default:
                mTopView.setImageResource(R.drawable.pulling_frame5);
                break;
        }
    }

    @Override
    public void onBottomDragScroll(int translationY, int style) {
        if (mIsLoadingMore || !mHasMoreData) {
            return;
        }
        int rotation = (int) (360f * translationY / RefreshCalculateHelper.MAX_TOP_DRAG_LENGTH / mContext.getResources().getDisplayMetrics().density);
        ivLoading.setRotation(rotation);
    }

    @Override
    public void onTopResetAnimation(int startPosition, long duration, int style) {

    }

    @Override
    public void onBottomResetAnimation(int startPosition, long duration, int style) {

    }

    @Override
    public void onTopGoToRefreshAnimation(int startPosition, int desPosition, long duration, int style, @Nullable CommonSwipeRefreshLayout.RefreshRunnable refreshRunnable) {

    }

    @Override
    public void onBottomGoToLoadMoreAnimation(int startPosition, int desPosition, long duration, int style, @Nullable CommonSwipeRefreshLayout.LoadMoreRunnable loadMoreRunnable) {

    }

    @Override
    public void onListTopTranslationAnimationStart(int startPosition, int desPosition, long duration, int style) {
        if (startPosition == 0) {
            mTopView.setImageResource(R.drawable.pulling_frame5);
        }
    }

    @Override
    public void onListTopTranslationAnimationEnd(int startPosition, int desPosition, long duration, int style) {

    }

    @Override
    public void onListBottomTranslationAnimationStart(int startPosition, int desPosition, long duration, int style) {

    }

    @Override
    public void onListBottomTranslationAnimationEnd(int startPosition, int desPosition, long duration, int style) {

    }

    @Override
    public void onStartRefresh() {
        mIsRefreshing = true;
        if (mAnimationDrawable == null) {
            mAnimationDrawable = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.jump_animation_drawable);
        }
        mTopView.setImageDrawable(mAnimationDrawable);
        mAnimationDrawable.start();
    }

    @Override
    public void onFinishRefresh() {
        mIsRefreshing = false;
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
            mTopView.clearAnimation();
        }
    }

    @Override
    public void onStartLoadMore() {
        mIsLoadingMore = true;
        if (!mHasMoreData) {
            return;
        }
        startLoadingMoreAnim();
    }

    @Override
    public void onFinishLoadMore() {
        mIsLoadingMore = false;
        ivLoading.clearAnimation();
    }

    @Override
    public int getTopRefreshTrigger() {
        return (int) (VIEW_SIZE * 1.5f);
    }

    @Override
    public int getBottomHeight() {
        return VIEW_SIZE;
    }

    private void startLoadingMoreAnim() {
        if (mLoadingAnimation == null) {
            mLoadingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.loading_animation_drawable);
            mLoadingAnimation.setInterpolator(new LinearInterpolator());
        }
        ivLoading.startAnimation(mLoadingAnimation);
    }
}
