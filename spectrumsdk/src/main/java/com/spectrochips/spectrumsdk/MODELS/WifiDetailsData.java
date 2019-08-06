package com.spectrochips.spectrumsdk.MODELS;

/**
 * Created by wave on 12/10/2018.
 */

public class WifiDetailsData {
    String ssid;
    String password;
    String iPAddress;
    int port;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getiPAddress() {
        return iPAddress;
    }

    public void setiPAddress(String iPAddress) {
        this.iPAddress = iPAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
