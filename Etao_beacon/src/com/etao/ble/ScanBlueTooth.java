package com.etao.ble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.etao_beacon.R;

public class ScanBlueTooth extends Activity implements OnItemClickListener {

	private static final String Tag = ScanBlueTooth.class.getName();

	private TextView tv_status;
	private TextView tv_scann_result;
	private ListView lv_list;

	private BluetoothManager mBleManager;
	private BluetoothAdapter mBleAdapter;

	private List<HashMap<String, String>> mListDevices;
	private HashMap<String, BluetoothDevice> mDevices;
	private SimpleAdapter mListAdapter;
	
    private static final char[] HEX_ARRAY = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

	private Handler mHandler = new Handler();

	private boolean mIsScanning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.scan_ble_layout);

		init();

	}

	private void initBleManager() {
		mBleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Log.i(Tag, "设备不支持 Ble");
		}
		if (null != mBleManager) {
			updateStatus("获取到 BleManger");

			mBleAdapter = mBleManager.getAdapter();
			if (null == mBleAdapter) {
				updateStatus("BleAdapter is null . ");
				return;
			}
			boolean bleAvaiable = mBleAdapter.isEnabled();
			// 如果bluetooth没有打开，则强制打开
			if (!bleAvaiable) {
				bleAvaiable = mBleManager.getAdapter().enable();
			}
			// 如果 ble 已经打开
			if (bleAvaiable) {
				scannBleDevices();
			}
		}
	}

	/**
	 * 扫描 ble 设备
	 */
	private void scannBleDevices() {
		mIsScanning = true;
		scanBleDevicesJellyMr2();
	}

	/**
	 * 使用旧的扫描方式
	 */
	private void scanBleDevicesJellyMr2() {
		mBleAdapter.startLeScan(mLeScanCallbac);
	}

	private LeScanCallback mLeScanCallbac = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.i(Tag, "扫描到设备 name: " + device.getName());
			Log.i(Tag, "扫描到设备 uuid: " + device.getUuids());
			Log.i(Tag, "扫描到设备 rssi: " + rssi);
			Log.i(Tag, "扫描到设备 scanRecord: " + scanRecord.length);
			Log.i(Tag, "扫描到设备 Manufacturers: " + Arrays.toString(scanRecord));
			
			final BleEntity entity = new BleEntity();
			if(scanRecord[7]!=2 || scanRecord[8] !=21)
			{
				Log.i(Tag, " 不是沃联的 设备。。跳过===================");
				return;
			}else{
				entity.major = (0xFF & scanRecord[25]) << 8 | (0xFF & scanRecord[26]);
				entity.minor = (0xFF & scanRecord[27]) << 8 | (0xFF & scanRecord[28]);
				String uuid = bytesToHex(Arrays.copyOfRange (scanRecord, 9,25) );
				entity.uuid = uuid;
			}
			
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					updateDevices(device, entity);
				}
			});

		}
	};

    protected static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    private String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));
            sb.append(" ");
        }
        return sb.toString().trim();
    }
	/**
	 * LoLLIPOP LeScanCallback
	 */
	// private ScanCallback mScanCallback = new ScanCallback() {
	// public void onBatchScanResults(
	// java.util.List<android.bluetooth.le.ScanResult> results) {
	//
	// };
	//
	// public void onScanFailed(int errorCode) {
	//
	// };
	//
	// public void onScanResult(int callbackType,
	// android.bluetooth.le.ScanResult result) {
	//
	// };
	// };

	private void init() {
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_scann_result = (TextView) findViewById(R.id.tv_scann_result);
		lv_list = (ListView) findViewById(R.id.lv_devices);
		mListDevices = new ArrayList<HashMap<String, String>>();
		mListAdapter = new SimpleAdapter(this, mListDevices, android.R.layout.simple_list_item_1, new String[] { "name" }, new int[] { android.R.id.text1 });
		lv_list.setAdapter(mListAdapter);
		lv_list.setOnItemClickListener(this);

		initBleManager();
	}

	private void updateStatus(String msg) {
		tv_status.setText(msg);
	}

	/**
	 * 展示获取到的 ble 设备
	 * 
	 * @param device
	 */
	private void updateDevices(BluetoothDevice device, BleEntity entity) {
		if (device == null)
			return;
		else {

			if (mListDevices != null) {
				if (mDevices == null)
					mDevices = new HashMap<String, BluetoothDevice>();
				HashMap<String, String> mentity = new HashMap<String, String>();
				mentity.put("name", device.getName() + " " + device.getAddress() + " major:" + entity.major + "  minor:"+ entity.minor);

				String mac = device.getAddress();
				mentity.put("address", mac);
				mListDevices.add(mentity);

				// 存储当前获取到的 device ，等用户选择时进行连接
				mDevices.put(mac, device);

				mListAdapter.notifyDataSetChanged();

			}
		}
	}

	/**
	 * 停止扫描
	 */
	private void stopScan() {
		mBleAdapter.stopLeScan(mLeScanCallbac);
		mIsScanning = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyScanner();
	}

	private void destroyScanner() {
		if (mBleAdapter != null) {
			if (mIsScanning)
				mBleAdapter.stopLeScan(mLeScanCallbac);
			mBleAdapter = null;
			mBleManager = null;

			Log.i(Tag, "stop  scann");
		}
	}

	/**
	 * 根据 mac 地址连接指定的设备
	 * 
	 * @param mac
	 */
	private void connect(String mac) {
		BluetoothDevice device = mDevices.get(mac);
		device.connectGatt(this, true, mGattCallback);

	}

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		public void onCharacteristicRead(BluetoothGatt gatt, android.bluetooth.BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS)
				Log.i(Tag, "onCharRead " + gatt.getDevice().getName() + " read " + characteristic.getUuid().toString() + " -> " + new String(characteristic.getValue() ));
		};


	};

	static class BleEntity {
		String address;
		String name;
		String uuid;
		int major;
		int minor;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HashMap<String, String> entity = (HashMap<String, String>) mListAdapter.getItem(position);
		stopScan();
		connect(entity.get("address"));
	}
}
