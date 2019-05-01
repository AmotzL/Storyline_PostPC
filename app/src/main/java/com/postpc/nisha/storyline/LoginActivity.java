package com.postpc.nisha.storyline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * The login to the app (initiate the app's db, google api helper and the Location Request).
 */
public class LoginActivity extends AppCompatActivity implements StoriesAdapter.ListItemClickListener {

    private static DbForStoriesHelper storiesDb;
    private GoogleApiHelper googleApiHelper;
    private static LoginActivity mInstance;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean test;
    private ArrayList<MyStoriesItems> listOFStories;
    private StoriesAdapter storiesAdapter;
    private RecyclerView rv_myStories;
    private static final String TAG = "FragmentActivity";
    private String permissionStateLocation = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private String permissionStateRead = Manifest.permission.READ_EXTERNAL_STORAGE;
    private String permissionStateWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_NEEDED_PERMISSIONS_REQUEST_CODE = 34;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mInstance = this;
        if (!checkPermissions()) {
            requestPermissions();
        }
        googleApiHelper = new GoogleApiHelper(mInstance);
        // Initiates the DB in the first visit in the app
        if (storiesDb == null) {
            storiesDb = new DbForStoriesHelper(this);
        }
        listOFStories = MyStoriesActivity.getStoriesWithDatesFromDB(this);
        // This lines connect between the recycler and the adapter
        rv_myStories = findViewById(R.id.rv_myStories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_myStories.setLayoutManager(layoutManager);
        rv_myStories.setHasFixedSize(true);
        storiesAdapter = new StoriesAdapter(listOFStories, this);
        rv_myStories.setAdapter(storiesAdapter);

        TextView createStoryTV = (TextView) findViewById(R.id.create_story_tv);
        TextView continueStoryTV = (TextView) findViewById(R.id.continue_story_tv);
        Typeface fontType = Typeface.createFromAsset(this.getAssets(), "fonts/ASSISTANT-LIGHT.TTF");
        createStoryTV.setTypeface(fontType);
        continueStoryTV.setTypeface(fontType);

        View crateStoryBtn = findViewById(R.id.btn_start_new_story);
        View continueStoryBtn = findViewById(R.id.btn_continue_story);
        String endDate = getApplicationContext().getString(R.string.default_end_date);
        Cursor unfinishedStoryName = storiesDb.getUnfinishedStory(endDate);

        if (unfinishedStoryName.getCount() != 0) {
//            LoginActivity.getGoogleApiHelper().buildGoogleApiClient();
            crateStoryBtn.setVisibility(View.GONE);
            continueStoryBtn.setVisibility(View.VISIBLE);
            createStoryTV.setVisibility(View.GONE);
            continueStoryTV.setVisibility(View.VISIBLE);
        } else{
            crateStoryBtn.setVisibility(View.VISIBLE);
            continueStoryBtn.setVisibility(View.GONE);
            createStoryTV.setVisibility(View.VISIBLE);
            continueStoryTV.setVisibility(View.GONE);
        }
    }

    /**
     * Check if the app has the required permissions.
     */
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                permissionStateLocation) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        permissionStateRead) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        permissionStateWrite) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request for the permissionStateLocation, permissionStateRead
     * and permissionStateWrite permissions.
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permissionStateLocation) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permissionStateRead) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permissionStateWrite);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_create_story),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{permissionStateLocation, permissionStateRead, permissionStateWrite},
                                    REQUEST_NEEDED_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{permissionStateLocation, permissionStateRead, permissionStateWrite},
                    REQUEST_NEEDED_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * This method returns list of stories from the DB according to the userName
     * that saves in the shared Preference.
     */
    private ArrayList<String> getStoriesFromDB() {
        ArrayList<String> storiesList = new ArrayList<>();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String userName = sharedPref.getString(getString(R.string.pref_username_key), "defaultUserName problem");
        DbForStoriesHelper storiesDb = LoginActivity.getStoriesDb();
        Cursor storiesNames = storiesDb.getAllUserStoriesNames(userName);
        if (storiesNames.getCount() > 0) {    // There is available stories
            while (storiesNames.moveToNext()) {     // runs through all the stories of the user.
                storiesList.add(storiesNames.getString(0)); // col 0 - because we have table with only one col (names)
            }
        }
        storiesNames.close();
        return storiesList;
    }

    /**
     * This method activates when the user click one of the story, it receive the the name of
     * the story that clicked, and sends it to the Finish activity.
     */
    @Override
    public void onListItemClick(int clickedItemIndex, String clickedStoryName, String endDate) {
        Intent intentToFinishAct = new Intent(LoginActivity.this, FinishStory.class);
        intentToFinishAct.putExtra(getString(R.string.intentKey_myStories_to_finish_story_name), clickedStoryName);
        startActivity(intentToFinishAct);
    }

    public static synchronized LoginActivity getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }

    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }

    /**
     * when the user clicked on the sign in button. gets the values from the EditText.
     * checks availability in the DB. if details available-
     * switch to CreateStory activity, and sends the userName with putExtra.
     */
    public void btn_start_new_story_clicked(View v){
        Intent intentToCreateActivity = new Intent(LoginActivity.this,
                CreateStoryActivity.class);
        intentToCreateActivity.putExtra(getString(R.string.intentKey_login_to_create_userName), "temp_login_to_create");
        startActivity(intentToCreateActivity);

    }

    /**
     * Continue into running story activity in case such story exist.
     */
    @SuppressLint("MissingPermission")
    public void btn_continue_story_clicked(View v){
        mGoogleApiClient = LoginActivity.getGoogleApiHelper().getGoogleApiClient();
        mLocationRequest = LoginActivity.getGoogleApiHelper().getmLocationRequest();
        test = mGoogleApiClient.isConnected();
        LocationRequestHelper.setRequesting(this, true);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, getPendingIntent());
        Intent intentToRunningActivity = new Intent(LoginActivity.this,
                RunningStoryActivity.class);
        String endDate = getApplicationContext().getString(R.string.default_end_date);
        Cursor storyNameCur = storiesDb.getUnfinishedStory(endDate);
        storyNameCur.moveToNext();
        String storyName = storyNameCur.getString(0);
        intentToRunningActivity.putExtra(getString(R.string.intentKey_create_to_running_story_name), storyName);
        startActivity(intentToRunningActivity);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * A getter method to the DB.
     */
    public static DbForStoriesHelper getStoriesDb() {
        return storiesDb;
    }
}

