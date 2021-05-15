package com.example.mrm.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;

public class EquipmentInfoActivity extends AppCompatActivity
        implements RegisterMachineEventDialogFragment.NoticeDialogListener {
    public static String TAG = "equipment_info_activity";

    public static final String MACHINE_CODE = "machine_code";
    public static final String MACHINE_UPDATE_INFO = "machine_update_info";

    public static final String EQUIPMENT_INFO_KEY = "equipment_info";
    public static final String FALLBACK_STRING = "unavailable info";

    private String mMachineCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_info);

        // Gets the input data
        String equipmentInfo = getIntent().getStringExtra(EQUIPMENT_INFO_KEY);

        // Declare variables for the data we're looking for
        // All declared as strings for the fallback message
        String id;
        String name;
        String type;
        String power;
        String brand;
        String model;
        // TODO: Translate status from backend
        String status;
        // TODO: Translate boolean values when showing on the UI
        String needsMaintenance;
        String comment;

        // TODO: Add pressure, throughput, voltage, year and serial number

        // Parses it as json
        try {
            JSONObject machineInfoJSON = new JSONObject(equipmentInfo);
            // TODO: Save keys as constants?
            id = machineInfoJSON.optString("id", FALLBACK_STRING);
            name = machineInfoJSON.optString("name", FALLBACK_STRING);
            type = machineInfoJSON.optString("type", FALLBACK_STRING);
            power = machineInfoJSON.optString("power", FALLBACK_STRING);
            brand = machineInfoJSON.optString("brand", FALLBACK_STRING);
            model = machineInfoJSON.optString("model", FALLBACK_STRING);
            status = machineInfoJSON.optString("status", FALLBACK_STRING);
            needsMaintenance = machineInfoJSON.optString("needsMaintenance", FALLBACK_STRING);
            comment = machineInfoJSON.optString("comment", FALLBACK_STRING);
        } catch (JSONException e) {
            // TODO: Improve error handling
            View layout = findViewById(R.id.equipmentInfoLayout);
            Snackbar.make(layout, "Error parsing machine info", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }

        // Saves the machine ID
        mMachineCode = id;

        // Prepares the output
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append(getResources().getString(R.string.machineID)).append(": ").append(id).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineName)).append(": ").append(name).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineType)).append(": ").append(type).append("\n");
        outputBuilder.append(getResources().getString(R.string.machinePower)).append(": ").append(power).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineBrand)).append(": ").append(brand).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineModel)).append(": ").append(model).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineStatus)).append(": ").append(status).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineMaintenanceNeeded)).append("? ").append(needsMaintenance).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineComment)).append(": ").append(comment).append("\n");

        // Writes it to the main textview
        TextView infoTextView = findViewById(R.id.infoTextView);
        infoTextView.setText(outputBuilder);

        // Button for registering an event that changes the machine status, like arrival or departure
        FloatingActionButton fab = findViewById(R.id.registerEventFAB);
        fab.setOnClickListener(view -> {
            // Show dialog in full screen
            RegisterMachineEventDialogFragment.display(getSupportFragmentManager());
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Get user input
        RegisterMachineEventDialogFragment dialogFragment = (RegisterMachineEventDialogFragment) dialog;
        MachineEventsEnum selectedOption = dialogFragment.SelectedEventOption;
        boolean flag = dialogFragment.MaintenanceFlagChecked;
        String comment = dialogFragment.Comment;

        // Generate update JSON
        // TODO: Handle error building the JSON object
        String updateJSON = buildUpdateJSON(selectedOption, flag, comment);

        // Send back to the main activity to connect to the backend and show the progress
        Intent data = new Intent();
        data.putExtra(MACHINE_CODE, mMachineCode);
        data.putExtra(MACHINE_UPDATE_INFO, updateJSON);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}

    private String buildUpdateJSON(MachineEventsEnum event, boolean needsMaintenance, String comment) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        try {
            jsonWriter.beginObject();

            // TODO: Constants for json keys
            jsonWriter.name("status").value(event.toString());
            jsonWriter.name("needsMaintenance").value(needsMaintenance);
            jsonWriter.name("comment").value(comment);

            jsonWriter.endObject();
        } catch (Exception e) {
            Log.d(TAG, "buildUpdateJSON: error building JSON: " + e.getMessage());
            return "";
        }

        // Return result
        String updateJSON = stringWriter.toString();

        try {
            stringWriter.close();
            jsonWriter.close();
        } catch (Exception e) {
            Log.d(TAG, "buildUpdateJSON: error closing writers: " + e.getMessage());
        }

        return updateJSON;
    }
}
