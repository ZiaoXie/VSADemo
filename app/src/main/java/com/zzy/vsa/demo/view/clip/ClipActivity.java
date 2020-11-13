package com.zzy.vsa.demo.view.clip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;

public class ClipActivity extends AppCompatActivity {

    EditText inputclip, outputclip;
    Button copy, paste;

    Button setlistener,removelistener;
    TextView outputlistener;

    ClipboardManager clipboard;
    ClipboardManager.OnPrimaryClipChangedListener onPrimaryClipChangedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        inputclip = (EditText) findViewById(R.id.inputclip);
        outputclip = (EditText) findViewById(R.id.outputclip);

        copy = (Button) findViewById(R.id.copy);
        paste = (Button) findViewById(R.id.paste);

        // 获取系统剪贴板
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkClip()){
                    return;
                }
                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）,其他的还有
                ClipData clipData = ClipData.newPlainText(null, inputclip.getText().toString());

                // 把数据集设置（复制）到剪贴板
                clipboard.setPrimaryClip(clipData);
                if (clipboard.hasPrimaryClip()){
                    Toast.makeText(ClipActivity.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ClipActivity.this, "调用剪切板失败", Toast.LENGTH_SHORT).show();
                }

            }
        });

        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkClip()){
                    return;
                }
                // 获取剪贴板的剪贴数据集
                ClipData clipData = clipboard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    // 从数据集中获取（粘贴）第一条文本数据
                    CharSequence text = clipData.getItemAt(0).getText();
                    outputclip.setText(text);
                } else {
                    Toast.makeText(ClipActivity.this, "剪切板中没有数据", Toast.LENGTH_SHORT).show();
                }
            }
        });



        outputlistener = (TextView) findViewById(R.id.outputlistener);
        setlistener = (Button) findViewById(R.id.setlistener);
        removelistener = (Button) findViewById(R.id.removelistener);

        outputlistener.setText("监听尚未开始");

        onPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                // 获取剪贴板的剪贴数据集
                ClipData clipData = clipboard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    // 从数据集中获取（粘贴）第一条文本数据
                    CharSequence text = clipData.getItemAt(0).getText();
                    outputlistener.setText(text);
                } else {
                    Toast.makeText(ClipActivity.this, "剪切板中没有数据", Toast.LENGTH_SHORT).show();
                }
            }
        };

        setlistener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkClip()){
                    return;
                }
                outputlistener.setText("开始监听");
                clipboard.addPrimaryClipChangedListener(onPrimaryClipChangedListener);
            }
        });

        removelistener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkClip()){
                    return;
                }
                outputlistener.setText("结束监听");
                clipboard.removePrimaryClipChangedListener(onPrimaryClipChangedListener);
            }
        });

    }

    boolean checkClip(){
        if(null == clipboard){
            Toast.makeText(ClipActivity.this,"获取剪贴板失败",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
