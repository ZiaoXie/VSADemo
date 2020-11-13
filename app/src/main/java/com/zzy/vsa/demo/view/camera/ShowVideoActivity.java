package com.zzy.vsa.demo.view.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.BroadcastUtil;
import com.zzy.vsa.demo.view.filemanager.ShowFileActivity;

public class ShowVideoActivity extends AppCompatActivity {

    int flag;
    String path;
    Uri uri;
    TextView title;
    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        flag = getIntent().getIntExtra("flag",-1);
        path = getIntent().getStringExtra("path");

        TextView photoPath = (TextView) findViewById(R.id.path);
        photoPath.setText(path);

        title = (TextView) findViewById(R.id.title);
        switch (flag) {
            case AppEnv.SYSTEM_CAMERA:
                title.setText("使用intent唤起录像");
                break;
            case AppEnv.CAMERA:
                title.setText("基于Camera的自定义录像");
                break;
            case AppEnv.CAMERA2:
                title.setText("基于Camera2的自定义录像");
                break;
        }

        video = (VideoView) findViewById(R.id.videoplayer);
        //给VideoView设置播放来源
        video.setVideoPath(path);
        //实例化一个媒体控制器
        MediaController mediaController=new MediaController(this);
        video.setMediaController(mediaController);
        mediaController.setMediaPlayer(video);


        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MediaScannerConnection.scanFile(ShowVideoActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {

                        }
                    });
                    Toast.makeText(ShowVideoActivity.this,"完成媒体库刷新", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ShowVideoActivity.this, ShowFileActivity.class);
                    intent.putExtra("type","video");
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
                    BroadcastUtil.scanFileBroadcast(ShowVideoActivity.this, path);
                    Toast.makeText(ShowVideoActivity.this,"完成媒体库刷新", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ShowVideoActivity.this, ShowFileActivity.class);
                    intent.putExtra("type","video");
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}
