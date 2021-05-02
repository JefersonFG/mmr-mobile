package com.example.mrm.mobile;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BackendConnectionWorker extends Worker {
    public static final String INPUT_MACHINE_CODE = "machineCode";
    public static final String WORKER_RESULT = "result";

    public BackendConnectionWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String machineCode = inputData.getString(INPUT_MACHINE_CODE);

        // Connect to the backend asking for the data of the machine with the id read in the qr code
        OkHttpClient client = new OkHttpClient();
        // TODO: Update references to backend endpoint
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3134/api/stockItems/" + machineCode)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                int errorCode = response.code();
                String backendResponse = Objects.requireNonNull(response.body()).string();
                if (errorCode < 400) {
                    Data connectionResponse = new Data.Builder()
                            .putString(WORKER_RESULT, backendResponse)
                            .build();
                    return Result.success(connectionResponse);
                } else {
                    // TODO: Improve error handling - parse json and get the data under "message"
                    Data errorResponse = new Data.Builder()
                            .putString(WORKER_RESULT, backendResponse)
                            .build();
                    return Result.failure(errorResponse);
                }
            } else {
                // TODO: Improve error handling
                Data errorResponse = new Data.Builder()
                        .putString(WORKER_RESULT, "generic error connecting to the backend")
                        .build();
                return Result.failure(errorResponse);
            }
        } catch (Exception e) {
            // TODO: Improve error handling
            Data errorResponse = new Data.Builder()
                    .putString(WORKER_RESULT, e.getMessage())
                    .build();
            return Result.failure(errorResponse);
        }
    }
}
