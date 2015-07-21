package com.etao.ble.parser;

import com.etao.ble.entity.Beacon;

public interface IParser {

	/**
	 * 获取 ble 设备  uuid
	 * @param scanBytes
	 */
	String getUUID(byte [] scanBytes);
	
	/**
	 * 获取设备特征码
	 * @param scanBytes
	 */
	int getFeatureId(byte [] scanBytes);
	
	
	/**
	 * 获取 ble major
	 * @param scanBytes
	 */
	int getMajor(byte [] scanBytes);
	
	/**
	 * 获取 ble minor
	 * @param scanBytes
	 */
	int getMinor(byte [] scanBytes);
	
	
	Beacon toBeacon(byte [] bytes);
	
}
