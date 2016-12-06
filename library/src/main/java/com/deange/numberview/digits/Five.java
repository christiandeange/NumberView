package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Five implements Digit {

    private final float[][] POINTS = { { 116, 20 }, { 61, 20 }, { 42, 78 }, { 115, 129 }, { 15, 154 } };
    private final float[][] CONTROLS1 = { { 61, 20 }, { 42, 78 }, { 67, 66 }, { 110, 183 } };
    private final float[][] CONTROLS2 = { { 61, 20 }, { 42, 78 }, { 115, 85 }, { 38, 198 } };

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

    @Override
    public char getChar() {
        return '5';
    }
}
