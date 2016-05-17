package com.skytech.solar.view.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.skytech.solar.target.ITarget;

public class Circle implements IShape {
    private int mRadius = 200;
    private boolean mAdjustToTarget = true;

    public Circle(){}

    public Circle(int radius) {
        mRadius = radius;
    }

    public Circle(Rect rectangleBounds) {
        getRadiusByBounds(rectangleBounds);
    }

    public Circle(ITarget target) {
        target.getRectangleBounds();
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    public boolean isAdjustToTarget() {
        return mAdjustToTarget;
    }

    public void setAdjustToTarget(boolean mAdjustToTarget) {
        this.mAdjustToTarget = mAdjustToTarget;
    }

    private int getRadiusByBounds(Rect bounds) {
        return Math.max(bounds.width(), bounds.height()) / 2;
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y, int padding) {
        if (mRadius > 0) {
            canvas.drawCircle(x, y, mRadius + padding, paint);
        }
    }

    @Override
    public void updateTarget(ITarget target) {
        if (mAdjustToTarget)
            mRadius = getRadiusByBounds(target.getRectangleBounds());
    }

    @Override
    public int getWidth() {
        return mRadius * 2;
    }

    @Override
    public int getHeight() {
        return mRadius * 2;
    }
}
