package com.zzy.vsa.demo.view.webview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.webview.WebDownloadHelper;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.util.UriUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowHtmlActivity extends AppCompatActivity {

    WebView webView;
    Button download1, download2, download3;
    TextView dest;

    WebDownloadHelper webDownloadHelper;
    EditText address;
    Button sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_html);

        webView = (WebView) findViewById(R.id.webview);
        address = (EditText) findViewById(R.id.address);
        sure = (Button) findViewById(R.id.sure);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(address.getText().toString());
            }
        });

        webView.loadUrl("https://www.baidu.com");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                webDownloadHelper = new WebDownloadHelper(ShowHtmlActivity.this, url, userAgent, contentDisposition, mimetype, contentLength);
                new AlertDialog.Builder(ShowHtmlActivity.this).setTitle("选择文件操作")
                        .setItems(new String[]{"下载文件方法1(默认浏览器)", "下载文件方法2(DownloadManager)", "下载文件方法3(HttpURLConnection)","下载文件方法4(Okhttp)"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        webDownloadHelper.downloadByBrowser();
                                        break;
                                    case 1:
                                        webDownloadHelper.downloadBySystem();
                                        break;
                                    case 2:
                                        webDownloadHelper.downloadByURL();
                                        break;
                                    case 3:
                                        webDownloadHelper.downloadByOkhttp();
                                        break;
                                }
                            }
                        }).show();
            }
        });

        dest = (TextView) findViewById(R.id.dest);

        download1 = (Button) findViewById(R.id.download1);
        download2 = (Button) findViewById(R.id.download2);
        download3 = (Button) findViewById(R.id.download3);

        // 使用
        DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, intentFilter);

        download1.setVisibility(View.GONE);
        download2.setVisibility(View.GONE);
        download3.setVisibility(View.GONE);


    }


    private class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            log.verbose("onReceive. intent:{}", intent != null ? intent.toUri(0) : null);
            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                    log.debug("downloadId:{}", downloadId);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
//                    log.debug("getMimeTypeForDownloadedFile:{}", type);
                    if (TextUtils.isEmpty(type)) {
                        type = "*/*";
                    }
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
//                    log.debug("UriForDownloadedFile:{}", uri);
//                    if (uri != null) {
//                        Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
//                        handlerIntent.setDataAndType(uri, type);
//                        context.startActivity(handlerIntent);
//                    }
                    dest.setText(UriUtil.getFilePathFromUri(ShowHtmlActivity.this, uri));
                    Toast.makeText(ShowHtmlActivity.this, "完成下载", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    //内部类
    public class MyWebViewClient extends WebViewClient {
        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，
        // 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
        public boolean shouldOverviewUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }

        public void onPageFinished(WebView view, String url) {

        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        }
    }

    int count = 0;

    // 如果不做任何处理，浏览网页，点击系统“Back”键，整个Browser会调用finish()而结束自身，
    // 如果希望浏览的网 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件。
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && count == 0) {
            Toast.makeText(ShowHtmlActivity.this, "再按一次以退出网页", Toast.LENGTH_SHORT).show();
            count++;
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && count > 0) {
            finish();
            return true;
        }

        return false;
    }


}
