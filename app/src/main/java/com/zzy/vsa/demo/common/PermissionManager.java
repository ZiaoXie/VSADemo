package com.zzy.vsa.demo.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zzy.vsa.demo.appenv.AppEnv;

public class PermissionManager {

    public static void requestPermission(Activity activity, String permissions[], int requestCode){
        boolean flag = true;
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        if(!flag){
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    public static void requestPermissionsResult(Activity activity,int requestCode, int[] grantResults){
        switch (requestCode) {
            case AppEnv.RequestPermissions: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    requestPermissionsDenied(activity);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static void requestPermissionsDenied(Activity activity){
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("警告！")
                .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                    }
                }).show();
    }
}
