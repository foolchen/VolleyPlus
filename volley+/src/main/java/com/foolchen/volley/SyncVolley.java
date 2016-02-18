package com.foolchen.volley;

import com.foolchen.volley.custom.OkHttpClientStack;
import com.foolchen.volley.toolbox.BasicNetwork;

/**
 * @author chenchong
 *         16/2/18
 *         下午4:35
 */
public class SyncVolley {
    private static BasicNetwork mNetwork;

    public static void init(OkHttpClientStack stack) {
        mNetwork = new BasicNetwork(stack);
    }

    public static <T> Response<T> executeRequest(Request<T> request) throws VolleyError {
        NetworkResponse networkResponse = mNetwork.performRequest(request);
        return request.parseNetworkResponse(networkResponse);
    }
}
