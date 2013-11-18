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
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class NumberActivity extends Activity {

    private final Timer mTimer = new Timer();

    private NumberView mTensView;
    private NumberView mOnesView;

    private int mTime = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTensView = (NumberView) findViewById(R.id.number_tens_position);
        mOnesView = (NumberView) findViewById(R.id.number_ones_position);

        mTensView.setAutoAdvance(false);
        mOnesView.setAutoAdvance(false);

        mTensView.setSequence(new int[] { 0, 1, 2, 3, 4, 5 });

        mTimer.scheduleAtFixedRate(new UpdateTask(), 0, 1000);
    }

    private void updateUi() {
        mOnesView.advance();
        if (mTime % 10 == 0) {
            mTensView.advance();
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
