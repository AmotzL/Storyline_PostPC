package com.postpc.nisha.storyline;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Callable;

public class CreateStoryActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPref;
    private DbForStoriesHelper storiesDb;

    private Typeface fontType;

    /**
     * holds the camera images path (calculated in runtime cause it varies between different phones
     */
    private static String cameraImagesPath = "";

    private static final String TAG = CreateStoryActivity.class.getSimpleName();

    private String permissionStateLocation = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private String permissionStateRead = Manifest.permission.READ_EXTERNAL_STORAGE;
    private String permissionStateWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_NEEDED_PERMISSIONS_REQUEST_CODE = 34;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */


    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;


    /**
     * The entry point to Google Play Services.
     */
    private GoogleApiClient mGoogleApiClient;

    // UI Widgets.
    private Button mRequestUpdatesButton;
//    private Button mRemoveUpdatesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);
        // Gets the references to the data bases.
        storiesDb = LoginActivity.getStoriesDb();
        sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        // This if clause enters only when we arrived here from the Login activity
        if(getIntent().hasExtra(getString(R.string.intentKey_login_to_create_userName))) {
            String userName = getIntent().getStringExtra(getString(R.string.intentKey_login_to_create_userName));
            // Saves the values from DB to the shared preference according to the unique userName
            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.putString(getString(R.string.pref_username_key), userName);
//            editor.putString(getString(R.string.pref_name_key), storiesDb.getOwnerNameOfUser(userName));
//            editor.putString(getString(R.string.pref_password_key), storiesDb.getPasswordOfUser(userName));
//            editor.putString(getString(R.string.pref_email_key), storiesDb.getEmailOfUser(userName));
            editor.apply();
        }

        mRequestUpdatesButton = (Button) findViewById(R.id.btn_createAct_startNewStory);
        fontType = Typeface.createFromAsset(this.getAssets(), "fonts/ASSISTANT-LIGHT.TTF");
        mRequestUpdatesButton.setTypeface(fontType);

//        mRemoveUpdatesButton = (Button) findViewById(R.id.remove_updates_button);
//        mLocationUpdatesResultView = (TextView) findViewById(R.id.location_updates_result);

        if (!checkPermissions()) {
            requestPermissions();
        }
        buildGoogleApiClient();

        checkCameraImagesPathIfFirstTimeToUseApp();

    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        updateButtonsState(LocationRequestHelper.getRequesting(this));
//        mLocationUpdatesResultView.setText(LocationResultHelper.getSavedLocationResult(this));
    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
//    private void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//
//        // Sets the fastest rate for active location updates. This interval is exact, and your
//        // application will never receive updates faster than this value.
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        // Sets the maximum time when batched location updates are delivered. Updates may be
//        // delivered sooner than this interval.
//        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
//    }

    /**
     * Builds {@link GoogleApiClient}, enabling automatic lifecycle management using
     * {@link GoogleApiClient.Builder#enableAutoManage(android.support.v4.app.FragmentActivity,
     * int, GoogleApiClient.OnConnectionFailedListener)}. I.e., GoogleApiClient connects in
     * {@link AppCompatActivity#onStart}, or if onStart() has already happened, it connects
     * immediately, and disconnects automatically in {@link AppCompatActivity#onStop}.
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = LoginActivity.getGoogleApiHelper().getGoogleApiClient();
        mLocationRequest = LoginActivity.getGoogleApiHelper().getmLocationRequest();
//        createLocationRequest();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        final String text = "Connection suspended";
        Log.w(TAG, text + ": Error code: " + i);
        showSnackbar("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        final String text = "Exception while connecting to Google Play services";
        Log.w(TAG, text + ": " + connectionResult.getErrorMessage());
        showSnackbar(text);
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.activity_create_story);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                permissionStateLocation) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        permissionStateRead) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        permissionStateWrite) == PackageManager.PERMISSION_GRANTED;
    }

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
                            ActivityCompat.requestPermissions(CreateStoryActivity.this,
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
            ActivityCompat.requestPermissions(CreateStoryActivity.this,
                    new String[]{permissionStateLocation, permissionStateRead, permissionStateWrite},
                    REQUEST_NEEDED_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_NEEDED_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Kick off the process of building and connecting
                // GoogleApiClient.
//                buildGoogleApiClient();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.activity_create_story),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(LocationResultHelper.KEY_LOCATION_UPDATES_RESULT)) {
//            mLocationUpdatesResultView.setText(LocationResultHelper.getSavedLocationResult(this));
        } else if (s.equals(LocationRequestHelper.KEY_LOCATION_UPDATES_REQUESTED)) {
//            updateButtonsState(LocationRequestHelper.getRequesting(this));
        }
    }

//    /**
//     * Handles the Request Updates button and requests start of location updates.
//     */
//    public void requestLocationUpdates(View view) {
//        try {
//            Log.i(TAG, "Starting location updates");
//            LocationRequestHelper.setRequesting(this, true);
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                    mGoogleApiClient, mLocationRequest, getPendingIntent());
//        } catch (SecurityException e) {
//            LocationRequestHelper.setRequesting(this, false);
//            e.printStackTrace();
//        }
//    }

    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */
    public void removeLocationUpdates(View view) {
        Log.i(TAG, "Removing location updates");
        LocationRequestHelper.setRequesting(this, false);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                getPendingIntent());
    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */
