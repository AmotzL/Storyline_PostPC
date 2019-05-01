package com.postpc.nisha.storyline;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * The current story of the user.
 */
public class RunningStoryActivity extends AppCompatActivity  implements OnMapReadyCallback, OnMapClickListener, OnCameraIdleListener, GoogleMap.OnMarkerClickListener {

    private String curStoryName;
    private GoogleMap mMap;
    private static final String TAG = CreateStoryActivity.class.getSimpleName();
    private int last_location_id = 0;
    PolylineOptions poly_line = new PolylineOptions();
    LatLngBounds.Builder bounds_builder = new LatLngBounds.Builder();
    DbForStoriesHelper storiesDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storiesDb = LoginActivity.getStoriesDb();
        setContentView(R.layout.activity_running_story);
        // Gets the storyName that inserted in the Create activity
        if(getIntent().hasExtra(getString(R.string.intentKey_create_to_running_story_name))) {
            curStoryName = getIntent().getStringExtra(getString(R.string.intentKey_create_to_running_story_name));
        }
        Long days_diff = calculateRunningDays();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView mapTitleTV = (TextView) findViewById(R.id.map_title_tv);
        TextView thTV = (TextView) findViewById(R.id.th_tv);
        Typeface fontType = Typeface.createFromAsset(this.getAssets(), "fonts/ASSISTANT-SEMIBOLD.TTF");
        mapTitleTV.setTypeface(fontType);
        thTV.setTypeface(fontType);
    }

    /**
     * Calculate the length of the current trip in days.
     */
    private Long calculateRunningDays(){
        Cursor all = storiesDb.getStoryStartDateFromName(curStoryName);
        all.moveToNext();
        String startStr = all.getString(1);
        Calendar today = Calendar.getInstance();
        today.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = null;
        try {
            startDate = sdf.parse(startStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Start Date Problem", Toast.LENGTH_SHORT).show();
        }
        Calendar startCalDay = Calendar.getInstance();
        startCalDay.setTime(startDate);
        long daysBetween = 0;
        while (startCalDay.before(today)) {
            startCalDay.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;

    }

    /**
     * Inflates the menu/running_act.xml, and return true to show it on the screen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.running_act, menu);
        return true;
    }

    /**
     * This func activated when there is a click on some item in the menu.
     * if my profile clicked we will activate the MyProfile activity.
     * if my stories clicked we will activate the MyStories activity.
     * if finish story clicked we will pop a message that asks permission to finish the story.
     * If LogOut clicked we will pop a message that asks permission to log out.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
//        if (itemThatWasClickedId == R.id.menu_runningAct_myProfile) {
//            Intent intentToMyProfile = new Intent(RunningStoryActivity.this, MyProfileActivity.class);
//            startActivity(intentToMyProfile);
//        }
        if (itemThatWasClickedId == R.id.menu_runningAct_myStories) {
            Intent intentToMyStories = new Intent(RunningStoryActivity.this, MyStoriesActivity.class);
            startActivity(intentToMyStories);
        }
        else if (itemThatWasClickedId == R.id.menu_runningAct_finishStory) {
            popStoryFinishingDialog();
        }
//        else if (itemThatWasClickedId == R.id.menu_runningAct_logOut) {
//            popLoggingOutDialog();
//        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * When the user try to logOut, we ask for his permission. If he said "yes" -
     * update the db as finish story, switch to Login activity. if "No" - do nothing.
     */
    private void popLoggingOutDialog() {
        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logging Out!")
                .setMessage("Your current story will be ended now!\n" +
                        "Are you sure you want to Log Out? ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbForStoriesHelper storiesDb = LoginActivity.getStoriesDb();
                        storiesDb.updateEndDateAndTime(getDateAsString(), curStoryName, getTimeAsString());
                        Intent intentToLogin = new Intent(RunningStoryActivity.this, LoginActivity.class);
                        startActivity(intentToLogin);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    /**
     * When the user try to finish the story, we ask for his permission. If he said "yes" -
     * update the DB as finish story, switch to Finish activity (send the storyName to
     * Finish activity). If "No" - do nothing.
     */
    private void popStoryFinishingDialog() {
        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Story Finishing")
                .setMessage("Are you sure you want to finish the current story?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GoogleApiClient mGoogleApiClient = LoginActivity.getGoogleApiHelper().getGoogleApiClient();
                        Log.i(TAG, "Removing location updates");
//                        LocationRequestHelper.setRequesting(this, false);

                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                                getPendingIntent());
                        // Updates the DB to be a finish story.
                        DbForStoriesHelper storiesDb = LoginActivity.getStoriesDb();
                        storiesDb.updateEndDateAndTime(getDateAsString(), curStoryName, getTimeAsString());
                        // switch to Finish activity (send the storyName)
//                        Intent intentToFinishStory = new Intent(RunningStoryActivity.this, FinishStory.class);
//                        intentToFinishStory.putExtra(getString(R.string.intentKey_myStories_to_finish_story_name), curStoryName);
//                        startActivity(intentToFinishStory);

                        createSouvenirGif();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * This method activates when the user press back. pops a message and stay in the activity.
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Story is running!")
                .setMessage("Sorry, press Finish Story to end your story")
                .setNeutralButton("OK", null)
                .show();
    }


    /**
     * Return the date of today as a String type.
     */
    private String getDateAsString() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(cal.getTime());
    }

    /**
     * Return the time as a string.
     */
    private String getTimeAsString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        return mdformat.format(calendar.getTime());
    }


//    /**
//     * TODO(4) delete this method, and delete the button map_test.
//     * its initiate google maps when the user click the button.
//     */
//    public void map_test_clicked(View view) {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("geo").path("0,0").query("Arlozorov 10, Jerusalem");
//        Uri adressUri = builder.build();
//        Intent intentToMap = new Intent(Intent.ACTION_VIEW);
//        intentToMap.setData(adressUri);
//        if (intentToMap.resolveActivity(getPackageManager()) != null) {
//            startActivity(intentToMap);
//        }
//    }

    /**
     * Prepare the map setting.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        updateMap(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        updateMap(false);
    }

    @Override
    public void onCameraIdle() {
        updateMap(false);
    }

    /**
     * Update the  locations pins on the map.
     */
    public void updateMap(boolean forceUpdate) {
        Cursor all = storiesDb.getAllUserLocations(curStoryName);
        int size = all.getCount();
        if (last_location_id < size || (forceUpdate && size > 0)) {
            mMap.clear();
            while (all.moveToNext()) {
                LatLng tmp_latLog = new LatLng(all.getDouble(1), all.getDouble(2));
                poly_line.add(tmp_latLog).width(5).color(Color.RED);
                bounds_builder.include(tmp_latLog);
                mMap.addMarker(new MarkerOptions().position(tmp_latLog).title(String.valueOf(all.getInt(0))));
            }
            LatLngBounds bounds = bounds_builder.build();
            mMap.addPolyline(poly_line);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
            last_location_id = size;
        }
    }

    /**
     * Handle a click on a marker of the map (open an image album of +-30 min
     * from the marker time stamp.)
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        String db_index = marker.getTitle();
        Cursor all = storiesDb.getTimeOfLocation(db_index);
        if (all.getCount() > 0)
        {
            all.moveToNext();
            long markerTime = Long.valueOf(all.getString(1));
            long minsToAdd = 60;
            String startTime = Epoch2DateString(markerTime, "HH:mm:ss",0);
            String startDate = Epoch2DateString(markerTime, "dd/MM/yyyy",0);
            String endTime = Epoch2DateString(markerTime, "HH:mm:ss",+ minsToAdd);
            String endDate = Epoch2DateString(markerTime, "dd/MM/yyyy",+ minsToAdd);
            goToRunningStoryActivity(startTime, startDate, endTime, endDate);
        }
        return false;
    }

    /**
     * Change the activity into running story activity.
     */
    private void goToRunningStoryActivity(String startTime, String startDate, String endTime, String endDate) {
        Cursor storyDetails = storiesDb.getStoryDetailsFromName(curStoryName);
        storyDetails.moveToNext();
        String folderPath = getImagesDir(storiesDb);
        Intent intent = new Intent(RunningStoryActivity.this, AlbumActivity.class);
        intent.putExtra("StartDate", startDate);
        intent.putExtra("StartTime", startTime);
        intent.putExtra("EndDate", endDate);
        intent.putExtra("EndTime", endTime);
        intent.putExtra("FolderPath", folderPath);
        startActivity(intent);
    }

    /**
     * Convert the current date into string.
     */
    public static String Epoch2DateString(long epochSeconds, String formatString, long minutesToAdd) {
        Date updatedate = new Date(epochSeconds + minutesToAdd*60*1000);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(updatedate);
    }

    /**
     * Get the path into the image directory from the db.
     */
    public static String getImagesDir(DbForStoriesHelper storiesHelper){
        String fullPath = storiesHelper.getPathAsString();
        Uri uri = Uri.parse(fullPath);
        return uri.getLastPathSegment();
    }

    /**
     * Create the souvenir (GIF) out of the chosen images.
     */
    private void createSouvenirGif(){
        DbForStoriesHelper storiesHelper = LoginActivity.getStoriesDb();
        Cursor storyDetails = storiesHelper.getStoryDetailsFromName(curStoryName);
        storyDetails.moveToNext();
        String startDate = storyDetails.getString(5);
        String startTime = storyDetails.getString(6);
        String endDate = storyDetails.getString(7);
        String endTime = storyDetails.getString(8);
        Intent souvenirGifCreationIntent = new Intent(RunningStoryActivity.this, SouvenirGifCreation.class);
        souvenirGifCreationIntent.putExtra(getString(R.string.intentKey_cur_story_name), curStoryName);
        souvenirGifCreationIntent.putExtra(getString(R.string.intentKey_cur_story_start_date), startDate);
        souvenirGifCreationIntent.putExtra(getString(R.string.intentKey_cur_story_end_date), endDate);
        souvenirGifCreationIntent.putExtra(getString(R.string.start_time_key), startTime);
        souvenirGifCreationIntent.putExtra(getString(R.string.end_time_key), endTime);
        startActivity(souvenirGifCreationIntent);
        Toast.makeText(this,"Your souvenir will be ready in few minutes",Toast.LENGTH_LONG).show();
    }

}


