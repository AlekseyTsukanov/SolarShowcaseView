package com.skytech.solar.view.showcase;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.skytech.solar.R;
import com.skytech.solar.animation.IShowcaseAnimation;
import com.skytech.solar.animation.ShowcaseAnimation;
import com.skytech.solar.config.SolarPreference;
import com.skytech.solar.config.SolarShowcaseConfig;
import com.skytech.solar.target.ITarget;
import com.skytech.solar.target.TargetView;
import com.skytech.solar.view.shape.Circle;
import com.skytech.solar.view.shape.IShape;
import com.skytech.solar.view.shape.None;
import com.skytech.solar.view.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class SolarShowcaseView extends FrameLayout implements View.OnTouchListener, View.OnClickListener {
    private int mOldHeight;
    private int mOldWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private ITarget mTarget;
    private IShape mShape;
    private int mXPosition;
    private int mYPosition;
    private int mShapePadding = SolarShowcaseConfig.DEFAULT_SHAPE_PADDING;
    private boolean mWasDismissed = false;

    private View mShowcaseContainer;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private TextView mDismissButton;
    private TextView mSkipButton;

    private int mGravity;
    private int mContentBottomMargin;
    private int mContentTopMargin;
    private int mMaskColour;
    private int mBottomMargin = 0;
    private long mFadeDurationInMillis = SolarShowcaseConfig.DEFAULT_FADE_TIME;
    private long mDelayInMillis = SolarShowcaseConfig.DEFAULT_DELAY;
    private boolean mDismissOnTouch = false;
    private boolean mShouldRender = false; // flag to decide when we should actually render
    private boolean mRenderOverNav = false;
    private boolean mShouldAnimate = true;
    private boolean mTargetTouchable = false;
    private boolean mDismissOnTargetTouch = true;
    private boolean mCanDismiss = false;
    private boolean mCanSkip = false;
    private boolean mReloadAfterSession = false;
    private List<IShowcaseListener> mListeners; // external listeners who want to observe when we show and canDismiss

    private ShowcaseAnimation mAnimation;
    private Handler mHandler;
    private UpdateOnGlobalLayout mLayoutListener;
    private IDetachListener mDetachedListener;
    private SolarPreference solarPreference = null;
    private IOnCompleteListener mOnCompleteListener;


    public SolarShowcaseView(Context context) {
        super(context);
        init(context);
    }

    public SolarShowcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SolarShowcaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SolarShowcaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mAnimation = new ShowcaseAnimation();
        mListeners = new ArrayList<>();
        mLayoutListener = new UpdateOnGlobalLayout();
        getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
        setOnTouchListener(this);
        mMaskColour = Color.parseColor(SolarShowcaseConfig.DEFAULT_BACKGROUND_COLOUR);
        setVisibility(INVISIBLE);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.showcase, this, true);
        mShowcaseContainer = contentView.findViewById(R.id.showcase_container);
        mTitleTextView = (TextView) contentView.findViewById(R.id.tv_title);
        mContentTextView = (TextView) contentView.findViewById(R.id.tv_content);
        mDismissButton = (TextView) contentView.findViewById(R.id.tv_dismiss);
        mSkipButton = (TextView) contentView.findViewById(R.id.tv_skip);
        mDismissButton.setOnClickListener(this);
        mSkipButton.setOnClickListener(this);
    }

    /**
     * Drawing SolarShowcaseView;
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mShouldRender) {
            return;
        }
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if(width <= 0 || height <= 0) {
            return;
        }
        if (mBitmap == null || mCanvas == null || mOldHeight != height || mOldWidth != width) {
            if (mBitmap != null) {
                mBitmap.recycle();
            }
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
        mOldWidth = width;
        mOldHeight = height;
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mCanvas.drawColor(mMaskColour);
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(0xFFFFFFFF);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }
        mShape.draw(mCanvas, mPaint, mXPosition, mYPosition, mShapePadding);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        notifyOnDismissed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mDismissOnTouch) {
            hide();
        }
        if(mTargetTouchable && mTarget.getRectangleBounds().contains((int)event.getX(), (int)event.getY())){
            if(mDismissOnTargetTouch){
                hide();
            }
            return false;
        }
        return true;
    }

    private void notifyOnDisplayed() {
        if(mListeners != null){
            for (IShowcaseListener listener : mListeners) {
                listener.onShowcaseDisplayed(this);
            }
        }
    }

    private void notifyOnDismissed() {
        if (mListeners != null) {
            for (IShowcaseListener listener : mListeners) {
                listener.onShowcaseDismissed(this);
            }
            mListeners.clear();
            mListeners = null;
        }
        /**
         * mDetachedListener is used for sequence of views; we notifying that the showcaseview should set to another target;
         * -> SolarShowcaseQueue, showNextItem() method;
         */
        if (mDetachedListener != null) {
            mDetachedListener.onShowcaseDetached(this, mWasDismissed, mOnCompleteListener);
        }
    }

    @Override
    public void onClick(View v) {
         // We only can dismiss and skip if 'setSingleUse(showcaseId)' has been applied;
        if (v.getId() == R.id.tv_dismiss) {
            if (mCanDismiss) {
                if (solarPreference != null) {
                    solarPreference.setDismissed(true);
                }
            } else if (!mCanDismiss && solarPreference == null) {
                solarPreference = new SolarPreference(getContext(), null);
                solarPreference.setDismissed(false);
            }
            hide();
        } else if (v.getId() == R.id.tv_skip) {
            if (mCanSkip) {
                if (solarPreference != null) {
                    solarPreference.setSkipped(true);
                }
            } else if (!mCanSkip && solarPreference == null) {
                solarPreference = new SolarPreference(getContext(), null);
                solarPreference.setTemporalSkip(true);
            }
            mWasDismissed = true;
            removeFromWindow();
        }
    }

    /**
     * Detecting targetView, resizing shape and applying views positions
     * @param target
     */
    public void setTarget(ITarget target) {
        mTarget = target;
        updateDismissButton();
        updateSkipButton();
        if (mTarget != null) {
            if (!mRenderOverNav && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBottomMargin = getSoftButtonsBarSizePort((Activity) getContext());
                FrameLayout.LayoutParams containerLayoutParams = (LayoutParams) getLayoutParams();
                if (containerLayoutParams != null && containerLayoutParams.bottomMargin != mBottomMargin)
                    containerLayoutParams.bottomMargin = mBottomMargin;
            }
            // apply the target position
            Point targetPoint = mTarget.getPoint();
            Rect targetBounds = mTarget.getRectangleBounds();
            setPosition(targetPoint);
            // now figure out whether to put content above or below it
            int height = getMeasuredHeight();
            int midPoint = height / 2;
            int yPos = targetPoint.y;

            int radius = Math.max(targetBounds.height(), targetBounds.width()) / 2;
            if (mShape != null) {
                mShape.updateTarget(mTarget);
                radius = mShape.getHeight() / 2;
            }

            if (yPos > midPoint) {
                // target is in lower half of screen, we'll sit above it
                mContentTopMargin = 0;
                mContentBottomMargin = (height - yPos) + radius + mShapePadding;
                mGravity = Gravity.BOTTOM;
            } else {
                // target is in upper half of screen, we'll sit below it
                mContentTopMargin = yPos + radius + mShapePadding;
                mContentBottomMargin = 0;
                mGravity = Gravity.TOP;
            }
        }
        applyLayoutParams();
    }

    private void applyLayoutParams() {
        if (mShowcaseContainer != null && mShowcaseContainer.getLayoutParams() != null) {
            FrameLayout.LayoutParams containerLayoutParams = (LayoutParams) mShowcaseContainer.getLayoutParams();
            boolean layoutParamsChanged = false;
            if (containerLayoutParams.bottomMargin != mContentBottomMargin) {
                containerLayoutParams.bottomMargin = mContentBottomMargin;
                layoutParamsChanged = true;
            } else
            if (containerLayoutParams.topMargin != mContentTopMargin) {
                containerLayoutParams.topMargin = mContentTopMargin;
                layoutParamsChanged = true;
            } else
            if (containerLayoutParams.gravity != mGravity) {
                containerLayoutParams.gravity = mGravity;
                layoutParamsChanged = true;
            }
            // setting new LayoutParams in case they were changed
            if (layoutParamsChanged) {
                mShowcaseContainer.setLayoutParams(containerLayoutParams);
            }
        }
    }

    // region setters
    private void setPosition(Point point) {
        setPosition(point.x, point.y);
    }

    private void setPosition(int x, int y) {
        mXPosition = x;
        mYPosition = y;
    }

    private void setTitle(CharSequence contentText) {
        if (mTitleTextView != null && !contentText.equals("")) {
            mTitleTextView.setText(contentText);
        }
    }

    private void setMessage(CharSequence contentText) {
        if (mContentTextView != null) {
            mContentTextView.setText(contentText);
        }
    }

    private void setDismissButton(CharSequence dismissText) {
        if (mDismissButton != null) {
            mDismissButton.setText(dismissText);
            updateDismissButton();
        }
    }

    private void setSkipButton(CharSequence skipText) {
        if (mSkipButton != null) {
            mSkipButton.setText(skipText);
            updateSkipButton();
        }
    }

    private void setTitleTextColor(int textColour) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(textColour);
        }
    }

    private void setContentTextColor(int textColour) {
        if (mContentTextView != null) {
            mContentTextView.setTextColor(textColour);
        }
    }

    private void setDismissTextColor(int textColour) {
        if (mDismissButton != null) {
            mDismissButton.setTextColor(textColour);
        }
    }

    private void setSkipTextColor(int textColor) {
        if (mSkipButton != null) {
            mSkipButton.setTextColor(textColor);
        }
    }

    private void setShapePadding(int padding) {
        mShapePadding = padding;
    }

    private void setDismissOnTouch(boolean dismissOnTouch) {
        mDismissOnTouch = dismissOnTouch;
    }

    private void setShouldRender(boolean shouldRender) {
        mShouldRender = shouldRender;
    }

    private void setMaskColour(int maskColour) {
        mMaskColour = maskColour;
    }

    private void setDelay(long delayInMillis) {
        mDelayInMillis = delayInMillis;
    }

    private void setFadeDuration(long fadeDurationInMillis) {
        mFadeDurationInMillis = fadeDurationInMillis;
    }

    private void setTargetTouchable(boolean targetTouchable){
        mTargetTouchable = targetTouchable;
    }

    private void setDismissOnTargetTouch(boolean dismissOnTargetTouch){
        mDismissOnTargetTouch = dismissOnTargetTouch;
    }

    // endregion setters

    public void addShowcaseListener(IShowcaseListener showcaseListener) {
        if (mListeners != null) {
            mListeners.add(showcaseListener);
        }
    }

    public void removeShowcaseListener(SolarShowcaseQueue showcaseListener) {
        if ((mListeners != null) && mListeners.contains(showcaseListener)) {
            mListeners.remove(showcaseListener);
        }
    }

    public void setDetachedListener(IDetachListener detachedListener) {
        mDetachedListener = detachedListener;
    }

    public void setOnCompleteListener(IOnCompleteListener onCompleteListener) {
        mOnCompleteListener = onCompleteListener;
    }

    private void setShape(IShape mShape) {
        this.mShape = mShape;
    }

    /**
     * Set properties based on a config object
     * @param config
     */
    protected void setConfig(SolarShowcaseConfig config) {
        setDelay(config.getDelay());
        setFadeDuration(config.getFadeDuration());
        setContentTextColor(config.getContentTextColor());
        setDismissTextColor(config.getDismissTextColor());
        setSkipTextColor(config.getSkipTextColor());
        setMaskColour(config.getMaskColor());
        setShape(config.getShape());
        setShapePadding(config.getShapePadding());
        setRenderOverNavigationBar(config.getRenderOverNavigationBar());
    }

    private void updateDismissButton() {
        // hide or show button
        if (mDismissButton != null) {
            if (TextUtils.isEmpty(mDismissButton.getText())) {
                mDismissButton.setVisibility(GONE);
            } else {
                mDismissButton.setVisibility(VISIBLE);
            }
        }
    }

    private void updateSkipButton() {
        if (mSkipButton != null)  {
            if (TextUtils.isEmpty(mSkipButton.getText())) {
                mSkipButton.setVisibility(GONE);
            } else {
                mSkipButton.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * REDRAW LISTENER - this ensures we redraw after activity finishes laying out
     */
    private class UpdateOnGlobalLayout implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            setTarget(mTarget);
        }
    }

    /**
     * Builder
     */
    public static class Builder {
        private static final int CIRCLE_SHAPE = 0;
        private static final int RECTANGLE_SHAPE = 1;
        private static final int NO_SHAPE = 2;

        private boolean fullWidth = false;
        private int shapeType = CIRCLE_SHAPE;

        final SolarShowcaseView showcaseView;

        private final Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
            showcaseView = new SolarShowcaseView(activity);
        }

        /**
         * Set the target view for SolarShowcaseView.
         * @param target
         * @return
         */
        public Builder setTarget(View target) {
            showcaseView.setTarget(new TargetView(target));
            return this;
        }

        /**
         * Set the dismiss button through text (if empty then button is hiding) shown on the SolarShowcaseView;
         * @param resId
         * @return
         */
        public Builder setDismissButton(int resId) {
            return setDismissButton(activity.getString(resId));
        }

        /**
         * Set the title text shown on the SolarShowcaseView.
         * @param dismissText
         * @return
         */
        public Builder setDismissButton(CharSequence dismissText) {
            showcaseView.setDismissButton(dismissText);
            return this;
        }

        /**
         * Set the skip button through text (if empty then button is hiding) show on the SolarShowcaseView;
         * @param resId
         * @return
         */
        public Builder setSkipButton(int resId) {
            return setSkipButton(activity.getString(resId));
        }

        /**
         * Set the skip button through text (if empty then button is hiding) show on the SolarShowcaseView;
         * @param skipText
         * @return
         */
        public Builder setSkipButton(CharSequence skipText) {
            showcaseView.setSkipButton(skipText);
            return this;
        }

        /**
         * Set the message for SolarShowcaseView
         * @param resId
         * @return
         */
        public Builder setMessage(int resId) {
            return setMessage(activity.getString(resId));
        }

        /**
         * Set the message for SolarShowcaseView
         * @param text
         * @return
         */
        public Builder setMessage(CharSequence text) {
            showcaseView.setMessage(text);
            return this;
        }

        /**
         * Set title shown on the ShowcaseView.
         * @param resId
         * @return
         */
        public Builder setTitle(int resId) {
            return setTitle(activity.getString(resId));
        }

        /**
         * Set title shown on the ShowcaseView.
         * @param text
         * @return
         */
        public Builder setTitle(CharSequence text) {
            showcaseView.setTitle(text);
            return this;
        }

        /**
         * If true, then user can touch showcase while it's visible;
         * Default value is 'false'
         * @param targetTouchable
         * @return
         */
        public Builder setTargetTouchable(boolean targetTouchable){
            showcaseView.setTargetTouchable(targetTouchable);
            return this;
        }

        /**
         * If true, then user can touch-to-dismiss;
         * Default valuer is 'true'
         * @param dismissOnTargetTouch
         * @return
         */
        public Builder setDismissOnTargetTouch(boolean dismissOnTargetTouch){
            showcaseView.setDismissOnTargetTouch(dismissOnTargetTouch);
            return this;
        }

        /**
         * If true, then user can touch-to-dismiss;
         * Default valuer is 'true'
         * @param dismissOnTouch
         * @return
         */
        public Builder setDismissOnTouch(boolean dismissOnTouch) {
            showcaseView.setDismissOnTouch(dismissOnTouch);
            return this;
        }

        /**
         * Setting up the color of the background of showcaseView
         * @param maskColour
         * @return
         */
        public Builder setMaskColour(int maskColour) {
            showcaseView.setMaskColour(maskColour);
            return this;
        }

        /**
         * Setting up the color of the title
         * @param textColour
         * @return
         */
        public Builder setTitleTextColor(int textColour) {
            showcaseView.setTitleTextColor(textColour);
            return this;
        }

        /**
         * Setting up the message text color;
         * @param textColour
         * @return
         */
        public Builder setContentTextColor(int textColour) {
            showcaseView.setContentTextColor(textColour);
            return this;
        }

        /**
         * Setting up the dismiss button text color;
         * @param textColour
         * @return
         */
        public Builder setDismissTextColor(int textColour) {
            showcaseView.setDismissTextColor(textColour);
            return this;
        }

        /**
         * Setting up the skip button text color;
         * @param textColor
         * @return
         */
        public Builder setSkipTextColor(int textColor) {
            showcaseView.setSkipTextColor(textColor);
            return this;
        }

        /**
         * Setting up the delay time between targeting;
         * @param delayInMillis
         * @return
         */
        public Builder setDelay(int delayInMillis) {
            showcaseView.setDelay(delayInMillis);
            return this;
        }

        /**
         * Fade in-out duration of the animation;
         * @param fadeDurationInMillis
         * @return
         */
        public Builder setFadeDuration(int fadeDurationInMillis) {
            showcaseView.setFadeDuration(fadeDurationInMillis);
            return this;
        }

        /**
         * Setting the listener for displaying and dismissing;
         * @param listener
         * @return
         */
        public Builder setListener(IShowcaseListener listener) {
            showcaseView.addShowcaseListener(listener);
            return this;
        }

        /**
         * Setting single use of SolarShowcaseView;
         * @param showcaseId
         * @return
         */
        public Builder setSingleUse(String showcaseId) {
            showcaseView.setSingleUse(showcaseId);
            return this;
        }

        /**
         * This singleUse is used only for sequence of items;
         * @param showcaseId
         * @return
         */
        public Builder setSequenceSingleUse(String showcaseId) {
            showcaseView.setSequenceSingleUse(showcaseId);
            return this;
        }

        /**
         * Setting up teh shape;
         * Default is circle;
         * @param shape
         * @return
         */
        public Builder setShape(IShape shape) {
            showcaseView.setShape(shape);
            return this;
        }

        public Builder withCircleShape() {
            shapeType = CIRCLE_SHAPE;
            return this;
        }

        public Builder withoutShape() {
            shapeType = NO_SHAPE;
            return this;
        }

        public Builder setShapePadding(int padding) {
            showcaseView.setShapePadding(padding);
            return this;
        }

        public Builder withRectangleShape() {
            return withRectangleShape(false);
        }

        public Builder withRectangleShape(boolean fullWidth) {
            this.shapeType = RECTANGLE_SHAPE;
            this.fullWidth = fullWidth;
            return this;
        }

        public Builder renderOverNavigationBar() {
            // Note: This only has an effect in Lollipop or above.
            showcaseView.setRenderOverNavigationBar(true);
            return this;
        }

        public SolarShowcaseView build() {
            if (showcaseView.mShape == null) {
                switch (shapeType) {
                    case RECTANGLE_SHAPE: {
                        showcaseView.setShape(new Rectangle(showcaseView.mTarget.getRectangleBounds(), fullWidth));
                        break;
                    }
                    case CIRCLE_SHAPE: {
                        showcaseView.setShape(new Circle(showcaseView.mTarget));
                        break;
                    }
                    case NO_SHAPE: {
                        showcaseView.setShape(new None());
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Unsupported shape type: " + shapeType);
                }
            }
            return showcaseView;
        }

        public SolarShowcaseView show() {
            build().show(activity);
            return showcaseView;
        }
    }

    private void setSingleUse(String showcaseId) {
        if (showcaseId != null && !showcaseId.equals("")) {
            mCanDismiss = true;
            mCanSkip = true;
            mReloadAfterSession = true;
            solarPreference = new SolarPreference(getContext(), showcaseId);
        }
    }

    private void setSequenceSingleUse(String showcaseId) {
        if (showcaseId != null && !showcaseId.equals("")) {
            mCanDismiss = true;
            mCanSkip = true;
            solarPreference = new SolarPreference(getContext(), showcaseId);
        }
    }

    private void removeFromWindow() {
        if (getParent() != null && getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        mPaint = null;
        mAnimation = null;
        mCanvas = null;
        mHandler = null;

        getViewTreeObserver().removeGlobalOnLayoutListener(mLayoutListener);
        mLayoutListener = null;

        // TODO: add and test closing solarPreference instance
    }


    /**
     * Reveal the showcaseview. Returns a boolean telling us whether we actually did show anything
     *
     * @param activity
     * @return
     */
    public boolean show(final Activity activity) {
        // If teh following condition args returns 'true', then we use builder (not sequence) with setSingleUse(..) method;
        if (mCanSkip && mCanDismiss && mReloadAfterSession) {
            if (solarPreference.isSkipped()) {
                return false;
            }
            if (solarPreference.isDismissed()) {
                return false;
            }
        }

        ((ViewGroup) activity.getWindow().getDecorView()).addView(this);
        setShouldRender(true);
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mShouldAnimate) {
                    fadeIn();
                } else {
                    setVisibility(VISIBLE);
                    notifyOnDisplayed();
                }
            }
        }, mDelayInMillis);

        updateDismissButton();
        updateSkipButton();
        return true;
    }


    private void hide() {
        // Notifying that the ShowcaseView has been dismissed
        mWasDismissed = true;
        if (mShouldAnimate) {
            fadeOut();
        } else {
            removeFromWindow();
        }
    }

    private void fadeIn() {
        setVisibility(INVISIBLE);
        mAnimation.fadeInView(this, mFadeDurationInMillis,
                new IShowcaseAnimation.AnimationStartListener() {
                    @Override
                    public void onAnimationStart() {
                        setVisibility(View.VISIBLE);
                        notifyOnDisplayed();
                    }
                }
        );
    }

    private void fadeOut() {
        mAnimation.fadeOutView(this, mFadeDurationInMillis, new IShowcaseAnimation.AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                setVisibility(INVISIBLE);
                removeFromWindow();
            }
        });
    }

    private static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight) {
                return realHeight - usableHeight;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private void setRenderOverNavigationBar(boolean mRenderOverNav) {
        this.mRenderOverNav = mRenderOverNav;
    }
}
