package com.spectrochips.spectrumsdk.MODELS;

import java.util.ArrayList;

/**
 * Created by wave on 12/10/2018.
 */

public class RCTableData {

    int stripIndex;
    String testItem ;
    String unit;
    double criticalwavelength;
    double[] R;
    double[]  C;
    String referenceRange;

    String numberFormat;
    ArrayList<LimetLineRanges> limetLineRanges;

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }

    public ArrayList<LimetLineRanges> getLimetLineRanges() {
        return limetLineRanges;
    }

    public void setLimetLineRanges(ArrayList<LimetLineRanges> limetLineRanges) {
        this.limetLineRanges = limetLineRanges;
    }
    public int getStripIndex() {
        return stripIndex;
    }

    public void setStripIndex(int stripIndex) {
        this.stripIndex = stripIndex;
    }

    public String getTestItem() {
        return testItem;
    }

    public void setTestItem(String testItem) {
        this.testItem = testItem;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getCriticalwavelength() {
        return criticalwavelength;
    }

    public void setCriticalwavelength(double criticalwavelength) {
        this.criticalwavelength = criticalwavelength;
    }

    public double[] getR() {
        return R;
    }

    public void setR(double[] r) {
        R = r;
    }

    public double[] getC() {
        return C;
    }

    public void setC(double[] c) {
        C = c;
    }
}
