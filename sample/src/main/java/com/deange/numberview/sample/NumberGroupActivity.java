package com.deange.numberview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.deange.numberview.NumberViewGroup;
import com.deange.numberview.digits.Digits;

import java.util.Timer;
import java.util.TimerTask;

public class NumberGroupActivity extends Activity implements View.OnClickListener {

    private static final String KEY_TIME = "time";

    private Timer mTimer = new Timer();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private NumberViewGroup mNumberViewGroup;

    private Button mResetButton;
    private Button mStartStopButton;

    private boolean mStarted = false;
    private int mTime;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_group);

        mResetButton = (Button) findViewById(R.id.button_reset);
        mStartStopButton = (Button) findViewById(R.id.button_start_stop);

        mResetButton.setOnClickListener(this);
        mStartStopButton.setOnClickListener(this);

        mNumberViewGroup = (NumberViewGroup) findViewById(R.id.number_group);
        mNumberViewGroup.hideNow();

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
        mNumberViewGroup.show(mTime);
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
        mNumberViewGroup.hideNow();
        mTime = 0;
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
