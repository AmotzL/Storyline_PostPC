package com.postpc.nisha.storyline;

import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.util.Log;

/**
 * directory file observer class. used to monitor changes in a given directory.
 * we use it to know when the gif creation is finished, so we can proceed to FinishStory activity
 */
public class DirectoryFileObserver extends FileObserver {

    private String aboslutePath ;
    private Context context;
    private String storyName;

    public DirectoryFileObserver(String path, String curStoryName, Context appContext) {
        super(path,FileObserver.CREATE);
        aboslutePath = path;
        context = appContext;
        storyName = curStoryName;
    }
    @Override
    public void onEvent(int event, String path) {
        if(path != null){
            Intent finishIntent = new Intent(context, FinishStory.class);
            finishIntent.putExtra(context.getString(R.string.intentKey_myStories_to_finish_story_name), storyName);
            finishIntent.putExtra("arrive from runningStory", "arrive from runningStory");
            context.startActivity(finishIntent);
        }
    }
}
