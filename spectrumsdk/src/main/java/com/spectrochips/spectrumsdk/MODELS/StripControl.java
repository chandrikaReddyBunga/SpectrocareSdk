package com.spectrochips.spectrumsdk.MODELS;

import java.util.ArrayList;

/**
 * Created by wave on 12/10/2018.
 */

public class StripControl {

    double distanceFromPostionSensorToSpectroMeterInMM;
    double distanceFromHolderEdgeTo1STStripInMM;
    double distancePerStepInMM;
    ArrayList<Steps> steps;

    public double getDistanceFromPostionSensorToSpectroMeterInMM() {
        return distanceFromPostionSensorToSpectroMeterInMM;
    }

    public void setDistanceFromPostionSensorToSpectroMeterInMM(double distanceFromPostionSensorToSpectroMeterInMM) {
        this.distanceFromPostionSensorToSpectroMeterInMM = distanceFromPostionSensorToSpectroMeterInMM;
    }

    public double getDistanceFromHolderEdgeTo1STStripInMM() {
        return distanceFromHolderEdgeTo1STStripInMM;
    }

    public void setDistanceFromHolderEdgeTo1STStripInMM(double distanceFromHolderEdgeTo1STStripInMM) {
        this.distanceFromHolderEdgeTo1STStripInMM = distanceFromHolderEdgeTo1STStripInMM;
    }

    public double getDistancePerStepInMM() {
        return distancePerStepInMM;
    }

    public void setDistancePerStepInMM(double distancePerStepInMM) {
        this.distancePerStepInMM = distancePerStepInMM;
    }

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Steps> steps) {
        this.steps = steps;
    }
}
