package com.example.mrm.mobile;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

public class StockItem {
    public static String TAG = "stock_item";

    public HashMap<StockItemFields, String> infoMap = new HashMap<>();

    private static final String FALLBACK_STRING = "unavailable info";

    // Builds the internal representation based on the json received from the backend
    public StockItem(String itemInfo) {
        try {
            JSONObject itemInfoJSON = new JSONObject(itemInfo);

            for (StockItemFields field : StockItemFields.values()) {
                infoMap.put(field, parseJSONItem(itemInfoJSON, field.toString()));
            }
        } catch (Exception e) {
            // TODO: Improve error handling
            Log.d(TAG, "Error parsing stock item json: " + e.getMessage());
        }
    }

    private String parseJSONItem(JSONObject itemInfoJSON, String infoToLookup) {
        String value = itemInfoJSON.optString(infoToLookup, FALLBACK_STRING);
        if (value.equals("null")) {
            value = FALLBACK_STRING;
        }
        return value;
    }
}
