package com.postpc.nisha.storyline;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class MyProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        // sets all the widgets values according to the shared preference
        final ImageView iv_myProfile_picture = findViewById(R.id.iv_myProfile_picture);
        final TextView tv_myProfile_name = findViewById(R.id.tv_myProfile_name);
        final TextView tv_myProfile_email = findViewById(R.id.tv_myProfile_email);

        iv_myProfile_picture.setImageBitmap(getPictureFromDB());
        tv_myProfile_name.setText(getNameFromDB());
        tv_myProfile_email.setText(getEmailFromDB());
    }


    /**
     * Returns the image to show. if no image take it will show some android logo
     */
    private Bitmap getPictureFromDB() {
        try {
            return BitmapFactory.decodeStream(openFileInput(getString(R.string.file_name_for_profile_image)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }


    /**
     * Returns the name that saves in the shared preferences
     */
    private String getNameFromDB() {
        return sharedPref.getString(getString(R.string.pref_name_key), "defaultName problem");
    }


    /**
     * Returns the email that saves in the shared preferences
     */
    private String  getEmailFromDB() {
        return sharedPref.getString(getString(R.string.pref_email_key), "defaultEmail problem");
    }


    /**
     * Inflates the menu/back.xml, and return true to show it on the screen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }


    /**
     * This func activated when there is a click on some item in the menu.
     * if the back icon clicked we will go back to the last activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.menu_back) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
