package com.etao.ble.scanner;

public interface IScanner {

	/**
	 * 开始扫描
	 */
	void startScan(IScannCallback mCallback);
	/**
	 * 停止扫描
	 */
	void stopScan();
	
	
}
