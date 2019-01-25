//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.liveness.type;

public final class BoundInfo {
  private int mX = -1;
  private int mY = -1;
  private int mRadius = -1;

  public BoundInfo(int x, int y, int radius) {
    this.mX = x;
    this.mY = y;
    this.mRadius = radius;
  }

  public int getX() {
    return this.mX;
  }

  public int getY() {
    return this.mY;
  }

  public int getRadius() {
    return this.mRadius;
  }

  public String toString() {
    return "BoundInfo[X: " + this.mX + ", Y: " + this.mY + ", Radius: " + this.mRadius + "]";
  }
}
