package com.teamcore.android.core.Util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.storage.UploadTask;
import com.teamcore.android.core.Exception.GifException;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
            quality = (int) ((RemoteConfig.LIMIT_MB*1024*1204)*100/getFileSizeInBytes());

        }

        Log.d("kbj","quality : " +quality);

        return quality;
    }

    // 원본
    private byte[] getResizeImageByteArray(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getResizeBitmap(bitmap).compress(Bitmap.CompressFormat.JPEG, getQuality(), stream);
        byte[] rst = stream.toByteArray();
        Log.d("kbj","ori length : " +rst.length);

        return stream.toByteArray();
    }

    // 썸네일
    private byte[] getThumbNailImageByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getResizeBitmap(bitmap).compress(Bitmap.CompressFormat.JPEG, (getQuality()*THUMB_NAIL_RATIO)/100, stream);
        byte[] rst = stream.toByteArray();
        Log.d("kbj","thum length : " +rst.length);
        return stream.toByteArray();
    }

    // 용량 제한
    private Bitmap getResizeBitmap(Bitmap bitmap) {
        return bitmap;
    }

    public Bitmap getBitmap() {
        return getResizeBitmap(bitmap);
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
        boolean isImageFromGoogleDrive = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                rawUserId = "";
                            } else {
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
                            }
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
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                    isImageFromGoogleDrive = true;
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

            if (isImageFromGoogleDrive) {
                bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                bitmap = BitmapUtil.rotateBitmap(imgPath, BitmapFactory.decodeFile(imgPath));   // TODO : 5메가 넘는 물개사진을 4장 올리면 가끔 OOM 생기는 버그
            } else {

                File f = new File(imgPath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
            }

        } else {
            bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
        }
    }

    public UploadTask upload(StorageReference ref) throws Exception {
        // Check Gif
        return getUploadTask(ref, uri);
    }

    public UploadTask upload(StorageReference ref, Uri uri) throws Exception {
        // Check Gif
        getImgPath(uri);
        return getUploadTask(ref, uri);
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
        long fileSizeInMB;// 크기 확인 : 5MB

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
            if (getFileSizeInMB() >= RemoteConfig.LIMIT_MB) throw new Exception("파일이 5MB를 넘어서 불가능합니다");

            //Uri
            GlideApp.with(editImage.getContext())
                    .load(uri)
                    .placeholder(R.drawable.a)
                    .into(editImage);
        } else {
            //if (getFileSizeInMB() >= 5) throw new Exception("파일이 5MB를 넘어서 불가능합니다");
            // 5메가가 넘는건 해상도 줄임
            Bitmap originalBitmap = this.getBitmap();
            editImage.setImageBitmap(originalBitmap);
        }
    }

    public UploadTask makeThumbNail(StorageReference thumbNailSpaceRef, Uri uri) throws Exception {
        getImgPath(uri);
        if (isGif()) {
            return null;
        } else {
            return thumbNailSpaceRef.putBytes(this.getThumbNailImageByteArray(bitmap));
        }

    }
}
