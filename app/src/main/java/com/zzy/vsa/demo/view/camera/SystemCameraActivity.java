package com.zzy.vsa.demo.view.camera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import java.io.IOException;

public class SystemCameraActivity extends AppCompatActivity {

    RelativeLayout getUriByFile,dcim;
    RelativeLayout insertFirst,withoutUri,clipdata,call;
    TextView getUriByFileText,dcimText;
    TextView insertFirstText,withoutUriText,clipdataText,callText;

    ImageView iv;
    File cameraSavePath;
    Uri uri;

    final int PROVIDER = 0;
    final int INSERT = 1 ;
    final int WITHOUTURI = 2;
    final int CLIPDATA = 3;
    final int SCAN_FILE_CALL = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_camera);


        getUriByFile = (RelativeLayout) findViewById(R.id.getUriByFile);
        getUriByFileText = (TextView) getUriByFile.findViewById(R.id.text);
        getUriByFileText.setText("图片存储在应用路径内");
        getUriByFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSavePath = FileUtil.initPhotoStorage();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uri = FileUtil.getUriByPath(SystemCameraActivity.this, cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, PROVIDER);
            }
        });

        dcim = (RelativeLayout) findViewById(R.id.dcim);
        dcimText = (TextView) dcim.findViewById(R.id.text);
        dcimText.setText("存储在DCIM");
        dcim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSavePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                        File.separator + System.currentTimeMillis() + ".jpg");

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uri = FileUtil.getUriByPath(SystemCameraActivity.this, cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, PROVIDER);
            }
        });


        insertFirst = (RelativeLayout) findViewById(R.id.insertFirst);
        insertFirstText = (TextView) insertFirst.findViewById(R.id.text);
        insertFirstText.setText("调用系统拍照方法2");
        insertFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSavePath = FileUtil.initPhotoStorage();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, cameraSavePath.getAbsolutePath());
                uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, INSERT);
            }
        });


        iv = (ImageView) findViewById(R.id.iv);
        withoutUri = (RelativeLayout) findViewById(R.id.withoutUri);
        withoutUriText = (TextView) withoutUri.findViewById(R.id.text);
        withoutUriText.setText("调用系统拍照方法3");
        withoutUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSavePath = FileUtil.initPhotoStorage();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uri = FileUtil.getUriByPath(SystemCameraActivity.this, cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(intent, WITHOUTURI);
            }
        });


        clipdata = (RelativeLayout) findViewById(R.id.clipdata);
        clipdataText = (TextView) clipdata.findViewById(R.id.text);
        clipdataText.setText("调用系统拍照方法4");
        clipdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP){
                    Toast.makeText(SystemCameraActivity.this,"该功能仅限Android 5.0", Toast.LENGTH_SHORT).show();
                    return;
                }

                cameraSavePath = FileUtil.initPhotoStorage();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uri = FileUtil.getUriByPath(SystemCameraActivity.this, cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.setClipData(ClipData.newRawUri(null, uri));
//
//                    intent.setClipData(ClipData.newUri(getContentResolver(),"test",uri));
                startActivityForResult(intent, CLIPDATA);
            }
        });

        call = (RelativeLayout) findViewById(R.id.call);
        callText = (TextView) call.findViewById(R.id.text);
        callText.setText("调用系统拍照方法5");
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                    Toast.makeText(SystemCameraActivity.this, "当前版本过低不支持此功能", Toast.LENGTH_LONG).show();
                    return;
                }

                cameraSavePath = FileUtil.initPhotoStorage();


                try {
                    if(!cameraSavePath.exists()){
                        cameraSavePath.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bundle extras = new Bundle();
                extras.putParcelable(Intent.EXTRA_STREAM,Uri.fromFile(cameraSavePath));
                Bundle result = getContentResolver().call(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "scan_file", null, extras);

                if (result != null) {
                    uri = result.getParcelable(Intent.EXTRA_STREAM);
                    Log.i("cameratest",uri.toString());
                } else {
                    Toast.makeText(SystemCameraActivity.this, "扫描文件失败", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, SCAN_FILE_CALL);

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
                    Intent intent = new Intent(SystemCameraActivity.this,ShowPhotoActivity.class);
                    intent.putExtra("flag",AppEnv.SYSTEM_CAMERA);
                    intent.putExtra("path", cameraSavePath.getAbsolutePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case INSERT:
                if (resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(SystemCameraActivity.this, ShowPhotoActivity.class);
                    intent.putExtra("flag", AppEnv.SYSTEM_CAMERA);
                    intent.putExtra("path", UriUtil.getFilePathFromUri(SystemCameraActivity.this, uri));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case WITHOUTURI:
                if (resultCode == Activity.RESULT_OK){
                    Bitmap bitmap = data.getParcelableExtra("data");
                    iv.setImageBitmap(bitmap);
                    iv.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case CLIPDATA:
                if (resultCode == Activity.RESULT_OK){
                    if (data != null){
                        Log.i("testcamera",data.getClipData().getItemAt(0).getUri().toString());
                    }

                    Intent intent = new Intent(SystemCameraActivity.this, ShowPhotoActivity.class);
                    intent.putExtra("flag", AppEnv.SYSTEM_CAMERA);
                    intent.putExtra("path", cameraSavePath.getAbsolutePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case SCAN_FILE_CALL:
                if (resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(SystemCameraActivity.this, ShowPhotoActivity.class);
                    intent.putExtra("flag", AppEnv.SYSTEM_CAMERA);
                    intent.putExtra("path", UriUtil.getFilePathFromUri(SystemCameraActivity.this, uri));
                    startActivity(intent);
                } else {
                    cameraSavePath.delete();
                    int del = getContentResolver().delete(uri,null,null);
                    Log.i("cameratest","删除了"+ del +"个文件");
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

}
