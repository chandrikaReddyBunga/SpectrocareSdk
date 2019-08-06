package com.spectrochips.spectrumsdk.MODELS;

/**
 * Created by wave on 12/10/2018.
 */

public class MeasureItemsStruct {

    String  testName;
    double distance;
    String distanceUnit;
    int steps;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
