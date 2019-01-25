//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.libface.bh.camera;

import android.app.Activity;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RecordingUtil {
  private Class vedioRecorderUtil = null;
  private Object vedioRecorderObject = null;
  private Method vedioRecording;

  public RecordingUtil(int duration, String savePath) {
    try {
      this.vedioRecorderUtil = Class.forName("com.videorecorder.lib.recorder.VideoRecorderUtil");
      Constructor constructor = this.vedioRecorderUtil.getConstructor(Integer.TYPE, Integer.TYPE, String.class);
      this.vedioRecorderObject = constructor.newInstance(0, duration, savePath);
    } catch (ClassNotFoundException var4) {
      var4.printStackTrace();
      Log.e("RecordingUtil", "使用了视频录制功能，但是没有添加视频录制相关依赖包！");
    } catch (NoSuchMethodException var5) {
      var5.printStackTrace();
    } catch (IllegalAccessException var6) {
      var6.printStackTrace();
    } catch (InstantiationException var7) {
      var7.printStackTrace();
    } catch (InvocationTargetException var8) {
      var8.printStackTrace();
    }

  }

  public void initVideoEncode(Activity activity, int previewWidth, int previewHeight, int angle) {
    try {
      Method m = this.vedioRecorderUtil.getMethod("initVideoEncode", Activity.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
      m.invoke(this.vedioRecorderObject, activity, previewWidth, previewHeight, angle);
    } catch (NoSuchMethodException var6) {
      var6.printStackTrace();
    } catch (IllegalAccessException var7) {
      var7.printStackTrace();
    } catch (InvocationTargetException var8) {
      var8.printStackTrace();
    }

  }

  public void prepared() {
    try {
      Method m = this.vedioRecorderUtil.getMethod("prepared");
      m.invoke(this.vedioRecorderObject);
    } catch (NoSuchMethodException var2) {
      var2.printStackTrace();
    } catch (IllegalAccessException var3) {
      var3.printStackTrace();
    } catch (InvocationTargetException var4) {
      var4.printStackTrace();
    }

  }

  public void videoRecoder(byte[] data, boolean needRotate) {
    try {
      if (this.vedioRecording == null) {
        this.vedioRecording = this.vedioRecorderUtil.getMethod("videoRecoder", byte[].class, Boolean.TYPE);
      }

      this.vedioRecording.invoke(this.vedioRecorderObject, data, needRotate);
    } catch (NoSuchMethodException var4) {
      var4.printStackTrace();
    } catch (IllegalAccessException var5) {
      var5.printStackTrace();
    } catch (InvocationTargetException var6) {
      var6.printStackTrace();
    }

  }

  public void stopVideoRecoder() {
    try {
      Method m = this.vedioRecorderUtil.getMethod("stopVideoRecoder");
      m.invoke(this.vedioRecorderObject);
    } catch (NoSuchMethodException var2) {
      var2.printStackTrace();
    } catch (IllegalAccessException var3) {
      var3.printStackTrace();
    } catch (InvocationTargetException var4) {
      var4.printStackTrace();
    }

  }

  public String getFileName() {
    try {
      Method m = this.vedioRecorderUtil.getMethod("getFileName");
      return (String)m.invoke(this.vedioRecorderObject);
    } catch (NoSuchMethodException var2) {
      var2.printStackTrace();
    } catch (IllegalAccessException var3) {
      var3.printStackTrace();
    } catch (InvocationTargetException var4) {
      var4.printStackTrace();
    }

    return null;
  }
}
