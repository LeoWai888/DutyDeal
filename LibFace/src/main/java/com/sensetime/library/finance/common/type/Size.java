//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.common.type;

public class Size {
  private int mWidth;
  private int mHeight;

  public Size(int width, int height) {
    this.mWidth = width;
    this.mHeight = height;
  }

  public int getWidth() {
    return this.mWidth;
  }

  public int getHeight() {
    return this.mHeight;
  }

  public String toString() {
    return "Size[Width: " + this.mWidth + ", Height: " + this.mHeight + "]";
  }
}
