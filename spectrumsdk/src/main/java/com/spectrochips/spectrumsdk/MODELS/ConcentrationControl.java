package com.spectrochips.spectrumsdk.MODELS;

/**
 * Created by wave on 12/10/2018.
 */

public class ConcentrationControl {

    String SNo;
    String testItem;
    String concentration;
    String units;
    String referenceRange;


    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }


    public String getSNo() {
        return SNo;
    }

    public void setSNo(String SNo) {
        this.SNo = SNo;
    }

    public String getTestItem() {
        return testItem;
    }

    public void setTestItem(String testItem) {
        this.testItem = testItem;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
