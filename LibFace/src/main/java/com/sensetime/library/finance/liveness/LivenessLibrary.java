//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.liveness;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import com.sensetime.library.finance.FinanceLibrary;
import com.sensetime.library.finance.common.type.LibraryStatus;
import com.sensetime.library.finance.common.type.PixelFormat;
import com.sensetime.library.finance.common.type.Size;
import com.sensetime.library.finance.common.util.DeviceUtil;
import com.sensetime.library.finance.liveness.DetectInfo.FaceDistance;
import com.sensetime.library.finance.liveness.DetectInfo.FaceState;
import com.sensetime.library.finance.liveness.type.BoundInfo;
import java.util.ArrayList;
import java.util.List;

class LivenessLibrary extends FinanceLibrary {
  private static final float RATE_FACE_FAR = 0.3F;
  private static final float RATE_FACE_CLOSE = 0.8F;
  private static LibraryStatus sStatus;
  private byte[] mLastDetectProtoBufData;
  private List<byte[]> mLastDetectImages;
  private SensorManager mSensorManager;
  private SensorEventListener mSensorListener;

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  static LivenessLibrary getInstance() {
    return LivenessLibrary.InstanceHolder.INSTANCE;
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  @SuppressLint("WrongConstant")
  LivenessCode init(Context context, String licenseFilePath, String modelFilePath) {
    if (context != null) {
      this.mSensorManager = (SensorManager)context.getSystemService("sensor");
    }

    LivenessCode result = this.checkLicense(licenseFilePath);
    return result == LivenessCode.OK ? this.createHandle(modelFilePath) : result;
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  LivenessCode prepare(int config) {
    LivenessCode result = this.start(config);
    if (result != LivenessCode.OK) {
      return result;
    } else {
      this.clearLastDetectResults();
      this.setStaticInfo();
      return LivenessCode.OK;
    }
  }

  DetectResult detect(byte[] image, PixelFormat format, Size previewSize, Size containerSize, int cameraOrientation, BoundInfo boundInfo) {
    if (image != null && image.length >= 1) {
      if (previewSize != null && containerSize != null) {
        if (cameraOrientation < 0) {
          return null;
        } else if (sStatus != LibraryStatus.STARTED) {
          return null;
        } else {
          int nativeOrientation = cameraOrientation / 90;
          DetectResult result = this.wrapperInput(image, format.getCode(), previewSize.getWidth(), previewSize.getHeight(), previewSize.getWidth() * format.getWidth(), nativeOrientation, (double)System.currentTimeMillis() / 1000.0D);
          if (result != null && result.faceCount > 0) {
            this.mirrorFaceRect(result, previewSize, nativeOrientation);
            this.generateFaceDistance(result, previewSize, nativeOrientation);
            this.convertFaceRectImageToScreen(result, previewSize, containerSize, nativeOrientation);
          }

          this.generateFaceState(result, containerSize, boundInfo);
          return result;
        }
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  void stopDetect(boolean withResult, boolean withImages) {
    if (this.stop()) {
      if (withResult) {
        this.mLastDetectProtoBufData = this.getResult();
      }

      if (withImages) {
        List<byte[]> images = this.getImages();
        if (images != null && images.size() > 0) {
          this.mLastDetectImages = new ArrayList(images);
        }
      }

    }
  }

  List<byte[]> getLastDetectImages() {
    return this.mLastDetectImages;
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  void release() {
    if (sStatus == LibraryStatus.PREPARED || sStatus == LibraryStatus.STARTED || sStatus == LibraryStatus.STOPPED || sStatus == LibraryStatus.ERROR) {
      this.stop();
      this.destroyWrapperHandle();
      sStatus = LibraryStatus.IDLE;
    }
  }

  boolean setMotion(int motion) {
    if (sStatus != LibraryStatus.STARTED) {
      return false;
    } else {
      int result = this.nativeSetMotion(motion);
      return result == 0;
    }
  }

  byte[] getLastDetectProtoBufData() {
    return this.mLastDetectProtoBufData;
  }

  void clearLastDetectResults() {
    if (this.mLastDetectImages != null) {
      this.mLastDetectImages.clear();
      this.mLastDetectImages = null;
    }

    this.mLastDetectProtoBufData = null;
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  private LivenessLibrary(Object o) {
    this.mLastDetectProtoBufData = null;
    this.mLastDetectImages = null;
    this.mSensorManager = null;
    this.mSensorListener = new SensorEventListener() {
      public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()) {
          case 1:
            LivenessLibrary.this.addSequentialInfo(NativeSensorInfoKey.ACCLERATION.getValue(), sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2] + " ");
            break;
          case 2:
            LivenessLibrary.this.addSequentialInfo(NativeSensorInfoKey.MAGNETIC_FIELD.getValue(), sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2] + " ");
            break;
          case 9:
            LivenessLibrary.this.addSequentialInfo(NativeSensorInfoKey.GRAVITY.getValue(), sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2] + " ");
            break;
          case 11:
            LivenessLibrary.this.addSequentialInfo(NativeSensorInfoKey.ROTATION_RATE.getValue(), sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2] + " ");
        }

      }

      public void onAccuracyChanged(Sensor sensor, int i) {
      }
    };
  }

  private void mirrorFaceRect(DetectResult result, Size previewSize, int orientation) {
    Rect originRect = new Rect(result.left, result.top, result.right, result.bottom);
    Rect mirrorRect = new Rect(originRect);
    switch(orientation) {
      case 0:
      case 2:
        mirrorRect = new Rect(previewSize.getWidth() - originRect.right, originRect.top, previewSize.getWidth() - originRect.left, originRect.bottom);
        break;
      case 1:
        return;
      case 3:
        mirrorRect = new Rect(originRect.left, previewSize.getHeight() - originRect.bottom, originRect.right, previewSize.getHeight() - originRect.top);
    }

    result.left = mirrorRect.left;
    result.top = mirrorRect.top;
    result.right = mirrorRect.right;
    result.bottom = mirrorRect.bottom;
  }

  private void generateFaceState(DetectResult result, Size containerSize, BoundInfo boundInfo) {
    if (result.faceCount < 1) {
      result.faceState = FaceState.MISSED;
    } else if (boundInfo != null && this.verifyBoundInfo(containerSize, boundInfo)) {
      int faceX = result.right - (result.right - result.left) / 2;
      int faceY = result.bottom - (result.bottom - result.top) / 2;
      int distance = (int)Math.sqrt(Math.pow((double)Math.abs(faceX - boundInfo.getX()), 2.0D) + Math.pow((double)Math.abs(faceY - boundInfo.getY()), 2.0D));
      if (distance > boundInfo.getRadius()) {
        result.faceState = FaceState.OUT_OF_BOUND;
      } else {
        result.faceState = FaceState.NORMAL;
      }
    } else {
      result.faceState = FaceState.UNKNOWN;
    }
  }

  private boolean verifyBoundInfo(Size containerSize, BoundInfo boundInfo) {
    return containerSize.getWidth() >= 0 && containerSize.getHeight() >= 0 && boundInfo.getX() >= 0 && boundInfo.getY() >= 0 && boundInfo.getRadius() >= 0 && boundInfo.getX() + boundInfo.getRadius() <= containerSize.getWidth() && boundInfo.getX() - boundInfo.getRadius() >= 0 && boundInfo.getY() + boundInfo.getRadius() <= containerSize.getHeight() && boundInfo.getY() - boundInfo.getRadius() >= 0;
  }

  private byte[] getResult() {
    return sStatus != LibraryStatus.STOPPED ? null : this.wrapperGetResult();
  }

  private List<byte[]> getImages() {
    if (sStatus != LibraryStatus.STOPPED) {
      return null;
    } else {
      byte[][] frames = this.wrapperGetImages();
      if (frames != null && frames.length != 0) {
        List<byte[]> images = null;
        byte[][] var3 = frames;
        int var4 = frames.length;

        for(int var5 = 0; var5 < var4; ++var5) {
          byte[] frame = var3[var5];
          if (images == null) {
            images = new ArrayList();
          }

          images.add(frame);
        }

        return images;
      } else {
        return null;
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  private boolean stop() {
    if (sStatus != LibraryStatus.STARTED) {
      return false;
    } else {
      int result = this.wrapperEnd();
      this.unregisterSensorListener();
      if (result == 0) {
        sStatus = LibraryStatus.STOPPED;
        return true;
      } else {
        sStatus = LibraryStatus.ERROR;
        return false;
      }
    }
  }

  private void generateFaceDistance(DetectResult result, Size previewSize, int orientation) {
    float faceWidthRatio = 0.0F;
    switch(orientation) {
      case 0:
      case 2:
        faceWidthRatio = (float)(result.right - result.left) / (float)previewSize.getWidth();
        break;
      case 1:
      case 3:
        faceWidthRatio = (float)(result.bottom - result.top) / (float)previewSize.getHeight();
    }

    result.faceDistance = faceWidthRatio < 0.3F ? FaceDistance.FAR : (faceWidthRatio > 0.8F ? FaceDistance.CLOSE : FaceDistance.NORMAL);
  }

  private void addSequentialInfo(int type, String info) {
    if (sStatus == LibraryStatus.STARTED && type >= 0 && !TextUtils.isEmpty(info)) {
      this.wrapperAddSequentialInfo(type, info);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  private void registerSensorListener() {
    if (this.mSensorManager != null) {
      this.mSensorManager.registerListener(this.mSensorListener, this.mSensorManager.getDefaultSensor(1), 3);
      this.mSensorManager.registerListener(this.mSensorListener, this.mSensorManager.getDefaultSensor(11), 3);
      this.mSensorManager.registerListener(this.mSensorListener, this.mSensorManager.getDefaultSensor(9), 3);
      this.mSensorManager.registerListener(this.mSensorListener, this.mSensorManager.getDefaultSensor(2), 3);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  private void unregisterSensorListener() {
    if (this.mSensorManager != null) {
      this.mSensorManager.unregisterListener(this.mSensorListener);
    }
  }

  private void convertFaceRectImageToScreen(DetectResult result, Size previewSize, Size containerSize, int orientation) {
    Rect rotateRect = null;
    switch(orientation) {
      case 0:
        rotateRect = new Rect(result.left, result.top, result.right, result.bottom);
        break;
      case 1:
        rotateRect = new Rect(previewSize.getHeight() - result.bottom, result.left, previewSize.getHeight() - result.top, result.right);
        previewSize = new Size(previewSize.getHeight(), previewSize.getWidth());
        break;
      case 2:
        rotateRect = new Rect(previewSize.getWidth() - result.right, previewSize.getHeight() - result.bottom, previewSize.getWidth() - result.left, previewSize.getHeight() - result.top);
        break;
      case 3:
        rotateRect = new Rect(result.top, previewSize.getWidth() - result.right, result.bottom, previewSize.getWidth() - result.left);
        previewSize = new Size(previewSize.getHeight(), previewSize.getWidth());
    }

    if (rotateRect != null) {
      float scale = (float)(containerSize.getHeight() / previewSize.getHeight());
      result.left = (int)((float)rotateRect.left * scale);
      result.top = (int)((float)rotateRect.top * scale);
      result.right = containerSize.getWidth() - (int)((float)(previewSize.getWidth() - rotateRect.right) * scale);
      result.bottom = containerSize.getHeight() - (int)((float)(previewSize.getHeight() - rotateRect.bottom) * scale);
    }
  }

  private void setStaticInfo() {
    if (sStatus == LibraryStatus.STARTED) {
      this.wrapperSetStaticInfo(NativeStaticInfoKey.DEVICE.getKeyValue(), Build.MODEL);
      this.wrapperSetStaticInfo(NativeStaticInfoKey.OS.getKeyValue(), "Android");
      this.wrapperSetStaticInfo(NativeStaticInfoKey.SDK_VERSION.getKeyValue(), "3.0");
      this.wrapperSetStaticInfo(NativeStaticInfoKey.SYS_VERSION.getKeyValue(), VERSION.RELEASE);
      this.wrapperSetStaticInfo(NativeStaticInfoKey.IS_ROOT.getKeyValue(), String.valueOf(DeviceUtil.isRoot()));
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  private LivenessCode start(int config) {
    if (sStatus != LibraryStatus.PREPARED && sStatus != LibraryStatus.STOPPED) {
      return LivenessCode.ERROR_WRONG_STATE;
    } else {
      int result = this.wrapperBegin(config);
      if (result == 0) {
        sStatus = LibraryStatus.STARTED;
        this.registerSensorListener();
        return LivenessCode.OK;
      } else {
        sStatus = LibraryStatus.ERROR;
        return LivenessCode.ERROR_CHECK_CONFIG_FAIL;
      }
    }
  }

  private LivenessCode createHandle(String modelFilePath) {
    if (TextUtils.isEmpty(modelFilePath)) {
      return LivenessCode.ERROR_MODEL_FILE_NOT_FOUND;
    } else if (sStatus != LibraryStatus.INITIALIZED) {
      return LivenessCode.ERROR_WRONG_STATE;
    } else {
      int result = this.createWrapperHandle(modelFilePath);
      if (result == 0) {
        sStatus = LibraryStatus.PREPARED;
        return LivenessCode.OK;
      } else {
        sStatus = LibraryStatus.ERROR;
        return result == -7 ? LivenessCode.ERROR_MODEL_FILE_NOT_FOUND : LivenessCode.ERROR_CHECK_MODEL_FAIL;
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  private LivenessCode checkLicense(String licenseFilePath) {
    if (TextUtils.isEmpty(licenseFilePath)) {
      return LivenessCode.ERROR_LICENSE_FILE_NOT_FOUND;
    } else {
      if (sStatus != LibraryStatus.IDLE) {
        this.stop();
        this.release();
        if (sStatus != LibraryStatus.IDLE) {
          return LivenessCode.ERROR_WRONG_STATE;
        }
      }

      int result = this.initLicense(licenseFilePath);
      if (result != 0 && result != -256) {
        sStatus = LibraryStatus.ERROR;
        switch(result) {
          case -15:
            return LivenessCode.ERROR_LICENSE_EXPIRE;
          case -14:
            return LivenessCode.ERROR_LICENSE_PACKAGE_NAME_MISMATCH;
          case -7:
            return LivenessCode.ERROR_LICENSE_FILE_NOT_FOUND;
          default:
            return LivenessCode.ERROR_CHECK_LICENSE_FAIL;
        }
      } else {
        sStatus = LibraryStatus.INITIALIZED;
        return LivenessCode.OK;
      }
    }
  }

  private static void loadLibrary() {
    try {
      System.loadLibrary("cvfinance_api_liveness_standard");
      System.loadLibrary("st_finance");
    } catch (UnsatisfiedLinkError var1) {
      sStatus = LibraryStatus.ERROR;
      var1.printStackTrace();
    }

  }

  @SuppressWarnings("JNIMissingFunction")
  private native int createWrapperHandle(String var1);

  private native int wrapperBegin(int var1);

  private native DetectResult wrapperInput(byte[] var1, int var2, int var3, int var4, int var5, int var6, double var7);

  private native int wrapperEnd();

  private native int wrapperSetStaticInfo(int var1, String var2);

  private native byte[] wrapperGetResult();

  private native byte[][] wrapperGetImages();

  private native void destroyWrapperHandle();

  private native int wrapperAddSequentialInfo(int var1, String var2);

  private native int nativeSetMotion(int var1);

  static {
    sStatus = LibraryStatus.IDLE;
    loadLibrary();
  }

  @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
  private static final class InstanceHolder {
    private static final LivenessLibrary INSTANCE = new LivenessLibrary(null);

    private InstanceHolder() {
    }
  }
}
