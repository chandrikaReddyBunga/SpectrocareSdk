package com.spectrochips.spectrumsdk.MODELS;

import java.util.ArrayList;

/**
 * Created by Abhilash on 10/17/2018.
 */

public class IntensityChart {

    private String testName;
    private Boolean pixelMode;
    private Boolean originalMode;
    private Boolean autoMode;
    private ArrayList<Float> xAxisArray;
    private ArrayList<Float> yAxisArray;
    private ArrayList<Float> substratedArray;
    private ArrayList<Float> wavelengthArray;
    private double criticalWavelength;


    public void setxAxisArray(ArrayList<Float> xAxisArray) {
        this.xAxisArray = xAxisArray;
    }



    public ArrayList<Float> getyAxisArray() {
        return yAxisArray;
    }

    public ArrayList<Float> getxAxisArray() {
        return xAxisArray;
    }


    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setyAxisArray(ArrayList<Float> yAxisArray) {
        this.yAxisArray = yAxisArray;
    }

    public Boolean getPixelMode() {
        return pixelMode;
    }

    public void setPixelMode(Boolean pixelMode) {
        this.pixelMode = pixelMode;
    }

    public Boolean getOriginalMode() {
        return originalMode;
    }

    public void setOriginalMode(Boolean originalMode) {
        this.originalMode = originalMode;
    }

    public Boolean getAutoMode() {
        return autoMode;
    }

    public void setAutoMode(Boolean autoMode) {
        this.autoMode = autoMode;
    }

    public ArrayList<Float> getSubstratedArray() {
        return substratedArray;
    }

    public void setSubstratedArray(ArrayList<Float> substratedArray) {
        this.substratedArray = substratedArray;
    }

    public ArrayList<Float> getWavelengthArray() {
        return wavelengthArray;
    }

    public void setWavelengthArray(ArrayList<Float> wavelengthArray) {
        this.wavelengthArray = wavelengthArray;
    }

    public double getCriticalWavelength() {
        return criticalWavelength;
    }

    public void setCriticalWavelength(double criticalWavelength) {
        this.criticalWavelength = criticalWavelength;
    }


}
