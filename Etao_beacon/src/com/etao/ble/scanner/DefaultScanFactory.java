package com.etao.ble.scanner;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
/**
 * 为了在低版本的终端上运行，使用工厂模式来创建不同的 IScanner 实例。在不支持 BleAdapter 的设备上，因为没有使用不存在的类，<br />
 * 所以不会 CNF 异常
 * @author YadiYan 2015-7-21
 *
 */
public class DefaultScanFactory implements ScannerFactory {

	private static ScannerFactory mInstance;

	public static ScannerFactory getFactory(Context context) {
		if (null == mInstance) {
			synchronized (DefaultScanFactory.class) {
				if (null == mInstance) {
					mInstance = new DefaultScanFactory(context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * use ApplicationContext
	 */
	private Context mContext;

	private DefaultScanFactory(Context context) {
		if (context instanceof Activity) {
			mContext = context.getApplicationContext();
		}

		else {
			mContext = context;
		}
	}

	@Override
	public IScanner getScanner() {
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
			return null;
		// 暂不支持 5.0  新版 API
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			return null;
		else
			return new ScannerJellyMr2(this.mContext);
	}

}
