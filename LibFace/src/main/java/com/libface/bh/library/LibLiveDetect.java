//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.libface.bh.library;

import android.content.Context;
import com.sensetime.library.finance.common.type.PixelFormat;
import com.sensetime.library.finance.liveness.LivenessCode;
import com.sensetime.library.finance.liveness.MotionLivenessApi;
import java.util.List;

public final class LibLiveDetect {
  public static final int EYEBLINK = 0;
  public static final int OPENMOUTH = 1;
  public static final int YAW = 2;
  public static final int DOWN_PITCH = 3;

  public LibLiveDetect() {
  }

  public static LibLiveDetect getInstance() {
    return LibLiveDetect.InstanceHolder.INSTANCE;
  }

  public static String getVersion() {
    return "5.0.0.1";
  }

  public static ResultCode init(Context context, String licenseFilePath, String modelFilePath) {
    LivenessCode code = MotionLivenessApi.init(context, licenseFilePath, modelFilePath);
    return ResultCode.valueOf(code.name());
  }

  public synchronized void stopDetect(boolean saveProtoBufData, boolean saveImages) {
    MotionLivenessApi.getInstance().stopDetect(saveProtoBufData, saveImages);
  }

  public synchronized byte[] getLastDetectProtoBufData() {
    return MotionLivenessApi.getInstance().getLastDetectProtoBufData();
  }

  public synchronized List<byte[]> getLastDetectImages() {
    return MotionLivenessApi.getInstance().getLastDetectImages();
  }

  public synchronized void clearLastDetectResult() {
    MotionLivenessApi.getInstance().clearLastDetectResult();
  }

  public synchronized void release() {
    MotionLivenessApi.getInstance().release();
  }

  public synchronized ResultCode prepare(int complexity) {
    return ResultCode.valueOf(MotionLivenessApi.getInstance().prepare(complexity).name());
  }

  public synchronized FaceInfo detect(byte[] image, Size previewSize, Size containerSize, int cameraOrientation, BoundaryInfo boundaryInfo) {
    return new FaceInfo(MotionLivenessApi.getInstance().detect(image, PixelFormat.NV21, previewSize.getSize(), containerSize.getSize(), cameraOrientation, boundaryInfo.getInfo()));
  }

  public synchronized boolean setMotion(int motion) {
    return MotionLivenessApi.getInstance().setMotion(motion);
  }

  public byte[] getDetectImageData() {
    List<byte[]> images = MotionLivenessApi.getInstance().getLastDetectImages();
    return images != null && !images.isEmpty() ? (byte[])images.get(0) : null;
  }

  private static final class InstanceHolder {
    private static final LibLiveDetect INSTANCE = new LibLiveDetect();

    private InstanceHolder() {
    }
  }
}
