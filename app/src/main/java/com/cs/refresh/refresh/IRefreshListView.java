package com.cs.refresh.refresh;

import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.ScrollingView;
import android.support.v7.widget.RecyclerView;

/**
 * Created by CuiShun on 2018/1/20.
 * 要想使用自定义的list，请实现该接口
 */

public interface IRefreshListView extends ScrollingView, NestedScrollingChild2 {

    boolean canScrollUp();

    boolean canScrollDown();

    void removeOnScrollListener(RecyclerView.OnScrollListener mScrollListener);

    void addOnScrollListener(RecyclerView.OnScrollListener mScrollListener);
}
