package com.zzy.vsa.demo.view.message;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appenv.AppEnv;

import java.util.Date;

public class SendMessageActivity extends AppCompatActivity {


    EditText address,body;

    Button messageintent,sendmessage;

    String targetSMSUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        targetSMSUri = getIntent().getStringExtra("uri");
        if(TextUtils.isEmpty(targetSMSUri)){
            targetSMSUri = "content://sms/sent";
        }

        address = (EditText) findViewById(R.id.address);
        body = (EditText) findViewById(R.id.body);

        messageintent = (Button) findViewById(R.id.messageintent);
        sendmessage = (Button) findViewById(R.id.sendmessage);

        
        SendMessageReceiver sender = new SendMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppEnv.PACKAGE_NAME + ".SEND_SMS");
        registerReceiver(sender,filter);

        messageintent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = body.getText().toString();
                String number = address.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    msg ="hello";
                }
                if (TextUtils.isEmpty(number)){
                    number = "12306";
                }

                ContentValues values = new ContentValues();
                values.put("address", number);
//                values.put("type", 5);
                values.put("read", 0);
                values.put("body", msg);
                values.put("date", System.currentTimeMillis());


                Uri uri = SendMessageActivity.this.getContentResolver().insert(Uri.parse(targetSMSUri), values);
                Toast.makeText(SendMessageActivity.this, targetSMSUri, Toast.LENGTH_SHORT).show();
                Log.i("test","msg:"+ msg + "number:" + number +uri.toString());
            }
        });

        sendmessage.setVisibility(View.GONE);
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = body.getText().toString();
                String number = address.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    msg ="hello";
                }
                if (TextUtils.isEmpty(number)){
                    number = "12306";
                }


                SmsManager sms = SmsManager.getDefault();
                PendingIntent pi = PendingIntent.getBroadcast(SendMessageActivity.this,0,new Intent(),0);
                sms.sendTextMessage("1",number,msg,pi,null);
            }
        });

    }


    class SendMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
