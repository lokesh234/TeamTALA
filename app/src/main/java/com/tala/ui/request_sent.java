package com.tala.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class request_sent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sent);
//        RelativeLayout relfriends = (RelativeLayout) findViewById(R.id.activfriend);
//        ImageButton newfriend = new ImageButton(request_sent.this);
//        RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lparams.addRule(RelativeLayout.BELOW, R.id.subtract);
//        newfriend.setLayoutParams(lparams);
//        newfriend.setImageResource(R.drawable.pallete);
//        relfriends.addView(newfriend);
        new Timer().schedule(new TimerTask() {
                                 @Override
                                 public void run() {
                                     startActivity(new Intent(request_sent.this, addingpeop.class));
                                     finish();
                                 }
                             },
                2000);
    }
}