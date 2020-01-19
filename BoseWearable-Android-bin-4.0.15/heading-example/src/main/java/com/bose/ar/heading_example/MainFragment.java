package com.bose.ar.heading_example;//
//  MainFragment.java
//  BoseWearable
//
//  Created by Tambet Ingo on 02/19/2019.
//  Copyright © 2019 Bose Corporation. All rights reserved.
//

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bose.blecore.DeviceException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.Executor;

public class MainFragment extends Fragment {
    public static final String ARG_DEVICE_ADDRESS = "device-address";
    public static final String ARG_USE_SIMULATED_DEVICE = "use-simulated-device";
    private static final String TAG = MainFragment.class.getSimpleName();

    float angle = 0.0f;
    float previousZone = 0;
    float clat, clon;
    DatabaseConnecter db;
    private String mDeviceAddress;
    private boolean mUseSimulatedDevice;
    @SuppressWarnings("PMD.SingularField") // Need to keep a reference to it so it does not get GC'd
    private MainViewModel mViewModel;
    private View mParentView;
    @Nullable
    private ProgressBar mProgressBar;
    private SwitchCompat mTrueNorthSwitch;
    private TextView mHeadingText;
    private TextView mAccuracyText;
    @Nullable
    private Snackbar mSnackBar;

    private MediaPlayer mp;
    private Integer musicOn = 0; //Both:0 Left:1 Right:2

    private FusedLocationProviderClient fusedLocationClient;

