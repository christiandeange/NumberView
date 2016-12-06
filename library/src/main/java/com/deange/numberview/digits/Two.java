package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Two implements Digit {

    private final float[][] POINTS = { { 26, 60 }, { 114.5f, 61 }, { 78, 122 }, { 27, 177 }, { 117, 177 } };
    private final float[][] CONTROLS1 = { { 29, 2 }, { 114.5f, 78 }, { 64, 138 }, { 27, 177 } };
    private final float[][] CONTROLS2 = { { 113, 4 }, { 100, 98 }, { 44, 155 }, { 117, 177 } };

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
        return '2';
    }
}
