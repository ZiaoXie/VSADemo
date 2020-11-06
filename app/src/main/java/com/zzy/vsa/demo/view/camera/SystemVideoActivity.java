package com.zzy.vsa.demo.view.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.util.UriUtil;

import java.io.File;

public class SystemVideoActivity extends AppCompatActivity {
    RelativeLayout getUriByFile,insertFirst,withoutUri,clipdata;
    TextView getUriByFileText,insertFirstText,withoutUriText,clipdataText;

    File cameraSavePath;
    Uri uri;

    final int PROVIDER = 0;
    final int INSERT = 1 ;
    final int WITHOUTURI = 2;
    final int CLIPDATA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video);


        getUriByFile = (RelativeLayout) findViewById(R.id.getUriByFile);
        getUriByFileText = (TextView) getUriByFile.findViewById(R.id.text);
        getUriByFileText.setText("从指定路径获取Uri");
        getUriByFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSavePath = FileUtil.initVideoStorage();
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                uri = FileUtil.getUriByPath(SystemVideoActivity.this, cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, PROVIDER);
            }
        });


        insertFirst = (RelativeLayout) findViewById(R.id.insertFirst);
        insertFirstText = (TextView) insertFirst.findViewById(R.id.text);
        insertFirstText.setText("预先插入数据库");
        insertFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSavePath = FileUtil.initVideoStorage();
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.TITLE, cameraSavePath.getAbsolutePath());
                uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, INSERT);
            }
        });


        withoutUri = (RelativeLayout) findViewById(R.id.withoutUri);
        withoutUriText = (TextView) withoutUri.findViewById(R.id.text);
        withoutUriText.setText("使用系统默认路径");
        withoutUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSavePath = FileUtil.initVideoStorage();
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(intent, WITHOUTURI);
            }
        });


        clipdata = (RelativeLayout) findViewById(R.id.clipdata);
        clipdataText = (TextView) clipdata.findViewById(R.id.text);
        clipdataText.setText("使用ClipData");
        clipdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSavePath = FileUtil.initVideoStorage();
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                uri = FileUtil.getUriByPath(SystemVideoActivity.this, cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));
                    startActivityForResult(intent, CLIPDATA);
                } else {
                    Toast.makeText(SystemVideoActivity.this,"SDK低于21", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("test",uri.toString() + UriUtil.getFileContentUri(CameraEnterActivity.this, cameraSavePath));
        switch (requestCode){
            case PROVIDER:
                if (resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(SystemVideoActivity.this,ShowVideoActivity.class);
                    intent.putExtra("flag", AppEnv.SYSTEM_CAMERA);
                    intent.putExtra("path", cameraSavePath.getAbsolutePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case INSERT:
                if (resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(SystemVideoActivity.this, ShowVideoActivity.class);
                    intent.putExtra("flag", AppEnv.SYSTEM_CAMERA);
                    intent.putExtra("path", UriUtil.getFilePathFromUri(SystemVideoActivity.this, uri));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case WITHOUTURI:
                if (resultCode == Activity.RESULT_OK){
                    Bitmap bitmap = data.getParcelableExtra("data");
                } else {
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case CLIPDATA:
                if (resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(SystemVideoActivity.this, ShowVideoActivity.class);
                    intent.putExtra("flag", AppEnv.SYSTEM_CAMERA);
                    intent.putExtra("path", cameraSavePath.getAbsolutePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }
}
