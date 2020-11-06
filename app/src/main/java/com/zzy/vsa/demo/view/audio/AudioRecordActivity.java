package com.zzy.vsa.demo.view.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.audio.PCMAudioTool;
import com.zzy.vsa.demo.appenv.AppEnv;

public class AudioRecordActivity extends AppCompatActivity {

    Button audio;

    boolean audioflag = false;

    String audiopath;

    final PCMAudioTool pcmAudioTool = new PCMAudioTool();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        audio = (Button) findViewById(R.id.audio);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!pcmAudioTool.isUseful()){
                    Toast.makeText(AudioRecordActivity.this, "录音失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!audioflag){
                    audioflag = true;
                    audio.setText("停止录音");

                    audiopath = pcmAudioTool.startRecord();

                } else {
                    pcmAudioTool.stopRecord();

                    Intent intent = new Intent(AudioRecordActivity.this, ShowAudioActivity.class);
                    intent.putExtra("flag", AppEnv.AUDIO_RECORDER);
                    intent.putExtra("path", audiopath);
                    startActivity(intent);
                }
            }
        });

    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        pcmAudioTool.stopRecord();
    }



}
