package com.ford.kcooley8.SYNCController;

/**
 * Created by omakke on 5/13/2016.
 */

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for scanning and displaying available BLE devices.
 */
public class DeviceScanActivity extends ListActivity {

    private BluetoothAdapter mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
    private boolean mScanning;
    private Handler mHandler = new Handler();
    private int sigoff = 0;
    private BluetoothGatt mGatt;
    MainActivity mActivity;

    private long lastTime = 0;
    boolean stopScan = false;



    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
 //           Log.e("GATT", " Found something!");
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
//            Log.e("GATT", " Found something!");
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
 //           Log.e("GATT", " Found something!");
            List<BluetoothGattService> services = gatt.getServices();
            List<BluetoothGattCharacteristic> pm2p5char = services.get(0).getCharacteristics();
            BluetoothGattCharacteristic pm2p5 = pm2p5char.get(0);
            int i = 0;
            for(BluetoothGattDescriptor descriptor : pm2p5.getDescriptors()){
                if(i++ == 0) continue;
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); // Was enable_indication_value
                gatt.writeDescriptor(descriptor);
            }
            gatt.setCharacteristicNotification(pm2p5, true);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
 //           Log.e("GATT", " Found something!");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
 //           Log.e("GATT", " Found something!");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
     //       Log.e("GATT", " Found something!");
            Integer value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 4);

            byte[] x1 = characteristic.getValue();
            // 10504 = 2908 hex => x[3] = 50, x[4] = 57, x[5] = 48, x[6] = 56
            if(x1.length < 7) return;
            if(x1[0] == 43 ) return; // Ignore the frame if it starts with +
            byte[] val1 = {x1[3], x1[4], x1[5], x1[6]};
          //  byte[] val1 = {(byte) (x1[6]-48), (byte) (x1[5]-48)};
            String hexValue = new String(val1);
            final Integer scaled1 = hex2decimal(hexValue);
            SharedData.setCabinAQI(scaled1);

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
//            Log.e("GATT", " Found something!");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
 //           Log.e("GATT", " Found something!");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.e("GATT", " Found something!");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.e("GATT", " Found something!");
        }
    };

    public DeviceScanActivity(MainActivity activity){
        mActivity = activity;
    }
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("CALLBACK", "Found device " + device.getAddress());
                            if(device.getName() != null && device.getName().toUpperCase().contains("AAS") &&  mScanning == true){
                                Log.d("BLE", "Found AAS Dust Sensor");
                                scanLeDevice(false);
                                mActivity.runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        mGatt = device.connectGatt(MainActivity.currentActivity, true, mGattCallback);
                                    }
                                });

                            }
                        }
                    });
                }
            };

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
}