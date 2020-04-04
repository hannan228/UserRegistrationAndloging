package com.example.OnlineRescueSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginScreen extends AppCompatActivity {

    private TextView registerTextView;
    private Button registerbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        registerbutton = (Button) findViewById(R.id.registerButtonID);
        registerTextView = (TextView) findViewById(R.id.registerTextViewID);

        ////registerTextView clickListener
        registerTextView.setPaintFlags(registerTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        registerTextView.setText(Html.fromHtml("<u>underlined</u> text"));
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //LoginScreen clickListener
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this,DashBoardLayout.class);
                startActivity(intent);
            }
        });
    }
}
