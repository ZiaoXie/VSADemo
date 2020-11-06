package com.zzy.vsa.demo.view.message;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.appcase.message.MessageAdapter;
import com.zzy.vsa.demo.bean.MessageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MessageListFragment extends Fragment {

    View view;
    String messageuri;
    RecyclerView messageList;
    TextView none;
    MessageAdapter adapter;
    List<MessageBean> messageBeans;

    String SMS_URI_ALL =  "content://sms/";
    String SMS_URI_INBOX = "content://sms/inbox";
    String SMS_URI_SENT = "content://sms/sent";
    String SMS_URI_DRAFT = "content://sms/draft";
    String SMS_URI_OUTBOX = "content://sms/outbox";
    String SMS_URI_FAILED = "content://sms/failed";
    String SMS_URI_QUEUED = "content://sms/queued";

    /*
    * content://sms/inbox 收件箱
content://sms/sent 已发送
content://sms/draft 草稿
content://sms/outbox 发件箱
content://sms/failed 发送失败
content://sms/queued 待发送列表
    *
    * */

    private MessageListFragment() {
        // Required empty public constructor
    }


    public MessageListFragment(String uri){
        messageuri = uri;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message_list, container, false);

        messageList = (RecyclerView) view.findViewById(R.id.messageList);
        none = (TextView) view.findViewById(R.id.none);

        messageBeans = new ArrayList<MessageBean>();

        initBeans();

        messageList.setLayoutManager(new LinearLayoutManager(getContext()));
        messageList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        adapter = new MessageAdapter(getContext(),messageBeans);
        adapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, final int position) {
                new AlertDialog.Builder(getContext()).setTitle("选择要进行的操作")
                        .setItems(new String[]{"删除","修改"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        String id = messageBeans.get(position).getId();

                                        ContentResolver CR = getContext().getContentResolver();

                                        Cursor c = CR.query(Uri.parse(messageuri), new String[] { "_id", "thread_id" }, "_id=?", new String[]{id}, null);

                                        if(!c.moveToFirst()){
                                            Toast.makeText(getContext(), "短信搜索失败", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        long threadId = c.getLong(c.getColumnIndex("thread_id"));
                                        int result = CR.delete(Uri.parse("content://sms/conversations/" + threadId), "_id=?", new String[]{id});

                                        if(result>0){
                                            Toast.makeText(getContext(), "删除短信成功", Toast.LENGTH_SHORT).show();
                                            messageBeans.remove(position);
                                            adapter.notifyItemRemoved(position);
                                        } else {
                                            Toast.makeText(getContext(), "删除短信失败", Toast.LENGTH_SHORT).show();
                                        }

                                        break;
                                    case 1:
                                        final View viewDialog=(View)getLayoutInflater().inflate(R.layout.layout_edittext_dialog,null);
                                        new AlertDialog.Builder(getContext()).setTitle("请输入要修改的正文内容")
                                                .setView(viewDialog)
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        EditText edit = (EditText)viewDialog.findViewById(R.id.input);
                                                        String newbody = edit.getText().toString();

                                                        ContentValues cv = new ContentValues();
                                                        cv.put("body",newbody);
                                                        int res = getContext().getContentResolver().update(Uri.parse(messageuri), cv, "_id=?", new String[]{messageBeans.get(position).getId()});
                                                        if(res > 0){
                                                            Toast.makeText(getContext(), "修改短信成功", Toast.LENGTH_SHORT).show();
                                                            messageBeans.get(position).setBody(newbody);
                                                            adapter.notifyItemChanged(position);

                                                        } else {
                                                            Toast.makeText(getContext(), "修改短信失败", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }).show();
                                        break;
                                }
                            }
                        }).show();
            }
        });
        messageList.setAdapter(adapter);


        return view;
    }

    private void initBeans(){
        messageBeans.clear();
        Cursor cursor = getContext().getContentResolver().query(Uri.parse(messageuri),null,null,null,null);
        while (cursor.moveToNext()){
            MessageBean messageBean = new MessageBean();
            messageBean.setId(cursor.getString(cursor.getColumnIndex("_id")));
            messageBean.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            messageBean.setBody(cursor.getString(cursor.getColumnIndex("body")));
            messageBean.setDate(cursor.getLong(cursor.getColumnIndex("date")));

            messageBeans.add(messageBean);
        }

        if(messageBeans.isEmpty()){
            messageList.setVisibility(View.GONE);
            none.setVisibility(View.VISIBLE);
        } else {
            messageList.setVisibility(View.VISIBLE);
            none.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        initBeans();
        adapter.notifyDataSetChanged();

    }

}
