package com.spectrochips.spectrumsdk.FRAMEWORK;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.spectrochips.spectrumsdk.DeviceConnectionModule.Commands;
import com.spectrochips.spectrumsdk.DeviceConnectionModule.DataPoint;
import com.spectrochips.spectrumsdk.DeviceConnectionModule.PolynomialRegression;
import com.spectrochips.spectrumsdk.MODELS.ConcentrationControl;
import com.spectrochips.spectrumsdk.MODELS.ImageSensorStruct;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;
import com.spectrochips.spectrumsdk.MODELS.LimetLineRanges;
import com.spectrochips.spectrumsdk.MODELS.RCTableData;
import com.spectrochips.spectrumsdk.MODELS.ReflectanceChart;
import com.spectrochips.spectrumsdk.MODELS.SpectorDeviceDataStruct;
import com.spectrochips.spectrumsdk.MODELS.Steps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by ADMIN on 14-05-2019.
 */

public class SCTestAnalysis {
    private static SCTestAnalysis ourInstance;
    //public Context context;
    public  ArrayList<Steps> motorSteps;
    private String darkSpectrumTitle = "Dark Spectrum";
    private String standardWhiteTitle = "Standard White (Reference)";
    public SpectorDeviceDataStruct spectroDeviceObject;
    private ArrayList<IntensityChart> intensityChartsArray = new ArrayList<>();
    private ArrayList<Float> pixelXAxis = new ArrayList<>();
    private  ArrayList<Float> wavelengthXAxis = new ArrayList<>();
    private ArrayList<ReflectanceChart> reflectenceChartsArray = new ArrayList<>();
    private  ArrayList<ConcentrationControl> concentrationArray = new ArrayList<>();
    private ArrayList<Float> darkSpectrumIntensityArray = new ArrayList<>();
    private ArrayList<Float> standardWhiteIntensityArray = new ArrayList<>();

    private  int stripNumber = 0;
    private  boolean isForDarkSpectrum = false;
    private  boolean isForSync = false;
    private  int commandNumber = 0;
    private  String requestCommand = "";

    private ByteArrayOutputStream outputStream;
    private  ArrayList<Float> intensityArray;
    private ArrayList<String> hexaDecimalArray;// For Hexadecimal values , Does't need in real time. It's for testing purpose only.
    private byte[] socketresponseData;
    private  String receivedData = "";
    public UartService mService = null;
    private int cal_c = 0;
    private  ArrayList<TestFactors> testItems = new ArrayList<>();
    private TeststaResultInterface testAnalysisListener;
    private SyncingInterface syncingInterface;
    public JsonFileInterface jsonFileInterface;
    private AbortInterface abortInterface;
    private  boolean isEjectType = false;
    private  boolean isInterrupted = false;

    public static SCTestAnalysis getInstance() {
        if (ourInstance == null) {
            ourInstance = new SCTestAnalysis();
            ourInstance.outputStream = new ByteArrayOutputStream();
            ourInstance.hexaDecimalArray = new ArrayList<>();
        }
        return ourInstance;
    }

    public void fillContext(Context context1) {
       // context = context1;
        ourInstance.intensityArray = new ArrayList<>();
        spectroDeviceObject = new SpectorDeviceDataStruct();
        loadDefaultSpectrodeviceObject();
    }

    private void loadDefaultSpectrodeviceObject() {
        SpectroDeviceDataController.getInstance().loadJsonFromUrl("no5_3518_urineTest.json");
        if (SpectroDeviceDataController.getInstance().spectroDeviceObject != null) {
            spectroDeviceObject = SpectroDeviceDataController.getInstance().spectroDeviceObject;
            motorSteps = spectroDeviceObject.getStripControl().getSteps();
            //Log.e("loadDefaultSpect", "call" + motorSteps.size());
        }
    }
    public void activatenotifications(JsonFileInterface jsonFileInterface1){
        this.jsonFileInterface=jsonFileInterface1;
    }
    public void getDeviceSettings(String testName, String category, String date,JsonFileInterface jsonFileInterface1) {
        this.jsonFileInterface=jsonFileInterface1;
        SpectroDeviceDataController.getInstance().setupTestParameters(testName, category);
        /*if (SpectroDeviceDataController.getInstance().spectroDeviceObject != null) {
            spectroDeviceObject = SpectroDeviceDataController.getInstance().spectroDeviceObject;
            motorSteps = spectroDeviceObject.getStripControl().getSteps();
            //Log.e("success", "call" + motorSteps.size());
        }*/
    }

