package com.example.OnlineRescueSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class LoginScreen extends AppCompatActivity {

    private TextView registerTextView;
    private Button registerbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide Action bar and Title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_screen);
        getSupportActionBar().hide();

        registerbutton = (Button) findViewById(R.id.loginButtonID_login);
        registerTextView = (TextView) findViewById(R.id.registerTextViewID_login);

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
