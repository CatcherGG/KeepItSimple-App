package com.productions.keep.keepitsimple;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

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
    private Vibrator vibrator;
    private DetectedActivitiesIntentService detectionService;
    private boolean mBounded;

    public static AlarmActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        Log.d("AlarmActivity", "start");
        super.onStart();
        Intent mIntent = new Intent(this, DetectedActivitiesIntentService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        inst = this;
        if (ringtone==null|| !ringtone.isPlaying()) {
            counter++;
            if (counter % 2 == 0) {
                stopAlarm.setImageResource(R.drawable.alarm2);
            } else {
                stopAlarm.setImageResource(R.drawable.alarm1);
            }
            startAlarm();
        }
    }

    public void setRingtone(Ringtone rington) {
        this.ringtone = rington;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
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
            DetectedActivitiesIntentService.LocalBinder mLocalBinder = (DetectedActivitiesIntentService.LocalBinder) service;
            detectionService = mLocalBinder.getServiceInstance();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AlarmActivity", "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alarm);
        stopAlarm = (ImageButton) findViewById(R.id.button5);
        childNotWithMe = (ImageButton) findViewById(R.id.button6);
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
                Log.d("AlarmActivity", "clicked on child not with me");
                String names[] = {"1 Hour", "8 Hours", "12 Hours", "24 Hours"};

                final CharSequence[] items = {"1 Hour", "8 Hours", "12 Hours", "24 Hours","Ops..."};

                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this,R.style.AboutDialog);
                builder.setTitle("Mute for:");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        Log.d("AlarmActivity","choose "+items[item].toString());
                        long seconds = -1;
                        switch (item){
                            case 0:
                                seconds = 60*60;
                                break;
                            case 1:
                                seconds = 60*60*8;
                                break;
                            case 2:
                                seconds = 60*60*12;
                                break;
                            case 3:
                                seconds = 60*60*24;
                                break;
                        }
                        detectionService.stopDetectionForPeriod(seconds);
                        finish();
                    }
                });
                builder.show();


                /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(AlarmActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.custom, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("Mute for:");
                ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AlarmActivity.this, android.R.layout.simple_list_item_1, names);
                lv.setAdapter(adapter);
                alertDialog.show();*/
                //Intent intent = new Intent(AlarmActivity.this,WithoutChild.class);
                //startActivity(intent);
                //finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (ringtone != null) {
            runThread = false;
            ringtone.stop();
            ringtone = null;
            vibrator = null;
        }
        super.onDestroy();
    }

    public void setVibrator(Vibrator vibrator) {
        this.vibrator = vibrator;
    }

    private void startAlarm() {
        Log.d("AlarmActivity", "start alarm");
        try {
            Intent myIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        } catch (Exception e) {
            Log.e("AlarmActivity", "failed to start alarm");
            Log.e("AlarmActivity", e.toString());
        }
    }

    private void stopAlarm() {
        Log.d("AlarmActivity", "clicked on stop the alarm");
        try {
            alarmManager.cancel(pendingIntent);
            ringtone.stop();
            vibrator.cancel();

        } catch (Exception e) {
            Log.e("AlarmActivity", "failed to close the alarm");
        }
    }

}
