package com.example.x_po.oringin_project;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x_po on 2016/11/29.
 */
public class MyRecyclerViewReplyAdapter extends RecyclerView.Adapter<MyRecyclerViewReplyAdapter.ViewHolder>{


    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_TOPIC = 2;
    private List data;

    String topic_content = null;

    ArrayList<Bitmap> replies_member_header = new ArrayList<Bitmap>();
    ArrayList<String> replies_member_id = new ArrayList();
    ArrayList<String> replies_content = new ArrayList();
    ArrayList<String> replies_member_username=  new ArrayList();
    String topic_username = null;
    private int TYPE_NOTHING = 3;


    public MyRecyclerViewReplyAdapter(List data,
                                      String topic_content,
                                      ArrayList<String> replies_member_id,
                                      ArrayList<String> replies_content,
                                      ArrayList<String> replies_member_username,
                                      ArrayList<Bitmap> replies_member_header,
                                      String topic_username){
        this.data =data ;
        Log.i("MyRecyclerView",replies_content.size()+" ");
        this.topic_content = topic_content;
        this.replies_member_id= replies_member_id;
        this.replies_content=replies_content;
        this.replies_member_username = replies_member_username;
        this.replies_member_header =replies_member_header;
        this.topic_username = topic_username;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("getItemViewType: ",replies_content.size()+" is size  +   position = "+position);
        if (position == 0){
            return  TYPE_TOPIC;
        }
        else if((position-1) == replies_content.size()){
            return TYPE_NOTHING;
        }
        else if((position + 1)  == (data.size()+1)||replies_member_id.isEmpty())
            return TYPE_FOOTER;
        else {
            return TYPE_ITEM;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_itemrecyclerview, parent, false);
            return new ItemViewHolder(view);
        }
        else if(viewType ==1){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_foot,parent,false);

            return new FootHolder(view);
        }
        else if(viewType == 2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_topic,parent,false);
            return new TopicHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_nothing,parent,false);
            return new FootHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            //Log.i("onBindViewHolder: ",replies_content.size()+" "+position);
            if(replies_member_header.size() >= 1) {
                ((ItemViewHolder) holder).tv.setText(replies_content.get(position - 1));
                holder.tv2.setText(replies_member_username.get(position - 1));
                holder.tv1.setText((position) + "L");
                holder.imageView.setImageBitmap(replies_member_header.get(position - 1));
            }
        }
        if(holder instanceof TopicHolder){
            ((TopicHolder) holder).imageView.setImageBitmap(getHeaderImg(topic_username));
            ((TopicHolder) holder).textView1.setText(topic_username);
            ((TopicHolder) holder).textView2.setText(topic_content);
        }
    }

    @Override
    public int getItemCount() {
        //Log.i("getItemCount: ",data.size()+"");
        return data.size() == 0 ? 0 : data.size()+1 ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        TextView tv1;
        TextView tv2;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.header_img);
            tv = (TextView) itemView.findViewById(R.id.content);
            tv1 = (TextView) itemView.findViewById(R.id.reply_count);
            tv2 = (TextView) itemView.findViewById(R.id.memberID);
        }
    }

    private class FootHolder extends ViewHolder {
        TextView tv ;
        public FootHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.testtesttest);
        }
    }

    private class ItemViewHolder extends ViewHolder {
        TextView tv;
        TextView tv1;
        RelativeLayout fl;
        public ItemViewHolder(View itemView) {
            super(itemView);
            fl = (RelativeLayout) itemView.findViewById(R.id.list_item);
            tv = (TextView) itemView.findViewById(R.id.content);
            tv1 = (TextView) itemView.findViewById(R.id.reply_count);
        }
    }

    private class TopicHolder extends ViewHolder {
        ImageView imageView;
        TextView textView1;
        TextView textView2;
        public TopicHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.header_img);
            textView1 = (TextView) view.findViewById(R.id.memberID);
            textView2 = (TextView) view.findViewById(R.id.content);
        }
    }
    private Bitmap getHeaderImg(String id){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.example.x_po.oringin_project/data.db",null);
        analysis analysis = new analysis() ;
        return analysis.getImgHeader(db,id,null);
    }
}
