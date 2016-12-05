package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Zero implements Digit {

    private final float[][] POINTS = { { 14.5f, 100 }, { 70, 18 }, { 126, 100 }, { 70, 180 }, { 14.5f, 100 } };
    private final float[][] CONTROLS1 = { { 14.5f, 60 }, { 103, 18 }, { 126, 140 }, { 37, 180 } };
    private final float[][] CONTROLS2 = { { 37, 18 }, { 126, 60 }, { 103, 180 }, { 14.5f, 140 } };

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
