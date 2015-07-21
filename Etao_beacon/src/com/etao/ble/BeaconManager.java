package com.etao.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;

import com.etao.ble.entity.Beacon;
import com.etao.ble.parser.BasePaser;
import com.etao.ble.parser.IParser;
import com.etao.ble.scanner.DefaultScanFactory;
import com.etao.ble.scanner.IScannCallback;
import com.etao.ble.scanner.IScanner;

public class BeaconManager {

	private IParser mBeaconParser;
	private IScanner mScanner;
	private Context mContext;
	private OnScanCallback mScanCallback;
	
	
	

	public static BeaconManager newInstance(Context mContext){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
			return null;
		return new BeaconManager(mContext);
	} 
	
	private BeaconManager(Context mContext){
		this.mContext = mContext;
		init();
	}
	
	
	private void init(){
		mBeaconParser = new VBeaconParser();
		mScanner = DefaultScanFactory.getFactory(mContext).getScanner();
	}
	/**
	 * 开始扫描
	 */
	public void startScan(){
		mScanner.startScan(getScanCallback());
	}
	/**
	 * 停止扫描
	 */
	public void stopScan(){
		if(mScanner != null)
			mScanner.stopScan();
	}
	/**
	 * 解析 沃联的  vBeacon 
	 * @author YadiYan 2015-7-21
	 *
	 */
	class VBeaconParser extends BasePaser{
		/**
		 * 沃联  7-8 位 为设备标示 (官方的配置客户端得知) <br />
		 * 9-25 位为 UUID 数据段 <br />
		 * 25-26 为 Major 数据段 <br />
		 * 27-28 为 Minor 数据段 <br />
		 */
		private static final String VBEACON_LAYOUT = "f:7-8=0x215,u:9-25,m:25-26,s:27-28";
		public VBeaconParser() {
			super(VBEACON_LAYOUT);
		}
		
	}
	
	private IScannCallback mCallback;
	/**
	 * IScanner startScan 所用的 scancallback
	 * @return
	 */
	private IScannCallback getScanCallback(){
		if(null == mCallback)
			mCallback = new VBeaconScanCallback();
		return mCallback;
	}
	
	class VBeaconScanCallback implements IScannCallback{

		@Override
		public void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			parseScanResult(device, rssi, scanRecord);
		}
		
	}
	/**
	 * 解析扫描后得到  record 
	 * @param device
	 * @param rssi
	 * @param scanRecord
	 */
	private void parseScanResult(BluetoothDevice device, int rssi, byte[] scanRecord){
		if(mScanCallback != null){
			Beacon beacon = mBeaconParser.toBeacon(scanRecord);
			beacon.setAddress(device.getAddress());
			beacon.setRssi(rssi);
			mScanCallback.onScan(beacon);
		}
	}
	
	/**
	 * 扫描到 Beacon 设备时
	 * @author YadiYan 2015-7-21
	 *
	 */
	public static interface OnScanCallback{
		void onScan(Beacon mbeacon);
	}
	
	public OnScanCallback getmScanCallback() {
		return mScanCallback;
	}

	public void setScanCallback(OnScanCallback mScanCallback) {
		this.mScanCallback = mScanCallback;
	}

}
