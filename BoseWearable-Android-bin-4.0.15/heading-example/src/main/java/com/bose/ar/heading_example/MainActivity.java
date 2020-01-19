package com.bose.ar.heading_example;

import android.graphics.Canvas;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bose.wearable.BoseWearable;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    DirectionDial canvas;
    Button dialButton;
    EditText input;
    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check that the minimum required API level is available
        if (Build.VERSION.SDK_INT < BoseWearable.MINIMUM_SUPPORTED_OS_VERSION) {
            Toast.makeText(this, getString(R.string.insufficient_api_level, BoseWearable.MINIMUM_SUPPORTED_OS_VERSION),
                Toast.LENGTH_LONG)
                .show();
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        MainActivity that = this;

        DatabaseConnecter db = new DatabaseConnecter(getApplicationContext());
        dialButton = findViewById(R.id.locationButton);
        dialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               fusedLocationClient = LocationServices.getFusedLocationProviderClient(that);

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(that, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    System.out.println("We did it!");
                                    float clat = (float)location.getLatitude();
                                    float clon = (float)location.getLongitude();
                                    try {
                                        db.login(
                                                "ajwurts",
                                               "password",
                                                new RequestUpdate() {
                                                    @Override
                                                    public void updateAfterRequest(JSONObject obj) {
                                                        System.out.println("Logged in");

                                                        db.setLoc(clat, clon, new RequestUpdate());

                                                    }
                                                });

                                    } catch (Exception e) {

                                    }
                                }
                            }
                        });

            }
        });
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final Bundle args = new Bundle();
        args.putString("username", "user2");
        args.putString("password", "pword");


            HomeFragment fragment = new HomeFragment();
            fragment.setArguments(args);
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

    }
}
