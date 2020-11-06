package com.zzy.vsa.demo.appcase.share;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.util.UriUtil;

public class ShareUtil {

    public static List<ResolveInfo> getAppInfoWithFilter(Context context, Intent intent){
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent,0);
        if(resInfo.isEmpty()){
            Toast.makeText(context, "没有可以分享的应用",Toast.LENGTH_SHORT).show();
            return null;
        }
        Collections.sort(resInfo, new ResolveInfo.DisplayNameComparator(pm));
        return resInfo;
    }

     public static Intent shareFileWithChooser(Context context, Intent intent){
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent,0);
        if(resInfo.isEmpty()){
            Toast.makeText(context, "没有可以分享的应用",Toast.LENGTH_SHORT).show();
            return null;
        }

         Collections.sort(resInfo, new ResolveInfo.DisplayNameComparator(pm));

        List<Intent> targetIntents = new ArrayList<>();
        for (ResolveInfo resolveInfo : resInfo) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            Intent target = new Intent(intent);
            target.setAction(Intent.ACTION_SEND);
            target.setComponent(new ComponentName(activityInfo.packageName,activityInfo.name));

            targetIntents.add(new LabeledIntent(target,activityInfo.packageName,resolveInfo.loadLabel(pm),resolveInfo.icon));

        }
        if (targetIntents.size()<= 0){
            Toast.makeText(context, "没有经过筛选的应用",Toast.LENGTH_SHORT).show();
            return null;
        }

        Intent chooser = Intent.createChooser(targetIntents.remove(targetIntents.size() - 1), "选择分享");
        if (chooser == null) return null;
        LabeledIntent[] labeledIntents = targetIntents.toArray(new LabeledIntent[targetIntents.size()]);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,labeledIntents);
        return chooser;
    }


    /**
     * 分享文字
     */
    public static Intent shareText(String content){
        Intent intent =new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        return intent;
    }

    public static List<ResolveInfo> shareTextWithFilter(Context context,String content){
        List<ResolveInfo> resInfo;
        Intent intent =new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);

        resInfo = getAppInfoWithFilter(context,intent);
        return resInfo;
    }


    public static Intent initFileShareIntent(Context context, String path){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File file = new File(path);

        switch (FileUtil.getFileType(path)){
            case image:
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, UriUtil.getImageContentUri(context, file));
                break;
            case txt:
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_STREAM, UriUtil.getFileContentUri(context, file));
                break;
            case music:
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_STREAM, UriUtil.getAudioContentUri(context, file));
                break;
            case video:
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, UriUtil.getVideoContentUri(context, file));
                break;
            default:
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, UriUtil.getFileContentUri(context, file));
                break;
        }
        return intent;
    }

    public static Intent shareFile(Context context, String path){

        Intent intent = initFileShareIntent(context, path);

//        intent = Intent.createChooser(intent, "发送");
        return intent;
    }

    public static List<ResolveInfo> shareFileWithFilter(Context context, String path){

        List<ResolveInfo> resInfo;

        Intent intent = initFileShareIntent(context, path);
        Log.i("test",intent.getParcelableExtra(Intent.EXTRA_STREAM).toString());

        resInfo = getAppInfoWithFilter(context,intent);
        return resInfo;
    }

    /**
     * 分享图片
     */
//    public void shareImg(String packageName,String className,File file){
//        if(file.exists()){
//            Uri uri = FileUtil.getUriByPath(context,file);
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_SEND);
//            intent.setType("image/*");
//            if(!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className)){
//                intent.setComponent(new ComponentName(packageName, className));
//            }else if (!TextUtils.isEmpty(packageName)) {
//                intent.setPackage(packageName);
//            }
//            intent.putExtra(Intent.EXTRA_STREAM, uri);
//            Intent chooserIntent = Intent.createChooser(intent, "分享到:");
//            context.startActivity(chooserIntent);
//        }else {
//            Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
//        }
//    }

}

