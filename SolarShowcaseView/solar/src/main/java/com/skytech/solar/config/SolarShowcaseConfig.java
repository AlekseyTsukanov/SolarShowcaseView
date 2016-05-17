package com.skytech.solar.config;

import android.graphics.Color;

import com.skytech.solar.view.shape.Circle;
import com.skytech.solar.view.shape.IShape;

public class SolarShowcaseConfig {
    public static final String DEFAULT_BACKGROUND_COLOUR = "#dd5f7cb8";
    public static final long DEFAULT_FADE_TIME = 300;
    public static final long DEFAULT_DELAY = 0;
    public static final int DEFAULT_SHAPE_PADDING = 10;
    public static final IShape DEFAULT_SHAPE = new Circle();
    private long mDelay = DEFAULT_DELAY;
    private long mFadeDuration = DEFAULT_FADE_TIME;
    private int mBackgroundColor;
    private int mContentTextColor;
    private int mDismissTextColor;
    private int mSkipTextColor;
    private int mShapePadding = DEFAULT_SHAPE_PADDING;
    private boolean mRenderOverNav = false;
    private IShape mShape = DEFAULT_SHAPE;

    public SolarShowcaseConfig() {
        mBackgroundColor = Color.parseColor(SolarShowcaseConfig.DEFAULT_BACKGROUND_COLOUR);
        mContentTextColor = Color.parseColor("#ffffff");
        mDismissTextColor = Color.parseColor("#ffffff");
    }

    public long getDelay() {
        return mDelay;
    }

    public void setDelay(long delay) {
        mDelay = delay;
    }

    public int getMaskColor() {
        return mBackgroundColor;
    }

    public void setMaskColor(int maskColor) {
        mBackgroundColor = maskColor;
    }

    public int getContentTextColor() {
        return mContentTextColor;
    }

    public void setContentTextColor(int mContentTextColor) {
        mContentTextColor = mContentTextColor;
    }

    public int getDismissTextColor() {
        return mDismissTextColor;
    }

    public void setDismissTextColor(int dismissTextColor) {
        mDismissTextColor = dismissTextColor;
    }

    public int getSkipTextColor() {
        return mSkipTextColor;
    }

    public void setSkipTextColor(int skipTextColor) {
        mSkipTextColor = skipTextColor;
    }

    public long getFadeDuration() {
        return mFadeDuration;
    }

    public void setFadeDuration(long fadeDuration) {
        mFadeDuration = fadeDuration;
    }

    public IShape getShape() {
        return mShape;
    }

    public void setShape(IShape shape) {
        mShape = shape;
    }

    public void setShapePadding(int padding) {
        mShapePadding = padding;
    }

    public int getShapePadding() {
        return mShapePadding;
    }

    public boolean getRenderOverNavigationBar() {
        return mRenderOverNav;
    }

    public void setRenderOverNavigationBar(boolean renderOverNav) {
        mRenderOverNav = renderOverNav;
    }
}