package com.skytech.solar.animation;

import android.graphics.Point;
import android.view.View;

import com.skytech.solar.view.showcase.SolarShowcaseView;

public interface IShowcaseAnimation {
    void fadeInView(View target, long duration, AnimationStartListener listener);
    void fadeOutView(View target, long duration, AnimationEndListener listener);
    void moveTargetToPoint(SolarShowcaseView showcaseView, Point point);

    public interface AnimationStartListener {
        void onAnimationStart();
    }

    public interface AnimationEndListener {
        void onAnimationEnd();
    }
}
