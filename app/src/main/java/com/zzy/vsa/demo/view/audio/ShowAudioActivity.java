package com.zzy.vsa.demo.view.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.audio.PCMAudioTool;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.BroadcastUtil;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.view.filemanager.ShowFileActivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowAudioActivity extends AppCompatActivity {

    int flag;
    String path;
    TextView title;
    MediaPlayer mediaPlayer;

    TextView info;

    MyHandler handler = new MyHandler(ShowAudioActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_audio);

        flag = getIntent().getIntExtra("flag",-1);
        path = getIntent().getStringExtra("path");

        TextView photoPath = (TextView) findViewById(R.id.path);
        photoPath.setText(path);

        Log.i("音频",path);

        title = (TextView) findViewById(R.id.title);
        info = (TextView) findViewById(R.id.info);

        switch (flag) {
            case AppEnv.SYSTEM_AUDIO:
                title.setText("使用intent唤起录音");
                break;
            case AppEnv.AUDIO_RECORDER:
                title.setText("基于AudioRecorder录音");
                break;
            case AppEnv.MEDIA_RECORDER:
                title.setText("基于MediaRecorder录音");
                break;
        }

        switch (flag) {
            case AppEnv.SYSTEM_AUDIO:
            case AppEnv.MEDIA_RECORDER:
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setLooping(true);
                mediaPlayer.start();

                break;
            case AppEnv.AUDIO_RECORDER:
                PCMAudioTool.play(path);
                break;
        }

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MediaScannerConnection.scanFile(ShowAudioActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {

                        }
                    });
                    Toast.makeText(ShowAudioActivity.this,"完成媒体库刷新", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ShowAudioActivity.this, ShowFileActivity.class);
                    intent.putExtra("type","audio");
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
                    BroadcastUtil.scanFileBroadcast(ShowAudioActivity.this, path);
                    Toast.makeText(ShowAudioActivity.this,"完成媒体库刷新", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ShowAudioActivity.this, ShowFileActivity.class);
                    intent.putExtra("type","audio");
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null != mediaPlayer){
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (flag != AppEnv.AUDIO_RECORDER){
            scanfile();
        }
    }

    protected void scanfile(){
        MediaScannerConnection.scanFile(ShowAudioActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
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

//                        infostring = infostring + "宽:" + c.getString(c.getColumnIndex(MediaStore.MediaColumns.WIDTH)) + "   ";
//                        infostring = infostring + "高:" + c.getString(c.getColumnIndex(MediaStore.MediaColumns.HEIGHT)) + "\n";
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
        private WeakReference<ShowAudioActivity> mWeakReference;

        public MyHandler(ShowAudioActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ShowAudioActivity activity = mWeakReference.get();

            activity.info.setText((String)msg.obj);
            Log.i("imageinfo", (String)msg.obj);
        }
    }

}
