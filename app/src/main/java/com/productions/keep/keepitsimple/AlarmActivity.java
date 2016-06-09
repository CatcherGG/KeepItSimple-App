package com.productions.keep.keepitsimple;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class AlarmActivity extends Activity {
    private ImageButton stopAlarm;
    private ImageButton childNotWithMe;
    private static AlarmActivity inst;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private MediaPlayer mp;
    private Vibrator vibrator;
    private DetectedActivitiesIntentService detectionService;
    private boolean mBounded;
    private AlertDialog dialog;

    public static AlarmActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        Log.d("AlarmActivity", "start");
        super.onStart();
        if(inst==null) {
            Intent mIntent = new Intent(this, DetectedActivitiesIntentService.class);
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
            inst = this;
            if (mp == null || !mp.isPlaying()) {
                startAlarm();
            }
        }
    }

    public void setMediaPlayer(MediaPlayer mp) {
        this.mp = mp;
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
            Log.d("WithoutChild", "disconnected the service");
            //Toast.makeText(WithoutChild.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            detectionService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("WithoutChild", "connected the service");
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

        dialog = createDialog();

        stopAlarm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopAlarm.setImageResource(R.drawable.alarm_nope_shade);
                    Log.d("AlarmActivity", "stop alarm");
                    stopAlarm();
                    finish();
                    // Do what you want
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stopAlarm.setImageResource(R.drawable.alarm_nope_pushed);
                    return true;
                }
                return false;
            }
        });
        childNotWithMe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    childNotWithMe.setImageResource(R.drawable.alarm_mute_shade);
                    Log.d("AlarmActivity", "clicked on child not with me");
                    stopAlarm();
                    dialog.show();
                    // Do what you want
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    childNotWithMe.setImageResource(R.drawable.alarm_mute_pushed);
                    return true;
                }
                return false;
            }
        });
    }

    private AlertDialog createDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.activity_other,
                null, false);
        final ImageButton hour1 = (ImageButton) formElementsView.findViewById(R.id.hour1);
        final ImageButton hour8 = (ImageButton) formElementsView.findViewById(R.id.hour8);
        final ImageButton hour12 = (ImageButton) formElementsView.findViewById(R.id.hour12);
        final ImageButton hour24 = (ImageButton) formElementsView.findViewById(R.id.hour24);
        final ImageButton ops = (ImageButton) formElementsView.findViewById(R.id.ops);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AlarmActivity.this).setView(formElementsView);
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        hour1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hour1.setImageResource(R.drawable.hour1_shade);
                    Log.d("AlarmActivity", "mute for 1 hour");
                    detectionService.stopDetectionForPeriod(60 * 60 * 1);
                    dialog.dismiss();
                    finish();
                    // Do what you want
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hour1.setImageResource(R.drawable.hour1_pushed);
                    return true;
                }
                return false;
            }
        });
        hour8.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hour8.setImageResource(R.drawable.hour8_shade);
                    Log.d("AlarmActivity", "mute for 8 hours");
                    detectionService.stopDetectionForPeriod(60 * 60 * 8);
                    dialog.dismiss();
                    finish();
                    // Do what you want
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hour8.setImageResource(R.drawable.hour8_pushed);
                    return true;
                }
                return false;
            }
        });
        hour12.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hour12.setImageResource(R.drawable.hour12_shade);
                    Log.d("AlarmActivity", "mute for 12 hours");
                    detectionService.stopDetectionForPeriod(60 * 60 * 12);
                    dialog.dismiss();
                    finish();
                    // Do what you want
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hour12.setImageResource(R.drawable.hour12_pushed);
                    return true;
                }
                return false;
            }
        });
        hour24.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hour24.setImageResource(R.drawable.hour24_shade);
                    Log.d("AlarmActivity", "mute for 24 hours");
                    detectionService.stopDetectionForPeriod(60 * 60 * 24);
                    dialog.dismiss();
                    finish();
                    // Do what you want
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hour24.setImageResource(R.drawable.hour24_pushed);
                    return true;
                }
                return false;
            }
        });
        ops.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ops.setImageResource(R.drawable.back_shade);
                    dialog.dismiss();
                    finish();
                    // Do what you want
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ops.setImageResource(R.drawable.back_pushed);
                    return true;
                }
                return false;
            }
        });

        return dialogBuilder.create();
    }

    @Override
    protected void onDestroy() {
        if (mp != null) {
            mp.stop();
            mp = null;
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
            if(mp.isPlaying()) {
                mp.stop();
            }
            vibrator.cancel();

        } catch (Exception e) {
            Log.e("AlarmActivity", "failed to close the alarm");
        }
    }

}
