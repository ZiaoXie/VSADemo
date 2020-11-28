package com.zzy.vsa.demo.appcase.fileoperation;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DefaultHolder extends RecyclerView.ViewHolder {
    public DefaultHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract public void showDownload(String path);

}
