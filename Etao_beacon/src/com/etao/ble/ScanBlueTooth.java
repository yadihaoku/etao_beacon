package com.etao.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.etao.ble.BeaconManager.OnScanCallback;
import com.etao.ble.entity.Beacon;
import com.etao.ble.utils.WeakHandler;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ScanBlueTooth extends Activity implements OnItemClickListener, OnClickListener, OnScanCallback {

	private static final String Tag = ScanBlueTooth.class.getName();

	private TextView tv_status;
	private TextView tv_scann_result;
	private ListView lv_list;
	private Button btn_start_scan;

	private List<HashMap<String, String>> mListDevices;
	private HashMap<String, BluetoothDevice> mDevices;
	private BeaconAdapter mListAdapter;

	private Handler mHandler;
	
	private BeaconManager mBeaconManager;
	
	private static final int CMD_NEW_BEACON = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.scan_ble_layout);
		init();

	}

	private void init() {
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_scann_result = (TextView) findViewById(R.id.tv_scann_result);

		btn_start_scan = (Button) findViewById(R.id.btn_start_scan);
		btn_start_scan.setOnClickListener(this);

		mListDevices = new ArrayList<HashMap<String, String>>();
		mListAdapter = new BeaconAdapter();

		lv_list = (ListView) findViewById(R.id.lv_devices);
		lv_list.setAdapter(mListAdapter);
		lv_list.setOnItemClickListener(this);
		
		mHandler = new ScanHandler(this);
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyScanner();
	}

	private void destroyScanner() {
		if(null != mBeaconManager)
			mBeaconManager.stopScan();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Beacon beacon = mListAdapter.getItem(position);
	}

	/**
	 * beaconAdapter
	 * 
	 * @author YadiYan 2015-7-21
	 * 
	 */
	class BeaconAdapter extends BaseAdapter {
		public BeaconAdapter() {
			beacons = new ArrayList<Beacon>();
		}

		private List<Beacon> beacons;

		@Override
		public int getCount() {
			return beacons.size();
		}

		public void addItem(Beacon beacon) {
			this.beacons.add(beacon);
		}

		@Override
		public Beacon getItem(int position) {
			return beacons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tvItem = null;
			if (null == convertView) {
				tvItem = (TextView) getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
			} else {
				tvItem = (TextView) convertView;
			}
			Beacon beacon = getItem(position);
			tvItem.setText(String.format("%s  %d %d %d", beacon.getUuid(), beacon.getMajor(), beacon.getMinor(), beacon.getRssi()));
			return tvItem;
		}

	}

	@Override
	public void onClick(View v) {
		if(null == mBeaconManager){
			mBeaconManager = BeaconManager.newInstance(this);
			mBeaconManager.setScanCallback(this);
		}
		mBeaconManager.startScan();
	}
	/**
	 * 扫描到新的 beacon 时，调用此方法
	 * @param beacon
	 */
	private void addNewBeacon(Beacon beacon){
		mListAdapter.addItem(beacon);
		mListAdapter.notifyDataSetChanged();
	}
	
	public static class ScanHandler extends WeakHandler<ScanBlueTooth>{
		public ScanHandler(ScanBlueTooth t) {
			super(t);
		}

		@Override
		public void doMsg(Message msg, ScanBlueTooth act) {
			switch(msg.what){
			case CMD_NEW_BEACON:
			{
				Beacon beacon = (Beacon) msg.obj;
				act.addNewBeacon(beacon);
			}
				break;
			}
		}
	} 
	@Override
	public void onScan(Beacon mbeacon) {
		Message msg = mHandler.obtainMessage();
		msg.what = CMD_NEW_BEACON;
		msg.obj = mbeacon;
		msg.sendToTarget();
	}
}
