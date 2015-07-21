package com.etao.ble.scanner;

import android.bluetooth.BluetoothDevice;

/**
 * 
 * @author YadiYan 2015-7-21
 *
 */
public interface IScannCallback {

	void onScan(BluetoothDevice device, int rssi, byte[] scanRecord);
}
