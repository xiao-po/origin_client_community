package com.example.x_po.oringin_project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reply_Activity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyRecyclerViewReplyAdapter adapter;
    private boolean isLoading = false;
    private Bundle bundle =new Bundle();
    public Handler handler = new Handler();

    private List<Map<String, Object>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("查看回复");
        initData();
        Log.i("onCreate: ","initData is success");
        bundle = this.getIntent().getExtras();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_reply);
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView_reply);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                });
            }
        });

        Log.i("onCreate: ","swipeRefreshLayout is success");
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        adapter = new MyRecyclerViewReplyAdapter(list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {


                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    Log.i("onScrolled: ","isRdfreshing = " +isRefreshing);
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        Log.d("test", "???");
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                                Log.d("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
//        Log.i("onCreate: ","test test test test tset ");
//        adapter.setOnItemClickListener(new MyRecyclerViewReplyAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Log.d("test", "onItemClick");
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//                Log.d("test", "onItemLongClick");
//            }
//        });
    }

    private void initData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);
    }

    private void getData(){
//        SQLiteDatabase db =db = SQLiteDatabase.openOrCreateDatabase(
//                "/data/data/com.example.x_po.oringin_project/data.db",null);
//        Cursor cursor = db.query("topic", null, "rnode = '"+node+"' and rtopic is not null ", null, null, null, null);
        for(int i= 0;i < 10 ; i++){
//            if(cursor.moveToFirst()&& cursor.getCount() >=list.size()) {
                Map map = new HashMap();
                list.add(map);
//            }
        }
//        cursor.close();
//        db.close();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyItemRemoved(adapter.getItemCount());
    }
    class imgTask extends AsyncTask<String,Integer,String> {//继承AsyncTask

        @Override
        protected String doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            publishProgress(0);//将会调用onProgressUpdate(Integer... progress)方法
//            list.clear();
//            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
//                    "/data/data/com.example.x_po.oringin_project/data.db",null);
//            com.example.x_po.oringin_project.analysis analysis = new analysis();
//            String src = bundle.getString("rtopic_src").substring(3,9);
//            String count = bundle.getString("rcount");
//            analysis.json_analysis(src,count,db);
//            db.close();
            return "ok";
        }


        protected void onPostExecute(String result) {//后台任务执行完之后被调用，在ui线程执行
            
                getData();
        }

        protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行
        }

    }
}
