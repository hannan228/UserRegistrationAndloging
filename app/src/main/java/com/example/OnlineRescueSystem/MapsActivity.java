package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.OnlineRescueSystem.Model.ActiveUser;
import com.example.OnlineRescueSystem.Model.DriverType;
import com.example.OnlineRescueSystem.Model.LocationInfo;
import com.example.OnlineRescueSystem.Model.UserRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference myRef;
    private DatabaseReference myRef1,myRef2,myRef3,activeCase,activeCaseDriver;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String subEmail;
    private String userEmail;
    private static final String TAG = "MapsActivity";
    private static String driverType;
    private TextView mapTitle;
    private double lat,log,latitude,longitude;
    private TextView estimatedDistanceMap,estimatedTimeMap;
    private String availableStatus="available";
    private int i,j = 0;
    private LocationInfo locationInfo;
    private ActiveUser activeUser;
    private DriverType driverType1;
    private LatLng callerLatLong;
    private double km = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        driverType = intent.getStringExtra("rescuer");
        availableStatus = intent.getStringExtra("availableStatus");
        //Log.d(TAG, "check point intent 2 "+driverType1);
        database = FirebaseDatabase.getInstance();

        mapTitle = findViewById(R.id.mapTitleID);
        estimatedDistanceMap = findViewById(R.id.distanceLocation);
        estimatedTimeMap = findViewById(R.id.estimatedTimeLocation);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        subEmail = mUser.getEmail();
        subEmail = subEmail.substring(0, subEmail.indexOf("."));
        myRef3 = database.getReference("ActiveDriver").child(subEmail).child("user request");
        activeCase = database.getReference("Active Case").child(subEmail);

        mapTitle.setText("Available: "+driverType);
        Log.d(TAG, "onCreate: .com"+subEmail);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat = location.getLatitude();
                log = location.getLongitude();

                if (driverType!= null) {
                    if (lat > 20.0 && log > 20.0) {
                        LatLng driverLocation = new LatLng(lat, log);

                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(driverLocation).title("driver is here"));

                        Log.d(TAG, "onLocationChanged: 2"+driverType);
                        Log.d(TAG, "onLocationChanged:3 "+availableStatus);


                        if (availableStatus.equals("available")){
                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        ActiveUser activeUser = child.getValue(ActiveUser.class);

                                        callerLatLong = new LatLng(Double.parseDouble(activeUser.getLat()), Double.parseDouble(activeUser.getLog()));
                                        mMap.addMarker(new MarkerOptions().position(callerLatLong).title("caller is here"));
                                        availableStatus = "notAvailable";
                                        userEmail = child.getKey();
                                        UserRequest userRequest = new UserRequest("" + lat, "" + log, "" + (child.getKey()));
                                        activeCase.setValue(userRequest);

                                        Log.d(TAG, "onDataChange: snapshop4" + child.getKey());// caller info
                                        Log.d(TAG, "onDataChange: snapshop4" + callerLatLong);// caller info
                                    }
                                } else {
                                    Log.d(TAG, "onDataChange: here" + dataSnapshot);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            } });
                    }else if (availableStatus.equals("notAvailable")){  // is ko 1 dafa e chla den sai trh attw kafi h
                            activeCase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        availableStatus = "notAvailable";
                                        UserRequest userRequest = dataSnapshot.getValue(UserRequest.class);
                                        userEmail = userRequest.getEmail();
                                        activeCaseDriver = database.getReference("Active Case").child(""+userEmail);
                                        activeCaseDriver.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()) {
                                                    Log.d(TAG, "onLocationChanged: email"+dataSnapshot.getKey());
                                                    Log.d(TAG, "onLocationChanged:user "+userEmail);
                                                    UserRequest userRequest = dataSnapshot.getValue(UserRequest.class);
                                                    latitude = Double.parseDouble(userRequest.getLat());
                                                    longitude = Double.parseDouble(userRequest.getLog());

                                                    LatLng callerLtLng = new LatLng(latitude,longitude);
                                                    mMap.addMarker(new MarkerOptions().position(callerLtLng).title("caller is here2"));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        }); } }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }else {

                        }
                        i= i+1;



                        if (userEmail!=null && lat!=0.0 && log != 0.0 && latitude!=0.0 && longitude != 0.0){
                            UserRequest userRequest = new UserRequest(""+lat,""+log,""+userEmail);
                            activeCase.setValue(userRequest);

                            double dis = distance(lat, log, latitude, longitude);
                            km = dis / 0.62137;
                            estimatedDistanceMap.setText(""+km+" km");


                        }


                        callerLatLong = new LatLng(latitude,longitude);
                        mMap.addMarker(new MarkerOptions().position(callerLatLong).title("caller is here2"));

                        locationInfo = new LocationInfo(""+lat,""+log);
                        // LocationInfo locationInfo1 = new LocationInfo();
                        //locationInfo1.setLat(""+lat);
                        Log.d(TAG, "check point maps activity data 1"+driverType);

                        driverType1 = new DriverType(""+lat,""+log,""+driverType);
                        activeUser = new ActiveUser(""+lat,""+log,""+driverType);

                        Log.d(TAG, "check point maps activity data 2"+activeUser.getDriverType());


                        if (i==1) {
                            myRef1.setValue(activeUser);
                            myRef.setValue(locationInfo);
                        }
                        myRef2.setValue(driverType1);

                    } else {
                        Log.d(TAG, "acci else ");
                    }
                } else if (lat > 20 && log > 20) {
                    LatLng driverLocation = new LatLng(lat, log);
                    mapTitle.setText("Not available for any service..");
                    Toast.makeText(MapsActivity.this,"here aggi",Toast.LENGTH_LONG).show();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(driverLocation));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLocation, 16));
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(driverLocation).title("you are here"));

                    // Log.d(TAG, "empty ");
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            mMap.setMyLocationEnabled(true);
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng newLocation = new LatLng(31.177167,74.105169);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 14));
        mMap.addMarker(new MarkerOptions().position(newLocation).title("you are here"));

        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                try {
                    locationManager.wait(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        myRef1 = database.getReference("ActiveDriver").child(subEmail);
        myRef = database.getReference(""+driverType).child(subEmail);
        myRef2 = database.getReference(driverType+"fuck").child(subEmail);

    }
}
