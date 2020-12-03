package com.zzy.vsa.demo.view.copy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.Native;
import com.zzy.vsa.demo.view.filemanager.FileChooseActivity;



public class CopyActivity extends AppCompatActivity {
    String inapt;
    Button button;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

                if (resultCode == Activity.RESULT_OK) {
                    Log.d("copy1", "success");
                    final String selectable = data.getStringExtra("selectedfile");
                    Log.d("copy1", "chushi"+selectable);
                    class mythread implements Runnable{

                        @Override
                        public void run() {
                            Native.copyFile(selectable,inapt);
                        }
                    }

                    mythread mt1 = new mythread();
                    mythread mt2 = new mythread();
                    mythread mt3 = new mythread();
                    mythread mt4 = new mythread();

                    Thread t1 = new Thread(mt1);
                    Thread t2 = new Thread(mt2);
                    Thread t3 = new Thread(mt3);
                    Thread t4 = new Thread(mt4);

                    t1.run();
                    t2.run();
                    t3.run();
                    t4.run();


                }
                else {
                    Log.d("copy1", "resultCode != Activity.RESULT_OK");
                }
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);

        final EditText edit1 = findViewById(R.id.edit_1);


        button = (Button) findViewById(R.id.copy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inapt = edit1.getText().toString();
                Log.d("copy1", "mu"+inapt);
                Intent intent = new Intent(CopyActivity.this, FileChooseActivity.class);
                intent.putExtra("operationCode", AppEnv.FILE_SHARE);
                startActivityForResult(intent, 0);
            }
        });
    };
}

