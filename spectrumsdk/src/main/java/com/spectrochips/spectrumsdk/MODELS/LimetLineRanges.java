package com.spectrochips.spectrumsdk.MODELS;

/**
 * Created by wave on 12/11/2018.
 */

public class LimetLineRanges {
    int sno;
    String lineSymbol;
    double CMinValue;
    double CMaxValue;
    double rMinValue;
    double rMaxValue;

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public String getLineSymbol() {
        return lineSymbol;
    }

    public void setLineSymbol(String lineSymbol) {
        this.lineSymbol = lineSymbol;
    }

    public double getCMinValue() {
        return CMinValue;
    }

    public void setCMinValue(double CMinValue) {
        this.CMinValue = CMinValue;
    }

    public double getCMaxValue() {
        return CMaxValue;
    }

    public void setCMaxValue(double CMaxValue) {
        this.CMaxValue = CMaxValue;
    }

    public double getrMinValue() {
        return rMinValue;
    }

    public void setrMinValue(double rMinValue) {
        this.rMinValue = rMinValue;
    }

    public double getrMaxValue() {
        return rMaxValue;
    }

    public void setrMaxValue(double rMaxValue) {
        this.rMaxValue = rMaxValue;
    }
}
