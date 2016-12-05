package com.deange.numberview.digits;

public final class StandardDigits {

    public static final int HIDE_NUMBER = -1;

    private StandardDigits() {
        throw new AssertionError();
    }

    public static Digit empty() {
        return forNumber(HIDE_NUMBER);
    }

    public static Digit forNumber(final int digit) {
        switch (digit) {
            case 0:
                return new Zero();
            case 1:
                return new One();
            case 2:
                return new Two();
            case 3:
                return new Three();
            case 4:
                return new Four();
            case 5:
                return new Five();
            case 6:
                return new Six();
            case 7:
                return new Seven();
            case 8:
                return new Eight();
            case 9:
                return new Nine();
            case HIDE_NUMBER:
                return new Empty();
            default:
                throw new IllegalArgumentException("Digit must be between 0 and 9 inclusively");
        }
    }

}
