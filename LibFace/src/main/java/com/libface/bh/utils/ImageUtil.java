//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.libface.bh.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class ImageUtil {
  public ImageUtil() {
  }

  public static void saveBitmapToFile(Bitmap bitmap, String filePath) {
    try {
      File file = new File(filePath);
      if (!file.exists() && file.getParentFile().mkdirs() && !file.createNewFile()) {
        return;
      }

      OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
      bitmap.compress(CompressFormat.JPEG, 100, os);
      os.close();
    } catch (IOException var4) {
      var4.printStackTrace();
    }

  }
}
