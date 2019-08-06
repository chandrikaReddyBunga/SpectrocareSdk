package com.spectrochips.spectrumsdk.MODELS;

import java.util.ArrayList;

/**
 * Created by wave on 12/10/2018.
 */

public class StripMeasurmentStruct {

    double stepDistanceInMM;
    int stepCountForOppositeDirection;
    int extraStepCountForEject;
    ArrayList<MeasureItemsStruct> measureItems;

    public double getStepDistanceInMM() {
        return stepDistanceInMM;
    }

    public void setStepDistanceInMM(double stepDistanceInMM) {
        this.stepDistanceInMM = stepDistanceInMM;
    }

    public int getStepCountForOppositeDirection() {
        return stepCountForOppositeDirection;
    }

    public void setStepCountForOppositeDirection(int stepCountForOppositeDirection) {
        this.stepCountForOppositeDirection = stepCountForOppositeDirection;
    }

    public int getExtraStepCountForEject() {
        return extraStepCountForEject;
    }

    public void setExtraStepCountForEject(int extraStepCountForEject) {
        this.extraStepCountForEject = extraStepCountForEject;
    }

    public ArrayList<MeasureItemsStruct> getMeasureItems() {
        return measureItems;
    }

    public void setMeasureItems(ArrayList<MeasureItemsStruct> measureItems) {
        this.measureItems = measureItems;
    }
}
