package com.etao.ble.scanner;

public interface ScannerFactory {

	/**
	 * 获取 Scanner 的实例 
	 * @return 在不支持 ble 的设备上，会返回 null 。注意检测。
	 */
	IScanner getScanner();
}
