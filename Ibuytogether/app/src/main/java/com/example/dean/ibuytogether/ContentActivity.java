package com.example.dean.ibuytogether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dean on 2015/7/1.
 */
public class ContentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        Bundle bundle = this.getIntent().getExtras();
        String content = bundle.getString("content");
        TextView t = (TextView)findViewById(R.id.content);
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
    }
}
