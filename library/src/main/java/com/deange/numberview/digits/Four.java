package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Four implements Digit {

    private final float[][] POINTS = { { 125, 146 }, { 13, 146 }, { 99, 25 }, { 99, 146 }, { 99, 179 } };
    private final float[][] CONTROLS1 = { { 125, 146 }, { 13, 146 }, { 99, 25 }, { 99, 146 } };
    private final float[][] CONTROLS2 = { { 13, 146 }, { 99, 25 }, { 99, 146 }, { 99, 179 } };

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
