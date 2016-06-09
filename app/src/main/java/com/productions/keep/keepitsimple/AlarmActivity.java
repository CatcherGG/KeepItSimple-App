package com.productions.keep.keepitsimple;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class AlarmActivity extends Activity {
    private ImageButton stopAlarm;
    private ImageButton childNotWithMe;
    private static AlarmActivity inst;
    private static int counter = 0;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Ringtone ringtone;
    private Thread animationThread;
    private boolean runThread = true;

    public static AlarmActivity instance(){
        return inst;
    }

    @Override
    public void onStart() {
        Log.d("AlarmActivity","start");
        super.onStart();
        inst = this;
        counter++;
        if (counter % 2 == 0) {
            stopAlarm.setImageResource(R.drawable.alarm2);
        } else {
            stopAlarm.setImageResource(R.drawable.alarm1);
        }
        startAlarm();
    }

    public void setRingtone(Ringtone rington){
        this.ringtone = rington;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AlarmActivity","onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alarm);
        stopAlarm = (ImageButton)findViewById(R.id.button5);
        childNotWithMe = (ImageButton)findViewById(R.id.button6);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                finish();
            }
        });
        childNotWithMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                Log.d("AlarmActivity","clicked on child not with me");
                Intent intent = new Intent(AlarmActivity.this,WithoutChild.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(ringtone != null) {
            runThread = false;
            ringtone.stop();
        }
        super.onDestroy();
    }

    private void startAlarm(){
        Log.d("AlarmActivity","start alarm");
        try {
            Intent myIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
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
