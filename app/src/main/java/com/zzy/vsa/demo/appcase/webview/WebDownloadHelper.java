package com.zzy.vsa.demo.appcase.webview;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.zzy.vsa.demo.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class WebDownloadHelper {

    private Context context;
    private String url;
    private String userAgent;
    private String contentDisposition;
    private String mimetype;
    private long contentLength;

    private String filename;
    private String destPath;

    public WebDownloadHelper(Context context,String url, String userAgent, String contentDisposition, String mimetype, long contentLength){
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

    private class DownloadTask extends AsyncTask<String, Void, Void> {
        // 传递两个参数：URL 和 目标路径
        private String url;
        private String destPath;

        @Override
        protected void onPreExecute() {
//            log.info("开始下载");
        }

        @Override
        protected Void doInBackground(String... params) {
//            log.debug("doInBackground. url:{}, dest:{}", params[0], params[1]);
            url = params[0];
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


}
