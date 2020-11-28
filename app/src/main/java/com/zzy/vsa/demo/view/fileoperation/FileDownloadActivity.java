package com.zzy.vsa.demo.view.fileoperation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.fileoperation.DefaultHolder;
import com.zzy.vsa.demo.appcase.fileoperation.DownloadHolder;
import com.zzy.vsa.demo.appcase.fileoperation.FileOperationAdapter;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.bean.FileClassificationBean;
import com.zzy.vsa.demo.util.FileOpenUtil;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.view.filemanager.FileManagerActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileDownloadActivity extends AppCompatActivity {

    final String download_path = FileUtil.getAppRootPath() + File.separator + "download";

    final static int DOWNLOAD = 0;
    final static int CONVERT = 1;

    ArrayList<FileClassificationBean> document_item = new ArrayList<FileClassificationBean>();
    ArrayList<FileClassificationBean> video_item = new ArrayList<FileClassificationBean>();
    ArrayList<FileClassificationBean> image_item = new ArrayList<FileClassificationBean>();
    ArrayList<FileClassificationBean> audio_item = new ArrayList<FileClassificationBean>();
    ArrayList<FileClassificationBean> compress_item = new ArrayList<FileClassificationBean>();
    ArrayList<FileClassificationBean> others_item = new ArrayList<FileClassificationBean>();


    ArrayList<FileClassificationBean> current_item;
    RecyclerView recyclerView;
    FileOperationAdapter myAdapter;

    AlertDialog alertDialog;
    View viewDialog;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initItem();

        File file = new File(download_path);
        if(!file.exists()){
            file.mkdirs();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        viewDialog = (View)getLayoutInflater().inflate(R.layout.layout_progress_dialog,null);
        alertDialog = new AlertDialog.Builder(FileDownloadActivity.this).setTitle("下载进度")
                .setCancelable(false).setView(viewDialog).create();


        String type = getIntent().getStringExtra("type");
        if(!TextUtils.isEmpty(type)){
            switch (type) {
                case "document":
                    current_item = document_item;
                    break;
                case "video":
                    current_item = video_item;
                    break;
                case "image":
                    current_item = image_item;
                    break;
                case "audio":
                    current_item = audio_item;
                    break;
                case "compressed":
                    current_item = compress_item;
                    break;
                case "others":
                    current_item = others_item;
                    break;
                default:
                    current_item = image_item;
                    break;
            }
        }

        myAdapter = new FileOperationAdapter(FileDownloadActivity.this, current_item, download_path);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(myAdapter);

    }

    public void saveFile(String url, final DefaultHolder holder, final int position) {

        alertDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FileDownloadActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                });

                Log.e("tag", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String fileName = "";

                HttpUrl realUrl = response.request().url();
                Log.e("zmm", "real:" + realUrl);
                if (realUrl != null) {
                    String temp = realUrl.toString();
                    temp = temp.substring(temp.lastIndexOf("/") + 1);
                    temp = URLDecoder.decode(temp, "UTF-8");
                    fileName = download_path + File.separator + temp ;
                    if (TextUtils.isEmpty(fileName)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FileDownloadActivity.this, "获取文件名失败", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                        return;
                    }
                    current_item.get(position).setPath(fileName);
                }

                File targetfile = new File(fileName);

                if (targetfile.exists()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 如果写入的进度值完毕，Toast
                            Toast.makeText(FileDownloadActivity.this, "该文件已存在", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            holder.showDownload("文件下载路径:"+ current_item.get(position).getPath());
                        }
                    });
                    return;
                }

                final ResponseBody body = response.body();        // 获取到请求体
                InputStream inputStream = body.byteStream();    // 转换成字节流
                final long totallength = body.contentLength();


                final TextView tv = viewDialog.findViewById(R.id.tv);

                long count = 0;

                try {
                    // 获取到输出流，写入到的地址
                    FileOutputStream outputStream = new FileOutputStream(targetfile);
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
                            Toast.makeText(FileDownloadActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            holder.showDownload("文件下载路径:"+ current_item.get(position).getPath());
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FileDownloadActivity.this, "下载文件失败", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    });
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