//    private void updateButtonsState(boolean requestingLocationUpdates) {
//        if (requestingLocationUpdates) {
//            mRequestUpdatesButton.setEnabled(false);
//            mRemoveUpdatesButton.setEnabled(true);
//        } else {
//            mRequestUpdatesButton.setEnabled(true);
//            mRemoveUpdatesButton.setEnabled(false);
//        }
//    }


    /**
     * Inflates the menu/create_act.xml, and return true to show it on the screen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_act, menu);
        return true;
    }


    /**
     * This func activated when there is a click on some item in the menu.
     * If my profile clicked we will activate the MyProfile activity.
     * If my stories clicked we will activate the MyStories activity.
     * If LogOut clicked we will pop up a message that asks permission to log out.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
//        if (itemThatWasClickedId == R.id.menu_createAct_myProfile) {
//            Intent intentToMyProfile = new Intent(CreateStoryActivity.this, MyProfileActivity.class);
//            startActivity(intentToMyProfile);
//        }
        if (itemThatWasClickedId == R.id.menu_createAct_myStories) {
            Intent intentToMyStories = new Intent(CreateStoryActivity.this, MyStoriesActivity.class);
            startActivity(intentToMyStories);
        }
//        else if (itemThatWasClickedId == R.id.menu_createAct_logOut) {
//            logOutPopUp();
//        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * when the user clicked on the start new story button. gets the values from the
     * EditTexts. Inserts all the details into the DB if there is a unique storyName,
     * its switch to RunningStory activity, and sends the storyName with putExtra.
     */
    public void btn_CreateAct_startNewStory_clicked(View v){
        final EditText et_registerAct_storyName = findViewById(R.id.et_createAct_storyName);
        final EditText et_registerAct_storyDescription = findViewById(R.id.et_createAct_storyDescription);
        final EditText et_registerAct_storyLocation = findViewById(R.id.et_crateAct_storyLocation);

        String name = et_registerAct_storyName.getText().toString();
        String description = et_registerAct_storyDescription.getText().toString();
        String location = et_registerAct_storyLocation.getText().toString();
        // For empty storyName, we can't continue the function
        if (name.isEmpty()) {
            Toast.makeText(this,"Fill your Story Name", Toast.LENGTH_LONG).show();
            return;
        }
        // Inserts the values to the DB.
        Boolean isInserted = storiesDb.insertNewStory(
                name,
                description,
                location,
                sharedPref.getString(getString(R.string.pref_username_key), "defaultUserName problem"),
                getDateAsString(),
                getTimeAsString(), "filefilefile", sharedPref.getString(getString(R.string.pref_password_key), "defaultPassword problem"), sharedPref.getString(getString(R.string.pref_name_key), "defaultOwnerName problem"), sharedPref.getString(getString(R.string.pref_email_key), "defaultEmail problem"), getString(R.string.default_end_date),
                getString(R.string.default_end_date));
        // It will be false when the storyName is not unique.
        if (isInserted) {
            Globals g = Globals.getInstance();
            g.setStoryName(name);
            Intent intentToRunningStoryActivity = new Intent(CreateStoryActivity.this, RunningStoryActivity.class);
            intentToRunningStoryActivity.putExtra(getString(R.string.intentKey_create_to_running_story_name), name);
            startActivity(intentToRunningStoryActivity);
        } else {
            Toast.makeText(CreateStoryActivity.this, "Story name already exists, Try again", Toast.LENGTH_LONG).show();
        }

        try {
            Log.i(TAG, "Starting location updates");
            LocationRequestHelper.setRequesting(this, true);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            LocationRequestHelper.setRequesting(this, false);
            e.printStackTrace();
        }
    }



    /**
     * When the user try to logOut, we ask for his permission.
     * If he said "yes" - switch to Login activity. if "No" - do nothing
     */
    private void logOutPopUp(){
        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logging Out!")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentToLogin = new Intent(CreateStoryActivity.this, LoginActivity.class);
                        startActivity(intentToLogin);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    /**
     * This method returns the date of today as a String type.
     */
    private String getDateAsString() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(cal.getTime());
    }
    private String getTimeAsString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        return mdformat.format(calendar.getTime());
    }

    /**
     * check if camera images path is already stored in db. if not (i.e it's the first time to use
     * the app), it asks the user to take a picture, and by doing it we can get the images camera
     * folder path
     */
    private void checkCameraImagesPathIfFirstTimeToUseApp() {
        if (isStoriesDbEmpty()){
            caseFirstTime();
        }
    }

    /**
     * checks if stories db is empry, i.e if there was any story created
     * @return true if db is empty, false otherwise
     */
    private boolean isStoriesDbEmpty(){
        if (storiesDb.getAllStories().getCount() == 0) {
            return true;
        }
        return false;
    }

    /**
     * handles the updating of cameraImagesPath when the user uses the app for the first time
     */
    private void caseFirstTime(){

        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Help us make your souvenir great :)")
                .setMessage("Please minimize Storyline app and take a picture with your phone, so we can be sure that your camera is good to go")
                .setPositiveButton("I took a picture", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleClickForCameraImagesPathDialogs();
                    }
                })
                .show();



