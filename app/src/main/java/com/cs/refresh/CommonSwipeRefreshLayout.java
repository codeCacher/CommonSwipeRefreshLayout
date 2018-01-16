package com.cs.refresh;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2018/1/16.
 */

public class CommonSwipeRefreshLayout extends SwipeRefreshLayout {

    private View mTarget;
    private RefreshListener mListener;
    private boolean mIsLoadingMore;

    public CommonSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CommonSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnRefreshListener(RefreshListener listener) {
        this.mListener = listener;
    }

    public void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(mTarget != null || getChildCount() <= 0) {
            return;
        }
        View child = getChildAt(0);
        if(child instanceof RecyclerView) {
            mTarget = child;
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     *         scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollDown() {
        return mTarget.canScrollVertically(1);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyUnconsumed > 0 && !canChildScrollDown()) {
            if(mListener != null && !mIsLoadingMore) {
                mIsLoadingMore = true;
                mListener.onLoadMore();
            }
        }
    }
}
