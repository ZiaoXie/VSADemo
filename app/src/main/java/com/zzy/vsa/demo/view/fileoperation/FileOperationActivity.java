package com.zzy.vsa.demo.view.fileoperation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileOperationActivity extends AppCompatActivity {

    final int APK = 0;

    Button downloadapk;
    TextView apkdownloadresult;
    Button apk2zip;
    TextView apk2zip_result;
    Button apk2zip_view;
    String apk_path = "";

    Button zip2apk;
    TextView zip2apk_result;
    Button zip2apk_view;
    String zip_path = "";

    AlertDialog alertDialog;
    View viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operation);

        viewDialog = (View)getLayoutInflater().inflate(R.layout.layout_progress_dialog,null);
        alertDialog = new AlertDialog.Builder(FileOperationActivity.this).setTitle("下载进度")
                .setCancelable(false).setView(viewDialog).create();

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
//                    downloadByURL(url, apk_path,String.valueOf(APK));
                    alertDialog.show();

                    saveFile(url, apk_path,APK);
                } else {
                    apkdownloadresult.setVisibility(View.VISIBLE);
                    apkdownloadresult.setText("下载文件为："+apk_path);
                    apk2zip.setVisibility(View.VISIBLE);
                    zip2apk.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
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


    private void saveFile(String url, final String destPath, final int what) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Request request = new Request.Builder().addHeader("Accept-Encoding", "identity")
                .url(url)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("tag", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final ResponseBody body = response.body();        // 获取到请求体
                InputStream inputStream = body.byteStream();    // 转换成字节流
                final long totallength = body.contentLength();


                final TextView tv = viewDialog.findViewById(R.id.tv);

                long count = 0;

                try {
                    // 获取到输出流，写入到的地址
                    FileOutputStream outputStream = new FileOutputStream(new File(destPath));
                    int length = -1;
                    byte[] bytes = new byte[1024 * 10];
                    while ((length = inputStream.read(bytes)) != -1) {
                        // 写入文件
                        outputStream.write(bytes, 0, length);
                        count += length;

                        final long finalCount = count;
                        final int finalLenght = length;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(finalCount< totallength){
                                    tv.setText((int) (100 * finalCount / totallength) + "%");  // 设置进度文本 （100 * 当前进度 / 总进度）
                                } else {
                                    tv.setText("正在下载:"+ FileUtil.sizeToChange(finalCount) );
                                }

                            }
                        });
                        Log.e("tag", "progress" + count + "max" + totallength);
                    }
                    inputStream.close();        // 关闭输入流
                    outputStream.close();       // 关闭输出流
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 如果写入的进度值完毕，Toast
                            Toast.makeText(FileOperationActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
//                            alertDialog.dismiss();
//                            pb.setProgress(0);
                            apkdownloadresult.setVisibility(View.VISIBLE);
                            apkdownloadresult.setText("下载文件为："+apk_path);
                            apk2zip.setVisibility(View.VISIBLE);
                            zip2apk.setVisibility(View.VISIBLE);
                            alertDialog.dismiss();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        new File(apk_path).delete();
        new File(zip_path).delete();

    }

}
