package com.postpc.nisha.storyline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class GifLarger extends AppCompatActivity {

    public static final String GIF = "GifPath";
    public static final String FLAGIMAGEGIF = "FLAG";

    private ImageView imageView;
    private int gifOrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_larger);

        String photo = "no photo";

        imageView = findViewById(R.id.my_image);
        if (getIntent().hasExtra(FLAGIMAGEGIF)){
            gifOrImage = getIntent().getIntExtra(FLAGIMAGEGIF, -1);
        }
        if (getIntent().hasExtra(GIF)){
            photo = getIntent().getStringExtra(GIF);
        }
        switch (gifOrImage){
            case 0:
                Glide.with(this).load("file://" + photo)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageView);
                break;
            case 1:
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
                Glide.with(this).load(photo).transform( new FinishStory.RotateTransformation( this, 90f )).into(imageViewTarget);
                break;
        }
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
