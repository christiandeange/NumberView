package com.deange.numberview.sample;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.deange.numberview.NumberView;

import java.util.Timer;
import java.util.TimerTask;

public class NumberActivity extends Activity implements View.OnClickListener {

    private static final String KEY_TIME = "time";

    private Timer mTimer = new Timer();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private NumberView mMinuteTensView;
    private NumberView mMinuteOnesView;
    private NumberView mSecondTensView;
    private NumberView mSecondOnesView;

    private Button mResetButton;
    private Button mStartStopButton;

    private int mTime = 0;

    private boolean mStarted = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);

        mResetButton = (Button) findViewById(R.id.button_reset);
        mStartStopButton = (Button) findViewById(R.id.button_start_stop);

        mResetButton.setOnClickListener(this);
        mStartStopButton.setOnClickListener(this);

        mSecondTensView = (NumberView) findViewById(R.id.number_second_tens_position);
        mSecondOnesView = (NumberView) findViewById(R.id.number_second_ones_position);
        mMinuteTensView = (NumberView) findViewById(R.id.number_minute_tens_position);
        mMinuteOnesView = (NumberView) findViewById(R.id.number_minute_ones_position);

        mMinuteTensView.getPaint().setStrokeWidth(5f);
        mMinuteOnesView.getPaint().setStrokeWidth(5f);

        mTime = savedInstanceState == null ? 0 : savedInstanceState.getInt(KEY_TIME);
    }

    @Override
    protected void onResume() {
        handleStartStop();
        super.onResume();
    }

    @Override
    protected void onPause() {
        handleStartStop();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putInt(KEY_TIME, mTime);
        super.onSaveInstanceState(outState);
    }

    private void updateUi() {

        mSecondOnesView.advance(mTime % 10);
        mSecondTensView.advance((mTime / 10) % 6);
        mMinuteOnesView.advance((mTime / 60) % 10);
        mMinuteTensView.advance((mTime / 600) % 6);

        mTime++;
    }

    private void startTimer() {
        mTimer.scheduleAtFixedRate(new UpdateTask(), 0, 1000);
    }

    private void handleStartStop() {
        if (mStarted) {
            mTimer.cancel();
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
        mSecondOnesView.advanceImmediate(0);
        mSecondTensView.advanceImmediate(0);
        mMinuteOnesView.advanceImmediate(0);
        mMinuteTensView.advanceImmediate(0);
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
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateUi();
                }
            });
        }
    }
}
