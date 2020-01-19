package com.tala.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.tala.ui.add;

public class friends extends AppCompatActivity {
    int i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        //ImageButton addbutton = (ImageButton) findViewById(R.id.add);
        ImageButton subtractbutton = (ImageButton) findViewById(R.id.f1);
        RelativeLayout relfriends = (RelativeLayout) findViewById(R.id.activfriend);
        final ImageButton addfriend = new ImageButton(friends.this);
        RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        System.out.println(i);
        addfriend.setId(i);
        lparams.addRule(RelativeLayout.BELOW, R.id.logo);
        lparams.setMarginStart(800);

        addfriend.setLayoutParams(lparams);
        addfriend.setImageResource(R.drawable.add);
        relfriends.addView(addfriend);
        //ImageButton track1 = (ImageButton) findViewById(R.id.f1);
        //ImageButton track2 = (ImageButton) findViewById(R.id.f2);
        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(friends.this, add.class));
//                RelativeLayout relfriends = (RelativeLayout) findViewById(R.id.activfriend);
//                ImageButton newfriend = new ImageButton(friends.this);
//                RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                newfriend.setId(i);
//                lparams.addRule(RelativeLayout.BELOW, newfriend.getId());
//                newfriend.setLayoutParams(lparams);
//                newfriend.setImageResource(R.drawable.pallete);
//                relfriends.addView(newfriend);
            }
        });
        subtractbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(friends.this, remove.class));
            }
        });
        /*
        track1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(friends.this, tracking.class));
            }
        });
        track2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(friends.this, tracking.class));
            }
        });
        if (submit){
            System.out.println(submit);
            ImageButton newfriend = new ImageButton(friends.this);
            RelativeLayout.LayoutParams r = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            r.addRule(RelativeLayout.BELOW, R.id.subtract);
            newfriend.setLayoutParams(r);
            newfriend.setImageResource(R.drawable.pallete);
            View relLayout = findViewById(R.id.activfriend);
            ((RelativeLayout) relLayout).addView(newfriend);
        }
        */
    }
}

