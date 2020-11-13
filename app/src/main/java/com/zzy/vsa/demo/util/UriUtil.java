package com.zzy.vsa.demo.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;

public class UriUtil {

    private static String TAG = "UriUtil";


    public static String getFilePathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            Log.i(TAG, "DocumentProvider");
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.i(TAG, "MediaStore");
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.i(TAG, "File");
            return uri.getPath();
        }
        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Files.FileColumns.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static  boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static  boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }




    public static Uri getFileContentUri(Context context, File file) {
        if (!file.exists()){
            Toast.makeText(context, "目标文件不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(file);
        }

        String volumeName = "external";
        String filePath = file.getAbsolutePath();
        String[] projection = new String[]{MediaStore.Files.FileColumns._ID};
        Uri uri = null;

        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri(volumeName), projection,
                MediaStore.Files.FileColumns.DATA + "=? ", new String[] { filePath }, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));

                Uri baseUri = MediaStore.Files.getContentUri(volumeName);
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }
            cursor.close();
        }


        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Files.getContentUri(volumeName), values);
        }

        return uri;


    }

    public static Uri getApkContentUri(Context context, File file) {
        if (!file.exists()){
            Toast.makeText(context, "目标文件不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(file);
        }

        String volumeName = "external";
        String filePath = file.getAbsolutePath();
        String[] projection = new String[]{MediaStore.Files.FileColumns._ID};
        Uri uri = null;

        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri(volumeName), projection,
                "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.apk' )", new String[] { filePath }, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));

                Uri baseUri = MediaStore.Files.getContentUri(volumeName);
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }
            cursor.close();
        }


        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Files.getContentUri(volumeName), values);
        }

        return uri;


    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        if (!imageFile.exists()){
            Toast.makeText(context, "目标文件不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(imageFile);
        }

        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }

    public static Uri getVideoContentUri(Context context, File videoFile) {
        if (!videoFile.exists()){
            Toast.makeText(context, "目标文件不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(videoFile);
        }

        Uri uri = null;
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID }, MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/video/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }


    public static Uri getAudioContentUri(Context context, File audioFile) {
        if (!audioFile.exists()){
            Toast.makeText(context, "目标文件不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(audioFile);
        }

        Uri uri = null;
        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID }, MediaStore.Audio.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/audio/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }
        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }

//    private static Uri forceGetFileUri(File shareFile) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            try {
//                @SuppressLint("PrivateApi")
//                Method rMethod = StrictMode.class.getDeclaredMethod("disableDeathOnFileUriExposure");
//                rMethod.invoke(null);
//            } catch (Exception e) {
//                Log.e(TAG, Log.getStackTraceString(e));
//            }
//        }
//
//        return Uri.parse("file://" + shareFile.getAbsolutePath());
//    }


}
