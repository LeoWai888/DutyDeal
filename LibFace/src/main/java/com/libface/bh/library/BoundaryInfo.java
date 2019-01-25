//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.libface.bh.library;

import com.sensetime.library.finance.liveness.type.BoundInfo;

public final class BoundaryInfo {
  private BoundInfo info;

  public BoundaryInfo(int x, int y, int radius) {
    this.info = new BoundInfo(x, y, radius);
  }

  public int getX() {
    return this.info.getX();
  }

  public int getY() {
    return this.info.getY();
  }

  public int getRadius() {
    return this.info.getRadius();
  }

  public BoundInfo getInfo() {
    return this.info;
  }

  public String toString() {
    return "BoundInfo[X: " + this.getX() + ", Y: " + this.getY() + ", Radius: " + this.getRadius() + "]";
  }
}
