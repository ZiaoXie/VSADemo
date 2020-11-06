package com.zzy.vsa.demo.view.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.BroadcastUtil;
import com.zzy.vsa.demo.view.filemanager.ShowFileActivity;

public class ShowPhotoActivity extends AppCompatActivity {

    ImageView image;
    int flag;
    String path;
    Uri uri;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        flag = getIntent().getIntExtra("flag",-1);
        path = getIntent().getStringExtra("path");

        TextView photoPath = (TextView) findViewById(R.id.path);
        photoPath.setText(path);

        title = (TextView) findViewById(R.id.title);
        switch (flag) {
            case AppEnv.SYSTEM_CAMERA:
                title.setText("使用intent唤起相机");
                break;
            case AppEnv.CAMERA:
                title.setText("基于Camera的自定义相机");
                break;
            case AppEnv.CAMERA2:
                title.setText("基于Camera2的自定义相机");
                break;
        }


//        Log.i("加载图片",path);

        image = (ImageView) findViewById(R.id.image);
        Glide.with(this).load(path).into(image);

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MediaScannerConnection.scanFile(ShowPhotoActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {

                        }
                    });
                    Toast.makeText(ShowPhotoActivity.this,"完成媒体库刷新", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ShowPhotoActivity.this, ShowFileActivity.class);
                    intent.putExtra("type","image");
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        findViewById(R.id.sendboardcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BroadcastUtil.scanFileBroadcast(ShowPhotoActivity.this, path);
                    Toast.makeText(ShowPhotoActivity.this,"完成媒体库刷新", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ShowPhotoActivity.this, ShowFileActivity.class);
                    intent.putExtra("type","image");
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }


            }
        });





    }
}
