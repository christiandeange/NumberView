/*
 * Copyright 2013 Christian De Angelis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deange.numberview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NumberView extends View {

    private static final String TAG = NumberView.class.getSimpleName();

    private static final boolean DEBUG = true;

    // "8" is used since it constitutes the widest number drawn
    private static final String MEASURING_TEXT = "8";

    // Invalidation "what" value
    private static final int HANDLER_WHAT = "what".hashCode();

    public static final int ALIGN_START = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_END = 2;

    private long mWaitUntil = System.currentTimeMillis();
    private final Handler mHandler = new Handler();

    private Runnable mInvalidateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };


    // NOTE: These fields are not static so that they may be scaled for each instance
    private float[][][] mPoints =
    {
            {{0, 82}, {55.5f, 0}, {111.5f, 82}, {55.5f, 162}, {0, 82}},
            {{0, 0}, {27.5f, 0}, {27.5f, 160.5f}, {27.5f, 160.5f}, {27.5f, 160.5f}},
            {{0, 43}, {88.5f, 44}, {52, 105}, {1, 160}, {91, 160}},
            {{9.25f, 36}, {45.5f, 0}, {45.5f, 78}, {46, 162}, {2.5f, 125}},
            {{112, 121}, {0, 121}, {86, 0}, {86, 121}, {86, 154}},
            {{101, 0}, {46, 0}, {27, 58}, {100, 109}, {0, 134}},
            {{64, 0}, {64, 0}, {0, 106}, {107, 106}, {7, 80}},
            {{1, 0}, {112, 0}, {74.67f, 52.34f}, {37.34f, 105.67f}, {0, 160}},
            {{45, 77}, {45, 0}, {45, 77}, {45, 160}, {45, 77}},
            {{101, 83}, {1, 57}, {108, 57}, {44, 163}, {44, 163}},
    };

    // The set of the "first" control points of each segment.
    private float[][][] mControlPoint1 =
    {
            {{0, 42}, {88.5f, 0}, {111.5f, 122}, {22.5f, 162}},
            {{0, 0}, {27.5f, 0}, {27.5f, 160.5f}, {27.5f, 160.5f}},
            {{3, -15}, {88.5f, 61}, {38, 121}, {1, 160}},
            {{9, 9}, {102, 0}, {104, 78}, {0, 162}},
            {{112, 121}, {0, 121}, {86, 0}, {86, 121}},
            {{46, 0}, {27, 58}, {52, 46}, {95, 163}},
            {{64, 0}, {25, 59}, {6, 188}, {100, 46}},
            {{1, 0}, {112, 0}, {74.67f, 52.34f}, {37.34f, 105.67f}},
            {{-12, 76}, {98, 0}, {-12, 77}, {98, 160}},
            {{78, 119}, {-4, -9}, {104, 91}, {44, 163}},
    };

    // The set of the "second" control points of each segment.
    private float[][][] mControlPoint2 =
    {
            {{22.5f, 0}, {111.5f, 42}, {88.5f, 162}, {0, 122}},
            {{27.5f, 0}, {27.5f, 160.5f}, {27.5f, 160.5f}, {27.5f, 160.5f}},
            {{87, -13}, {74, 81}, {18, 138}, {91, 160}},
            {{32, 0}, {92, 78}, {96, 162}, {2, 132}},
            {{0, 121}, {86, 0}, {86, 121}, {86, 154}},
            {{46, 0}, {27, 58}, {100, 65}, {23, 178}},
            {{64, 0}, {2, 72}, {112, 172}, {30, 44}},
            {{112, 0}, {74.67f, 52.34f}, {37.34f, 105.67f}, {0, 160}},
            {{-12, 0}, {98, 77}, {-20, 160}, {98, 77}},
            {{8, 117}, {102, -25}, {83, 104}, {44, 163}},
    };

    private static final float DEFAULT_WIDTH = 115;
    private static final float DEFAULT_HEIGHT = 165;

    private Paint mPaint = new Paint();
    private final Path mPath = new Path();

    private long mLastChange = System.currentTimeMillis();

    private int mIndex;
    private int mCurrent;
    private int mFrame;
    private int mFrameCount;

    private int mAlignX;
    private int mAlignY;
    private int mWidth;
    private int mHeight;
    private float mTranslateX;

    private int[] mTempSequence;
    private int[] mSequence;

    private boolean mAutoAdvance;
    private float mScale;
    private int mDuration;
    private Interpolator mInterpolator;

    public enum TweenStyle {
        BOUNCE,
        OVERSHOOT,
        ANTICIPATE,
        ANTICIPATE_OVERSHOOT,
        ACCEL,
        DECEL,
        ACCEL_DECEL,
        WACKY,
        NONE,
    }

    private static final Map<TweenStyle, Interpolator> mInterpolators = new HashMap<TweenStyle, Interpolator>();
    static {
        mInterpolators.put(TweenStyle.BOUNCE, new BounceInterpolator());
        mInterpolators.put(TweenStyle.OVERSHOOT, new OvershootInterpolator());
        mInterpolators.put(TweenStyle.ANTICIPATE, new AnticipateInterpolator());
        mInterpolators.put(TweenStyle.ANTICIPATE_OVERSHOOT, new AnticipateOvershootInterpolator());
        mInterpolators.put(TweenStyle.ACCEL, new AccelerateInterpolator());
        mInterpolators.put(TweenStyle.DECEL, new DecelerateInterpolator());
        mInterpolators.put(TweenStyle.ACCEL_DECEL, new AccelerateDecelerateInterpolator());
        mInterpolators.put(TweenStyle.WACKY, new CycleInterpolator(1));
        mInterpolators.put(TweenStyle.NONE, new LinearInterpolator());
    }

    public NumberView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);
        setInterpolator(TweenStyle.ACCEL_DECEL);

        // A new paint with the style as stroke.
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2f); //0f);
        mPaint.setStyle(Paint.Style.STROKE);

        // Set up timing values
        mAutoAdvance = true;
        mFrameCount = 24;
        mDuration = 1000;
        mScale = 1;

        mWidth = (int) (DEFAULT_WIDTH * mScale);
        mHeight = (int) (DEFAULT_HEIGHT * mScale);

        mTempSequence = null;
        mSequence = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        setAlignX(ALIGN_START);
        setAlignY(ALIGN_START);

        // Calculate the right value for the default text size
        int size = 0;
        do {
            size++;
            final float scaledSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics());
            mPaint.setTextSize(scaledSize);
        } while (mPaint.measureText(MEASURING_TEXT) < mWidth);

        setTextSize(mPaint.getTextSize());

        fixPointsAndDebug();
    }

    private float getMin(final float[][] array, final int point) {

        float min = Float.POSITIVE_INFINITY;

        for (float[] pointCoordinates : array) {

            final float pos = pointCoordinates[point];
            if (min > pos) {
                min = pos;
            }
        }

        return min;
    }

    private float getMax(final float[][] array, final int point) {

        float max = Float.NEGATIVE_INFINITY;

        for (float[] pointCoordinates : array) {

            final float pos = pointCoordinates[point];
            if (max < pos) {
                max = pos;
            }
        }

        return max;
    }

    private void fixPointsAndDebug() {

        final float[] xOffsets = new float[mPoints.length];
        final float[] yOffsets = new float[mPoints.length];

        // This iterates through each number. Each number is independent from the next
        for (int idx = 0; idx < mPoints.length; idx++) {

            final int x = 0;
            final int y = 1;

            final float minX = Math.min(getMin(mPoints[idx], x),
                               Math.min(getMin(mControlPoint1[idx], x),
                                        getMin(mControlPoint2[idx], x)));

            final float minY = Math.min(getMin(mPoints[idx], y),
                               Math.min(getMin(mControlPoint1[idx], y),
                                        getMin(mControlPoint2[idx], y)));

            xOffsets[idx] = -minX;
            yOffsets[idx] = -minY;
        }

        System.out.println("mXOffsets = " + Arrays.toString(xOffsets));
        System.out.println("mYOffsets = " + Arrays.toString(yOffsets));

        System.out.println("mPoints = ");
        printWithOffsets(mPoints, xOffsets, yOffsets);

        System.out.println("mControlPoint1 = ");
        printWithOffsets(mControlPoint1, xOffsets, yOffsets);

        System.out.println("mControlPoint2 = ");
        printWithOffsets(mControlPoint2, xOffsets, yOffsets);

    }

    private void printWithOffsets(final float[][][] array, final float[] xOffsets, final float[] yOffsets) {

        String s = "{\n";

        // This iterates through each number. Each number is independent from the next
        for (int idx = 0; idx < array.length; idx++) {

            final float[][] numberPoints = array[idx];

            s += "    {";

            for (int i = 0; i < numberPoints.length; i++) {

                String x = String.valueOf((numberPoints[i][0] + xOffsets[idx])) + "f";
                String y = String.valueOf((numberPoints[i][1] + yOffsets[idx])) + "f";

                s += "{" + x.replace(".0f", "") + ", " + y.replace(".0f", "") + "}";

                if (i != numberPoints.length - 1) {
                    s += ", ";
                }

            }

            s += "}, \n";
        }

        s += "};\n";

        System.out.println(s);
    }

    public void setSequence(final int[] sequence) {

        if (sequence == null) {
            throw new IllegalArgumentException("Sequence cannot be null");
        }

        if (mFrame != mFrameCount) {
            mTempSequence = new int[sequence.length];
            System.arraycopy(sequence, 0, mTempSequence, 0, sequence.length);

        } else {
            mSequence = new int[sequence.length];
            System.arraycopy(sequence, 0, mSequence, 0, sequence.length);
            checkSequenceBounds();
        }
    }

    public int[] getSequence() {
        return mSequence;
    }

    public void setInterpolator(final TweenStyle style) {
        setInterpolator(mInterpolators.get(style));
    }

    public void setInterpolator(final Interpolator interpolator) {

        if (interpolator == null) {
            throw new IllegalArgumentException("Interpolator cannot be null");
        }

        mInterpolator = interpolator;
    }

    public void setDuration(final int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setPaint(final Paint paint) {
        mPaint = new Paint(paint);
        mTranslateX = mPaint.getStrokeWidth();
        setScale(mPaint.measureText(MEASURING_TEXT) / mWidth);
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
        mTranslateX = mPaint.getStrokeWidth();
        setScale(mPaint.measureText(MEASURING_TEXT) / mWidth);
    }

    public float getTextSize() {
        return mPaint.getTextSize();
    }

    public void setAlignX(final int alignX) {
        mAlignX = alignX;
    }

    public int getAlignX() {
        return mAlignX;
    }

    public void setAlignY(final int alignY) {
        mAlignY = alignY;
    }

    public int getAlignY() {
        return mAlignY;
    }

    public void setAutoAdvance(final boolean autoAdvance) {
        mAutoAdvance = autoAdvance;

        if (!mAutoAdvance) {
            // Reset frame
            mFrame = 0;
        }
    }

    public boolean isAutoAdvance() {
        return mAutoAdvance;
    }

    public int getCurrentNumber() {
        return mCurrent;
    }

    public void setCurrentNumberIndex(final int index) {
        mIndex = index;
    }

    private strictfp void setScale(float scale) {

        scale = Math.abs(scale);

        if (scale == 0 || scale == mScale) {
            return;
        }

        final float inverseFactor = (scale / mScale);

        mWidth *= inverseFactor;
        mHeight *= inverseFactor;

        // We must reset the values back to normal and then multiply them by the new scale
        // We can do this all at once by using the inverseFactor!
        //
        // inverseFactor = new / old;
        // mScale = old (originally)
        // mScale * inverseFactor
        //      = mScale * new / old
        //      = old * (new / old)
        //      = new * (old / old)
        //      = new
        //
        // This changes the scale to the new value
        // </math>

        applyScale(mPoints, inverseFactor);
        applyScale(mControlPoint1, inverseFactor);
        applyScale(mControlPoint2, inverseFactor);

        mScale = scale;

        invalidateDelayed();
    }

    private strictfp void applyScale(final float[][][] array, final float scale) {
        for (float[][] numberPoints : array) {
            for (float[] pointCoordinates : numberPoints) {
                pointCoordinates[0] = ((pointCoordinates[0] - mTranslateX) * scale) + mTranslateX;
                pointCoordinates[1] = scale;
            }
        }
    }

    private float[] empty() {
        // Used to indicate an empty number field
        return new float[] {70, 100};
    }

    public void advance(final int nextIndex) {
        // Convenience to set the next index and advance to it in one call
        setCurrentNumberIndex(nextIndex);
        advance();
    }

    public void advance() {

        if (mFrame % mFrameCount == 0) {
            mFrame = 0;

        } else {
            mIndex++;
            checkSequenceBounds();
        }

        invalidateDelayed(1);
    }

    private void checkSequenceBounds() {
        if (mIndex >= mSequence.length) {
            // Wrap around to the start of the sequence
            mIndex = 0;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // Need to realign our drawing bounds.
        // Otherwise, we get some strange bounds for the first frame
        resolveLayoutParams();
    }

    private void resolveLayoutParams() {

        final ViewGroup.LayoutParams params = getLayoutParams();
        if (params != null) {
            boolean changeParams = false;

            if (params.width != ViewGroup.LayoutParams.WRAP_CONTENT
                    && params.width != ViewGroup.LayoutParams.MATCH_PARENT
                    && params.width != mWidth) {
                params.width = mWidth;
                changeParams = true;
            }

            if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT && params.height != mHeight) {
                params.height = mHeight;
                changeParams = true;
            }

            if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT && params.width != mWidth) {
                params.width = mWidth;
                changeParams = true;
            }

            if (changeParams) {
                setLayoutParams(params);
            }
        }
    }

    private float resolveTranslatedValue(final int alignStyle, final int parentDimen, final int drawDimen) {
        float resolvedValue;

        switch (alignStyle) {
            case ALIGN_CENTER:
                resolvedValue = (parentDimen - drawDimen) / 2 - 1;
                break;

            case ALIGN_END:
                resolvedValue = parentDimen - drawDimen;
                break;

            case ALIGN_START:
            default:
                resolvedValue = 1;
        }

        return resolvedValue;
    }

    @Override
    public void onDraw(final Canvas canvas) {

        super.onDraw(canvas);

        canvas.save();

        if (mFrame == 0) {
            mLastChange = System.currentTimeMillis();
        }

        resolveLayoutParams();

        // A factor of the diference between current and next frame based on interpolation.
        final float factor = mInterpolator.getInterpolation((float) mFrame / (float) mFrameCount);

        // Reset the path.
        mPath.reset();

        checkSequenceBounds();
        final int nextNumberShown = mSequence[mIndex];

        final float[][] curr = mPoints[mCurrent];
        final float[][] next = mPoints[nextNumberShown];
        final float[][] curr1 = mControlPoint1[mCurrent];
        final float[][] next1 = mControlPoint1[nextNumberShown];
        final float[][] curr2 = mControlPoint2[mCurrent];
        final float[][] next2 = mControlPoint2[nextNumberShown];

        final int x = 0;
        final float currentMaxX = Math.max(getMax(curr, x),
                                  Math.max(getMax(curr1, x),
                                           getMax(curr2, x)));

        final float nextMaxX    = Math.max(getMax(next, x),
                                  Math.max(getMax(next1, x),
                                           getMax(next2, x)));

        final int newWidth = (int) (((1 - factor) * currentMaxX) + (factor * nextMaxX));
        if (mWidth != newWidth && System.currentTimeMillis() >= mWaitUntil) {
            // TODO make this work.
//            mWidth = newWidth;
        }

        final float translateX = resolveTranslatedValue(mAlignX, getWidth(), mWidth);
        final float translateY = resolveTranslatedValue(mAlignY, getHeight(), mHeight);

        // Draw the first point
        mPath.moveTo(
                curr[0][0] + ((next[0][0] - curr[0][0]) * factor + translateX),
                curr[0][1] + ((next[0][1] - curr[0][1]) * factor + translateY));

        // Connect the rest of the points as a bezier curve.
        for (int i = 0; i < 4; i++) {
            mPath.cubicTo(
                    curr1[i][0] + ((next1[i][0] - curr1[i][0]) * factor + translateX),         // Control point 1
                    curr1[i][1] + ((next1[i][1] - curr1[i][1]) * factor + translateY),
                    curr2[i][0] + ((next2[i][0] - curr2[i][0]) * factor + translateX),         // Control point 2
                    curr2[i][1] + ((next2[i][1] - curr2[i][1]) * factor + translateY),
                    curr[i + 1][0] + ((next[i + 1][0] - curr[i + 1][0]) * factor + translateX),    // Point
                    curr[i + 1][1] + ((next[i + 1][1] - curr[i + 1][1]) * factor + translateY));
        }

        // Draw the path.
        canvas.drawPath(mPath, mPaint);

        if (DEBUG) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        }

        canvas.restore();

        // Next frame.
        mFrame++;

        int frameDelay = 0;

        // End of the current number animation
        // Begin setting values for the next number in the sequence
        if (mFrame > mFrameCount) {

            mFrame = 0;
            mCurrent = nextNumberShown;
            mIndex++;

            // Calculate wait time for the next second
            final long now = System.currentTimeMillis();
            frameDelay = mDuration - (int) (now - mLastChange);

            // Update the sequence when this current number tween animation ends
            if (mTempSequence != null) {
                mSequence = mTempSequence;
                mTempSequence = null;
            }

            checkSequenceBounds();
        }

        // If we are not doing an auto advance, then stop!
        if (!mAutoAdvance && mFrame == 0) {
            mWaitUntil = System.currentTimeMillis() + frameDelay;
            return;
        }

        // Callback for the next frame.
        invalidateDelayed(frameDelay);
    }

    public void invalidateDelayed() {
        invalidateDelayed(0);
    }

    // If we request an invalidation, then we have to ensure that we cancel any pending ones.
    // This is to fix the weird issue with the delay not being respected
    public void invalidateDelayed(final long delayMilliseconds) {

        mHandler.removeMessages(HANDLER_WHAT);

        final Message msg = Message.obtain(mHandler, mInvalidateRunnable);
        msg.what = HANDLER_WHAT;

        mWaitUntil = System.currentTimeMillis() + delayMilliseconds;
        mHandler.sendMessageDelayed(msg, delayMilliseconds);
    }

}
