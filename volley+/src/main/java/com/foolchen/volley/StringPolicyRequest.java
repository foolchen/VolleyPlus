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
    /** 网络数据回调接口 */
    private final CallBack<String> mCallBack;
    /** 缓存数据回调接口 */
    private CacheCallBack<String> mCacheCallBack;

    /**
     * @see #StringPolicyRequest(String, CallBack, CacheCallBack)
     */
    public StringPolicyRequest(final String url, final CallBack<String> callBack) {
        super(url, callBack);
        this.mCallBack = callBack;
    }

    /**
     * 构造函数
     *
     * @param url           要请求的URL
     * @param callBack      网络数据回调接口
     * @param cacheCallBack 缓存数据回调接口
     */
    public StringPolicyRequest(final String url, final CallBack<String> callBack, final CacheCallBack<String> cacheCallBack) {
        super(url, callBack);
        this.mCallBack = callBack;
        this.mCacheCallBack = cacheCallBack;
    }

    @Override
    protected void deliverCache(final String response) {
        mCacheCallBack.onCacheResponse(response);
    }

    @Override
    protected void deliverCacheError(final VolleyError error) {
        mCacheCallBack.onCacheErrorResponse(error);

    }

    @Override
    protected Response<String> parseNetworkResponse(final NetworkResponse response) {
        Response<String> result;
        if (response.empty) {
            // 如果返回为空(在CACHE_ONLY情况下可能会出现),则直接返回空
            result = Response.success(null, null);
        } else {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            result = Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }
        return result;
    }

    @Override
    protected void deliverResponse(final String response) {
        mCallBack.onResponse(response);
    }
}
