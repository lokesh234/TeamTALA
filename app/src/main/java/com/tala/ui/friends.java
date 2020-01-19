package com.tala.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class friends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ImageButton addbutton = (ImageButton) findViewById(R.id.loginbutton);
        ImageButton subtractbutton = (ImageButton) findViewById(R.id.signup);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(friends.this, add.class));
            }
        });
        subtractbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(friends.this, remove.class));
            }
        });
    }
}

