package com.productions.keep.keepitsimple;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private Ringtone ringtone;

    public static AlarmActivity instance(){
        return inst;
    }

    @Override
    public void onStart() {
        Log.d("AlarmActivity","start");
        super.onStart();
        inst = this;
        startAlarm();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AlarmActivity","onCreate");
        setContentView(R.layout.activity_alarm);
        stopAlarm = (Button)findViewById(R.id.button5);
        childNotWithMe = (Button)findViewById(R.id.button6);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });
        childNotWithMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                Log.d("AlarmActivity","clicked on child not with me");
                Intent intent = new Intent(AlarmActivity.this,OtherActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        ringtone.stop();
        super.onDestroy();
    }

    private void startAlarm(){
        Log.d("AlarmActivity","start alarm");
        try {
            Intent myIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            ringtone.play();
        }
        catch(Exception e){
            Log.e("AlarmActivity","failed to start alarm");
            Log.e("AlarmActivity",e.toString());
        }
    }

    private void stopAlarm(){
        Log.d("AlarmActivity","clicked on stop the alarm");
        try {
            alarmManager.cancel(pendingIntent);
            ringtone.stop();
        }
        catch(Exception e){
            Log.e("AlarmActivity","failed to close the alarm");
        }
    }
}
