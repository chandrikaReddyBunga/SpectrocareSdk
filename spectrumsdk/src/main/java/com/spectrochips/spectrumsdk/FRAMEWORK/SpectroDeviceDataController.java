package com.spectrochips.spectrumsdk.FRAMEWORK;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.AsyncTask;

import com.spectrochips.spectrumsdk.MODELS.DeviceInformationStruct;
import com.spectrochips.spectrumsdk.MODELS.ImageSensorStruct;
import com.spectrochips.spectrumsdk.MODELS.LEDInfoData;
import com.spectrochips.spectrumsdk.MODELS.LimetLineRanges;
import com.spectrochips.spectrumsdk.MODELS.MeasureItemsStruct;
import com.spectrochips.spectrumsdk.MODELS.RCTableData;
import com.spectrochips.spectrumsdk.MODELS.SpectorDeviceDataStruct;
import com.spectrochips.spectrumsdk.MODELS.Steps;
import com.spectrochips.spectrumsdk.MODELS.StripControl;
import com.spectrochips.spectrumsdk.MODELS.StripMeasurmentStruct;
import com.spectrochips.spectrumsdk.MODELS.WavelengthCalibrationData;
import com.spectrochips.spectrumsdk.MODELS.WifiDetailsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Abhilash on 3/20/2019.
 */

public class SpectroDeviceDataController {

    private static SpectroDeviceDataController ourInstance;
    //public Context context;
    public SpectorDeviceDataStruct spectroDeviceObject;
    public ArrayList<Steps> motorSteps;
    String FETCH_FILES_DATA_STRING = "http://54.210.61.0:8096/spectrocare/sdkjsonfiles/fileread";


    public static SpectroDeviceDataController getInstance() {
        if (ourInstance == null) {
            ourInstance = new SpectroDeviceDataController();
        }
        return ourInstance;
    }

    public void fillContext(Context context1) {
     //   context = context1;
        spectroDeviceObject = new SpectorDeviceDataStruct();
    }
    public void setupTestParameters(final String fileNmae, final String category) {
       new AsyncTask<String, String, String>() {
           @Override
           protected String doInBackground(String... params) {
               try {
                   String response = makePostRequest(FETCH_FILES_DATA_STRING,fileNmae);
                   try {
                       JSONObject jsonObject=new JSONObject(response);
                       processJsonData(jsonObject);
                       //Log.e("setupTestParameters", "calll");
                   } catch (JSONException e) {
                       e.printStackTrace();
                       if (SCTestAnalysis.getInstance().jsonFileInterface!=null){
                           SCTestAnalysis.getInstance().jsonFileInterface.onFailureForConfigureJson("failed");
                       }
                   }
                   return "Success";
               } catch (IOException ex) {
                   ex.printStackTrace();
                   return "";
               }
           }

       }.execute("");
   }

