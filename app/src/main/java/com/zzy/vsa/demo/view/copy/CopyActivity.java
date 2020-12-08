package com.zzy.vsa.demo.view.copy;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.fileoperation.Fileopreration;
import com.zzy.vsa.demo.appcase.fileoperation.agnomen;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.view.filemanager.FileChooseActivity;

public class CopyActivity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

                if (resultCode == Activity.RESULT_OK) {
                    Log.d("copy1", "success");
                    final String selectable = data.getStringExtra("selectedfile");
                    Log.d("copy1", "chushi  "+selectable);
                    String inpath = agnomen.fileAgnomen(selectable);

                    if(inpath == null){
                        Toast.makeText(CopyActivity.this,"Copy failed",Toast.LENGTH_LONG).show();
                    }
                    Fileopreration.multiThreadCopy(selectable, inpath);

                    AlertDialog.Builder dialog_1 = new AlertDialog.Builder(CopyActivity.this);
                    dialog_1.setTitle("path: ");
                    dialog_1.setMessage(inpath);
                    dialog_1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog_1.show();

                }

                else {
                    Toast.makeText(CopyActivity.this,"Copy failed",Toast.LENGTH_LONG).show();
                    Log.d("copy1", "resultCode != Activity.RESULT_OK");
                }
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);

        Button button = findViewById(R.id.copy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CopyActivity.this, FileChooseActivity.class);
                intent.putExtra("operationCode", AppEnv.FILE_SHARE);
                startActivityForResult(intent, 0);
            }
        });
    };
}

