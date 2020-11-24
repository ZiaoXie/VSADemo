package com.zzy.vsa.demo.view.watermark;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzy.vsa.demo.R;

public class WaterMarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark);

        RelativeLayout dialogTest = (RelativeLayout) findViewById(R.id.dialogTest);
        TextView dialogTestText = (TextView) dialogTest.findViewById(R.id.text);
        dialogTestText.setText("测试弹窗水印");
        dialogTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(WaterMarkActivity.this).setTitle("测试弹窗").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        RelativeLayout surfaceTest = (RelativeLayout) findViewById(R.id.surfaceTest);
        TextView surfaceTestText = (TextView) surfaceTest.findViewById(R.id.text);
        surfaceTestText.setText("测试SurfaceView水印");
        surfaceTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WaterMarkActivity.this, SurfaceViewWaterMarkActivity.class));
            }
        });

    }
}
