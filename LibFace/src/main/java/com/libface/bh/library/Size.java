//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.libface.bh.library;

public final class Size {
  private com.sensetime.library.finance.common.type.Size size;

  public Size(int width, int height) {
    this.size = new com.sensetime.library.finance.common.type.Size(width, height);
  }

  public com.sensetime.library.finance.common.type.Size getSize() {
    return this.size;
  }

  public int getWidth() {
    return this.size.getWidth();
  }

  public int getHeight() {
    return this.size.getHeight();
  }

  public String toString() {
    return "Size[Width: " + this.getWidth() + ", Height: " + this.getHeight() + "]";
  }
}
