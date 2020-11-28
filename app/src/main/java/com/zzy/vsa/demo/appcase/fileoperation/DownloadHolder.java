package com.zzy.vsa.demo.appcase.fileoperation;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.vsa.demo.R;

public class DownloadHolder extends DefaultHolder {

    public TextView type;
    public Button download;
    public TextView downloadresult;
    public Button downloadview, open;


    public DownloadHolder(@NonNull View itemView) {
        super(itemView);
        type = (TextView) itemView.findViewById(R.id.type);
        download = (Button) itemView.findViewById(R.id.download);
        downloadresult = (TextView) itemView.findViewById(R.id.downloadresult);
        downloadview = (Button) itemView.findViewById(R.id.downloadview);
        open = (Button) itemView.findViewById(R.id.open);

    }

    public void init() {
        downloadresult.setVisibility(View.GONE);
        downloadview.setVisibility(View.GONE);
        open.setVisibility(View.GONE);
    }

    public void showDownload(String path){
        downloadresult.setVisibility(View.VISIBLE);
        downloadresult.setText(path);
        downloadview.setVisibility(View.VISIBLE);
        open.setVisibility(View.VISIBLE);
    }


}
