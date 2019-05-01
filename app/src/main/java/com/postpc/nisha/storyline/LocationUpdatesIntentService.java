package com.postpc.nisha.storyline;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

public class LocationUpdatesIntentService extends IntentService {
    static final String ACTION_PROCESS_UPDATES =
            "com.postpc.nisha.storyline.action" +
                    ".PROCESS_UPDATES";
    private static final String TAG = LocationUpdatesIntentService.class.getSimpleName();


    public LocationUpdatesIntentService() {
        // Name the worker thread.
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    String endDate = getApplicationContext().getString(R.string.default_end_date);

                    LocationResultHelper locationResultHelper = new LocationResultHelper(this,
                            locations,endDate);
                    // Save the location data to SharedPreferences.
                    locationResultHelper.saveResults();

                    // Add the new locations to the DB.
                    locationResultHelper.addLocationToDb();



                    // Show notification with the location data.
                    // TODO: 03/10/2018 change here in order to show notification.
//                    locationResultHelper.showNotification();
                    Log.i(TAG, LocationResultHelper.getSavedLocationResult(this));
                }
            }
        }
    }
}
