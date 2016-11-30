package com.example.x_po.oringin_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ItemrecyclerviewFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static int column_count;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> list = new ArrayList<>();
    private Map[] maps;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler =new Handler();
    private MyItemrecyclerviewRecyclerViewAdapter adapter;
    private boolean isLoading = false;
    private analysis analysis= new analysis();
    private static String v2ex_src_name[] = {"tech","creative","play","apple","deals","city","qna","hot","all"};
    private int MAX_LIST=10;
    // TODO: Customize parameters

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemrecyclerviewFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemrecyclerviewFragment newInstance(int columnCount) {
        ItemrecyclerviewFragment fragment = new ItemrecyclerviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        column_count = columnCount;
        fragment.setArguments(args);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                        "/data/data/com.example.x_po.oringin_project/data.db",null);
                com.example.x_po.oringin_project.analysis analysis = new analysis();
                //Log.i("doInBackground: ",""+column_count);
                analysis.analysis_init(v2ex_src_name[column_count],db);
                db.close();
            }
        }).start();



        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itemrecyclerview_list, container, false);

        initData();
        initView(view);

        return view;
    }

    private void initView(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
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
                /*new Thread(){
                    public void run() {
                        list.clear();
                        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                                "/data/data/com.example.x_po.oringin_project/data.db",null);
                        com.example.x_po.oringin_project.analysis analysis = new analysis();
                        analysis.analysis_init("tech",db);
                        getData();

                    }
                }.start();*/
                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                    }
                },1500);*/
                new imgTask().execute();
            }
        });
        adapter = new MyItemrecyclerviewRecyclerViewAdapter(list,getArguments().getInt(ARG_COLUMN_COUNT));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
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
                        //return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData(v2ex_src_name[column_count-1]);
                                Log.d("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
        adapter.setOnItemClickListener(new MyItemrecyclerviewRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position,String username) {
                Log.d("Intent", "GO!GO!GO!");
                Bundle bundle = new Bundle();
                bundle.putInt("content_id",position);
                bundle.putString("node",v2ex_src_name[column_count-1]);
                bundle.putString("member_username",username);
                Intent intent = new Intent();
                intent.setClass(getContext(), Reply_Activity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("test", "onItemLongClick");
            }
        });
    }

    private void initData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                new imgTask().execute();
                //getData();
            }
        }, 1500);
    }
    private void getData(String node){
        SQLiteDatabase db =db = SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.example.x_po.oringin_project/data.db",null);
        Cursor cursor = db.query("topic", null, "rnode = '"+node+"' and rtopic is not null ", null, null, null, null);
        for(int i= 0;i < 10 ; i++){
            if(cursor.moveToFirst()&& cursor.getCount() >=list.size()) {
                Map map = new HashMap();
                list.add(map);
            }
        }
        cursor.close();
        db.close();
        Log.i("getData: ",list.size()+"");
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyItemRemoved(adapter.getItemCount());
    }

    class imgTask extends AsyncTask<String,Integer,String> {//继承AsyncTask

        @Override
        protected String doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            publishProgress(0);//将会调用onProgressUpdate(Integer... progress)方法
            list.clear();
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                    "/data/data/com.example.x_po.oringin_project/data.db",null);
            com.example.x_po.oringin_project.analysis analysis = new analysis();
            Log.i("doInBackground: ",""+v2ex_src_name[column_count-1]+ "count = "+(column_count-1));

            analysis.analysis_init(v2ex_src_name[column_count-1],db);
            db.close();
            return "ok";
        }


        protected void onPostExecute(String result) {//后台任务执行完之后被调用，在ui线程执行
            if(result.equals("ok"))
                getData(v2ex_src_name[column_count-1]);
        }

        protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行
        }

    }
}
