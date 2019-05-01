package com.postpc.nisha.storyline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class PhotosDetailsActivity extends AppCompatActivity {

    public static final String IMAGE = "InsideGallery.String";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_details);

        String photo = "no photo";

        imageView = findViewById(R.id.my_image);
        if (getIntent().hasExtra(IMAGE)){
            photo = getIntent().getStringExtra(IMAGE);
        }



        Glide.with(this).load("file://" + photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }
}
