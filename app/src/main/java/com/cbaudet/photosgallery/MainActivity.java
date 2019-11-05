package com.cbaudet.photosgallery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TouchExample view = new TouchExample(this);
        setContentView(view);

    }
}
