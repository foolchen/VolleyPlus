package com.foolchen.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.foolchen.volley.CallBack;
import com.foolchen.volley.StringPolicyRequest;
import com.foolchen.volley.VolleyError;
import com.foolchen.volley.custom.RequestPolicy;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.button_request) Button mButtonRequest;
    @InjectView(R.id.text_result) TextView mTextResult;
    @InjectView(R.id.spinner) Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            public void onCacheResponse(String response) {
                mTextResult.setText(Html.fromHtml("cache<br/>" + response));
            }

            @Override
            public void onResponse(String response) {
                mTextResult.setText(Html.fromHtml(response));
            }
        });
        final RequestPolicy policy = (RequestPolicy) mSpinner.getSelectedItem();
        request.setPolicy(policy);
        request.setShouldCache(true);
    }

    void initSpinner() {
        RequestPolicy[] policies = {RequestPolicy.DEFAULT, RequestPolicy.CACHE_ONLY, RequestPolicy.CACHE_THEN_NET};
        ArrayAdapter<RequestPolicy> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, policies);
        mSpinner.setAdapter(adapter);
    }
}
