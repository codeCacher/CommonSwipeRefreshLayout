package com.cs.refresh.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class MySwipeRefreshLayout extends FrameLayout implements NestedScrollingParent,
        NestedScrollingChild {

    private static final String TAG = "MySwipeRefreshLayout";

    public static final int REFRESH_STYPE_INTRUSIVE = 0;
    public static final int REFRESH_STYPE_NON_INTRUSIVE = 1;

    private static final long ANIM_DURATION = 200;

    private View mTarget;
    private IRefreshProgressViewController mProgressController;

    private View mTopView;
    private View mBottomView;

    private int mTopStyle;
    private int mBottomStyle;

    private int mTranslationY;

    private RefreshListener mRefreshListener;

    private boolean mIsLoadingMore;
    private boolean mIsRefreshing;
    private boolean mIsDraggingTop;
    private boolean mIsDraggingBottom;

    private RefreshCalculateHelper mCalculateHelper;

    public MySwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setTarget();
        layoutTargetList();
        layoutTopView();
        layoutBottomView();
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        Log.i(TAG, "onNestedPreFling");
        return mIsDraggingTop;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.i(TAG, "onNestedFling");
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        if (mIsDraggingTop && !mIsRefreshing) {
            int newY = mTranslationY - dy;
            if (!mCalculateHelper.isSameSymbol(mTranslationY, newY)) {
                mIsDraggingTop = false;
                mTranslationY = 0;
            } else if (mTranslationY > 0 || (mTranslationY == 0 && !canTargetScrollUp())) {
                mTranslationY = mCalculateHelper.calculateTopTranslationY(mTranslationY, dy);
            }
            mTarget.setTranslationY(mTranslationY);
            consumed[1] = dy;
            return;
        }
        if (mIsDraggingBottom) {
            int newY = mTranslationY - dy;
            if (!mCalculateHelper.isSameSymbol(mTranslationY, newY)) {
                mIsDraggingBottom = false;
                mTranslationY = 0;
            } else if (mTranslationY < 0 || (mTranslationY == 0 && !canTargetScrollDown())) {
                mTranslationY = mCalculateHelper.calculateBottomTranslationY(mTranslationY, dy);
            }
            mTarget.setTranslationY(mTranslationY);
            consumed[1] = dy;
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        Log.i(TAG, "onNestedScroll");

        boolean isUp = dyUnconsumed < 0 && !canTargetScrollUp();
        boolean isDown = dyUnconsumed > 0 && !canTargetScrollDown();

        if (isUp) {
            mIsDraggingTop = true;
            return;
        }

        if (isDown && !mIsLoadingMore) {
            mIsDraggingBottom = true;
            if (mRefreshListener != null && !mIsRefreshing) {
                mIsLoadingMore = true;
                mRefreshListener.onLoadMore();
            }
            return;
        }

        if (mTranslationY != 0 && !mIsRefreshing && !mIsLoadingMore) {
            mTranslationY = 0;
            mTarget.setTranslationY(mTranslationY);
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull View child) {
        super.onStopNestedScroll(child);
        if (!mIsRefreshing && mIsDraggingTop) {
            if (mTranslationY > mCalculateHelper.getDefaultRefreshTrigger()
                    && mRefreshListener != null && !mIsLoadingMore) {
                startGoToRefreshingPositionAnimation();
            } else {
                startResetAnimation();
            }
        } else if(mIsDraggingBottom) {
            if (-mTranslationY > mCalculateHelper.getDefaultBottomHeight()) {
                startGoToLoadingMorePositionAnimation();
            }
        }
        mIsDraggingTop = false;
        mIsDraggingBottom = false;
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

    public void setRefreshListener(RefreshListener listener) {
        this.mRefreshListener = listener;
    }

    public void setRefreshing(boolean refreshing) {
        mIsRefreshing = refreshing;
        if (!refreshing) {
            startResetAnimation();
        }
    }

    public void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
        if (!loadingMore) {
            startResetAnimation();
        }
    }

    public void setTopStyle(int style) {
        this.mTopStyle = style;
    }

    public void setBottomStyle(int style) {
        this.mBottomStyle = style;
    }

    private void init() {
        mCalculateHelper = new RefreshCalculateHelper(this);
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
        if (mTopView == null) {
            return;
        }
        mProgressController.layoutTopView(this, mTarget, mTopView);
    }

    private void layoutBottomView() {
        if (mBottomView == null) {
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
        if (mTarget instanceof RecyclerView) {
            return mTarget.canScrollVertically(-1);
        }
        if (mTarget instanceof IRefreshListView) {
            return ((IRefreshListView) mTarget).canScrollUp();
        }
        return false;
    }

    private boolean canTargetScrollDown() {
        if (mTarget instanceof RecyclerView) {
            return mTarget.canScrollVertically(1);
        }
        if (mTarget instanceof IRefreshListView) {
            return ((IRefreshListView) mTarget).canScrollDown();
        }
        return false;
    }

    private boolean isRefreshingOrLoadingMore() {
        return mIsRefreshing || mIsLoadingMore;
    }

    private void startResetAnimation() {
        if (mTranslationY == 0) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTarget, "translationY", mTranslationY, 0);
        animator.setDuration(ANIM_DURATION);
        animator.start();
        mTranslationY = 0;
    }

    private void startGoToRefreshingPositionAnimation() {
        int position = mCalculateHelper.getDefaultRefreshTrigger();
        mIsRefreshing = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTarget, "translationY", mTranslationY, position);
        animator.setDuration(ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mIsRefreshing && mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                    return;
                }
                if (mIsRefreshing && mRefreshListener == null) {
                    mIsRefreshing = false;
                }
            }
        });
        animator.start();
        mTranslationY = position;
    }

    private void startGoToLoadingMorePositionAnimation() {
        int position = -mCalculateHelper.getDefaultBottomHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTarget, "translationY", mTranslationY, position);
        animator.setDuration(ANIM_DURATION);
        animator.start();
        mTranslationY = position;
    }
}
