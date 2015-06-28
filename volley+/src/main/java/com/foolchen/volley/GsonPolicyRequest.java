package com.foolchen.volley;

import com.foolchen.volley.custom.RequestPolicy;
import com.foolchen.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * 使用Gson解析数据的{@link PolicyRequest}<p/
 * 此时需要传入Class用于解析数据
 *
 * @author chenchong
 *         15/6/28
 *         下午4:48
 */
public class GsonPolicyRequest<T> extends PolicyRequest<T> {
    private CallBack<T> mCallBack;
    private Class<T> mClazz;

    public GsonPolicyRequest(String url, Class<T> clazz, CallBack<T> callBack) {
        super(url, callBack);
        this.mCallBack = callBack;
        this.mClazz = clazz;
    }

    public GsonPolicyRequest(RequestPolicy policy, String url, Class<T> clazz, CallBack<T> callback) {
        super(policy, url, callback);
        this.mCallBack = callback;
        this.mClazz = clazz;
    }

    public GsonPolicyRequest(RequestPolicy policy, int method, String url, Class<T> clazz, CallBack<T> callback) {
        super(policy, method, url, callback);
        this.mCallBack = callback;
        this.mClazz = clazz;
    }

    @Override
    protected void deliverCache(T response) {
        mCallBack.onCacheResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        if (response.empty) {
            // 如果返回为空(在CACHE_ONLY情况下可能会出现),则直接返回空
            return Response.success(null, null);
        }
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        T result = new Gson().fromJson(parsed, mClazz);
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        mCallBack.onResponse(response);
    }
}
