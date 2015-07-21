package com.etao.ble.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.etao.ble.entity.Beacon;

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

	
	/**
	 * 解析指定的 数据字段 布局
	 * @param layout
	 */
	private void splitLayout(String layout) {
		String patterns[] = layout.split(",");
		for (String p : patterns) {
			
			//匹配特征码 位置
			Matcher matcher = FEATURE_PATTERN.matcher(p);
			while (matcher.find()) {
				mFeatureStartIndex = Integer.parseInt(matcher.group(1));
				mFeatureEndIndex = Integer.parseInt(matcher.group(2));
				try {
					mDefaultFeature = Integer.decode(matcher.group(3));
					
				} catch (Exception e) {
				}
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
				mMinorStartIndex = Integer.parseInt(matcher.group(1));
				mMinorEndIndex = Integer.parseInt(matcher.group(2));
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

	/**
	 * 获取设备特征 id
	 */
	@Override
	public int getFeatureId(byte[] scanBytes) {
		int feature;
		byte firstByte = scanBytes[mFeatureStartIndex];
		byte lastByte = scanBytes[mFeatureEndIndex];
		feature = ((firstByte << 8) | lastByte);
		return feature;
	}

	/*
	 * 从获取 byte 中获取 major 
	 * (non-Javadoc)
	 * @see com.etao.ble.parser.IParser#getMajor(byte[])
	 */
	@Override
	public int getMajor(byte[] scanBytes) {
		int major;
		if(mMajorEndIndex < scanBytes.length)
		{
			major = ( scanBytes[mMajorStartIndex] << 8) | scanBytes[mMajorEndIndex];
		}else{
			throw new RuntimeException(" scanBytes.lenth = "+ scanBytes.length+ "   MajorEndIndex = "+ mMajorEndIndex);
		}
			
		return major;
	}

	/**
	 * 打获取的 byte 中取出 minor
	 */
	@Override
	public int getMinor(byte[] scanBytes) {
		int minor;
		if(mMajorEndIndex < scanBytes.length)
		{
			minor = ( scanBytes[mMinorStartIndex] << 8) | scanBytes[mMinorEndIndex];
		}else{
			throw new RuntimeException(" scanBytes.lenth = "+ scanBytes.length+ "   MajorEndIndex = "+ mMinorEndIndex);
		}
			
		return minor;
	}


	@Override
	public Beacon toBeacon(byte[] scanBytes) {
		Beacon beacon = new Beacon();
		
		beacon.setMajor(getMajor(scanBytes));
		beacon.setMinor(getMinor(scanBytes));
		beacon.setUuid(getUUID(scanBytes));
		
		return beacon;
	}

}
