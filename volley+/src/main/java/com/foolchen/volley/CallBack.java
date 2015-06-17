package com.foolchen.volley;

/**
 * 回调接口
 *
 * @author chenchong
 *         15/6/17
 *         下午10:41
 */
public interface CallBack<T> extends Response.Listener<T>, Response.ErrorListener {
    /** 缓存的回调方法 */
    void onCacheResponse(T response);
}
