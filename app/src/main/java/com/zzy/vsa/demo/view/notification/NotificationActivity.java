package com.zzy.vsa.demo.view.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.view.MainActivity;

public class NotificationActivity extends AppCompatActivity {

    Button send1,send2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        send1 = (Button) findViewById(R.id.sendnotification1);
        send1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationAtOnce(NotificationActivity.this,"VSADemo","通知测试1","正文",1);
            }
        });

        send2 = (Button) findViewById(R.id.sendnotification2);
        send2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationAtOnce(NotificationActivity.this,"VSANotification","通知测试2","正文",2);
            }
        });

    }


    public void sendNotificationAtOnce(Context context, String channelId, String title, String text, int id){

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }

        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
//        BitmapDrawable bd = (BitmapDrawable) d;
//        Bitmap bm = bd.getBitmap();
        Bitmap bm = drawableToBitmap(d);

        Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationActivity.this, 0 ,intent,0);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = manager.getNotificationChannel(channelId);
            if(channel.getImportance() == NotificationManager.IMPORTANCE_NONE){
                Intent set = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                set.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                set.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                startActivity(set);
            }
        }

        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle(title).setContentText(text).setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentIntent(pendingIntent)
                .setLargeIcon(bm).setSmallIcon(R.mipmap.ic_launcher_round).build();
        manager.notify(id,notification);


    }

    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return
     */
    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
