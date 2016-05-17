package com.skytech.solar.view.showcase;

import android.app.Activity;
import android.view.View;

import com.skytech.solar.config.SolarPreference;
import com.skytech.solar.config.SolarShowcaseConfig;

import java.util.LinkedList;
import java.util.Queue;

public class SolarShowcaseQueue implements IDetachListener {
    private Activity mActivity;
    private String mSequenceID = null;
    private boolean mSingleUse = false;
    private Queue<SolarShowcaseView> mShowcaseQueue;
    private SolarShowcaseConfig mConfig;
    private SolarShowcaseView mSequenceItem;
    private SolarPreference mSolarPreference = null;
    private IOnCompleteListener mOnCompleteListener;

    /**
     * Default constructor without single usage;
     *
     * @param activity
     */
    public SolarShowcaseQueue(Activity activity) {
        mActivity = activity;
        mShowcaseQueue = new LinkedList<>();
        SolarPreference.resetAll(mActivity);
    }

    /**
     * Constructor for single usage
     *
     * @param activity
     * @param sequenceID
     */
    public SolarShowcaseQueue(Activity activity, String sequenceID) {
        mActivity = activity;
        mSequenceID = sequenceID;
        mShowcaseQueue = new LinkedList<>();
        setSingleUse(mSequenceID);
    }

    /**
     * Adding views;
     *
     * @param targetView
     * @param title
     * @param content
     * @param dismissText
     * @param skipText
     * @return
     */
    public SolarShowcaseQueue addShowcaseView(View targetView, String title, String content, String dismissText, String skipText) {
        mSequenceItem = new SolarShowcaseView.Builder(mActivity)
                .setTarget(targetView)
                .setTitle(title)
                .setDismissButton(dismissText)
                .setMessage(content)
                .setSkipButton(skipText)
                .setSequenceSingleUse(mSequenceID)
                .build();

        if (mConfig != null) {
            mSequenceItem.setConfig(mConfig);
        }
        mShowcaseQueue.add(mSequenceItem);
        return this;
    }

    /**
     * Adding view;
     *
     * @param sequenceItem
     * @return
     */
    public SolarShowcaseQueue addShowcaseView(SolarShowcaseView sequenceItem) {
        mShowcaseQueue.add(sequenceItem);
        return this;
    }

    /**
     * Start point of the showcase
     */
    public void show() {
        // If we had set up the single usage and pressed dismiss button, then don't show ShowcaseView again
        if (mSingleUse) {
            if (hasShownAndDismissed()) {
                return;
            }
        }
        if (mShowcaseQueue.size() > 0) {
            if (mSolarPreference != null) {
                // If we hadn't skipped and dismissed our showcase - show next item, otherwise - don't show anything;
                if (!mSolarPreference.isSkipped() && !mSolarPreference.isDismissed()) {
                    showNextItem();
                }
            } else {
                showNextItem();
            }
        }
    }

    public void show(IOnCompleteListener onCompleteListener) {
        mOnCompleteListener = onCompleteListener;
        // If we had set up the single usage and pressed dismiss button, then don't show ShowcaseView again
        if (mSingleUse) {
            if (hasShownAndDismissed()) {
                if (mShowcaseQueue.size() == 0) { // If we would restart application the would would call again, so we need to check it for re-launch
                    mOnCompleteListener.onCompleteListener();
                }
                return;
            }
        }
        if (mShowcaseQueue.size() > 0) {
            if (mSolarPreference != null) {
                // If we hadn't skip and dismiss our showcase - show next item, otherwise - don't show anything;
                if (!mSolarPreference.isSkipped() && !mSolarPreference.isDismissed()) {
                    showNextItem(onCompleteListener);
                }
            } else {
                showNextItem(onCompleteListener);
            }
        } else if (mShowcaseQueue.size() == 0 && mOnCompleteListener != null) {
            mOnCompleteListener.onCompleteListener();
        }
    }

    /**
     * Setting up configurations such as colors etc;
     *
     * @param config
     */
    public void setConfig(SolarShowcaseConfig config) {
        this.mConfig = config;
    }

    private void showNextItem() {
        if (mShowcaseQueue.size() > 0 && !mActivity.isFinishing()) {
            SolarShowcaseView sequenceItem = mShowcaseQueue.remove();
            sequenceItem.setDetachedListener(this);
            sequenceItem.show(mActivity);
        }
    }

    private void showNextItem(IOnCompleteListener onCompleteListener) {
        if (mShowcaseQueue.size() > 0 && !mActivity.isFinishing()) {
            SolarShowcaseView sequenceItem = mShowcaseQueue.remove();
            sequenceItem.setDetachedListener(this);
            sequenceItem.setOnCompleteListener(onCompleteListener);
            sequenceItem.show(mActivity);
        } else if (mShowcaseQueue.size() == 0) {
            if (mOnCompleteListener != null) {
                show(mOnCompleteListener);
            }
        }
    }

    /**
     * Used only when second param was passed in constructor;
     *
     * @param sequenceID
     * @return
     */
    private SolarShowcaseQueue setSingleUse(String sequenceID) {
        mSingleUse = true;
        mSolarPreference = new SolarPreference(mActivity, sequenceID);
        return this;
    }

    private boolean hasShownAndDismissed() {
        if (mSolarPreference.isDismissed()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calling on queue if the has been dismissed; Checks for onCompleteListener availability to show next or return;
     * @param showcaseView
     * @param wasDismissed
     * @param onCompleteListener
     */
    @Override
    public void onShowcaseDetached(SolarShowcaseView showcaseView, boolean wasDismissed, IOnCompleteListener onCompleteListener) {
        showcaseView.setDetachedListener(null);
        if (wasDismissed) {
            if (mSolarPreference != null) {
                if (!mSolarPreference.isSkipped() && !mSolarPreference.getTemporalSkip()) {
                    if (mShowcaseQueue.size() > 0) {
                        showNextItem();
                    } else {
                        showNextItem(onCompleteListener);
                    }
                } else if (!mSolarPreference.isSkipped() && mSolarPreference.getTemporalSkip() && mOnCompleteListener != null) {
                    mOnCompleteListener.onCompleteListener();
                } else if (mSolarPreference.isSkipped() && !mSolarPreference.getTemporalSkip() && mOnCompleteListener != null) {
                    mOnCompleteListener.onCompleteListener();
                }
            } else {
                mSolarPreference = new SolarPreference(mActivity, null);
                if (!mSolarPreference.getTemporalSkip() && !mSolarPreference.isDismissed()) {
                    showNextItem();
                } else if (!mSolarPreference.getTemporalSkip() && mSolarPreference.isDismissed()) {
                    showNextItem();
                } else if (mSolarPreference.getTemporalSkip() && !mSolarPreference.isDismissed() && mOnCompleteListener != null) { // Runs if Skip war pressed at first item
                    mOnCompleteListener.onCompleteListener();
                }
            }
        }
    }
}

