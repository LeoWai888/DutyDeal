package com.gd.icbc.dutydeal.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import java.util.Locale;

public class Voice implements TextToSpeech.OnInitListener {
    Context context;
    private TextToSpeech tts;
    public Voice(Context context ){
        this.context=context;
        tts = new TextToSpeech(context, (TextToSpeech.OnInitListener) this);
    }
    @Override
    public void onInit(int status) {

        // 判断是否转化成功
        if (status == TextToSpeech.SUCCESS) {
            //默认设定语言为中文，原生的android貌似不支持中文。
            int result = tts.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("语音：", "CHINA数据丢失或不支持");
                result = tts.setLanguage(Locale.CHINESE);
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("语音：", "CHINESE数据丢失或不支持");
                //不支持中文就将语言设置为英文
                tts.setLanguage(Locale.US);
                Log.e("语音：", "不支持中文，改设置为英文");
            }
        }
    }

    public void speak(String msg) {
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }

   
}
