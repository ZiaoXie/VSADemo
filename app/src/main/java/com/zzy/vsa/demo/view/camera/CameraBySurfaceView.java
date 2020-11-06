package com.zzy.vsa.demo.view.camera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.camera.CameraPreview;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.appenv.AppEnv;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CameraBySurfaceView extends AppCompatActivity {

    CameraPreview mPreview;
    boolean imageflag = false,videoflag = false;
    Button image,video;
    File videopath;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_by_surface_view);


        // 使用
        ImageCompleteReceiver receiver = new ImageCompleteReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppEnv.PACKAGE_NAME+".image_done");
        registerReceiver(receiver, filter);

        mPreview = (CameraPreview) findViewById(R.id.camera_preview);


        image =(Button)findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mPreview.isImageUseful()){
                    Toast.makeText(CameraBySurfaceView.this, "获取相机失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPreview.takePicture(CameraBySurfaceView.this);
                alertDialog = new AlertDialog.Builder(CameraBySurfaceView.this).setTitle("存储图片中").show();

            }
        });

        video = (Button)findViewById(R.id.videobutton);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!videoflag){
                    videopath = FileUtil.initVideoStorage();
                    videoflag = true;
                    video.setText("停止");
                    mPreview.startRecord(videopath.getPath());
                } else {
                    mPreview.stopRecord();
                    Intent intent = new Intent(CameraBySurfaceView.this, ShowVideoActivity.class);
                    intent.putExtra("flag", AppEnv.CAMERA);
                    intent.putExtra("path", videopath.toString());
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class ImageCompleteReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent i) {
            alertDialog.dismiss();

            File photoPath = mPreview.getPhotoFile();
            Intent intent = new Intent(CameraBySurfaceView.this, ShowPhotoActivity.class);
            intent.putExtra("flag", AppEnv.CAMERA);
            intent.putExtra("path", photoPath.toString());
            CameraBySurfaceView.this.startActivity(intent);
            CameraBySurfaceView.this.finish();
        }
    }


}