//        for(FileClassificationBean item : current_item){
//            String path = item.getPath();
//            if(!TextUtils.isEmpty(path)){
//                File file = new File(path);
//                if(file.exists()){
//                    file.delete();
//                }
//            }
//        }

    }

    void initItem(){
        document_item.clear();
        document_item.add(new FileClassificationBean("pages", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/easy.pages"));
        document_item.add(new FileClassificationBean("doc", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/doc.doc"));
        document_item.add(new FileClassificationBean("html", ""));
        document_item.add(new FileClassificationBean("java", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/CheckCategroyExist.java"));
        document_item.add(new FileClassificationBean("xls", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/xls.xls"));
        document_item.add(new FileClassificationBean("py", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/cmd.py"));
        document_item.add(new FileClassificationBean("htm", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/%E7%BE%8E%E9%A4%90.htm"));
        document_item.add(new FileClassificationBean("pdf", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/%E5%AE%89%E5%85%A8%E6%8A%A5%E5%91%8A.pdf"));
        document_item.add(new FileClassificationBean("txt", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/2.1%E5%8D%87%E7%BA%A72.1.0.1utf8.txt"));
        document_item.add(new FileClassificationBean("ppt", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/ppt.ppt"));
        document_item.add(new FileClassificationBean("docx", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/%E5%AE%89%E5%85%A8%E9%82%AE%E4%BB%B6.docx"));
        document_item.add(new FileClassificationBean("pptx", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/%E7%89%88%E6%9C%AC%E9%85%8D%E7%BD%AE.pptx"));
        document_item.add(new FileClassificationBean("xlsx", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/%E9%9C%80%E6%B1%82%E9%97%AE%E9%A2%98.xlsx"));
        document_item.add(new FileClassificationBean("csv", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/%E5%8C%BB%E7%96%97%E6%9C%BA%E6%9E%84.csv"));
        document_item.add(new FileClassificationBean("xml", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/xml.xml"));
        document_item.add(new FileClassificationBean("c", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/C%E8%AF%AD%E8%A8%80.c"));
        document_item.add(new FileClassificationBean("sh", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/start-service.sh"));

        video_item.clear();
        video_item.add(new FileClassificationBean("flv", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/A%20is%20for%20apple.flv"));
        video_item.add(new FileClassificationBean("avi", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/Go_Away.avi"));
        video_item.add(new FileClassificationBean("mkv", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/The%20Wind%20Blew_480P_0.mkv"));
        video_item.add(new FileClassificationBean("mov", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/mov.mov"));
        video_item.add(new FileClassificationBean("rmvb", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/rmvb.rmvb"));
        video_item.add(new FileClassificationBean("mpg", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/mpg.mpg"));
        video_item.add(new FileClassificationBean("mp4", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/normal%20video.mp4"));

        image_item.clear();
        image_item.add(new FileClassificationBean("png", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/logo.png"));
        image_item.add(new FileClassificationBean("tif", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/tif.tif"));
        image_item.add(new FileClassificationBean("bmp", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/bmp.bmp"));
        image_item.add(new FileClassificationBean("gif", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/640.gif"));
        image_item.add(new FileClassificationBean("pic", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/pic%E5%8A%A8%E5%9B%BE.pic"));
        image_item.add(new FileClassificationBean("jpg", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/jpg.jpg"));
        image_item.add(new FileClassificationBean("jpeg", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/jpeg.jpeg"));

        audio_item.clear();
        audio_item.add(new FileClassificationBean("amr", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/R2014.amr"));
        audio_item.add(new FileClassificationBean("wma", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/wma.wma"));
        audio_item.add(new FileClassificationBean("mp3", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/3102.mp3"));
        audio_item.add(new FileClassificationBean("wav", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/2211.wav"));

        compress_item.clear();
        compress_item.add(new FileClassificationBean("zip", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/didi%20iOS%E6%B5%8B%E8%AF%95%E6%96%87%E4%BB%B6.zip"));
        compress_item.add(new FileClassificationBean("7z", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/Cookies.7z"));
        compress_item.add(new FileClassificationBean("tar", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/up.tar"));
        compress_item.add(new FileClassificationBean("tar.gz", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/testfile.tar.gz"));
        compress_item.add(new FileClassificationBean("rar", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/%7F%E4%BB%BB%E5%8A%A1.rar"));
        compress_item.add(new FileClassificationBean("jar", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/jackson-databind-2.9.10.3.jar"));
        compress_item.add(new FileClassificationBean("war", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/soap.war"));

        others_item.clear();
        others_item.add(new FileClassificationBean("sql", "http://192.168.1.98:8888/dev_share/fileEncrypt/files/up_db.sql"));
        others_item.add(new FileClassificationBean("apk","http://192.168.1.37:9003/ReBuildApk/rebuildapk/download?file=/17187/77287/2020/11/04/com.zzy.vsa.demo_1_1604475819271_uusafe_signed_64.apk",CONVERT));

    }



}
