package com.spectrochips.spectrumsdk.MODELS;

import java.util.ArrayList;

/**
 * Created by wave on 12/10/2018.
 */

public class SpectorDeviceDataStruct {
    String modifiedDate;
    ArrayList<DeviceInformationStruct> deviceInformation;
    int[] spectrumDisplayRegionInPixel;
    int[] spectrumDisplayRegionInWavelength;
    int[] baselineRegionInPixel;
    int[] baselineRegionInWavelength;
    ImageSensorStruct imageSensor ;
    StripMeasurmentStruct stripMeasurment;
    WavelengthCalibrationData wavelengthCalibration;
    WifiDetailsData wifiDetails;
    StripControl stripControl;

    ArrayList<RCTableData> RCTable;
    ArrayList<LEDInfoData> lEDInfo;

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public ArrayList<DeviceInformationStruct> getDeviceInformation() {
        return deviceInformation;
    }

    public void setDeviceInformation(ArrayList<DeviceInformationStruct> deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public int[] getSpectrumDisplayRegionInPixel() {
        return spectrumDisplayRegionInPixel;
    }

    public void setSpectrumDisplayRegionInPixel(int[] spectrumDisplayRegionInPixel) {
        this.spectrumDisplayRegionInPixel = spectrumDisplayRegionInPixel;
    }

    public int[] getSpectrumDisplayRegionInWavelength() {
        return spectrumDisplayRegionInWavelength;
    }

    public void setSpectrumDisplayRegionInWavelength(int[] spectrumDisplayRegionInWavelength) {
        this.spectrumDisplayRegionInWavelength = spectrumDisplayRegionInWavelength;
    }

    public int[] getBaselineRegionInPixel() {
        return baselineRegionInPixel;
    }

    public void setBaselineRegionInPixel(int[] baselineRegionInPixel) {
        this.baselineRegionInPixel = baselineRegionInPixel;
    }

    public int[] getBaselineRegionInWavelength() {
        return baselineRegionInWavelength;
    }

    public void setBaselineRegionInWavelength(int[] baselineRegionInWavelength) {
        this.baselineRegionInWavelength = baselineRegionInWavelength;
    }

    public ImageSensorStruct getImageSensor() {
        return imageSensor;
    }

    public void setImageSensor(ImageSensorStruct imageSensor) {
        this.imageSensor = imageSensor;
    }

    public WavelengthCalibrationData getWavelengthCalibration() {
        return wavelengthCalibration;
    }

    public void setWavelengthCalibration(WavelengthCalibrationData wavelengthCalibration) {
        this.wavelengthCalibration = wavelengthCalibration;
    }

    public StripControl getStripControl() {
        return stripControl;
    }

    public void setStripControl(StripControl stripControl) {
        this.stripControl = stripControl;
    }

    public ArrayList<RCTableData> getRCTable() {
        return RCTable;
    }

    public void setRCTable(ArrayList<RCTableData> RCTable) {
        this.RCTable = RCTable;
    }

    public WifiDetailsData getWifiDetails() {
        return wifiDetails;
    }

    public void setWifiDetails(WifiDetailsData wifiDetails) {
        this.wifiDetails = wifiDetails;
    }

    public StripMeasurmentStruct getStripMeasurment() {
        return stripMeasurment;
    }

    public void setStripMeasurment(StripMeasurmentStruct stripMeasurment) {
        this.stripMeasurment = stripMeasurment;
    }

    public ArrayList<LEDInfoData> getlEDInfo() {
        return lEDInfo;
    }

    public void setlEDInfo(ArrayList<LEDInfoData> lEDInfo) {
        this.lEDInfo = lEDInfo;
    }
}
