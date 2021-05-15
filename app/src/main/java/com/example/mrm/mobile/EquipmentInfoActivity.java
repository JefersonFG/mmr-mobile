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

    private StockItem mStockItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_info);

        // Gets the input data
        String equipmentInfo = getIntent().getStringExtra(EQUIPMENT_INFO_KEY);

        // Parse the equipment info into its own class
        mStockItem = new StockItem(equipmentInfo);

        // Prepares the output
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append(getResources().getString(R.string.machineID)).append(": ").append(mStockItem.infoMap.get(StockItemFields.id)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineName)).append(": ").append(mStockItem.infoMap.get(StockItemFields.name)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineType)).append(": ").append(mStockItem.infoMap.get(StockItemFields.type)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machinePower)).append(": ").append(mStockItem.infoMap.get(StockItemFields.power)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineBrand)).append(": ").append(mStockItem.infoMap.get(StockItemFields.brand)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineModel)).append(": ").append(mStockItem.infoMap.get(StockItemFields.model)).append("\n");
        // TODO: Translate status from backend
        outputBuilder.append(getResources().getString(R.string.machineStatus)).append(": ").append(mStockItem.infoMap.get(StockItemFields.status)).append("\n");
        // TODO: Translate boolean values when showing on the UI
        outputBuilder.append(getResources().getString(R.string.machineMaintenanceNeeded)).append("? ").append(mStockItem.infoMap.get(StockItemFields.needsMaintenance)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineComment)).append(": ").append(mStockItem.infoMap.get(StockItemFields.comment)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machinePressure)).append(": ").append(mStockItem.infoMap.get(StockItemFields.pressure)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineThroughput)).append(": ").append(mStockItem.infoMap.get(StockItemFields.throughput)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineVoltage)).append(": ").append(mStockItem.infoMap.get(StockItemFields.voltage)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineYear)).append(": ").append(mStockItem.infoMap.get(StockItemFields.year)).append("\n");
        outputBuilder.append(getResources().getString(R.string.machineSerialNumber)).append(": ").append(mStockItem.infoMap.get(StockItemFields.serialNumber)).append("\n");

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
        data.putExtra(MACHINE_CODE, mStockItem.infoMap.get(StockItemFields.id));
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
