package com.example.mrm.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Floating action button that leads to the camera view for scanning a QR Code
        FloatingActionButton fab = findViewById(R.id.cameraFAB);
        fab.setOnClickListener(view -> Snackbar.make(view, "Button clicked!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
}
