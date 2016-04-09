package com.example.android.sunshine.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;

import static com.example.android.sunshine.app.SunshineWatchFace.*;

/**
 * Created by Nora on 4/4/2016.
 */
public class SunshineWatchUpdateService extends WearableListenerService {

    public static final String REQUEST_WEATHER_DATA = "/SunshineWatchUpdateService/RequestData";
    public static final String WEATHER_HAS_BEEN_UPDATED = "/SunshineWatchUpdateService/WeatherUpdate";

    public static final String WEATHER_TEMP_HIGH = "weatherTempHigh";
    public static final String WEATHER_TEMP_LOW = "weatherTempLow";
    public static final String WEATHER_ICON_ASSET = "weatherIconAsset";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            if (null == dataEvent
                    || dataEvent.getType() != DataEvent.TYPE_CHANGED
                    || !(dataEvent.getDataItem().getUri().getPath().equals(WEATHER_HAS_BEEN_UPDATED))) {
                continue;
            }
            parseDataEvent(dataEvent);
        }
    }

    private void parseDataEvent(DataEvent dataEvent) {
        DataItem dataItem = dataEvent.getDataItem();
        String dataEventUri = dataItem.getUri().getPath();
        if (dataEventUri.equals(WEATHER_HAS_BEEN_UPDATED)) {
            DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();

            int high = dataMap.getInt(WEATHER_TEMP_HIGH);
            int low = dataMap.getInt(WEATHER_TEMP_LOW);

            Asset weatherIconAsset = dataMap.getAsset(WEATHER_ICON_ASSET);
            Bitmap weatherIcon = weatherIconAsset == null ? null : getWeatherIcon(weatherIconAsset);
            updateWeather(high, low, weatherIcon);
        }
    }

    private Bitmap getWeatherIcon(Asset asset) {
        final GoogleApiClient googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();
        InputStream inputStream = Wearable.DataApi
                .getFdForAsset(googleApiClient, asset)
                .await()
                .getInputStream();
        googleApiClient.disconnect();

        return (inputStream == null ? null : BitmapFactory.decodeStream(inputStream));
    }

}
