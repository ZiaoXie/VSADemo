package com.zzy.vsa.demo.appcase.message;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.bean.MessageBean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageHolder extends RecyclerView.ViewHolder {

    private TextView address;
    private TextView date;
    private TextView body;



    public MessageHolder(@NonNull View itemView) {
        super(itemView);

        address = (TextView) itemView.findViewById(R.id.address);
        date = (TextView) itemView.findViewById(R.id.date);
        body = (TextView) itemView.findViewById(R.id.body);
    }

    public void initView(MessageBean messageBean){
        address.setText(messageBean.getAddress());
        date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(messageBean.getDate())));
        body.setText(messageBean.getBody());

    }



}
