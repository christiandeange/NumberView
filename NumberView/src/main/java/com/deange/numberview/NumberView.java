package com.deange.numberview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

public class NumberView extends View {

    private static final String TAG = NumberView.class.getSimpleName();

    //  NOTE: These fields are not static so that they may be scaled for each instance

    // Approximate width:   140
    // Approximate height:  200



    // The 5 end points. (Note: The last end point is the first end point of the next segment.
    private float[][][] mPoints =
    {
        {{14.5f, 100}, {70, 18}, {126, 100}, {70, 180}, {14.5f, 100}},
        {{47, 20.5f}, {74.5f, 20.5f}, {74.5f, 181}, {74.5f, 181}, {74.5f, 181}},
        {{26, 60}, {114.5f, 61}, {78, 122}, {27, 177}, {117, 177}},
        {{33.25f, 54}, {69.5f, 18}, {69.5f, 96}, {70, 180}, {26.5f, 143}},
        {{125, 146}, {13, 146}, {99, 25}, {99, 146}, {99, 179}},
        {{116, 20}, {61, 20}, {42, 78}, {115, 129}, {15, 154}},
        {{80, 20}, {80, 20}, {16, 126}, {123, 126}, {23, 100}},
        {{17, 21}, {128, 21}, {90.67f, 73.34f}, {53.34f, 126.67f}, {16, 181}},
        {{71, 96}, {71, 19}, {71, 96}, {71, 179}, {71, 96}},
        {{117, 100}, {17, 74}, {124, 74}, {60, 180}, {60, 180}},
        {{71, 96}, {71, 96}, {71, 96}, {71, 96}, {71, 96}},
    };


    // The set of the "first" control points of each segment.
    private float[][][] mControlPoint1 =
    {
        {{14.5f, 60}, {103, 18}, {126, 140}, {37, 180}},
        {{47, 20.5f}, {74.5f, 20.5f}, {74.5f, 181}, {74.5f, 181}},
        {{29, 2}, {114.5f, 78}, {64, 138}, {27, 177}},
        {{33, 27}, {126, 18}, {128, 96}, {24, 180}},
        {{125, 146}, {13, 146}, {99, 25}, {99, 146}},
        {{61, 20}, {42, 78}, {67, 66}, {110, 183}},
        {{80, 20}, {41, 79}, {22, 208}, {116, 66}},
        {{17, 21}, {128, 21}, {90.67f, 73.34f}, {53.34f, 126.67f}},
        {{14, 95}, {124, 19}, {14, 96}, {124, 179}},
        {{94, 136}, {12, 8}, {122, 108}, {60, 180}},
        {{71, 96}, {71, 96}, {71, 96}, {71, 96}, {71, 96}},
    };

    // The set of the "second" control points of each segment.
    private float[][][] mControlPoint2 =
    {
        {{37, 18}, {126, 60}, {103, 180}, {14.5f, 140}},
        {{74.5f, 20.5f}, {74.5f, 181}, {74.5f, 181}, {74.5f, 181}},
        {{113, 4}, {100, 98}, {44, 155}, {117, 177}},
        {{56, 18}, {116, 96}, {120, 180}, {26, 150}},
        {{13, 146}, {99, 25}, {99, 146}, {99, 179}},
        {{61, 20}, {42, 78}, {115, 85}, {38, 198}},
        {{80, 20}, {18, 92}, {128, 192}, {46, 64}},
        {{128, 21}, {90.67f, 73.34f}, {53.34f, 126.67f}, {16, 181}},
        {{14, 19}, {124, 96}, {6, 179}, {124, 96}},
        {{24, 134}, {118, -8}, {99, 121}, {60, 180}},
        {{71, 96}, {71, 96}, {71, 96}, {71, 96}, {71, 96}},
    };

    private static final int FRAME_COUNT_DELAY_PENALTY = 1;

    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();

    private long mLastChange = System.currentTimeMillis();
    private long mWaitUntil;

    private int mIndex;
    private int mCurrent;
    private int mFrame;
    private int mFrameCount;

    private float mWidth;
    private float mHeight;

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

        mWidth = 140 * mScale;
        mHeight = 200 * mScale;

        mTempSequence = null;
        mSequence = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setScale((float) ((Math.random() + 1) * 2));
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

        mWidth *= inverseFactor;
        mHeight *= inverseFactor;

        // We must reset the values back to normal and then multiply them by the new scale
        applyScale(mPoints, inverseFactor);
        applyScale(mControlPoint1, inverseFactor);
        applyScale(mControlPoint2, inverseFactor);

        mScale = scale;

        postInvalidateDelayed(1);
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
    public void onDraw(Canvas canvas) {
        int count = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.MATRIX_SAVE_FLAG |
                Canvas.CLIP_SAVE_FLAG |
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        super.onDraw(canvas);

        final ViewGroup.LayoutParams params = getLayoutParams();
        if (params != null) {
            boolean changeParams = false;
            if (params.width != (int) mWidth) {
                params.width = (int) mWidth;
                changeParams = true;
            }

            if (params.height != (int) mHeight) {
                params.height = (int) mHeight;
                changeParams = true;
            }

            if (changeParams) {
                setLayoutParams(params);
            }
        }

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

        debugLayout(canvas, 0, getWidth(), 0, getHeight());

        canvas.restoreToCount(count);

        if (mWaitUntil != 0 && (System.currentTimeMillis() + 10 < mWaitUntil)) {
            postInvalidateDelayed(mWaitUntil - System.currentTimeMillis());
            return;
        }

        mWaitUntil = 0;

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

            mWaitUntil = now + frameDelay;

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
