package com.gd.icbc.dutydeal.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gd.icbc.dutydeal.Activity.HomeActivity;


/**
 * 
 *本类是开机广播
 * 
 *@author zjxin2 on 2016-10-10
 *@version  
 *
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
	
	private final String TAG = "BootBroadcastReceiver";
	
	public BootBroadcastReceiver() {}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.w(TAG, "action = " + intent.getAction()
				+ "\n package = " + intent.getPackage()
				+ "\n dataStr = " + intent.getDataString()
				+ "\n Cur package = " + context.getPackageName());
		String action = intent.getAction();
		if(action.equals("android.intent.action.BOOT_COMPLETED")) {
			startCurApp(context, HomeActivity.class);
		}

	}
	
	private void startCurApp(Context context, Class<?> cls) {
		Intent intentStart = new Intent(context, cls);
		intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intentStart);
	}
	
	
	private static OnReceiveListener mOnReceiveListener;
	
	public static void setOnReceiveListener (OnReceiveListener listener) {
		mOnReceiveListener = listener;
	}
	
	public interface OnReceiveListener {
		public void onPkgAdd(String pkgInfo);
	}

}
