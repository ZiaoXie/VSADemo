package com.zzy.vsa.demo.view.copy;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.fileoperation.Fileopreration;
import com.zzy.vsa.demo.appcase.fileoperation.agnomen;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.Native;
import com.zzy.vsa.demo.view.filemanager.FileChooseActivity;

import java.lang.ref.WeakReference;

public class CopyActivity extends AppCompatActivity {
    public TextView pressent_1;
    public ProgressBar bar;
    public AlertDialog.Builder dialog_1;
    public int choose = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d("copy1", "success");
            final String selectable = data.getStringExtra("selectedfile");
            Log.d("copy1", "chushi  "+selectable);
            String inpath = agnomen.fileAgnomen(selectable);
            dialog_1.setMessage(inpath);
            bar.setVisibility(View.VISIBLE);
            pressent_1.setVisibility(View.VISIBLE);
            long size = Native.getFileSize(selectable);

            Fileopreration.multiThreadCopy(selectable, inpath, choose, size);
        }
        else {
            Toast.makeText(CopyActivity.this,"Copy failed",Toast.LENGTH_LONG).show();
            Log.d("copy1", "resultCode != Activity.RESULT_OK");
        }

    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sCopyAct = new WeakReference<>(this);
        setContentView(R.layout.activity_copy);
        bar = findViewById(R.id.progressBar_1);
        pressent_1 = findViewById(R.id.pressent);
        dialog_1 = new AlertDialog.Builder(CopyActivity.this);
        dialog_1.setTitle("path: ");
        dialog_1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final Button button_1 = findViewById(R.id.copy);
        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bar.setProgress(0);
                choose = 1;
                Intent intent = new Intent(CopyActivity.this, FileChooseActivity.class);
                intent.putExtra("operationCode", AppEnv.FILE_SHARE);
                startActivityForResult(intent, 0);

            }
        });

        final Button button_2 = findViewById(R.id.copy2);
        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setProgress(0);
                choose = 2;
                Intent intent = new Intent(CopyActivity.this, FileChooseActivity.class);
                intent.putExtra("operationCode", AppEnv.FILE_SHARE);
                startActivityForResult(intent, 0);
            }
        });

        final Button button_3 = findViewById(R.id.copy3);
        button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setProgress(0);
                choose = 3;
                Intent intent = new Intent(CopyActivity.this, FileChooseActivity.class);
                intent.putExtra("operationCode", AppEnv.FILE_SHARE);
                startActivityForResult(intent, 0);
            }
        });

    };

    public void updateProgressBar(int progress) {
        bar.setProgress(progress);
    }
    public void updateText(int progress) {
        pressent_1.setText(progress+"%");
    }
    public void dialogShow(){
        dialog_1.show();
    }
    public void invisible(){
        bar.setVisibility(View.INVISIBLE);
        pressent_1.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        sCopyAct = null;
        super.onDestroy();
    }

    private static WeakReference<CopyActivity> sCopyAct;
    public static void updateProgress(final int progress) {
        if (null != sCopyAct && null != sCopyAct.get()) {
            sCopyAct.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != sCopyAct && null != sCopyAct.get()) {
                        sCopyAct.get().updateProgressBar(progress);
                        sCopyAct.get().updateText(progress);
                        if (progress == 100) {
                            sCopyAct.get().dialogShow();
                            sCopyAct.get().invisible();
                        }
                    }
                }
            });
        }
    }
}

