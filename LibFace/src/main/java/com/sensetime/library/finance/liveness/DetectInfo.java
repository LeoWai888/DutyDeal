//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.liveness;

import android.graphics.Rect;

public final class DetectInfo {
  private DetectResult mResult = null;

  DetectInfo(DetectResult result) {
    this.mResult = result;
  }

  public boolean isPass() {
    return this.mResult != null && this.mResult.passed;
  }

  public Rect getFaceRect() {
    return this.mResult == null ? null : new Rect(this.mResult.left, this.mResult.top, this.mResult.right, this.mResult.bottom);
  }

  public DetectInfo.FaceDistance getFaceDistance() {
    return this.mResult == null ? DetectInfo.FaceDistance.UNKNOWN : this.mResult.faceDistance;
  }

  public DetectInfo.FaceState getFaceState() {
    return this.mResult == null ? DetectInfo.FaceState.UNKNOWN : this.mResult.faceState;
  }

  DetectInfo() {
  }

  public String toString() {
    return "DetectInfo[Pass: " + this.isPass() + ", Distance: " + this.getFaceDistance() + ", State: " + this.getFaceState() + "]";
  }

  public static enum FaceState {
    NORMAL,
    MISSED,
    CHANGED,
    OUT_OF_BOUND,
    UNKNOWN;

    private FaceState() {
    }
  }

  public static enum FaceDistance {
    NORMAL,
    CLOSE,
    FAR,
    UNKNOWN;

    private FaceDistance() {
    }
  }
}
