package com.foolchen.volley;

import com.foolchen.volley.custom.RequestPolicy;

/**
 * 所有网络请求的基类
 *
 * @author chenchong
 *         15/6/17
 *         下午10:43
 */
public abstract class PolicyRequest<T> extends Request<T> {
    private RequestPolicy policy;
    /** 在网络数据已经返回的情况下,则不再需要缓存数据,此时抛弃 */
    public boolean cacheAbandon;

    PolicyRequest(String url, CallBack callBack) {
        this(RequestPolicy.DEFAULT, url, callBack);
    }

    /**
     * 构造函数
     *
     * @param policy   请求策略
     * @param url      请求的url
     * @param callback 回调接口
     */
    public PolicyRequest(RequestPolicy policy, String url, CallBack callback) {
        super(url, callback);
        this.policy = policy;
    }

    /**
     * 构造函数
     *
     * @param policy   请求策略
     * @param method   请求方式,见{@link com.foolchen.volley.Request.Method}
     * @param url      请求的url
     * @param callback 回调接口
     */
    public PolicyRequest(RequestPolicy policy, int method, String url, CallBack callback) {
        super(method, url, callback);
        this.policy = policy;
    }

    /** 设置当前Request的请求策略 */
    public void setPolicy(RequestPolicy policy) {
        this.policy = policy;
    }

    /** 获取当前Request的请求策略 */
    public RequestPolicy getPolicy() {
        return policy;
    }

    protected abstract void deliverCache(T response);
}
