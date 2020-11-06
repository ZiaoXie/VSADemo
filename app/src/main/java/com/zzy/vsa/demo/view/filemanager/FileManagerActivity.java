package com.zzy.vsa.demo.view.filemanager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.filemanager.adapter.FileAdapter;
import com.zzy.vsa.demo.appcase.filemanager.adapter.TitleAdapter;
import com.zzy.vsa.demo.appcase.filemanager.adapter.base.RecyclerViewAdapter;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.bean.FileBean;
import com.zzy.vsa.demo.appenv.FileType;
import com.zzy.vsa.demo.bean.TitlePath;
import com.zzy.vsa.demo.appcase.filemanager.FileOperationHelper;
import com.zzy.vsa.demo.util.FileOpenUtil;
import com.zzy.vsa.demo.util.FileUtil;
import com.zzy.vsa.demo.appcase.share.ShareUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileManagerActivity extends AppCompatActivity {
    private RecyclerView title_recycler_view ;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<FileBean> beanList = new ArrayList<>();
    private File rootFile ;
    private LinearLayout empty_rel ;
    private String rootPath ;
    private TitleAdapter titleAdapter ;

    private FileOperationHelper fileOperationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        //设置Title
        title_recycler_view = (RecyclerView) findViewById(R.id.title_recycler_view);
        title_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL , false ));
        titleAdapter = new TitleAdapter( FileManagerActivity.this , new ArrayList<TitlePath>() ) ;
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
                FileType fileType = file.getFileType() ;
                if ( fileType == FileType.directory) {
                    getFile(file.getPath());
                    refreshTitleState( file.getName() , file.getPath() );
                }else {
                    FileOpenUtil.openIntent(FileManagerActivity.this, new File( file.getPath() ) );
                }
            }
        });

        fileAdapter.setOnItemLongClickListener(new RecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, final int position) {
                final FileBean fileBean = (FileBean) fileAdapter.getItem( position );

                new AlertDialog.Builder(FileManagerActivity.this).setTitle("选择文件操作")
                        .setItems(new String[]{"分享","删除","重命名","复制到","移动到"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(FileManagerActivity.this, FileChooseActivity.class);
                                switch (which){
                                    case 0:
                                        FileType fileType = fileBean.getFileType() ;
                                        if ( fileType != null && fileType != FileType.directory ){
                                            Intent share = ShareUtil.shareFile( FileManagerActivity.this , fileBean.getPath() );
                                            startActivity(share);
                                        }
                                        break;
                                    case 1:
                                        fileOperationHelper.delete(fileBean.getPath());
                                        beanList.remove(fileBean);
                                        fileAdapter.notifyDataSetChanged();
                                        break;
                                    case 2:
                                        final View viewDialog=(View)getLayoutInflater().inflate(R.layout.layout_edittext_dialog,null);
                                        new AlertDialog.Builder(FileManagerActivity.this).setTitle("请输入新的名称")
                                                .setView(viewDialog)
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        EditText newnameExitText = (EditText)viewDialog.findViewById(R.id.input);
                                                        String newname = newnameExitText.getText().toString();
                                                        File temp = new File(fileBean.getPath());
                                                        File target = new File(temp.getParent()+File.separator+newname);
                                                        temp.renameTo(target);
                                                        fileBean.setName(newname);
                                                        fileBean.setPath(target.getAbsolutePath());
                                                        fileAdapter.notifyDataSetChanged();
                                                    }
                                                }).show();
                                        break;
                                    case 3:
                                        intent.putExtra("operationCode", AppEnv.FILE_COPY);
                                        intent.putExtra("path",fileBean.getPath());
//                                        startActivityForResult(intent, AppEnv.FILE_COPY);
                                        startActivity(intent);
                                        break;
                                    case 4:
                                        intent.putExtra("operationCode", AppEnv.FILE_MOVE);
                                        intent.putExtra("path",fileBean.getPath());
//                                        startActivityForResult(intent, AppEnv.FILE_MOVE);
                                        startActivity(intent);
                                        break;
                                }
                            }
                        }).show();
                return false;
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
        refreshTitleState( "内部存储设备" , rootPath );

    }

    @Override
    protected void onResume(){
        super.onResume();

        getFile(rootPath);

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
//                        FileBean fileBean = new FileBean(f.getName(), f.getAbsolutePath(), FileUtil.getFileType( f ),FileUtil.getFileChildCount( f ), f.length());

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
