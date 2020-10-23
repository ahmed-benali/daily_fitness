package com.benaliahmed.dailyfitness.run;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.benaliahmed.dailyfitness.R;
import com.benaliahmed.dailyfitness.database.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import java.util.Calendar;
import java.util.HashMap;


public class runFragment extends Fragment {
    public static final int DEFAULT_UPDATE_INTERVAL = 3;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    double latitude, longitude;
    float accuracy;
    TextView tv_distance, tv_calories;
    Chronometer chronometer;
    double distance_setting=0.0002;

    Button stop_btn,restart_btn;
    ToggleButton play_btn;
    String age,weight,height;
    double calories;


    GoogleMap map;
    //Location request: config file
    LocationRequest locationRequest;
    //google API for location
    FusedLocationProviderClient fusedLocationProviderClient;
    //locationcallback
    LocationCallback locationCallBack;

    Location mylocation;
    Marker mymarker;



    @Override
    public void onStart() {
        super.onStart();

        play_btn=getView().findViewById(R.id.play_btn);
        stop_btn=getView().findViewById(R.id.stop_btn);
        restart_btn=getView().findViewById(R.id.reset_btn);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        tv_distance = getView().findViewById(R.id.distance);
        chronometer = getView().findViewById(R.id.chronometer);
        tv_calories = getView().findViewById(R.id.calories);

        final SessionManager sessionManagerStats = new SessionManager(getContext(), SessionManager.SESSION_STATS);
        HashMap<String, String> stats_data = sessionManagerStats.getStatsFromSession();
        age=stats_data.get(SessionManager.KEY_AGE);
        weight=stats_data.get(SessionManager.KEY_WEIGHT);
        height=stats_data.get(SessionManager.KEY_HEIGHT);


        updateGPS();
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {

            map = googleMap;
            getStartingLocation();
            final progressFragment progressfragment = new progressFragment();
            final long[] pauseoffset = new long[1];
            final boolean[] pasue_clicked = {false};
            final boolean[] start = {true};

            play_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (start[0] == true) {
                        start[0] = false;
                        startLocationUpdate();
                        chronometer.setBase(SystemClock.elapsedRealtime() - pauseoffset[0]);
                        chronometer.start();


                    } else if (start[0] == false) {
                        start[0] = true;
                        pasue_clicked[0] = true;
                        stopLocationUpdate();
                        chronometer.stop();
                        pauseoffset[0] =SystemClock.elapsedRealtime()-chronometer.getBase();
                    }
                }
            });
            stop_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean stop_clicked = true;
                    if(!start[0] || pasue_clicked[0]){
                    try {
                        chronometer.stop();
                        stopLocationUpdate();
                        if(!pasue_clicked[0]){
                        pauseoffset[0] =SystemClock.elapsedRealtime()-chronometer.getBase();}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String distance= tv_distance.getText().toString();
                    String duration= String.valueOf(pauseoffset[0]/60000);
                    String date= String.valueOf(Calendar.getInstance().getTime());
                    Intent intent= new Intent(getContext(),MainMenu.class);
                    intent.putExtra("stop_btn",stop_clicked);
                    intent.putExtra("date",date);
                    intent.putExtra("distance",distance);
                    intent.putExtra("duration",duration);
                    startActivity(intent);
                }}
            });
            restart_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    map.clear();
                    play_btn.setChecked(false);
                    try {
                        stopLocationUpdate();
                        chronometer.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment frag= getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    ft.detach(frag).attach(frag).commit();
                }
            });
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), "this app need permission to work properly", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                }

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        //Toast.makeText(getContext(), "stop tracking", Toast.LENGTH_SHORT).show();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "needs location permission", Toast.LENGTH_SHORT).show();
            return;
        }
        final LatLng[] previous_cordoned = {null};
        final double[] distance_sum = {0};
        final double[] previous_distance = {0};
        Toast.makeText(getContext(), "tracking", Toast.LENGTH_SHORT).show();
        map.clear();
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //save location
                Location location = locationResult.getLastLocation();

                mylocation = location;
                //Toast.makeText(getContext(), mylocation.getLatitude()+"\n"+mylocation.getLongitude(), Toast.LENGTH_SHORT).show();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng cordoned = new LatLng(latitude, longitude);
                if (mymarker == null) {
                    mymarker = map.addMarker(new MarkerOptions().position(cordoned).title("you"));
                } else {
                    mymarker.remove();
                    mymarker = map.addMarker(new MarkerOptions().position(cordoned).title("you"));
                    if (previous_cordoned[0] != null) {
                        double distance = SphericalUtil.computeDistanceBetween(cordoned, previous_cordoned[0])/1000;
                        if (distance>distance_setting){
                        distance_sum[0] += distance+ previous_distance[0];
                        previous_distance[0] = distance;
                        tv_distance.setText(String.format("%.2f", distance_sum[0]));
                        calories=(distance_sum[0]) * Integer.parseInt(weight) * 1.036;
                        tv_calories.setText(String.format("%.2f",calories));}
                    }
                    previous_cordoned[0] = cordoned;
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
    }

    private void getStartingLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
/*                    LatLng startpoint = new LatLng(latitude, longitude);
                    CameraPosition newCamPos = new CameraPosition(new LatLng(latitude, longitude),
                            15.5f,
                            map.getCameraPosition().tilt, //use old tilt
                            map.getCameraPosition().bearing); //use old bearing
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);
                    map.addMarker(new MarkerOptions().position(startpoint).title("starting point"));*/
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.start_marker);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap start_img= Bitmap.createScaledBitmap(b,100,100,false);


                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(new LatLng(latitude, longitude))
                                    .zoom(15)
                                    .build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    LatLng startpoint = new LatLng(latitude, longitude);
                    Marker start_marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .anchor(0.5f, 0.5f)
                            .title("STARTING POINT")
                            .snippet("STARTING POINT")
                            .icon(BitmapDescriptorFactory.fromBitmap(start_img)));
                    //map.addMarker(new MarkerOptions().position(startpoint).title("starting point"));
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

    }
}
