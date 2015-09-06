package com.example.dean.ibuytogether;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
    private ImageButton search_button,cloth_button,cosmetic_button,bag_button;
    private Handler handler;
    private EditText editText;
    private Spinner spinner;
    private ParseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView push = (TextView)findViewById(R.id.push);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        TextView recommend = (TextView)findViewById(R.id.recommend);
        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread recommend_thread = new recommend_thread();
                TableLayout case_list = (TableLayout)findViewById(R.id.user_list);
                case_list.removeAllViews();
                TextView loading = new TextView(MainActivity.this);
                loading.setText("載入中  請稍候...");
                loading.setGravity(Gravity.CENTER);
                loading.setPadding(0,30,0,0);
                case_list.addView(loading);
                recommend_thread.start();
            }
        });
        TextView login = (TextView)findViewById(R.id.login);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            actionBar.setTitle("iBuytogether-"+currentUser.getUsername());
            login.setText("登出");
            ParsePush.subscribeInBackground(currentUser.getUsername(), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                    } else {
                        Log.e("com.parse.push", "failed to subscribe for push", e);
                    }
                }
            });

            push.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("push", "安安");
                    ParseQuery query = ParseInstallation.getQuery();
                    query.whereEqualTo("deviceType", "android");
                    ParsePush androidPush = new ParsePush();
                    androidPush.setMessage("推薦您七個可能有興趣的團");
                    androidPush.setQuery(query);
                    androidPush.sendInBackground();
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser.logOut();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            Thread recommend_thread = new recommend_thread();
            TableLayout case_list = (TableLayout)findViewById(R.id.user_list);
            TextView loading = new TextView(MainActivity.this);
            loading.setText("載入中  請稍候...");
            loading.setGravity(Gravity.CENTER);
            loading.setPadding(0,30,0,0);
            case_list.addView(loading);
            recommend_thread.start();

        }else{
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        spinner = (Spinner)findViewById(R.id.spinner);
        String[] category = {"更多","下半身", "日常用品", "寵物", "票券", "食物", "鞋襪", "配件"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,category);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    Thread thread = new typeThread(4);
                    thread.start();
                }
                if(position==2){
                    Thread thread = new typeThread(2);
                    thread.start();
                }
                if(position==3){
                    Thread thread = new typeThread(10);
                    thread.start();
                }
                if(position==4){
                    Thread thread = new typeThread(11);
                    thread.start();
                }
                if(position==5){
                    Thread thread = new typeThread(9);
                    thread.start();
                }
                if(position==6){
                    Thread thread = new typeThread(5);
                    thread.start();
                }
                if(position==7){
                    Thread thread = new typeThread(7);
                    thread.start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cloth_button = (ImageButton)findViewById(R.id.cloth_button);
        cloth_button.setOnClickListener(new getClass(3));
        cosmetic_button = (ImageButton)findViewById(R.id.cosmetic_button);
        cosmetic_button.setOnClickListener(new getClass(8));
        bag_button = (ImageButton)findViewById(R.id.bag_button);
        bag_button.setOnClickListener(new getClass(6));
        search_button = (ImageButton)findViewById(R.id.searchButton);
        search_button.setOnClickListener(getDBRecord);
        editText =(EditText)findViewById(R.id.edit_text);
        handler = new myHandler();



    }
    class searchThread extends Thread {
        @Override
        public void run() {
            String input = editText.getText().toString();
            String fn = "tfidf.php";
            String fn1 = "getImage.php";
            try {
                String result = DBconnector.executeQuery(input,fn);
                System.out.println(result);
                JSONArray jsonArray = new JSONArray(result);
                String[] output;
                output = new String[15];
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData3 = jsonArray.getJSONObject(i);
                    output[i] =jsonData3.getString("title");
                    output[i+5] =jsonData3.getString("content");
                    String result1 = DBconnector.executeQuery(jsonData3.getString("cid"),fn1);
                    System.out.println(result1);
                    JSONArray jsonArray1 = new JSONArray(result1);
                    JSONObject jsonData4 = jsonArray1.getJSONObject(0);
                    output[i+10] = jsonData4.getString("imageUrl");
                }

                Message msg = handler.obtainMessage();
                msg.obj= output;
                msg.arg1 = 1;
                handler.sendMessage(msg);


            } catch(Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    };
    class recommend_thread extends Thread {
        @Override
        public void run() {
            String fn = "userRecommend.php";
            String fn1 = "getImage.php";
            try {
                String result = DBconnector.executeQuery(currentUser.getUsername(),fn);
                System.out.println(result);
                JSONArray jsonArray = new JSONArray(result);
                String[] output;
                output = new String[21];
                Log.e("dean","長度:"+jsonArray.length());
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData3 = jsonArray.getJSONObject(i);
                    output[i] =jsonData3.getString("title");
                    output[i+7] =jsonData3.getString("content");
                    String result1 = DBconnector.executeQuery(jsonData3.getString("cid"),fn1);
                    JSONArray jsonArray1 = new JSONArray(result1);
                    JSONObject jsonData4 = jsonArray1.getJSONObject(0);
                    output[i+14] = jsonData4.getString("imageUrl");
                }

                Message msg = handler.obtainMessage();
                msg.obj= output;
                msg.arg1 = 3;
                handler.sendMessage(msg);


            } catch(Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    }
    class myHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==1){
                String[] output;
                output = (String[]) msg.obj;
                String s1 = output[0];
                String s2 = output[1];
                String s3 = output[2];
                String s4 = output[3];
                String s5 = output[4];
                TableLayout user_list = (TableLayout)findViewById(R.id.user_list);
                user_list.removeAllViews();
                user_list.setStretchAllColumns(true);

                TableRow.LayoutParams view_layout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView title = new TextView(MainActivity.this);
                TextView title1 = new TextView(MainActivity.this);
                TextView title2 = new TextView(MainActivity.this);
                TextView title3 = new TextView(MainActivity.this);
                TextView title4 = new TextView(MainActivity.this);
                if(!output[10].equals("null")){
                    title.setText(s1+"(有圖)");
                }else{
                    title.setText(s1+"(沒圖)");
                }

                title.setTextSize(14);
                title.setPadding(0,18,0,18);
                title.setLayoutParams(view_layout);
                user_list.addView(title);
                title.setOnClickListener(new text_listener(output[5],output[10]));
               // System.out.println("3");

                if(!output[11].equals("null")){
                    title1.setText(s2+"(有圖)");
                }else{
                    title1.setText(s2+"(沒圖)");
                }
                title1.setTextSize(14);
                title1.setPadding(0,18,0,18);
                title1.setLayoutParams(view_layout);
                user_list.addView(title1);
                title1.setOnClickListener(new text_listener(output[6],output[11]));
              //  System.out.println("4");

                if(!output[12].equals("null")){
                    title2.setText(s3+"(有圖)");
                }else{
                    title2.setText(s3+"(沒圖)");
                }
                title2.setTextSize(14);
                title2.setPadding(0,18,0,18);
                title2.setLayoutParams(view_layout);
                user_list.addView(title2);
                title2.setOnClickListener(new text_listener(output[7],output[12]));
               // System.out.println("5");

                if(!output[13].equals("null")){
                    title3.setText(s4+"(有圖)");
                }else{
                    title3.setText(s4+"(沒圖)");
                }
                title3.setTextSize(14);
                title3.setPadding(0,18,0,18);
                title3.setLayoutParams(view_layout);
                user_list.addView(title3);
                title3.setOnClickListener(new text_listener(output[8],output[13]));
                //System.out.println("6");

                if(!output[14].equals("null")){
                    title4.setText(s5+"(有圖)");
                }else{
                    title4.setText(s5+"(沒圖)");
                }
                title4.setTextSize(14);
                title4.setPadding(0,18,0,18);
                title4.setLayoutParams(view_layout);
                user_list.addView(title4);
                title4.setOnClickListener(new text_listener(output[9],output[14]));
                //System.out.println("7");
            }
            if(msg.arg1==2){
                System.out.println("2");
                String[] output;
                output = (String[]) msg.obj;
                String s1 = output[0];
                String s2 = output[1];
                String s3 = output[2];
                String s4 = output[3];
                String s5 = output[4];
                TableLayout user_list = (TableLayout)findViewById(R.id.user_list);
                user_list.removeAllViews();
                user_list.setStretchAllColumns(true);
                TableRow.LayoutParams view_layout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView title = new TextView(MainActivity.this);
                TextView title1 = new TextView(MainActivity.this);
                TextView title2 = new TextView(MainActivity.this);
                TextView title3 = new TextView(MainActivity.this);
                TextView title4 = new TextView(MainActivity.this);

                if(!output[10].equals("null")){
                    title.setText(s1+"(有圖)");
                }else{
                    title.setText(s1+"(沒圖)");
                }
                title.setTextSize(14);
                title.setPadding(0,18,0,18);
                title.setLayoutParams(view_layout);
                user_list.addView(title);
                title.setOnClickListener(new text_listener(output[5],output[10]));
                //System.out.println("3");

                if(!output[11].equals("null")){
                    title1.setText(s2+"(有圖)");
                }else{
                    title1.setText(s2+"(沒圖)");
                }
                title1.setTextSize(14);
                title1.setPadding(0,18,0,18);
                title1.setLayoutParams(view_layout);
                user_list.addView(title1);
                title1.setOnClickListener(new text_listener(output[6],output[11]));
                //System.out.println("4");

                if(!output[12].equals("null")){
                    title2.setText(s3+"(有圖)");
                }else{
                    title2.setText(s3+"(沒圖)");
                }
                title2.setTextSize(14);
                title2.setPadding(0,18,0,18);
                title2.setLayoutParams(view_layout);
                user_list.addView(title2);
                title2.setOnClickListener(new text_listener(output[7],output[12]));
                //System.out.println("5");

                if(!output[13].equals("null")){
                    title3.setText(s4+"(有圖)");
                }else{
                    title3.setText(s4+"(沒圖)");
                }
                title3.setTextSize(14);
                title3.setPadding(0,18,0,18);
                title3.setLayoutParams(view_layout);
                user_list.addView(title3);
                title3.setOnClickListener(new text_listener(output[8],output[13]));
                //System.out.println("6");

                if(!output[14].equals("null")){
                    title4.setText(s5+"(有圖)");
                }else{
                    title4.setText(s5+"(沒圖)");
                }
                title4.setTextSize(14);
                title4.setPadding(0,18,0,18);
                title4.setLayoutParams(view_layout);
                user_list.addView(title4);
                title4.setOnClickListener(new text_listener(output[9],output[14]));
                //System.out.println("7");
            }
            if(msg.arg1==3){
                String[] output;
                output = (String[]) msg.obj;
                String s1 = output[0];
                String s2 = output[1];
                String s3 = output[2];
                String s4 = output[3];
                String s5 = output[4];
                String s6 = output[5];
                String s7 = output[6];
                TableLayout user_list = (TableLayout)findViewById(R.id.user_list);
                user_list.removeAllViews();
                user_list.setStretchAllColumns(true);

                TableRow.LayoutParams view_layout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView title = new TextView(MainActivity.this);
                TextView title1 = new TextView(MainActivity.this);
                TextView title2 = new TextView(MainActivity.this);
                TextView title3 = new TextView(MainActivity.this);
                TextView title4 = new TextView(MainActivity.this);
                TextView title5 = new TextView(MainActivity.this);
                TextView title6 = new TextView(MainActivity.this);

                if(!output[14].equals("null")){
                    title.setText(s1+"(有圖)");
                }else{
                    title.setText(s1+"(沒圖)");
                }
                title.setTextSize(14);
                title.setPadding(0,18,0,18);
                title.setLayoutParams(view_layout);
                user_list.addView(title);
                title.setOnClickListener(new text_listener(output[7],output[14]));
                // System.out.println("3");

                if(!output[15].equals("null")){
                    title1.setText(s2+"(有圖)");
                }else{
                    title1.setText(s2+"(沒圖)");
                }
                title1.setTextSize(14);
                title1.setPadding(0,18,0,18);
                title1.setLayoutParams(view_layout);
                user_list.addView(title1);
                title1.setOnClickListener(new text_listener(output[8], output[15]));
                //  System.out.println("4");

                if(!output[16].equals("null")){
                    title2.setText(s3+"(有圖)");
                }else{
                    title2.setText(s3+"(沒圖)");
                }
                title2.setTextSize(14);
                title2.setPadding(0,18,0,18);
                title2.setLayoutParams(view_layout);
                user_list.addView(title2);
                title2.setOnClickListener(new text_listener(output[9], output[16]));
                // System.out.println("5");

                if(!output[17].equals("null")){
                    title3.setText(s4+"(有圖)");
                }else{
                    title3.setText(s4+"(沒圖)");
                }
                title3.setTextSize(14);
                title3.setPadding(0,18,0,18);
                title3.setLayoutParams(view_layout);
                user_list.addView(title3);
                title3.setOnClickListener(new text_listener(output[10], output[17]));
                //System.out.println("6");

                if(!output[18].equals("null")){
                    title4.setText(s5+"(有圖)");
                }else{
                    title4.setText(s5+"(沒圖)");
                }
                title4.setTextSize(14);
                title4.setPadding(0,18,0,18);
                title4.setLayoutParams(view_layout);
                user_list.addView(title4);
                title4.setOnClickListener(new text_listener(output[11], output[18]));
                //System.out.println("7");
                if(!output[19].equals("null")){
                    title5.setText(s6+"(有圖)");
                }else{
                    title5.setText(s6+"(沒圖)");
                }
                title5.setTextSize(14);
                title5.setPadding(0,18,0,18);
                title5.setLayoutParams(view_layout);
                user_list.addView(title5);
                title5.setOnClickListener(new text_listener(output[12], output[19]));


                if(!output[20].equals("null")){
                    title6.setText(s7+"(有圖)");
                }else{
                    title6.setText(s7+"(沒圖)");
                }
                title6.setTextSize(14);
                title6.setPadding(0,18,0,18);
                title6.setLayoutParams(view_layout);
                user_list.addView(title6);
                title6.setOnClickListener(new text_listener(output[13],output[20]));
            }

        }
    }
    private class text_listener implements View.OnClickListener{
        String content;
        String imgUrl;
        public text_listener(String s,String imgUrl){
            content = s;
            this.imgUrl = imgUrl;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ContentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("content",content);
            bundle.putString("imgUrl",imgUrl);
            intent.putExtras(bundle);

            startActivity(intent);


        }
    }
    private class getClass implements View.OnClickListener{
        int t;
        public getClass(int i){
            t = i;
        }
        @Override
        public void onClick(View v) {
            TableLayout case_list = (TableLayout)findViewById(R.id.user_list);
            case_list.removeAllViews();
            TextView loading = new TextView(MainActivity.this);
            loading.setText("載入中  請稍候...");
            loading.setGravity(Gravity.CENTER);
            loading.setPadding(0,30,0,0);
            case_list.addView(loading);
            Thread thread = new typeThread(t);
            thread.start();
        }
    }

    class typeThread extends Thread{
        String k;
        public typeThread(int i){
            k = Integer.toString(i);
        }

        @Override
        public void run() {
            String input = k;
            String fn = "category_json.php";
            String fn1 = "getImage.php";
            try {
                String result = DBconnector.executeQuery(input,fn);
                JSONArray jsonArray = new JSONArray(result);
                String[] output;
                output = new String[15];

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData3 = jsonArray.getJSONObject(i);
                    output[i] =jsonData3.getString("title");
                    output[i+5] =jsonData3.getString("content");
                    String result1 = DBconnector.executeQuery(jsonData3.getString("cid"),fn1);
                    System.out.println(result1);
                    JSONArray jsonArray1 = new JSONArray(result1);
                    JSONObject jsonData4 = jsonArray1.getJSONObject(0);
                    output[i+10] = jsonData4.getString("imageUrl");
                }
                Message msg = handler.obtainMessage();
                msg.obj= output;
                msg.arg1 = 2;
                handler.sendMessage(msg);


            } catch(Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    }



    private Button.OnClickListener getDBRecord = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            TableLayout case_list = (TableLayout)findViewById(R.id.user_list);
            case_list.removeAllViews();
            TextView loading = new TextView(MainActivity.this);
            loading.setText("載入中  請稍候...");
            loading.setGravity(Gravity.CENTER);
            loading.setPadding(0,30,0,0);
            case_list.addView(loading);
            Thread thread = new searchThread();
            thread.start();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
