package com.example.dean.ibuytogether;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
    private ImageButton search_button,cloth_button,cosmetic_button,bag_button;
    private Handler handler;
    private EditText editText;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner)findViewById(R.id.spinner);
        String[] category = {"下半身", "日常用品", "寵物", "票券", "食物", "鞋襪", "配件"};


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
            try {
                String result = DBconnector.executeQuery(input,fn);
                System.out.println(result);
                JSONArray jsonArray = new JSONArray(result);
                String[] output;
                output = new String[3];
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData3 = jsonArray.getJSONObject(i);
                    output[i] =jsonData3.getString("title");
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

    class myHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==1){
                String[] output;
                output = (String[]) msg.obj;
                String s1 = output[0];
                String s2 = output[1];
                String s3 = output[2];
                TableLayout user_list = (TableLayout)findViewById(R.id.user_list);
                user_list.removeAllViews();
                user_list.setStretchAllColumns(true);

                TableRow.LayoutParams view_layout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView title = new TextView(MainActivity.this);
                TextView title1 = new TextView(MainActivity.this);
                TextView title2 = new TextView(MainActivity.this);
                title.setText(s1);
                title.setLayoutParams(view_layout);
                user_list.addView(title);
                title1.setText(s2);
                title1.setLayoutParams(view_layout);
                user_list.addView(title1);
                title2.setText(s3);
                title2.setLayoutParams(view_layout);
                user_list.addView(title2);
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
                title.setText(s1);
                title.setLayoutParams(view_layout);
                user_list.addView(title);
                title.setOnClickListener(new text_listener(output[5]));
                System.out.println("3");

                title1.setText(s2);
                title1.setLayoutParams(view_layout);
                user_list.addView(title1);
                title1.setOnClickListener(new text_listener(output[6]));
                System.out.println("4");

                title2.setText(s3);
                title2.setLayoutParams(view_layout);
                user_list.addView(title2);
                title2.setOnClickListener(new text_listener(output[7]));
                System.out.println("5");

                title3.setText(s4);
                title3.setLayoutParams(view_layout);
                user_list.addView(title3);
                title3.setOnClickListener(new text_listener(output[8]));
                System.out.println("6");

                title4.setText(s5);
                title4.setLayoutParams(view_layout);
                user_list.addView(title4);
                title4.setOnClickListener(new text_listener(output[9]));
                System.out.println("7");
            }

        }
    }
    private class text_listener implements View.OnClickListener{
        String content;
        public text_listener(String s){
            content = s;
        }
        @Override
        public void onClick(View v) {
            setContentView(R.layout.second);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ContentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("content",content);
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
            try {
                String result = DBconnector.executeQuery(input,fn);
                JSONArray jsonArray = new JSONArray(result);
                String[] output;
                output = new String[10];

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData3 = jsonArray.getJSONObject(i);
                    output[i] =jsonData3.getString("title");
                    output[i+5] =jsonData3.getString("content");
                    System.out.println(output[i]);
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
