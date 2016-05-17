package com.skytech.solar.view.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.skytech.solar.target.ITarget;

public class None implements IShape {

    @Override
    public void updateTarget(ITarget target) {}

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y, int padding) {}

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
