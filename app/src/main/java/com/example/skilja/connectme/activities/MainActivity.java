package com.example.skilja.connectme.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.skilja.connectme.R;

import com.facebook.FacebookSdk;

public class MainActivity extends AppCompatActivity   {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
    }

}
