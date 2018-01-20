package com.cs.refresh.refresh;

import android.view.View;

/**
 * Created by CuiShun on 2018/1/20.
 */

public class BaseProgressViewController implements IRefreshProgressViewController {
    @Override
    public View createTopProgressView() {
        return null;
    }

    @Override
    public View createBottomProgressView() {
        return null;
    }

    @Override
    public void layoutTopView(View parent, View refreshListView, View topView) {

    }

    @Override
    public void layoutBottomView(View parent, View refreshListView, View bottomView) {

    }
}
