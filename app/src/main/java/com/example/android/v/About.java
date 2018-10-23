package com.example.android.v;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent  i = getIntent();
        String s= i.getStringExtra("a");
        if (s.equals("about")) {
            setContentView(R.layout.about);
        }else {
            setContentView(R.layout.use_speak);

        }
    }
}
