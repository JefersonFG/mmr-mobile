package com.example.mrm.mobile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

// TODO: Error handling strings must be translated as well

public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<Intent> mIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    String QRCode = data.getStringExtra("QRCode");
                    launchBackendConnectionWorker(QRCode);
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

        // Search button for getting data for a machine with ID typed into the main editor
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(view -> {
            // Get the typed in machine code, validate and start backend connection process
            // TODO: Machine code is a better name once we got the actual data on the QR Code
            EditText editTextQRCode = findViewById(R.id.editTextQRCode);
            String machineCode = editTextQRCode.getText().toString();
            if (!machineCode.isEmpty() && android.text.TextUtils.isDigitsOnly(machineCode)) {
                launchBackendConnectionWorker(machineCode);
            } else {
                View mainView = findViewById(R.id.mainLayout);
                Snackbar.make(mainView, "Machine code must be non null and number based", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    private void launchBackendConnectionWorker(String QRCode) {
        View mainView = findViewById(R.id.mainLayout);
        Snackbar.make(mainView, "QRCode obtained: " + QRCode, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        // Show progress bar indicating that a connection to the backend is in progress
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView textView = findViewById(R.id.progressText);
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);

        // Send QRCode data to the worker object
        Data inputData = new Data.Builder()
                .putString(BackendConnectionWorker.INPUT_QRCODE_KEY, QRCode)
                .build();

        WorkRequest backendConnectionRequest = new OneTimeWorkRequest
                .Builder(BackendConnectionWorker.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(this).enqueue(backendConnectionRequest);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(backendConnectionRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo.getState().isFinished()) {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            String resultMessage = workInfo.getOutputData()
                                    .getString(BackendConnectionWorker.RESPONSE_KEY);
                            Snackbar.make(mainView, resultMessage, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            String error = workInfo.getOutputData()
                                    .getString(BackendConnectionWorker.ERROR_MESSAGE_KEY);
                            Snackbar.make(mainView, error, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                    }
                });
    }
}
