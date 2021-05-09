package com.example.mrm.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class EquipmentInfoActivity extends AppCompatActivity
        implements RegisterMachineEventDialogFragment.NoticeDialogListener {
    public static final String EQUIPMENT_INFO_KEY = "equipment_info";
    public static final String FALLBACK_STRING = "unavailable info";

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
        String status;
        String needsMaintenance;
        String active;
        String comment;

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
            active = machineInfoJSON.optString("active", FALLBACK_STRING);
            comment = machineInfoJSON.optString("comment", FALLBACK_STRING);
        } catch (JSONException e) {
            // TODO: Improve error handling
            View layout = findViewById(R.id.equipmentInfoLayout);
            Snackbar.make(layout, "Error parsing machine info", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }

        // Prepares the output
        // TODO: Determine which data to show, which to hide
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append(getResources().getString(R.string.machineID)).append(": ").append(id).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineName)).append(": ").append(name).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineType)).append(": ").append(type).append("\n");
        outputBuilder.append(getResources().getString(R.string.machinePower)).append(": ").append(power).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineBrand)).append(": ").append(brand).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineModel)).append(": ").append(model).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineStatus)).append(": ").append(status).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineMaintenanceNeeded)).append("? ").append(needsMaintenance).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineActive)).append("? ").append(active).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineComment)).append(": ").append(comment).append("\n");

        // Writes it to the main textview
        TextView infoTextView = findViewById(R.id.infoTextView);
        infoTextView.setText(outputBuilder);

        // Button for registering an event that changes the machine status, like arrival or departure
        FloatingActionButton fab = findViewById(R.id.registerEventFAB);
        fab.setOnClickListener(view -> {
            // TODO: Show dialog in full screen
            DialogFragment dialog = new RegisterMachineEventDialogFragment();
            dialog.show(getSupportFragmentManager(), "RegisterMachineEventDialogFragment");
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        RegisterMachineEventDialogFragment dialogFragment = (RegisterMachineEventDialogFragment) dialog;
        int selectedOption = dialogFragment.SelectedEventOption;
        View layout = findViewById(R.id.equipmentInfoLayout);
        Snackbar.make(layout, "Selected option: " + selectedOption, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        View layout = findViewById(R.id.equipmentInfoLayout);
        Snackbar.make(layout, "Cancel pressed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
