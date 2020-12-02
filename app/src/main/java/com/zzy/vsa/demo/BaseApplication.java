package com.zzy.vsa.demo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.widget.Toast;

import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.FileUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BaseApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<NotificationChannel> channels = new ArrayList<NotificationChannel>();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel;

            String channelId = "VSADemo";
            String channelName = "测试通知频道";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channel = new NotificationChannel(channelId, channelName, importance);
            channels.add(channel);

            channelId = "VSANotification";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(channelId, channelName, importance);
            channels.add(channel);

            notificationManager.createNotificationChannels(channels);

            File download = new File(FileUtil.getAppRootPath() + File.separator + "download");
            if(download.exists()){
                for( File f : download.listFiles()){
                    if(f.getAbsolutePath().endsWith(".temp")){
                        f.delete();
                    }
                }
            }

        }
    }

//    public static final String CLASS_SMS_MANAGER = "com.android.internal.telephony.SmsApplication";

//    public static final String CLASS_SMS_MANAGER = AppEnv.PACKAGE_NAME;
//
//    public static final String METHOD_SET_DEFAULT = "setDefaultApplication";
//
//    private void setDefaultSms(Boolean isMyApp) {
//        try {
//            Class<?> smsClass = Class.forName(CLASS_SMS_MANAGER);
//            Method method = smsClass.getMethod(METHOD_SET_DEFAULT, String.class, Context.class);
//            method.invoke(null, "要设置的包名", this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