//        MessageDialog alert = new MessageDialog();
//        String firstTimeMsg = "Help us make your souvenir great :) \n" +
//                "Please minimize Storyline app and take a picture with your phone, so we can be " +
//                "sure that your camera is good to go";
//        String btnText = "I took a picture";
//        alert.showDialog(this, firstTimeMsg, btnText, new Callable<Void>() {
//            public Void call() {
//                handleClickForCameraImagesPathDialogs();
//                return null;
//            }
//        });
    }

    /**
     * handles the updating of cameraImagesPath when the user took selfie and the phone stores
     * selfies in different folder
     */
    private void caseSelfie(){

        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Great!")
                .setMessage("Now please take a picture with your back camera (not Selfie)")
                .setPositiveButton("I took a non-Selfie picture", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleClickForCameraImagesPathDialogs();
                    }
                })
                .show();


//        MessageDialog alert = new MessageDialog();
//        String selfieMsg = "Great! Now please take a picture with your back camera (not Selfie)";
//        String btnText = "I took a non-Selfie picture";
//        alert.showDialog(this, selfieMsg, btnText, new Callable<Void>() {
//            public Void call() {
//                handleClickForCameraImagesPathDialogs();
//                return null;
//            }
//        });
    }

    /**
     * handles the updating of cameraImagesPath when the last added image to the phone storage is
     * not a camera captured image (e.g image that was download from whatsapp etc...)
     */
    private void caseWrongPath(){

        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Something went wrong...")
                .setMessage("Please take another picture")
                .setPositiveButton("I took another picture", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleClickForCameraImagesPathDialogs();
                    }
                })
                .show();

//        MessageDialog alert = new MessageDialog();
//        String wrongMsg = "Something went wrong...\n" +
//                "Please take another picture";
//        String btnText = "I took another picture";
//        alert.showDialog(this, wrongMsg, btnText, new Callable<Void>() {
//            public Void call() {
//                handleClickForCameraImagesPathDialogs();
//                return null;
//            }
//        });
    }

    /**
     * handles the case when the path is PROBABLY good (cause we don't check every case in the world
     * for wrong path...)
     */
    private void caseGoodPath(){

        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Great!")
                .setMessage("Everything looks good!")
                .setPositiveButton("Lets go!", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        handleClickForCameraImagesPathDialogs();
                    }
                })
                .show();


