package com.etao.ble.utils;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

/**
 * 持有弱引用 的 Handler ，解决 Handler 持有 Activity 造成内存泄露的bug.
 * @author YadiYan 2015-7-21
 *
 * @param <T>
 */
public abstract class WeakHandler<T> extends Handler {
	public WeakHandler(T t) {
		mReference = new WeakReference<T>(t);
	}
	private WeakReference<T> mReference;
	protected T getInstance() {
		return mReference.get();
	}
	@Override
	public final void handleMessage(Message msg) {
		T t = getInstance();
		if(t == null)return;
		doMsg(msg, t);
	}
	public abstract void doMsg(Message msg, T t);
	
}
