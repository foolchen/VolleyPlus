package com.foolchen.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.foolchen.demo.volley.RequestManager;
import com.foolchen.volley.CacheCallBack;
import com.foolchen.volley.CallBack;
import com.foolchen.volley.StringPolicyRequest;
import com.foolchen.volley.VolleyError;
import com.foolchen.volley.custom.RequestPolicy;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.text_result) TextView mTextResult;
    @Bind(R.id.spinner) Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initSpinner();
    }

    @OnClick(R.id.button_request)
    public void load() {
        mTextResult.setText(null);
        StringPolicyRequest request = new StringPolicyRequest("http://www.cnblogs.com/chenchong/p/3256192.html", new CallBack<String>() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextResult.setText(error.toString());
            }

            @Override
            public void onResponse(String response) {
                mTextResult.setText(Html.fromHtml(response));
            }
        }, new CacheCallBack<String>() {
            @Override
            public void onCacheResponse(String response) {
                mTextResult.setText(Html.fromHtml("cache<br/>" + response));
            }

            @Override
            public void onCacheErrorResponse(VolleyError error) {

            }
        });
        final RequestPolicy policy = (RequestPolicy) mSpinner.getSelectedItem();
        request.setPolicy(policy);
        request.setShouldCache(true);
        RequestManager.executeRequest(request, this);
    }

    void initSpinner() {
        RequestPolicy[] policies = {RequestPolicy.DEFAULT, RequestPolicy.CACHE_ONLY, RequestPolicy.CACHE_THEN_NET};
        ArrayAdapter<RequestPolicy> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, policies);
        mSpinner.setAdapter(adapter);
    }
}
