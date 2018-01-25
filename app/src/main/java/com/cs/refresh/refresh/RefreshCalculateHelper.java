package com.cs.refresh.refresh;

import android.view.View;

/**
 * Created by CuiShun on 2018/1/20.
 */

public class RefreshCalculateHelper {

    private static final int DEFAULT_REFRESH_TRIGGER = 64;
    private static final int DEFAULT_BOTTOM_HEIGHT = 64;
    private static final int MAX_TOP_DRAG_LENGTH = 150;
    private static final int MAX_BOTTOM_DRAG_LENGTH = 150;

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

    public int getDefaultBottomHeight() {
        return (int) (DEFAULT_BOTTOM_HEIGHT * mDensity);
    }

    public int ensureTopTranslationY(int y) {
        int maxTopDragLength = getMaxTopDragLength();
        if (y > 0 && y > maxTopDragLength) {
            y = maxTopDragLength;
        }
        return y;
    }

    public int ensureBottomTranslationY(int y) {
        int maxBottomDragLength = getMaxBottomDragLength();
        if (y < 0 && -y > maxBottomDragLength) {
            y = -maxBottomDragLength;
        }
        return y;
    }

    public int calculateTopTranslationY(int y, int dy) {
        y = ensureTopTranslationY(y);
        if (dy < 0) {
            dy = (int) ((1 - 1f * y / getMaxTopDragLength()) * dy);
        }
        return y - dy;
    }

    public int calculateBottomTranslationY(int y, int dy) {
        y = ensureBottomTranslationY(y);
        if (dy > 0) {
            dy = (int) ((1 + 1f * y / getMaxBottomDragLength()) * dy);
        }
        return y - dy;
    }
}
