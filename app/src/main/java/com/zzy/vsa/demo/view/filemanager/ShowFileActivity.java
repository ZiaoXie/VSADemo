package com.zzy.vsa.demo.view.filemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.filemanager.FileOperationHelper;
import com.zzy.vsa.demo.appcase.filemanager.FileQueryHelper;
import com.zzy.vsa.demo.appcase.filemanager.adapter.FileAdapter;
import com.zzy.vsa.demo.appcase.filemanager.adapter.base.RecyclerViewAdapter;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.appenv.FileType;
import com.zzy.vsa.demo.bean.FileBean;
import com.zzy.vsa.demo.util.FileOpenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowFileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<FileBean> beanList = new ArrayList<>();
    private FileQueryHelper fileQueryHelper;
    private FileOperationHelper fileOperationHelper;
    private LinearLayout empty_rel ;

    String rootPath;
    int operationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_file);

        fileQueryHelper = new FileQueryHelper(this);
        operationCode = getIntent().getIntExtra("operationCode",-1);

        String type = getIntent().getStringExtra("type");
        switch (type){
            case "audio":
                beanList = fileQueryHelper.getMusics();
                break;
            case "video":
                beanList = fileQueryHelper.getVideos();
                break;
            case "image":
                beanList = fileQueryHelper.getImageFolders();
                break;
            case "document":
                beanList = fileQueryHelper.getDocuments();
                break;
            case "archive":
                beanList = fileQueryHelper.getArchives();
                break;
            case "installation":
                beanList = fileQueryHelper.getInstallions();
                break;
            case "provider":
                beanList = fileQueryHelper.getFilesFromLocalProvider();
                break;
        }


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        fileAdapter = new FileAdapter(this, beanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(fileAdapter);

        fileOperationHelper = FileOperationHelper.getInstance(this);

        empty_rel = (LinearLayout) findViewById( R.id.empty_rel );

        fileAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                FileBean file = beanList.get(position);
                if( operationCode == AppEnv.FILE_SHARE){
                    Intent i = new Intent();
                    i.putExtra("selectedfile", file.getPath());
                    setResult(Activity.RESULT_OK, i);
                    finish();
                    return;
                }

                FileType fileType = file.getFileType() ;
                if ( fileType == FileType.directory) {
                    beanList = fileQueryHelper.getImgListByDir(file.getPath());
                    fileAdapter.refresh(beanList);
                }else {
                    FileOpenUtil.openIntent(ShowFileActivity.this, new File( file.getPath() ) );
                }
            }
        });


        fileAdapter.setOnItemLongClickListener(new RecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, final int position) {
                final FileBean fileBean = (FileBean) fileAdapter.getItem( position );


                final AlertDialog alertDialog = new AlertDialog.Builder(ShowFileActivity.this).setTitle("确定要删除该文件吗？").
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fileOperationHelper.delete(fileBean.getPath());
                        beanList.remove(fileBean);
                        fileAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

                return false;
            }
        });


    }




}
