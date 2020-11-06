package com.zzy.vsa.demo.view.camera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.camera.AutoFitTextureView;
import com.zzy.vsa.demo.appcase.camera.Camera2Tool;
import com.zzy.vsa.demo.appenv.AppEnv;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends AppCompatActivity {

    private static final String TAG = "Camera2Activity";

    AutoFitTextureView textureView;

    Camera2Tool camera2Tool;

    boolean videoflag = false;
    Button video;
    String videopath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        textureView = (AutoFitTextureView) findViewById(R.id.textureView);
        camera2Tool = new Camera2Tool(Camera2Activity.this, textureView);


        textureView.setSurfaceTextureListener(camera2Tool.textureListener);

        findViewById(R.id.imagebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera2Tool.isUseful()){
                    camera2Tool.capture();
                }
            }
        });

        video =(Button)findViewById(R.id.videobutton);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!camera2Tool.isUseful()){
                    Toast.makeText(Camera2Activity.this, "获取相机失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!videoflag){
                    videoflag = true;
                    video.setText("停止");
                    videopath = camera2Tool.startRecordingVideo();
                } else {
                    camera2Tool.stopRecordingVideo();
                    Intent intent = new Intent(Camera2Activity.this, ShowVideoActivity.class);
                    intent.putExtra("flag", AppEnv.CAMERA2);
                    intent.putExtra("path", videopath);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        camera2Tool.closeCamera();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();



    }



}
