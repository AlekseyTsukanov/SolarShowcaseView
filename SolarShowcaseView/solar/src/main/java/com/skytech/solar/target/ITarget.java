package com.skytech.solar.target;

import android.graphics.Point;
import android.graphics.Rect;

public interface ITarget {
    Point getPoint();
    Rect getRectangleBounds();

    ITarget NONE = new ITarget() {
        @Override
        public Point getPoint() {
            return new Point(1000000, 1000000);
        }

        @Override
        public Rect getRectangleBounds() {
            Point point = getPoint();
            return new Rect(point.x - 190, point.y - 190, point.x + 190, point.y + 190);
        }
    };
}
