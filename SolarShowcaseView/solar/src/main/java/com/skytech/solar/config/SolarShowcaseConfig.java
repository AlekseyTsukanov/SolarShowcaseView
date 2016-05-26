package com.skytech.solar.config;

import android.graphics.Color;

import com.skytech.solar.view.shape.Circle;
import com.skytech.solar.view.shape.IShape;

public class SolarShowcaseConfig {
    public static final String DEFAULT_BACKGROUND_COLOUR = "#965f7cb8";
    public static final long DEFAULT_FADE_TIME = 300;
    public static final long DEFAULT_DELAY = 0;
    public static final int DEFAULT_SHAPE_PADDING = 10;
    public static final IShape DEFAULT_SHAPE = new Circle();
    private long mDelay = DEFAULT_DELAY;
    private long mFadeDuration = DEFAULT_FADE_TIME;
    private int mBackgroundColor;
    private int mTitleTextColor;
    private int mContentTextColor;
    private int mDismissTextColor;
    private int mSkipTextColor;
    private int mTitleBackgroundColor;
    private int mContentBackgroundColor;
    private int mDismissBackgroundColor;
    private int mSkipBackgroundColor;
    private int mShapePadding = DEFAULT_SHAPE_PADDING;
    private boolean mRenderOverNav = false;
    private IShape mShape = DEFAULT_SHAPE;

    public SolarShowcaseConfig() {
        mBackgroundColor = Color.parseColor(SolarShowcaseConfig.DEFAULT_BACKGROUND_COLOUR);
        mTitleTextColor = Color.parseColor("#ffffff");
        mContentTextColor = Color.parseColor("#ffffff");
        mDismissTextColor = Color.parseColor("#ffffff");
        mSkipTextColor = Color.parseColor("#ffffff");
        mTitleBackgroundColor = Color.TRANSPARENT;
        mContentBackgroundColor = Color.TRANSPARENT;
        mDismissBackgroundColor = Color.TRANSPARENT;
        mSkipBackgroundColor = Color.TRANSPARENT;
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

    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    public void setTitleTextColor(int mTitleTextColor) {
        this.mTitleTextColor = mTitleTextColor;
    }

    public int getTitleBackgroundColor() {
        return mTitleBackgroundColor;
    }

    public void setTitleBackgroundColor(int mTitleBackgroundColor) {
        this.mTitleBackgroundColor = mTitleBackgroundColor;
    }

    public int getContentBackgroundColor() {
        return mContentBackgroundColor;
    }

    public void setContentBackgroundColor(int mContentBackgroundColor) {
        this.mContentBackgroundColor = mContentBackgroundColor;
    }

    public int getDismissBackgroundColor() {
        return mDismissBackgroundColor;
    }

    public void setDismissBackgroundColor(int mDismissBackgroundColor) {
        this.mDismissBackgroundColor = mDismissBackgroundColor;
    }

    public int getSkipBackgroundColor() {
        return mSkipBackgroundColor;
    }

    public void setSkipBackgroundColor(int mSkipBackgroundColor) {
        this.mSkipBackgroundColor = mSkipBackgroundColor;
    }
}