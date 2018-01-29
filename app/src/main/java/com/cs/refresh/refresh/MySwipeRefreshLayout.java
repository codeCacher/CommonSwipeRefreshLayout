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
import android.view.MotionEvent;
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

    private int mTopStyle;
    private int mBottomStyle;

    private int mTranslationY;
    private int mTopY;

    private RefreshListener mRefreshListener;

    private boolean mIsLoadingMore;
    private boolean mIsRefreshing;
    private boolean mIsDraggingTop;
    private boolean mIsDraggingBottom;
    private boolean mCancelTouch;

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mProgressController != null && mProgressController.getTopProgressView() != null) {
            mProgressController.onMeasureTopView(this, mTarget);
        }
        if (mProgressController != null && mProgressController.getBottomProgressView() != null) {
            mProgressController.onMeasureBottomView(this, mTarget);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        Log.i(TAG, "onNestedPreFling");
        return mIsDraggingTop || mIsDraggingBottom;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.i(TAG, "onNestedFling");
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        if (mIsRefreshing && canTargetScrollUp()) {
            return;
        }
        if (mTranslationY != 0 && !mIsRefreshing && !mIsLoadingMore) {
            consumed[1] = dy;
        }
        if (dy < 0 && mTranslationY < 0) {
            consumed[1] = dy;
            mIsDraggingBottom = true;
        }
        if (mIsDraggingTop) {
            int newY = mTranslationY - dy;
            if (!mCalculateHelper.isSameSymbol(mTranslationY, newY)) {
                mIsDraggingTop = false;
                mTranslationY = 0;
            } else if (mTranslationY > 0 || (mTranslationY == 0 && !canTargetScrollUp())) {
                mTranslationY = mCalculateHelper.calculateTopTranslationY(mTranslationY, dy);
            }
            if (mTranslationY < mCalculateHelper.getDefaultRefreshTrigger() && mIsRefreshing) {
                mTranslationY = mCalculateHelper.getDefaultBottomHeight();
            } else if (!canTargetScrollUp()) {
                consumed[1] = dy;
            }
            mTarget.setTranslationY(mTranslationY);
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

        if (isDown) {
            mIsDraggingBottom = true;
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
        if (mIsDraggingTop) {
            if (mTranslationY > mCalculateHelper.getDefaultRefreshTrigger()
                    && mRefreshListener != null && !mIsLoadingMore) {
                startGoToRefreshingPositionAnimation();
                startRefresh();
            } else {
                startResetAnimation();
            }
        } else if (mIsDraggingBottom) {
            if (-mTranslationY > mCalculateHelper.getDefaultBottomHeight()
                    && mRefreshListener != null && !mIsRefreshing) {
                startGoToLoadingMorePositionAnimation();
                startLoadMore();
            } else {
                startResetAnimation();
            }
        }
        mIsDraggingTop = false;
        mIsDraggingBottom = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("cuishun", "onInterceptTouchEvent" + ev.toString());
        if (mCancelTouch) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setRefreshProgressController(IRefreshProgressViewController controller) {
        if (controller == null) {
            return;
        }
        this.mProgressController = controller;
        controller.createTopProgressView();
        controller.createBottomProgressView();
        if (mProgressController != null && mProgressController.getTopProgressView() != null) {
            addView(mProgressController.getTopProgressView());
        }
        if (mProgressController != null && mProgressController.getBottomProgressView() != null) {
            addView(mProgressController.getBottomProgressView());
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
        if (mProgressController != null) {
            mProgressController.layoutTopView(this, mTarget);
        }
    }

    private void layoutBottomView() {
        if (mProgressController != null) {
            mProgressController.layoutBottomView(this, mTarget);
        }
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
        mCancelTouch = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTarget, "translationY", mTranslationY, 0);
        animator.setDuration(ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCancelTouch = false;
            }
        });
        animator.start();
        mTranslationY = 0;
    }

    private void startRefresh() {
        if (!mIsRefreshing && mRefreshListener != null) {
            mIsRefreshing = true;
            mRefreshListener.onRefresh();
        }
    }

    private void startLoadMore() {
        if (!mIsLoadingMore && mRefreshListener != null) {
            mIsLoadingMore = true;
            mRefreshListener.onLoadMore();
        }
    }

    private void startGoToRefreshingPositionAnimation() {
        mCancelTouch = true;
        int position = mCalculateHelper.getDefaultRefreshTrigger();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTarget, "translationY", mTranslationY, position);
        animator.setDuration(ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCancelTouch = false;
            }
        });
        animator.start();
        mTranslationY = position;
    }

    private void startGoToLoadingMorePositionAnimation() {
        mCancelTouch = true;
        int position = -mCalculateHelper.getDefaultBottomHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTarget, "translationY", mTranslationY, position);
        animator.setDuration(ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCancelTouch = false;
            }
        });
        animator.start();
        mTranslationY = position;
    }
}
