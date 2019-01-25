package com.gd.icbc.dutydeal.utils;

import java.io.DataOutputStream;
import java.io.IOException;

public class LedUtils {

	private static int mLedConfig = 0;
	
	public static void prepare(){
		if (mLedConfig == 2){
			return;
		}
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream dos = new DataOutputStream(
					process.getOutputStream());
			dos.writeBytes("echo O_GPIOX_18 > /sys/class/aml_gpio/export"
					+ "\n");
			dos.flush();

			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void openLed() {
		if (mLedConfig == 2){
			return;
		}
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream dos = new DataOutputStream(
					process.getOutputStream());
			dos.writeBytes("echo L_GPIOX_18 > /sys/class/aml_gpio/gpio" + "\n");
			dos.flush();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeLed() {
		if (mLedConfig == 2){
			return;
		}
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream dos = new DataOutputStream(
					process.getOutputStream());
			dos.writeBytes("echo H_GPIOX_18 > /sys/class/aml_gpio/gpio" + "\n");
			dos.flush();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateLedState(int ledConfig) {
		mLedConfig = ledConfig;
		switch (ledConfig) {
			case 1:
				openLed();
				break;
			default:
				closeLed();
				break;
		}
	}
}
