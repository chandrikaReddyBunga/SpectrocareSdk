package com.spectrochips.spectrumsdk.FRAMEWORK;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpectroCareSDK {
    private static SpectroCareSDK ourInstance;
    public Context context;
    private  String url="http://54.210.61.0/spectrocare/sdkapplication/keychecking";
    private  String appkey, secreatkey,packagename;
    public   boolean isSDKAccess = false;
    public  String sdkAccessMessage = "";
    public   boolean isVerifiedWithCloud;
    String errorMessage = "you are not allowed to use SDK.Please verify your app Bundle Identifier BUNDLE_ID matches with Spectrochip-services bundle Identifer PLIST-BUNDLE-ID.";
    String plistFileNotFoundMessage = "Unable to find spectrochips-services.plist file in your project. Kindly make sure spectrochips-services.plist file is added in your project";
    private SpectroSdkInterface spectroSdkInterface;

    public static SpectroCareSDK getInstance() {
        if (ourInstance == null) {
            ourInstance = new SpectroCareSDK();
        }
        return ourInstance;
    }

    public void fillContext(Context context1) {
        context = context1;
        loadJsonFromassert("BLEsample.json",context);
    }
    public void configureData(SpectroSdkInterface spectroSdkInterface1){
        this.spectroSdkInterface=spectroSdkInterface1;
        String package_name = context.getPackageName();
        //Log.e("stusauval","call"+package_name);
        if (package_name.equals(packagename)) {
            getAppData();
        }else{
            isSDKAccess = false;
            sdkAccessMessage = errorMessage.replace("BUNDLE_ID", package_name);
            sdkAccessMessage = sdkAccessMessage.replace("PLIST-BUNDLE-ID", packagename);
            spectroSdkInterface.statusCallBack(isSDKAccess,sdkAccessMessage);
        }

    }
    public void getAppData(){
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    String response = makePostRequest(url);
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        if (jsonObject.getString("response").equals("3")){
                            //Log.e("setupTestParameters", "calll"+jsonObject.toString());
                            isSDKAccess = true;
                            isVerifiedWithCloud = true;
                            sdkAccessMessage = "You are allowed to access SDK";
                            spectroSdkInterface.statusCallBack(true,"You are allowed to access SDK");
                        }else {
                                // isSDKAccess = false
                                isVerifiedWithCloud = false;
                                if (isSDKAccess == false){
//                                sdkAccessMessage = errorMessage.replacingOccurrences(of: "BUNDLE_ID", with: bundleID!)
//                                sdkAccessMessage = sdkAccessMessage.replacingOccurrences(of: "PLIST-BUNDLE-ID", with: plistBundleId!)
                                    spectroSdkInterface.statusCallBack(isSDKAccess, "Invalid Plist");
                                } else{
                                    spectroSdkInterface.statusCallBack(isSDKAccess,"Internet Connection is Disabled");
                                }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        isSDKAccess=false;
                        spectroSdkInterface.statusCallBack(isSDKAccess,"Getting error");

                    }
                    return "Success";
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return "Failed";
                }
            }

        }.execute("");
    }
    private String makePostRequest(String stringUrl) throws IOException {

        JSONObject params = new JSONObject();
        try {
            params.put("App_Key", appkey);
            params.put("Secret_Key", secreatkey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        URL url = new URL(stringUrl);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();

        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();

        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(params.toString());
        writer.close();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            try {
                JSONObject jsonObj = new JSONObject(String.valueOf(jsonString));
                //Log.e("jsonresponse","call"+jsonObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        uc.disconnect();
        return jsonString.toString();
    }
    public void loadJsonFromassert(String fileName,Context context){
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(fileName,context));
            appkey = obj.getString("App_Key");
            secreatkey = obj.getString("Secret_Key");
            packagename= obj.getString("Package_Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String loadJSONFromAsset(String filename,Context context) {
        String json = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.e("typeofjson", "call" + filename);
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    //In this interface, you can define messages, which will be send to owner.
    public interface SpectroSdkInterface {
        void statusCallBack(boolean val, String msg);
    }
}
