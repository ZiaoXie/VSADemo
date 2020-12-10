package com.zzy.vsa.demo.view.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;

public class EncodeYUVToH264Activity2 extends AppCompatActivity {
    private static final String TAG = "EncodeYUVToH262Activity";
    private static final int REQUEST_CAMERA = 1;
    private Camera2Preview camera2Preview;
    private Button mRecordBtn;
    String outputfile = "";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_yuvto_h264);
        mRecordBtn = (Button)findViewById(R.id.record_btn);

        requestPermissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA);
        } else {
            initCameraView();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCameraView();
                } else {
                    Toast.makeText(this, "权限请求失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initCameraView() {
        camera2Preview = new Camera2Preview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(camera2Preview);

        outputfile = camera2Preview.outputfile.getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        if (camera2Preview != null) {
            camera2Preview.onResume();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        super.onPause();
        if (camera2Preview != null) {
            camera2Preview.onPause();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toggleVideo(View view) {

        if (camera2Preview.toggleVideo()) {
            mRecordBtn.setText("停止录制视频");
        } else {
            mRecordBtn.setText("开始录制视频");
            Intent intent = new Intent(EncodeYUVToH264Activity2.this, ShowVideoActivity.class);
            intent.putExtra("flag", AppEnv.CAMERA2);
            intent.putExtra("path", outputfile);
            startActivity(intent);
            finish();
        }
    }

}
