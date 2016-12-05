package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Nine implements Digit {

    private final float[][] POINTS = { { 117, 100 }, { 17, 74 }, { 124, 74 }, { 60, 180 }, { 60, 180 } };
    private final float[][] CONTROLS1 = { { 94, 136 }, { 12, 8 }, { 122, 108 }, { 60, 180 } };
    private final float[][] CONTROLS2 = { { 24, 134 }, { 118, -8 }, { 99, 121 }, { 60, 180 } };

    @Override
    public float[][] getPoints() {
        return POINTS;
    }

    @Override
    public float[][] getControlPoints1() {
        return CONTROLS1;
    }

    @Override
    public float[][] getControlPoints2() {
        return CONTROLS2;
    }

    @Override
    public float getWidth() {
        return DEFAULT_WIDTH;
    }
}
