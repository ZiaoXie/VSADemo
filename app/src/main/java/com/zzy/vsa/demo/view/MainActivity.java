package com.zzy.vsa.demo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.view.call.CallActivity;
import com.zzy.vsa.demo.view.clip.ClipActivity;
import com.zzy.vsa.demo.view.filemanager.FileEnterActivity;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.common.PermissionManager;
import com.zzy.vsa.demo.view.audio.AudioEnterActivity;
import com.zzy.vsa.demo.view.camera.CameraEnterActivity;
import com.zzy.vsa.demo.view.message.ShowMessageActivity;
import com.zzy.vsa.demo.view.notification.NotificationActivity;
import com.zzy.vsa.demo.view.remotecontrol.RemoteControlActivity;
import com.zzy.vsa.demo.view.share.ShareActivity;
import com.zzy.vsa.demo.view.webview.ShowHtmlActivity;


import java.io.File;

public class MainActivity extends AppCompatActivity {

    final String permissions[] = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
    };
    String TAG = "MainActivity";


    RelativeLayout camera,audio,filemanager,clip,webview,remotecontrol,share,notification,call,message;
    TextView cameraText,audioText,filemanagerText,clipText,webviewText,remotecontrolText,shareText,notificationText,callText,messageText;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = (RelativeLayout) findViewById(R.id.camera);
        cameraText = (TextView) camera.findViewById(R.id.text);
        cameraText.setText("1.测试调用摄像头");
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CameraEnterActivity.class));
            }
        });


        audio = (RelativeLayout) findViewById(R.id.audiocall);
        audioText = (TextView) audio.findViewById(R.id.text);
        audioText.setText("2.测试使用录音");
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AudioEnterActivity.class));
            }
        });


        filemanager =(RelativeLayout) findViewById(R.id.filemanager);
        filemanagerText = (TextView) filemanager.findViewById(R.id.text);
        filemanagerText.setText("3.打开文件管理器");
        filemanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FileEnterActivity.class));
            }
        });

        clip = (RelativeLayout) findViewById(R.id.clip);
        clipText = (TextView) clip.findViewById(R.id.text);
        clipText.setText("4.测试剪贴板");
        clip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ClipActivity.class));
            }
        });

        webview = (RelativeLayout) findViewById(R.id.webviewdownload);
        webviewText = (TextView) webview.findViewById(R.id.text);
        webviewText.setText("5.测试网页浏览器WebView");
        webview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowHtmlActivity.class));
            }
        });

        remotecontrol = (RelativeLayout) findViewById(R.id.remotecontrol);
        remotecontrolText = (TextView) remotecontrol.findViewById(R.id.text);
        remotecontrolText.setText("6.测试远程单品");
        remotecontrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RemoteControlActivity.class));
            }
        });
        remotecontrol.setVisibility(View.GONE);

        share = (RelativeLayout) findViewById(R.id.share);
        shareText = (TextView) share.findViewById(R.id.text);
        shareText.setText("7.测试分享功能");
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShareActivity.class));
            }
        });

        notification = (RelativeLayout) findViewById(R.id.notification);
        notificationText = (TextView) notification.findViewById(R.id.text);
        notificationText.setText("8.发送通知");
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });

        call = (RelativeLayout) findViewById(R.id.call);
        callText = (TextView) call.findViewById(R.id.text);
        callText.setText("9.测试打电话");
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CallActivity.class));
            }
        });

        message = (RelativeLayout) findViewById(R.id.message);
        messageText = (TextView) message.findViewById(R.id.text);
        messageText.setText("10.管理短信");
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowMessageActivity.class));
            }
        });


        PermissionManager.requestPermission(this, permissions, AppEnv.RequestPermissions);

        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/"+ AppEnv.PACKAGE_NAME);
        if(!dir.exists()){
            dir.mkdirs();
        }
        getPackageName();

        Button button = (Button) findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        button.setVisibility(View.GONE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        PermissionManager.requestPermissionsResult(this, requestCode, grantResults);
    }




}