    public void startTestAnalysis(TeststaResultInterface teststaResultInterface1) {
        this.testAnalysisListener = teststaResultInterface1;
        stripNumber = 0;
        SCConnectionHelper.getInstance().prepareCommandForMoveToPosition();

    }
    private void getDarkSpectrum() {
        clearPreviousTestResulsArray();
        loadPixelArray();
        reprocessWavelength();
        prepareChartsDataForIntensity();
        isForDarkSpectrum = true;
        getIntensity();
    }
   /* private void getDarkSpectrum() {
        //Log.e("getDarkSpectrum", "call");
        isForDarkSpectrum = true;
        getIntensity();
    }
   */
    private void getIntensity() {
        requestCommand = Commands.INTESITY_VALUES_TAG;
        //Log.e("getIntensity", "call" + requestCommand);
        SendString(requestCommand);
    }

    private void loadPixelArray() {
        pixelXAxis = new ArrayList<>();
        pixelXAxis.clear();
        int roiArray[] = spectroDeviceObject.getImageSensor().getROI();
        int pixelCount = roiArray[1];
        //Log.e("pixelcount", "call" + pixelCount);

        for (int i = 1; i <= pixelCount; i++) {
            pixelXAxis.add(Float.valueOf(i));
        }
        //Log.e("pixelcountarray", "call" + pixelXAxis.toString());

    }

    private void reprocessWavelength() {
        //reprocess wavelength calculation
        for (int i = 0; i < pixelXAxis.size(); i++) {
            wavelengthXAxis.add(pixelXAxis.get(i));
        }
        //Log.e("reprocessWavelength", "call" + wavelengthXAxis.toString());
        wavelengthXAxis.clear();

        if (spectroDeviceObject.getWavelengthCalibration() != null) {
            double[] resultArray = spectroDeviceObject.getWavelengthCalibration().getCoefficients();
            DataPoint theData[] = new DataPoint[0];
            PolynomialRegression poly = new PolynomialRegression(theData, spectroDeviceObject.getWavelengthCalibration().getNoOfCoefficients());
            poly.fillMatrix();
            DecimalFormat df = new DecimalFormat("#.##");
            for (int index = 0; index < pixelXAxis.size(); index++) {
                wavelengthXAxis.add(Float.valueOf(df.format(poly.predictY(resultArray, pixelXAxis.get(index)) * 100 / 100)));
            }
            //Log.e("reprocessWavelength", "call" + wavelengthXAxis.toString());
        }

    }

    private void prepareChartsDataForIntensity() {
        intensityArray.clear();
        intensityChartsArray.clear();
        if (spectroDeviceObject.getRCTable() != null) {
            for (RCTableData objRc : spectroDeviceObject.getRCTable()) {
                IntensityChart objIntensity = new IntensityChart();
                objIntensity.setTestName(objRc.getTestItem());
                //Log.e("ssss", "" + objIntensity.getTestName());
                objIntensity.setPixelMode(true);
                objIntensity.setOriginalMode(true);
                objIntensity.setAutoMode(true);
                objIntensity.setxAxisArray(pixelXAxis);
                objIntensity.setyAxisArray(null);
                objIntensity.setSubstratedArray(null);
                objIntensity.setWavelengthArray(wavelengthXAxis);
                objIntensity.setCriticalWavelength(objRc.getCriticalwavelength());
                intensityChartsArray.add(objIntensity);

            }
        }
        // If needed to show dark spectrum and Standard White spectrum Then use below methods

        IntensityChart objSWIntensity = new IntensityChart();
        objSWIntensity.setTestName(standardWhiteTitle);
        objSWIntensity.setPixelMode(true);
        objSWIntensity.setOriginalMode(true);
        objSWIntensity.setAutoMode(true);
        objSWIntensity.setxAxisArray(pixelXAxis);
        objSWIntensity.setyAxisArray(null);
        objSWIntensity.setSubstratedArray(null);
        objSWIntensity.setWavelengthArray(wavelengthXAxis);
        objSWIntensity.setCriticalWavelength(0.0);
        intensityChartsArray.add(objSWIntensity);

        IntensityChart objDarkIntensity = new IntensityChart();
        objDarkIntensity.setTestName(darkSpectrumTitle);
        objDarkIntensity.setPixelMode(true);
        objDarkIntensity.setOriginalMode(true);
        objDarkIntensity.setAutoMode(true);
        objDarkIntensity.setxAxisArray(pixelXAxis);
        objDarkIntensity.setyAxisArray(null);
        objDarkIntensity.setSubstratedArray(null);
        objDarkIntensity.setWavelengthArray(wavelengthXAxis);
        objDarkIntensity.setCriticalWavelength(0.0);
        intensityChartsArray.add(objDarkIntensity);
    }

    public void clearPreviousTestResulsArray() {
        intensityChartsArray.clear();
        reflectenceChartsArray.clear();
        concentrationArray.clear();
        stripNumber = 0;
        isForDarkSpectrum = false;
        isForSync = false;
    }

