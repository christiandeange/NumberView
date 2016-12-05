package com.deange.numberview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Property;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.deange.numberview.digits.Digit;
import com.deange.numberview.digits.StandardDigits;

import static com.deange.numberview.digits.StandardDigits.HIDE_NUMBER;

public class NumberView extends View {

    private static final boolean DEBUG = true;

    public static final long DEFAULT_ANIMATION_DURATION = 500L;
    public static final float DEFAULT_WIDTH = 140f;
    public static final float DEFAULT_HEIGHT = 200f;
    public static final float ASPECT_RATIO = DEFAULT_WIDTH / DEFAULT_HEIGHT;

    // "8" is used since it constitutes the widest number drawn
    private static final String MEASURING_TEXT = "8";

    private static final Property<NumberView, Float> FACTOR =
            new Property<NumberView, Float>(Float.class, "factor") {
                @Override
                public Float get(final NumberView object) {
                    return object.mFactor;
                }

                @Override
                public void set(final NumberView object, final Float value) {
                    object.mFactor = value;
                    object.invalidate();
                }
            };

    private final SparseArray<Digit> mDigitCache = new SparseArray<>();
    private final NumberViewPaint mPaint = new NumberViewPaint();
    private final Path mPath = new Path();

    private int mNext = HIDE_NUMBER;
    private int mCurrent = HIDE_NUMBER;
    private boolean mFirstLayout = true;

    private float mWidth;
    private float mHeight;
    private float mScale;
    private float mFactor;
    private ValueAnimator mAnimator;

    public NumberView(final Context context) {
        super(context);
        init();
    }

