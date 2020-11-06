package com.zzy.vsa.demo.view.filemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzy.vsa.demo.R;

public class FileMenuActivity extends AppCompatActivity {

    LinearLayout audio,video,image,document,archive,installation,collection,recent;
    TextView audionum,videonum,imagenum;
    TextView documentnum,archivenum,installationnum;
    TextView collectionnum,recentnum;

    RelativeLayout all,localprovider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_menu);

        initView();
    }


    private void initView(){
        audio = (LinearLayout) findViewById(R.id.audio);
        video = (LinearLayout) findViewById(R.id.video);
        image = (LinearLayout) findViewById(R.id.image);
        document = (LinearLayout) findViewById(R.id.document);
        archive = (LinearLayout) findViewById(R.id.archive);
        installation = (LinearLayout) findViewById(R.id.installation);
        collection = (LinearLayout) findViewById(R.id.collection);
        recent = (LinearLayout) findViewById(R.id.recent);

        all = (RelativeLayout) findViewById(R.id.all);
        localprovider = (RelativeLayout) findViewById(R.id.localprovider);

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileMenuActivity.this, ShowFileActivity.class);
                intent.putExtra("type","audio");
                startActivity(intent);
            }
        });


        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileMenuActivity.this, ShowFileActivity.class);
                intent.putExtra("type","video");
                startActivity(intent);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileMenuActivity.this, ShowFileActivity.class);
                intent.putExtra("type","image");
                startActivity(intent);
            }
        });

        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileMenuActivity.this, ShowFileActivity.class);
                intent.putExtra("type","document");
                startActivity(intent);
            }
        });

        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileMenuActivity.this, ShowFileActivity.class);
                intent.putExtra("type","archive");
                startActivity(intent);
            }
        });

        installation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileMenuActivity.this, ShowFileActivity.class);
                intent.putExtra("type","installation");
                startActivity(intent);
            }
        });

        collection.setVisibility(View.GONE);
        recent.setVisibility(View.GONE);

        TextView allnum = (TextView) all.findViewById(R.id.text);
        allnum.setText("全部文件");
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FileMenuActivity.this, FileManagerActivity.class));
            }
        });

        TextView localproviderText = (TextView) localprovider.findViewById(R.id.text);
        localproviderText.setText("查找本地fileprovider");
        localprovider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileMenuActivity.this, ShowFileActivity.class);
                intent.putExtra("type","provider");
                startActivity(intent);
            }
        });
        localprovider.setVisibility(View.GONE);


        audionum = (TextView) findViewById(R.id.audionum);
        videonum = (TextView) findViewById(R.id.vedionum);
        imagenum = (TextView) findViewById(R.id.imagenum);
        documentnum = (TextView) findViewById(R.id.documentnum);
        archivenum = (TextView) findViewById(R.id.archivenum);
        installationnum = (TextView) findViewById(R.id.installationnum);
        collectionnum = (TextView) findViewById(R.id.collectionnum);
        recentnum = (TextView) findViewById(R.id.recentnum);


    }

    @Override
    protected void onResume(){
        super.onResume();

        new FileNumberTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
    }

    class FileNumberTask extends AsyncTask{

        int audio_count,video_count,image_count;
        int doc_count = 0,arch_count = 0,installion_count = 0 ;
        ContentResolver mContentResolver;

        @Override
        protected Object doInBackground(Object[] objects) {
            mContentResolver = getContentResolver();
            Cursor c = null;
            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            audio_count = c.getCount();


            c = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            video_count = c.getCount();

            c = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            image_count = c.getCount();

            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    "(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='text/plain')", null, null);
            doc_count = c.getCount();


            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    "(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='application/zip' or " + MediaStore.Files.FileColumns.MIME_TYPE + "=='application/rar' )", null, null);
            arch_count = c.getCount();

            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.apk' )", null, null);
            installion_count = c.getCount();

            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            audionum.setText("音频("+ audio_count +")");
            videonum.setText("视频(" + video_count +")");
            imagenum.setText("图片("+ image_count +")");
            documentnum.setText("文档("+  doc_count +")");
            archivenum.setText("压缩包("+ arch_count +")");
            installationnum.setText("安装包(" + installion_count +")");
        }


    }

}
