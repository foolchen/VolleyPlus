package com.foolchen.volley;

import com.foolchen.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * 一个简单的String请求
 *
 * @author chenchong
 *         15/6/17
 *         下午11:25
 */
public class StringPolicyRequest extends PolicyRequest<String> {
    private CallBack<String> mCallBack;

    public StringPolicyRequest(String url, CallBack<String> callBack) {
        super(url, callBack);
        this.mCallBack = callBack;
    }

    @Override
    protected void deliverCache(String response) {
        mCallBack.onCacheResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
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
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mCallBack.onResponse(response);
    }
}
