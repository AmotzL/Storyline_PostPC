package com.postpc.nisha.storyline;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.File;

public class FinishStory extends AppCompatActivity {

    private static final int RUNNING_ID = 8;
    private static final int MY_STORIES_ID = 9;

    Button album_btn;
    Button map_btn;
    private int caller;
    private ImageView gifView;
    private String curStoryName;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String folderPath;
    private String gifPath;
    DbForStoriesHelper storiesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_story);

        TextView album_tv = (TextView) findViewById(R.id.album_text_tv);
        TextView map_tv = (TextView) findViewById(R.id.map_text_tv);
        TextView headline_tv = (TextView) findViewById(R.id.story_name_view);
        Typeface fontType = Typeface.createFromAsset(this.getAssets(), "fonts/ASSISTANT-REGULAR.TTF");
        gifView = findViewById(R.id.gif_finish_story);
        album_tv.setTypeface(fontType);
        map_tv.setTypeface(fontType);
        headline_tv.setTypeface(fontType);

        if (getIntent().hasExtra(getString(R.string.intentKey_myStories_to_finish_story_name))) {
            curStoryName = getIntent().getStringExtra(getString(R.string.intentKey_myStories_to_finish_story_name));
        }
        caller = MY_STORIES_ID;            // TODO DANKO
        if (getIntent().hasExtra("arrive from runningStory")){// TODO DANKO
            caller = RUNNING_ID;           // TODO DANKO
        }
        storiesHelper = LoginActivity.getStoriesDb();
        setUpActivity();
        Cursor gifCursor = storiesHelper.getStoryGifDirDetailsFromStoryName(curStoryName);
        gifCursor.moveToNext();
        gifPath = "";
        gifPath = gifCursor.getString(1);
        gifCursor.close();

        Cursor isEmptyGifCursor = storiesHelper.getStoryGifDirDetailsFromStoryName(curStoryName);
        isEmptyGifCursor.moveToNext();
        String isEmptyGif = isEmptyGifCursor.getString(2);
        isEmptyGifCursor.close();

        if (isEmptyGif.equals("true")) {
            gifView.setImageResource(R.drawable.empty_gif);
        } else {
            setUpGif();
        }
        album_btn = findViewById(R.id.album_btn);
        album_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinishStory.this, AlbumActivity.class);
                intent.putExtra("StartDate", startDate);
                intent.putExtra("EndDate", endDate);
                intent.putExtra("FolderPath", folderPath);
                intent.putExtra("StartTime", startTime);
                intent.putExtra("EndTime", endTime);
                startActivity(intent);
            }
        });

        gifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinishStory.this, GifLarger.class);
                intent.putExtra("GifPath", gifPath);
                intent.putExtra(GifLarger.FLAGIMAGEGIF, 1);
                startActivity(intent);
            }
        });

        map_btn = findViewById(R.id.map_btn);
        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinishStory.this, MapFinishActivity.class);
                intent.putExtra(MapFinishActivity.FIELD, curStoryName);
                startActivity(intent);
            }
        });

    }

    private void setUpGif(){

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifView);
        Glide.with(this).load(gifPath).transform( new RotateTransformation(this, 90f )).into(imageViewTarget);
    }

    private void setUpActivity(){

        Cursor storyDetails = storiesHelper.getStoryDetailsFromName(curStoryName);
        storyDetails.moveToNext();

        folderPath = getImagesDir(storiesHelper);
        final TextView tv_finishAct_storyName = findViewById(R.id.story_name_view);
        tv_finishAct_storyName.setText(storyDetails.getString(1));
        startDate = storyDetails.getString(5);
        startTime = storyDetails.getString(6);
        endDate = storyDetails.getString(7);
        endTime = storyDetails.getString(8);
        storyDetails.close();
    }


    public static String getImagesDir(DbForStoriesHelper storiesHelper){
        String fullPath = storiesHelper.getPathAsString();
        Uri uri = Uri.parse(fullPath);
        return uri.getLastPathSegment();
    }

    /**
     * If My Stories is the caller activity, it inflates the menu/back.xml.
     * If Running is the caller activity, it inflates the menu/finish_act.xml.
     * and return true to show it on the screen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (caller == MY_STORIES_ID) {
            getMenuInflater().inflate(R.menu.finish_from_my_stories, menu);
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
            Intent intentToCreateActivity = new Intent(FinishStory.this, CreateStoryActivity.class);
            startActivity(intentToCreateActivity);
        }
//        else if (itemThatWasClickedId == R.id.menu_finishAct_myProfile) {
//            Intent intentToMyProfile = new Intent(FinishStoryActivity.this, MyProfileActivity.class);
//            startActivity(intentToMyProfile);
//        }
        else if (itemThatWasClickedId == R.id.menu_finishAct_myStories) {
            Intent intentToMyStories = new Intent(FinishStory.this, MyStoriesActivity.class);
            startActivity(intentToMyStories);
        }
//        else if (itemThatWasClickedId == R.id.menu_finishAct_logOut) {
//            logOutPopUp();
//        }

        else if (itemThatWasClickedId == R.id.menu_finishAct_share)
            shareGif();
        return super.onOptionsItemSelected(item);
    }

    private void shareGif() {
//        Uri uri = Uri.parse(gifPath);
        Uri uri = Uri.parse(new File(gifPath).toString());

        ShareCompat.IntentBuilder
                .from(this)
                .setType("image/gif")
                .setStream(uri)
                .startChooser();

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



    public static class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super( context );

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public String getId() {
            return "rotate" + rotateRotationAngle;
        }
    }
}
