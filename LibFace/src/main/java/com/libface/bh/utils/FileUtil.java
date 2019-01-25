//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.libface.bh.utils;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtil {
  public FileUtil() {
  }

  public static boolean copyAssets(Context context, String[] fileNames, String save2path) {
    if (fileNames == null) {
      return false;
    } else {
      try {
        String[] var6 = fileNames;
        int var5 = fileNames.length;

        for(int var4 = 0; var4 < var5; ++var4) {
          String fileName = var6[var4];
          copyFile2Storage(context, fileName, save2path);
        }

        return true;
      } catch (Exception var7) {
        return false;
      }
    }
  }

  private static boolean copyFile2Storage(Context mContext, String mFileName, String mTargetDir) {
    try {
      File targetDir = new File(mTargetDir);
      if (!targetDir.exists()) {
        targetDir.mkdirs();
      } else if ((new File(targetDir, mFileName)).exists()) {
        return true;
      }

      InputStream modelIs = mContext.getAssets().open(mFileName);
      FileOutputStream fileOut = new FileOutputStream(new File(targetDir, mFileName));
      byte[] buffer = new byte[1024];
      boolean var7 = true;

      int len;
      while((len = modelIs.read(buffer)) > 0) {
        fileOut.write(buffer, 0, len);
      }

      fileOut.flush();
      fileOut.close();
      modelIs.close();
      return true;
    } catch (IOException var8) {
      return false;
    }
  }
}
