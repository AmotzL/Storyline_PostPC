package com.postpc.nisha.storyline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

/**
 * this class holds a recycler view with all the stories of the user from the dataBase.
 */
public class MyStoriesActivity extends AppCompatActivity implements StoriesAdapter.ListItemClickListener{

    private ArrayList<MyStoriesItems> listOFStories;
    private StoriesAdapter storiesAdapter;
    private RecyclerView rv_myStories;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stories);
        listOFStories = getStoriesWithDatesFromDB(this);
        // This lines connect between the recycler and the adapter
        rv_myStories = findViewById(R.id.rv_myStories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_myStories.setLayoutManager(layoutManager);
        rv_myStories.setHasFixedSize(true);
        storiesAdapter = new StoriesAdapter(listOFStories, this);
        rv_myStories.setAdapter(storiesAdapter);
        Log.d("amotz", "test");
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


    /**
     * This method activates when the user click one of the story, it receive the the name of
     * the story that clicked, and sends it  to the Finish activity.
     */
    @Override
    public void onListItemClick(int clickedItemIndex, String clickedStoryName, String endDate) {
        Intent intent;
        if (endDate.equals(getString(R.string.default_end_date))){
            intent = new Intent(MyStoriesActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            intent = new Intent(MyStoriesActivity.this, FinishStory.class);
            intent.putExtra(getString(R.string.intentKey_myStories_to_finish_story_name), clickedStoryName);
            startActivity(intent);
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
     *This method returns list of stories that already finished.
     * i.e. without the stories that currently running.
     */
    public static ArrayList<MyStoriesItems> getStoriesWithDatesFromDB(Context context) {
        ArrayList<MyStoriesItems> storiesItems = new ArrayList<>();
        DbForStoriesHelper storiesDb = LoginActivity.getStoriesDb();
        Cursor storiesNames = storiesDb.getAllStoriesNamesAndEndDates();
        if (storiesNames.getCount() > 0) {
            while (storiesNames.moveToNext()) {
                if (!storiesNames.getString(2).equals(context.getString(R.string.default_end_date)))
                {
                    storiesItems.add(new MyStoriesItems(storiesNames.getString(1), storiesNames.getString(2)));
                }
            }
        }
        storiesNames.close();
        return storiesItems;
    }
}
