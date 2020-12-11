package com.zzy.vsa.demo.appcase.webview;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.view.fileoperation.FileDownloadActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class WebDownloadHelper {

    private Activity context;
    private String url;
    private String userAgent;
    private String contentDisposition;
    private String mimetype;
    private long contentLength;

    private String filename;
    private String destPath;

    public WebDownloadHelper(Activity context, String url, String userAgent, String contentDisposition, String mimetype, long contentLength){
        this.context = context;
        this.url = url;
        this.userAgent = userAgent;
        this.contentDisposition = contentDisposition;
        this.mimetype = mimetype;
        this.contentLength = contentLength;

        this.filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        this.destPath = FileUtil.initWebDownloaderStorage(filename);
    }

    public void downloadByBrowser(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }


    public void downloadBySystem(){
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
//        request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName  = URLUtil.guessFileName(url, contentDisposition, mimetype);
//            log.debug("fileName:{}", fileName);
//            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setDestinationUri(Uri.fromFile(new File(FileUtil.initWebDownloaderStorage(fileName))));
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
//            log.debug("downloadId:{}", downloadId);
    }


    public void downloadByURL(){
        new DownloadTask().execute(url, destPath);
    }

    public class DownloadTask extends AsyncTask<String, Void, Void> {
        // 传递两个参数：URL 和 目标路径
        private String downloadurl;
        private String destPath;

        @Override
        protected void onPreExecute() {
//            log.info("开始下载");
        }

        @Override
        protected Void doInBackground(String... params) {
//            log.debug("doInBackground. url:{}, dest:{}", params[0], params[1]);
            downloadurl = params[0];
            destPath = params[1];
            OutputStream out = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
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
            Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileUtil.getUriByPath(context, new File(destPath));
//            Uri uri = Uri.fromFile(new File(destPath));
//            log.debug("mimiType:{}, uri:{}", mimeType, uri);
            Toast.makeText(context, "完成下载" , Toast.LENGTH_SHORT).show();
            handlerIntent.setDataAndType(uri, mimetype);
            context.startActivity(handlerIntent);
        }
    }

    public void downloadByOkhttp(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                Log.e("tag", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String fileName = FileUtil.initWebDownloaderStorage(URLUtil.guessFileName(url, contentDisposition, mimetype));

//                HttpUrl realUrl = response.request().url();
//                Log.e("zmm", "real:" + realUrl);
//                if (realUrl != null) {
//                    String temp = realUrl.toString();
//                    temp = temp.substring(temp.lastIndexOf("/") + 1);
//                    temp = URLDecoder.decode(temp, "UTF-8");
//                    fileName = FileUtil.getAppRootPath() + File.separator + temp ;
//                }

                File targetfile = new File(fileName + ".temp");
                File resfile = new File(fileName);

                if (resfile.exists()){
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "该文件已存在", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                final ResponseBody body = response.body();        // 获取到请求体
                InputStream inputStream = body.byteStream();    // 转换成字节流
                final long totallength = body.contentLength();

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
//                        Log.e("tag", "progress" + count + "max" + totallength);
                    }
                    inputStream.close();        // 关闭输入流
                    outputStream.close();       // 关闭输出流

                    targetfile.renameTo(resfile);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


}
