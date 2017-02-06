package com.example.wear;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import static android.R.attr.data;

/**
 * Created by robbi on 2/4/2017.
 */

public class WeatherListener extends WearableListenerService {
    public final String LOG_TAG = WeatherListener.class.getSimpleName();

    public static final String HIGH_TEMP = "highTemp";
    public static final String LOW_TEMP = "lowTemp";
    public static final String ICON = "iconID";
    public static final String TIME = "time";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.v(LOG_TAG, "DataMap item made it to watch");

        //get data, set temps and icon on WatchFace
        for (DataEvent event : dataEvents) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (path.equals("/weather")) {
                final DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                WatchFace.highTemp = dataMap.getDouble(HIGH_TEMP);
                WatchFace.lowTemp = dataMap.getDouble(LOW_TEMP);
                WatchFace.icon = dataMap.getInt(ICON);
                WatchFace.timeSent = dataMap.getLong(TIME);
            }
        }
    }
}
