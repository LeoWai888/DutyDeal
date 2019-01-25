package com.gd.icbc.dutydeal.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

/**
 * JDK自带API计算两个日期的差值
 *
 * @author gcc
 * <p>
 * 2018年1月25日
 */
public class TimeUtils {


    public static Date stringToDate(String timeStr) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public static long differenceSenconds(Date startDate, Date endDate) {

        return (endDate.getTime() - startDate.getTime()) / 1000;
    }

    public static long differenceSenconds(String startDate, String endDate) {

        return (stringToDate(endDate).getTime() - stringToDate(startDate).getTime()) / 1000;
    }

    //倒计时
    //参数为倒计时总豪秒数
    //返回为具体倒计时的时间字符串（HH:MM:SS)

    public static String CountDownSenconds(long SencondNum) {
        long hour = (long) (SencondNum / 3600);
        long min = (long) (SencondNum / 60 % 60);
        long second = (long) (SencondNum % 60);
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }

    /*
    返回某个特定时间字符串（"yyyy-MM-dd HH:mm:ss"）前一天时间
     */
    public static Date getSpecifiedDayBefore(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = stringToDate(specifiedDay);
        date.setTime(date.getTime() - 3600 * 24 * 1000);
        return date;
    }

    /*
    将HH:mm:ss字符串转为文字说明
     */

    public static String getSpeakTimeString(String TimeString) {
        String result = "";
        int hourNum = Integer.valueOf(TimeString.substring(1, 2));
        if (hourNum > 0) {
            result = result + ToCH(hourNum) + "时";
        }
        int minutesNum = Integer.valueOf(TimeString.substring(4, 5));
        if (minutesNum > 0) {
            result = result + ToCH(minutesNum) + "分";
        }
        int secondNum = Integer.valueOf(TimeString.substring(7, 8));
        if (secondNum > 0) {
            result = result + ToCH(secondNum) + "秒";
        }
        return result;

    }

    public static String ToCH(int intInput) {
        String si = String.valueOf(intInput);
        String sd = "";
        if (si.length() == 1) // 個
        {
            sd += GetCH(intInput);
            return sd;
        } else if (si.length() == 2)// 十
        {
            if (si.substring(0, 1).equals("1")) sd += "十";
            else sd += (GetCH(intInput / 10) + "十");
            sd += ToCH(intInput % 10);
        } else if (si.length() == 3)// 百
        {
            sd += (GetCH(intInput / 100) + "百");
            if (String.valueOf(intInput % 100).length() < 2) sd += "零";
            sd += ToCH(intInput % 100);
        } else if (si.length() == 4)// 千
        {
            sd += (GetCH(intInput / 1000) + "千");
            if (String.valueOf(intInput % 1000).length() < 3) sd += "零";
            sd += ToCH(intInput % 1000);
        } else if (si.length() == 5)// 萬
        {
            sd += (GetCH(intInput / 10000) + "萬");
            if (String.valueOf(intInput % 10000).length() < 4) sd += "零";
            sd += ToCH(intInput % 10000);
        }

        return sd;
    }

    private static String GetCH(int input) {
        String sd = "";
        switch (input) {
            case 1:
                sd = "一";
                break;
            case 2:
                sd = "二";
                break;
            case 3:
                sd = "三";
                break;
            case 4:
                sd = "四";
                break;
            case 5:
                sd = "五";
                break;
            case 6:
                sd = "六";
                break;
            case 7:
                sd = "七";
                break;
            case 8:
                sd = "八";
                break;
            case 9:
                sd = "九";
                break;
            default:
                break;
        }
        return sd;
    }

    public static String getTimeAfterSeconds(String specifiedTime, long seconds) {
        Date date = new Date(stringToDate(specifiedTime).getTime() + seconds * 1000);
        return DateFormat.format("yyyy-MM-dd HH:mm:ss", date).toString();//时间显示格式
    }

    public static String getCurrentTimeString() {
        Date date = new Date();
        return DateFormat.format("yyyy-MM-dd HH:mm:ss", date).toString();//时间显示格式
    }

}
