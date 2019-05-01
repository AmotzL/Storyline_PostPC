package com.postpc.nisha.storyline;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.postpc.nisha.storyline.DbForStoriesHelper;

import java.util.ArrayList;

public class AdapterAlbum extends RecyclerView.Adapter<AdapterAlbum.MyViewHolder> {

    private Context context;
    private ArrayList<String> imagesPath;


    public AdapterAlbum(Context context, ArrayList<String> imagesPath) {
        this.context = context;
        this.imagesPath = imagesPath;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_grid_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAlbum.MyViewHolder holder, int position) {
        holder.iv_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load("file://" + imagesPath.get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_image);
    }

    @Override
    public int getItemCount() {
        return imagesPath.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView iv_image;


        public MyViewHolder(View view){
            super(view);

            iv_image = view.findViewById(R.id.my_image);
            iv_image.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                String imagePath = imagesPath.get(position);

                Intent intent = new Intent(context, GifLarger.class);
                intent.putExtra(GifLarger.GIF, imagePath);
                intent.putExtra(GifLarger.FLAGIMAGEGIF, 0);
                context.startActivity(intent);
            }
        }
    }
}
