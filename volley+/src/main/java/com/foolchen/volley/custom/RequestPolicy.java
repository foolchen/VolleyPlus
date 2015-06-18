package com.foolchen.volley.custom;

/**
 * 加载策略
 *
 * @author chenchong
 *         15/6/17
 *         下午10:34
 */
public enum RequestPolicy {
    /** 默认加载方式(Volley原有加载方式,根据缓存是否过期决定是否请求网络) */
    DEFAULT,
    /** 只读缓存(无论缓存是否过期,都只读缓存,缓存不存在也不再请求网络) */
    CACHE_ONLY,
    /** 先读取缓存,并进行回调;之后再请求网络,请求结束后再次进行回调 */
    CACHE_THEN_NET
}
