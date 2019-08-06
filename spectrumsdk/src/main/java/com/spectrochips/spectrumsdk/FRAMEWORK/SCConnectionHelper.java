package com.spectrochips.spectrumsdk.FRAMEWORK;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.spectrochips.spectrumsdk.DeviceConnectionModule.Commands;
import com.spectrochips.spectrumsdk.MODELS.ImageSensorStruct;

import java.util.ArrayList;

/**
 * Created by wave on 10/6/2018.
 */

public class SCConnectionHelper {
    private static SCConnectionHelper myObj;
    public boolean isConnected = false;
    public BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private int mState = UART_PROFILE_DISCONNECTED;
    private static final String TAG = "SCConnectionHelper";
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    private static final long SCAN_PERIOD = 10000; //10 seconds
    private Handler mHandler = new Handler();
    private ScanDeviceInterface scanDeviceInterface;
    private String COMPANY_BLE_IDENTIFIER = "0D00DBFB";

    public static SCConnectionHelper getInstance() {
        if (myObj == null) {
            myObj = new SCConnectionHelper();
        }
        return myObj;
    }

    private void devicesNotDiscovered() {
        if (deviceList.size() == 0) {
            didDevicesNotFound();
        }
        deviceList.clear();
        startScan(true);
    }

    public void startScan(final boolean enable) {
        if (enable) {
            deviceList.clear();
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // mScanning = false;// for stop the scan after 1o sec
                    //stopScan();// without this line scan continuously
                    deviceList.clear();
                    startScan(true);
                    didDevicesNotFound();
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            stopScan();
        }

    }

    private void didDevicesNotFound() {
        scanDeviceInterface.onSuccessForScanning(deviceList,false);
    }

