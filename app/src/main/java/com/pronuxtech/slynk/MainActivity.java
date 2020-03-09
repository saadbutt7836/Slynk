package com.pronuxtech.slynk;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    //WIDGETS
    Button signUpBtn, signInBtn;

    //    VALUES
    private static String SENDING_SIGNIN_VALUE = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SENDING_SIGNIN_VALUE = null;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SENDING_SIGNIN_VALUE = "0";
                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SENDING_SIGNIN_VALUE = "1";
                Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                intent.putExtra("SENDING_SIGNIN_VALUE", SENDING_SIGNIN_VALUE);
                startActivity(intent);
            }
        });
    }
}
