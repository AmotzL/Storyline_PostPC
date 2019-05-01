package com.postpc.nisha.storyline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoryViewHolder> {

    final private ListItemClickListener mOnClickListener;
    private ArrayList<MyStoriesItems> listOFPastStories;


    // This interface tells what happens when clicking some item
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, String storyName, String endDate);
    }


    // Constructor, initiates the fields
    StoriesAdapter(ArrayList<MyStoriesItems> listOFPastStories, ListItemClickListener listener) {
        this.listOFPastStories = listOFPastStories;
        mOnClickListener = listener;
    }


    /**
     * This func called when the RecyclerView instantiates a new ViewHolder instance
     */
    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.story_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        StoryViewHolder viewHolder = new StoryViewHolder(view);
        return viewHolder;
    }


    /**
     * This func called when the RecyclerView wants to populate the view with data
     */
    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        holder.bind(position);
//        notifyDataSetChanged(); //todo: this line should be on, but it makes a problem.
    }


    /**
     * Returns the total number of items.
     */
    @Override
    public int getItemCount() {
        return listOFPastStories.size();
    }



    // View Holder class that implements our interface
    class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        TextView tv_storyItem_index;
        ImageView iv_storyItem_storyImg;
        TextView tv_storyItem_storyName;


        // Constructor, initiates the fields
        StoryViewHolder(View itemView) {
            super(itemView);
//            tv_storyItem_index = itemView.findViewById(R.id.tv_storyItem_index);
            iv_storyItem_storyImg = itemView.findViewById(R.id.IV_storyItem_storyImg);
            tv_storyItem_storyName = itemView.findViewById(R.id.tv_storyItem_storyName);
            itemView.setOnClickListener(this);
        }


        /**
         * bind between the the text views and the values
         */
        void bind(int listIndex) {
//            tv_storyItem_index.setText(String.valueOf(listIndex));
            iv_storyItem_storyImg.setImageResource(R.drawable.ic_my_stories_icon);
            tv_storyItem_storyName.setText(listOFPastStories.get(listIndex).getStoryName());
        }


        /**
         * Called when an item has been clicked.
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            String clickedStoryName = tv_storyItem_storyName.getText().toString();
            mOnClickListener.onListItemClick(clickedPosition, clickedStoryName, listOFPastStories.get(clickedPosition).getEndDate());
        }


    }
}
