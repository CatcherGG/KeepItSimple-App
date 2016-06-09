package com.productions.keep.keepitsimple;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by Guy Gonen on 09/06/2016.
 */
public class DataHolder {
    private String data;
    private boolean shouldAlert = true;
    private ArrayList<Pair<Integer, Integer>> latest_predictions;
    private boolean is_walked = false;
    private boolean is_vechicle = false;


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

    public void add_prediction(Pair<Integer, Integer> type_prediction){
        if (!(latest_predictions.size() < 5)){
            latest_predictions.remove(0);
        }
        latest_predictions.add(type_prediction);
    }



    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}