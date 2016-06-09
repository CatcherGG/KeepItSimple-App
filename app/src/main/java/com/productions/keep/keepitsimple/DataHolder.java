package com.productions.keep.keepitsimple;

import java.util.ArrayList;

/**
 * Created by Guy Gonen on 09/06/2016.
 */
public class DataHolder {
    private String data;
    private boolean shouldAlert = false;
    private ArrayList<Integer> latest_predictions = new ArrayList<Integer>();
    private boolean is_walked = false;
    private boolean is_vechicle = false;

    private long sleep_timer = -1;
    private long timer_start_time = -1;


    public String getData() {return data;}
    public void setData(String data) {this.data = data;}

    public boolean getShouldAlert(){
        return shouldAlert;
    }
    public void setShouldAlert(boolean shouldAlert){
        this.shouldAlert = shouldAlert;
    }
    public boolean getIsWalked(){
        return is_walked;
    }
    public void setIsWalked(boolean is_walked){
        this.is_walked = is_walked;
    }
    public boolean getIsVechicle(){
        return is_vechicle;
    }
    public void setIsVechicle(boolean is_vechicle){
        this.is_vechicle = is_vechicle;
    }

    public ArrayList<Integer> add_prediction(int type_prediction){
        if (!(latest_predictions.size() < Constants.CACHING_SIZE)){
            latest_predictions.remove(0);
        }
        latest_predictions.add(type_prediction);
        return latest_predictions;
    }
    public void clear_predictions(){
        latest_predictions.clear();
    }

    public void setSleepTimer(long sleepTimer, long start_time){
        this.sleep_timer = sleepTimer;
        this.timer_start_time = start_time;
    }

    public boolean is_timer_on(long curr_time){
        if (timer_start_time == -1 || sleep_timer == -1) {
            return false;
        }
        return curr_time - timer_start_time < sleep_timer;
    }

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}

}