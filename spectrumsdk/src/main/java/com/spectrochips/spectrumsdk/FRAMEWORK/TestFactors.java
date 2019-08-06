package com.spectrochips.spectrumsdk.FRAMEWORK;

/**
 * Created by ADMIN on 15-05-2019.
 */

public class TestFactors {
    String SNo;
    String testname;
    String value;
    String units;
    String referenceRange;
    boolean flag;
    String result;

    public String getSNo() {
        return SNo;
    }

    public void setSNo(String SNo) {
        this.SNo = SNo;
    }

    public String getTestname() {
        return testname;
    }

    public void setTestname(String testname) {
        this.testname = testname;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
