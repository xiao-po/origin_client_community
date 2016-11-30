package com.example.x_po.oringin_project;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

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
    private analysis analysis= new analysis();
    String topic_content = null;
    private int Scrolldy=0;
    String topic_username = null ;
    ArrayList<Bitmap> replies_member_header = new ArrayList<Bitmap>();
    ArrayList<String> replies_member_id = new ArrayList();
    ArrayList<String> replies_content = new ArrayList();
    ArrayList<String> replies_member_username=  new ArrayList();
    ArrayList<String> replies_member_headersrc = new ArrayList();
    private List<Map<String, Object>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("查看回复");
        initData();
        get_topic_id();
        Log.i("onCreate: ","initData is success");
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
                        list.clear();
                        getData();
                    }
                });
            }
        });
        initReclerView();
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
        new asyncTask().execute();
        Log.i( "initData: ","已经执行");

    }

    private void getData(){
//        SQLiteDatabase db =db = SQLiteDatabase.openOrCreateDatabase(
//                "/data/data/com.example.x_po.oringin_project/data.db",null);
//        Cursor cursor = db.query("topic", null, "rnode = '"+node+"' and rtopic is not null ", null, null, null, null);
        for(int i= 0;i < 10 ; i++){
            if((replies_content.size()) >= list.size() ) {
                Map map = new HashMap();
                list.add(map);
            }
        }
//        cursor.close();
//        db.close();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyItemRemoved(adapter.getItemCount());
    }
    class asyncTask extends AsyncTask<String,Integer,String> {//继承AsyncTask

        @Override
        protected String doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            publishProgress(0);//将会调用onProgressUpdate(Integer... progress)方法
//            list.clear();
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                    "/data/data/com.example.x_po.oringin_project/data.db",null);
//            com.example.x_po.oringin_project.analysis analysis = new analysis();
//            String src = bundle.getString("rtopic_src").substring(3,9);
//            String count = bundle.getString("rcount");
//            analysis.json_analysis(src,count,db);
//            db.close();
            com.example.x_po.oringin_project.analysis analysis = new analysis();
            String message = analysis.json_analysis(get_topic_id());
            Log.i("doInBackground1: ",message);
            replies_member_id = analysis.GetReplies_member_id();
            replies_member_username= analysis.GetReplies_member_username();
            replies_content = analysis.GetReplies_content();
            replies_member_headersrc = analysis.GetReplies_member_headersrc();
            topic_content = analysis.GetTopic_content();
            for(int i = 0;i < replies_member_id.size();i++){
                if(!db.query("member_header", null, "rmember_id ='"+replies_member_id.get(i)+"'", null, null, null, null).moveToFirst())
                    analysis.db_put_img_header(analysis.img_header_dl(replies_member_headersrc.get(i)), db, replies_member_id.get(i));
                replies_member_header.add(analysis.getImgHeader(db,replies_member_id.get(i).toString(),null));
            }
            db.close();
            //Log.i("doInBackground: ",replies_content.size()+"  "+replies_content.get(0));
            return "ok";
        }


        protected void onPostExecute(String result) {//后台任务执行完之后被调用，在ui线程执行
            initReclerView();
            getData();
        }

        protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行
        }

    }
    public String get_topic_id(){

        bundle = this.getIntent().getExtras();
        int message = bundle.getInt("content_id");
        String message2 = bundle.getString("node");
        topic_username = bundle.getString("member_username");
        String topic_id = analysis.topic_id(message,message2);
        return topic_id;
    }

    public void initReclerView(){
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        adapter = new MyRecyclerViewReplyAdapter(list,topic_content,replies_member_id,replies_content,replies_member_username,replies_member_header,topic_username);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            private static final float HIDE_THRESHOLD = 10;
            private static final float SHOW_THRESHOLD = 70;

            private int mToolbarOffset = 0;
            private boolean mControlsVisible = true;
            private int mToolbarHeight = toolbar.getHeight() ;

            private int temp_onscroll = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mControlsVisible) {
                        if (mToolbarOffset > HIDE_THRESHOLD) {
                            setInvisible();
                        } else {
                            setVisible();
                        }
                    } else {
                        if ((mToolbarHeight - mToolbarOffset) > SHOW_THRESHOLD) {
                            setVisible();
                        } else {
                            setInvisible();
                        }
                    }
                }
                if(Scrolldy <=88){
                    setVisible();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                clipToolbarOffset();
                onMoved(mToolbarOffset);

                if((mToolbarOffset <mToolbarHeight && dy>0) || (mToolbarOffset >0 && dy<0)) {
                    mToolbarOffset += dy;
                }
                Scrolldy +=dy;
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
            private void clipToolbarOffset() {
                if(mToolbarOffset > mToolbarHeight) {
                    mToolbarOffset = mToolbarHeight;
                } else if(mToolbarOffset < 0) {
                    mToolbarOffset = 0;
                }
            }

            private void setVisible() {
                if(mToolbarOffset > 0) {
                    onShow();
                    mToolbarOffset = 0;
                }
                mControlsVisible = true;
            }

            private void setInvisible() {
                if(mToolbarOffset < mToolbarHeight) {
                    onHide();
                    mToolbarOffset = mToolbarHeight;
                }
                mControlsVisible = false;
            }
            public void onMoved(int distance) {
                toolbar.setTranslationY(-distance);
            }

            public void onShow() {
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            public void onHide() {
                toolbar.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
            }

        });
    }
}
