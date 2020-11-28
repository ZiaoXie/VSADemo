package com.zzy.vsa.demo.appcase.fileoperation;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zzy.vsa.demo.R;

public class ConvertHolder extends DefaultHolder {

    public TextView type;
    public Button download;
    public TextView downloadresult;
    public Button downloadview, open;

    public LinearLayout convert;
    public Button file2zip;
    public TextView file2zip_result;
    public Button open_zip;
    public Button rollback;

    public String filepath;

    public ConvertHolder(@NonNull View itemView) {
        super(itemView);
        type = (TextView) itemView.findViewById(R.id.type);
        download = (Button) itemView.findViewById(R.id.download);
        downloadresult = (TextView) itemView.findViewById(R.id.downloadresult);
        downloadview = (Button) itemView.findViewById(R.id.downloadview);
        open = (Button) itemView.findViewById(R.id.open);

        convert = (LinearLayout) itemView.findViewById(R.id.convert);
        file2zip = (Button) itemView.findViewById(R.id.file2zip);
        file2zip_result = (TextView) itemView.findViewById(R.id.file2zip_result);
        open_zip = (Button) itemView.findViewById(R.id.open_zip);
        rollback = (Button) itemView.findViewById(R.id.rollback);

    }

    public void init() {
        downloadresult.setVisibility(View.GONE);
        downloadview.setVisibility(View.GONE);
        open.setVisibility(View.GONE);
        convert.setVisibility(View.GONE);
    }

    public void showDownload(String path){
        downloadresult.setVisibility(View.VISIBLE);
        downloadresult.setText(path);
        downloadview.setVisibility(View.VISIBLE);
        open.setVisibility(View.VISIBLE);

        convert.setVisibility(View.VISIBLE);
    }

}
