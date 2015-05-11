package com.deange.numberview;

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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class NumberView extends View {

    private static final boolean DEBUG = false;

    // "8" is used since it constitutes the widest number drawn
    private static final String MEASURING_TEXT = "8";

    private static final int[] DEFAULT_SEQUENCE = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private static final int FRAME_COUNT = 24;
    private static final int EMPTY_POSITION = 10;
    private static final float DEFAULT_WIDTH = 140f;
    private static final float DEFAULT_HEIGHT = 200f;
    private static final float ASPECT_RATIO = DEFAULT_WIDTH / DEFAULT_HEIGHT;

    // NOTE: These fields are not static so that they may be scaled for each instance
    private float[][][] mPoints =
            {
                    {{14.5f, 100}, {70, 18}, {126, 100}, {70, 180}, {14.5f, 100}},
                    {{15, 20.5f}, {42.5f, 20.5f}, {42.5f, 181}, {42.5f, 181}, {42.5f, 181}},
                    {{26, 60}, {114.5f, 61}, {78, 122}, {27, 177}, {117, 177}},
                    {{33.25f, 54}, {69.5f, 18}, {69.5f, 96}, {70, 180}, {26.5f, 143}},
                    {{125, 146}, {13, 146}, {99, 25}, {99, 146}, {99, 179}},
                    {{116, 20}, {61, 20}, {42, 78}, {115, 129}, {15, 154}},
                    {{80, 20}, {80, 20}, {16, 126}, {123, 126}, {23, 100}},
                    {{17, 21}, {128, 21}, {90.67f, 73.34f}, {53.34f, 126.67f}, {16, 181}},
                    {{71, 96}, {71, 19}, {71, 96}, {71, 179}, {71, 96}},
                    {{117, 100}, {17, 74}, {124, 74}, {60, 180}, {60, 180}},
                    {empty(), empty(), empty(), empty(), empty()},
            };

    // The set of the "first" control points of each segment
    private float[][][] mControlPoint1 =
            {
                    {{14.5f, 60}, {103, 18}, {126, 140}, {37, 180}},
                    {{15, 20.5f}, {42.5f, 20.5f}, {42.5f, 181}, {42.5f, 181}},
                    {{29, 2}, {114.5f, 78}, {64, 138}, {27, 177}},
                    {{33, 27}, {126, 18}, {128, 96}, {24, 180}},
                    {{125, 146}, {13, 146}, {99, 25}, {99, 146}},
                    {{61, 20}, {42, 78}, {67, 66}, {110, 183}},
                    {{80, 20}, {41, 79}, {22, 208}, {116, 66}},
                    {{17, 21}, {128, 21}, {90.67f, 73.34f}, {53.34f, 126.67f}},
                    {{14, 95}, {124, 19}, {14, 96}, {124, 179}},
                    {{94, 136}, {12, 8}, {122, 108}, {60, 180}},
                    {empty(), empty(), empty(), empty(), empty()},
            };

    // The set of the "second" control points of each segment
    private float[][][] mControlPoint2 =
            {
                    {{37, 18}, {126, 60}, {103, 180}, {14.5f, 140}},
                    {{12.5f, 20.5f}, {42.5f, 181}, {42.5f, 181}, {42.5f, 181}},
                    {{113, 4}, {100, 98}, {44, 155}, {117, 177}},
                    {{56, 18}, {116, 96}, {120, 180}, {26, 150}},
                    {{13, 146}, {99, 25}, {99, 146}, {99, 179}},
                    {{61, 20}, {42, 78}, {115, 85}, {38, 198}},
                    {{80, 20}, {18, 92}, {128, 192}, {46, 64}},
                    {{128, 21}, {90.67f, 73.34f}, {53.34f, 126.67f}, {16, 181}},
                    {{14, 19}, {124, 96}, {6, 179}, {124, 96}},
                    {{24, 134}, {118, -8}, {99, 121}, {60, 180}},
                    {empty(), empty(), empty(), empty(), empty()},
            };

    private float[] mNumberWidth =
            {
                    140f, 70f, 140f, 140f, 140f, 140f, 140f, 140f, 140f, 140f, 1f,
            };

    private final NumberViewPaint mPaint = new NumberViewPaint();
    private final Path mPath = new Path();

    private int mNext;
    private int mCurrent;
    private int mFrame;
    private boolean mFirstLayout = true;
    private boolean mFirstDraw = true;
    private boolean mDrawRequested;

    private float mWidth;
    private float mHeight;

    private int[] mTempSequence;
    private int[] mSequence;

    private float mScale;
    private Interpolator mInterpolator;

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
        mInterpolator = new AccelerateDecelerateInterpolator();

        // A new paint with the style as stroke
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2f);
        mPaint.setStyle(Paint.Style.STROKE);

        // Set up size values
        mScale = 1;
        mWidth = DEFAULT_WIDTH;
        mHeight = DEFAULT_HEIGHT;

        mTempSequence = null;
        mSequence = DEFAULT_SEQUENCE;

        measureTextSize(mWidth);
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

    public void setSequence(final int[] sequence) {

        if (sequence == null) {
            throw new IllegalArgumentException("Sequence cannot be null");

        } else if (sequence.length == 0) {
            throw new IllegalArgumentException("Sequence cannot be empty");
        }

        if (isAnimating()) {
            mTempSequence = new int[sequence.length];
            System.arraycopy(sequence, 0, mTempSequence, 0, sequence.length);

        } else {
            mSequence = new int[sequence.length];
            System.arraycopy(sequence, 0, mSequence, 0, sequence.length);
            checkSequenceBounds();
        }
    }

    public void setInterpolator(final Interpolator interpolator) {
        mInterpolator = (interpolator == null) ? new LinearInterpolator() : interpolator;
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

    public int getCurrentNumber() {
        return mSequence[mCurrent];
    }

    public void setNextNumberIndex(final int next) {
        mNext = next;
        checkSequenceBounds();
    }

    public void advance() {
        advance(mNext + 1);
    }

    public void advance(final int nextIndex) {
        // Convenience to set the next index and advance to it in one call
        setNextNumberIndex(nextIndex);
        checkSequenceBounds();

        if (!isAnimating()) {
            drawNextNumber();
        }
    }

    public void advanceImmediate() {
        advanceImmediate(mNext + 1);
    }

    public void advanceImmediate(final int nextIndex) {
        // Convenience to set the next index and advance to it immediately in one call
        setNextNumberIndex(nextIndex);
        mCurrent = mNext;

        if (!isAnimating()) {
            postInvalidate();
        }
    }

    private void setScale(float scale) {

        if (scale == 0) {
            throw new IllegalArgumentException("Scale cannot be 0");
        }

        scale = Math.abs(scale);

        if (mScale == scale) return;

        // We must reset the values back to normal and then multiply them by the new scale
        // We can do this all at once by using the inverseFactor!
        final float inverseFactor = (scale / mScale);

        mWidth *= inverseFactor;
        mHeight *= inverseFactor;

        applyScale(mPoints, inverseFactor);
        applyScale(mControlPoint1, inverseFactor);
        applyScale(mControlPoint2, inverseFactor);
        applyScale(mNumberWidth, inverseFactor);

        mScale = scale;

        mFirstDraw = false;
        requestLayout();
        invalidate();
    }

    private void applyScale(final float[][][] array, final float scale) {
        for (float[][] numberPoints : array) {
            for (float[] pointCoordinates : numberPoints) {
                pointCoordinates[0] *= scale;
                pointCoordinates[1] *= scale;
            }
        }
    }

    private void applyScale(final float[] array, final float scale) {
        for (int i = 0; i < array.length; i++) {
            array[i] *= scale;
        }
    }

    private float[] empty() {
        // Used to indicate an empty number field
        return new float[] { DEFAULT_WIDTH / 8, DEFAULT_HEIGHT / 2};
    }

    private void checkSequenceBounds() {
        // Wrap around to the start of the sequence. Ensures positive value
        final int mod = mSequence.length;
        mNext = (mNext % mod + mod) % mod;
    }

    private boolean isAnimating() {
        return mFrame != 0;
    }

    private float getAnimationFraction() {
        return (float) mFrame / (float) FRAME_COUNT;
    }

    private float lerp(float v0, float v1, float t) {
        return ((1 - t) * v0) + (t * v1);
    }

    private void drawNextNumber() {
        mDrawRequested = true;
        postInvalidate();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int minWidth = getSuggestedMinimumWidth();
        final int minHeight = getSuggestedMinimumHeight();
        int width, height;

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
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
    protected void onLayout(
            final boolean changed, final int l, final int t, final int r, final int b) {
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

        // Reset the path
        mPath.reset();

        checkSequenceBounds();
        final int thisNumberShown = mFirstDraw ? EMPTY_POSITION : mSequence[mCurrent];
        final int nextNumberShown = mSequence[mNext];

        final float[][] current = mPoints[thisNumberShown];
        final float[][] next = mPoints[nextNumberShown];
        final float[][] curr1 = mControlPoint1[thisNumberShown];
        final float[][] next1 = mControlPoint1[nextNumberShown];
        final float[][] curr2 = mControlPoint2[thisNumberShown];
        final float[][] next2 = mControlPoint2[nextNumberShown];

        // A factor of the diference between current and next frame based on interpolation
        // If we ourselves did not specifically request drawing, then draw our previous state
        final float factor = mInterpolator.getInterpolation(getAnimationFraction());

        final float thisWidth = mNumberWidth[thisNumberShown];
        final float nextWidth = mNumberWidth[nextNumberShown];
        final float interpolatedWidth = lerp(thisWidth, nextWidth, factor);
        if (thisWidth != nextWidth) {
            mWidth = interpolatedWidth;
            requestLayout();
        }

        final float translateX = ((float) getMeasuredWidth()  -  mWidth) / 2f;
        final float translateY = ((float) getMeasuredHeight() - mHeight) / 2f;

        // Draw the first point
        mPath.moveTo(
                lerp(current[0][0], next[0][0], factor) + translateX,
                lerp(current[0][1], next[0][1], factor) + translateY);

        // Connect the rest of the points as a bezier curve
        for (int i = 0; i < 4; i++) {
            mPath.cubicTo(
                    lerp(curr1[i][0], next1[i][0], factor) + translateX,
                    lerp(curr1[i][1], next1[i][1], factor) + translateY,
                    lerp(curr2[i][0], next2[i][0], factor) + translateX,
                    lerp(curr2[i][1], next2[i][1], factor) + translateY,
                    lerp(current[i + 1][0], next[i + 1][0], factor) + translateX,
                    lerp(current[i + 1][1], next[i + 1][1], factor) + translateY);
        }

        // Draw the path
        canvas.drawPath(mPath, mPaint);

        if (DEBUG) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        }

        if (!mDrawRequested) {
            return;
        }
        mDrawRequested = false;

        // Next frame
        mFrame++;

        // End of the current number animation
        // Begin setting values for the next number in the sequence
        if (mFrame > FRAME_COUNT) {

            mFrame = 0;
            mFirstDraw = false;
            mCurrent = mNext;
            mNext++;

            // Update the sequence when this current number tween animation ends
            if (mTempSequence != null) {
                mSequence = mTempSequence;
                mTempSequence = null;
            }

            checkSequenceBounds();

        } else {
            // Callback for the next frame.
            drawNextNumber();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final SavedState ss = new SavedState(super.onSaveInstanceState());

        // If we are animating while saving state, skip to the end by saving mCurrent as mNext
        ss.next = mNext;
        ss.current = isAnimating() ? mNext : mCurrent;
        ss.firstDraw = mFirstDraw;

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
        mFirstDraw = ss.firstDraw;
    }

    private static class SavedState extends BaseSavedState {
        public int next;
        public int current;
        public boolean firstDraw;

        private SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            next = in.readInt();
            current = in.readInt();
            firstDraw = in.readInt() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(next);
            out.writeInt(current);
            out.writeInt(firstDraw ? 1 : 0);
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
