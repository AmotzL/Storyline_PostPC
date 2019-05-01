package com.postpc.nisha.storyline;

import java.util.Comparator;

/**
 * comparator of classified image confidence (score) values
 */
public class ClassifiedImageComparator implements Comparator<ClassifiedImage> {
    @Override
    public int compare(ClassifiedImage image1, ClassifiedImage image2) {
        if (image1.getClassificationResult().getConfidence() - image2.getClassificationResult().getConfidence() > 0) {
            return 1;
        } else if (image1.getClassificationResult().getConfidence() - image2.getClassificationResult().getConfidence() < 0) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
