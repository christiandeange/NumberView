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
