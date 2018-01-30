package com.cs.refresh.refresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

/**
 * Created by CuiShun on 2018/1/20.
 */

public class BaseProgressViewController implements IRefreshProgressViewController {

    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    static final int CIRCLE_DIAMETER = 40;

    private final Context mContext;
    private CircleImageView mTopCircleView;
    private CircularProgressDrawable mTopProgress;
    private CircleImageView mBottomCircleView;
    private CircularProgressDrawable mBottomProgress;

    private int mCircleDiameter;

    public BaseProgressViewController(Context context) {
        this.mContext = context;
        mCircleDiameter = (int) (context.getResources().getDisplayMetrics().density * CIRCLE_DIAMETER);
    }

    @Override
    public void createTopProgressView() {
        mTopCircleView = new CircleImageView(mContext, CIRCLE_BG_LIGHT);
        mTopProgress = new CircularProgressDrawable(mContext);
        mTopProgress.setStyle(CircularProgressDrawable.DEFAULT);
        mTopCircleView.setImageDrawable(mTopProgress);
        mTopCircleView.setVisibility(View.GONE);
    }

    @Override
    public void createBottomProgressView() {
        mBottomCircleView = new CircleImageView(mContext, CIRCLE_BG_LIGHT);
        mBottomProgress = new CircularProgressDrawable(mContext);
        mBottomProgress.setStyle(CircularProgressDrawable.DEFAULT);
        mBottomCircleView.setImageDrawable(mBottomProgress);
        mBottomCircleView.setVisibility(View.GONE);
    }

    @Override
    public View getTopProgressView() {
        return mTopCircleView;
    }

    @Override
    public View getBottomProgressView() {
        return mBottomCircleView;
    }

    @Override
    public void onMeasureTopView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        mTopCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
    }

    @Override
    public void onMeasureBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        mBottomCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
    }

    @Override
    public void layoutTopView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        final int width = parent.getMeasuredWidth();
        int circleWidth = mTopCircleView.getMeasuredWidth();
        int circleHeight = mTopCircleView.getMeasuredHeight();
        mTopCircleView.layout((width / 2 - circleWidth / 2), refreshListView.getTop() - circleHeight,
                (width / 2 + circleWidth / 2), refreshListView.getTop());
    }

    @Override
    public void layoutBottomView(@NonNull ViewGroup parent, @NonNull View refreshListView) {
        int width = parent.getMeasuredWidth();
        int circleWidth = mBottomCircleView.getMeasuredWidth();
        int circleHeight = mBottomCircleView.getMeasuredHeight();
        mBottomCircleView.layout((width / 2 - circleWidth / 2), (int) (refreshListView.getBottom() - circleHeight * 1.5f),
                (width / 2 + circleWidth / 2), (int) (refreshListView.getBottom() - circleHeight * 0.5f));
    }

    @Override
    public void onTopDragScroll(int dyTotal, int dyTranslation, int style) {

    }

    @Override
    public void onBottomDragScroll(int dyTotal, int dyTranslation, int style) {

    }
}
