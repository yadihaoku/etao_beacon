package com.etao.ble;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasePaser implements IParser {

	/**
	 * 特征码 标示
	 */
	private static final Pattern FEATURE_PATTERN = Pattern.compile("f\\:(\\d)-(\\d)=(\\w+)");

	private static final Pattern UUID_PATTERN = Pattern.compile("u\\:(\\d)-(\\d)");

	private static final Pattern MAJOR_PATTERN = Pattern.compile("m\\:(\\d)-(\\d)");

	private static final Pattern MINOR_PATTERN = Pattern.compile("mi\\:(\\d)-(\\d)");

	private int mFeatureStartIndex;
	private int mFeatureEndIndex;
	private int mUuidStartIndex;
	private int mUuidEndIndex;
	private int mMajorStartIndex;
	private int mMajorEndIndex;
	private int mMinorStartIndex;
	private int mMinorEndIndex;

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
			}
			
			matcher = MAJOR_PATTERN.matcher(p);
			while(matcher.find()){
				mMajorStartIndex = Integer.parseInt( matcher.group(1) );
				mMajorEndIndex = Integer.parseInt(matcher.group(2));
			}
			
		}
	}

	@Override
	public String getUUID(byte[] scanBytes) {
		return null;
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