    public void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mHandler.removeCallbacksAndMessages(null);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            //Log.e("mLeScanCallback", "call" + device.getName() + device.getAddress() + scanRecord.length);
            if (bytesToHexString(scanRecord).contains(COMPANY_BLE_IDENTIFIER)) {
                if (device.getAddress() != null) {
                    addDevice(device, rssi);
                }
            }
        }
    };

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;
        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }
        if (!deviceFound) {
            deviceList.add(device);
            if (scanDeviceInterface == null) {
                //Fire proper event. bitmapList or error message will be sent to
                //class which set scanDeviceInterface.
            } else {
                scanDeviceInterface.onSuccessForScanning(deviceList ,true);
            }
        }
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

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                mState = UART_PROFILE_CONNECTED;
                //Log.e("broadcastconnected", "call");
                isConnected = true;
                if (scanDeviceInterface == null) {
                    //Fire proper event. bitmapList or error message will be sent to
                    //class which set scanDeviceInterface.
                } else {
                    scanDeviceInterface.onSuccessForConnection("Device Connected");
                }
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                Log.d(TAG, "UART_DISCONNECT_MSG");
                mState = UART_PROFILE_DISCONNECTED;
                isConnected = false;
                SCTestAnalysis.getInstance().mService.close();
                //Log.e("broadcast", "call");
                if (scanDeviceInterface == null) {
                } else {
                    scanDeviceInterface.onFailureForConnection("Dis Connected");
                }
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                SCTestAnalysis.getInstance().mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                //Log.e("Received Bytes", "" + txValue.length);
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                showMessage("Device doesn't support UART. Disconnecting");
                disconnectWithPeripheral();
                if (scanDeviceInterface == null) {
                    //Fire proper event. bitmapList or error message will be sent to
                    //class which set scanDeviceInterface.
                } else {
                    scanDeviceInterface.onFailureForConnection("DisConnected");
                }
            }


        }
    };


    public void initilizeSevice() {
        Intent bindIntent = new Intent(SpectroCareSDK.getInstance().context, UartService.class);
        SpectroCareSDK.getInstance().context.bindService(bindIntent, mServiceConnection, SpectroCareSDK.getInstance().context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(SpectroCareSDK.getInstance().context).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private  IntentFilter makeGattUpdateIntentFilter() {
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
            SCTestAnalysis.getInstance().mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + SCTestAnalysis.getInstance().mService);
            if (!SCTestAnalysis.getInstance().mService.initialize()) {
                //Log.e(TAG, "Unable to initialize Bluetooth");
                if (scanDeviceInterface == null) {
                    //Fire proper event. bitmapList or error message will be sent to
                    //class which set scanDeviceInterface.
                } else {
                    scanDeviceInterface.uartServiceClose("Close service");
                }
                //finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnectWithPeripheral(mDevice);
            SCTestAnalysis.getInstance().mService = null;
        }
    };

    public void ejectStripCommand() {
        String ejectCommand = "$MRS5000#";

        //Log.e("motorPostionControl", "call" + ejectCommand);
        if (isConnected) {
            SCTestAnalysis.getInstance().SendString(ejectCommand);
        }
    }

    public void prepareCommandForChangeSSIDandPassword(String ssid, String password) {
        //Log.e("ssidpsw", "call" + ssid + password);

        //"$APC@SSID@PWD@PASSWORD@!"
        String commandString = Commands.WIFI_INFO_CHANGE_TAG;
        commandString = commandString.replace("@SSID@", ssid);
        commandString = commandString.replace("@PASSWORD@", password);
        //Log.e("motorPostionControl", "call" + commandString);
        if (isConnected) {
            SCTestAnalysis.getInstance().SendString(commandString);
        }
    }

    public void prepareCommandForAnalogGain(String analogValue) {

        String commandString = Commands.START_TAG;
        commandString = commandString + Commands.ANALOG_GAIN_TAG;
        commandString = commandString + analogValue;
        commandString = commandString + Commands.END_TAG;
        //Log.e("CommandForAnalogGain", "call" + commandString);
        if (isConnected) {
            SCTestAnalysis.getInstance().SendString(commandString);
        }
    }

    public void prepareCommandForDigitalGain(double digitalGainValue) {

        String commandString = Commands.START_TAG;
        commandString = commandString + Commands.DIGITAL_GAIN_TAG;
        String digitalGainString = String.valueOf(digitalGainValue);

        if (digitalGainString.contains(".")) {
            String digitalGainArray[] = digitalGainString.split("\\.");

            int firstValue = Integer.parseInt(digitalGainArray[0]);
            double secondValue = digitalGainValue - (double) firstValue;
            int finalSecondValue = (int) Math.round(secondValue / Commands.DIGITALGAIN_CONST_VALUE);

            String firstString = Integer.toBinaryString(firstValue);
            firstString = pad(firstString, 3);
            //Log.e("firstString", "call" + firstString);

            String secondString = Integer.toBinaryString(finalSecondValue);
            secondString = pad(secondString, 5);
            //Log.e("secondString", "call" + secondString);

            String finalString = firstString + secondString;

            Integer number = Integer.parseInt(finalString, 2);//binary to int
            //Log.e("cccccccccccc", "call" + number);

            String number1 = Integer.toBinaryString(number);

            int digitalVal = Integer.parseInt(number1, 2);

            if (number1 != null) {
                //Log.e("numbervlaue", "call" + digitalVal);
                commandString = commandString + digitalVal;
                commandString = commandString + Commands.END_TAG;
                //Log.e("commandString", "call" + commandString);
                if (isConnected) {
                    SCTestAnalysis.getInstance().SendString(commandString);
                }
            }

        }


    }

    private String pad(String string, int toSize) {
        String padded = string;
        if (string.length() < toSize) {
            for (int t = 0; t < (toSize - string.length()); t++) {
                padded = "0" + padded;
            }
        }
        return padded;
    }

    public void prepareCommandForExitStrip() {
        //$MLS4000#
        String commandString = Commands.START_TAG;
        commandString = commandString + Commands.MOVE_STRIP_COUNTER_CLOCKWISE_TAG;

        commandString = commandString + String.valueOf("400");//
        commandString = commandString + Commands.END_TAG;
        // requestCommand = commandString
        //Log.e("CommandForExitStrip", "call" + commandString);

        prepareCommandForLED(false);
    }

    private void prepareCommandForLED(boolean isOn) {
        if (isOn) {
            if (isConnected) {
                SCTestAnalysis.getInstance().SendString(Commands.LED_TURN_ON);
            }
        } else {
            if (isConnected) {
                SCTestAnalysis.getInstance().SendString(Commands.LED_TURN_OFF);
            }
        }

    }

    public void prepareCommandForROI(int ho, int hc, int vo, int vc) {
        //$ROIho,hc,vo,lc,#
        String commandString = Commands.START_TAG;
        commandString = commandString + Commands.ROI_TAG;
        commandString = commandString + String.valueOf(ho) + "," + String.valueOf(hc) + "," + String.valueOf(vo) + "," + String.valueOf(vc);
        commandString = commandString + Commands.END_TAG;
        // requestCommand = commandString
        //Log.e("prepareCommandForROI", "call" + commandString);
        if (isConnected) {
            SCTestAnalysis.getInstance().SendString(commandString);
        }
    }

    public void disconnectWithPeripheral() {
        if (isConnected) {
            isConnected = false;
            SCTestAnalysis.getInstance().mService.disconnect();
        }
    }

    public void disconnect() {
        if (isConnected) {
            isConnected = false;
            SCTestAnalysis.getInstance().mService.close();
        }
    }

    public void prepareCommandForNoOfAverage(int count) {

        //$AFC40#
        String commandString = Commands.START_TAG;
        commandString = commandString + Commands.AVG_FRAME_TAG;
        commandString = commandString + String.valueOf(count);
        commandString = commandString + Commands.END_TAG;
        // requestCommand = commandString

        //Log.e("CommandForNoOfAverage", "call" + commandString);
        if (isConnected) {
            SCTestAnalysis.getInstance().SendString(commandString);
        }

    }

    public void prepareCommandForExpousureCount(int count) {

        //$ELCxxxxx#
        String commandString = Commands.START_TAG;
        commandString = commandString + Commands.EXPOUSURE_TAG;
        commandString = commandString + String.valueOf(count);
        commandString = commandString + Commands.END_TAG;
        // requestCommand = commandString
        //Log.e("prepaForExpousureCount", "call" + commandString);
        if (isConnected) {
            SCTestAnalysis.getInstance().SendString(commandString);
        }

    }

    public void sendExposureTime() {
        if (SCConnectionHelper.getInstance().isConnected) {
            if (SCTestAnalysis.getInstance().spectroDeviceObject.getImageSensor() != null) {
                ImageSensorStruct objSensor = SCTestAnalysis.getInstance().spectroDeviceObject.getImageSensor();
                int exposureTme = objSensor.getExposureTime();
                prepareCommandForExpousureCount(exposureTme);
            }
        } else {
            Toast.makeText(SpectroCareSDK.getInstance().context, "Device not connected !!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendAnanlogGain() {
        if (SCConnectionHelper.getInstance().isConnected) {
            if (SCTestAnalysis.getInstance().spectroDeviceObject.getImageSensor() != null) {
                ImageSensorStruct objSensor = SCTestAnalysis.getInstance().spectroDeviceObject.getImageSensor();
                String analogval = String.valueOf(objSensor.getAnalogGain());
                prepareCommandForAnalogGain(analogval + "X");
            }
        } else {
            Toast.makeText(SpectroCareSDK.getInstance().context, "Device not connected !!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendDigitalGain() {
        if (SCConnectionHelper.getInstance().isConnected) {
            if (SCTestAnalysis.getInstance().spectroDeviceObject.getImageSensor() != null) {
                ImageSensorStruct objSensor = SCTestAnalysis.getInstance().spectroDeviceObject.getImageSensor();
                String digitalGainString = String.valueOf(objSensor.getDigitalGain());
                prepareCommandForDigitalGain(Double.parseDouble(digitalGainString));
            }
        } else {
            Toast.makeText(SpectroCareSDK.getInstance().context, "Device not connected !!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSpectrumAVG() {
        if (SCConnectionHelper.getInstance().isConnected) {
            if (SCTestAnalysis.getInstance().spectroDeviceObject.getImageSensor() != null) {
                ImageSensorStruct objSensor = SCTestAnalysis.getInstance().spectroDeviceObject.getImageSensor();
                int darkSpectrum = objSensor.getNoOfAverageForDarkSpectrum();
                prepareCommandForNoOfAverage(darkSpectrum);
            } else {
                Toast.makeText(SpectroCareSDK.getInstance().context, "Device not connected !!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void prepareCommandForMotorMove(int steps, String direction) {
        String commandForMotorControl = Commands.START_TAG;
        commandForMotorControl = commandForMotorControl + direction;
        commandForMotorControl = commandForMotorControl + String.valueOf(steps);
        commandForMotorControl = commandForMotorControl + Commands.END_TAG;
        SCTestAnalysis.getInstance().SendString(commandForMotorControl);
    }

    public void prepareCommandForMoveToPosition() {
        String commandForMotorControl =Commands. START_TAG;
        commandForMotorControl = commandForMotorControl + Commands.MOVE_STRIP_POSITION;
        commandForMotorControl = commandForMotorControl + "1";
        commandForMotorControl = commandForMotorControl + Commands.END_TAG;
        //  print(commandForMotorControl
        //Log.e("prepareCommandF", "call" + commandForMotorControl);
        SCTestAnalysis.getInstance().SendString(commandForMotorControl);
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

    private void showMessage(String msg) {
        Toast.makeText(SpectroCareSDK.getInstance().context, msg, Toast.LENGTH_SHORT).show();
    }
    public void initializeAdapterAndServcie() {
        initilizeSevice();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void activateScanNotification(ScanDeviceInterface scanDeviceInterface1) {
        this.scanDeviceInterface=scanDeviceInterface1;
    }

    //In this interface, you can define messages, which will be send to owner.
    public interface ScanDeviceInterface {
        //In this case we have two messages,
        //the first that is sent when the process is successful.
        void onSuccessForConnection(String msg);

        void onSuccessForScanning(ArrayList<BluetoothDevice> deviceArray, boolean msg);

        //And The second message, when the process will fail.
        void onFailureForConnection(String error);

        void uartServiceClose(String error);
    }

}


