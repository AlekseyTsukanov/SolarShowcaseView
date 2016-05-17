package com.skytech.solar.view.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.skytech.solar.target.ITarget;

public class Rectangle implements IShape {
    private boolean mIsFullWidth = false;
    private int mWidth = 0;
    private int mHeight = 0;
    private boolean mAdjustToTarget = true;
    private Rect rect;

    public Rectangle(int width, int height) {
        mWidth = width;
        mHeight = height;
        init();
    }

    public Rectangle(Rect bounds) {
        this(bounds, false);
    }

    public Rectangle(Rect bounds, boolean isFullWidth) {
        this.mIsFullWidth = isFullWidth;
        mHeight = bounds.height();
        if (isFullWidth) {
            mWidth = Integer.MAX_VALUE;
        } else {
            mWidth = bounds.width();
        }
        init();
    }

    public boolean isAdjustToTarget() {
        return mAdjustToTarget;
    }

    public void setAdjustToTarget(boolean adjustToTarget) {
        mAdjustToTarget = adjustToTarget;
    }

    private void init() {
        rect = new Rect((-mWidth) / 2, (-mHeight) / 2, mWidth / 2, mHeight / 2);
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y, int padding) {
        if (!rect.isEmpty()) {
            canvas.drawRect(
                    rect.left + x - padding,
                    rect.top + y - padding,
                    rect.right + x + padding,
                    rect.bottom + y + padding,
                    paint
            );
        }
    }

    @Override
    public void updateTarget(ITarget target) {
        if (mAdjustToTarget) {
            Rect bounds = target.getRectangleBounds();
            mHeight = bounds.height();
            if (mIsFullWidth) {
                mWidth = Integer.MAX_VALUE;
            } else {
                mWidth = bounds.width();
            }
            init();
        }
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }
}
