package com.example.x_po.oringin_project;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by x_po on 2016/11/29.
 */
public class MyRecyclerViewReplyAdapter extends RecyclerView.Adapter<MyRecyclerViewReplyAdapter.ViewHolder>{


    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 0;
    private List data;



    public MyRecyclerViewReplyAdapter(List data){
        this.data =data ;
    }

    @Override
    public int getItemViewType(int position) {
        if((position + 1)  == data.size())
            return TYPE_FOOTER;
        else {
            return TYPE_ITEM;
        }
    }

    @Override
    public MyRecyclerViewReplyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_itemrecyclerview, parent, false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_foot,parent,false);

            return new FootViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final MyRecyclerViewReplyAdapter.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            holder.tv.setText(position+"");
            holder.tv1.setText("");
            holder.tv1.setBackgroundColor(Color.argb(0,0,0,0));
        }
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        TextView tv1;
        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.content);
            tv1 = (TextView) itemView.findViewById(R.id.reply_count);
        }
    }

    private class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {

            super(view);
        }
    }
}
