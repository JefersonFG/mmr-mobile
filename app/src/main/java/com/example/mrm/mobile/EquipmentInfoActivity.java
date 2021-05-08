package com.example.mrm.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class EquipmentInfoActivity extends AppCompatActivity {
    public static final String EQUIPMENT_INFO_KEY = "equipment_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_info);

        // Gets the input data
        String equipmentInfo = getIntent().getStringExtra(EQUIPMENT_INFO_KEY);

        // TODO: Parse the received json, fill relevant data on UI
        // Writes it to the main textview
        TextView infoTextView = (TextView) findViewById(R.id.infoTextView);
        infoTextView.setText(equipmentInfo);
    }
}
