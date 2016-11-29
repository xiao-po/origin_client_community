    package com.example.x_po.oringin_project;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by x_po on 2016/11/26.
 */
public class analysis {
    public static final String INIT_DB_CHECKED_1 = "init_db_checked_111";
    private static final String TAG = "test";
    private String title_text[] = new String[50];
    private String request_person[] = new String[50];
    private String last_person[] = new String[50];
    private String count_response[]= new String[50];
    private String header_src[] = new String[50];
    private String src[]=new String[50];
    String topic_content = null;
    ArrayList<String> replies_member_id = new ArrayList();
    ArrayList<String> replies_content = new ArrayList();
    ArrayList<String> replies_member_username=  new ArrayList();
    private static String v2ex_src = "https://v2ex.com/";
    private static String v2ex_src_name[] = {"?tab=tech","?tab=creative","?tab=play","?tab=apple","?tab=deals","?tab=city","?tab=qna","?tab=hot","?tab=all"};
    private static String replies_api = "https://www.v2ex.com/api/replies/show.json?topic_id=";
    private static String topic_api = "https://www.v2ex.com/api/topics/show.json?id=";

    public void analysis(Context context){

        /*if(!mySharedPreferences.getBoolean(INIT_DB_CHECKED_1,false)){
            initDatabase(db,mySharedPreferences);
        }*/

    }

