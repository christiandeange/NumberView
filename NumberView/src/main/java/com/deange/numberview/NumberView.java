package com.deange.numberview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

public class NumberView extends View {

    private static final String TAG = NumberView.class.getSimpleName();

    //  NOTE: These fields are not static so that they may be scaled for each instance

    // Approximate width:   points 30 -> 170 == width of 140
    // Approximate height:  points  0 -> 200 == height of 200

    // The 5 end points. (Note: The last end point is the first end point of the next segment.
    private float[][][] mPoints =
    {
            {{44.5f, 100}, {100, 18}, {156, 100}, {100, 180}, {44.5f, 100}},                // 0
            {{77, 20.5f}, {104.5f, 20.5f}, {104.5f, 181}, {104.5f, 181}, {104.5f, 181}},    // 1
            {{56, 60}, {144.5f, 61}, {108, 122}, {57, 177}, {147, 177}},                    // 2
            {{63.25f, 54}, {99.5f, 18}, {99.5f, 96}, {100, 180}, {56.5f, 143}},             // 3
            {{155, 146}, {43, 146}, {129, 25}, {129, 146}, {129, 179}},                     // 4
            {{146, 20}, {91, 20}, {72, 78}, {145, 129}, {45, 154}},                         // 5
            {{110, 20}, {110, 20}, {46, 126}, {153, 126}, {53, 100}},                       // 6
            {{47, 21}, {158, 21}, {120.67f, 73.34f}, {83.34f, 126.67f}, {46, 181}},         // 7
            {{101, 96}, {101, 19}, {101, 96}, {101, 179}, {101, 96}},                       // 8
            {{147, 100}, {47, 74}, {154, 74}, {90, 180}, {90, 180}},                        // 9
            {{101, 96}, {101, 96}, {101, 96}, {101, 96}, {101, 96}}                         // nothing
    };

    // The set of the "first" control points of each segment.
    private float[][][] mControlPoint1 =
    {
            {{44.5f, 60}, {133, 18}, {156, 140}, {67, 180}},                // 0
            {{77, 20.5f}, {104.5f, 20.5f}, {104.5f, 181}, {104.5f, 181}},   // 1
            {{59, 2}, {144.5f, 78}, {94, 138}, {57, 177}},                  // 2
            {{63, 27}, {156, 18}, {158, 96}, {54, 180}},                    // 3
            {{155, 146}, {43, 146}, {129, 25}, {129, 146}},                 // 4
            {{91, 20}, {72, 78}, {97, 66}, {140, 183}},                     // 5
            {{110, 20}, {71, 79}, {52, 208}, {146, 66}},                    // 6
            {{47, 21}, {158, 21}, {120.67f, 73.34f}, {83.34f, 126.67f}},    // 7
            {{44, 95}, {154, 19}, {44, 96}, {154, 179}},                    // 8
            {{124, 136}, {42, 8}, {152, 108}, {90, 180}},                   // 9
            {{101, 96}, {101, 96}, {101, 96}, {101, 96}, {101, 96}}         // nothing
    };

    // The set of the "second" control points of each segment.
    private float[][][] mControlPoint2 =
    {
            {{67, 18}, {156, 60}, {133, 180}, {44.5f, 140}},                // 0
            {{104.5f, 20.5f}, {104.5f, 181}, {104.5f, 181}, {104.5f, 181}}, // 1
            {{143, 4}, {130, 98}, {74, 155}, {147, 177}},                   // 2
            {{86, 18}, {146, 96}, {150, 180}, {56, 150}},                   // 3
            {{43, 146}, {129, 25}, {129, 146}, {129, 179}},                 // 4
            {{91, 20}, {72, 78}, {145, 85}, {68, 198}},                     // 5
            {{110, 20}, {48, 92}, {158, 192}, {76, 64}},                    // 6
            {{158, 21}, {120.67f, 73.34f}, {83.34f, 126.67f}, {46, 181}},   // 7
            {{44, 19}, {154, 96}, {36, 179}, {154, 96}},                    // 8
            {{54, 134}, {148, -8}, {129, 121}, {90, 180}},                  // 9
            {{101, 96}, {101, 96}, {101, 96}, {101, 96}, {101, 96}}         // nothing
    };

    private static final int FRAME_COUNT_DELAY_PENALTY = 1;

    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();

    private long mLastChange = System.currentTimeMillis();

    private int mIndex;
    private int mCurrent;
    private int mFrame;
    private int mFrameCount;

    private float mLeft;
    private float mRight;
    private float mTop;
    private float mBottom;

