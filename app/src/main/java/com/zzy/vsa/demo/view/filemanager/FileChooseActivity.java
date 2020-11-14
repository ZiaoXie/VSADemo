package com.zzy.vsa.demo.view.filemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.filemanager.adapter.FileAdapter;
import com.zzy.vsa.demo.appcase.filemanager.adapter.TitleAdapter;
import com.zzy.vsa.demo.appcase.filemanager.adapter.base.RecyclerViewAdapter;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.bean.FileBean;
import com.zzy.vsa.demo.appenv.FileType;
import com.zzy.vsa.demo.bean.TitlePath;
import com.zzy.vsa.demo.appcase.filemanager.FileOperationHelper;
import com.zzy.vsa.demo.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileChooseActivity extends AppCompatActivity {

    private RecyclerView title_recycler_view ;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<FileBean> beanList = new ArrayList<>();
    private File rootFile ;
    private LinearLayout empty_rel ;
    private String rootPath ;
    private TitleAdapter titleAdapter ;

    private FileOperationHelper fileOperationHelper;
    TextView sure,mkdir;

    int operationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_choose);

        operationCode = getIntent().getIntExtra("operationCode",-1);

        //设置Title
        title_recycler_view = (RecyclerView) findViewById(R.id.title_recycler_view);
        title_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL , false ));
        titleAdapter = new TitleAdapter( FileChooseActivity.this , new ArrayList<TitlePath>() ) ;
        title_recycler_view.setAdapter( titleAdapter );

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
                if( operationCode == AppEnv.FILE_SHARE &&  ! new File(file.getPath()).isDirectory() ){
                    Intent i = new Intent();
                    i.putExtra("selectedfile", file.getPath());
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }

                FileType fileType = file.getFileType() ;
                if ( fileType == FileType.directory) {
                    getFile(file.getPath());
                    refreshTitleState( file.getName() , file.getPath() );
                }
            }
        });



        titleAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                TitlePath titlePath = (TitlePath) titleAdapter.getItem( position );
                getFile( titlePath.getPath() );

                int count = titleAdapter.getItemCount() ;
                int removeCount = count - position - 1 ;
                for ( int i = 0 ; i < removeCount ; i++ ){
                    titleAdapter.removeLast();
                }
            }
        });

        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        sure = (TextView) findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = getIntent().getStringExtra("path");
                switch (operationCode){
                    case AppEnv.FILE_COPY:
                        fileOperationHelper.copyFileAndDir(new File(path),rootFile);
                        break;
                    case AppEnv.FILE_MOVE:
                        fileOperationHelper.copyFileAndDir(new File(path),rootFile);
                        fileOperationHelper.delete(path);
                        break;
                }
                finish();
            }
        });

        mkdir = (TextView) findViewById(R.id.mkdir);
        mkdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View viewDialog=(View)getLayoutInflater().inflate(R.layout.layout_edittext_dialog,null);
                new AlertDialog.Builder(FileChooseActivity.this).setTitle("请输入新的名称")
                        .setView(viewDialog)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                EditText newnameExitText = (EditText)viewDialog.findViewById(R.id.input);
                                String newname = newnameExitText.getText().toString();
                                File f = new File(rootFile.getAbsolutePath()+ File.separator+ newname);
                                if(!f.exists()){
                                    f.mkdirs();
                                }

                                FileBean fileBean = new FileBean();
                                fileBean.setName(f.getName());
                                fileBean.setPath(f.getAbsolutePath());
                                fileBean.setFileType( FileUtil.getFileType( f ));
                                fileBean.setChildCount( FileUtil.getFileChildCount( f ));
                                fileBean.setSize( f.length() );

                                beanList.add(fileBean);

                                fileAdapter.notifyDataSetChanged();
                            }
                        }).show();
            }
        });

        if(operationCode == AppEnv.FILE_SHARE){
            mkdir.setVisibility(View.GONE);
            sure.setVisibility(View.GONE);
        }


    }

    @Override
    protected void onResume(){
        super.onResume();

        getFile(rootPath);
        refreshTitleState( "内部存储设备" , rootPath );
    }

    public void getFile(String path ) {
        rootPath = path;
        rootFile = new File( path + File.separator  )  ;
        new MyTask(rootFile).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , "") ;
    }

    class MyTask extends AsyncTask {
        File file;

        MyTask(File file) {
            this.file = file;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            List<FileBean> fileBeenList = new ArrayList<>();
            if ( file.isDirectory() ) {
                File[] filesArray = file.listFiles();
                if ( filesArray != null ){
                    List<File> fileList = new ArrayList<>() ;
                    Collections.addAll( fileList , filesArray ) ;  //把数组转化成list
                    Collections.sort( fileList , FileUtil.comparator );  //按照名字排序

                    for (File f : fileList ) {
                        if (f.isHidden()) continue;

                        FileBean fileBean = new FileBean();
                        fileBean.setName(f.getName());
                        fileBean.setPath(f.getAbsolutePath());
                        fileBean.setFileType( FileUtil.getFileType( f ));
                        fileBean.setChildCount( FileUtil.getFileChildCount( f ));
                        fileBean.setSize( f.length() );

                        fileBeenList.add(fileBean);

                    }
                }
            }

            beanList = fileBeenList;
            return fileBeenList;
        }

        @Override
        protected void onPostExecute(Object o) {
            if ( beanList.size() > 0  ){
                empty_rel.setVisibility( View.GONE );
            }else {
                empty_rel.setVisibility( View.VISIBLE );
            }
            fileAdapter.refresh(beanList);
        }
    }

    void refreshTitleState( String title , String path ){
        TitlePath filePath = new TitlePath() ;
        filePath.setNameState( title + " > " );
        filePath.setPath( path );
        titleAdapter.addItem( filePath );
        title_recycler_view.smoothScrollToPosition( titleAdapter.getItemCount());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            List<TitlePath> titlePathList = (List<TitlePath>) titleAdapter.getAdapterData();
            if ( titlePathList.size() == 1 ){
                finish();
            }else {
                titleAdapter.removeItem( titlePathList.size() - 1 );
                getFile( titlePathList.get(titlePathList.size() - 1 ).getPath() );
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}