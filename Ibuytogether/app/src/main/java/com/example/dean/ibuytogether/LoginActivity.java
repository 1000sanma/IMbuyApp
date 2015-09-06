package com.example.dean.ibuytogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by dean on 2015/8/26.
 */
public class LoginActivity extends ActionBarActivity {
    EditText login,pw;
    Button loginButton;
    String username,password;
    int condition= 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (EditText)findViewById(R.id.login);
        pw = (EditText)findViewById(R.id.pw);
        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    private void signup() {

        username = login.getText().toString().trim();
        password = pw.getText().toString().trim();

                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseUser.logInInBackground(username, password, new LogInCallback() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {
                                        if (parseUser != null) {
                                            Intent intent = new Intent();
                                            intent.setClass(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Log.e("dean", e.getMessage());

                                        }
                                    }
                                });

                            } else {
                                Log.d("dean", "已經註冊過");
                                ParseUser.logInInBackground(username, password, new LogInCallback() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {
                                        if (parseUser != null) {
                                            Intent intent = new Intent();
                                            intent.setClass(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Log.e("dean", e.getMessage());

                                        }
                                    }
                                });
                            }
                        }
                    });
            }






}
