package com.example.dean.ibuytogether;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;


/**
 * Created by dean on 2015/8/26.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "A0a1FYo7ZGE5T1mBPOO5M0cG88LgpVrjChMnB9Qj", "CC0jUhiVOzeYtH12VxiIC45yIoclcyiXPsGyUFCX");
        ParseInstallation.getCurrentInstallation().saveInBackground();






    }
}
