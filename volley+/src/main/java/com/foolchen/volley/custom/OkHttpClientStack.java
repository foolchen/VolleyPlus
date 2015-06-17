package com.foolchen.volley.custom;

import com.foolchen.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * chenchong
 * 2014/8/28 0028
 * 15:27
 */
public class OkHttpClientStack extends HurlStack {

    public OkHttpClientStack() {
    }

    private OkUrlFactory generateDefaultOkUrlFactory() {
        OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
        return new OkUrlFactory(client);
    }


    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        OkUrlFactory okUrlFactory = generateDefaultOkUrlFactory();
        return okUrlFactory.open(url);
    }
}
