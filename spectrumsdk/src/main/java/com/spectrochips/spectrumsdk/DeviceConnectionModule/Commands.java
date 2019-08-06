package com.spectrochips.spectrumsdk.DeviceConnectionModule;

/**
 * Created by wave on 10/5/2018.
 */

public class Commands {
    // LED Commands
    public static String START_TAG = "$";
    public static String END_TAG = "#";

    public static String LED_TURN_ON = "$SWL1#";
    public static String LED_TURN_OFF = "$SWL0#";

    public static String EXPOUSURE_TAG = "ELC";
    public static String AVG_FRAME_TAG = "AFC";
    public static String DIGITAL_GAIN_TAG = "GNV";
    public static String ANALOG_GAIN_TAG = "AGN";

    public static String MOVE_STRIP_CLOCKWISE_TAG = "MRS";
    public static String MOVE_STRIP_COUNTER_CLOCKWISE_TAG = "MLS";

    public static String MOVE_STRIP_POSITION = "MSP";

    public static String INTESITY_VALUES_TAG = "$CAL#";

    public static double DIGITALGAIN_CONST_VALUE = 0.03125;
    public static String ROI_TAG = "ROI";

    public static String WIFI_INFO_CHANGE_TAG = "$APC@SSID@PWD@PASSWORD@!";
}
