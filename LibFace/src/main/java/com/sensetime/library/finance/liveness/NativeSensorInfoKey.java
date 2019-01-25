//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.liveness;

enum NativeSensorInfoKey {
  ACCLERATION(0),
  ROTATION_RATE(1),
  GRAVITY(2),
  MAGNETIC_FIELD(3);

  private int mValue;

  private NativeSensorInfoKey(int value) {
    this.mValue = value;
  }

  int getValue() {
    return this.mValue;
  }
}
