package com.postpc.nisha.storyline;

import com.postpc.nisha.storyline.classifier.Result;

/**
 * classified image object. holds its absolute path and classification result
 */
public class ClassifiedImage {
    private String path;
    private Result classificationResult;


    public ClassifiedImage(String path, Result classificationResult){
        this.path = path;
        this.classificationResult = classificationResult;
    }

    /**
     * get classified image absolute path
     */
    public String getPath() {
        return path;
    }

    /**
     * get classified image absolute classification result
     */
    public Result getClassificationResult() {
        return classificationResult;
    }

}
