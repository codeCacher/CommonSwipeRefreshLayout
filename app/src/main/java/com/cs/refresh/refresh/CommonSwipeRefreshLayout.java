package com.cs.refresh.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.cs.refresh.R;

/**
 * Created by CuiShun on 2018/1/20.
 * 提供下拉刷新和上拉加载更多功能的layout
 */

public class CommonSwipeRefreshLayout extends FrameLayout implements NestedScrollingParent,
        NestedScrollingChild {

    private static final String TAG = CommonSwipeRefreshLayout.class.getSimpleName();

    /**
     * 侵入式，list不会移动，需要controller控制top和bottom的移动
     */
    public static final int REFRESH_STYPE_INTRUSIVE = 0;
    /**
     * 非侵入式，top和bottom会跟随list移动，controller不需要控制移动
     */
    public static final int REFRESH_STYPE_NONE_INTRUSIVE = 1;

    private static final long ANIM_DURATION = 200;

    private View mTarget;
    private IRefreshProgressViewController mProgressController;

    private int mTopStyle;
    private int mBottomStyle;

    private int mTranslationY;

    private RefreshListener mRefreshListener;

    private boolean mIsLoadingMore;
    private boolean mIsRefreshing;
    private boolean mIsDraggingTop;
    private boolean mIsDraggingBottom;
    private boolean mCancelTouch;
    private boolean mStartFling;

    private boolean mIsEnableRefresh;
    private boolean mIsEnableLoadMore;

    private Scroller mScroller;
    private int mMaximumVelocity;
    private int mMinimumVelocity;

    private RefreshCalculateHelper mCalculateHelper;

    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (!mIsEnableLoadMore) {
                return;
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mStartFling) {
                mStartFling = false;
                mScroller.computeScrollOffset();
                float currVelocity = mScroller.getCurrVelocity();
                mScroller.abortAnimation();
                if (!canTargetScrollDown() && canTargetScrollUp() && !mIsRefreshing) {
                    long duration = mCalculateHelper.calculateBottomAnimDuration(currVelocity);
                    Log.i(TAG, "SCROLL_STATE_IDLE speed:" + currVelocity + " duration:" + duration);
                    startGoToLoadingMorePositionAnimation(duration);
                }
            }
        }
    };

    public CommonSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CommonSwipeRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTarget instanceof RecyclerView) {
            ((RecyclerView) mTarget).removeOnScrollListener(mScrollListener);
        } else if (mTarget instanceof IRefreshListView) {
            ((IRefreshListView) mTarget).removeOnScrollListener(mScrollListener);
        }
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
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.i(TAG, "onNestedFling");
        mStartFling = true;
        if (consumed) {
            mScroller.abortAnimation();
            mScroller.fling(0, 0, (int) velocityX, (int) velocityY, 0, 0, mMinimumVelocity, mMaximumVelocity);
        }
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        if (mIsDraggingTop) {
            int topTranslationY = mCalculateHelper.getTopTranslationY(mIsRefreshing);
            int newY = mTranslationY - dy;
            if (!mCalculateHelper.isSameSymbol(mTranslationY - topTranslationY, newY - topTranslationY)) {
                mIsDraggingTop = false;
                mTranslationY = topTranslationY;
            } else {
                mTranslationY = mCalculateHelper.calculateTopTranslationY(mTranslationY, dy);
                consumed[1] = dy;
            }
            if (mProgressController != null) {
                mProgressController.onTopDragScroll(mTranslationY, mTopStyle);
            }
            if (mTopStyle == REFRESH_STYPE_NONE_INTRUSIVE) {
                this.setScrollY(-mTranslationY);
            }
            return;
        }
        if (dy < 0 && mTranslationY < 0) {
            mIsDraggingBottom = true;
        }
        if (mIsDraggingBottom) {
            int newY = mTranslationY - dy;
            if (!mCalculateHelper.isSameSymbol(mTranslationY, newY)) {
                mIsDraggingBottom = false;
                mTranslationY = 0;
            } else {
                mTranslationY = mCalculateHelper.calculateBottomTranslationY(mTranslationY, dy);
                consumed[1] = dy;
            }
            if (mProgressController != null) {
                mProgressController.onBottomDragScroll(mTranslationY, mBottomStyle);
            }
            if (mBottomStyle == REFRESH_STYPE_NONE_INTRUSIVE) {
                this.setScrollY(-mTranslationY);
            }
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
        boolean isDown = dyUnconsumed > 0 && !canTargetScrollDown() && canTargetScrollUp() && !mIsRefreshing;

        if (isUp && mIsEnableRefresh) {
            mIsDraggingTop = true;
            return;
        }

        if (isDown && mIsEnableLoadMore) {
            if (mBottomStyle == REFRESH_STYPE_INTRUSIVE) {
                startLoadMore();
            } else {
                mIsDraggingBottom = true;
            }
        }

        int topTranslationY = 0;
        if (mTopStyle == REFRESH_STYPE_NONE_INTRUSIVE) {
            topTranslationY = mCalculateHelper.getTopTranslationY(mIsRefreshing);
        }
        if (mTranslationY != topTranslationY && !mIsRefreshing && !mIsLoadingMore) {
            mTranslationY = topTranslationY;
            this.setScrollY(-mTranslationY);
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
                startResetAnimation(true);
            }
        } else if (mIsDraggingBottom) {
            if (-mTranslationY > mCalculateHelper.getDefaultBottomHeight()
                    && !mIsRefreshing && !canTargetScrollDown()) {
                startGoToLoadingMorePositionAnimation();
            } else {
                startResetAnimation(false);
            }
        }
        mIsDraggingTop = false;
        mIsDraggingBottom = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(TAG, "mCancelTouch:" + mCancelTouch + " onInterceptTouchEvent:" + ev.toString());
        return mCancelTouch || super.onInterceptTouchEvent(ev);
    }

    public void setRefreshProgressController(IRefreshProgressViewController controller) {
        if (controller == null) {
            return;
        }
        this.mProgressController = controller;
        mProgressController.createTopProgressView(mTopStyle);
        mProgressController.createBottomProgressView(mBottomStyle);
        if (mProgressController.getTopProgressView() != null) {
            addView(mProgressController.getTopProgressView());
        }
        if (mProgressController.getBottomProgressView() != null) {
            addView(mProgressController.getBottomProgressView());
        }
        int topRefreshTrigger = mProgressController.getTopRefreshTrigger();
        int bottomHeight = mProgressController.getBottomHeight();
        if (topRefreshTrigger > 0) {
            mCalculateHelper.setDefaultRefreshTrigger(topRefreshTrigger);
        }
        if (bottomHeight > 0) {
            mCalculateHelper.setDefaultBottomHeight(bottomHeight);
        }
        postInvalidate();
    }

    public void setRefreshListener(RefreshListener listener) {
        this.mRefreshListener = listener;
    }

    public void setRefreshing(boolean refreshing) {
        Log.i(TAG, "setRefreshing:" + refreshing);
        if (!refreshing && mIsRefreshing) {
            mIsRefreshing = false;
            startResetAnimation(true);
            if (mProgressController != null) {
                mProgressController.onFinishRefresh();
            }
        } else if (refreshing && !isRefreshingOrLoadingMore()) {
            startGoToRefreshingPositionAnimation();
            startRefresh();
        }
    }

    public void setLoadingMore(boolean loadingMore) {
        Log.i(TAG, "setLoadingMore:" + loadingMore);
        if (!loadingMore && mIsLoadingMore) {
            mIsLoadingMore = false;
            startResetAnimation(false);
            if (mProgressController != null) {
                mProgressController.onFinishLoadMore();
            }
        } else if (loadingMore && !isRefreshingOrLoadingMore()) {
            startGoToLoadingMorePositionAnimation();
            startLoadMore();
        }
    }

    public void setRefreshEnable(boolean enable) {
        this.mIsEnableRefresh = enable;
    }

    public void setLoadMoreEnable(boolean enable) {
        this.mIsEnableLoadMore = enable;
    }

    private void init(Context context) {
        mCalculateHelper = new RefreshCalculateHelper(this);
        mScroller = new Scroller(context);
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonSwipeRefreshLayout, defStyleAttr, 0);
        mTopStyle = typedArray.getBoolean(R.styleable.CommonSwipeRefreshLayout_top_intrusive, true) ? REFRESH_STYPE_INTRUSIVE : REFRESH_STYPE_NONE_INTRUSIVE;
        mBottomStyle = typedArray.getBoolean(R.styleable.CommonSwipeRefreshLayout_bottom_intrusive, true) ? REFRESH_STYPE_INTRUSIVE : REFRESH_STYPE_NONE_INTRUSIVE;
        mIsEnableRefresh = typedArray.getBoolean(R.styleable.CommonSwipeRefreshLayout_enable_refresh, true);
        mIsEnableLoadMore = typedArray.getBoolean(R.styleable.CommonSwipeRefreshLayout_enable_load_more, true);
        typedArray.recycle();
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
            mProgressController.layoutTopView(this, mTarget, mTopStyle);
        }
    }

    private void layoutBottomView() {
        if (mProgressController != null) {
            mProgressController.layoutBottomView(this, mTarget, mBottomStyle);
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
            throw new IllegalStateException(this.getClass().getSimpleName() + "的子View必须为RecyclerView或继承AbstractRefreshListView的View");
        }
        if (mTarget instanceof RecyclerView) {
            ((RecyclerView) mTarget).addOnScrollListener(mScrollListener);
        } else if (mTarget instanceof IRefreshListView) {
            ((IRefreshListView) mTarget).addOnScrollListener(mScrollListener);
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

    private void startResetAnimation(final boolean isTop) {
        Log.i(TAG, "startResetAnimation");
        if (mTranslationY == 0) {
            return;
        }
        if (mProgressController != null) {
            if (isTop) {
                mProgressController.onTopTranslationAnimation(mTranslationY, 0, ANIM_DURATION, mTopStyle);
            } else {
                mProgressController.onBottomTranslationAnimation(mTranslationY, 0, ANIM_DURATION, mBottomStyle);
            }
        }
        if ((isTop && mTopStyle == REFRESH_STYPE_INTRUSIVE) ||
                (!isTop && mBottomStyle == REFRESH_STYPE_INTRUSIVE)) {
            mTranslationY = 0;
            return;
        }

        mCancelTouch = true;
        final ObjectAnimator animator = ObjectAnimator.ofInt(this, "scrollY", -mTranslationY, 0);
        animator.setDuration(ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mProgressController != null) {
                    if (isTop) {
                        mProgressController.onListTopTranslationAnimationStart(mTranslationY, 0, ANIM_DURATION, mTopStyle);
                    } else {
                        mProgressController.onListBottomTranslationAnimationStart(mTranslationY, 0, ANIM_DURATION, mBottomStyle);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCancelTouch = false;
                animator.removeAllListeners();
                if (mProgressController != null) {
                    if (isTop) {
                        mProgressController.onListTopTranslationAnimationEnd(mTranslationY, 0, ANIM_DURATION, mTopStyle);
                    } else {
                        mProgressController.onListBottomTranslationAnimationEnd(mTranslationY, 0, ANIM_DURATION, mBottomStyle);
                    }
                }
                mTranslationY = 0;
            }
        });
        animator.start();
    }

    private void startRefresh() {
        if (!isRefreshingOrLoadingMore()) {
            mIsRefreshing = true;
            if (mProgressController != null) {
                mProgressController.onStartRefresh();
            }
            if (mRefreshListener != null) {
                mRefreshListener.onRefresh();
            }
        }
    }

    private void startLoadMore() {
        if (!isRefreshingOrLoadingMore()) {
            mIsLoadingMore = true;
            if (mProgressController != null) {
                mProgressController.onStartLoadMore();
            }
            if (mRefreshListener != null) {
                mRefreshListener.onLoadMore();
            }
        }
    }

    private void startGoToRefreshingPositionAnimation() {
        final int position = mCalculateHelper.getDefaultRefreshTrigger();
        if (mProgressController != null) {
            mProgressController.onTopTranslationAnimation(mTranslationY, position, ANIM_DURATION, mTopStyle);
        }
        if (mTopStyle == REFRESH_STYPE_INTRUSIVE) {
            mTranslationY = position;
            return;
        }
        mCancelTouch = true;
        final ObjectAnimator animator = ObjectAnimator.ofInt(this, "scrollY", -mTranslationY, -position);
        animator.setDuration(ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mProgressController != null) {
                    mProgressController.onListTopTranslationAnimationStart(mTranslationY, position, ANIM_DURATION, mTopStyle);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCancelTouch = false;
                animator.removeAllListeners();
                if (mProgressController != null) {
                    mProgressController.onListTopTranslationAnimationEnd(mTranslationY, position, ANIM_DURATION, mTopStyle);
                }
                mTranslationY = position;
            }
        });
        animator.start();
    }

    private void startGoToLoadingMorePositionAnimation(final long duration) {
        final int position = -mCalculateHelper.getDefaultBottomHeight();
        if (mProgressController != null) {
            mProgressController.onBottomTranslationAnimation(mTranslationY, position, duration, mBottomStyle);
        }
        if (mBottomStyle == REFRESH_STYPE_INTRUSIVE) {
            mTranslationY = position;
            startLoadMore();
            return;
        }
        mCancelTouch = true;
        final ObjectAnimator animator = ObjectAnimator.ofInt(this, "scrollY", -mTranslationY, -position);
        animator.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mProgressController != null) {
                    mProgressController.onListBottomTranslationAnimationStart(mTranslationY, position, duration, mBottomStyle);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCancelTouch = false;
                animator.removeAllListeners();
                startLoadMore();
                if (mProgressController != null) {
                    mProgressController.onListBottomTranslationAnimationEnd(mTranslationY, position, duration, mBottomStyle);
                }
                mTranslationY = position;
            }
        });
        animator.start();
    }

    private void startGoToLoadingMorePositionAnimation() {
        startGoToLoadingMorePositionAnimation(ANIM_DURATION);
    }
}
