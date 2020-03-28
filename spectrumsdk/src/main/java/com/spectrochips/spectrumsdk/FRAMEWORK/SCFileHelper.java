package com.spectrochips.spectrumsdk.FRAMEWORK;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.spectrochips.spectrumsdk.MODELS.SCFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ADMIN on 14-05-2019.
 */

public class SCFileHelper {
    private static SCFileHelper ourInstance;
        String FETCH_FILES_URL_STRING ="http://52.1.227.73:8096/spectrocare/sdkjsonfilesforengg/getallstripes";
    // String FETCH_FILES_URL_STRING ="http://54.210.61.0:8096/spectrocare/sdkjsonfilesforengg/getallstripes";
    // String FETCH_FILES_URL_STRING = "http://54.210.61.0:8096/spectrocare/sdkjsonfiles/getallstripes";
    private JSONObject filesObj;
    private DownloadJsonFiles downloadJsonFiles;
    private ArrayList<SCFile> scFileArrayList=new ArrayList<>();

    public static SCFileHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new SCFileHelper();
        }
        return ourInstance;
    }
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) SpectroCareSDK.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
    public void fetchJsonfiles(DownloadJsonFiles downloadJsonFiles1){
        this.downloadJsonFiles=downloadJsonFiles1;
    }

    public void getJsonFiles() {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    String response = makePostRequest(FETCH_FILES_URL_STRING);
                    //Log.e("response", "calll" + response);
                    return "Success";
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return "";
                }
            }

        }.execute("");
    }

    private String makePostRequest(String stringUrl) throws IOException {

        JSONObject params = new JSONObject();
        try {
            params.put("username", "viswanath3344@gmail.com");
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
                filesObj = jsonObj;
                if (jsonObj.getString("response").equals("3")){
                    JSONArray array=jsonObj.getJSONArray("files");
                    scFileArrayList.clear();
                    for (int i=0;i<array.length();i++){
                        JSONObject obj= (JSONObject) array.get(i);
                        SCFile scFile=new SCFile();
                        scFile.setAddedDate(obj.getString("addedDate"));
                        scFile.setFilename(obj.getString("filename"));
                        scFile.setId(obj.getString("id"));
                        scFile.setCategory(obj.getString("category"));
                        scFileArrayList.add(scFile);
                        if (i==array.length()-1){
                            if (downloadJsonFiles == null) {
                            }else {
                                downloadJsonFiles.onSuccessForLoadJson(scFileArrayList);
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                downloadJsonFiles.onFailureForLoadJson();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            downloadJsonFiles.onFailureForLoadJson();
        }
        uc.disconnect();
        return jsonString.toString();
    }

    public String convertTimestampTodate(String stringData) {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }

    public String loadDateFromTimeStamp(String timestamp) {
        String date = "";
        if (timestamp.contains(".")) {
            String timearray[] = timestamp.split("\\.");
            //Log.e("timearray", "call" + timearray[0]);
            date = convertTimestampTodate(timearray[0]);
        } else {
            date = timestamp;
        }
        return date;
    }
    public interface DownloadJsonFiles {
        void onSuccessForLoadJson(ArrayList<SCFile> list);
        void onFailureForLoadJson();
    }
}
