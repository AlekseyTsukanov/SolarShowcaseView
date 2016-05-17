package com.skytech.solar.target;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

public class TargetView implements ITarget {
    private Activity mActivity;
    private View mView;

    public TargetView(View v) {
        mView = v;
    }

    public TargetView(Activity activity, int resId) {
        mView = activity.findViewById(resId);
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        int x = location[0] + mView.getWidth() / 2;
        int y = location[1] + mView.getHeight() / 2;
        return new Point(x, y);
    }

    @Override
    public Rect getRectangleBounds() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        return new Rect(location[0], location[1], location[0] + mView.getMeasuredWidth(), location[1] + mView.getMeasuredHeight());
    }
}
