/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.productions.keep.keepitsimple;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *  IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";
    IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public DetectedActivitiesIntentService getServiceInstance() {
            return DetectedActivitiesIntentService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    public void stopDetectionForPeriod(long seconds){
        Log.d("DetectedService","stop for "+String.valueOf(seconds)+" seconds");
        long sleep_timer = seconds;
        long curr_time = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        DataHolder.getInstance().setSleepTimer(sleep_timer, curr_time);
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
        long curr_time = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        // Log each activity.
        Log.i(TAG, "activities detected");
        for (DetectedActivity da: detectedActivities) {
            Log.i(TAG, Constants.getActivityString(
                            getApplicationContext(),
                            da.getType()) + " " + da.getConfidence() + "%"
            );
        }

        handle_last_activity(result.getMostProbableActivity());
        if (DataHolder.getInstance().getShouldAlert() && !DataHolder.getInstance().is_timer_on(curr_time)) {
            DataHolder.getInstance().clear_predictions();
            DataHolder.getInstance().setShouldAlert(false);
            DataHolder.getInstance().setIsVechicle(false);

            Intent intent_1 = new Intent(this, AlarmActivity.class);
            intent_1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_1);
        }

        // Broadcast the list of detected activities.
        localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }


    public void handle_last_activity(DetectedActivity latest_most_probable_activity){
        DataHolder holder = DataHolder.getInstance();
        List<Integer> predictions = holder.add_prediction(latest_most_probable_activity.getType());
        Log.i(TAG, "Predictions: "+predictions);
        // If i wasn't in Vechicle.
        if (!holder.getIsVechicle()) {
            int vechicle_count = countVechicle(predictions);
            int count_walking = countWalkingOnFoot(predictions);
            if (vechicle_count > Math.ceil(Constants.CACHING_SIZE / 2)
                    && count_walking < Math.floor(Constants.CACHING_SIZE / 2)){
                Log.i(TAG, "Setting vehicle to true.");
                holder.setIsVechicle(true);
            }
        }

        // If i was in the vehicle,
        if (holder.getIsVechicle()) {
            int vechicle_count = countVechicle(predictions);
            int count_walking = countWalkingOnFoot(predictions);
            if (vechicle_count < Math.floor(Constants.CACHING_SIZE / 2)
                    && count_walking > Math.ceil(Constants.CACHING_SIZE / 2)){
                Log.i(TAG, "Setting vehicle to false.");
                holder.setShouldAlert(true);
                holder.setIsVechicle(false);
                DataHolder.getInstance().clear_predictions();
            }
        }
    }

    private int countVechicle(List<Integer> predictions) {
        int count = 0;
        for (int prediction_type : predictions) {
            if (prediction_type == DetectedActivity.IN_VEHICLE){
                count++;
            }
        }
        return count;
    }

    private int countWalkingOnFoot(List<Integer> predictions) {
        int count = 0;
        for (int prediction_type : predictions) {
            if (prediction_type == DetectedActivity.WALKING || prediction_type == DetectedActivity.ON_FOOT){
                count++;
            }
        }
        return count;
    }
}
