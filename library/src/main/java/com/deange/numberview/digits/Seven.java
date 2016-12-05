package com.deange.numberview.digits;

import static com.deange.numberview.NumberView.DEFAULT_WIDTH;

/* package */ class Seven implements Digit {

    private final float[][] POINTS = { { 17, 21 }, { 128, 21 }, { 90.67f, 73.34f }, { 53.34f, 126.67f }, { 16, 181 } };
    private final float[][] CONTROLS1 = { { 17, 21 }, { 128, 21 }, { 90.67f, 73.34f }, { 53.34f, 126.67f } };
    private final float[][] CONTROLS2 = { { 128, 21 }, { 90.67f, 73.34f }, { 53.34f, 126.67f }, { 16, 181 } };

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
