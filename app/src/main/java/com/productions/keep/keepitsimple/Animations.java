package com.productions.keep.keepitsimple;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.util.Log;

public class Animations extends AppCompatActivity {
    private RelativeLayout layout;
    private Button mainButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animations);
        layout = (RelativeLayout)findViewById(R.id.layout1);
        mainButton = (Button)findViewById(R.id.button);
        showMainButton();
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST","clicked");
                moveMainButton();
            }
        });
    }

    private void showMainButton(){
        TransitionManager.beginDelayedTransition(layout);
        final ViewGroup.LayoutParams originalParams = mainButton.getLayoutParams();
        Log.d("showMainButton","height: "+originalParams.height+" weight:"+originalParams.width);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.width = 50;
        layoutParams.height = 50;
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        mainButton.setLayoutParams(layoutParams);
    }

    private void moveMainButton(){
        TransitionManager.beginDelayedTransition(layout);
        final ViewGroup.LayoutParams originalParams = mainButton.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        layoutParams.setMargins(0,150,0,0);
        mainButton.setLayoutParams(layoutParams);
    }
}
