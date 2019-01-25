//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sensetime.library.finance.common.util;

import java.io.File;

public final class DeviceUtil {
  public static boolean isRoot() {
    try {
      String[] kSuSearchPaths = new String[]{"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
      String[] var1 = kSuSearchPaths;
      int var2 = kSuSearchPaths.length;

      for(int var3 = 0; var3 < var2; ++var3) {
        String path = var1[var3];
        File f = new File(path + "su");
        if (f.exists()) {
          return true;
        }
      }
    } catch (Exception var6) {
      var6.printStackTrace();
    }

    return false;
  }

  private DeviceUtil() {
  }
}
