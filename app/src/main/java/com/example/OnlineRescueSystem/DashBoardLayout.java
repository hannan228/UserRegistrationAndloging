package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.OnlineRescueSystem.Model.ActiveUser;
import com.example.OnlineRescueSystem.Model.Registration;
import com.example.OnlineRescueSystem.Model.UserRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class DashBoardLayout extends AppCompatActivity implements View.OnClickListener{

    private static final int Request_Call = 1;
    private String rescuerType,rescueType1;
    private FirebaseDatabase database;
    private DatabaseReference mRef,mRef1,myRef3,activeCase,userRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser mUser;
    private static final String TAG = "DashBoardLayout";
    private String driverType,email,subEmail,userEmail;
    private TextView availability,advice;
    private int available = 0;
    private double lat,log;
    private LatLng callerLatLong;
    private ProgressDialog mProgress1;
    private String availableStatus="available";
    private String phoneNumber = "tel:03048146310";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        mProgress1 = new ProgressDialog(DashBoardLayout.this);
        database = FirebaseDatabase.getInstance();
        availability = findViewById(R.id.availablityy);
        advice = findViewById(R.id.advicee);
        Log.d(TAG, "check point 1");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        subEmail = mUser.getEmail();
        subEmail = subEmail.substring(0,subEmail.indexOf("."));
        myRef3 = database.getReference("ActiveDriver").child(subEmail).child("user request");
        activeCase = database.getReference("Active Case").child(subEmail);

        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: my ref " + myRef3);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        ActiveUser activeUser = child.getValue(ActiveUser.class);

                        lat = Double.parseDouble(activeUser.getLat());
                        log = Double.parseDouble(activeUser.getLog());
                        Log.d(TAG, "onDataChange: snapshop4" + lat);// caller info
                        Log.d(TAG, "onDataChange: snapshop4" + log);// caller info

                    }
                } else {
                    Log.d(TAG, "onDataChange: snapshop2" + dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "onCreate: "+subEmail);
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                } else {

                    startActivity(new Intent(DashBoardLayout.this,LoginScreen.class));
                }
            }
        };

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, "token"+token);
                    }
                });

        if(available==1){
            availability.setText("Available as "+driverType);
            advice.setText("Change type of availability by pressing ");
        }

    } // end of onCreate

    public void checkAvailability(){
//        mProgress1.setMessage("please wait... make sure you have internet facility");
//        mProgress1.show();

        Log.d(TAG, "onDataChange: "+mRef);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d(TAG, "check point checkavailability");
                        ActiveUser activeUser = child.getValue(ActiveUser.class);
                        driverType = activeUser.getDriverType();
                        Log.d(TAG, "check point checkavailability driverType"+driverType);
                        email = child.getKey();
                        Log.d(TAG, "onDataChange: ema"+email);
                        //assert email != null;
                        if (subEmail.equals(email)) {
                            available = 1;
                            mRef1 = database.getReference(""+driverType).child(subEmail);
                            rescuerType = driverType;
//                            Log.d(TAG, "onDataChange1: " + email);
                            availability.setText("Available as: "+driverType);
                            advice.setText("change type of availability by pressing ");
                            break;
                        }else
                        {
                            Log.d(TAG, "onDataChange:1 vvvv");
                        }
                        Log.d(TAG, "onDataChange2: " + driverType);
                    }
//                    mProgress1.dismiss();
                    //return driverType
                } else {
                    Toast.makeText(DashBoardLayout.this, "you are not available for any rescue service make yourself available by pressing button", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                Log.d(TAG, "onDataChange:" + databaseError.toException());

            }
        });
        //String subEmail = mUser.getEmail();

    }//end of chkAvailability


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: availableStatus"+availableStatus);
        if (availableStatus.equals("available")){
        switch (v.getId()) {
            case R.id.lifeGuardview:
                open("life guard service");
                break;

            case R.id.AccidentrecoveryCardView:
                open("Accident recovery team");
                break;

            case R.id.fireCardView:
                open("Fire brigade");
                break;

            case R.id.structureCollapseImageAndCardViewID:
                open("Rescue service");
                break;
        }
        }else if (availableStatus.equals("notAvailable")){
            Toast.makeText(DashBoardLayout.this,"You have not completed your job yet",Toast.LENGTH_LONG).show();
        }
    }

    public void open(final String RescuerType){
        Log.d(TAG, "check point open");
        Toast.makeText(DashBoardLayout.this,""+RescuerType,Toast.LENGTH_LONG).show();
        Log.d(TAG, "open: email"+email);
        Log.d(TAG, "open: email"+subEmail);
        if (available==1){
            Log.d(TAG, "check point available");
            Log.d(TAG, "open: email"+subEmail);
            Toast.makeText(DashBoardLayout.this,"already available for service",Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("It would be replace your old availability. Are you sure to change your type of availability");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Log.d(TAG, "open: email "+mRef1.child(""+subEmail));

                            mRef1.removeValue();
                            rescuerType = RescuerType;
                            Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
                            intent.putExtra("rescuer",rescuerType);
                            startActivity(intent);
                            //        DashBoardLayout.this.getSharedPreferences("YOUR_PREFS", 0).edit().clear().commit();
                            //finish();

                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(DashBoardLayout.this, "Press yes to change your type of availability", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }else {
            Log.d(TAG, "check point not available");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Make me available as "+RescuerType);
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            rescuerType = RescuerType;
                            Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
                            intent.putExtra("rescuer",rescuerType);
                            intent.putExtra("availableStatus",availableStatus);
                            startActivity(intent);

                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(DashBoardLayout.this, "Press yes to make you Available", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    public void call(View view){
        makeCall();
    }

    protected void makeCall() {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));

        if (ContextCompat.checkSelfPermission(DashBoardLayout.this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoardLayout.this, new String[]{Manifest.permission.CALL_PHONE},Request_Call); {

            }
        }else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber)));
        }
    }

    public void onMapPress(View view){
        Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
        intent.putExtra("rescuer",rescuerType);
        Log.d(TAG, "onMapPress: "+availableStatus);
        intent.putExtra("availableStatus",availableStatus);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Request_Call){
            if(grantResults.length>0&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
                makeCall();
            }else {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_profile:
                Intent intent = new Intent(DashBoardLayout.this,ProfileActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRef = database.getReference("ActiveDriver");
        mProgress1.setMessage("please wait...");
        mProgress1.show();
        checkAvailability();
        Log.d(TAG, "check point onstart");
        mAuth.addAuthStateListener(firebaseAuthListener);
        activeCase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    availableStatus = "notAvailable";
                    Log.d(TAG, "onDataChange:ava"+dataSnapshot);
                    UserRequest userRequest = dataSnapshot.getValue(UserRequest.class);
                    userEmail = userRequest.getEmail();
                    Log.d(TAG, "onDataChange:user "+userRef);
                    Log.d(TAG, "onDataChang me yha"+dataSnapshot);
                    userRef = database.getReference("Caller Data").child(""+userEmail).child("profile detail").child("wese");

                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Registration registration = dataSnapshot.getValue(Registration.class);
                            phoneNumber = "tel:"+(registration.getPhoneNumber());
                            Log.d(TAG, "onDataChange: hh"+phoneNumber);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mProgress1.dismiss();
                }else {
                    Log.d(TAG, "onDataChange:not");
                    mProgress1.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null){
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DashBoardLayout.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
