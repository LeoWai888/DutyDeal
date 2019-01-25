package com.gd.icbc.dutydeal.base;

import android.app.Application;
import android.content.Context;
import android.text.format.DateFormat;

import com.blankj.utilcode.util.Utils;
import com.gd.icbc.dutydeal.utils.GrgLog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyApplication extends Application implements Thread.UncaughtExceptionHandler{

    // 建立请求队列
    private static Context appContext;
    public static String LOG_PATH = "";

    public static final String PARENT_FILE_PATH = android.os.Environment.getExternalStorageDirectory().getPath()
            + "/dutydeal";

    private static String sysTimeStr;//当前系统时间

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();
        Date sysTime = new Date();

        sysTimeStr = DateFormat.format("HH:mm:ss", sysTime).toString();//时间显示格式


        Utils.init(this);

        LOG_PATH = PARENT_FILE_PATH + "/log/" + new SimpleDateFormat(GrgLog.DATE_FORMAT).format(new Date());

        GrgLog.init(LOG_PATH);

        Thread.setDefaultUncaughtExceptionHandler(this);


    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e == null) {
            return;
        }
        GrgLog.e("DutyDeal", "uncaughtException", e);
    }

    public static Context getContext()
    {
        return appContext;
    }
}