    private int[] mTempSequence;
    private int[] mSequence;

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

    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);
        setTweenStyle(TweenStyle.ACCEL_DECEL);

        // A new paint with the style as stroke.
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(5.0f);
        mPaint.setStyle(Paint.Style.STROKE);

        mFrameCount = 24;
        mDuration = 1000;
        mScale = 1;

        setScale(2);

        mLeft = 30 * mScale;
        mRight = 170 * mScale;
        mTop = 0 * mScale;
        mBottom = 200 * mScale;

        mTempSequence = null;
        mSequence = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setScale((float) (Math.random() * 5));
            }
        });

    }

    public void setTweenStyle(TweenStyle style) {

        if (style == null) {
            Log.w(TAG, "TweenStyle is null, setting to TweenStyle.NONE");
            style = TweenStyle.NONE;
        }

        mInterpolator = mInterpolators.get(style);
    }

    public void setDuration(final int duration) {
        mDuration = duration;
    }

    public void setSequence(final int[] sequence) {

        if (sequence == null) {
            throw new NullPointerException("Sequence cannot be null");
        }

        if (mFrame != mFrameCount) {
            mTempSequence = new int[sequence.length];
            System.arraycopy(sequence, 0, mTempSequence, 0, sequence.length);
            return;
        }

        mSequence = new int[sequence.length];
        System.arraycopy(sequence, 0, mSequence, 0, sequence.length);
    }

    public strictfp void setScale(final float scale) {

        if (scale == 0) {
            throw new IllegalArgumentException("Scale cannot be 0");
        }

        if (mScale == scale) return;
        
        final float inverseFactor = (scale / mScale);

        mLeft *= inverseFactor;
        mRight *= inverseFactor;
        mTop *= inverseFactor;
        mBottom *= inverseFactor;

        // We must reset the values back to normal and then multiply them by the new scale
        applyScale(mPoints, inverseFactor);
        applyScale(mControlPoint1, inverseFactor);
        applyScale(mControlPoint2, inverseFactor);

        mScale = scale;
    }

    private strictfp void applyScale(final float[][][] array, final float scale) {
        for (float[][] numberPoints : array) {
            for (float[] pointCoordinates : numberPoints) {
                pointCoordinates[0] *= scale;
                pointCoordinates[1] *= scale;
            }
        }
    }

    public int getDuration() {
        return mDuration;
    }

    public int getCurrentNumber() {
        return mCurrent;
    }

    public int[] getSequence() {
        return mSequence;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();



    }

    @Override
    public void onDraw(Canvas canvas) {
        int count = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.MATRIX_SAVE_FLAG |
                Canvas.CLIP_SAVE_FLAG |
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        super.onDraw(canvas);

        if (mFrame == 0) {
            mLastChange = System.currentTimeMillis();
        }

        // A factor of the diference between current and next frame based on interpolation.
        final float factor = mInterpolator.getInterpolation((float) mFrame / (float) mFrameCount);

        // Reset the path.
        mPath.reset();

        final int nextValue = mSequence[mIndex];

        final float[][] current = mPoints[mCurrent];
        final float[][] next = mPoints[nextValue];

        final float[][] curr1 = mControlPoint1[mCurrent];
        final float[][] next1 = mControlPoint1[nextValue];

        final float[][] curr2 = mControlPoint2[mCurrent];
        final float[][] next2 = mControlPoint2[nextValue];

        // First point.
        mPath.moveTo(
                current[0][0] + ((next[0][0] - current[0][0]) * factor),
                current[0][1] + ((next[0][1] - current[0][1]) * factor));

        // Rest of the points connected as bezier curve.
        for (int i = 0; i < 4; i++) {
            mPath.cubicTo(
                    curr1[i][0] + ((next1[i][0] - curr1[i][0]) * factor),         // Control point 1
                    curr1[i][1] + ((next1[i][1] - curr1[i][1]) * factor),
                    curr2[i][0] + ((next2[i][0] - curr2[i][0]) * factor),         // Control point 2
                    curr2[i][1] + ((next2[i][1] - curr2[i][1]) * factor),
                    current[i + 1][0] + ((next[i + 1][0] - current[i + 1][0]) * factor),    // Point
                    current[i + 1][1] + ((next[i + 1][1] - current[i + 1][1]) * factor));
        }

        // Draw the path.
        canvas.drawPath(mPath, mPaint);

        final float minW = mLeft;
        final float maxW = mRight;
        final float minH = mTop;
        final float maxH = mBottom;

        debugLayout(canvas, 0, getWidth(), 0, getHeight());
        debugLayout(canvas, minW, maxW, minH, maxH);

        canvas.restoreToCount(count);

        // Next frame.
        mFrame++;

        int frameDelay = 1;

        // End of the current number animation
        // Begin setting values for the next number in the sequence
        if (mFrame > mFrameCount) {

            mFrame = 0;
            mCurrent = nextValue;
            mIndex++;

            if (mIndex >= mSequence.length) {
                mIndex = 0;
            }

            final long now = System.currentTimeMillis();
            frameDelay = mDuration - (int) (now - mLastChange);

            if (frameDelay < 0) {
                int newFrameCount = Math.max(0, mFrameCount - FRAME_COUNT_DELAY_PENALTY);

                Log.w(TAG, "Animation took too long! Reducing frame count from " +
                        mFrameCount + " to " + newFrameCount);

                mFrameCount = newFrameCount;
            }

            // Update the sequence when this current number tween animation ends
            if (mTempSequence != null) {
                mSequence = mTempSequence;
                mTempSequence = null;
            }
        }

        // Callback for the next frame.
        postInvalidateDelayed(frameDelay);
    }

    private void debugLayout(final Canvas canvas, final float minW, final float maxW, final float minH, final float maxH) {

        final float[] array = new float[16];
        int ptr = 0;
        array[ptr++] = minW;
        array[ptr++] = minH;
        array[ptr++] = maxW;
        array[ptr++] = minH;

        array[ptr++] = maxW;
        array[ptr++] = minH;
        array[ptr++] = maxW;
        array[ptr++] = maxH;

        array[ptr++] = maxW;
        array[ptr++] = maxH;
        array[ptr++] = minW;
        array[ptr++] = maxH;

        array[ptr++] = minW;
        array[ptr++] = maxH;
        array[ptr++] = minW;
        array[ptr++] = minH;

        canvas.drawLines(array, mPaint);
    }
}