    public void initializeService() {
        Intent bindIntent = new Intent(SpectroCareSDK.getInstance().context, UartService.class);
        SpectroCareSDK.getInstance().context.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(SpectroCareSDK.getInstance().context).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }
    public void removereceiver(){
        LocalBroadcastManager.getInstance(SpectroCareSDK.getInstance().context).unregisterReceiver(UARTStatusChangeReceiver);
    }

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                // showMessage("Device Connected.");
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                // showMessage("Device Disconnected.");
            }
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                //   //Log.e("Received Bytes", "" + txValue.length + byteArrayToByteString(txValue));
                try {
                    if (txValue.length > 0) {
                        String text = new String(txValue, "UTF-8");
                        if (text.length() > 0) {
                            if (text.contains("^2560#")) {
                                cal_c = 0;
                                cal_c = txValue.length - 6;
                            } else if (text.contains("^EOF#") || text.contains("EOF#") || text.contains("OF#") || text.contains("F#")) {
                                cal_c += (txValue.length - 5);
                                //Log.e("RESPONSE", "Total bytes = " + cal_c);
                            } else {
                                cal_c += txValue.length;
                            }
                            socketDidReceiveMessage(txValue, requestCommand);
                        }
                    }
                } catch (Exception e) {
                    //Log.e("exception", e.toString());
                }
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
            }
        }
    };

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d("", "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                //Log.e("", "Unable to initialize Bluetooth");
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnectWithPeripheral(mDevice);
            mService = null;
        }
    };

    private void socketDidReceiveMessage(byte[] data, String request) {
        //Log.e("datacount", "call" + data.length + "ccccccccccccc" + request);
        // //Log.e("hexStringreceivedData", "call" + rawBuffer2Hex(data));

        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        receivedData = receivedData + rawBuffer2Hex(data);

        // 5e45525223 - ^ERR#
        if (receivedData.startsWith("5e45525223")) {
            receivedData = "";
            socketresponseData = outputStream.toByteArray();
            dataRecieved(socketresponseData, request);
            outputStream.reset();
            socketresponseData = null;
            //Log.e("^ERR# Data Recieved", "call");
        }
        //244f4b23 - $OK#
        if (receivedData.startsWith("244f4b23") || receivedData.startsWith("244f4b21") || receivedData.startsWith("5e4f4b23")) {
            receivedData = "";
            socketresponseData = outputStream.toByteArray();
            dataRecieved(outputStream.toByteArray(), request);
            outputStream.reset();
            socketresponseData = null;
            //Log.e("$OK# Data Recieved", "call");

        }
        if (receivedData.startsWith("2445525223")) {
            receivedData = "";
            socketresponseData = outputStream.toByteArray();
            dataRecieved(outputStream.toByteArray(), request);
            outputStream.reset();
            socketresponseData = null;
        }

        // 2423 - $# - invalid data
        if (receivedData.startsWith("2423")) {
            receivedData = "";
            outputStream.reset();
            socketresponseData = null;
            //swal("Error", "Cofiguration error!", "error");
        }
        // Position Sensor success response  ^POS#
        if (receivedData.startsWith("5e504f5323") || receivedData.startsWith("24504f5323")) {
            //Log.e("poscall", "call" + receivedData);
            receivedData = "";
            socketresponseData = outputStream.toByteArray();
            dataRecieved(outputStream.toByteArray(), request);
            outputStream.reset();
            socketresponseData = null;
        }
        if (receivedData.startsWith("5e53545023") || (receivedData.startsWith("2453545023"))) {
            receivedData = "";
            socketresponseData = outputStream.toByteArray();
            dataRecieved(outputStream.toByteArray(), request);
            outputStream.reset();
            socketresponseData = null;
        }

        // //Log.e("Intesnisty", "call" + outputStream.toByteArray().length);
        //notify graph processing only when we got complete data 5e3235363023
        if (receivedData.startsWith("5e") && receivedData.endsWith("5e454f4623")) {
            if (request.equals(Commands.INTESITY_VALUES_TAG)) {
                receivedData = "";
                socketresponseData = outputStream.toByteArray();
                intensityDataRecieved(outputStream.toByteArray(), request);
                outputStream.reset();
                socketresponseData = null;
                //Log.e("Intesnisty received", "Intesnisty Data Recieved");
            }

        }
    }
    private void intensityDataRecieved(byte[] responseData, String request) {
        //Log.e("dataRecieved", "call" + request + responseData.length);
        if (request.equals(Commands.INTESITY_VALUES_TAG)) {
            //Log.e("stripnumber", "call" + stripNumber);
            if (processIntensityValues(responseData)) {
                if (!isForDarkSpectrum) {
                    performMotorStepsFunction();
                } else {
                    isForDarkSpectrum = false;
                }
            } else {
                //// Data mismatch,  Resend for Intensity getting.
                //Log.e("processIntensityfalse", "calll");
                requestCommand = "";
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getIntensity();
                    }
                }, 2000 * 1);
            }
        }
    }

    private void dataRecieved(byte[] responseData, String request) {
        //Log.e("dataRecieved", "call" + request + responseData);
        processResponseData(request, responseData);
    }

    private void processResponseData(String request, byte[] byteArray) {
        String decodeStr = decodeUTF8(byteArray);
        //Log.e("DeviceData", "Called Response" + request + ":" + decodeStr);
        if (decodeStr.contains("OK")) {
            if (isForSync) {
                if (isInterrupted){
                    if (abortInterface != null){
                        abortInterface.onAbortForTesting(true);
                        abortInterface=null;
                        clearCache();
                    }
                    syncDone();
                    return;
                }
                switch (commandNumber) {
                    case 1:
                        SCConnectionHelper.getInstance().sendExposureTime();
                        break;
                    case 2:
                        SCConnectionHelper.getInstance().sendSpectrumAVG();
                        break;
                    case 3:
                        SCConnectionHelper.getInstance().sendDigitalGain();
                        break;
                    case 4:
                        SCConnectionHelper.getInstance().sendAnanlogGain();
                        break;
                    case 5:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getDarkSpectrum();
                            }
                        }, 1000 * 1);

                        break;
                    default:
                        syncDone();
                        syncingInterface.isSyncingCompleted(false);
                        break;

                }
                commandNumber = commandNumber + 1;
            } else {
                if (request.equals(Commands.LED_TURN_ON)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isInterrupted) {
                                stripNumber = 0;
                                performMotorStepsFunction();
                            }
                        }
                    }, 2000 * 1);
                }
                if (isEjectType){ // Call when Testing is complete
                    abortInterface.onAbortForTesting(true);
                    abortInterface=null;
                    clearCache();
                    SCConnectionHelper.getInstance().ejectStripCommand();
                    clearPreviousTestResulsArray();
                }
            }
        } else if (decodeStr.contains("POS")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ledControl(true);
                }
            }, 2000 * 1);
        } else if (decodeStr.contains("STP")) {
            if (isInterrupted) {
                if (isEjectType) {// Call when eject completed. .
                    isEjectType=false;
                    isInterrupted=false;
                    if (abortInterface != null){
                        abortInterface.onAbortForTesting(true);
                    }
                }
            }  else {
                if (stripNumber != motorSteps.size() - 1) {
                    int dwellTime = motorSteps.get(stripNumber).getDwellTimeInSec();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestCommand = "";
                            stripNumber += 1;
                            getIntensity();
                        }
                    }, dwellTime * 1000);
                } else {
                    stripNumber = 0;
                    ledControl(false);
                }
            }
        } else if (decodeStr.contains("ERR")) {
            if (isInterrupted){
                if (isEjectType){ // Call when eject used.
                    isEjectType = false;
                    isInterrupted = false;
                    abortInterface.onAbortForTesting(true);
                }
            }else {
                if  (isForSync){
                    isForSync = false;
                    clearCache();
                    syncingInterface.isSyncingCompleted(false);
                }else{
                    testAnalysisListener.onFailureForTesting("Testing Fail.");
                   // clearPreviousTestResulsArray();
                }
            }
        }
    }

    public void unRegisterReceiver(){
        //Log.e("unRegisterReceiver", "call");
        isForSync = false;
        if (isInterrupted&&isEjectType){
            mService.disconnect();
        }
        isInterrupted=false;
        isEjectType=false;
        clearCache();
        testAnalysisListener =null;
        clearPreviousTestResulsArray();
        LocalBroadcastManager.getInstance(SpectroCareSDK.getInstance().context).unregisterReceiver(UARTStatusChangeReceiver);
    }


    private String rawBuffer2Hex(byte[] buf) {
        String str = "";

        for (int i = 0; i < buf.length; i++) {
            ////Log.e("obj","call"+buf[i]);
            String immedidateData = String.format("%02x", buf[i] & 0xff);

            if (immedidateData.length() == 1) {
                immedidateData = "0" + immedidateData;
            }
            str = str + immedidateData;
        }
        return str;
    }

    public void clearCache() {
        receivedData = "";
        socketresponseData = null;
    }

    private void syncDone() {
        //Log.e("syncDone", "call");
        commandNumber = 0;
        isForSync = false;
        requestCommand = "";
        clearCache();
    }

    public void SendString(final String message) {
        //Log.e("SendString", "Sent: " + message);
        requestCommand = message;
        byte[] value;
        try {
            //send data to service
            value = message.getBytes("UTF-8");

            int len = value.length;
            int index = 0;

            while (len > 0) {

                int packet_len;
                packet_len = (len >= 20) ? 20 : len;
                byte[] packet = new byte[packet_len];
                System.arraycopy(value, index, packet, 0, packet_len);

                mService.writeRXCharacteristic(packet);

                len -= packet_len;
                index += packet_len;

            }

            //Log.e("RESPONSE", "Sent: " + message);
//                    mService.writeRXCharacteristic(value);
            //Update the log with time stamp
            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private boolean processIntensityValues(byte[] data) {
        //Log.e("processIntensityValues", "call" + data.length);

        hexaDecimalArray = new ArrayList<>();
        intensityArray = new ArrayList<>();
        byte[] responseData = data;

        byte[] filteredByteArray = Arrays.copyOfRange(responseData, 6, responseData.length - 5);
        responseData = filteredByteArray;
        int startIndex = 0;
        int readingBytesCount = 2;  // reading every 2 bytes
        //Log.e("Data mismatched false", "call" + responseData.length + pixelXAxis.size());

        if (responseData.length / readingBytesCount != pixelXAxis.size()) {
            //Log.e("Data mismatched false", "call");
            return false;
        }

        while (responseData.length - readingBytesCount >= startIndex) {

            byte[] twoBytesData = Arrays.copyOfRange(responseData, startIndex, startIndex + readingBytesCount); // Getting two bytes and creating data

            String twobytesHexaString = bytesToHexString(twoBytesData); // Converting Data to heaxdecimalString
            hexaDecimalArray.add(twobytesHexaString); // Adding to hexa decimal array . It's for testing purpose only.
            //    //Log.e("hexaDecimalArray", "call" + hexaDecimalArray.toString());// For testing purpose
            float intensityValue = hex2decimal(twobytesHexaString); // Converting hexadecimal to decimal
            intensityArray.add(intensityValue); // Adding to intensity array
            //  //Log.e("intensityArray", "call" + intensityArray.toString());// For testing purpose
            //   //Log.e("intensityArraySize", "call" + intensityArray.size());// For testing purpose
            startIndex = startIndex + readingBytesCount; // Increasing starting index for read next two bytes.
        }
        //Log.e("intensityArrayarray", "call" + intensityArray.toString());

        if (isForDarkSpectrum) {
            darkSpectrumIntensityArray = intensityArray;
            //Log.e("intensityArrayfordark", "call" + darkSpectrumIntensityArray.toString());
            if (getPositionForTilte(darkSpectrumTitle) != -1) {
                int position = getPositionForTilte(darkSpectrumTitle);
                IntensityChart object = intensityChartsArray.get(position);
                object.setyAxisArray(darkSpectrumIntensityArray);
                intensityChartsArray.set(position, object);
                if (syncingInterface !=null){
                    syncingInterface.gettingDarkSpectrum(true);
                    syncDone();
                }
            }
        } else {
            setIntensityArrayForTestItem();
            if (stripNumber == motorSteps.size()-1) {  // Before Eject command , Process the Testing completed command.
                testCompleted();
                showMessage("Testing completed.");
                testAnalysisListener.onSuccessForTestComplete(testItems,"Testing completed.");
            }
        }
        return true;
    }

    private void setIntensityArrayForTestItem() {
        Steps currentObject = motorSteps.get(stripNumber - 1);
        if (currentObject.getStandardWhiteIndex() == 0) {
            for (int i = 0; i < intensityChartsArray.size(); i++) {
                IntensityChart object = intensityChartsArray.get(i);
                if (object.getTestName().equals(currentObject.getTestName())) {
                    object.setyAxisArray(intensityArray);
                    //Log.e("intensityArray", "call" + intensityArray.toString());
                    //Log.e("darkSpectrumArray", "call" + darkSpectrumIntensityArray.toString());
                    object.setSubstratedArray(getSubstratedArray(intensityArray, darkSpectrumIntensityArray));
                    intensityChartsArray.set(i, object);
                }
            }
        } else {
            //Log.e("elseintensityArray", "call" + intensityArray.toString());
            standardWhiteIntensityArray = intensityArray;
            int position = getPositionForTilte(standardWhiteTitle);
            IntensityChart object = intensityChartsArray.get(position);
            object.setyAxisArray(standardWhiteIntensityArray);
            object.setSubstratedArray(getSubstratedArray(standardWhiteIntensityArray, darkSpectrumIntensityArray));
            intensityChartsArray.set(position, object);
        }

        if (stripNumber == motorSteps.size() - 1) {
            //Log.e("testingended", "call");
            // Testing ended.
        }
    }

    private ArrayList<Float> getSubstratedArray(ArrayList<Float> spectrumIntensityArray, ArrayList<Float> darkSpectrumIntensityArray) {
        ArrayList<Float> substratedArray = new ArrayList<>();
        for (int i = 0; i < spectrumIntensityArray.size(); i++) {
            substratedArray.add(spectrumIntensityArray.get(i) - darkSpectrumIntensityArray.get(i));
        }
        //Log.e("substratedArray", "call" + substratedArray.toString());
        return substratedArray;
    }

    private int getPositionForTilte(String title) {
        for (int i = 0; i < intensityChartsArray.size(); i++) {
            IntensityChart object = intensityChartsArray.get(i);
            if (object.getTestName().equals(title)) {
                return i;
            }
        }
        return -1;
    }

    private final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private float hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return (float) val;
    }

    public void showMessage(String msg) {
        Toast.makeText(SpectroCareSDK.getInstance().context, msg, Toast.LENGTH_SHORT).show();
    }

    private void processRCConversion() {

        reflectenceChartsArray.clear();

        if (getStandardwhiteSubstrateArray() != null) {
            ArrayList<Float> swSubstratedArray = getStandardwhiteSubstrateArray();
            for (IntensityChart objIntensitychartObject : intensityChartsArray) {
                if (!objIntensitychartObject.getTestName().equals(standardWhiteTitle) && !objIntensitychartObject.getTestName().equals(darkSpectrumTitle)) {
                    ArrayList<Float> originalArray = getOriginalDivReference(objIntensitychartObject.getSubstratedArray(), swSubstratedArray);
                    double interpolationValue = getClosestValue(objIntensitychartObject.getWavelengthArray(), originalArray, objIntensitychartObject.getCriticalWavelength());
                    ReflectanceChart objReflectanceChart = new ReflectanceChart();
                    objReflectanceChart.setTestName(objIntensitychartObject.getTestName());
                    objReflectanceChart.setxAxisArray(wavelengthXAxis);
                    objReflectanceChart.setyAxisArray(originalArray);
                    objReflectanceChart.setCriticalWavelength(objIntensitychartObject.getCriticalWavelength());
                    objReflectanceChart.setAutoMode(true);
                    objReflectanceChart.setInterpolationValue(interpolationValue);
                    reflectenceChartsArray.add(objReflectanceChart);
                }
            }
        } else {
            //Log.e("No SW available", "call");
        }
        processFinalTestResults();
    }

    private ArrayList<Float> getOriginalDivReference(ArrayList<Float> originalArray, ArrayList<Float> referenceArray) {
        //Log.e("originalArray", "call" + originalArray.toString());
        //Log.e("referenceArray", "call" + referenceArray.toString());

        ArrayList<Float> divisionArray = new ArrayList<>();
        for (int i = 0; i < originalArray.size(); i++) {
            divisionArray.add(originalArray.get(i) / referenceArray.get(i));
        }
        //Log.e("divisionArray", "call" + divisionArray.toString());
        return divisionArray;
    }

    private ArrayList<Float> getStandardwhiteSubstrateArray() {

        for (IntensityChart objIntensitychartObject : intensityChartsArray) {
            if (objIntensitychartObject.getTestName().equals(standardWhiteTitle)) {
                return objIntensitychartObject.getSubstratedArray();
            }
        }
        return null;
    }

    private void processFinalTestResults() {
        concentrationArray.clear();
        int index = 1;
        for (ReflectanceChart objReflectance : reflectenceChartsArray) {
            if (getRCObjectFortestName(objReflectance.getTestName()) != null) {
                RCTableData rcTableObject = getRCObjectFortestName(objReflectance.getTestName());
                //Log.e("tcunitvalue", "call" + rcTableObject.getUnit());
                ArrayList<Float> rArray = new ArrayList<>();
                ArrayList<Float> cArray = new ArrayList<>();

                for (double d : rcTableObject.getR()) {
                    rArray.add((float) d);
                }

                for (double d : rcTableObject.getC()) {
                    cArray.add((float) d);
                }
                double finalC = getClosestValue(rArray, cArray, objReflectance.getInterpolationValue());
                ConcentrationControl objConcetration = new ConcentrationControl();
                objConcetration.setSNo(String.valueOf(index));
                objConcetration.setConcentration(String.valueOf(finalC));
                objConcetration.setUnits(rcTableObject.getUnit());
                objConcetration.setTestItem(rcTableObject.getTestItem());
                objConcetration.setReferenceRange(rcTableObject.getReferenceRange());
                concentrationArray.add(objConcetration);
                index += 1;
            }
        }
        //Log.e("concentrationArray", "call" + concentrationArray.size());
    }

    private ArrayList<Float> sortXValuesArray(ArrayList<Float> xValues, final double criticalWavelength) {
        //Log.e("beforesort", "call" + xValues.toString());
        //Log.e("beforecritical", "call" + criticalWavelength);
        Collections.sort(xValues, new Comparator<Float>() {
            @Override
            public int compare(Float s1, Float s2) {
                return Double.valueOf(Math.abs(criticalWavelength - s1)).compareTo(Double.valueOf(Math.abs(criticalWavelength - s2)));
            }
        });
        //Log.e("criticalWavelength", "call" + criticalWavelength);
        //Log.e("sortXValuesArray", "call" + xValues.toString());
        return xValues;
    }

    private double getClosestValue(final ArrayList<Float> xValues, ArrayList<Float> yValues, final double criticalWavelength) {

        // Sorting array based on Difference
        ArrayList<Float> beforeXvalues = new ArrayList<>(xValues);

        ArrayList<Float> sortedArrayBasedOnDifference = sortXValuesArray(xValues, criticalWavelength);

        //Log.e("sortedOnDifference", "call" + sortedArrayBasedOnDifference.toString());
        //Log.e("yvluesarray", "call" + yValues.toString());
        //Log.e("yvluesarraysize", "call" + yValues.size());
        //Log.e("xvzlues", "call" + criticalWavelength);

        float firstXValue = sortedArrayBasedOnDifference.get(0);
        float secondXValue = sortedArrayBasedOnDifference.get(1);

        float firstYValue = yValues.get(beforeXvalues.indexOf(firstXValue));
        float secondYValue = yValues.get(beforeXvalues.indexOf(secondXValue));


        float x1 = firstXValue;
        float x2 = secondXValue;
        float y1 = firstYValue;
        float y2 = secondYValue;
        //Log.e("finalY", "call" + x1 + x2 + y1 + y2);

        if (x1 > x2) {
            x1 = secondXValue;
            x2 = firstXValue;
            y1 = secondYValue;
            y2 = firstYValue;
        }

        double finalY = y1 + ((criticalWavelength - x1) * (y2 - y1)) / (x2 - x1);
        //Log.e("finalY", "call" + finalY);
        return finalY;
    }

    private RCTableData getRCObjectFortestName(String testName) {

        if (spectroDeviceObject.getRCTable() != null) {
            ArrayList<RCTableData> rcTable = spectroDeviceObject.getRCTable();
            for (RCTableData objRCTable : rcTable) {
                if (objRCTable.getTestItem().equals(testName)) {
                    //Log.e("rarray", "call" + objRCTable.getR().toString());
                    //Log.e("carray", "call" + objRCTable.getC().toString());
                    return objRCTable;
                }
            }
        }
        return null;
    }

    public void syncSettingsWithDevice() {
        isForSync=false;
        isForDarkSpectrum=true;
        if (SCConnectionHelper.getInstance().isConnected) {
            if (!isForSync) {
                isForSync = true;
                commandNumber = 1;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommandForROIParams();
                    }
                }, 3000 * 1);
            }
        }

    }

    private void sendCommandForROIParams() {
        if (spectroDeviceObject.getImageSensor() != null) {
            ImageSensorStruct objSensor = spectroDeviceObject.getImageSensor();
            int[] ROIvaluesArray = objSensor.getROI();
            SCConnectionHelper.getInstance().prepareCommandForROI(ROIvaluesArray[0], ROIvaluesArray[1], ROIvaluesArray[2], ROIvaluesArray[3]);
        }
    }

    private void performMotorStepsFunction() {
        //Log.e("stripnumberandsize","call"+stripNumber+"cal"+(intensityChartsArray.size()-1));
        if (stripNumber == intensityChartsArray.size()-1 ) {  // Last Step is Eject . if match then turn of LED and execute last step with 1 sec delay.
            ledControl(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    motorStepsControl(motorSteps.get(stripNumber));
                }
            }, 3000 * 1);
        }
        else{
            motorStepsControl(motorSteps.get(stripNumber));
        }
    }

    private void motorStepsControl(Steps motorObject) {
        String direction = Commands.MOVE_STRIP_COUNTER_CLOCKWISE_TAG;

        if (motorObject.getDirection().equals("CW")) {
            direction = Commands.MOVE_STRIP_CLOCKWISE_TAG;
        }
        SCConnectionHelper.getInstance().prepareCommandForMotorMove(motorObject.getNoOfSteps(), direction);
    }

    private void ledControl(boolean isOn) {
        String ledCommandString;
        ledCommandString =Commands. LED_TURN_OFF;
        if (isOn) {
            ledCommandString = Commands.LED_TURN_ON;
        }
        requestCommand = ledCommandString;
        //Log.e("ledControl", "call" + requestCommand);

        if (SCConnectionHelper.getInstance().isConnected) {
            SendString(requestCommand);
        }
    }

    private void testCompleted() {

        processRCConversion();
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");

        testItems.clear();
        int sno = 0;

        for (int index = 0; index < concentrationArray.size(); index++) {
            ConcentrationControl object = concentrationArray.get(index);
            String testName = object.getTestItem();
            String unit = " " + object.getUnits();
            String objValue = object.getConcentration();
            String objHealthReferenceRange = object.getReferenceRange();
            boolean flag = false;
            String resultText = " ";
            double finalValue = Double.parseDouble(objValue);
            flag = getFlagForTestItemWithValue(testName, finalValue);
            resultText = " " + getResultTextForTestItemwithValue(testName, finalValue);
            String testValue = getNumberFormatStringforTestNameWithValue(testName, finalValue);

            //Log.e("testunits", "" + unit + resultText + object.getReferenceRange());
            //Log.e("testresult", "" + resultText);
            //Log.e("testvalue", "" + testValue);
            sno = index + 1;

            TestFactors objTest = new TestFactors();
            objTest.setFlag(flag);
            objTest.setUnits(unit);
            objTest.setReferenceRange(objHealthReferenceRange);
            objTest.setTestname(testName);
            objTest.setSNo(String.valueOf(sno));
            objTest.setResult(resultText);
            objTest.setValue(testValue);

            testItems.add(objTest);
        }
    }


    private String getNumberFormatStringforTestNameWithValue(String testName, double value) {
        //Log.e("formantetestvalue", "" + value);
        String formattedString = String.valueOf(value);

        if (spectroDeviceObject.getRCTable() != null) {
            for (RCTableData objRCTable : spectroDeviceObject.getRCTable()) {
                if (objRCTable.getTestItem().equals(testName)) {
                    //Log.e("numberformate", "call" + objRCTable.getNumberFormat());
                    if (objRCTable.getNumberFormat().equals("X")) {
                        formattedString = String.format("%.0f", value);
                    } else if (objRCTable.getNumberFormat().equals("X.X")) {
                        formattedString = String.format("%.1f", value);
                    } else if (objRCTable.getNumberFormat().equals("X.XX")) {
                        formattedString = String.format("%.2f", value);
                    } else if (objRCTable.getNumberFormat().equals("X.XXX")) {
                        formattedString = String.format("%.3f", value);
                    } else if (objRCTable.getNumberFormat().equals("X.XXXX")) {
                        formattedString = String.format("%.4f", value);
                    }
                }
            }
        }

        return formattedString;

    }

    public boolean getFlagForTestItemWithValue(String testName, double value) {
        boolean isOk = false;
        if (spectroDeviceObject.getRCTable() != null) {
            for (RCTableData objRc : spectroDeviceObject.getRCTable()) {
                if (objRc.getTestItem().equals(testName)) {
                    if (objRc.getLimetLineRanges().get(0) != null) {
                        LimetLineRanges safeRange = objRc.getLimetLineRanges().get(0);
                        if (value > safeRange.getCMinValue() && value <= safeRange.getCMaxValue()) {
                            isOk = true;
                            return isOk;
                        }
                    }
                }
            }
        }
        return isOk;
    }

    public String getResultTextForTestItemwithValue(String testName, double value) {
        if (spectroDeviceObject.getRCTable() != null) {
            for (RCTableData objRc : spectroDeviceObject.getRCTable()) {
                if (objRc.getTestItem().equals(testName)) {
                    for (LimetLineRanges objLimitRange : objRc.getLimetLineRanges()) {
                        if (value > objLimitRange.getCMinValue() && value <= objLimitRange.getCMaxValue()) {
                            //Log.e("linesymboal", "call" + objLimitRange.getLineSymbol());
                            return objLimitRange.getLineSymbol();
                        }
                    }
                }
            }
        }
        return "";
    }

    private Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    private byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    public void startTestProcess(SyncingInterface syncingInterface1) {
        this.syncingInterface=syncingInterface1;
        removereceiver();
        initializeService();
        syncSettingsWithDevice();
    }
    public void abortTesting(AbortInterface abortInterface1) {
        this.abortInterface=abortInterface1;
        clearPreviousTestResulsArray();
        // String ejectCommand = "$MRS5000#";
        if (SCConnectionHelper.getInstance().isConnected) {
            isInterrupted = true;
            //  SendString(ejectCommand);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isEjectType = true;
                    ledControl(false);
                }
            }, 8000);
        }
    }
    public void performTryAgainFunction(){
        startTestAnalysis(testAnalysisListener);
    }

    public void performTestCancelFunction(){
        clearCache();
        clearPreviousTestResulsArray();
        SCConnectionHelper.getInstance().disconnect();// it will goes to SpectroDeviceScanViewController
        unRegisterReceiver();
    }

    public interface JsonFileInterface {
        void onSuccessForConfigureJson();
        void onFailureForConfigureJson(String bitmapList);
    }
   public interface SyncingInterface {
       void isSyncingCompleted(boolean error);
       void  gettingDarkSpectrum(boolean isgetting);

   }
    public interface TeststaResultInterface {
         void onSuccessForTestComplete(ArrayList<TestFactors> results, String msg);
         void onFailureForTesting(String error);
    }
    public interface AbortInterface {
        void onAbortForTesting(boolean bool);
    }

}
