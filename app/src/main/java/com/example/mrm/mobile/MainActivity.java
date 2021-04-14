package com.example.mrm.mobile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> mIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    String QRCode = data.getStringExtra("QRCode");
                    View mainView = findViewById(R.id.mainLayout);
                    Snackbar.make(mainView, "QRCode obtained: " + QRCode, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    TextView textView = findViewById(R.id.progressText);

                    progressBar.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<String> mRequestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent intent = new Intent(this, CameraActivity.class);
                    mIntentLauncher.launch(intent);
                } else {
                    // TODO: Improve handling when permission is denied, give user a second chance to grant the permission
                    View mainView = findViewById(R.id.mainLayout);
                    Snackbar.make(mainView, "Permission denied, not opening the camera view", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Floating action button that leads to the camera view for scanning a QR Code
        FloatingActionButton fab = findViewById(R.id.cameraFAB);
        fab.setOnClickListener(view -> {
            // Request camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, CameraActivity.class);
                mIntentLauncher.launch(intent);
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // TODO: Improve this UI, inform the user that the camera permission is important and ask again
                mRequestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                mRequestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }
}
