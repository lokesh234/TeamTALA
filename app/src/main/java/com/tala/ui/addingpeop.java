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

public class addingpeop extends AppCompatActivity {
    int i = 0;
    int u = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addingpeop);
        ImageButton addfriend = (ImageButton)findViewById(R.id.f1);
        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout relfriends = (RelativeLayout) findViewById(R.id.activfriend);
                RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ImageButton newfriend = new ImageButton(addingpeop.this);
                newfriend.setId(i);
                lparams.addRule(RelativeLayout.BELOW, (newfriend.getId() - u));
                                             System.out.println("This is id");
                System.out.println(newfriend.getId());
                newfriend.setLayoutParams(lparams);
                newfriend.setImageResource(R.drawable.pallete);
                relfriends.addView(newfriend);
                i++;
            }
        });
    }
}
