package com.example.mrm.mobile;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackendConnectionWorker extends Worker {
    public static final String INPUT_QRCODE_KEY = "qrcode";
    public static final String RESPONSE_KEY = "response";
    public static final String ERROR_MESSAGE_KEY = "error";

    public BackendConnectionWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Data inputData = getInputData();
        String QRCode = inputData.getString(INPUT_QRCODE_KEY);

        Data connectionResponse = new Data.Builder()
                .putString(RESPONSE_KEY, "Test response for input: " + QRCode)
                .build();

        return Result.success(connectionResponse);
    }
}
