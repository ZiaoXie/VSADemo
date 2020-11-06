package com.zzy.vsa.demo.view.filemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.util.UriUtil;

public class FileEnterActivity extends AppCompatActivity {

    RelativeLayout pick, getcontent, myfilemanager;
    TextView pickText,getcontentText, myfilemanagerText;

    TextView show;

    final int PICK_IMAGE = 10;
    final int PICK_AUDIO = 11;
    final int PICK_VIDEO = 12;
    final int PICK_OTHER = 13;

    final int GET_CONTENT_IMAGE = 20;
    final int GET_CONTENT_AUDIO = 21;
    final int GET_CONTENT_VIDEO = 22;
    final int GET_CONTENT_OTHER = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_enter);

        pick = (RelativeLayout) findViewById(R.id.pick);
        pickText = (TextView) pick.findViewById(R.id.text);
        pickText.setText("Intent.ACTION_PICK");
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(FileEnterActivity.this).setItems(new String[]{"相册","音频","视频","其他"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which){
                            case 0:
                                intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
//                                intent = Intent.createChooser(intent, "Choose a file");
                                startActivityForResult(intent, PICK_IMAGE);
                                break;
                            case 1:
                                intent=new Intent(Intent.ACTION_PICK);
//                                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                                intent.setType("audio/*");
//                                intent = Intent.createChooser(intent, "Choose a file");
                                startActivityForResult(intent,PICK_AUDIO);
                                break;
                            case 2:
                                intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("video/*");
//                                intent = Intent.createChooser(intent, "Choose a file");
                                startActivityForResult(intent, PICK_VIDEO);
                                break;
                            case 3:
                                intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("*/*");
                                startActivityForResult(intent, PICK_OTHER);
                                break;
                        }
                    }
                }).show();
            }
        });

        getcontent = (RelativeLayout) findViewById(R.id.getcontent);
        getcontentText = (TextView) getcontent.findViewById(R.id.text);
        getcontentText.setText("Intent.ACTION_GET_CONTENT");
        getcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FileEnterActivity.this).setItems(new String[]{"相册","音频","视频","其他"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        switch (which){
                            case 0:
                                intent.setType("image/*");
                                startActivityForResult(intent,GET_CONTENT_IMAGE);
                                break;
                            case 1:
                                intent.setType("audio/*");
                                startActivityForResult(intent,GET_CONTENT_AUDIO);
                                break;
                            case 2:
                                intent.setType("video/*");
                                startActivityForResult(intent,GET_CONTENT_VIDEO);
                                break;
                            case 3:
                                intent.setType("*/*");
                                startActivityForResult(intent,GET_CONTENT_OTHER);
                                break;
                        }
                    }
                }).show();
            }
        });



        myfilemanager = (RelativeLayout) findViewById(R.id.myfilemanager);
        myfilemanagerText = (TextView) myfilemanager.findViewById(R.id.text);
        myfilemanagerText.setText("自定义文件管理器");
        myfilemanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FileEnterActivity.this, FileMenuActivity.class));
            }
        });

        show = (TextView) findViewById(R.id.show);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            Log.i("test",data.getData().toString());
            show.setText("选中的文件为："+UriUtil.getFilePathFromUri(FileEnterActivity.this, data.getData()));
//            switch (requestCode){
//                case PICK_IMAGE:
//                case GET_CONTENT_IMAGE:
//                case GET_CONTENT_AUDIO:
//                case GET_CONTENT_VIDEO:
//                case GET_CONTENT_OTHER:
//
//                    break;
//            }
//
        }
    }

}
