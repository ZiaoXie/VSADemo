package com.zzy.vsa.demo.view.gateway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zzy.vsa.demo.R;

import java.io.IOException;
import java.net.Socket;

public class GateWayActivity extends AppCompatActivity {

    EditText address;
    EditText port;
    Button connect;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_way);

        address = (EditText) findViewById(R.id.address);
        port = (EditText) findViewById(R.id.port);
        connect = (Button) findViewById(R.id.connect);
        result = (TextView) findViewById(R.id.result);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });


            }
        });


    }

    class MyTask extends AsyncTask{

        Socket socket;
        String res = "";

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                socket = new Socket(address.getEditableText().toString(), Integer.parseInt(port.getEditableText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
                res = "连接失败\n";
                for(StackTraceElement ste : e.getStackTrace()){
                    res = res + ste.toString() +'\n';
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            if (!TextUtils.isEmpty(res) || socket == null){
                result.setText(res);
                return;
            }

            if(socket.isConnected()){
                result.setText("连接成功");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




        }
    }
}
