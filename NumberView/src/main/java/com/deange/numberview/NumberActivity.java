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
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class NumberActivity extends Activity implements View.OnClickListener {

    private Timer mTimer = new Timer();

    private NumberView mMinuteTensView;
    private NumberView mMinuteOnesView;
    private NumberView mSecondTensView;
    private NumberView mSecondOnesView;

    private Button mResetButton;
    private Button mStartStopButton;

    private int mTime = 0;

    private boolean mStarted = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResetButton = (Button) findViewById(R.id.button_reset);
        mStartStopButton = (Button) findViewById(R.id.button_start_stop);

        mResetButton.setOnClickListener(this);
        mStartStopButton.setOnClickListener(this);

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

        startTimer();
    }

    private void updateUi() {

        mSecondOnesView.advance(mTime % 10);

        if (mTime % 10 == 0) {
            mSecondTensView.advance((mTime / 10) % 6);

            if (mTime % 60 == 0) {
                mMinuteOnesView.advance((mTime / 60) % 10);

                if (mTime % 600 == 0) {
                    mMinuteTensView.advance((mTime / 600) % 6);
                }
            }
        }

        mTime++;
    }

    private void startTimer() {
        mTimer.scheduleAtFixedRate(new UpdateTask(), 0, 1000);
    }

    private void handleStartStop() {
        if (mStarted) {
            mTimer.cancel();
            mTimer.purge();
            mStartStopButton.setText(R.string.button_start);

        } else {
            mTimer = new Timer();
            startTimer();
            mStartStopButton.setText(R.string.button_stop);
        }

        mStarted = !mStarted;
    }

    private void handleReset() {
        mTime = 0;

        mSecondOnesView.advance(0);
        mSecondTensView.advance(0);
        mMinuteOnesView.advance(0);
        mMinuteTensView.advance(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_reset:
                handleReset();
                break;

            case R.id.button_start_stop:
                handleStartStop();
                break;

        }
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            updateUi();
        }
    }
}
