package com.cs.refresh.refresh;

import android.view.View;

/**
 * Created by CuiShun on 2018/1/20.
 */

public class RefreshCalculateHelper {

    private static final int DEFAULT_REFRESH_TRIGGER = 64;
    private static final int MAX_TOP_DRAG_LENGTH = 80;
    private static final int MAX_BOTTOM_DRAG_LENGTH = 80;

    private float mDensity;

    public RefreshCalculateHelper(View view) {
        mDensity = view.getResources().getDisplayMetrics().density;
    }

    public boolean isSameSymbol(int a, int b) {
        return (a >= 0 && b >= 0) || (a <= 0 && b <= 0);
    }

    public int getMaxTopDragLength() {
        return (int) (MAX_TOP_DRAG_LENGTH * mDensity);
    }

    public int getMaxBottomDragLength() {
        return (int) (MAX_BOTTOM_DRAG_LENGTH * mDensity);
    }

    public int getDefaultRefreshTrigger() {
        return (int) (DEFAULT_REFRESH_TRIGGER * mDensity);
    }

    public int ensureTranslationY(int y) {
        if (y > 0 && y > getMaxTopDragLength()) {
            y = getMaxTopDragLength();
        }
        if (y < 0 && -y > getMaxBottomDragLength()) {
            y = -getMaxBottomDragLength();
        }
        return y;
    }
}
