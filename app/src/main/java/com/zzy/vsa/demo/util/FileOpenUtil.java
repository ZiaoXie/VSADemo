package com.zzy.vsa.demo.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

public class FileOpenUtil {

    public static void openIntent(Context context, File file){

        if(!file.exists()){
            Toast.makeText(context, "该文件不存在", Toast.LENGTH_LONG).show();
            return;
        }

        switch (FileUtil.getFileType(file)){
            case apk:
                openAppIntent( context , new File( file.getPath() ) );
                break;
            case image:
                openImageIntent( context , new File( file.getPath() ));
                break;
            case txt:
                openTextIntent( context , new File( file.getPath() ) );
                break;
            case music:
                openMusicIntent( context ,  new File( file.getPath() ) );
                break;
            case video:
                openVideoIntent( context ,  new File( file.getPath() ) );
                break;
            default:
                openApplicationIntent( context , new File( file.getPath() ) );
                break;
        }


    }

    /**
     * 安装apk
     * @param context
     * @param file
     */
    public static void openAppIntent(Context context , File file ){
        Uri path = FileUtil.getUriByPath(context, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(path, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 打开图片资源
     * @param context
     * @param file
     */
    public static void openImageIntent( Context context , File file ) {
        Uri path = UriUtil.getImageContentUri(context, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(path, "image/*");
        context.startActivity(intent);
    }

    /**
     * 打开文本资源
     * @param context
     * @param file
     */
    public static void openTextIntent( Context context , File file ) {
        Uri path = UriUtil.getFileContentUri(context, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(path, "text/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * 打开音频资源
     * @param context
     * @param file
     */
    public static void openMusicIntent( Context context , File file ){
        Uri path = UriUtil.getAudioContentUri(context, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(path, "audio/*");
        context.startActivity(intent);
    }

    /**
     * 打开视频资源
     * @param context
     * @param file
     */
    public static void openVideoIntent( Context context , File file ){
        Uri path = UriUtil.getVideoContentUri(context,file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(path, "video/*");
        context.startActivity(intent);
    }

    /**
     * 打开所有能打开应用资源
     * @param context
     * @param file
     */
    public static void openApplicationIntent( Context context , File file ){
        Uri path = FileUtil.getUriByPath(context, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(path, FileUtil.getMimeType(file));
        context.startActivity(intent);
    }


}
