package com.productions.keep.keepitsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class OtherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        Log.d("OtherActivity","started");
    }
}
