package com.deange.numberview.digits;

import com.deange.numberview.NumberView;

/* package */ class Empty implements Digit {

    private static final float[] F = { NumberView.DEFAULT_WIDTH / 8f, NumberView.DEFAULT_HEIGHT / 2f };
    private static final float[][] POINTS = new float[][]{ F.clone(), F.clone(), F.clone(), F.clone(), F.clone() };
    private static final float[][] CONTROLS1 = new float[][]{ F.clone(), F.clone(), F.clone(), F.clone() };
    private static final float[][] CONTROLS2 = new float[][]{ F.clone(), F.clone(), F.clone(), F.clone() };

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
        return 1f;
    }

    @Override
    public char getChar() {
        return '\0';
    }
}
