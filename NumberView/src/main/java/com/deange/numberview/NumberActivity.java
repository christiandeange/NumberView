/*
 * Copyright 2013 Christian De Angelis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deange.numberview;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class NumberActivity extends Activity {

    private final Timer mTimer = new Timer();

    private NumberView mMinuteTensView;
    private NumberView mMinuteOnesView;
    private NumberView mSecondTensView;
    private NumberView mSecondOnesView;

    private int mTime = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSecondTensView = (NumberView) findViewById(R.id.number_second_tens_position);
        mSecondOnesView = (NumberView) findViewById(R.id.number_second_ones_position);
        mMinuteTensView = (NumberView) findViewById(R.id.number_minute_tens_position);
        mMinuteOnesView = (NumberView) findViewById(R.id.number_minute_ones_position);

        mSecondTensView.setAutoAdvance(false);
        mSecondOnesView.setAutoAdvance(false);
        mMinuteTensView.setAutoAdvance(false);
        mMinuteOnesView.setAutoAdvance(false);

        mSecondTensView.setSequence(new int[]{0, 1, 2, 3, 4, 5});
        mMinuteTensView.setSequence(new int[]{0, 1, 2, 3, 4, 5});

        final Paint thickPaint = mMinuteTensView.getPaint();
        thickPaint.setStrokeWidth(5f);
        mMinuteTensView.setPaint(thickPaint);
        mMinuteOnesView.setPaint(thickPaint);

        mTimer.scheduleAtFixedRate(new UpdateTask(), 0, 1000);
    }

    private void updateUi() {

        mSecondOnesView.advance();

        if (mTime != 0) {
            if (mTime % 10 == 0) {
                mSecondTensView.advance();

                if (mTime % 60 == 0) {
                    mMinuteOnesView.advance();

                    if (mTime % 600 == 0) {
                        mMinuteTensView.advance();
                    }
                }
            }
        }

        mTime++;
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            updateUi();
        }
    }
}
