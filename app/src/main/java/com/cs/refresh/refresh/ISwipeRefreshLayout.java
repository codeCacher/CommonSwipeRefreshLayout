package com.cs.refresh.refresh;

/**
 * Created by CuiShun on 2018/1/20.
 * 下拉刷新和上拉加载更多的Layout接口
 */
public interface ISwipeRefreshLayout {

    /**
     * 设置上下拉刷新progressView控制器
     *
     * @param controller IRefreshProgressViewController
     */
    void setRefreshProgressController(IRefreshProgressViewController controller);

    /**
     * 刷新监听
     *
     * @param refreshListener 刷新监听
     */
    void setRefreshListener(RefreshListener refreshListener);

    /**
     * 开始、结束刷新
     *
     * @param refreshing true:开始刷新 false:结束刷新
     */
    void setRefreshing(boolean refreshing);

    /**
     * 开始、结束加载更多
     *
     * @param loadingMore true:开始加载更多 false:结束加载更多
     */
    void setLoadingMore(boolean loadingMore);

    /**
     * 设置是否可以加载更多数据，和{@link #setLoadMoreEnable(boolean isLoadMore)｝
     * 不同的是当设置为false时仍然可以显示底部的view用于提示没有更多数据
     *
     * @param hasMoreData 是否有更多数据可以加载
     */
    void setHasMoreData(boolean hasMoreData);

    /**
     * 是否可以下拉刷新
     *
     * @param refreshEnable 是否可以下拉刷新
     */
    void setRefreshEnable(boolean refreshEnable);

    /**
     * 是否可以加载更多
     *
     * @param isLoadMore 是否可以加载更多
     */
    void setLoadMoreEnable(boolean isLoadMore);
}
