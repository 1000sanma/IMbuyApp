package com.example.dean.ibuytogether;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dean on 2015/7/1.
 */
public class ContentActivity extends Activity {
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        handler = new myHandler();
        Bundle bundle = this.getIntent().getExtras();
        String content = bundle.getString("content");
        String imgUrl = bundle.getString("imgUrl");
        TextView t = (TextView)findViewById(R.id.content);

        ImageView m = (ImageView)findViewById(R.id.push);
        Log.e("tw8","imgURL:"+imgUrl);


        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ContentActivity.this, PushActivity.class);
                startActivity(intent);
            }
        });

        t.setText(content);
        ImageView i =(ImageView)findViewById(R.id.back);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ContentActivity.this, MainActivity.class);
                startActivity(intent);
                ContentActivity.this.finish();
            }
        });
        Thread imgThread = new imageThread(imgUrl);
        imgThread.start();
    }
    class myHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            ImageView img =(ImageView)findViewById(R.id.img);
            Bitmap b = (Bitmap)msg.obj;
            img.setImageBitmap(b);
        }
    }
    class imageThread extends Thread{
        String url;
        public imageThread(String i){
            url = i;
        }

        @Override
        public void run() {
            if(!url.equals("null")){
                Bitmap b = ImgUtils.getBitmapFromURL(url);
                Message msg = handler.obtainMessage();
                msg.obj= b;
                handler.sendMessage(msg);
            }
        }
    }
    public static class ImgUtils {
        //讀取網路圖片，型態為Bitmap
        public static Bitmap getBitmapFromURL(String imageUrl){
            try
            {
                imageUrl ="http:"+ imageUrl ;
                URL url = new URL(imageUrl);
                Log.e("tw8",imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }
}
