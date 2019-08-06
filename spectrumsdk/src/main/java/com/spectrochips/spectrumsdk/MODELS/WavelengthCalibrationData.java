package com.spectrochips.spectrumsdk.MODELS;

/**
 * Created by wave on 12/10/2018.
 */

public class WavelengthCalibrationData {

    int noOfCoefficients;
    double[] coefficients;

    public int getNoOfCoefficients() {
        return noOfCoefficients;
    }

    public void setNoOfCoefficients(int noOfCoefficients) {
        this.noOfCoefficients = noOfCoefficients;
    }

    public double[] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(double[] coefficients) {
        this.coefficients = coefficients;
    }
}
