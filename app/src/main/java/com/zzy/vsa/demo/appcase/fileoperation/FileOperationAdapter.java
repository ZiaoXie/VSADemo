package com.zzy.vsa.demo.appcase.fileoperation;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.bean.FileClassificationBean;
import com.zzy.vsa.demo.util.FileOpenUtil;
import com.zzy.vsa.demo.view.filemanager.FileManagerActivity;
import com.zzy.vsa.demo.view.fileoperation.FileDownloadActivity;

import java.io.File;
import java.util.ArrayList;

public class FileOperationAdapter extends RecyclerView.Adapter {
    private FileDownloadActivity activity;
    private ArrayList<FileClassificationBean> item = null;
    private String download_path;

    public FileOperationAdapter(FileDownloadActivity activity, ArrayList<FileClassificationBean> item, String download_path) {
        this.activity = activity;
        this.item = item;
        this.download_path = download_path;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(activity).inflate(R.layout.list_item_download, parent, false);
                holder = new DownloadHolder(view);
                break;
            case 1:
                view = LayoutInflater.from(activity).inflate(R.layout.list_item_convert, parent, false);
                holder = new ConvertHolder(view);
                break;
//            default:
//                view = LayoutInflater.from(activity).inflate(R.layout.list_item_download, parent, false);
//                holder = new DownloadHolder(view);
//                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DownloadHolder){
            final DownloadHolder myHolder = (DownloadHolder)holder;

            myHolder.init();
            myHolder.type.setText(item.get(position).getSuffix());
            myHolder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.saveFile(item.get(position).getUrl(), myHolder,position);
                }
            });

            myHolder.downloadview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, FileManagerActivity.class);
                    intent.putExtra("rootPath",download_path);
                    activity.startActivity(intent);
                }
            });

            myHolder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileOpenUtil.openIntent(activity, new File(item.get(position).getPath()));
                }
            });
        } else if( holder instanceof ConvertHolder) {
            final ConvertHolder myHolder = (ConvertHolder)holder;

            myHolder.init();
            myHolder.type.setText(item.get(position).getSuffix());
            myHolder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.saveFile(item.get(position).getUrl(), myHolder,position);
                }
            });

            myHolder.downloadview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, FileManagerActivity.class);
                    intent.putExtra("rootPath",download_path);
                    activity.startActivity(intent);
                }
            });

            myHolder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileOpenUtil.openIntent(activity, new File(item.get(position).getPath()));
                }
            });

            myHolder.file2zip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filepath = item.get(position).getPath();
                    int index = filepath.lastIndexOf(".");
                    String zip_path = filepath.substring(0,index) + ".zip";
                    File file = new File(filepath);
                    File target = new File(zip_path);
                    file.renameTo(target);

                    myHolder.file2zip_result.setText("生成的ZIP文件路径:"+zip_path);
                    myHolder.filepath = zip_path;
                }
            });

            myHolder.open_zip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileOpenUtil.openIntent(activity, new File(myHolder.filepath));
                }
            });

            myHolder.rollback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filepath = item.get(position).getPath();

                    File file = new File(myHolder.filepath);
                    File target = new File(filepath);
                    file.renameTo(target);

                }
            });

        }

    }

    @Override
    public int getItemViewType(int position){

        return item.get(position).getViewtype();
    }

    @Override
    public int getItemCount() {
        return item.size();
    }


}
