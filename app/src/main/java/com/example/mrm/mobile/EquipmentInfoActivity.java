package com.example.mrm.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class EquipmentInfoActivity extends AppCompatActivity {
    public static final String EQUIPMENT_INFO_KEY = "equipment_info";

    // TODO: Add button to change machine status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_info);

        // Gets the input data
        String equipmentInfo = getIntent().getStringExtra(EQUIPMENT_INFO_KEY);

        // TODO: Add all relevant info
        // Declare variables for the data we're looking for
        int machineId = -1;
        String machineStatus = "not found";
        String machineProductModel = "not found";

        // Parses it as json
        try {
            JSONObject machineInfoJSON = new JSONObject(equipmentInfo);
            // TODO: Save keys as constants?
            machineId = machineInfoJSON.getInt("id");
            machineStatus = machineInfoJSON.getString("status");
            machineProductModel = machineInfoJSON.getString("productModel");
        } catch (JSONException e) {
            // TODO: Improve error handling
            View mainView = findViewById(R.id.mainLayout);
            Snackbar.make(mainView, "Error parsing machine info", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        // Prepares the output
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("Machine ID: ").append(machineId).append("\n");
        outputBuilder.append("Machine Status: ").append(machineStatus).append("\n");
        outputBuilder.append("Product Model: ").append(machineProductModel).append("\n");

        // Writes it to the main textview
        TextView infoTextView = (TextView) findViewById(R.id.infoTextView);
        infoTextView.setText(outputBuilder);
    }
}
