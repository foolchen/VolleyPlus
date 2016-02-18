package com.foolchen.demo.volley;

import android.content.Context;
import com.foolchen.volley.*;
import com.foolchen.volley.custom.OkHttpClientStack;
import com.foolchen.volley.toolbox.Volley;

/**
 * 该类用于在Application中初始化网络请求(Volley)
 *
 * @author chenchong
 *         2014/5/30 0030
 *         15:33
 */
public class RequestManager {
    public static final String TAG = "RequestManager";
    public static final int REQUEST_FAILED_DELAY = 150000;
    private static RequestQueue mRequestQueue;

    private RequestManager() {
        // no instances
    }

    /** 初始化请求队列 */
    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context, new OkHttpClientStack());
        SyncVolley.init(new OkHttpClientStack());
    }

    /**
     * 获取到当前的请求队列
     *
     * @return 请求队列
     */
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    /**
     * 添加并开启新的请求
     *
     * @param request 要添加的请求
     * @param tag     标志不同请求的tag,用于中断请求等操作
     */
    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    /** 中断所有对应tag的请求 */
    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    /**
     * 执行request
     *
     * @param request 要执行的request
     * @param tag     执行request时绑定的tag，用于取消request
     */
    public static void executeRequest(Request<?> request, Object tag) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_FAILED_DELAY,
                /*DefaultRetryPolicy.DEFAULT_MAX_RETRIES*/0,
                /*DefaultRetryPolicy.DEFAULT_BACKOFF_MULT*/0));
        addRequest(request, tag);
    }

    public static <T> Response<T> executeRequest(Request<T> request) throws VolleyError {
        return SyncVolley.executeRequest(request);
    }
}
