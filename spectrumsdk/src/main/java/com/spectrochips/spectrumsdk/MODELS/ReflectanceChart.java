package com.spectrochips.spectrumsdk.MODELS;

import java.util.ArrayList;

/**
 * Created by wave on 12/3/2018.
 */

public class ReflectanceChart {

    String testName;
    ArrayList<Float> xAxisArray;
    ArrayList<Float> yAxisArray;
    double criticalWavelength;
    boolean autoMode;
    double interpolationValue;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public ArrayList<Float> getxAxisArray() {
        return xAxisArray;
    }

    public void setxAxisArray(ArrayList<Float> xAxisArray) {
        this.xAxisArray = xAxisArray;
    }

    public ArrayList<Float> getyAxisArray() {
        return yAxisArray;
    }

    public void setyAxisArray(ArrayList<Float> yAxisArray) {
        this.yAxisArray = yAxisArray;
    }

    public double getCriticalWavelength() {
        return criticalWavelength;
    }

    public void setCriticalWavelength(double criticalWavelength) {
        this.criticalWavelength = criticalWavelength;
    }

    public boolean isAutoMode() {
        return autoMode;
    }

    public void setAutoMode(boolean autoMode) {
        this.autoMode = autoMode;
    }

    public double getInterpolationValue() {
        return interpolationValue;
    }

    public void setInterpolationValue(double interpolationValue) {
        this.interpolationValue = interpolationValue;
    }
}
