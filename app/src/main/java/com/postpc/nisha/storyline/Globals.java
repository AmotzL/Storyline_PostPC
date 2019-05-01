package com.postpc.nisha.storyline;

public class Globals{
    private static Globals instance;

    // Global variable
    private String storyName;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setStoryName(String d){
        this.storyName = d;
    }
    public String getStoryName(){
        return this.storyName;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}