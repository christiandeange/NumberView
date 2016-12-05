package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Three implements Digit {

    private final float[][] POINTS = { { 33.25f, 54 }, { 69.5f, 18 }, { 69.5f, 96 }, { 70, 180 }, { 26.5f, 143 } };
    private final float[][] CONTROLS1 = { { 33, 27 }, { 126, 18 }, { 128, 96 }, { 24, 180 } };
    private final float[][] CONTROLS2 = { { 56, 18 }, { 116, 96 }, { 120, 180 }, { 26, 150 } };

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
