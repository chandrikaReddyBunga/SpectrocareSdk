package com.spectrochips.spectrumsdk.MODELS;

/**
 * Created by wave on 12/10/2018.
 */

public class Steps {
    String testName;
    String direction;
    int stripIndex;
    int noOfSteps;
    int dwellTimeInSec;
    int standardWhiteIndex;
    int noOfAverage;
    double distanceInMM;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getStripIndex() {
        return stripIndex;
    }

    public void setStripIndex(int stripIndex) {
        this.stripIndex = stripIndex;
    }

    public int getNoOfSteps() {
        return noOfSteps;
    }

    public void setNoOfSteps(int noOfSteps) {
        this.noOfSteps = noOfSteps;
    }

    public int getDwellTimeInSec() {
        return dwellTimeInSec;
    }

    public void setDwellTimeInSec(int dwellTimeInSec) {
        this.dwellTimeInSec = dwellTimeInSec;
    }

    public int getStandardWhiteIndex() {
        return standardWhiteIndex;
    }

    public void setStandardWhiteIndex(int standardWhiteIndex) {
        this.standardWhiteIndex = standardWhiteIndex;
    }

    public int getNoOfAverage() {
        return noOfAverage;
    }

    public void setNoOfAverage(int noOfAverage) {
        this.noOfAverage = noOfAverage;
    }

    public double getDistanceInMM() {
        return distanceInMM;
    }

    public void setDistanceInMM(double distanceInMM) {
        this.distanceInMM = distanceInMM;
    }
}
