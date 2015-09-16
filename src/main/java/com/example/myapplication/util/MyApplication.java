package com.example.myapplication.util;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by yuechenglei on 2015/9/5.
 */
public class MyApplication extends Application {

    public static RequestQueue queue;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getHttpQueue(){
        return queue;
    }
}
