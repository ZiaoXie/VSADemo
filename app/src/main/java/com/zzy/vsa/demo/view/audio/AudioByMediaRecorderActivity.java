package com.zzy.vsa.demo.view.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.FileUtil;

import java.io.IOException;

public class AudioByMediaRecorderActivity extends AppCompatActivity {

    Button audio;

    boolean audioflag = false;

    String audiopath;

    MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_by_media_recorder);

        audiopath = FileUtil.initAudioStorage().getAbsolutePath();

        audio = (Button) findViewById(R.id.audiobutton);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!audioflag){
                    audioflag = true;
                    audio.setText("停止录音");

                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiopath);
                    try {
                        recorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recorder.start();   // 开始录音

                } else {

                    recorder.stop();
                    recorder.reset();   // You can reuse the object by going back to setAudioSource() step
                    recorder.release(); // Now the object cannot be reused

                    Intent intent = new Intent(AudioByMediaRecorderActivity.this, ShowAudioActivity.class);
                    intent.putExtra("flag", AppEnv.MEDIA_RECORDER);
                    intent.putExtra("path", audiopath);
                    startActivity(intent);
                }
            }
        });
    }





}
