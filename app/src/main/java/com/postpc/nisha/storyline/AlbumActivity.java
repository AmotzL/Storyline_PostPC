package com.postpc.nisha.storyline;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AlbumActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 0;

    private ArrayList<String> imagesPath;
    private String folderPath;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat myHourFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);

        }

        setUpStrings();

        RecyclerView recyclerView = findViewById(R.id.my_album_recycler);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        imagesPath = setUpImagesFromCamera(folderPath, startDate, endDate, getApplicationContext());
        AdapterAlbum adapterImages = new AdapterAlbum(this, imagesPath);
        recyclerView.setAdapter(adapterImages);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not  granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public static ArrayList<String> setUpImagesFromCamera(String folderPath, Calendar startDate, Calendar endDate, Context context) {
        ArrayList<String> imagesPath = new ArrayList<>();

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            String folderName = cursor.getString(column_index_folder_name);
            if (folderName.equals(folderPath)) {
                absolutePathOfImage = cursor.getString(column_index_data);
                ExifInterface imageData = null;
                try {
                    imageData = new ExifInterface(absolutePathOfImage);
                    String dateString = imageData.getAttribute(ExifInterface.TAG_DATETIME);
                    Calendar imageDate = Calendar.getInstance();
                    SimpleDateFormat imageDateFormat = new SimpleDateFormat("yyyy:MM:dd");
                    imageDate.setTime(imageDateFormat.parse(dateString));
                    setHourOfCalender(dateString.substring(11, dateString.length()), imageDate);
                    if (checkBetweenDates(imageDate, endDate, startDate)) {
                        imagesPath.add(absolutePathOfImage);
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        cursor.close();
        return imagesPath;
    }

    private void setUpStrings() {
        try {
            System.out.println(startDate.getTime());
            if (getIntent().hasExtra(getString(R.string.start_date_key))) {
                startDate.setTime(myDateFormat.parse(getIntent().getStringExtra(getString(R.string.start_date_key))));
            }
            System.out.println(startDate.getTime());
            if (getIntent().hasExtra(getString(R.string.end_date_key))) {
                endDate.setTime(myDateFormat.parse(getIntent().getStringExtra(getString(R.string.end_date_key))));
            }

            if (getIntent().hasExtra(getString(R.string.folder_key))) {
                folderPath = getIntent().getStringExtra(getString(R.string.folder_key));
            }

            if (getIntent().hasExtra(getString(R.string.start_time_key))) {
                setHourOfCalender(getIntent().getStringExtra(getString(R.string.start_time_key)), startDate);
            }
            if (getIntent().hasExtra(getString(R.string.end_time_key))) {
                setHourOfCalender(getIntent().getStringExtra(getString(R.string.end_time_key)), endDate);
            }
            System.out.println(startDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that set the hour of day according to the parameter.
     * @param timeOfDay hour of day in format hh:mm:ss
     */
    public static void setHourOfCalender(String timeOfDay, Calendar calendar)
    {
        int hour = Integer.parseInt(timeOfDay.substring(0,2));
        int minutes = Integer.parseInt(timeOfDay.substring(3,5));
        int seconds = Integer.parseInt(timeOfDay.substring(6,8));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
    }

    private static boolean checkBetweenDates(Calendar photoDate, Calendar endDate, Calendar startDate) {
        return (photoDate.before(endDate) && photoDate.after(startDate));
    }

    private static boolean checkEqualDates(Calendar photoDate, Calendar other){
        return other.get(Calendar.YEAR) == photoDate.get(Calendar.YEAR) &&
                other.get(Calendar.MONTH) == photoDate.get(Calendar.MONTH) &&
                other.get(Calendar.DAY_OF_MONTH) == photoDate.get(Calendar.DAY_OF_MONTH);
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


    public static int rotateImageToRightOrient(int imageOrient) {
        int rotate = 0;
        switch (imageOrient){
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
        }
        return rotate;
    }
}

