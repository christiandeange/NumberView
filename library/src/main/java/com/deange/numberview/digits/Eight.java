package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Eight implements Digit {

    private final float[][] POINTS = { { 71, 96 }, { 71, 19 }, { 71, 96 }, { 71, 179 }, { 71, 96 } };
    private final float[][] CONTROLS1 = { { 14, 95 }, { 124, 19 }, { 14, 96 }, { 124, 179 } };
    private final float[][] CONTROLS2 = { { 14, 19 }, { 124, 96 }, { 6, 179 }, { 124, 96 } };

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
        return '8';
    }
}
