/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foolchen.volley;

import android.os.Process;
import com.foolchen.volley.custom.RequestPolicy;

import java.util.concurrent.BlockingQueue;

/**
 * Provides a thread for performing cache triage on a queue of requests.
 * <p/>
 * Requests added to the specified cache queue are resolved from cache.
 * Any deliverable response is posted back to the caller via a
 * {@link com.foolchen.volley.ResponseDelivery}.  Cache misses and responses that require
 * refresh are enqueued on the specified network queue for processing
 * by a {@link com.foolchen.volley.NetworkDispatcher}.
 */
public class CacheDispatcher extends Thread {

    private static final boolean DEBUG = VolleyLog.DEBUG;

    /** The queue of requests coming in for triage. */
    private final BlockingQueue<Request<?>> mCacheQueue;

    /** The queue of requests going out to the network. */
    private final BlockingQueue<Request<?>> mNetworkQueue;

    /** The cache to read from. */
    private final Cache mCache;

    /** For posting responses. */
    private final ResponseDelivery mDelivery;

    /** Used for telling us to die. */
    private volatile boolean mQuit = false;

    /**
     * Creates a new cache triage dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param cacheQueue   Queue of incoming requests for triage
     * @param networkQueue Queue to post requests that require network to
     * @param cache        Cache interface to use for resolution
     * @param delivery     Delivery interface to use for posting responses
     */
    public CacheDispatcher(
            BlockingQueue<Request<?>> cacheQueue, BlockingQueue<Request<?>> networkQueue,
            Cache cache, ResponseDelivery delivery) {
        mCacheQueue = cacheQueue;
        mNetworkQueue = networkQueue;
        mCache = cache;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        if (DEBUG) VolleyLog.v("start new dispatcher");
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        // Make a blocking call to initialize the cache.
        mCache.initialize();

        while (true) {
            try {
                // Get a request from the cache triage queue, blocking until
                // at least one is available.
                // 从缓存队列中获取一个request
                final Request<?> request = mCacheQueue.take();
                request.addMarker("cache-queue-take");

                // If the request has been canceled, don't bother dispatching it.
                // 如果request已经被取消,则直接结束
                if (request.isCanceled()) {
                    request.finish("cache-discard-canceled");
                    continue;
                }

                // 此处尝试获取策略
                RequestPolicy policy = RequestPolicy.DEFAULT;
                if (request instanceof PolicyRequest) {
                    request.addMarker("policy-request-found");
                    policy = ((PolicyRequest) request).getPolicy();
                }

                if (policy == RequestPolicy.DEFAULT) {
                    // Attempt to retrieve this item from cache.
                    // 尝试从缓存中获取key对应的数据
                    Cache.Entry entry = mCache.get(request.getCacheKey());
                    if (entry == null) {
                        request.addMarker("cache-miss");
                        // 如果为默认请求策略,则在没有读取到缓存的情况下,直接将request添加到网络请求队列中
                        // Cache miss; send off to the network dispatcher.
                        // 没有获取到数据,则将request添加到网络请求队列中
                        mNetworkQueue.put(request);
                        continue;
                    }

                    // If it is completely expired, just send it to the network.
                    // 读取到了缓存,但是缓存过期了,将request添加到网络请求队列中
                    if (entry.isExpired()) {
                        request.addMarker("cache-hit-expired");
                        request.setCacheEntry(entry);
                        mNetworkQueue.put(request);
                        continue;
                    }

                    // We have a cache hit; parse its data for delivery back to the request.
                    // 获取到了可用(未过期)的缓存,则将它转换为需要的response回传给request
                    request.addMarker("cache-hit");
                    Response<?> response = request.parseNetworkResponse(
                            new NetworkResponse(entry.data, entry.responseHeaders));
                    request.addMarker("cache-hit-parsed");

                    if (!entry.refreshNeeded()) {
                        // Completely unexpired cache hit. Just deliver the response.
                        // 如果缓存没有超过存活时间,则直接回调
                        mDelivery.postResponse(request, response);
                    } else {
                        // Soft-expired cache hit. We can deliver the cached response,
                        // but we need to also send the request to the network for
                        // refreshing.
                        // 缓存超过了存活时间,我们可以将缓存进行回调
                        // 但是也需要进行网络请求刷新数据
                        request.addMarker("cache-hit-refresh-needed");
                        request.setCacheEntry(entry);

                        // Mark the response as intermediate.
                        // 此时不会执行回调
                        response.intermediate = true;

                        // Post the intermediate response back to the user and have
                        // the delivery then forward the request along to the network.
                        mDelivery.postResponse(request, response, new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mNetworkQueue.put(request);
                                } catch (InterruptedException e) {
                                    // Not much we can do about this.
                                }
                            }
                        });
                    }
                } else {
                    Cache.Entry entry = mCache.get(request.getCacheKey());
                    if (policy == RequestPolicy.CACHE_ONLY || policy == RequestPolicy.CACHE_THEN_NET
                            || policy == RequestPolicy.CACHE_INVALID_THEM_NET) {
                        // 请求策略为CACHE_ONLY
                        if (entry == null) {
                            // 缓存为空,则回调空response
                            request.addMarker("cache-miss");
                            Response<?> response = Response.success(null, null);
                            response.cache = true;
                            // Mark the response as intermediate.
                            response.intermediate = (policy == RequestPolicy.CACHE_THEN_NET || policy == RequestPolicy.CACHE_INVALID_THEM_NET);
                            request.addMarker("cache-miss-deliver-empty-response");
                            mDelivery.postResponse(request, response, response.intermediate ? createRunnable(request) : null);
                        } else {
                            // 缓存不为空,则回调缓存response
                            Response<?> response = request.parseNetworkResponse(
                                    new NetworkResponse(entry.data, entry.responseHeaders));
                            response.cache = true;
                            request.addMarker("cache-hit-deliver");
                            response.intermediate = (policy == RequestPolicy.CACHE_THEN_NET || policy == RequestPolicy.CACHE_INVALID_THEM_NET);

                            request.setCacheEntry(entry);
                            mDelivery.postResponse(request, response, response.intermediate ? createRunnable(request) : null);
                        }
                    }
                }

            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
            }
        }
    }

    private Runnable createRunnable(final Request<?> request) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    mNetworkQueue.put(request);
                } catch (InterruptedException e) {
                    // Not much we can do about this.
                }
            }
        };
    }
}
