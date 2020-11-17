package com.zzy.vsa.demo.view.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.BroadcastUtil;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.view.filemanager.ShowFileActivity;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowPhotoActivity extends AppCompatActivity {

    ImageView image;
    int flag;
    String path;
    Uri uri;
    TextView title;
    TextView info;

    MyHandler handler = new MyHandler(ShowPhotoActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);



        flag = getIntent().getIntExtra("flag",-1);
        path = getIntent().getStringExtra("path");

        TextView photoPath = (TextView) findViewById(R.id.path);
        photoPath.setText(path);

        title = (TextView) findViewById(R.id.title);
        info = (TextView) findViewById(R.id.info);

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

        image = (ImageView) findViewById(R.id.image);
        Glide.with(this).load(path).into(image);

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MediaScannerConnection.scanFile(ShowPhotoActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            Intent intent = new Intent(ShowPhotoActivity.this, ShowFileActivity.class);
                            intent.putExtra("type","image");
                            startActivity(intent);
                        }
                    });
                    Toast.makeText(ShowPhotoActivity.this,"完成媒体库刷新", Toast.LENGTH_SHORT).show();


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

    @Override
    protected void onResume(){
        super.onResume();

        MediaScannerConnection.scanFile(ShowPhotoActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                Log.i("imageinfo", "Scanned " + path + ":");
                Log.i("imageinfo", "-> uri=" + uri);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        null, MediaStore.Images.Media.DATA + "=? ", new String[] { path }, null);

                Cursor c = getContentResolver().query(uri,
                        null, null, null, null);

                String infostring = "文件信息:\n";
                if( c != null ){
                    if(c.moveToFirst()){
                        infostring = infostring + "文件ID:" + c.getLong(c.getColumnIndex(MediaStore.MediaColumns._ID)) + "\n";
                        infostring = infostring + "文件名:" + c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)) + "\n";

                        String time = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));
                        infostring = infostring + "文件创建时间:" + sdf.format(new Date(Long.parseLong(time)*1000)) + "\n";

                        time = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
                        infostring = infostring + "文件修改时间:" + sdf.format(new Date(Long.parseLong(time)*1000)) + "\n";

                        Long size = Long.parseLong(c.getString(c.getColumnIndex(MediaStore.MediaColumns.SIZE)));
                        infostring = infostring + "文件大小:" + FileUtil.sizeToChange(size) + "\n";

                        infostring = infostring + "宽:" + c.getString(c.getColumnIndex(MediaStore.MediaColumns.WIDTH)) + "   ";
                        infostring = infostring + "高:" + c.getString(c.getColumnIndex(MediaStore.MediaColumns.HEIGHT)) + "\n";
                    } else {
                        Log.e("imageinfo","nullquery");
                    }
                    c.close();

                } else {
                    Log.e("imageinfo","nullcursor");
                }

                Message msg = new Message();
                msg.obj = infostring;
                handler.sendMessage(msg);

            }
        });
    }

    private static class MyHandler extends Handler {
        private WeakReference<ShowPhotoActivity> mWeakReference;

        public MyHandler(ShowPhotoActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ShowPhotoActivity activity = mWeakReference.get();

            activity.info.setText((String)msg.obj);
            Log.i("imageinfo", (String)msg.obj);
        }
    }

}
