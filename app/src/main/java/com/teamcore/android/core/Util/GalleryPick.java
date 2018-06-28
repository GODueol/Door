package com.teamcore.android.core.Util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.teamcore.android.core.Exception.GifException;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by gimbyeongjin on 2018. 1. 18..
 */
public class GalleryPick {
    private BaseActivity activity;
    private Uri uri;
    private Bitmap bitmap;
    public static final int REQUEST_GALLERY = 2;
    private static final int THUMB_NAIL_RATIO = 35;

    private String imgPath;

    private String getMimeType(Uri uriImage) {
        String strMimeType = null;

        @SuppressLint("Recycle") Cursor cursor = activity.getContentResolver().query(uriImage,
                new String[]{MediaStore.MediaColumns.MIME_TYPE},
                null, null, null);

        if (cursor != null && cursor.moveToNext()) {
            strMimeType = cursor.getString(0);
        }

        return strMimeType;
    }

    public GalleryPick(BaseActivity activity) {
        this.activity = activity;
    }

    public Uri getUri() {
        return uri;
    }

    // 용량 제한
    private int getQuality() {
        int quality = 100;
        long mb = getFileSizeInMB();
        if(mb >= RemoteConfig.LIMIT_MB){
            // 퀄리티 계산
            quality = (int) ((RemoteConfig.LIMIT_MB*1024*1024)*100/getFileSizeInBytes());

        }

        Log.d("kbj","quality : " +quality);

//        return quality;
        return 100;
    }

    // 원본
    private byte[] getResizeImageByteArray(Bitmap bitmap) {


        Log.d("kbj","getFileSizeInBytes() : " +getFileSizeInBytes());
        Log.d("kbj","bitmap.getByteCount() : " +bitmap.getByteCount());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, getQuality(), stream);
        bitmap.recycle();
        byte[] rst = stream.toByteArray();
        Log.d("kbj","ori length : " +rst.length);

