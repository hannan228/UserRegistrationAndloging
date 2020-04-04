package com.example.OnlineRescueSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //splash screen code
        Thread th = new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception ex) {

                    ex.printStackTrace();
                } finally {

                    Intent intent = new Intent(MainActivity.this,LoginScreen.class);
                    startActivity(intent);
                }
            }};th.start();
    }

}
