package com.zzy.vsa.demo.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.zzy.vsa.demo.appenv.FileType;
import com.zzy.vsa.demo.appenv.AppEnv;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ${zhaoyanjun} on 2017/1/11.
 */

public class FileUtil {

    final static String TAG = "FileUtil";

    public static String getAppRootPath(){
        return Environment.getExternalStorageDirectory().getPath() + File.separator + AppEnv.PACKAGE_NAME;
    }


    /**
     * 初始化图片存储位置
     */
    public static File initPhotoStorage() {
        return new File(createName(".jpg"));
    }

    public static File initVideoStorage() {
        return new File(createName(".mp4"));
//        return new File(Environment.getExternalStorageDirectory().getPath() + File.separator + AppEnv.PACKAGE_NAME + File.separator + System.currentTimeMillis() + ".mp4");
    }

    public static File initAudioStorage() {
        return new File(createName(".amr"));
    }

    public static File initPCMAudioStorage() {
        return new File(createName(".pcm"));
    }

    public static String initWebDownloaderStorage(String filename){

        return Environment.getExternalStorageDirectory().getPath() + File.separator + AppEnv.PACKAGE_NAME + File.separator + System.currentTimeMillis() + filename ;
    }

    private static String createName(String suffix){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        return Environment.getExternalStorageDirectory().getPath() + File.separator + AppEnv.PACKAGE_NAME + File.separator + sdf.format(new Date()) + suffix;
    }


    /*
     * 根据文件路径获取Uri
     * */
    public static Uri getUriByPath(Context context, File cameraSavePath) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //第二个参数为 包名.fileprovider
            uri = FileProvider.getUriForFile(context, AppEnv.FileProvider, cameraSavePath);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        Log.i(TAG,uri.toString());
        return uri;
    }


    public static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    public static String getSuffix(String name) {

        if (name.equals("") || name.endsWith(".")) {
            return null;
        }
        int index = name.lastIndexOf(".");
        if (index != -1) {
            return name.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    public static String getMimeType(File file){
        String suffix = getSuffix(file);
        if (TextUtils.isEmpty(suffix)) {
            return "*/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (!TextUtils.isEmpty(type)) {
            return type;
        }
        return "*/*";
    }


    public static FileType getFileType(String path) {
        return getFileType(new File(path));
    }

    /**
     * 获取文件类型
     *
     * @param file
     * @return
     */
    public static FileType getFileType(File file) {
        if (file.isDirectory()) {
            return FileType.directory;
        }

        String mimeType = getMimeType(file);
        String prefix = mimeType.split("/")[0];
        if(TextUtils.isEmpty(prefix)){
            return FileType.other;
        }

        if(prefix.equals("audio")){
            return FileType.music;
        }

        if (prefix.equals("video")) {
            return FileType.video;
        }

        if (prefix.equals("image")) {
            return FileType.image;
        }

        if (prefix.equals("text")) {
            return FileType.txt;
        }

        if (mimeType.equals("application/zip") || mimeType.equals("application/rar") ) {
            return FileType.zip;
        }

        if (mimeType.equals("application/vnd.android.package-archive")) {
            return FileType.apk;
        }

        return FileType.other;
    }

    /**
     * 文件按照名字排序
     */
    public static Comparator comparator = new Comparator<File>() {
        @Override
        public int compare(File file1, File file2) {
            if (file1.isDirectory() && file2.isFile()) {
                return -1;
            } else if (file1.isFile() && file2.isDirectory()) {
                return 1;
            } else {
                return file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase());
            }
        }
    };

    /**
     * 获取文件的子文件个数
     *
     * @param file
     * @return
     */
    public static int getFileChildCount(File file) {
        int count = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isHidden()) continue;
                count++;
            }
        }
        return count;
    }

    /**
     * 文件大小转换
     *
     * @param size
     * @return
     */
    public static String sizeToChange(long size) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");  //字符格式化，为保留小数做准备

        double G = size * 1.0 / 1024 / 1024 / 1024;
        if (G >= 1) {
            return df.format(G) + " GB";
        }

        double M = size * 1.0 / 1024 / 1024;
        if (M >= 1) {
            return df.format(M) + " MB";
        }

        double K = size * 1.0 / 1024;
        if (K >= 1) {
            return df.format(K) + " KB";
        }

        return size + " B";
    }

}
