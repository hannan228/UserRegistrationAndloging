package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

    private View leftLowerViewForMap;
    private static final int Request_Call = 1;
    private String rescuerType = null ;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser mUser;
    private static final String TAG = "DashBoardLayout";
    private String driverType,email,subEmail;
    private TextView availability,advice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ActiveDriver");
        checkAvailability();
        availability = findViewById(R.id.availablityy);
        advice = findViewById(R.id.advicee);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        subEmail = mUser.getEmail();
        subEmail = subEmail.substring(0,subEmail.indexOf("."));
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
                        Log.d(TAG, "msg"+token);
                        Toast.makeText(DashBoardLayout.this, "recieved", Toast.LENGTH_SHORT).show();
                    }
                });

        if(subEmail.equals(email)){
            availability.setText("Available as "+rescuerType);
            advice.setText("change type of availability by pressing ");
        }

    } // end of onCreate

    public void checkAvailability(){
        Log.d(TAG, "onDataChange:ddsdjsnd ");
        Log.d(TAG, "onDataChange:ref"+myRef);

//        final String subEmail = mUser.getEmail();
//        subEmail.substring(0, subEmail.indexOf("."));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
                if (dataSnapshot.exists()) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        ActiveUser activeUser = child.getValue(ActiveUser.class);
                        driverType = activeUser.getDriverType();
                        email = child.getKey();
                        Log.d(TAG, "onDataChange: ema"+email);
                        //assert email != null;
                        if (subEmail.equals(email)) {
                            Log.d(TAG, "onDataChange1: " + email);
                            availability.setText("Available as: "+driverType);
                            advice.setText("change type of availability by pressing ");
                            break;
                        }else
                        {
                            Log.d(TAG, "onDataChange:1 vvvv");
                        }
                        Log.d(TAG, "onDataChange2: " + driverType);
                    }

                    //return driverType

                } else {
                    Toast.makeText(DashBoardLayout.this, "Try later!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                Log.d(TAG, "onDataChange:" + databaseError.toException());

            }
        });
        //String subEmail = mUser.getEmail();

    }


    @Override
    public void onClick(View v) {
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
    }

    public void open(final String RescuerType){
        Toast.makeText(DashBoardLayout.this,""+RescuerType,Toast.LENGTH_LONG).show();
        if (email.equals(subEmail)){
            Toast.makeText(DashBoardLayout.this,"already available for service",Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("It would be replace your old availability. Are you sure to change your type of availability");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            rescuerType = RescuerType;
                            Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
                            intent.putExtra("rescuerType",rescuerType);
                            startActivity(intent);
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Make me available as "+RescuerType);
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            rescuerType = RescuerType;
                            Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
                            intent.putExtra("rescuerType",rescuerType);
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
        String caller = "03451012867";
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:03451012867"));

        if (ContextCompat.checkSelfPermission(DashBoardLayout.this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoardLayout.this, new String[]{Manifest.permission.CALL_PHONE},Request_Call); {

            }
        }else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:03451012867")));
        }
    }

    public void onMapPress(View view){
        Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
        intent.putExtra("rescuerType",rescuerType);
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
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null){
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

}
