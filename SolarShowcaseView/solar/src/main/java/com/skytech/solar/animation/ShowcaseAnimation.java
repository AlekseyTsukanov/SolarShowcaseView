package com.skytech.solar.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.skytech.solar.view.showcase.SolarShowcaseView;

public class ShowcaseAnimation implements IShowcaseAnimation {
    private static final String ALPHA = "alpha";
    private static final float INVISIBLE = 0f;
    private static final float VISIBLE = 1f;
    private AccelerateDecelerateInterpolator mInterpolator;

    public ShowcaseAnimation() {
        mInterpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public void fadeInView(View targetView, long duration, final AnimationStartListener listener) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(targetView, ALPHA, INVISIBLE, VISIBLE);
        objectAnimator.setDuration(duration).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                listener.onAnimationStart();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        objectAnimator.start();
    }

    @Override
    public void fadeOutView(View target, long duration, final AnimationEndListener listener) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(target, ALPHA, INVISIBLE);
        oa.setDuration(duration).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                listener.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        oa.start();
    }

    @Override
    public void moveTargetToPoint(SolarShowcaseView showcaseView, Point point) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator xAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseX", point.x);
        ObjectAnimator yAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseY", point.y);
        set.playTogether(xAnimator, yAnimator);
        set.setInterpolator(mInterpolator);
        set.start();
    }
}