    public void analysis_init(String node,SQLiteDatabase db){
        Document doc = null;

        String v2ex_node_src = v2ex_src+"?tab="+ node;
        Log.i(TAG, "analysis_init: "+v2ex_node_src);
        try {
            doc = Jsoup.connect(v2ex_node_src).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.getElementsByClass("cell item");
        Elements title = element.select(".item_title").select("a");
        Elements username=doc.select(".box").select("td[width$=auto]");
        Elements username_strong;
        Elements count = doc.select(".box").select("tr");
        Elements userheader_srcs = doc.getElementsByClass("cell item").select("img");


        Log.i("analysis", "init going ");
        int i=0;


        for(Element text_word : title){
            if(node.equals("all")&& i >=49)
                break;
            src[i]=text_word.attr("href");
            title_text[i]=text_word.text();
            i++;
        }
        //Log.i(TAG, "analysis_init: "+title_text[0]);
        for(int i1=0;i1<username.size();i1++){
            if(node.equals("all")&& i1 >=49)
                break;
            username_strong=username.get(i1).select("strong");
            request_person[i1]=username_strong.get(0).text();
            if(username_strong.size() == 2)
                last_person[i1]=username_strong.get(1).text();
            else
                last_person[i1]=null;
            if(count.get(i1).select("td").size() == 4)
                count_response[i1]=count.get(i1).select("td").get(3).text();
            else
                count_response[i1]=null;
        }
        i=0;
        // Log.i(TAG, "analysis_init: "+userheader_srcs.toString());
        //db_put_img_header(img_header_dl("//cdn.v2ex.co/avatar/c4ca/4238/1_normal.png?m=1466415272"), db, "livid");
        for (Element userheader_src : userheader_srcs){
            if(node.equals("all")&& i >=49)
                break;
            String[] selerctargs = {"rimg_header"};
            //Log.i(TAG, "analysis_init: "+userheader_src.attr("src").toString()+" member = "+request_person[i]);
            header_src[i] =userheader_src.attr("src");
            if(!db.query("member_header", selerctargs, "rmember_id ='"+request_person[i]+"'", null, null, null, null).moveToFirst())
                db_put_img_header(img_header_dl(header_src[i]), db, request_person[i]);
            i++;
        }
        //Log.i(TAG, "title: "+title.get(0).text());
        update_topic(node,db);

        /*String text  =word;
        str = text;
        Log.d("test", SUCCESS);
        Message msg=new Message();
        msg.what= 1;
        handler.sendMessage(msg);
        */
    }



    public void initDatabase(SQLiteDatabase db) {
        db= SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.example.x_po.oringin_project/data.db",null);
        String stu_table="create table topic(" +
                "uid integer primary key autoincrement," +
                "rnode text," +//节点信息
                "rtopic_src text," +//帖子src
                "rtopic text," +//帖子标题
                "rmember text," +//发帖人id
                "rlast_member text," +//最后一个回帖人
                "rcount text," +//回复数量
                "rheader_src text )";//头像地址

        db.execSQL(stu_table);
        /*stu_table="create table replies(" +
                "uid integer primary key autoincrement," +
                "rtitlesrc text," +//帖子src
                "rreplies_text text," + //回帖内容
                "rreplies_member_id text," +//回帖人id
                "rreplies_member_username text,"+//回帖人名字
                "rpagecount int)";//页数
        db.execSQL(stu_table);
        stu_table="create table topic_content(" +
                "uid integer primary key autoincrement," +
                "rsrc text," +//帖子src
                "rtopic_content text)";//帖子内容
        db.execSQL(stu_table);*/
        stu_table="create table member_header(" +
                "uid integer primary key autoincrement," +
                "rmember_id text," +//帖子src
                "rimg_header blob)";//帖子内容
        db.execSQL(stu_table);
        Log.i("initDatabase", "initDatabase: success");
        db.close();
    }
    //json解析
    public String json_analysis(String src,String rcount,SQLiteDatabase db){
        String json_str=null;
        Log.i(TAG, "json_analysis: "+replies_api + src);
        try {
            URL url = new URL(replies_api + src) ;
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5*1000);
            int code = conn.getResponseCode();
            if(code ==200) {
                InputStream is = conn.getInputStream();
                json_str = readStream(is);
            }
            JSONTokener jsonParser = new JSONTokener(json_str);
            JSONArray json = (JSONArray) jsonParser.nextValue();
            for(int index = 0;index < json.length();index++){
                JSONObject json1 = (JSONObject) json.get(index);
                JSONObject member  =json1.getJSONObject("member");
                replies_member_username.add(member.getString("username"));
                replies_member_id.add(member.getString("id"));
                replies_content.add(json1.getString("content"));
            }
            insert_replies(src,Integer.parseInt(rcount),db);

        } catch (IOException e) {
            e.printStackTrace();
            json_str=null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            URL url = new URL(topic_api + src) ;
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5*1000);
            int code = conn.getResponseCode();
            if(code ==200) {
                InputStream is = conn.getInputStream();
                json_str = readStream(is);
            }
            JSONTokener jsonParser = new JSONTokener(json_str);
            JSONArray json = (JSONArray) jsonParser.nextValue();
            JSONObject json2 = (JSONObject) json.get(0);
            topic_content = json2.getString("content");
            /*String db_content_src[] = { "rsrc = "+src };
            Cursor cursor= db.query("topic_content",db_content_src,null,null,null,null,null);
            if(cursor.moveToFirst()){
                ContentValues contentValues = new ContentValues();
                contentValues.put("rsrc",src);
                contentValues.put("rtopic_content",topic_content);
                db.insert("topic_content",null,contentValues);
            }*/
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return topic_content;

    }
    //流内容转换
    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while( (len = is.read(buffer))!=-1){
                baos.write(buffer, 0, len);
            }
            is.close();
            String temptext = new String(baos.toByteArray());
            if(temptext.contains("charset=gb2312")){//解析meta标签
                return new String(baos.toByteArray(),"gb2312");
            }else{
                return new String(baos.toByteArray(),"utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //更新topic表
    private void update_topic(String node,SQLiteDatabase db){
        Log.i("update_topic", "update_topic  start" + "  node = "+node );
        ContentValues cValue = new ContentValues();
        Cursor cursor;
        String[] whereArgs = {"uid"};
        for(int i = 0;i<50;i++){
            int a = 0;
            a = (int)i/10;
            cValue.put("rnode",node);
            cValue.put("rtopic_src",src[i]);
            cValue.put("rtopic",title_text[i]);
            cValue.put("rmember",request_person[i]);
            cValue.put("rlast_member",last_person[i]);
            cValue.put("rcount",count_response[i]);
            cValue.put("rheader_src",header_src[i]);

            //Log.i(TAG, "update_topic: "+src[i]+"   "+header_src[i]);
            cursor = db.query("topic",whereArgs,"rnode ='"+node+"'",null,null,null,null);
            if(cursor.moveToFirst()&&cursor.getCount() > i) {
                cursor.move(i);

                //Log.i(TAG, "update_topic: "+cursor.getString(0).toString()+"  db_count = "+cursor.getCount()+" title_text = "+title_text[i] +"  i = "+i);
                db.update("topic", cValue, "rnode ='" + node + "' and uid = '"+cursor.getString(0).toString()+"'", null);
            }
            else {
                //Log.i(TAG, "update_topic: insert" +"  i  ="+i);
                db.insert("topic", null, cValue);
            }//Log.i(TAG, "update_topic: "+"  db_count = "+cursor.getCount());
           /*if(OnUpdateQuery(node,db)) {
                db.update("topic", cValue, "uid = "+i+" rnode = "+node,null);
                cValue.clear();
            }else{
                db.insert("topic",null,cValue);
                cValue.clear();
            }*/
        }
        Log.i(TAG, "rtopic_src: "+src[0] );


        /*Log.i("update_topic", "update_topic check");
        if(OnUpdateQuery(node,db)) {
            Log.i("update_topic", "update start");
            db.update("topic", cValue, "uid = ?",null);
        }else{
            Log.i("update_topic", "insert start");
        }*/

    }
    //判断是否更新topic还是插入
    private Boolean OnUpdateQuery(String node,SQLiteDatabase db){
        String[] selectionArgs = { "rnode" };
        String db_yuju="select rnode from topic where rnode = " +node;
        Cursor cursor = db.query ("topic",selectionArgs,null,null,null,null,null);
        if(cursor.getCount()>40 )
            return true;
        else
            return false;
    }
    //获取回复数量
    public int query_rcount(String selectionarg,SQLiteDatabase db){
        String[] selectionArgs = { "rtopic_src = "+selectionarg };
        int result=0;
        Cursor cursor = db.query("topic",selectionArgs,null,null,null,null,null);
        if(cursor.moveToFirst()){
            String result_query = cursor.getString(6);
            result = Integer.parseInt(result_query);
        }
        return result;
    }
    //取出topic的API的id
    private String srcID(String selectionarg,SQLiteDatabase db){
        String[] selectionArgs = { selectionarg };
        String result=null;
        Cursor cursor = db.query("topic",selectionArgs,null,null,null,null,null);
        if(cursor.moveToFirst()){
            String result_query = cursor.getString(2);
            result = result_query;
        }
        return src_into_srcID(result);
    }
    private String src_into_srcID(String src){
        return src.substring(3,9);
    }
    //插入回复表
    private void insert_replies(String srcID,int topic_rcount,SQLiteDatabase db){
        ContentValues cValue = new ContentValues();
        for(int i = topic_rcount;i<replies_content.size();i++){
            int a = 0;
            a = (int)i/10;
            cValue.put("rtitlesrc",srcID);
            cValue.put("rreplies_text",replies_content.get(i));
            cValue.put("rreplies_member_id",replies_member_id.get(i));
            cValue.put("rreplies_member_username",replies_member_username.get(i));
            cValue.put("rpagecount",a);

            db.insert("replies",null,cValue);
            cValue.clear();
        }
        replies_content.clear();
        replies_member_id.clear();
        replies_member_username.clear();
    }
    public void query_test(SQLiteDatabase db){
        Cursor cursor = db.query("replies",null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            for(int i = 0 ; i<cursor.getCount() ;i++){


                Log.i("query_test"," rnode="+cursor.getString(1)
                        +"  rtopic_src="+cursor.getString(2)
                        +"  i ="+i
                );
                cursor.move(1);
            }
        }

    }
    public void delete_db(SQLiteDatabase db){
        db.delete("topic",null,null);
        //db.delete("replies",null,null);
        //db.delete("topic_content",null,null);
        //db.delete("member_header",null,null);
    }
    public Bitmap img_header_dl(String src_head){
        Log.i(TAG, "img_header_dl: downLoding"+ "  and src = "+src_head);
        Bitmap bitmap = null;
        String urltest = "http://"+src_head.substring(2);
        try {
            URL url = new URL(urltest);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10*1000);
            conn.setDoInput(true);
            int code = conn.getResponseCode();
            Log.i(TAG, "code: "+code);
            if(code ==200) {
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IOerror ");
        }

        return bitmap;
    }

    public void db_put_img_header(Bitmap bitmap,SQLiteDatabase db,String id){
        ContentValues values = new ContentValues();
        String[] selerctargs = {"rimg_header"};
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        values.put("rmember_id",id);
        values.put("rimg_header",os.toByteArray());
        db.insert("member_header",null,values);
        values.clear();

    }
    public Bitmap getImgHeader(SQLiteDatabase db,String id,String headsrc){
        byte[] header = null;
        String[] selerctargs = {"rimg_header"};
        Bitmap bitmap = null;
        try {
            Cursor cursor = db.query("member_header", selerctargs, "rmember_id = '" + id+"'", null, null, null, null);
            if (cursor.moveToFirst()) {
                header = cursor.getBlob(cursor.getColumnIndex("rimg_header"));
                cursor.close();
                if (header != null) {
                    return BitmapFactory.decodeByteArray(header, 0, header.length);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return  bitmap;
    }
    public Map display_query(SQLiteDatabase db, String node,int position){
        String[] selerctargs = {"rtopic_src","rtopic","rmember","rlast_member","rcount","rheader_src"};
        Map map= new HashMap();
        Log.i(TAG, "display_query: "+node);
        Cursor cursor = db.query("topic", selerctargs, "rnode = '"+node+"'", null, null, null, null);

        if(cursor.moveToFirst()){
            cursor.move(position);
                map.put("rtopic_src",
                        cursor.getString(0));
                map.put("rtopic",
                        cursor.getString(1));
                map.put("rmember",
                        cursor.getString(2));
                map.put("rlast_member",
                        cursor.getString(3));
                if (!cursor.getString(4).isEmpty())
                    map.put("rcount",
                            cursor.getString(4));
                else
                    map.put("rcount",0);

                map.put("rheader_src",
                        cursor.getString(5));
            Log.i(TAG, "display_query: "+cursor.getString(1)+" rmember = "+map.get("rmember"));
                map.put("rheader",getImgHeader(db,map.get("rmember").toString(),null));


        }
        cursor.close();
        return map;

    }

}

    /*"rnode text," +//节点信息
            "rtopic_src text," +//帖子src
            "rtopic text," +//帖子标题
            "rmember text," +//发帖人id
            "rlast_member text," +//最后一个回帖人
            "rcount text," +//回复数量
            "rpagecount int)";//页数*/