package com.zzy.vsa.demo.view.audio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.util.UriUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class AudioEnterActivity extends AppCompatActivity {

    private static final String TAG = "AudioEnterActivity";

    RelativeLayout system, audio_recorder, media_recorder;
    TextView systemText,audio_recorderText, media_recorderText;

    private String fileName;
    private String mVoicePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_enter);

        system = (RelativeLayout) findViewById(R.id.system);
        systemText = (TextView) system.findViewById(R.id.text);
        systemText.setText("调用系统录音");
        system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRecord = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intentRecord.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(intentRecord, AppEnv.SYSTEM_AUDIO);
            }
        });

        audio_recorder = (RelativeLayout) findViewById(R.id.audio_recorder);
        audio_recorderText = (TextView) audio_recorder.findViewById(R.id.text);
        audio_recorderText.setText("测试AudioRecorder");
        audio_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AudioEnterActivity.this, AudioRecordActivity.class));
            }
        });

        media_recorder = (RelativeLayout) findViewById(R.id.media_recorder);
        media_recorderText = (TextView) media_recorder.findViewById(R.id.text);
        media_recorderText.setText("测试MediaRecorder");
        media_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AudioEnterActivity.this, AudioByMediaRecorderActivity.class));
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == AppEnv.SYSTEM_AUDIO) {
            Uri uri = data.getData();
            if (uri != null) {
                Log.i("voicetest", uri.toString() + "+++" + uri.getAuthority());
                String temp;
                if(uri.getAuthority().equals(MediaStore.AUTHORITY) ){
                    mVoicePath = FileUtil.initAudioStorage().getAbsolutePath();
                    temp = UriUtil.getFilePathFromUri(AudioEnterActivity.this,uri);
                    saveVoiceToSD(getInputStream(temp));
                    getContentResolver().delete(uri, null, null);
                    (new File(temp)).delete();
//
                } else {
                    temp = FileUtil.getSuffix(uri.toString());
                    if(TextUtils.isEmpty(temp)){
                        Toast.makeText(AudioEnterActivity.this, "获取后缀名失败",Toast.LENGTH_LONG).show();
                        return;
                    }
                    mVoicePath = Environment.getExternalStorageDirectory().getPath() + File.separator +
                            AppEnv.PACKAGE_NAME + File.separator + System.currentTimeMillis() + "." + temp;
                    try {
                        saveVoiceToSD(getContentResolver().openInputStream(uri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent = new Intent(AudioEnterActivity.this, ShowAudioActivity.class);
            intent.putExtra("flag", AppEnv.SYSTEM_AUDIO);
            intent.putExtra("path", mVoicePath);
            startActivity(intent);
        }
    }

    /**
     * 保存音频到SD卡的指定位置
     *
     * @param path 录音文件的路径
     */

    public InputStream getInputStream(String path){
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveVoiceToSD(InputStream isFrom) {
        boolean b = false;
        //创建输入输出
        FileOutputStream osTo = null;
        try {
            osTo = new FileOutputStream(mVoicePath);
            byte bt[] = new byte[1024];
            int len;
            while ((len = isFrom.read(bt)) != -1) {
                osTo.write(bt, 0, len);
            }
            Log.i(TAG, "保存录音完成。");
            b=true;
        } catch (IOException e) {
            e.printStackTrace();
            b=false;
            Log.i(TAG, "e:"+e.toString());
        } finally {
            if (osTo != null) {
                try {
                    //不管是否出现异常，都要关闭流
                    osTo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    b=false;
                }
            }
            if (isFrom != null) {
                try {
                    isFrom.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    b=false;
                }
            }
        }

        return b;
    }
}
