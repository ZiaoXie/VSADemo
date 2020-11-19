package com.zzy.vsa.demo.view.share;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.HttpUtils;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.appcase.share.ShareUtil;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.util.UriUtil;
import com.zzy.vsa.demo.view.filemanager.FileChooseActivity;
import com.zzy.vsa.demo.view.filemanager.ShowFileActivity;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    final int SELECTFILE = 0;
    final int SELECTAPK = 1;
    Intent intent;

    EditText text;
    Button sharetext1, sharetext2, selectfile;
    Button sharefile1, sharefile2, sharefile3, sharefile4, sharefile5;
    TextView targetfile, targetapk;
    String selectedfile, selectedapk;

    Button selectapk, installapk, installapk2, installapk3;

    Button qqsharedefault;

    List<ResolveInfo> resInfos;

    Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
// 其中APP_ID是分配给第三方应用的appid，类型为String。
// 其中Authorities为 Manifest文件中注册FileProvider时设置的authorities属性值
        mTencent = Tencent.createInstance("101917806", this.getApplicationContext(), "com.zzy.vsa.demo.fileprovider");
// 1.4版本:此处需新增参数，传入应用程序的全局context，可通过activity的getApplicationContext方法获取

        initTextShare();
        initFileShare();
        initAPKShare();
        initQQShare();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTencent.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTFILE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedfile = data.getStringExtra("selectedfile");
                    targetfile.setText("选中文件:" + selectedfile);
                }
                break;
            case SELECTAPK:
                if (resultCode == Activity.RESULT_OK) {
                    selectedapk = data.getStringExtra("selectedfile");
                    targetapk.setText("选中文件:" + selectedapk);
                }
                break;

        }
    }

    public void initTextShare() {
        text = (EditText) findViewById(R.id.text);
        targetfile = (TextView) findViewById(R.id.targetfile);
        targetapk = (TextView) findViewById(R.id.targetapk);

        sharetext1 = (Button) findViewById(R.id.sharetext1);
        sharetext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = ShareUtil.shareText(text.getEditableText().toString());
                startActivity(intent);
            }
        });


        sharetext2 = (Button) findViewById(R.id.sharetext2);
        sharetext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resInfos = ShareUtil.shareTextWithFilter(ShareActivity.this, selectedfile);
                String items[] = new String[resInfos.size()];
                for (int i = 0; i < resInfos.size(); i++) {
                    items[i] = resInfos.get(i).loadLabel(getPackageManager()).toString();
                }
                new AlertDialog.Builder(ShareActivity.this).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResolveInfo info = resInfos.get(which);
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                        startActivity(intent);
                    }
                }).show();
            }
        });
    }

    public void initFileShare() {
        selectfile = (Button) findViewById(R.id.selectfile);
        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareActivity.this, FileChooseActivity.class);
                intent.putExtra("operationCode", AppEnv.FILE_SHARE);
                startActivityForResult(intent, SELECTFILE);
            }
        });


        sharefile1 = (Button) findViewById(R.id.sharefile1);
        sharefile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedfile)) {
                    return;
                }
                intent = ShareUtil.shareFile(ShareActivity.this, selectedfile);
                if (null != intent) {
                    startActivity(intent);
                }
            }
        });


        sharefile2 = (Button) findViewById(R.id.sharefile2);
        sharefile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedfile)) {
                    return;
                }
                resInfos = ShareUtil.shareFileWithFilter(ShareActivity.this, selectedfile);
                String items[] = new String[resInfos.size()];
                for (int i = 0; i < resInfos.size(); i++) {
                    items[i] = resInfos.get(i).loadLabel(getPackageManager()).toString();
                }
                new AlertDialog.Builder(ShareActivity.this).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResolveInfo info = resInfos.get(which);
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                        startActivity(intent);
                    }
                }).show();

            }
        });


        sharefile3 = (Button) findViewById(R.id.sharefile3);
        sharefile3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedfile)) {
                    return;
                }
                intent = ShareUtil.shareFile(ShareActivity.this, selectedfile);

                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_CHOOSER);
                intent2.putExtra(Intent.EXTRA_TITLE, "选择");
                intent2.putExtra(Intent.EXTRA_INTENT, intent);
                startActivity(intent2);
            }
        });

        sharefile4 = (Button) findViewById(R.id.sharefile4);
        sharefile4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedfile)) {
                    return;
                }
                intent = ShareUtil.shareFile(ShareActivity.this, selectedfile);

                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_PICK_ACTIVITY);
                intent2.putExtra(Intent.EXTRA_TITLE, "选择");
                intent2.putExtra(Intent.EXTRA_INTENT, intent);
                startActivity(intent2);
            }
        });

        sharefile5 = (Button) findViewById(R.id.sharefile5);
        sharefile5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedfile)) {
                    return;
                }
                intent = ShareUtil.initFileShareIntent(ShareActivity.this, selectedfile);
                intent = ShareUtil.shareFileWithChooser(ShareActivity.this, intent);
                startActivity(intent);
            }
        });
    }

    public void initAPKShare() {
        selectapk = (Button) findViewById(R.id.selectapk);
        selectapk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareActivity.this, ShowFileActivity.class);
                intent.putExtra("type", "installation");
                intent.putExtra("operationCode", AppEnv.FILE_SHARE);
                startActivityForResult(intent, SELECTAPK);
            }
        });

        installapk = (Button) findViewById(R.id.installapk);
        installapk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedapk)) {
                    return;
                }
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(UriUtil.getFileContentUri(ShareActivity.this, new File(selectedapk)), "application/vnd.android.package-archive");
                startActivity(intent);


            }
        });

        installapk2 = (Button) findViewById(R.id.installapk2);
        installapk2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedapk)) {
                    return;
                }
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(FileUtil.getUriByPath(ShareActivity.this, new File(selectedapk)), "application/vnd.android.package-archive");
                startActivity(intent);
            }
        });

        installapk3 = (Button) findViewById(R.id.installapk3);
        installapk3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedapk)) {
                    return;
                }
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(Uri.fromFile(new File(selectedapk)), "application/vnd.android.package-archive");
                startActivity(intent);
            }
        });
    }

    public void initQQShare() {
        qqsharedefault = (Button) findViewById(R.id.qqsharedefault);
        qqsharedefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    public void share() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "分享");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://www.baidu.com");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "VSADemo");
        mTencent.shareToQQ(this, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onWarning(int i) {

            }
        });
    }


    class BaseUiListener implements IUiListener {
        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onComplete(Object o) {

        }

        @Override
        public void onError(UiError e) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onWarning(int i) {

        }
    }

    private class BaseApiListener implements IRequestListener {
        @Override
        public void onComplete(JSONObject jsonObject) {

        }

        @Override
        public void onIOException(IOException e) {

        }

        @Override
        public void onMalformedURLException(MalformedURLException e) {

        }

        @Override
        public void onJSONException(JSONException e) {

        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e) {

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException e) {

        }

        @Override
        public void onNetworkUnavailableException(HttpUtils.NetworkUnavailableException e) {

        }

        @Override
        public void onHttpStatusException(HttpUtils.HttpStatusException e) {

        }

        @Override
        public void onUnknowException(Exception e) {

        }
    }


}
