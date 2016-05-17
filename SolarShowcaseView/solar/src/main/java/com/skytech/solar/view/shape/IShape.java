package com.skytech.solar.view.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.skytech.solar.target.ITarget;

public interface IShape {

    void draw(Canvas canvas, Paint paint, int x, int y, int padding);

    int getWidth();

    int getHeight();

    void updateTarget(ITarget target);
}