    private String makePostRequest(String stringUrl,String fileNmae) throws IOException {

        JSONObject params = new JSONObject();
        try {//["username" : "viswa","filename":fileName]
            params.put("username", "viswa");
            params.put("filename", fileNmae);

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

        } catch (Exception ex) {
            ex.printStackTrace();
            SCTestAnalysis.getInstance().jsonFileInterface.onFailureForConfigureJson("Fail to Configure");
        }
        uc.disconnect();
        return jsonString.toString();
    }
    public void loadJsonFromUrl(String fileName){
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(fileName));
            JSONArray deviceArray = obj.getJSONArray("deviceInformation");
            JSONObject measureObj = obj.getJSONObject("stripMeasurment");
            JSONObject objImagesensor = obj.getJSONObject("imageSensor");
            JSONObject Objwavelenght = obj.getJSONObject("wavelengthCalibration");//imageSensor
            JSONObject wifiObj = obj.getJSONObject("wifiDetails");
            JSONArray objrctable = obj.getJSONArray("RCTable");
            JSONObject objMotor = obj.getJSONObject("stripControl");
            processDeviceInformation(deviceArray);
            processStripeMeasurment(measureObj);
            loadPixelArrays(obj);
            processImagesensordata(objImagesensor);
            processwavelengthCalibration(Objwavelenght);
            processWifiDetails(wifiObj);
            processMotorData(objMotor);
            processRcTableData(objrctable);
            updatedefaultMotorSteps();
            //processJsonData(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public String loadJSONFromAsset(String filename) {
        String json = null;
        InputStream is = null;
        try {
            is =  SpectroCareSDK.getInstance().context.getAssets().open(filename);
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
    public void processJsonData(JSONObject obj) throws IOException {// json file in assert folder.
        try {
            JSONArray deviceArray = obj.getJSONArray("deviceInformation");
            JSONObject measureObj = obj.getJSONObject("stripMeasurment");
            JSONObject objImagesensor = obj.getJSONObject("imageSensor");
            JSONObject Objwavelenght = obj.getJSONObject("wavelengthCalibration");//imageSensor
            JSONObject wifiObj = obj.getJSONObject("wifiDetails");
            JSONArray objrctable = obj.getJSONArray("RCTable");
            JSONObject objMotor = obj.getJSONObject("stripControl");
            //JSONArray objLed = obj.getJSONArray("lEDInfo");
            processDeviceInformation(deviceArray);
            processStripeMeasurment(measureObj);
            loadPixelArrays(obj);
            processImagesensordata(objImagesensor);
            processwavelengthCalibration(Objwavelenght);
            processWifiDetails(wifiObj);
            processMotorData(objMotor);
            processRcTableData(objrctable);

            updateMotorSteps();


        } catch (JSONException e) {
            e.printStackTrace();
            if (SCTestAnalysis.getInstance().jsonFileInterface!=null){
                SCTestAnalysis.getInstance().jsonFileInterface.onFailureForConfigureJson("failed");
            }
        }
    }

    private void processRcTableData(JSONArray jsonArray) throws JSONException {
        DecimalFormat df = new DecimalFormat("#.##");
        ArrayList<RCTableData> rcList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ArrayList<LimetLineRanges> limitArray = new ArrayList<>();
            //Log.e("jsonArray", "cal" + jsonArray.length() + jsonArray.get(i).toString());
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            JSONArray carray = jsonObject1.getJSONArray("C");
            JSONArray rarray = jsonObject1.getJSONArray("R");
            JSONArray limitrangrArray = jsonObject1.getJSONArray("limitLineRanges");
            String testItem = jsonObject1.getString("testItem");
            String units = jsonObject1.getString("unit");//referenceRange
            String referenceRange = jsonObject1.getString("referenceRange");//referenceRange
            int stripIndex = jsonObject1.getInt("stripIndex");
            String criticalwavelength = jsonObject1.getString("criticalwavelength");//numberFormat
            String numberFormat = jsonObject1.getString("numberFormat");//numberFormat

            double[] Carr = new double[carray.length()];
            for (int j = 0; j < carray.length(); j++) {
                Carr[j] = Double.parseDouble(df.format(carray.getDouble(j)));
            }

            double[] Rarr = new double[rarray.length()];
            for (int j = 0; j < rarray.length(); j++) {
                Rarr[j] = rarray.getDouble(j);
            }

            for (int index = 0; index < limitrangrArray.length(); index++) {
                JSONObject object = limitrangrArray.getJSONObject(index);
                int sno = object.getInt("sno");
                String lineSymbol = object.getString("lineSymbol");
                double cmin = object.getDouble("CMinValue");
                double cmax = object.getDouble("CMaxValue");
                double rmin = object.getDouble("rMinValue");
                double rmax = object.getDouble("rMaxValue");
                LimetLineRanges objLImit = new LimetLineRanges();
                objLImit.setSno(sno);
                objLImit.setCMinValue(cmin);
                objLImit.setCMaxValue(cmax);
                objLImit.setrMinValue(rmin);
                objLImit.setrMaxValue(rmax);
                objLImit.setLineSymbol(lineSymbol);
                limitArray.add(objLImit);
            }

            RCTableData objRc = new RCTableData();
            objRc.setTestItem(testItem);
            objRc.setStripIndex(stripIndex);
            objRc.setR(Rarr);
            objRc.setC(Carr);
            objRc.setCriticalwavelength(Double.parseDouble(criticalwavelength));
            objRc.setUnit(units);
            objRc.setNumberFormat(numberFormat);
            //Log.e("uintinrc", "cal" + objRc.getUnit());
            objRc.setReferenceRange(referenceRange);
            objRc.setLimetLineRanges(limitArray);
            rcList.add(objRc);
        }
        spectroDeviceObject.setRCTable(rcList);
    }

