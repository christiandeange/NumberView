package com.deange.numberview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

public class NumberViewGroup extends LinearLayout {

    private boolean mImmediate;
    private int mMinShown = -1;
    private int mNumber = NumberView.HIDE_NUMBER;

    public NumberViewGroup(final Context context) {
        super(context);
        init();
    }

    public NumberViewGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public NumberViewGroup(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberViewGroup(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    protected NumberView addNewChild() {
        final NumberView child = new NumberView(getContext());
        child.hideImmediate();
        addView(child, 0);

        return child;
    }

    private int getDigit(int number, int digit) {
        return (int) ((number / Math.pow(10, digit)) % 10);
    }

    private int getIntLength(final int number) {
        // Yeah it's ugly as sin but it works, and it works fast.
        final int n = (number == Integer.MIN_VALUE) ? Integer.MAX_VALUE : Math.abs(number);
        return n < 100000
                ? n < 100 ? n < 10 ? 1 : 2 : n < 1000 ? 3 : n < 10000 ? 4 : 5
                : n < 10000000 ? n < 1000000 ? 6 : 7 : n < 100000000 ? 8 : n < 1000000000 ? 9 : 10;
    }

    private int getRequiredChildCount() {
        final int requested;
        switch (mNumber) {
            case NumberView.HIDE_NUMBER:
                requested = 0;
                break;

            default:
                requested = getIntLength(mNumber);
                break;
        }

        return Math.max(mMinShown, requested);
    }

    private void bindViews() {

        final int size = getRequiredChildCount();

        for (int i = 0; i < size; i++) {

            while (i >= getChildCount()) {
                addNewChild();
            }

            final NumberView child = getDigit(i);
            final int number = Math.abs(getDigit(mNumber, i));

            if (mImmediate) {
                child.advanceImmediate(number);
            } else {
                child.advance(number);
            }
        }

        for (int i = size; i < getChildCount(); i++) {
            // Unused children :'(
            final NumberView child = getDigit(i);
            if (mImmediate) {
                child.hideImmediate();
            } else {
                child.hide();
            }
        }

        requestLayout();
        invalidate();
    }

    public NumberView getDigit(final int index) {
        // Reverse the indexing order of the children
        return (NumberView) getChildAt(getChildCount() - index - 1);
    }

    public NumberView[] getDigits() {
        // Returns views in order from LSB to MSB
        final NumberView[] views = new NumberView[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            views[i] = getDigit(i);
        }
        return views;
    }

    public void advance(final int number) {
        mNumber = number;
        mImmediate = false;
        bindViews();
    }

    public void advanceImmediate(final int number) {
        mNumber = number;
        mImmediate = true;
        bindViews();
    }

    public void advance() {
        advance(mNumber + 1);
    }

    public void advanceImmediate() {
        advanceImmediate(mNumber + 1);
    }

    public void hide() {
        advance(NumberView.HIDE_NUMBER);
    }

    public void hideImmediate() {
        advanceImmediate(NumberView.HIDE_NUMBER);
    }

    public void setMinimumNumbersShown(final int minimum) {
        mMinShown = minimum;

        while (getChildCount() < mMinShown) {
            addNewChild();
        }
    }
}
