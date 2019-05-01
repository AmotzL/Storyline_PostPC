package com.postpc.nisha.storyline;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapFinishActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String FIELD = "StoryName";

    GoogleMap mMap;
    private String curStoryName;
    DbForStoriesHelper storiesDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storiesDb = LoginActivity.getStoriesDb();
        setContentView(R.layout.activity_map_finish);

        if (getIntent().hasExtra(FIELD)){
            curStoryName = getIntent().getStringExtra(FIELD);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        updateMap();
    }

    public void updateMap() {
        PolylineOptions poly_line = new PolylineOptions();
        LatLngBounds.Builder bounds_builder = new LatLngBounds.Builder();
        DbForStoriesHelper storiesDb = LoginActivity.getStoriesDb();
        Cursor all = storiesDb.getAllUserLocations(curStoryName);
        while (all.moveToNext()) {
            LatLng tmp_latLog = new LatLng(all.getDouble(1), all.getDouble(2));
            poly_line.add(tmp_latLog).width(5).color(Color.RED);
            bounds_builder.include(tmp_latLog);
            mMap.addMarker(new MarkerOptions().position(tmp_latLog).title(String.valueOf(all.getInt(0))));
        }
        LatLngBounds bounds = bounds_builder.build();
        mMap.addPolyline(poly_line);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
    }

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
            String endTime = Epoch2DateString(markerTime, "HH:mm:ss",minsToAdd);
            String endDate = Epoch2DateString(markerTime, "dd/MM/yyyy",minsToAdd);
            goToRunningStoryActivity(startTime, startDate, endTime, endDate);
        }
        return false;
    }

    private void goToRunningStoryActivity(String startTime, String startDate, String endTime, String endDate) {
        Cursor storyDetails = storiesDb.getStoryDetailsFromName(curStoryName);
        storyDetails.moveToNext();
        String folderPath = getImagesDir(storiesDb);
        Intent intent = new Intent(MapFinishActivity.this, AlbumActivity.class);
        intent.putExtra("StartDate", startDate);
        intent.putExtra("StartTime", startTime);
        intent.putExtra("EndDate", endDate);
        intent.putExtra("EndTime", endTime);
        intent.putExtra("FolderPath", folderPath);
        startActivity(intent);
    }

    public static String Epoch2DateString(long epochSeconds, String formatString, long minutesToAdd) {
        Date updatedate = new Date(epochSeconds + minutesToAdd*60*1000);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(updatedate);
    }



    public static String getImagesDir(DbForStoriesHelper storiesHelper){
        String fullPath = storiesHelper.getPathAsString();
        Uri uri = Uri.parse(fullPath);
        return uri.getLastPathSegment();
    }

}
