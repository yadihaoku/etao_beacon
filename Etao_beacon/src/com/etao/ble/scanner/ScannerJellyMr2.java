package com.etao.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

/**
 * 封闭的 LeScanCallback
 * 
 * @author YadiYan 2015-7-21
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ScannerJellyMr2 implements IScanner {
	private static final String Tag = ScannerJellyMr2.class.getName();
	private Context mContext;

	private boolean isScanning;

	public ScannerJellyMr2(Context context) {
		this.mContext = context;
	}

	/**
	 * BluetoothManager 实例
	 */
	private BluetoothManager mBleManager;
	private BluetoothAdapter mBleAdapter;

	private IScannCallback mScanCallback;
	private LeScanCallback mCallback;

	public LeScanCallback getLeScanCallback() {
		if (mCallback == null) {
			mCallback = new DefaultScanCallback();
		}
		return mCallback;
	}

	private void initBleManager() {
		mBleManager = (BluetoothManager) this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Log.i(Tag, "设备不支持 Ble");
		}
		if (null != mBleManager) {
			Log.i(Tag, "获取到 BleManger");

			mBleAdapter = mBleManager.getAdapter();
			if (null == mBleAdapter) {
				Log.i(Tag, "BleAdapter is null . ");
				return;
			}
			boolean bleAvaiable = mBleAdapter.isEnabled();
			// 如果bluetooth没有打开，则强制打开
			if (!bleAvaiable) {
				bleAvaiable = mBleManager.getAdapter().enable();
			}
		}
	}

	private BluetoothAdapter getBluetoothAdapter() {
		if (null == mBleAdapter)
			initBleManager();
		return mBleAdapter;
	}

	/**
	 * 
	 * @author YadiYan 2015-7-21
	 * 
	 */
	class DefaultScanCallback implements LeScanCallback {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (mScanCallback != null) {
				mScanCallback.onScan(device, rssi, scanRecord);
			}
		}

	}

	@Override
	public void startScan(IScannCallback callback) {
		this.mScanCallback = callback;
		if (null == mScanCallback)
			throw new RuntimeException(" startScan, ScanCallback 不能为 null ");
		BluetoothAdapter mAdapter = getBluetoothAdapter();
		if (null == mAdapter) {
			Log.e(Tag, " BluetoothAdapter is null .can not to scan.");
			return;
		}

		mAdapter.startLeScan(getLeScanCallback());
		isScanning = true;
	}

	@Override
	public void stopScan() {
		BluetoothAdapter mAdapter = getBluetoothAdapter();
		if (null == mAdapter) {
			Log.e(Tag, " BluetoothAdapter is null .can not to scan.");
			return;
		}
		if (isScanning) {
			mAdapter.stopLeScan(getLeScanCallback());
			isScanning = false;
		}

	}

	// new LeScanCallback() {
	//
	// @Override
	// public void onLeScan(final BluetoothDevice device, int rssi, byte[]
	// scanRecord) {
	// Log.i(Tag, "扫描到设备 name: " + device.getName());
	// Log.i(Tag, "扫描到设备 uuid: " + device.getUuids());
	// Log.i(Tag, "扫描到设备 rssi: " + rssi);
	// Log.i(Tag, "扫描到设备 scanRecord: " + scanRecord.length);
	// Log.i(Tag, "扫描到设备 Manufacturers: " + Arrays.toString(scanRecord));
	//
	// final BleEntity entity = new BleEntity();
	// if(scanRecord[7]!=2 || scanRecord[8] !=21)
	// {
	// Log.i(Tag, " 不是沃联的 设备。。跳过===================");
	// return;
	// }else{
	// entity.major = (0xFF & scanRecord[25]) << 8 | (0xFF & scanRecord[26]);
	// entity.minor = (0xFF & scanRecord[27]) << 8 | (0xFF & scanRecord[28]);
	// String uuid = bytesToHex(Arrays.copyOfRange (scanRecord, 9,25) );
	// entity.uuid = uuid;
	// }
	//
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// updateDevices(device, entity);
	// }
	// });
	//
	// }
	// };
}
