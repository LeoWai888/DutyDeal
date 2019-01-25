//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.liveness;

enum NativeStaticInfoKey {
  DEVICE(0),
  OS(1),
  SDK_VERSION(2),
  SYS_VERSION(3),
  IS_ROOT(4),
  IDFA(5),
  CONTROL_SEQ(6),
  CUSTOMER_ID(7);

  private int mValue = -1;

  int getKeyValue() {
    return this.mValue;
  }

  private NativeStaticInfoKey(int value) {
    this.mValue = value;
  }
}
