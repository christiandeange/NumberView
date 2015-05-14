package com.deange.numberview;

import android.graphics.Paint;

public interface PaintProvider {
    void mutate(final Paint paint, final int digit);
}
