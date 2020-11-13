package com.zzy.vsa.demo.view.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.zzy.vsa.demo.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ShowMessageActivity extends AppCompatActivity {


    String titles[] = {"收件箱","已发送","草稿","发件箱","发送失败","待发送列表"};
    String uris[] = {
            "content://sms/inbox",
            "content://sms/sent",
            "content://sms/draft",
            "content://sms/outbox",
            "content://sms/failed",
            "content://sms/queued"
    };

    String defaultSms;

    List<MessageListFragment> fragments = new ArrayList<MessageListFragment>();
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView addmessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);



        if ( Build.VERSION.SDK_INT  >=Build.VERSION_CODES.KITKAT ) {
            defaultSms = Telephony.Sms.getDefaultSmsPackage(ShowMessageActivity.this);
            if(!defaultSms.equals(getPackageName())){
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                Log.e("defaultsms", defaultSms);
                startActivity(intent);
            }
        }

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        addmessage = (TextView) findViewById(R.id.addmessage);

        for(int i=0;i<titles.length;i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
            fragments.add(new MessageListFragment(uris[i]));
        }

        addmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ShowMessageActivity.this).setTitle("选择要添加到的类别").setItems(titles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ShowMessageActivity.this, SendMessageActivity.class);
                        intent.putExtra("uri",uris[which]);
                        startActivity(intent);
                    }
                }).show();
            }
        });

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });

        tabLayout.setupWithViewPager(viewPager);


    }


    public static final String CLASS_SMS_MANAGER = "com.android.internal.telephony.SmsApplication";

    public static final String METHOD_SET_DEFAULT = "setDefaultApplication";

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if ( Build.VERSION.SDK_INT  >=Build.VERSION_CODES.KITKAT ) {
            if(!defaultSms.equals(Telephony.Sms.getDefaultSmsPackage(ShowMessageActivity.this))){
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSms);
                startActivity(intent);
                Toast.makeText(ShowMessageActivity.this,"短信应用恢复为默认",Toast.LENGTH_SHORT).show();
            }


        }

//        if ( Build.VERSION.SDK_INT  >=Build.VERSION_CODES.KITKAT  && Telephony.Sms.getDefaultSmsPackage(ShowMessageActivity.this).equals(getPackageName())) {
//            try {
//                Class<?> smsClass = Class.forName(CLASS_SMS_MANAGER);
//                Method method = smsClass.getMethod(METHOD_SET_DEFAULT, String.class, Context.class);
//                method.invoke(null, defaultSms, this);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }

    }

}
