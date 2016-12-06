package com.deange.numberview.digits;

import java.io.Serializable;

public interface Digit extends Serializable {

    float[][] getPoints();

    float[][] getControlPoints1();

    float[][] getControlPoints2();

    float getWidth();

    char getChar();

}
