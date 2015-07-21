package com.etao.ble;

import com.etao.ble.parser.BasePaser;

import android.test.AndroidTestCase;

public class TestScanDataParser extends AndroidTestCase {

	private BasePaser mParser;
	private byte [] bytes;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		String layout = "f:7-8=0x215,u:9-25,m:25-26,s:27-28"	;
		mParser = new BasePaser(layout);
		bytes = new byte[]{2, 1, 6, 26, -1, 76, 0, 2, 21, -3, -91, 6, -109, -92, -30, 79, -79, -81, -49, -58, -21, 7, 100, 120, 37, 39, 17, 91, 70, -59, 8, 9, 84, 101, 115, 116, 49, 0, 0, 21, 22, -64, -34, 2, 0, 100, -31, 6, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}
	
	
	public void testParseUuid(){
		System.out.println(mParser.getUUID(bytes));
		System.out.println(mParser.getFeatureId(bytes));
	}

	
	public void testParseMajor(){
		
		int major = mParser.getMajor(bytes);
		assertEquals(major, 10001);
		System.out.println(mParser.getMajor(bytes));
		System.out.println(major);
	}
	
	public void testParseMinor(){
		System.out.println(mParser.getMinor(bytes));
	}
	// conflict fixed


	//from dev branch

	
	//write by yadiYan

	// form dev_third  branch
	
	// wrote by dev branch
}
