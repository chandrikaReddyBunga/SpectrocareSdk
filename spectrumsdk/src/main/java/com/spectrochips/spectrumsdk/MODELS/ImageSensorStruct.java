package com.spectrochips.spectrumsdk.MODELS;

/**
 * Created by wave on 12/10/2018.
 */

public class ImageSensorStruct {

    int[] ROI;
    int exposureTime;
    int exposureMinTime;
    int exposureMaxTime;

    int analogGain;
    int analogGainMinTime;
    int analogGainMaxTime ;

    double digitalGain;
    double digitalGainMinValue;
    double digitalGainMaxValue;

    int noOfAverage;
    int noOfAverageMin;
    int noOfAverageMax;

    int noOfAverageForDarkSpectrum;
    int noOfAverageMinForDarkSpectrum;
    int noOfAverageMaxForDarkSpectrum;

    public int[] getROI() {
        return ROI;
    }

    public void setROI(int[] ROI) {
        this.ROI = ROI;
    }

    public int getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(int exposureTime) {
        this.exposureTime = exposureTime;
    }

    public int getExposureMinTime() {
        return exposureMinTime;
    }

    public void setExposureMinTime(int exposureMinTime) {
        this.exposureMinTime = exposureMinTime;
    }

    public int getExposureMaxTime() {
        return exposureMaxTime;
    }

    public void setExposureMaxTime(int exposureMaxTime) {
        this.exposureMaxTime = exposureMaxTime;
    }

    public int getAnalogGain() {
        return analogGain;
    }

    public void setAnalogGain(int analogGain) {
        this.analogGain = analogGain;
    }

    public int getAnalogGainMinTime() {
        return analogGainMinTime;
    }

    public void setAnalogGainMinTime(int analogGainMinTime) {
        this.analogGainMinTime = analogGainMinTime;
    }

    public int getAnalogGainMaxTime() {
        return analogGainMaxTime;
    }

    public void setAnalogGainMaxTime(int analogGainMaxTime) {
        this.analogGainMaxTime = analogGainMaxTime;
    }

    public double getDigitalGain() {
        return digitalGain;
    }

    public void setDigitalGain(double digitalGain) {
        this.digitalGain = digitalGain;
    }

    public double getDigitalGainMinValue() {
        return digitalGainMinValue;
    }

    public void setDigitalGainMinValue(double digitalGainMinValue) {
        this.digitalGainMinValue = digitalGainMinValue;
    }

    public double getDigitalGainMaxValue() {
        return digitalGainMaxValue;
    }

    public void setDigitalGainMaxValue(double digitalGainMaxValue) {
        this.digitalGainMaxValue = digitalGainMaxValue;
    }

    public int getNoOfAverage() {
        return noOfAverage;
    }

    public void setNoOfAverage(int noOfAverage) {
        this.noOfAverage = noOfAverage;
    }

    public int getNoOfAverageMin() {
        return noOfAverageMin;
    }

    public void setNoOfAverageMin(int noOfAverageMin) {
        this.noOfAverageMin = noOfAverageMin;
    }

    public int getNoOfAverageMax() {
        return noOfAverageMax;
    }

    public void setNoOfAverageMax(int noOfAverageMax) {
        this.noOfAverageMax = noOfAverageMax;
    }

    public int getNoOfAverageForDarkSpectrum() {
        return noOfAverageForDarkSpectrum;
    }

    public void setNoOfAverageForDarkSpectrum(int noOfAverageForDarkSpectrum) {
        this.noOfAverageForDarkSpectrum = noOfAverageForDarkSpectrum;
    }

    public int getNoOfAverageMinForDarkSpectrum() {
        return noOfAverageMinForDarkSpectrum;
    }

    public void setNoOfAverageMinForDarkSpectrum(int noOfAverageMinForDarkSpectrum) {
        this.noOfAverageMinForDarkSpectrum = noOfAverageMinForDarkSpectrum;
    }

    public int getNoOfAverageMaxForDarkSpectrum() {
        return noOfAverageMaxForDarkSpectrum;
    }

    public void setNoOfAverageMaxForDarkSpectrum(int noOfAverageMaxForDarkSpectrum) {
        this.noOfAverageMaxForDarkSpectrum = noOfAverageMaxForDarkSpectrum;
    }
}
