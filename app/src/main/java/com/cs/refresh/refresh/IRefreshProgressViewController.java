package com.cs.refresh.refresh;

import android.view.View;

/**
 * Created by CuiShun on 2018/1/20.
 * 下拉刷新和上拉加载更多的加载View的控制器
 */

public interface IRefreshProgressViewController {
    View createTopProgressView();

    View createBottomProgressView();

    void layoutTopView(View parent, View refreshListView, View topView);

    void layoutBottomView(View parent, View refreshListView, View bottomView);
}
