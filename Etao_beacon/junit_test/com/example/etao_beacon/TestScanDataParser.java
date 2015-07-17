package com.example.etao_beacon;

import com.etao.ble.BasePaser;

import android.test.AndroidTestCase;

public class TestScanDataParser extends AndroidTestCase {

	private BasePaser mParser;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		String layout = "f:7-8=asdf"	;
		mParser = new BasePaser(layout);
	}
	
	public void testParseUuid(){
		System.out.println("asdfasd");
	}


}
