package com.zzy.vsa.demo.view.fileoperation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.zzy.vsa.demo.bean.FileClassificationBean;
import com.zzy.vsa.demo.util.FileOpenUtil;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.view.filemanager.FileManagerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileDownloadActivity extends AppCompatActivity {

    static ArrayList<FileClassificationBean> document_item = new ArrayList<FileClassificationBean>();

    static {
        document_item.add(new FileClassificationBean("pages", ""));
        document_item.add(new FileClassificationBean("doc", ""));
        document_item.add(new FileClassificationBean("html", ""));
        document_item.add(new FileClassificationBean("java", ""));
        document_item.add(new FileClassificationBean("xls", ""));
        document_item.add(new FileClassificationBean("py", ""));
        document_item.add(new FileClassificationBean("htm", ""));
        document_item.add(new FileClassificationBean("pdf", ""));
        document_item.add(new FileClassificationBean("txt", ""));
        document_item.add(new FileClassificationBean("ppt", ""));
        document_item.add(new FileClassificationBean("docx", ""));
        document_item.add(new FileClassificationBean("pptx", ""));
        document_item.add(new FileClassificationBean("xlsx", ""));
        document_item.add(new FileClassificationBean("csv", ""));
        document_item.add(new FileClassificationBean("xml", ""));
        document_item.add(new FileClassificationBean("c", ""));
        document_item.add(new FileClassificationBean("sh", ""));
    }

    static ArrayList<FileClassificationBean> video_item = new ArrayList<FileClassificationBean>();

    static {
        video_item.add(new FileClassificationBean("flv", ""));
        video_item.add(new FileClassificationBean("avi", ""));
        video_item.add(new FileClassificationBean("mkv", ""));
        video_item.add(new FileClassificationBean("mov", ""));
        video_item.add(new FileClassificationBean("rmvb", ""));
        video_item.add(new FileClassificationBean("mpg", ""));
        video_item.add(new FileClassificationBean("mp4", ""));
    }

    static ArrayList<FileClassificationBean> image_item = new ArrayList<FileClassificationBean>();

    static {
        image_item.add(new FileClassificationBean("png", ""));
        image_item.add(new FileClassificationBean("tif", ""));
        image_item.add(new FileClassificationBean("bmp", ""));
        image_item.add(new FileClassificationBean("gif", ""));
        image_item.add(new FileClassificationBean("pic", ""));
        image_item.add(new FileClassificationBean("jpg", ""));
        image_item.add(new FileClassificationBean("jpeg", ""));
    }

    static ArrayList<FileClassificationBean> audio_item = new ArrayList<FileClassificationBean>();

    static {
        audio_item.add(new FileClassificationBean("amr", ""));
        audio_item.add(new FileClassificationBean("wma", ""));
        audio_item.add(new FileClassificationBean("mp3", ""));
        audio_item.add(new FileClassificationBean("wav", ""));

    }

    static ArrayList<FileClassificationBean> compress_item = new ArrayList<FileClassificationBean>();

    static {
        compress_item.add(new FileClassificationBean("zip", ""));
        compress_item.add(new FileClassificationBean("7z", ""));
        compress_item.add(new FileClassificationBean("tar", ""));
        compress_item.add(new FileClassificationBean("tar.gz", ""));
        compress_item.add(new FileClassificationBean("rar", ""));
        compress_item.add(new FileClassificationBean("jar", ""));
        compress_item.add(new FileClassificationBean("war", ""));
    }

    static ArrayList<FileClassificationBean> others_item = new ArrayList<FileClassificationBean>();

    static {
        others_item.add(new FileClassificationBean("sql", ""));
    }

    ArrayList<FileClassificationBean> current_item;
    RecyclerView recyclerView;
    MyAdapter myAdapter;


    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

    AlertDialog alertDialog;
    View viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        viewDialog = (View)getLayoutInflater().inflate(R.layout.layout_progress_dialog,null);
        alertDialog = new AlertDialog.Builder(FileDownloadActivity.this).setTitle("下载进度")
                .setCancelable(false).setView(viewDialog).create();


        String type = getIntent().getStringExtra("type");
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
            case "compress":
                current_item = compress_item;
                break;
            case "others":
                current_item = others_item;
                break;
        }
        myAdapter = new MyAdapter(current_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(myAdapter);

    }

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        ArrayList<FileClassificationBean> item = null;

        public MyAdapter(ArrayList<FileClassificationBean> item) {
            this.item = item;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(FileDownloadActivity.this).inflate(R.layout.list_item_download, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
            holder.init();
            holder.type.setText(item.get(position).getSuffix());
            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mime = mimeTypeMap.getMimeTypeFromExtension("txt");
                    alertDialog.show();
                    saveFile(item.get(position).getUrl(), holder,position);
                }
            });

            holder.downloadview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FileDownloadActivity.this, FileManagerActivity.class);
                    intent.putExtra("rootPath",FileUtil.getAppRootPath());
                    startActivity(intent);
                }
            });

            holder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileOpenUtil.openIntent(FileDownloadActivity.this, new File(current_item.get(position).getPath()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return item.size();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView type;
        Button download;
        TextView downloadresult;
        Button downloadview, open;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.type);
            download = (Button) itemView.findViewById(R.id.download);
            downloadresult = (TextView) itemView.findViewById(R.id.downloadresult);
            downloadview = (Button) itemView.findViewById(R.id.downloadview);
            open = (Button) itemView.findViewById(R.id.open);
        }

        public void init() {
            downloadresult.setVisibility(View.GONE);
            downloadview.setVisibility(View.GONE);
            open.setVisibility(View.GONE);
        }
    }

    private void saveFile(String url, final MyHolder holder, final int position) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Request request = new Request.Builder()
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

                String fileName = "";

                HttpUrl realUrl = response.request().url();
                Log.e("zmm", "real:" + realUrl);
                if (realUrl != null) {
                    String temp = realUrl.toString();
                    fileName = FileUtil.getAppRootPath() + File.separator + temp.substring(temp.lastIndexOf("/") + 1);
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
                            holder.downloadresult.setVisibility(View.VISIBLE);
                            holder.downloadresult.setText("文件下载路径:"+ current_item.get(position).getPath());
                            holder.downloadview.setVisibility(View.VISIBLE);
                            holder.open.setVisibility(View.VISIBLE);
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
                            holder.downloadresult.setVisibility(View.VISIBLE);
                            holder.downloadresult.setText("文件下载路径:"+ current_item.get(position).getPath());
                            holder.downloadview.setVisibility(View.VISIBLE);
                            holder.open.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

}
