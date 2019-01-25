package com.gd.icbc.dutydeal.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {

    private Context context;
    public Preferences(Context context)
    {
        this.context=context;
    }


    public void save(String areaNo)
    {
        //保存文件名字为"shared",保存形式为Context.MODE_PRIVATE即该数据只能被本应用读取
        SharedPreferences preferences=context.getSharedPreferences("shared",Context.MODE_PRIVATE);

        Editor editor=preferences.edit();
        editor.putString("AREA_NO", areaNo);

        editor.commit();//提交数据
    }


}