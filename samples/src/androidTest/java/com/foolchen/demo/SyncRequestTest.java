package com.foolchen.demo;

import android.test.AndroidTestCase;
import android.util.Log;
import com.foolchen.demo.volley.RequestManager;
import com.foolchen.volley.Response;
import com.foolchen.volley.toolbox.StringRequest;

/**
 * @author chenchong
 *         16/2/18
 *         下午4:42
 */
public class SyncRequestTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RequestManager.init(getContext());
    }

    public void testSyncRequest() throws Throwable {
        StringRequest request = new StringRequest("http://m.baidu.com", null, null);
        Response<String> response = RequestManager.executeRequest(request);
        if (response.isSuccess()) {
            Log.i("SyncRequest", response.result);
        } else {
            Log.e("SyncRequest", response.error.toString());
        }
    }
}
