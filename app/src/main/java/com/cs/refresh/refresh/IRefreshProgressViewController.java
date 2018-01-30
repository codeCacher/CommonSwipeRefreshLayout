package com.cs.refresh.refresh;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by CuiShun on 2018/1/20.
 * 下拉刷新和上拉加载更多的加载View的控制器
 */

public interface IRefreshProgressViewController {
    void createTopProgressView();

    void createBottomProgressView();

    View getTopProgressView();

    View getBottomProgressView();

    void onMeasureTopView(@NonNull ViewGroup parent, @NonNull View refreshListView);

    void onMeasureBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView);

    void layoutTopView(@NonNull ViewGroup parent, @NonNull View refreshListView, int style);

    void layoutBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView, int style);

    void onTopDragScroll(int translationY, int style);

    void onBottomDragScroll(int translationY, int style);

    void onTopTranslationAnimation(int desPosition, long duration);

    void onBottomTranslationAnimation(int desPosition, long duration);

    void onStartRefresh();

    void onFinishRefresh();

    void onStartLoadMore();

    void onFinishLoadMore();
}
