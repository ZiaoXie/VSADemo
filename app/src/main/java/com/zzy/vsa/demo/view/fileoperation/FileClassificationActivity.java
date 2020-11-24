package com.zzy.vsa.demo.view.fileoperation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzy.vsa.demo.R;

public class FileClassificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_classification);

        RelativeLayout document = (RelativeLayout) findViewById(R.id.document_classification);
        TextView documentText = (TextView) document.findViewById(R.id.text);
        documentText.setText("文档");
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileClassificationActivity.this, FileDownloadActivity.class);
                intent.putExtra("type","document");
                startActivity(intent);
            }
        });

        RelativeLayout video = (RelativeLayout) findViewById(R.id.video_classification);
        TextView videoText = (TextView) video.findViewById(R.id.text);
        videoText.setText("视频");
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileClassificationActivity.this, FileDownloadActivity.class);
                intent.putExtra("type","video");
                startActivity(intent);
            }
        });

        RelativeLayout image = (RelativeLayout) findViewById(R.id.image_classification);
        TextView imageText = (TextView) image.findViewById(R.id.text);
        imageText.setText("图片");
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileClassificationActivity.this, FileDownloadActivity.class);
                intent.putExtra("type","image");
                startActivity(intent);
            }
        });

        RelativeLayout audio = (RelativeLayout) findViewById(R.id.audio_classification);
        TextView audioText = (TextView) audio.findViewById(R.id.text);
        audioText.setText("音频");
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileClassificationActivity.this, FileDownloadActivity.class);
                intent.putExtra("type","audio");
                startActivity(intent);
            }
        });

        RelativeLayout compressed = (RelativeLayout) findViewById(R.id.compressed_classification);
        TextView compressedText = (TextView) compressed.findViewById(R.id.text);
        compressedText.setText("压缩包");
        compressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileClassificationActivity.this, FileDownloadActivity.class);
                intent.putExtra("type","compressed");
                startActivity(intent);
            }
        });

        RelativeLayout others = (RelativeLayout) findViewById(R.id.others_classification);
        TextView othersText = (TextView) others.findViewById(R.id.text);
        othersText.setText("其他");
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileClassificationActivity.this, FileDownloadActivity.class);
                intent.putExtra("type","others");
                startActivity(intent);
            }
        });

    }
}
