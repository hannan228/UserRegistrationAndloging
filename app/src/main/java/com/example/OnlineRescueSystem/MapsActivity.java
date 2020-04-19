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
import com.example.OnlineRescueSystem.Model.LocationInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference myRef;
    private DatabaseReference myRef1;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static final String TAG = "MapsActivity";
    private String rescueType="";
    private TextView mapTitle;

    private TextView estimatedDistanceMap,estimatedTimeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        rescueType = intent.getStringExtra("rescuerType");

        mapTitle = findViewById(R.id.mapTitleID);

        estimatedDistanceMap = findViewById(R.id.distanceLocation);
        estimatedTimeMap = findViewById(R.id.estimatedTimeLocation);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        final String subEmail = mUser.getEmail();


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

//                double lat = location.getLatitude();
//                double log = location.getLongitude();
                double lat = 31.174273;
                double log = 74.086615;

                LatLng currentLocation = new LatLng(lat, log);

                Toast.makeText(MapsActivity.this,""+rescueType,Toast.LENGTH_LONG).show();
                Log.d(TAG, "onLocationChanged: "+rescueType);

                if (rescueType!= null) {

                    if (lat > 20.0 && log > 20.0) {

                        database = FirebaseDatabase.getInstance();
                        myRef1 = database.getReference("ActiveDriver");
                        myRef = database.getReference(""+rescueType);

                        LocationInfo locationInfo = new LocationInfo(""+lat,""+log);
                        ActiveUser activeUser = new ActiveUser(""+lat,""+log,""+rescueType);
                        myRef.child(subEmail.substring(0, subEmail.indexOf("."))).setValue(locationInfo);
                        myRef1.child(subEmail.substring(0, subEmail.indexOf("."))).setValue(activeUser);

                        double lat1 = 31.180263;
                        double log1 = 74.094517;
                        LatLng driverLocation = new LatLng(lat1, log1);

                        //driver marker
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(driverLocation));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLocation, 14));
                        mMap.addMarker(new MarkerOptions().position(driverLocation).title("driver is here"));
                        //driver marker



                        //awais yar ye icon marker lgaya h is ko chota kr mjhe ni ata.
                        //mMap.addMarker(new MarkerOptions().position(driverLocation).title("driver is here")).setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.mark));


                        //user location
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("you are here"));
                        //user marker
                        locationManager.removeUpdates(locationListener);

                        double mi = distance(lat, log, lat1, log1);
                        double km = mi / 0.62137;
                        // double dis = ;
                        mapTitle.setText("Available as: "+rescueType);
                        estimatedDistanceMap.setText(new DecimalFormat("##.####").format(km) + " km");

                        if (km <= 1) {
                            double time1 = km + 1;
                            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
                        } else if (km > 1 || km <= 2) {
                            double time1 = km + 2;
                            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
                        } else if (km > 2 || km <= 4) {
                            double time1 = km + 3;
                            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
                        } else if (km > 4 || km <= 10) {
                            double time1 = km + 5;
                            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
                        } else if (km > 10) {
                            double time1 = km + 6;
                            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
                        }

                        //Log.d(TAG, "not empty ");
                    } else {
                        Log.d(TAG, "acci else ");
                    }
                } else if (lat > 20 && log > 20) {

                    mapTitle.setText("Not available for any service..");

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("you are here"));
                    locationManager.removeUpdates(locationListener);

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
        mMap.clear();
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                try {
                    locationManager.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }


}