        return stream.toByteArray();
    }

    // 썸네일
    private byte[] getThumbNailImageByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, (getQuality()*THUMB_NAIL_RATIO)/100, stream);
        byte[] rst = stream.toByteArray();
        Log.d("kbj","thum length : " +rst.length);
        return stream.toByteArray();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public GalleryPick goToGallery() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
        }
        return this;
    }

    public void invoke(Intent data) throws FileNotFoundException {
        uri = data.getData();
        getImgPath(uri);
    }

    private void getImgPath(Uri uri) throws FileNotFoundException {
        this.uri = uri;
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    imgPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    Pattern DIR_SEPORATOR = Pattern.compile("/");
                    Set<String> rv = new HashSet<>();
                    String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                    String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                    String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                    if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                        if (TextUtils.isEmpty(rawExternalStorage)) {
                            rv.add("/storage/sdcard0");
                        } else {
                            rv.add(rawExternalStorage);
                        }
                    } else {
                        String rawUserId;
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String[] folders = DIR_SEPORATOR.split(path);
                        String lastFolder = folders[folders.length - 1];
                        boolean isDigit = false;
                        try {
                            Integer.valueOf(lastFolder);
                            isDigit = true;
                        } catch (NumberFormatException ignored) {
                        }
                        rawUserId = isDigit ? lastFolder : "";
                        if (TextUtils.isEmpty(rawUserId)) {
                            rv.add(rawEmulatedStorageTarget);
                        } else {
                            rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                        }
                    }
                    if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                        String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                        Collections.addAll(rv, rawSecondaryStorages);
                    }
                    String[] temp = rv.toArray(new String[rv.size()]);
                    for (String aTemp : temp) {
                        File tempf = new File(aTemp + "/" + split[1]);
                        if (tempf.exists()) {
                            imgPath = aTemp + "/" + split[1];
                        }
                    }
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};
                try {
                    cursor = activity.getContentResolver().query(contentUri, projection, null, null,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        imgPath = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};

                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};

                try {
                    cursor = activity.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        imgPath = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = null;
            String column = "_data";
            String[] projection = {column};

            try {
                cursor = activity.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(column);
                    imgPath = cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imgPath = uri.getPath();
        }

        if(isGif()) return;

        if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
            bitmap = rotateBitmap(imgPath, decodeUri(uri));
        } else {
            bitmap = decodeUri(uri);
        }

    }

    public StorageTask<UploadTask.TaskSnapshot> upload(StorageReference ref) throws Exception {
        // Check Gif
        return upload(ref, uri);
    }

    public StorageTask<UploadTask.TaskSnapshot> upload(StorageReference ref, Uri uri) throws Exception {
        // Check Gif
//        getImgPath(uri);
        return getUploadTask(ref, uri).addOnSuccessListener(taskSnapshot -> recycle());
    }

    @NonNull
    private UploadTask getUploadTask(StorageReference ref, Uri uri) throws Exception {
        if (isGif()) {
            if(!DataContainer.getInstance().isPlus) throw new GifException(activity.getString(R.string.possibleCorePlusGIF));
            if (getFileSizeInMB() >= RemoteConfig.LIMIT_MB) throw new GifException(activity.getString(R.string.cannotOver5Mb));
            return ref.putFile(uri);
        } else {
            return ref.putBytes(this.getResizeImageByteArray(bitmap));
        }
    }

    private long getFileSizeInBytes(){
        @SuppressLint("Recycle") Cursor returnCursor =
                activity.getContentResolver().query(uri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        return returnCursor.getLong(sizeIndex);
    }
    private long getFileSizeInMB() {
        long fileSizeInMB; // 크기 확인 : 5MB

        long fileSizeInBytes = getFileSizeInBytes();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)

        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        fileSizeInMB = fileSizeInKB / 1024;

        return fileSizeInMB;
    }

    private boolean isGif() {
        String rst = getMimeType(uri);
        return rst.contains("gif");
    }

    public void setImage(ImageView editImage) throws Exception {

        // Gif 파일인 경우
        if (isGif()) {
            if(!DataContainer.getInstance().isPlus) throw new Exception(activity.getString(R.string.possibleCorePlusGIF));
            if (getFileSizeInMB() >= RemoteConfig.LIMIT_MB) throw new Exception("파일이 "+ RemoteConfig.LIMIT_MB +"MB를 넘어서 불가능합니다");

            //Uri
            GlideApp.with(editImage.getContext())
                    .load(uri)
                    .placeholder(R.drawable.pic_load_ani2)
                    .into(editImage);
        } else {
            //if (getFileSizeInMB() >= 5) throw new Exception("파일이 5MB를 넘어서 불가능합니다");
            // 5메가가 넘는건 해상도 줄임
            Bitmap originalBitmap = this.getBitmap();
            editImage.setImageBitmap(originalBitmap);
        }
    }

    public UploadTask makeThumbNail(StorageReference thumbNailSpaceRef) {
//        getImgPath(uri);
        if (isGif()) {
            return null;
        } else {
            return thumbNailSpaceRef.putBytes(this.getThumbNailImageByteArray(bitmap));
        }

    }

    private Bitmap rotateBitmap(String src, Bitmap bitmap) {
//        bitmap = getResizeBitmap(bitmap);
        int orientation = getExifOrientation(src);

        if (orientation == 1) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        switch (orientation) {
            case 2:
                matrix.setScale(-1, 1);
                break;
            case 3:
                matrix.setRotate(180);
                break;
            case 4:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case 5:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case 6:
                matrix.setRotate(90);
                break;
            case 7:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case 8:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }

        try {
            Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return oriented;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private static int getExifOrientation(String src) {
        int orientation = 1;

        try {
            Class<?> exifClass = Class.forName("android.media.ExifInterface");
            Constructor<?> exifConstructor = exifClass.getConstructor(String.class);
            Object exifInstance = exifConstructor.newInstance(src);
            Method getAttributeInt = exifClass.getMethod("getAttributeInt", String.class, int.class);
            Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
            String tagOrientation = (String) tagOrientationField.get(null);
            orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[]{tagOrientation, 1});
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return orientation;
    }

    private void recycle() {
        if(bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to (HD)
        final int REQUIRED_SIZE = 1920/2;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Log.d("KBJ", "o2.inSampleSize : " + o2.inSampleSize);
        return BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(selectedImage), null, o2);

    }
}
