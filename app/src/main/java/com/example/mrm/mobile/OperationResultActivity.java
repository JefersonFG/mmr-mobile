package com.example.mrm.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OperationResultActivity extends AppCompatActivity {
    public static final String RESULT_KEY = "result";
    public static final String MESSAGE_KEY = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_result);

        // Gets the input data
        boolean result = getIntent().getBooleanExtra(RESULT_KEY, false);

        ImageView indicatorImage = findViewById(R.id.indicatorIcon);
        TextView indicatorText = findViewById(R.id.indicatorText);

        if (result) {
            // Set success image and text
            indicatorImage.setImageResource(R.drawable.ic_success);
            indicatorText.setText(getResources().getString(R.string.operationResultTextSuccess));
        } else {
            // Set failure image and text, gets details and set detail text
            indicatorImage.setImageResource(R.drawable.ic_failure);
            indicatorText.setText(getResources().getString(R.string.operationResultTextFailure));

            // Gets error message
            String errorMessage = getIntent().getStringExtra(MESSAGE_KEY);

            TextView indicatorDetails = findViewById(R.id.indicatorDetails);
            indicatorDetails.setText(errorMessage);
        }

        // Sets the button click to finish this activity
        Button operationResultAcknowledgeButton = findViewById(R.id.operationResultAcknowledgeButton);
        operationResultAcknowledgeButton.setOnClickListener(view -> finish());
    }
}