package com.etao.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public class ScanManager {

	 private static ScanManager mInstance;
	 
	 private BluetoothAdapter mBluetoothAdapter;
	 private BluetoothManager mBluetoothManager;
	 private ScanManager(Context context){
		 
	 }
	 
	 public static ScanManager getInstance(Context context){
		 if(mInstance==null){
			 synchronized (ScanManager.class) {
				if(mInstance==null){
					mInstance = new ScanManager(context);
				}
			}
		 }
		 return mInstance;
	 }

}
