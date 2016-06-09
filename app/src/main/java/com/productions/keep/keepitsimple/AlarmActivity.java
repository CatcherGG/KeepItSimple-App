package com.productions.keep.keepitsimple;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;

public class AlarmActivity extends Activity {
    private Button stopAlarm;
    private Button childNotWithMe;
    private static AlarmActivity inst;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public static AlarmActivity instance(){
        return inst;
    }

    @Override
    public void onStart() {
        Log.d("AlarmActivity","start");
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AlarmActivity","onCreate");
        setContentView(R.layout.activity_alarm);
        stopAlarm = (Button)findViewById(R.id.button5);
        childNotWithMe = (Button)findViewById(R.id.button6);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AlarmActivity","clicked on stop the alarm");
                stopAlarm();
            }
        });
        childNotWithMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AlarmActivity","clicked on child not with me");
                Intent intent = new Intent(AlarmActivity.this,OtherActivity.class);
                startActivity(intent);
            }
        });
        startAlarm();
    }

    private void startAlarm(){
        Log.d("AlarmActivity","start alarm");
        /*Intent myIntent = new Intent(AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);*/
    }

    private void stopAlarm(){
        try {
            alarmManager.cancel(pendingIntent);
        }
        catch(Exception e){
            Log.e("AlarmActivity","failed to close the alarm");
        }
    }
}
