package com.cs.refresh.refresh;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by CuiShun on 2018/1/20.
 * 下拉刷新和上拉加载更多的加载View的控制器
 */

public interface IRefreshProgressViewController {
    /**
     * 创建顶部下拉刷新的progress view
     *
     * @param style 刷新的样式
     */
    void createTopProgressView(int style);

    /**
     * 创建底部加载更多的progress view
     *
     * @param style 刷新的样式
     */
    void createBottomProgressView(int style);

    /**
     * 将底部刷新View设置为没有更多数据的提示
     *
     * @param style 刷新的样式
     */
    void setBottomNoMoreDataView(int style);

    /**
     * 将底部刷新View设置为正在刷新的提示
     *
     * @param style 刷新的样式
     */
    void setBottomLoadingView(int style);

    /**
     * @return 返回 {@link #createTopProgressView(int style)} 创建的View
     */
    View getTopProgressView();

    /**
     * @return 返回 {@link #createBottomProgressView(int style)} 创建的View
     */
    View getBottomProgressView();

    /**
     * measure TopView
     *
     * @param parent          {@link CommonSwipeRefreshLayout}
     * @param refreshListView 列表
     */
    void onMeasureTopView(@NonNull ViewGroup parent, @NonNull View refreshListView);

    /**
     * measure BottomView
     *
     * @param parent          {@link CommonSwipeRefreshLayout}
     * @param refreshListView 列表
     */
    void onMeasureBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView);

    /**
     * layout TopView
     *
     * @param parent          {@link CommonSwipeRefreshLayout}
     * @param refreshListView 列表
     * @param style           刷新的样式
     */
    void layoutTopView(@NonNull ViewGroup parent, @NonNull View refreshListView, int style);

    /**
     * layout BottomView
     *
     * @param parent          {@link CommonSwipeRefreshLayout}
     * @param refreshListView 列表
     * @param style           刷新的样式
     */
    void layoutBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView, int style);

    /**
     * 当下拉的时候调用
     *
     * @param translationY 下拉的偏移量
     * @param style        刷新的样式
     */
    void onTopDragScroll(int translationY, int style);

    /**
     * 当上拉的时候调用
     *
     * @param translationY 上拉的偏移量
     * @param style        刷新的样式
     */
    void onBottomDragScroll(int translationY, int style);

    /**
     * 当顶部执行动画前调用
     *
     * @param startPosition 动画开始的位置
     * @param desPosition   动画结束的位置
     * @param duration      动画时长
     * @param style         刷新样式
     */
    void onTopTranslationAnimation(int startPosition, int desPosition, long duration, int style);

    /**
     * 当底部执行动画前调用
     *
     * @param startPosition 动画开始的位置
     * @param desPosition   动画结束的位置
     * @param duration      动画时长
     * @param style         刷新样式
     */
    void onBottomTranslationAnimation(int startPosition, int desPosition, long duration, int style);

    /**
     * 当顶部开始执行动画时调用
     *
     * @param startPosition 动画开始的位置
     * @param desPosition   动画结束的位置
     * @param duration      动画时长
     * @param style         刷新样式
     */
    void onListTopTranslationAnimationStart(int startPosition, int desPosition, long duration, int style);

    /**
     * 当顶部结束执行动画时调用
     *
     * @param startPosition 动画开始的位置
     * @param desPosition   动画结束的位置
     * @param duration      动画时长
     * @param style         刷新样式
     */
    void onListTopTranslationAnimationEnd(int startPosition, int desPosition, long duration, int style);

    /**
     * 当底部开始执行动画时调用
     *
     * @param startPosition 动画开始的位置
     * @param desPosition   动画结束的位置
     * @param duration      动画时长
     * @param style         刷新样式
     */
    void onListBottomTranslationAnimationStart(int startPosition, int desPosition, long duration, int style);

    /**
     * 当底部结束执行动画时调用
     *
     * @param startPosition 动画开始的位置
     * @param desPosition   动画结束的位置
     * @param duration      动画时长
     * @param style         刷新样式
     */
    void onListBottomTranslationAnimationEnd(int startPosition, int desPosition, long duration, int style);

    /**
     * 当开始刷新时调用
     */
    void onStartRefresh();

    /**
     * 结束刷新时调用
     */
    void onFinishRefresh();

    /**
     * 加载更多时调用
     */
    void onStartLoadMore();

    /**
     * 结束加载更多时调用
     */
    void onFinishLoadMore();

    /**
     * 设置下拉刷新的触发距离，单位DP
     *
     * @return 下拉刷新的触发距离，单位DP
     */
    int getTopRefreshTrigger();

    /**
     * 设置加载更多的触发距离，单位DP
     *
     * @return 加载更多的触发距离，单位DP
     */
    int getBottomHeight();
}
