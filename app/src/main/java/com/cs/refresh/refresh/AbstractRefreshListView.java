package com.cs.refresh.refresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.ScrollingView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by CuiShun on 2018/1/20.
 */

public abstract class AbstractRefreshListView extends View implements ScrollingView, NestedScrollingChild2 {
    public AbstractRefreshListView(Context context) {
        super(context);
    }

    public AbstractRefreshListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractRefreshListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract boolean canScrollUp();

    public abstract boolean canScrollDown();

    public abstract void removeOnScrollListener(RecyclerView.OnScrollListener mScrollListener);

    public abstract void addOnScrollListener(RecyclerView.OnScrollListener mScrollListener);

    @Override
    abstract public boolean startNestedScroll(int axes, int type);

    @Override
    abstract public void stopNestedScroll(int type);

    @Override
    abstract public boolean hasNestedScrollingParent(int type);

    @Override
    abstract public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type);

    @Override
    abstract public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type);

    @Override
    abstract public int computeHorizontalScrollRange();

    @Override
    abstract public int computeHorizontalScrollOffset();

    @Override
    abstract public int computeHorizontalScrollExtent();

    @Override
    abstract public int computeVerticalScrollRange();

    @Override
    abstract public int computeVerticalScrollOffset();

    @Override
    abstract public int computeVerticalScrollExtent();
}
