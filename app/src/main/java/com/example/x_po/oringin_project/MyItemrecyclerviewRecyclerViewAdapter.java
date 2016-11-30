package com.example.x_po.oringin_project;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x_po.oringin_project.dummy.DummyContent.DummyItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LoggingMXBean;


public class MyItemrecyclerviewRecyclerViewAdapter extends RecyclerView.Adapter<MyItemrecyclerviewRecyclerViewAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NOTHING = 2;
    private final List data;
    private int mposition;
    private Map map = new HashMap();
    private analysis analysis_test;
    private SQLiteDatabase db;
    private String node ;
    private static String v2ex_src_name[] = {"tech","creative","play","apple","deals","city","qna","hot","all"};
    private int m1position;
    private int x=0;
    private int MAX_LIST;
    public interface OnItemClickListener {
        void onItemClick(View view, int position,String member_username);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public MyItemrecyclerviewRecyclerViewAdapter(List data,int position){
        this.data = data;
        this.mposition = position;
        analysis_test= new analysis();
        db=SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.example.x_po.oringin_project/data.db",null);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view =LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_itemrecyclerview, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            Log.d("test","see");
            View view =LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item_foot, parent, false);
            return new FootViewHolder(view);
        }else if(viewType == TYPE_NOTHING){
            Log.i("view type", "onCreateViewHolder: nothing");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item_nothing, parent, false);
            return new NothingViewHolder(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder){
            if (onItemClickListener != null) {

                map = analysis_test.display_query(db,v2ex_src_name[mposition],position);
                final String username = (String) map.get("rmember");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position,username);
                    }
                });
                m1position = position;
               // holder.mContentView.setText("fragment" + position+"   pagerview "+mposition + " topic: "+map.get("rtopic"));
                holder.mContentView.setText(""+map.get("rtopic"));
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
                ((ItemViewHolder) holder).imageView.setImageBitmap((Bitmap) map.get("rheader"));;
                ((ItemViewHolder) holder).tv1.setText(map.get("rmember")+"");
                //if(map.get("rount") == null)
                    ((ItemViewHolder) holder).tv2.setText(""+map.get("rcount"));
                //else
                //    ((ItemViewHolder) holder).tv2.setText(""+0);
                //new imgTask().execute();
            }

        }
    }
    @Override
    public int getItemViewType(int position) {
        MAX_LIST= db.query("topic", null, "rnode = '"+v2ex_src_name[mposition]+"' and rtopic is not null ", null, null, null, null).getCount();
        if(position+1 > MAX_LIST){
            return TYPE_NOTHING;
        }
        else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else{
            return TYPE_ITEM;
        }
    }
    @Override
    public int getItemCount() {
        if(data.size() >= MAX_LIST ){
            return MAX_LIST+1;
        }
        return data.size() == 0 ? 0 : data.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }
    private class ItemViewHolder extends ViewHolder {
        TextView tv1;
        TextView tv;
        TextView tv2;
        ImageView imageView;
        public ItemViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.content);
            imageView = (ImageView) view.findViewById(R.id.header_img);
            tv1 = (TextView) view.findViewById(R.id.memberID);
            tv2 = (TextView) view.findViewById(R.id.reply_count);
        }
    }

    private class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);

        }
    }
    /*class imgTask extends AsyncTask<String,Integer,Bitmap> {//继承AsyncTask

        @Override
        protected Bitmap doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            publishProgress(0);//将会调用onProgressUpdate(Integer... progress)方法
            analysis_test = new analysis();
            Bitmap bitmap = null;
            //bitmap = analysis_test.getImgHeader(db, map.get("rmember").toString(), map.get("rheader_src").toString());
            //bitmap = analysis_test.getImgHeader(db, "livid",null);
            return bitmap;
        }


        protected void onPostExecute(Bitmap result) {//后台任务执行完之后被调用，在ui线程执行
            if(result != null) {
                mImageView.setImageBitmap(result);
            }else {
            }
        }

        protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行
            mImageView.setImageBitmap(null);
        }

    }*/

    private class NothingViewHolder extends ViewHolder {
        TextView tv;
        public NothingViewHolder(View view) {
            super(view);
            tv= (TextView) view.findViewById(R.id.reloding);
        }
    }
}