    private void processLedData(JSONArray ledArray) throws JSONException {
        ArrayList<LEDInfoData> ledInfoDatas = new ArrayList<>();
        for (int i = 0; i < ledArray.length(); i++) {
            //Log.e("ledArray", "cal" + ledArray.length());
            JSONObject jsonObject1 = ledArray.getJSONObject(i);
            String modifiedName = jsonObject1.getString("modifiedName");
            String originalName = jsonObject1.getString("originalName");
            int status = jsonObject1.getInt("status");

            LEDInfoData ledModel = new LEDInfoData();
            ledModel.setOriginalName(originalName);
            ledModel.setModifiedName(modifiedName);
            ledModel.setStatus(status);
            ledInfoDatas.add(ledModel);
        }
        spectroDeviceObject.setlEDInfo(ledInfoDatas);
    }

    private void processMotorData(JSONObject jsonObject) throws JSONException {
        motorSteps = new ArrayList<>();

        double stepDistanceInMM = jsonObject.getDouble("distanceFromPostionSensorToSpectroMeterInMM");
        double oppositeDirection = jsonObject.getDouble("distanceFromHolderEdgeTo1STStripInMM");
        double extraStepCount = jsonObject.getDouble("distancePerStepInMM");

        JSONArray jsonArray = jsonObject.getJSONArray("steps");
        //Log.e("jsonArray", "cal" + jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            //Log.e("jsonObject1", "cal" + jsonObject1.toString());
            int sw = jsonObject1.getInt("standardWhiteIndex");
            int dwell = jsonObject1.getInt("dwellTimeInSec");
            String direction = jsonObject1.getString("direction");
            String dis = jsonObject1.getString("distanceInMM");
            int noofsteps = jsonObject1.getInt("noOfSteps");
            String testname = jsonObject1.getString("testName");
            int stripindex = jsonObject1.getInt("stripIndex");
            int noOfAverage = jsonObject1.getInt("noOfAverage");

            Steps objMOtor = new Steps();

            objMOtor.setDirection(direction);
            objMOtor.setTestName(testname);
            objMOtor.setDwellTimeInSec(dwell);
            objMOtor.setStripIndex(stripindex);
            objMOtor.setNoOfSteps(noofsteps);
            objMOtor.setStandardWhiteIndex(sw);
            objMOtor.setNoOfAverage(noOfAverage);
            objMOtor.setDistanceInMM(Double.parseDouble(dis));
            motorSteps.add(objMOtor);

        }
        StripControl stripControl = new StripControl();
        stripControl.setSteps(motorSteps);
        stripControl.setDistanceFromHolderEdgeTo1STStripInMM(oppositeDirection);
        stripControl.setDistancePerStepInMM(extraStepCount);
        stripControl.setDistanceFromPostionSensorToSpectroMeterInMM(stepDistanceInMM);
        spectroDeviceObject.setStripControl(stripControl);

    }

    private void processDeviceInformation(JSONArray deviceInfoArray) {
        ArrayList<DeviceInformationStruct> deviceInfo = new ArrayList<DeviceInformationStruct>();
        for (int i = 0; i < deviceInfoArray.length(); i++) {
            try {
                JSONObject jsonObject1 = deviceInfoArray.getJSONObject(i);
                //Log.e("jsonObject1", "cal" + jsonObject1.toString());
                String title = jsonObject1.getString("title");
                int id = jsonObject1.getInt("id");
                String description = jsonObject1.getString("description");
                DeviceInformationStruct objInfo = new DeviceInformationStruct();
                objInfo.setId(id);
                objInfo.setDescription(description);
                objInfo.setTitle(title);
                deviceInfo.add(objInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spectroDeviceObject.setDeviceInformation(deviceInfo);
    }

    private void processStripeMeasurment(JSONObject jsonObject) throws JSONException {
        StripMeasurmentStruct objStrucut = new StripMeasurmentStruct();
        ArrayList<MeasureItemsStruct> measureList = new ArrayList<MeasureItemsStruct>();

        double stepDistanceInMM = jsonObject.getDouble("stepDistanceInMM");
        int oppositeDirection = jsonObject.getInt("stepCountForOppositeDirection");
        int extraStepCount = jsonObject.getInt("extraStepCountForEject");
        JSONArray measureItem = jsonObject.getJSONArray("measureItems");

        for (int i = 0; i < measureItem.length(); i++) {
            JSONObject jsonObject1 = measureItem.getJSONObject(i);
            //Log.e("measureItem", "cal" + jsonObject1.toString());
            String testName = jsonObject1.getString("testName");
            String distanceUnit = jsonObject1.getString("distanceUnit");
            double distance = jsonObject1.getDouble("distance");
            int steps = jsonObject1.getInt("steps");

            MeasureItemsStruct objMeasure = new MeasureItemsStruct();
            objMeasure.setTestName(testName);
            objMeasure.setDistanceUnit(distanceUnit);
            objMeasure.setDistance(distance);
            objMeasure.setSteps(steps);

            measureList.add(objMeasure);
        }
        objStrucut.setExtraStepCountForEject(extraStepCount);
        objStrucut.setStepCountForOppositeDirection(oppositeDirection);
        objStrucut.setStepDistanceInMM(stepDistanceInMM);
        objStrucut.setMeasureItems(measureList);

        spectroDeviceObject.setStripMeasurment(objStrucut);
    }

    private void loadPixelArrays(JSONObject pixelArray) throws JSONException {
        JSONArray jsonArray = pixelArray.getJSONArray("spectrumDisplayRegionInPixel");
        int[] regioInPixel = new int[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            regioInPixel[i] = jsonArray.getInt(i);
        }
        spectroDeviceObject.setSpectrumDisplayRegionInPixel(regioInPixel);

        JSONArray array = pixelArray.getJSONArray("spectrumDisplayRegionInWavelength");
        int[] regioInwavelength = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            regioInwavelength[i] = array.getInt(i);
        }
        spectroDeviceObject.setSpectrumDisplayRegionInWavelength(regioInwavelength);

        JSONArray baseline = pixelArray.getJSONArray("baselineRegionInPixel");
        int[] baselineInPixel = new int[baseline.length()];
        for (int i = 0; i < baseline.length(); i++) {
            baselineInPixel[i] = baseline.getInt(i);
        }
        spectroDeviceObject.setBaselineRegionInPixel(baselineInPixel);


        JSONArray baselineWave = pixelArray.getJSONArray("baselineRegionInWavelength");
        int[] baselineInWave = new int[baselineWave.length()];
        for (int i = 0; i < baselineWave.length(); i++) {
            baselineInWave[i] = baselineWave.getInt(i);
        }
        spectroDeviceObject.setBaselineRegionInWavelength(baselineInWave);

    }

    private void processImagesensordata(JSONObject jsonObject) {

        ImageSensorStruct objSensor = new ImageSensorStruct();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("ROI");
            int[] rOIinVertical = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                rOIinVertical[i] = jsonArray.getInt(i);
            }

            //Log.e("objSensor", "call" + jsonArray.getDouble(2) + "cdcd" + rOIinVertical.length);

            objSensor.setExposureTime(jsonObject.getInt("exposureTime"));
            objSensor.setExposureMinTime(jsonObject.getInt("exposureMinTime"));
            objSensor.setExposureMaxTime(jsonObject.getInt("exposureMaxTime"));

            objSensor.setAnalogGain(jsonObject.getInt("analogGain"));
            objSensor.setAnalogGainMinTime(jsonObject.getInt("analogGainMinTime"));
            objSensor.setAnalogGainMaxTime(jsonObject.getInt("analogGainMaxTime"));
            //
            objSensor.setNoOfAverage(jsonObject.getInt("noOfAverage"));
            objSensor.setNoOfAverageMin(jsonObject.getInt("noOfAverageMin"));
            objSensor.setNoOfAverageMax(jsonObject.getInt("noOfAverageMax"));
            //
            objSensor.setDigitalGain(jsonObject.getDouble("digitalGain"));
            objSensor.setDigitalGainMinValue(jsonObject.getDouble("digitalGainMinValue"));
            objSensor.setDigitalGainMaxValue(jsonObject.getDouble("digitalGainMaxValue"));

            objSensor.setNoOfAverageForDarkSpectrum(jsonObject.getInt("noOfAverageForDarkSpectrum"));
            objSensor.setNoOfAverageMinForDarkSpectrum(jsonObject.getInt("noOfAverageMinForDarkSpectrum"));
            objSensor.setNoOfAverageMaxForDarkSpectrum(jsonObject.getInt("noOfAverageMaxForDarkSpectrum"));

            objSensor.setROI(rOIinVertical);

            spectroDeviceObject.setImageSensor(objSensor);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processwavelengthCalibration(JSONObject jsonObject) {
        WavelengthCalibrationData objWave = new WavelengthCalibrationData();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("coefficients");
            double[] coefficient = new double[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                coefficient[i] = jsonArray.getDouble(i);
            }
            //Log.e("jsonarray", "call" + jsonArray.getDouble(2) + "cdcd" + coefficient.length);
            objWave.setCoefficients(coefficient);
            objWave.setNoOfCoefficients(jsonObject.getInt("noOfCoefficients"));

            spectroDeviceObject.setWavelengthCalibration(objWave);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processWifiDetails(JSONObject jsonObject) {
        try {
            WifiDetailsData wifiDetails = new WifiDetailsData();
            wifiDetails.setiPAddress(jsonObject.getString("iPAddress"));
            wifiDetails.setPassword(jsonObject.getString("password"));
            wifiDetails.setPort(Integer.parseInt(jsonObject.getString("port")));
            wifiDetails.setSsid(jsonObject.getString("ssid"));

            spectroDeviceObject.setWifiDetails(wifiDetails);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void updatedefaultMotorSteps() {
        for (int i = 0; i < motorSteps.size(); i++) {
            //Log.e("DirectionForTestItem", "call" + motorSteps.get(i).getTestName());
            if (calculateTheRealDistanceStpesAndDirectionForTestItem(i) != null) {  //  Calculated  Motor step success
                Steps objSteps = calculateTheRealDistanceStpesAndDirectionForTestItem(i);
                motorSteps.set(i, objSteps);
                if (i == motorSteps.size()-1){
                    //Log.e("testinggggggg", "call");
                    if (spectroDeviceObject != null) {
                        SCTestAnalysis.getInstance(). spectroDeviceObject = spectroDeviceObject;
                        SCTestAnalysis.getInstance().motorSteps=motorSteps;
                    }
                }
            }
        }
    }
    public void updateMotorSteps() {
        for (int i = 0; i < motorSteps.size(); i++) {
            //Log.e("DirectionForTestItem", "call" + motorSteps.get(i).getTestName());
            if (calculateTheRealDistanceStpesAndDirectionForTestItem(i) != null) {  //  Calculated  Motor step success
                Steps objSteps = calculateTheRealDistanceStpesAndDirectionForTestItem(i);
                motorSteps.set(i, objSteps);
                if (i == motorSteps.size()-1){
                    //Log.e("testinggggggg", "call");
                    if (spectroDeviceObject != null) {
                        SCTestAnalysis.getInstance(). spectroDeviceObject = spectroDeviceObject;
                        SCTestAnalysis.getInstance().motorSteps=motorSteps;
                        if (SCTestAnalysis.getInstance().jsonFileInterface!=null){
                            SCTestAnalysis.getInstance().jsonFileInterface.onSuccessForConfigureJson();
                        }
                    }
                }
            }
        }
        }

    public Steps calculateTheRealDistanceStpesAndDirectionForTestItem(int position) {
        Steps tempObjMotorStep = motorSteps.get(position);
        if (position == 0) {  // Handle the First object by adding required values.
            MeasureItemsStruct psToSpectroMeasureObject = getStripMeasureObjectForItem("positionSensorToSpectrometer");
            MeasureItemsStruct psToStripMeasureObject = getStripMeasureObjectForItem("stripHolderToStrip");
            MeasureItemsStruct testItemMeasureObject = getStripMeasureObjectForItem(tempObjMotorStep.getTestName());

            if (psToSpectroMeasureObject != null && psToStripMeasureObject != null && testItemMeasureObject != null) {
                int finalMotroStepValue = psToStripMeasureObject.getSteps() + testItemMeasureObject.getSteps() - psToSpectroMeasureObject.getSteps();
                double finalDistanceValue = psToStripMeasureObject.getDistance() + testItemMeasureObject.getDistance() - psToSpectroMeasureObject.getDistance();
                String direction = "CCW";
                if (finalMotroStepValue < 0) {
                    direction = "CW";
                }
                tempObjMotorStep.setDirection(direction);
                tempObjMotorStep.setNoOfSteps(Math.abs(finalMotroStepValue));// = abs(finalMotroStepValue)
                tempObjMotorStep.setDistanceInMM(Math.abs(finalDistanceValue)); //= abs(finalDistanceValue)
                //Log.e("setNoOfSteps", "call"+tempObjMotorStep.getNoOfSteps());

                return tempObjMotorStep;
            }
            return null;
        } else {
            if (position == motorSteps.size() - 1) {
                // Last Position  It's for Eject
                Steps prevMotorStep = motorSteps.get(position - 1);
                MeasureItemsStruct prevTestItemMeasureObject = getStripMeasureObjectForItem(prevMotorStep.getTestName());
                MeasureItemsStruct psToStripMeasureObject = getStripMeasureObjectForItem("stripHolderToStrip");
                double stepDistanceInMM = getDistanceForStep();
                int extraStepsForEject = getExtraStepCountForEject();

                if (prevTestItemMeasureObject != null && psToStripMeasureObject != null) {
                    double extraDistance = (double) extraStepsForEject * stepDistanceInMM;

                    int finalMotroStepValue = psToStripMeasureObject.getSteps() + prevTestItemMeasureObject.getSteps() + extraStepsForEject;
                    double finalDistanceValue = psToStripMeasureObject.getDistance() + prevTestItemMeasureObject.getDistance() + extraDistance;
                    String direction = "CW"; // Here strip is already inside , So need to clock wise direction to eject strip.

                    tempObjMotorStep.setDirection(direction);
                    tempObjMotorStep.setNoOfSteps(Math.abs(finalMotroStepValue));// = abs(finalMotroStepValue)
                    tempObjMotorStep.setDistanceInMM(Math.abs(finalDistanceValue)); //= abs(finalDistanceValue)
                    //Log.e("vccvNoOfSteps", "call"+tempObjMotorStep.getNoOfSteps());

                    return tempObjMotorStep;
                } else {
                    return null;
                }
            } else {
                Steps prevMotorStep = motorSteps.get(position - 1);
                //Log.e("prevMotorStep", "call" + prevMotorStep.getTestName());
                MeasureItemsStruct prevTestItemMeasureObject = getStripMeasureObjectForItem(prevMotorStep.getTestName());
                MeasureItemsStruct nowTestItemMeasureObject = getStripMeasureObjectForItem(tempObjMotorStep.getTestName());
                double stepDistanceInMM = getDistanceForStep();
                double motorStepsForOppositeDirection = getStepCountForOppsiteDirection();
                if (prevTestItemMeasureObject != null && nowTestItemMeasureObject != null) {//&& stepDistanceInMM != null && motorStepsForOppositeDirection != null
                    int finalMotroStepValue = nowTestItemMeasureObject.getSteps() - prevTestItemMeasureObject.getSteps();
                    double finalDistanceValue = nowTestItemMeasureObject.getDistance() - prevTestItemMeasureObject.getDistance();

                    String direction = "CCW";
                    if (finalMotroStepValue < 0) {
                        direction = "CW";
                    }
                    finalMotroStepValue = Math.abs(finalMotroStepValue);
                    finalDistanceValue = Math.abs(finalDistanceValue);
                    if (direction != prevMotorStep.getDirection()) {
                        finalMotroStepValue = finalMotroStepValue + (int) motorStepsForOppositeDirection;
                        finalDistanceValue = finalDistanceValue + motorStepsForOppositeDirection * stepDistanceInMM;
                    }
                    tempObjMotorStep.setDirection(direction);
                    tempObjMotorStep.setNoOfSteps(finalMotroStepValue);
                    tempObjMotorStep.setDistanceInMM(finalDistanceValue);
                    //Log.e("setNumberOfSteps", "call" + finalMotroStepValue);
                    return tempObjMotorStep;
                }
                return null;
            }

        }
    }

    private double getDistanceForStep() {
        return spectroDeviceObject.getStripMeasurment().getStepDistanceInMM();
    }

    private int getStepCountForOppsiteDirection() {

        return spectroDeviceObject.getStripMeasurment().getStepCountForOppositeDirection();
    }

    private int getExtraStepCountForEject() {

        return spectroDeviceObject.getStripMeasurment().getExtraStepCountForEject();
    }

    private MeasureItemsStruct getStripMeasureObjectForItem(String itemName) {

        ArrayList<MeasureItemsStruct> measureItemsSets = new ArrayList<MeasureItemsStruct>(spectroDeviceObject.getStripMeasurment().getMeasureItems());
        for (MeasureItemsStruct objMeasureItem : measureItemsSets) {
            if (objMeasureItem.getTestName().equals(itemName)) {
                return objMeasureItem;
            }
        }
        return null;
    }

}


