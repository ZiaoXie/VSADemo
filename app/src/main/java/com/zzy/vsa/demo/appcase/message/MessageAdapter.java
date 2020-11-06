package com.zzy.vsa.demo.appcase.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.vsa.demo.R;
import com.zzy.vsa.demo.bean.MessageBean;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int position);
    }

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setOnItemLongClickListener( OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    private Context context;
    private List<MessageBean> messageBeans;

    private LayoutInflater mLayoutInflater ;

    public MessageAdapter(Context context, List<MessageBean> list) {
        this.context = context;
        this.messageBeans = list;
        mLayoutInflater = LayoutInflater.from( context ) ;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        view = mLayoutInflater.inflate(R.layout.list_item_message, parent, false) ;
        return new MessageHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MessageHolder messageHolder = (MessageHolder) holder;


        messageHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition() ;
                    onItemClickListener.onItemClick( v , holder , pos);
                }
            }
        });

        messageHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ( onItemLongClickListener != null ){
                    int pos = holder.getLayoutPosition() ;
                    return onItemLongClickListener.onItemLongClick( v , holder , pos  );
                }
                return false;
            }
        });

        messageHolder.initView(messageBeans.get(position));

    }

    @Override
    public int getItemCount() {
        return messageBeans.size();
    }
}
