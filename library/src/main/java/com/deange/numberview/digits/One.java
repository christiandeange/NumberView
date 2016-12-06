package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class One implements Digit {

    private final float[][] POINTS = { { 15, 20.5f }, { 42.5f, 20.5f }, { 42.5f, 181 }, { 42.5f, 181 }, { 42.5f, 181 } };
    private final float[][] CONTROLS1 = { { 15, 20.5f }, { 42.5f, 20.5f }, { 42.5f, 181 }, { 42.5f, 181 } };
    private final float[][] CONTROLS2 = { { 15, 20.5f }, { 42.5f, 20.5f }, { 42.5f, 181 }, { 42.5f, 181 } };

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
        return DEFAULT_WIDTH / 2f;
    }

    @Override
    public char getChar() {
        return '1';
    }
}
