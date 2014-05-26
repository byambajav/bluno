package com.example.bluno_demo;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RSSIService extends Service {
	private static final String TAG = RSSIService.class.getSimpleName();
	BluetoothGatt mBluetoothGatt;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStarted");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroyed");
	}
}