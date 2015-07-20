package com.etao.ble;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasePaser implements IParser {

	/**
	 * 特征码 标示
	 */
	private static final Pattern FEATURE_PATTERN = Pattern.compile("f:(\\d)-(\\d)\\=(\\w+)");

	private static final Pattern UUID_PATTERN = Pattern.compile("u\\:(\\d+)\\-(\\d+)");

	private static final Pattern MAJOR_PATTERN = Pattern.compile("m\\:(\\d+)-(\\d+)");

	private static final Pattern MINOR_PATTERN = Pattern.compile("s\\:(\\d+)-(\\d+)");

	
	private int mFeatureStartIndex;
	private int mFeatureEndIndex;
	private int mUuidStartIndex;
	private int mUuidEndIndex;
	private int mMajorStartIndex;
	private int mMajorEndIndex;
	private int mMinorStartIndex;
	private int mMinorEndIndex;
	
	/**
	 * 沃联 vBeacon 特征码   215
	 */
	private int mDefaultFeature = 0x215;

	public BasePaser(String pattern_layout) {
		splitLayout(pattern_layout);
	}


	private void splitLayout(String layout) {
		String patterns[] = layout.split(",");
		for (String p : patterns) {
			
			//匹配特征码 位置
			Matcher matcher = FEATURE_PATTERN.matcher(p);
			while (matcher.find()) {
				mFeatureStartIndex = Integer.parseInt(matcher.group(1));
				mFeatureEndIndex = Integer.parseInt(matcher.group(2));
				mDefaultFeature = Integer.parseInt(matcher.group(3));
			}
			
			matcher = MAJOR_PATTERN.matcher(p);
			while(matcher.find()){
				mMajorStartIndex = Integer.parseInt( matcher.group(1) );
				mMajorEndIndex = Integer.parseInt(matcher.group(2));
			}
			
			matcher = UUID_PATTERN.matcher(p);
			while(matcher.find()){
				mUuidStartIndex = Integer.parseInt(matcher.group(1));
				mUuidEndIndex = Integer.parseInt(matcher.group(2));
			}
			
			matcher = MINOR_PATTERN.matcher(p);
			while(matcher.find()){
				mMinorEndIndex = Integer.parseInt(matcher.group(1));
				mMajorStartIndex = Integer.parseInt(matcher.group(2));
			}
			
		}
	}

	/**
	 * UUID 共 16 字节，128位。
	 * 标准的 UUID 长度串
	 * 8-4-4-4-12
	 * 
	 * 
	 */
	@Override
	public String getUUID(byte[] scanBytes) {
		StringBuilder uuids = new StringBuilder();
		if(mUuidEndIndex < scanBytes.length ){
			for(int i=mUuidStartIndex; i < mUuidEndIndex;i++){
				uuids.append(String.format("%02x",  scanBytes[i] ));
			}
		}
		String lastStr = uuids.toString();
		if(lastStr.length() == 32){
			StringBuilder uuid = new StringBuilder();
			uuid.append(lastStr.substring(0, 8));
			uuid.append("-");
			uuid.append(lastStr.substring(8, 12));
			uuid.append("-");
			uuid.append(lastStr.substring(12, 16));
			uuid.append("-");
			uuid.append(lastStr.substring(16, 20));
			uuid.append("-");
			uuid.append(lastStr.substring(20));
			return uuid.toString();
		}
		return lastStr;
	}

	@Override
	public String getFeatureId(byte[] scanBytes) {
		return null;
	}

	@Override
	public int getMajor(byte[] scanBytes) {
		return 0;
	}

	@Override
	public int getMinor(byte[] scanBytes) {
		return 0;
	}

}
