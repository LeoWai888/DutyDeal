package com.gd.icbc.dutydeal.utils;

import android.os.Looper;
import android.widget.Toast;

import com.gd.icbc.dutydeal.base.MyApplication;

public class ToastUtil {
    public static void showToast(String msg){
        if(isMainThread()){
            Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
        }else{
            Looper.prepare();
            Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    public static void showLongToast(String msg){
        if(isMainThread()){
            Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_LONG).show();
        }else{
            Looper.prepare();
            Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_LONG).show();
            Looper.loop();
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

}