    public static float calcAngle(float lat1, float lon1, float lat2, float lon2) {
        return  (float)Math.toDegrees(Math.atan2(lat1 - lat2, lon1 - lon2));
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.out.println("We did it!");
                            clat = (float)location.getLatitude();
                            clon = (float)location.getLongitude();
                        }
                    }
                });

        angle = 100;
        db = new DatabaseConnecter(getContext());
        try {
            db.login(
                    args.getString("username", "username"),
                    args.getString("password", "password"),
                    new RequestUpdate() {
                        @Override
                        public void updateAfterRequest(JSONObject obj) {
                            System.out.println("Logged in");

                            db.track(1, new RequestUpdate() {
                                @Override
                                public void updateAfterRequest(JSONObject obj) {
                                    try {
                                        System.out.println("Got Tracking Data");
                                        JSONArray latlon = obj.getJSONArray("data");
                                        float lat = (float)latlon.getDouble(0);
                                        float lon = (float)latlon.getDouble(1);
                                        System.out.println(lat);
                                        System.out.println(lon);
                                        System.out.println("Current Lat: " + clat);
                                        System.out.println("Current Lon: " + clon);
                                        angle = MainFragment.calcAngle(clat, clon, lat, lon);

                                        System.out.println("Angle: " + angle);

                                    } catch (Exception e) {

                                    }


                                }
                            });

                        }
                    });

        } catch (Exception e) {

        }


        mp = new MediaPlayer();

        if (args != null) {
            mDeviceAddress = args.getString(ARG_DEVICE_ADDRESS);
            mUseSimulatedDevice = args.getBoolean(ARG_USE_SIMULATED_DEVICE, false);
        }

        if (mDeviceAddress == null && !mUseSimulatedDevice) {
            throw new IllegalArgumentException();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mParentView = view.findViewById(R.id.container);

        mTrueNorthSwitch = view.findViewById(R.id.trueNorthSwitch);
        mHeadingText = view.findViewById(R.id.headingText);
        mAccuracyText = view.findViewById(R.id.accuracyText);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = requireActivity();
        mProgressBar = activity.findViewById(R.id.progressbar);

        mViewModel = ViewModelProviders.of(this)
            .get(MainViewModel.class);

        mViewModel.busy()
            .observe(this, this::onBusy);

        mViewModel.errors()
            .observe(this, this::onError);

        mViewModel.sensorsSuspended()
            .observe(this, this::onSensorsSuspended);

        mViewModel.heading()
            .observe(this, this::onHeadingUpdated);

        mViewModel.heading()
                .observe(this, this::playSong);

        mViewModel.accuracy()
            .observe(this, this::onAccuracyUpdated);

        mTrueNorthSwitch.setChecked(mViewModel.useTrueNorth());
        mTrueNorthSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mViewModel.useTrueNorth(isChecked);
        });

        if (mDeviceAddress != null) {
            mViewModel.selectDevice(mDeviceAddress);
        } else if (mUseSimulatedDevice) {
            mViewModel.selectSimulatedDevice();
        }
    }

    @Override
    public void onDestroy() {
        onBusy(false);

        final Snackbar snackbar = mSnackBar;
        mSnackBar = null;
        if (snackbar != null) {
            snackbar.dismiss();
        }

        super.onDestroy();
    }

    private void onBusy(final boolean isBusy) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(isBusy ? View.VISIBLE : View.INVISIBLE);
        }

        final Activity activity = getActivity();
        final Window window = activity != null ? activity.getWindow() : null;
        if (window != null) {
            if (isBusy) {
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }

    private void onError(@NonNull final Event<DeviceException> event) {
        final DeviceException deviceException = event.get();
        if (deviceException != null) {
            showError(deviceException.getMessage());
            getFragmentManager().popBackStack();
        }
    }

    private void onSensorsSuspended(final boolean isSuspended) {
        final Snackbar snackbar;
        if (isSuspended) {
            snackbar = Snackbar.make(mParentView, R.string.sensors_suspended,
                Snackbar.LENGTH_INDEFINITE);
        } else if (mSnackBar != null) {
            snackbar = Snackbar.make(mParentView, R.string.sensors_resumed,
                Snackbar.LENGTH_SHORT);
        } else {
            snackbar = null;
        }

        if (snackbar != null) {
            snackbar.show();
        }

        mSnackBar = snackbar;
    }

    private void onHeadingUpdated(final double heading) {
        mHeadingText.setText(formatAngle((heading + 720) % 360));
    }

    private void onAccuracyUpdated(final double accuracy) {
        mAccuracyText.setText(formatAngle(accuracy));
    }

    private void showError(final String message) {
        final Context context = getContext();
        if (context != null) {
            final Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Log.e(TAG, "Device error: " + message);
        }
    }

    private String formatAngle(final double angle) {
        return String.format(Locale.US, "%.2f°", angle);
    }

    private void playSong(final double heading) {
        // angle to target
//        System.out.println(heading);
//        System.out.println(angle);
        // angle = 100;


//        System.out.println(angle);
//        System.out.println(heading);
        double new_heading = (heading + 540);
        double new_angle = (angle + 540);

        double opposite = 180 + new_angle;
        if (new_angle > 450) {
            opposite -= 180;
        }


        System.out.println("Heading: " + new_heading + " angle: " + new_angle + " opposite: " + opposite);


        // Given an angle add 180
        // If heading is less angle + 180 turn left else turn right
//        new_heading - new_angle < 10 || new_angle + 360 - new_heading < 10
        if ( Math.abs(new_angle - new_heading) < 10 || 360 - Math.abs(new_angle - new_heading) < 10) {
            if (musicOn != 0) {

                musicOn = 0;
                mp.reset();
                mp = MediaPlayer.create(getContext(), R.raw.both);
                mp.start();
            }
            System.out.println("close enough");

        } else if (new_heading > opposite){
            if (musicOn != 1){
                musicOn = 1;
                mp.reset();
                mp = MediaPlayer.create(getContext(), R.raw.left);
                mp.start();
            }
            System.out.println("greater"); //right

        } else {
            if (musicOn != 2) {
                musicOn = 2;
                mp.reset();
                mp = MediaPlayer.create(getContext(), R.raw.right);
                mp.start();
            }
            System.out.println("smaller"); //left

        }
    }

}
