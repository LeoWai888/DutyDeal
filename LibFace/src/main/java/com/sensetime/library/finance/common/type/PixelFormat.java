//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.common.type;

public enum PixelFormat {
  GRAY8(0, 1),
  YUV420P(1, 1),
  NV12(2, 1),
  NV21(3, 1),
  BGRA8888(4, 4),
  BGR888(5, 3);

  private int mCode = 0;
  private int mWidth = 0;

  private PixelFormat(int code, int width) {
    this.mCode = code;
    this.mWidth = width;
  }

  public int getWidth() {
    return this.mWidth;
  }

  public int getCode() {
    return this.mCode;
  }
}
