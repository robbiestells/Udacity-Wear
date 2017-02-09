package com.example.wear;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static android.R.attr.data;
import static android.content.ContentValues.TAG;

/**
 * Created by robbi on 2/4/2017.
 */

public class WeatherListener extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks {
    private static final long TIMEOUT_MS = 10000;
    public final String LOG_TAG = WeatherListener.class.getSimpleName();

    public static final String HIGH_TEMP = "highTemp";
    public static final String LOW_TEMP = "lowTemp";
    public static final String ICON = "iconID";
    public static final String TIME = "time";

    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
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

                Asset iconAsset = dataMap.getAsset(ICON);
                Bitmap iconBitmap = loadBitmapFromAsset(iconAsset);
                WatchFace.icon = iconBitmap;
                WatchFace.timeSent = dataMap.getLong(TIME);
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset iconAsset) {
        if (iconAsset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        ConnectionResult result =
                mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, iconAsset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
