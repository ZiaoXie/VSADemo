package com.zzy.vsa.demo.view.fileoperation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.filemanager.FileOperationHelper;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.view.filemanager.FileManagerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileOperationActivity extends AppCompatActivity {

    final int APK = 0;
    MyHandler handler = new MyHandler(FileOperationActivity.this);

    Button downloadapk;
    TextView apkdownloadresult;
    Button apk2zip;
    TextView apk2zip_result;
    Button apk2zip_view;
    String apk_path;

    Button zip2apk;
    TextView zip2apk_result;
    Button zip2apk_view;
    String zip_path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operation);

        initAPK();



    }

    protected void initAPK(){
        downloadapk = (Button) findViewById(R.id.downloadapk);

        apkdownloadresult = (TextView) findViewById(R.id.apkdownloadresult);
        apkdownloadresult.setVisibility(View.GONE);

        apk2zip = (Button) findViewById(R.id.apk2zip);
        apk2zip.setVisibility(View.GONE);
        apk2zip_result = (TextView) findViewById(R.id.apk2zip_result);
        apk2zip_result.setVisibility(View.GONE);
        apk2zip_view = (Button) findViewById(R.id.apk2zip_view);
        apk2zip_view.setVisibility(View.GONE);

        zip2apk = (Button) findViewById(R.id.zip2apk);
        zip2apk.setVisibility(View.GONE);
        zip2apk_result = (TextView) findViewById(R.id.zip2apk_result);
        zip2apk_result.setVisibility(View.GONE);
        zip2apk_view = (Button) findViewById(R.id.zip2apk_view);
        zip2apk_view.setVisibility(View.GONE);

        downloadapk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://192.168.1.37:9003/ReBuildApk/rebuildapk/download?file=/17187/77287/2020/11/04/com.zzy.vsa.demo_1_1604475819271_uusafe_signed_64.apk&type=1";
                apk_path = FileUtil.getAppRootPath() + File.separator + URLUtil.guessFileName(url,null,"application/vnd.android.package-archive");
                if(! new File(apk_path).exists()){
                    downloadByURL(url, apk_path,String.valueOf(APK));
                } else {
                    Message msg = new Message();
                    msg.what = APK;
                    handler.sendMessage(msg);
                    Toast.makeText(FileOperationActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                }
            }
        });

        apk2zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = apk_path.lastIndexOf(".");
                zip_path = apk_path.substring(0,index) + ".zip";
                File file = new File(apk_path);
                File target = new File(zip_path);
                file.renameTo(target);

                apk2zip_result.setVisibility(View.VISIBLE);
                apk2zip_view.setVisibility(View.VISIBLE);
                apk2zip_result.setText("生成的ZIP文件路径:"+zip_path);
            }
        });

        apk2zip_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileOperationActivity.this, FileManagerActivity.class);
                intent.putExtra("rootPath",FileUtil.getAppRootPath());
                startActivity(intent);
            }
        });

        zip2apk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Cursor c = null;
//                try {
//                    c = getContentResolver().query(MediaStore.Files.getContentUri("external"), null,
//                            "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.zip' )", null, null);
//                    if(c != null && c.moveToFirst()){
//                        String path = c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));// 路径
//                        String title = c.getString(c.getColumnIndex(MediaStore.MediaColumns.TITLE));
//
//                        FileOperationHelper.getInstance(FileOperationActivity.this).copy(new File(path),new File(FileUtil.getAppRootPath()));
//
//                        c.close();
//                    } else {
//                        Toast.makeText(FileOperationActivity.this, "搜索不到本地ZIP",Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (c != null) {
//                        c.close();
//                    }
//                }

                if (TextUtils.isEmpty(zip_path) || TextUtils.isEmpty(apk_path)){
                    return;
                }

                File file = new File(zip_path);
                File target = new File(apk_path);
                file.renameTo(target);

                zip2apk_result.setVisibility(View.VISIBLE);
                zip2apk_view.setVisibility(View.VISIBLE);
                zip2apk_result.setText("生成的APK文件路径:"+apk_path);
            }
        });

        zip2apk_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileOperationActivity.this, FileManagerActivity.class);
                intent.putExtra("rootPath",FileUtil.getAppRootPath());
                startActivity(intent);
            }
        });
    }


    public void downloadByURL(String url, String destPath, String msg){
        new DownloadTask().execute(url, destPath, msg);
    }

    private class DownloadTask extends AsyncTask<String, Void, Void> {

        private int what;

        @Override
        protected void onPreExecute() {
//            log.info("开始下载");
        }

        @Override
        protected Void doInBackground(String... params) {
//            log.debug("doInBackground. url:{}, dest:{}", params[0], params[1]);
            what = Integer.parseInt(params[2]);
            OutputStream out = null;
            HttpURLConnection urlConnection = null;
            try {
                URL urltarget = new URL(params[0]);
                urlConnection = (HttpURLConnection) urltarget.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(15000);
                InputStream in = urlConnection.getInputStream();
                out = new FileOutputStream(params[1]);
                byte[] buffer = new byte[10 * 1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Message msg = new Message();
            msg.what = what;
            handler.sendMessage(msg);
        }
    }

    private class MyHandler extends Handler{
        private WeakReference<FileOperationActivity> mWeakReference;

        public MyHandler(FileOperationActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FileOperationActivity activity = mWeakReference.get();
            switch (msg.what){
                case APK:
                    apkdownloadresult.setVisibility(View.VISIBLE);
                    apkdownloadresult.setText("下载文件为："+apk_path);
                    apk2zip.setVisibility(View.VISIBLE);
                    zip2apk.setVisibility(View.VISIBLE);
                    break;

            }
        }
    }
}
