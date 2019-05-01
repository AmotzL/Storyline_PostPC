package com.postpc.nisha.storyline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class FinishStoryActivity extends AppCompatActivity {

    private static final int RUNNING_ID = 8;
    private static final int MY_STORIES_ID = 9;
    private int caller;

    private String curStoryName;

    private static final String TAG = FinishStoryActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 35;

    private static ProgressBar waitingSpinner;
    public static Handler waitingSpinnerHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        waitingSpinner = findViewById(R.id.progressBar);
        waitingSpinner.setVisibility(View.GONE);
        waitingSpinnerHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                int spinnerVisible = 1;
                int spinnerGone = 2;
                if (message.what == spinnerVisible){
                    waitingSpinner.setVisibility(View.VISIBLE);
                } else if (message.what == spinnerGone) {
                    waitingSpinner.setVisibility(View.GONE);
                }
            }
        };

        // Updates the variable 'caller' to hold the activity that initiate this.
        // Also updates the storyName according to the value that caller sent.
        caller = RUNNING_ID;
        if(getIntent().hasExtra(getString(R.string.intentKey_running_to_finish_story_name))) {
            curStoryName = getIntent().getStringExtra(getString(R.string.intentKey_running_to_finish_story_name));
        } else if (getIntent().hasExtra(getString(R.string.intentKey_myStories_to_finish_story_name))) {
            curStoryName = getIntent().getStringExtra(getString(R.string.intentKey_myStories_to_finish_story_name));
            caller = MY_STORIES_ID;
        }
        fillTextViews();
        createsAllSouvenirOptions();

        if (!checkPermissions()) {
            requestPermissions();
        }
    }


    private boolean checkPermissions() {
        int readPermissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermissionState == PackageManager.PERMISSION_GRANTED
                && writePermissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationaleForRead =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean shouldProvideRationaleForWrite =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationaleForRead && shouldProvideRationaleForWrite) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_create_story),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(FinishStoryActivity.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(FinishStoryActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Gets the details from the DB according to the unique storyName,
     * And fill the text views. if description an location not inserted in the Create
     * activity, those fields will stay empty
     */
    private void fillTextViews() {
        DbForStoriesHelper storiesDb = LoginActivity.getStoriesDb();
        Cursor storyDetails = storiesDb.getStoryDetailsFromName(curStoryName);
        storyDetails.moveToNext();

        final TextView tv_finishAct_storyName = findViewById(R.id.tv_FinishAct_storyName);
        final TextView tv_finishAct_storyDescription = findViewById(R.id.tv_FinishAct_storyDescription);
        final TextView tv_finishAct_storyLocation = findViewById(R.id.tv_FinishAct_storyLocation);
        final TextView tv_finishAct_startDate = findViewById(R.id.tv_FinishAct_startDate);
        final TextView tv_finishAct_endDate = findViewById(R.id.tv_FinishAct_endDate);

        tv_finishAct_storyName.setText(storyDetails.getString(1));
        tv_finishAct_storyDescription.setText(storyDetails.getString(2));
        tv_finishAct_storyLocation.setText(storyDetails.getString(3));
        tv_finishAct_startDate.setText(storyDetails.getString(5));
        tv_finishAct_endDate.setText(storyDetails.getString(6));
        if (storyDetails.getString(6).equals(getString(R.string.default_end_date))){
            // HIDE all buttons when the story still in running mode
            findViewById(R.id.tv_FinishAct_chooseSouvenir).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_FinishAct_album).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_FinishAct_story).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_FinishAct_video).setVisibility(View.INVISIBLE);
        }
        storyDetails.close();
    }


    //--------------------------------------------------------------------------------------
    //TODO(5) this methods creates the souvenirs and upload it to some DataBase
    /**
     *  Creates all the souvenirs according to the unique storyName,
     *  and saves all of them to some file/DB
     */
    private void createsAllSouvenirOptions() {
        DbForStoriesHelper storiesDb = LoginActivity.getStoriesDb();
        Cursor storyDetails = storiesDb.getStoryDetailsFromName(curStoryName);
        storyDetails.moveToNext();
        createLongStory();
        createShortStory();
        createLongVideo();
        createShortVideo();
        createLongAlbum();
        createShortAlbum();
        storyDetails.close();
    }
    private void createLongStory(){}
    private void createShortStory(){}
    private void createLongVideo(){}
    private void createShortVideo(){}
    private void createLongAlbum(){}
    private void createShortAlbum(){}
    //--------------------------------------------------------------------------------------


    /**
     * If My Stories is the caller activity, it inflates the menu/back.xml.
     * If Running is the caller activity, it inflates the menu/finish_act.xml.
     * and return true to show it on the screen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (caller == MY_STORIES_ID) {
            getMenuInflater().inflate(R.menu.back, menu);
        } else if (caller ==RUNNING_ID) {
            getMenuInflater().inflate(R.menu.finish_act, menu);
        }
        return true;
    }


    /**
     * This func activated when there is a click on some item in the menu.
     * If the caller is MyStories and the back clicked we will back to the caller
     * if createNewStory clicked we will activate the createStory activity.
     * if my profile clicked we will activate the MyProfile activity.
     * if my stories clicked we will activate the MyStories activity.
     * If LogOut clicked we will pop a message that asks permission to log out.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.menu_back) {
            super.onBackPressed();
        } else if (itemThatWasClickedId == R.id.menu_finishAct_createNewStory) {
            Intent intentToCreateActivity = new Intent(FinishStoryActivity.this, CreateStoryActivity.class);
            startActivity(intentToCreateActivity);
        }
//        else if (itemThatWasClickedId == R.id.menu_finishAct_myProfile) {
//            Intent intentToMyProfile = new Intent(FinishStoryActivity.this, MyProfileActivity.class);
//            startActivity(intentToMyProfile);
//        }
        else if (itemThatWasClickedId == R.id.menu_finishAct_myStories) {
            Intent intentToMyStories = new Intent(FinishStoryActivity.this, MyStoriesActivity.class);
            startActivity(intentToMyStories);
        }
//        else if (itemThatWasClickedId == R.id.menu_finishAct_logOut) {
//            logOutPopUp();
//        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * When the user try to logOut, we ask for his permission. If he said "yes" -
     * switch to Login activity. if "No" - do nothing.
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
                        Intent intentToLoginActivity = new Intent(FinishStoryActivity.this, LoginActivity.class);
                        startActivity(intentToLoginActivity);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    /**
     * This method activates when the user press back.
     * If the caller is RunningActivity it pops a message and stay in the activity.
     * If the caller is MyStories we will back to the caller
     */
    @Override
    public void onBackPressed() {
        if(caller == RUNNING_ID) {
            popStoryFinishedDialog();
        }
        else if (caller == MY_STORIES_ID) {
            super.onBackPressed();
        }
    }


    /**
     * This method pops a message and stay in the activity.
     */
    private void popStoryFinishedDialog() {
        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Story Already Finished!")
                .setMessage("Sorry, try to create new story")
                .setNeutralButton("OK", null)
                .show();
    }


    /**
     * Those methods call when one of the buttons clicked.
     * they create a dialog to choose the Length of the souvenir.
     */
    public void btn_FinishAct_story_clicked(View view) {
        popSouvenirLengthDialog("story");
    }
    public void btn_FinishAct_video_clicked(View view) {
        popSouvenirLengthDialog("video"); }
    public void btn_FinishAct_album_clicked(View view) {
        popSouvenirLengthDialog("album"); }


    /**
     * This method pop a dialog to choose the length of the souvenir to show.
     * And activates fragment with the required details.
     */
    private void popSouvenirLengthDialog(final String souvenirType) {
        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Souvenir Length")
                .setMessage("Choose the length of your souvenir?")
                .setPositiveButton("Long", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (souvenirType){
                            case "story": activateLongStoryFragment(); break;
                            case "video": activateLongVideoFragment(); break;
                            case "album": activateLongAlbumFragment(); break;
                        }
                    }
                })
                .setNegativeButton("Short", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (souvenirType){
                            case "story": activateShortStoryFragment(); break;
                            case "video": activateShortVideoFragment(); break;
                            case "album": activateShortAlbumFragment(); break;
                        }
                    }
                })
                .show();
    }

    //--------------------------------------------------------------------------------------
    //TODO(6) those methods get the souvenir from the file/DB and show it in a fragment
    private void activateShortAlbumFragment() {Toast.makeText(this,"SHORT ALBUM WILL BE SHOWN",Toast.LENGTH_SHORT).show();}
    private void activateShortVideoFragment() {Toast.makeText(this,"SHORT VIDEO WILL BE SHOWN",Toast.LENGTH_SHORT).show();}
    private void activateShortStoryFragment() { createSouvenirGif(); }
    private void activateLongAlbumFragment() {Toast.makeText(this,"LONG ALBUM WILL BE SHOWN",Toast.LENGTH_SHORT).show();}
    private void activateLongVideoFragment() {Toast.makeText(this,"LONG VIDEO WILL BE SHOWN",Toast.LENGTH_SHORT).show();}
    private void activateLongStoryFragment() {Toast.makeText(this,"LONG STORY WILL BE SHOWN",Toast.LENGTH_SHORT).show();
    }

    //--------------------------------------------------------------------------------------

    private void createSouvenirGif(){
        Intent souvenirGifCreationIntent = new Intent(FinishStoryActivity.this, SouvenirGifCreation.class);
        souvenirGifCreationIntent.putExtra(getString(R.string.intentKey_myStories_to_finish_story_name), curStoryName);
        souvenirGifCreationIntent.putExtra(getString(R.string.intentKey_myStories_to_finish_story_name), curStoryName);
        souvenirGifCreationIntent.putExtra(getString(R.string.intentKey_myStories_to_finish_story_name), curStoryName);
        startActivity(souvenirGifCreationIntent);
        Toast.makeText(this,"Your souvenir will be ready in few minutes",Toast.LENGTH_LONG).show();
    }
}


