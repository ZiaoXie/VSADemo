package com.zzy.vsa.demo.appcase.filemanager.adapter.base;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.zzy.vsa.demo.appcase.filemanager.adapter.FileAdapter;

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int position);
    }

    public OnItemClickListener onItemClickListener;
    public OnItemLongClickListener onItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setOnItemLongClickListener( OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition() ;
                    onItemClickListener.onItemClick( v , holder , pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ( onItemLongClickListener != null ){
                    int pos = holder.getLayoutPosition() ;
                    return onItemLongClickListener.onItemLongClick( v , holder , pos  );
                }
                return false;
            }
        });

        onBindViewHolders( holder , position );
    }

    public abstract void onBindViewHolders( RecyclerView.ViewHolder holder, int position ) ;

    public abstract Object getAdapterData() ;

    public abstract Object getItem( int positon );

}
