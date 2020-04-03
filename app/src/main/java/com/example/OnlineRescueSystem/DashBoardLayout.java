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
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class DashBoardLayout extends AppCompatActivity implements View.OnClickListener{

   // private CardView accidentCardView, fireCardView, medicalCardView, crimeCardView, drowningCardView, structureCollapseCardView;
    private static final int Request_Call = 1;
    private String accidentType = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

//        accidentCardView = findViewById(R.id.accidentImageAndLableCardViewID);
//        fireCardView = findViewById(R.id.fireImageAndLableCardViewID);
//        medicalCardView = findViewById(R.id.medicalImageAndLableCardViewID);
//        crimeCardView = findViewById(R.id.crimeImageAndLableCardViewID);
//        drowningCardView = findViewById(R.id.drowningImageAndLableCardViewID);
//        structureCollapseCardView = findViewById(R.id.structureCollapseImageAndLableCardViewID);
//
//
//        accidentCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DashBoardLayout.this, "Accident", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        fireCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DashBoardLayout.this, "Fire", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        medicalCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DashBoardLayout.this, "Medical", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        crimeCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DashBoardLayout.this, "Crime", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        drowningCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DashBoardLayout.this, "Drowning", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        structureCollapseCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DashBoardLayout.this, "Structure COllapse", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accidentImageAndLableCardViewID:
                open("RoadAccident case");
                break;

            case R.id.fireImageAndLableCardViewID:
                open("Fire case");
                break;
            case R.id.medicalImageAndLableCardViewID:
                open("Medical case ");
                break;

            case R.id.crimeImageAndLableCardViewID:
                open("Crime case");
                break;

            case R.id.drowningImageAndLableCardViewID:
                open("drowning case");
                break;

            case (R.id.structureCollapseImageAndLableCardViewID):
                open("Building collapse case");
                break;

        }
    }


    public void open(String type) {
        accidentType = type;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make a call to ::Rescue 1122::");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        makeCall();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DashBoardLayout.this, "You clicked No button", Toast.LENGTH_SHORT).show();
                Toast.makeText(DashBoardLayout.this, " Press Yes on notification if you need 1122", Toast.LENGTH_LONG).show();


                 finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void makeCall() {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:9875432100"));

        if (ContextCompat.checkSelfPermission(DashBoardLayout.this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoardLayout.this, new String[]{Manifest.permission.CALL_PHONE},Request_Call); {

            }
        }else {

            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:03451012867")));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Request_Call){
            if(grantResults.length>0&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
                makeCall();
            }else {
                Toast.makeText(this,"Permison denied",Toast.LENGTH_SHORT);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
