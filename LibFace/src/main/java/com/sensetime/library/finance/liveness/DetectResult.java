//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.liveness;

import com.sensetime.library.finance.liveness.DetectInfo.FaceDistance;
import com.sensetime.library.finance.liveness.DetectInfo.FaceState;

final class DetectResult {
  boolean passed;
  int message;
  float hacknessScore;
  int faceCount;
  int left;
  int top;
  int right;
  int bottom;
  int faceId;
  FaceDistance faceDistance;
  FaceState faceState;

  DetectResult() {
  }

  public String toString() {
    return "DetectResult[Passed: " + this.passed + ", Message: " + this.message + ", Score: " + this.hacknessScore + ", Count: " + this.faceCount + ", Left: " + this.left + ", Top: " + this.top + ", Right: " + this.right + ", Bottom: " + this.bottom + ", ID: " + this.faceId + ", Distance: " + this.faceDistance + ", State: " + this.faceState + "]";
  }
}
