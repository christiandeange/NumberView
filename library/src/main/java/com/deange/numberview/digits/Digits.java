package com.deange.numberview.digits;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public final class Digits {

    private static final String TAG = "Digits";

    private static final Map<Character, Digit> DIGITS = new HashMap<>();

    static {
        register(new Zero());
        register(new One());
        register(new Two());
        register(new Three());
        register(new Four());
        register(new Five());
        register(new Six());
        register(new Seven());
        register(new Eight());
        register(new Nine());
        register(new Empty());
    }

    private Digits() {
        throw new AssertionError();
    }

    public static void register(final Digit digit) {
        final char character = digit.getChar();
        final Digit oldValue = DIGITS.put(character, digit);
        if (oldValue != null) {
            Log.w(TAG, "Replacing existing digit " + oldValue + " for character '" + character + "'");
        }
    }

    public static Digit empty() {
        return forChar('\0');
    }

    public static Digit forChar(final char character) {
        return DIGITS.get(character);
    }

    public static Digit forInt(final int digit) {
        if (digit < 0 || digit > 9) {
            throw new IllegalArgumentException("Digit must be between 0 and 9");
        }
        return forChar((char) ('0' + digit));
    }

}
