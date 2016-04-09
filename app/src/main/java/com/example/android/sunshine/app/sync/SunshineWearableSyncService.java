package com.example.android.sunshine.app.sync;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Nora on 4/6/2016.
 */
public class SunshineWearableSyncService extends WearableListenerService {

    public static final String REQUEST_WEATHER_DATA = "/SunshineWatchUpdateService/RequestData";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED && dataEvent.getDataItem().getUri().getPath().equals(REQUEST_WEATHER_DATA)) {
                SunshineSyncAdapter.syncImmediately(getApplicationContext());
            }
        }
    }

}
