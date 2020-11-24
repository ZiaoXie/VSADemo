package com.zzy.vsa.demo.view.watermark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.camera.CameraPreview;

public class SurfaceViewWaterMarkActivity extends AppCompatActivity {


    CameraPreview surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view_water_mark);

        surfaceView = (CameraPreview) findViewById(R.id.surface);
    }
}
