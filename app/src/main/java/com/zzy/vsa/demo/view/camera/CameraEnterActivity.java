package com.zzy.vsa.demo.view.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.UriUtil;

import java.io.File;

public class CameraEnterActivity extends AppCompatActivity {

    RelativeLayout system, system_video, camera1, camera2, camera3;
    TextView systemText,system_videoText, camera1Text, camera2Text, camera3Text;

    File cameraSavePath;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_enter);

        system = (RelativeLayout) findViewById(R.id.system_image);
        systemText = (TextView) system.findViewById(R.id.text);
        systemText.setText("调用系统拍照");
        system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraEnterActivity.this, SystemCameraActivity.class));
            }
        });

        system_video = (RelativeLayout) findViewById(R.id.system_video);
        system_videoText = (TextView) system_video.findViewById(R.id.text);
        system_videoText.setText("调用系统录像");
        system_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraSavePath = FileUtil.initVideoStorage();
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                uri = FileUtil.getUriByPath(CameraEnterActivity.this, cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, AppEnv.TAKE_VIDEO);
            }
        });


        camera1 = (RelativeLayout) findViewById(R.id.camera1);
        camera1Text = (TextView) camera1.findViewById(R.id.text);
        camera1Text.setText("自定义相机1(Android 5.0以上不推荐使用)");
        camera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraEnterActivity.this, CameraBySurfaceView.class));
            }
        });

        camera2 = (RelativeLayout) findViewById(R.id.camera2);
        camera2Text = (TextView) camera2.findViewById(R.id.text);
        camera2Text.setText("自定义相机2(Android 5.0以上可用)");
        camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    startActivity(new Intent(CameraEnterActivity.this, Camera2Activity.class));
                } else {
                    Toast.makeText(CameraEnterActivity.this, "该功能只能在Android 5.0以上使用", Toast.LENGTH_SHORT).show();
                }
            }
        });


        camera3 = (RelativeLayout) findViewById(R.id.camera3);
        camera3Text = (TextView) camera3.findViewById(R.id.text);
        camera3Text.setText("自定义相机3");
        camera3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CameraEnterActivity.this, EncodeYUVToH264Activity2.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("test",uri.toString() + UriUtil.getFileContentUri(CameraEnterActivity.this, cameraSavePath));
        switch (requestCode){
            case AppEnv.TAKE_VIDEO:
                if (resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(CameraEnterActivity.this, ShowVideoActivity.class);
                    intent.putExtra("flag", AppEnv.SYSTEM_CAMERA);
                    intent.putExtra("path", cameraSavePath.getAbsolutePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "获取视频失败", Toast.LENGTH_SHORT).show();
                }


        }

    }
}
