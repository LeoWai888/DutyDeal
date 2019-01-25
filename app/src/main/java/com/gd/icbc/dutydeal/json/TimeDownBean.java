package com.gd.icbc.dutydeal.json;

import android.graphics.Bitmap;
import com.gd.icbc.dutydeal.utils.BitmapUtils;

public class TimeDownBean {
    private String lastTime;
    private long useTime;
    private boolean timeFlag;
    private String content;
    private String photo;

    public TimeDownBean(String lastTime, long useTime, String content, String photo) {
        this.lastTime = lastTime;
        this.content = content;
        this.useTime = useTime;
        this.photo=photo;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public boolean isTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(boolean timeFlag) {
        this.timeFlag = timeFlag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getPhoto() {      //将string转换为bitmap
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
