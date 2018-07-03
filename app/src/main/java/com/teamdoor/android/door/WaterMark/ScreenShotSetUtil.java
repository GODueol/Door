package com.teamdoor.android.door.WaterMark;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;

import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.BitmapUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by soochun on 2017-01-16.
 */

public class ScreenShotSetUtil {
    public static int dpToPx(int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics()));
    }

    public static Bitmap getOverlayBitmap(Context context, Bitmap bitmap, String text) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        float scale = context.getResources().getDisplayMetrics().density;
        Canvas canvas = new Canvas(result);
        TextPaint mTextPaint = new TextPaint();

        // "이 화면은 도어 앱에서 촬영된 화면입니다" 부분
        mTextPaint.setTextSize((int) (16 * scale));
        mTextPaint.setColor(Color.RED);
        mTextPaint.setAlpha(30);
        StaticLayout mTextLayout = new StaticLayout(text, mTextPaint, canvas.getWidth() + 700, Layout.Alignment.ALIGN_CENTER, 1.1f, 0.3f, true);
        canvas.save();

        float textX = -200;
        float textY = -10;

        canvas.translate(textX, textY);
        mTextLayout.draw(canvas);
        canvas.restore();
        return result;
    }

    public static Bitmap getOverlayBitmap2(Context context, Bitmap bitmap, String text) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        float scale = context.getResources().getDisplayMetrics().density;
        Canvas canvas = new Canvas(result);

        // "본인 의지와 무관한 아웃팅은~" 부분
        TextPaint mTextPaint = new TextPaint();
        mTextPaint.setTextSize((int) (14 * scale));
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAlpha(1000);
        mTextPaint.setShadowLayer(5f, 0f, 1f, Color.DKGRAY);

        StaticLayout mTextLayout = new StaticLayout(text, mTextPaint, canvas.getWidth() - ScreenShotSetUtil.dpToPx(87), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.3f, true);

        // 도어 아이콘 박기
        Resources r = context.getResources();
        Bitmap icon = BitmapFactory.decodeResource(r, R.drawable.icon);
        Bitmap resizeIcon = BitmapUtil.resizeBitmapImageFn(icon, 100);
        Paint paint = new Paint(); //페인트 객체 선언
        paint.setAlpha(700);
        canvas.drawBitmap(resizeIcon, (canvas.getWidth() / 2) - (resizeIcon.getWidth() / 2),
                result.getHeight() - ScreenShotSetUtil.dpToPx(85) - resizeIcon.getHeight(), paint);

        canvas.save();

        // "본인 의지와 무관한 아웃팅은~" 부분 위치 조정
        float textX = (canvas.getWidth() / 2) - (mTextLayout.getWidth() / 2); //센터
        float textY = result.getHeight() - ScreenShotSetUtil.dpToPx(78); //높이

        canvas.translate(textX, textY);
        mTextLayout.draw(canvas);


        canvas.restore();
        return result;
    }

    public static void saveImage(Context context, Bitmap bitmap, String title) throws Exception {
        OutputStream fOut = null;
        title = title.replaceAll(" ", "+");
        int index = title.lastIndexOf(".png");
        if (index == -1) {
            index = title.lastIndexOf(".jpg");
        }
        String fileName = title.substring(0, index) + ScreenShotContentObserver.FILE_POSTFIX + ".png";
        final String appDirectoryName = "Screenshots";
        final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);
        imageRoot.mkdirs();
        final File file = new File(imageRoot, fileName);
        fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "XXXXX");
        values.put(MediaStore.Images.Media.DESCRIPTION, "description here");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.hashCode());
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName());
        values.put("_data", file.getAbsolutePath());
        ContentResolver cr = context.getContentResolver();
        Uri newUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }
}
