package com.example.kwoncheolhyeok.core.Camera;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by juyeol on 2017-07-04.
 * 이미지 파일들의 Uri를 얻어옴
 */

public class ProviderUtil {
  private static final String TAG = "ProviderUtil";

  private static File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
  private static String imageFileName = "";
  private static String outputPath = "";

  public static File getOutputFile(Uri uri) {
    return getOutputFile(new File(uri.getPath()).getName());
  }

  public static File getOutputFile(String imageFileName) {
    return new File(imageDirectory, imageFileName);
  }

  public static String getOutputFilePath(Uri uri) {
    return getOutputFile(uri).getAbsolutePath();
  }

  public static String getOutputFilePath(String imageFileName) {
    return getOutputFile(imageFileName).getAbsolutePath();
  }

  public static Uri getOutputMediaFileUri(Context context, File file) {
    Log.d("asd", "file:"+file);
    Log.d("asd", "BuildConfig.APPLICATION_ID:"+ BuildConfig.APPLICATION_ID);
    return FileProvider.getUriForFile(context, "com.example.kwoncheolhyeok.core.camera", file);
  }

  public static Uri getOutputMediaFileUri(Context context) {
    try {
      return getOutputMediaFileUri(context, getOutputMediaFile());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static File getOutputMediaFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    imageFileName = "IMG_" + timeStamp;
    File storageDir = new File(imageDirectory, "Camera");
    File image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );

    outputPath = "file:" + image.getAbsolutePath();
    return image;
  }

}
