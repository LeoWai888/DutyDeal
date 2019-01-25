//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.liveness;

import android.content.Context;
import com.sensetime.library.finance.common.type.PixelFormat;
import com.sensetime.library.finance.common.type.Size;
import com.sensetime.library.finance.liveness.type.BoundInfo;
import java.util.List;

public final class MotionLivenessApi {
  public static MotionLivenessApi getInstance() {
    return MotionLivenessApi.InstanceHolder.INSTANCE;
  }

  public static String getVersion() {
    return "3.0";
  }

  public static LivenessCode init(Context context, String licenseFilePath, String modelFilePath) {
    return LivenessLibrary.getInstance().init(context, licenseFilePath, modelFilePath);
  }

  public synchronized void stopDetect(boolean saveProtoBufData, boolean saveImages) {
    LivenessLibrary.getInstance().stopDetect(saveProtoBufData, saveImages);
  }

  public synchronized byte[] getLastDetectProtoBufData() {
    return LivenessLibrary.getInstance().getLastDetectProtoBufData();
  }

  public synchronized List<byte[]> getLastDetectImages() {
    return LivenessLibrary.getInstance().getLastDetectImages();
  }

  public synchronized void clearLastDetectResult() {
    LivenessLibrary.getInstance().clearLastDetectResults();
  }

  public synchronized void release() {
    LivenessLibrary.getInstance().release();
  }

  public synchronized LivenessCode prepare(int complexity) {
    return LivenessLibrary.getInstance().prepare(complexity | 1);
  }

  public synchronized DetectInfo detect(byte[] image, PixelFormat format, Size previewSize, Size containerSize, int cameraOrientation, BoundInfo boundInfo) {
    return new DetectInfo(LivenessLibrary.getInstance().detect(image, format, previewSize, containerSize, cameraOrientation, boundInfo));
  }

  public synchronized boolean setMotion(int motion) {
    return LivenessLibrary.getInstance().setMotion(motion);
  }

  private MotionLivenessApi() {
  }

  private static final class InstanceHolder {
    private static final MotionLivenessApi INSTANCE = new MotionLivenessApi();

    private InstanceHolder() {
    }
  }
}
