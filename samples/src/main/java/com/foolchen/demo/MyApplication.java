package com.foolchen.demo;

import android.app.Application;
import com.foolchen.demo.volley.RequestManager;
import com.foolchen.volley.VolleyLog;

/**
 * @author chenchong
 *         15/6/8
 *         下午11:01
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RequestManager.init(this);
        VolleyLog.DEBUG = true;
    }
}