//        MessageDialog alert = new MessageDialog();
//        String msg = "Great! Everything looks good!";
//        String btnText = "Lets go!";
//        alert.showDialog(this, msg, btnText, new Callable<Void>() {
//            public Void call() {
//                return null;
//            }
//        });
    }

    /**
     * checks if the path is a selfie path
     * @return true if selfie path, false otherwise
     */
    private boolean isSelfiePath(){
        if (cameraImagesPath.contains("selfie") || cameraImagesPath.contains("Selfie") ||
                cameraImagesPath.contains("SELFIE")) {
            return true;
        }
        return false;
    }

    /**
     * checks if the path is a wrong path
     * @return true if wrong path, false otherwise
     */
    private boolean isWrongPath(){
        boolean pathStillEmpty = cameraImagesPath.equals("");
        boolean whatsapp =
                cameraImagesPath.contains("whatsapp") ||
                cameraImagesPath.contains("Whatsapp") ||
                cameraImagesPath.contains("WhatsApp") ||
                cameraImagesPath.contains("WHATSAPP");
        boolean telegram =
                cameraImagesPath.contains("telegram") ||
                cameraImagesPath.contains("Telegram") ||
                cameraImagesPath.contains("TELEGRAM");
        boolean instagram =
                cameraImagesPath.contains("instagram") ||
                cameraImagesPath.contains("Instagram") ||
                cameraImagesPath.contains("INSTAGRAM");
        boolean facebook =
                cameraImagesPath.contains("facebook") ||
                cameraImagesPath.contains("Facebook") ||
                cameraImagesPath.contains("FACEBOOK");
        boolean snapchat =
                cameraImagesPath.contains("snapchat") ||
                cameraImagesPath.contains("Snapchat") ||
                cameraImagesPath.contains("SnapChat") ||
                cameraImagesPath.contains("SNAPCHAT");
        boolean screenshot =
                cameraImagesPath.contains("screenshot") ||
                cameraImagesPath.contains("Screenshot") ||
                cameraImagesPath.contains("ScreenShot")
                || cameraImagesPath.contains("SCREENSHOT") ||
                cameraImagesPath.contains("screenshots") ||
                cameraImagesPath.contains("Screenshots")
                || cameraImagesPath.contains("ScreenShots") ||
                cameraImagesPath.contains("Screenshots") ||
                cameraImagesPath.contains("SCREENSHOTS");
        boolean download =
                cameraImagesPath.contains("download") ||
                cameraImagesPath.contains("Download") ||
                cameraImagesPath.contains("DOWNLOAD") ||
                cameraImagesPath.contains("downloads") ||
                cameraImagesPath.contains("Downloads") ||
                cameraImagesPath.contains("DOWNLOADS");

        if (pathStillEmpty || whatsapp || telegram || instagram || facebook || snapchat ||
                screenshot || download) {
            return true;
        }
        return false;
    }

    /**
     * handles click for camera images path dialogs
     */
    private void handleClickForCameraImagesPathDialogs(){
        setCameraImagesPathToLastImageAddedToStorage();
        if (isSelfiePath()) {
            caseSelfie();
        } else if (isWrongPath()) {
            caseWrongPath();
        } else {
            caseGoodPath();
        }
    }

    /**
     * set camera images path to last image added to storage
     */
    private void setCameraImagesPathToLastImageAddedToStorage() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection,
                null, null, orderBy + " DESC");
        int column_index_data;
        String absolutePathOfImage = "";
        try {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            cursor.moveToNext();
            absolutePathOfImage = cursor.getString(column_index_data);
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        updatePathInDb(absolutePathOfImage);
        cameraImagesPath = absolutePathOfImage;
    }

    /**
     * update the camera images path in db
     * @param absolutePathOfImage absolute path of image
     */
    private void updatePathInDb(String absolutePathOfImage){
        File image = new File(absolutePathOfImage);
        String cameraImagesPath = image.getParent();
        if (storiesDb.getPath().getCount() == 0) {
            storiesDb.insertNewPath(cameraImagesPath);
        } else {
            storiesDb.updatePath(cameraImagesPath);

        }
    }
}
