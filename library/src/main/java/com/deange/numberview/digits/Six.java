package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Six implements Digit {

    private final float[][] POINTS = { { 80, 20 }, { 80, 20 }, { 16, 126 }, { 123, 126 }, { 23, 100 } };
    private final float[][] CONTROLS1 = { { 80, 20 }, { 41, 79 }, { 22, 208 }, { 116, 66 } };
    private final float[][] CONTROLS2 = { { 80, 20 }, { 18, 92 }, { 128, 192 }, { 46, 64 } };

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
        return '6';
    }
}
