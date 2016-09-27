package com.huchao.schoolgroup.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by bjhujunjie on 2016/9/20.
 */
public class BitmapTools {

  public static void compressBmpToFile(Bitmap bmp){
    File tmpDir=new File(Environment.getExternalStorageDirectory()+"/temp");    //保存地址及命名
    if (!tmpDir.exists()){
      tmpDir.mkdir(); //生成目录进行保存
    }
    File file=new File(tmpDir.getAbsolutePath()+"/temp.png");
    Log.e("test","file path = " + file.getPath());
    file.delete();
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int options = 80;//个人喜欢从80开始,
    bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
    while (baos.toByteArray().length / 1024 > 100) {
      baos.reset();
      options -= 10;
      bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
      Log.e("test","质量压缩到原来的" + options + "%时大小为：" + baos.toByteArray().length + "byte");
    }
    try {
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(baos.toByteArray());
      fos.flush();
      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 回收位图对象
   *
   * @param bitmap
   */
  public static void recycleBitmap(Bitmap bitmap) {
    if (bitmap != null && !bitmap.isRecycled()) {
      bitmap.recycle();
      System.gc();
      bitmap = null;
    }
  }

  public static int dp2px(Context context, float dp) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }

  public static int px2dp(Context context, float px) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (px / scale + 0.5f);
  }
}
