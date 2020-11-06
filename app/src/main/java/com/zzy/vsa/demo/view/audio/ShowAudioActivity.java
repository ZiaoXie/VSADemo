package com.zzy.vsa.demo.view.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.audio.PCMAudioTool;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.BroadcastUtil;
import com.zzy.vsa.demo.view.filemanager.ShowFileActivity;

import java.io.File;
import java.io.IOException;

public class ShowAudioActivity extends AppCompatActivity {

    int flag;
    String path;
    TextView title;
    MediaPlayer mediaPlayer;

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

}