    public NumberView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        // A new paint with the style as stroke
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2f);
        mPaint.setStyle(Paint.Style.STROKE);

        // Set up size values
        mScale = 1;
        mWidth = DEFAULT_WIDTH;
        mHeight = DEFAULT_HEIGHT;

        measureTextSize(mWidth);

        mAnimator = ObjectAnimator.ofFloat(this, FACTOR, 0f, 1f);
        mAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                // End of the current number animation
                // Begin setting values for the next number in the sequence
                mCurrent = mNext;
            }
        });
        mAnimator.start();
    }

    private void measureTextSize(final float targetMaxWidth) {
        // Calculate the right scale for the text size
        int sp = 0;
        float px = 0;
        float validPx;
        do {
            sp++;
            validPx = px;
            px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
            mPaint.setTextSizeInternal(px);
        } while (mPaint.measureText(MEASURING_TEXT) < targetMaxWidth);

        setTextSize(validPx);
    }

    public void setAnimationDuration(final long duration) {
        mAnimator.setDuration(duration);
    }

    public void setInterpolator(final Interpolator interpolator) {
        mAnimator.setInterpolator((interpolator == null) ? new LinearInterpolator() : interpolator);
    }

    public void setPaint(final Paint paint) {
        mPaint.set(paint);
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setTextSize(final int sizeUnit, final float textSize) {
        final float pixelSize = TypedValue.applyDimension(sizeUnit, textSize, getResources().getDisplayMetrics());
        setTextSize(pixelSize);
    }

    public void setTextSize(final float textSize) {
        mPaint.setTextSize(textSize);
    }

    public float getTextSize() {
        return getPaint().getTextSize();
    }

    public int getCurrentNumber() {
        return mNext;
    }

    public void hide() {
        advance(HIDE_NUMBER);
    }

    public void hideImmediate() {
        advanceImmediate(HIDE_NUMBER);
    }

    public void advance() {
        // Convenience to set the next number and advance to it in one call
        doAdvance(mNext + 1, false);
    }

    public void advance(final int next) {
        doAdvance(next, false);
    }

    public void advanceImmediate() {
        // Convenience to set the next number and advance to it immediately in one call
        doAdvance(mNext + 1, true);
    }

    public void advanceImmediate(final int next) {
        doAdvance(next, true);
    }

    private void doAdvance(final int next, final boolean immediate) {
        mNext = next;
        checkSequenceBounds();

        if (immediate) {
            mCurrent = mNext;
        }

        mAnimator.start();
    }

    private void setScale(float scale) {
        if (scale == 0) {
            throw new IllegalArgumentException("Scale cannot be 0");
        }

        scale = Math.abs(scale);

        if (mScale == scale) return;

        final float inverseFactor = (scale / mScale);
        mWidth *= inverseFactor;
        mHeight *= inverseFactor;

        mScale = scale;

        requestLayout();
        invalidate();
    }

    private void checkSequenceBounds() {
        // Ensures single-digit values only
        // This also preserves -1 as mNext (for empty digit)
        if (mNext != HIDE_NUMBER) {
            mNext = (mNext + 10) % 10;
        }
    }

    private Digit getDigit(final int number) {
        Digit digit = mDigitCache.get(number);
        if (digit == null) {
            digit = StandardDigits.forNumber(number);
            mDigitCache.put(number, digit);
        }
        return digit;
    }

    private boolean isAnimating() {
        return mAnimator.isRunning();
    }

    private float lerp(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }

    private boolean fequals(final float f0, final float f1) {
        final float ulp0 = Math.ulp(f0);
        final float ulp1 = Math.ulp(f1);
        return Math.abs(f0 - f1) <= Math.max(ulp0, ulp1);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int minWidth = getSuggestedMinimumWidth();
        final int minHeight = getSuggestedMinimumHeight();
        int width, height;

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (!isAnimating()) {
                mWidth = mScale * getDigit(mCurrent).getWidth();
            }
            width = (int) Math.max(minWidth, mWidth);
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = (int) Math.max(minHeight, mHeight);
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }

        if (height * ASPECT_RATIO < width) {
            height = (int) (width / ASPECT_RATIO);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        super.onLayout(changed, l, t, r, b);

        // Handles the case of an absolute dimension specified in the layout params
        if (mFirstLayout) {
            mFirstLayout = false;
            final ViewGroup.LayoutParams params = getLayoutParams();
            if (params != null && params.width > 0) {
                measureTextSize(params.width);
            }
        }
    }

    @Override
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        checkSequenceBounds();
        final Digit thisNumber = getDigit(mCurrent);
        final Digit nextNumber = getDigit(mNext);

        final float[][] current = thisNumber.getPoints();
        final float[][] next = nextNumber.getPoints();
        final float[][] curr1 = thisNumber.getControlPoints1();
        final float[][] next1 = nextNumber.getControlPoints1();
        final float[][] curr2 = thisNumber.getControlPoints2();
        final float[][] next2 = nextNumber.getControlPoints2();

        // A factor of the difference between current and next frame based on interpolation
        // If we ourselves did not specifically request drawing, then draw our previous state
        final float factor = mFactor;

        final float thisWidth = mScale * thisNumber.getWidth();
        final float nextWidth = mScale * nextNumber.getWidth();
        final float interpolatedWidth = lerp(thisWidth, nextWidth, factor);
        if (!fequals(thisWidth, nextWidth) || !fequals(mWidth, interpolatedWidth)) {
            mWidth = Math.max(interpolatedWidth, 1f);
            requestLayout();
        }

        final float translateX = ((float) getMeasuredWidth() - mWidth) / 2f;
        final float translateY = ((float) getMeasuredHeight() - mHeight) / 2f;

        // Reset the path
        mPath.reset();

        // Draw the first point
        mPath.moveTo(
                mScale * (lerp(current[0][0], next[0][0], factor) + translateX),
                mScale * (lerp(current[0][1], next[0][1], factor) + translateY));

        // Connect the rest of the points as a bezier curve
        for (int i = 0; i < 4; i++) {
            mPath.cubicTo(
                    mScale * (lerp(curr1[i][0], next1[i][0], factor) + translateX),
                    mScale * (lerp(curr1[i][1], next1[i][1], factor) + translateY),
                    mScale * (lerp(curr2[i][0], next2[i][0], factor) + translateX),
                    mScale * (lerp(curr2[i][1], next2[i][1], factor) + translateY),
                    mScale * (lerp(current[i + 1][0], next[i + 1][0], factor) + translateX),
                    mScale * (lerp(current[i + 1][1], next[i + 1][1], factor) + translateY));
        }

        // Draw the path
        canvas.drawPath(mPath, mPaint);

        if (DEBUG) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final SavedState ss = new SavedState(super.onSaveInstanceState());

        // If we are animating while saving state, skip to the end by saving mCurrent as mNext
        ss.next = mNext;
        ss.current = isAnimating() ? mNext : mCurrent;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mNext = ss.next;
        mCurrent = ss.current;
    }

    private static class SavedState extends BaseSavedState {
        public int next;
        public int current;

        private SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            next = in.readInt();
            current = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(next);
            out.writeInt(current);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    private class NumberViewPaint extends Paint {
        @Override
        public void setTextSize(final float textSize) {
            super.setTextSize(textSize);
            setScale(measureText(MEASURING_TEXT) / DEFAULT_WIDTH);
        }

        @Override
        public void set(final Paint src) {
            super.set(src);
            setScale(measureText(MEASURING_TEXT) / DEFAULT_WIDTH);
        }

        protected void setTextSizeInternal(final float textSize) {
            super.setTextSize(textSize);
        }
    }

}
