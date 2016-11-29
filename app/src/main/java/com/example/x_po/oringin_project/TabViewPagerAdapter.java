package com.example.x_po.oringin_project;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by x_po on 2016/11/21.
 */
public class TabViewPagerAdapter extends FragmentPagerAdapter {
    private String title[] = {"技术","创意","好玩","APPLE","交易","城市","问与答","最热","全部"};
    public TabViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("Fragment","Fragment =" +position+"" );
        return ItemrecyclerviewFragment.newInstance(position);
    }
    
    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public int getItemPosition(Object object) {

        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return  title[position];
    }

}