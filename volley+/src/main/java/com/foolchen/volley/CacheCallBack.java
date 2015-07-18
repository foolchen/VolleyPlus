package com.foolchen.volley;

/**
 * 针对缓存的回调接口
 *
 * @author chenchong
 *         15/7/18
 *         下午7:20
 */
public interface CacheCallBack<T> {
    /**
     * 缓存成功回调
     *
     * @param response 进行回调的缓存
     */
    void onCacheResponse(T response);

    /**
     * 缓存读取/转换错误的回调
     *
     * @param error 发生的错误
     */
    void onCacheErrorResponse(VolleyError error);
}
