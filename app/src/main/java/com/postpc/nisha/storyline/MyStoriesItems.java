package com.postpc.nisha.storyline;

/**
 * This class represents a story that already finished.
 */
public class MyStoriesItems{

    private String storyName;
    private String endDate;

    /**
     * A constructor
     */
    MyStoriesItems(String name, String date){
        storyName = name;
        endDate = date;
    }

    /**
     * returns the name of the story.
     */
    public String getStoryName() {
        return storyName;
    }

    /**
     *  Returns the end date of the story.
     */
    public String getEndDate() {
        return endDate;
    }
}