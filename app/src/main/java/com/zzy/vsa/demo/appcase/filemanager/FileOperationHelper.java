package com.zzy.vsa.demo.appcase.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


import com.zzy.vsa.demo.appenv.FileType;
import com.zzy.vsa.demo.util.BroadcastUtil;
import com.zzy.vsa.demo.util.FileUtil;

public class FileOperationHelper {

    private static FileOperationHelper mInstance;
    private static Context mContext;
    private static ContentResolver mContentResolver;
    private static Object mLock = new Object();

    public static FileOperationHelper getInstance(Context context){
        if (mInstance == null){
            synchronized (mLock){
                if (mInstance == null){
                    mInstance = new FileOperationHelper();
                    mContext = context;
                    mContentResolver = context.getContentResolver();
                }
            }
        }
        return mInstance;
    }


    public boolean copy(File oldPath$Name, File newPath$Name){
        boolean flag = copyFileAndDir(oldPath$Name, newPath$Name);
        BroadcastUtil.scanFileBroadcast(mContext, newPath$Name.getAbsolutePath());
        return flag;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath$Name String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPath$Name String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     * <code>false</code> otherwise
     */
    private boolean copyFileAndDir(File oldPath$Name, File newPath$Name) {
        Log.e("copy",oldPath$Name + "   " + newPath$Name);
        try {

            if (oldPath$Name.isDirectory()) {
                File newFile = new File(newPath$Name+ File.separator + oldPath$Name.getName());
                if (!newFile.exists()) {
                    if (!newFile.mkdirs()) {
                        Log.i("--Method--", "copyFolder: cannot create directory.");
                        return false;
                    }
                }
                File[] files = oldPath$Name.listFiles();
                File temp;
                for (File file : files) {
                    copyFileAndDir(new File(oldPath$Name.getAbsolutePath() + File.separator + file.getName()),
                            new File(newFile.getAbsolutePath() ) );
//                    if(file.isDirectory()){
//
//                        copyFileAndDir(new File(oldPath$Name.getAbsolutePath() + File.separator + file.getName()),
//                                new File(newFile.getAbsolutePath() + File.separator + file.getName()) );
//                    } else {
//
//                    }
                }
            } else {
                if (!oldPath$Name.exists()) {
                    Log.i("--Method--", "copyFile:  oldFile not exist.");
                    return false;
                } else if (!oldPath$Name.isFile()) {
                    Log.i("--Method--", "copyFile:  oldFile not file.");
                    return false;
                } else if (!oldPath$Name.canRead()) {
                    Log.i("--Method--", "copyFile:  oldFile cannot read.");
                    return false;
                }
            /* 如果不需要打log，可以使用下面的语句
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }
            */
                FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
                FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name + File.separator + oldPath$Name.getName());
                byte[] buffer = new byte[1024];
                int byteRead;
                while (-1 != (byteRead = fileInputStream.read(buffer))) {
                    fileOutputStream.write(buffer, 0, byteRead);
                }
                fileInputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean move(File oldPath$Name, File newPath$Name){
        boolean flag = renameFileAndDir(oldPath$Name, newPath$Name);
        BroadcastUtil.scanFileBroadcast(mContext, oldPath$Name.getAbsolutePath());
        BroadcastUtil.scanFileBroadcast(mContext, newPath$Name.getAbsolutePath());
        return flag;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath$Name String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPath$Name String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     * <code>false</code> otherwise
     */
    private boolean renameFileAndDir(File oldPath$Name, File newPath$Name) {
        Log.e("move",oldPath$Name + "   " + newPath$Name);
        try {

            if (oldPath$Name.isDirectory()) {
                File newFile = new File(newPath$Name+ File.separator + oldPath$Name.getName());
                if (!newFile.exists()) {
                    if (!newFile.mkdirs()) {
                        Log.i("--Method--", "copyFolder: cannot create directory.");
                        return false;
                    }
                }
                File[] files = oldPath$Name.listFiles();
                File temp;
                for (File file : files) {
                    renameFileAndDir(new File(oldPath$Name.getAbsolutePath() + File.separator + file.getName()),
                            new File(newFile.getAbsolutePath() ) );
//                    if(file.isDirectory()){
//                        renameFileAndDir(new File(oldPath$Name.getAbsolutePath() + File.separator + file.getName()),
//                                new File(newFile.getAbsolutePath() + File.separator + file.getName()) );
//                    } else {
//
//                    }
                }
                oldPath$Name.delete();
            } else {
                if (!oldPath$Name.exists()) {
                    Log.i("--Method--", "copyFile:  oldFile not exist.");
                    return false;
                } else if (!oldPath$Name.isFile()) {
                    Log.i("--Method--", "copyFile:  oldFile not file.");
                    return false;
                } else if (!oldPath$Name.canRead()) {
                    Log.i("--Method--", "copyFile:  oldFile cannot read.");
                    return false;
                }
            /* 如果不需要打log，可以使用下面的语句
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }
            */
                oldPath$Name.renameTo(new File(newPath$Name.getAbsolutePath() + File.separator + oldPath$Name.getName()));
                Log.i("move", "moveFile:" + oldPath$Name.getAbsolutePath() + " to " + newPath$Name.getAbsolutePath());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void delete(String file){
        FileType fileType = FileUtil.getFileType(file);
        if (fileType == FileType.directory){
            File dir = new File(file);
            String[] fileList = dir.list();
            for(String next: fileList){
                delete(file+File.separator+next);
            }
            dir.delete();
        } else {
            switch (fileType){
                case image:
                    deletePicture(file);
                    break;
                case video:
                    deleteVideo(file);
                    break;
                case music:
                    deleteAudio(file);
                    break;
                case other:
                    deleteOthers(file);
                    break;
                default:
                    deleteOthers(file);
                    break;
            }
            Log.i("删除",file);
        }
    }

    private void deletePicture(String localPath) {
        if(!TextUtils.isEmpty(localPath)){
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String url = MediaStore.Images.Media.DATA + "=?";
            mContentResolver.delete(uri, url, new String[]{localPath});
        }
        File file = new File(localPath);
        if(file.exists()){
            file.delete();
        }
    }


    private void deleteVideo(String localPath) {
        if(!TextUtils.isEmpty(localPath)){
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String url = MediaStore.Video.Media.DATA + "=?";
            mContentResolver.delete(uri, url, new String[]{localPath});
        }
        File file = new File(localPath);
        if(file.exists()){
            file.delete();
        }
    }

    private void deleteAudio(String localPath) {
        if(!TextUtils.isEmpty(localPath)){
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            String url = MediaStore.Audio.Media.DATA + "=?";
            mContentResolver.delete(uri, url, new String[]{localPath});
        }
        File file = new File(localPath);
        if(file.exists()){
            file.delete();
        }
    }

    private void deleteOthers(String localPath) {

        if(!TextUtils.isEmpty(localPath)){
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String url = MediaStore.Files.FileColumns.DATA + "=?";
            mContentResolver.delete(MediaStore.Files.getContentUri("external"), url, new String[]{localPath});
        }
        File file = new File(localPath);
        if(file.exists()){
            file.delete();
        }
    }


}
