package com.example.x_po.oringin_project;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import com.example.x_po.oringin_project.common.SlidingTabLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static com.example.x_po.oringin_project.analysis.INIT_DB_CHECKED_1;

public class MainActivity extends AppCompatActivity {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private SQLiteDatabase db;
    private analysis analysis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        analysis = new analysis();
        db=SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.example.x_po.oringin_project/data.db",null);
        SharedPreferences mySharedPreferences= getSharedPreferences("MainActivity",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor  sharedPreferences= mySharedPreferences.edit();

        if(!mySharedPreferences.getBoolean(INIT_DB_CHECKED_1,false)){
            new Thread(){
                public void run() {
                    analysis.initDatabase(db);
                }
            }.start();

            Log.i("test", "onCreate: 11");
            sharedPreferences.putBoolean(INIT_DB_CHECKED_1,true);
            sharedPreferences.commit();
        }

        if(getSupportFragmentManager() != null){
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new TabViewPagerAdapter(getSupportFragmentManager()));
            slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
            //slidingTabLayout.setDistributeEvenly(true); 是否填充满屏幕的宽度
            slidingTabLayout.setViewPager(viewPager);
        }
    }


}
