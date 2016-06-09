package com.productions.keep.keepitsimple;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.productions.keep.keepitsimple.DetectedActivitiesIntentService.LocalBinder;

public class WithoutChild extends Activity {

    private Button stopForTime;
    private DetectedActivitiesIntentService detectionService;
    private boolean mBounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        Log.d("WithoutChild", "started");
        stopForTime = (Button)findViewById(R.id.button7);
        stopForTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("WithoutChild", "stop for time");
                sendStopForTime(60 * 60);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, DetectedActivitiesIntentService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Log.d("WithoutChild","disconnected the service");
            //Toast.makeText(WithoutChild.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            detectionService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("WithoutChild","connected the service");
            //Toast.makeText(WithoutChild.this, "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            LocalBinder mLocalBinder = (DetectedActivitiesIntentService.LocalBinder) service;
            detectionService = mLocalBinder.getServiceInstance();
        }
    };

    private void sendStopForTime(long seconds) {
        detectionService.stopDetectionForPeriod(seconds);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }
}
