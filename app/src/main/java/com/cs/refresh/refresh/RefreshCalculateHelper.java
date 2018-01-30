package com.cs.refresh.refresh;

import android.view.View;

/**
 * Created by CuiShun on 2018/1/20.
 */

public class RefreshCalculateHelper {

    static final int DEFAULT_REFRESH_TRIGGER = 64;
    static final int DEFAULT_BOTTOM_HEIGHT = 50;
    static final int MAX_TOP_DRAG_LENGTH = 150;
    static final int MAX_BOTTOM_DRAG_LENGTH = 150;

    private float mDensity;

    RefreshCalculateHelper(View view) {
        mDensity = view.getResources().getDisplayMetrics().density;
    }

    boolean isSameSymbol(int a, int b) {
        return (a >= 0 && b >= 0) || (a <= 0 && b <= 0);
    }

    private int getMaxTopDragLength() {
        return (int) (MAX_TOP_DRAG_LENGTH * mDensity);
    }

    private int getMaxBottomDragLength() {
        return (int) (MAX_BOTTOM_DRAG_LENGTH * mDensity);
    }

    int getDefaultRefreshTrigger() {
        return (int) (DEFAULT_REFRESH_TRIGGER * mDensity);
    }

    int getDefaultBottomHeight() {
        return (int) (DEFAULT_BOTTOM_HEIGHT * mDensity);
    }

    private int ensureTopTranslationY(int y) {
        int maxTopDragLength = getMaxTopDragLength();
        if (y > 0 && y > maxTopDragLength) {
            y = maxTopDragLength;
        }
        return y;
    }

    private int ensureBottomTranslationY(int y) {
        int maxBottomDragLength = getMaxBottomDragLength();
        if (y < 0 && -y > maxBottomDragLength) {
            y = -maxBottomDragLength;
        }
        return y;
    }

    int calculateTopTranslationY(int y, int dy) {
        y = ensureTopTranslationY(y);
        if (dy < 0) {
            dy = (int) ((1 - 1f * y / getMaxTopDragLength()) * dy);
        }
        return y - dy;
    }

    int calculateBottomTranslationY(int y, int dy) {
        y = ensureBottomTranslationY(y);
        if (dy > 0) {
            dy = (int) ((1 + 1f * y / getMaxBottomDragLength()) * dy);
        }
        return y - dy;
    }

    int getTopTranslationY(boolean isRefresh) {
        if (isRefresh) {
            return getDefaultRefreshTrigger();
        } else {
            return 0;
        }
    }

    long calculateBottomAnimDuration(float currVelocity) {
        long t = (long) (getDefaultBottomHeight() / currVelocity * 1000 * mDensity);
        if (t > 300) {
            t = 300;
        }
        if (t < 50) {
            t = 50;
        }
        return t;
    }
}
