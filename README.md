# Volley+

## 目标

在保留原有加载逻辑的情况下,对加载方式进行扩展,预计实现以下功能:

- 原有加载逻辑(根据缓存是否过期决定是否请求网络)
- 只读缓存(已完成)
- 只请求网络(已完成)
- 读取缓存后再请求网络(已完成)
- 直接使用Gson进行解析,而不再返回字符串(已完成)
- 自定义缓存
- 计算缓存大小

## 使用方法

生成实体类

``` java
GsonPolicyRequest<TestModel> request = new GsonPolicyRequest<TestModel>(url, TestModel.class, new CallBack<TestModel>() {
              @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(TestModel response) {
            }
        }, new CacheCallBack<String>() {
            @Override
            public void onCacheResponse(String response) {
            }

            @Override
            public void onCacheErrorResponse(VolleyError error) {
            }
        });
RequestManager.addRequest(request, this);
```

生成List

``` java
GsonPolicyRequest<List<TestModel>> request = new GsonPolicyRequest<List<TestModel>>(url, Util.<TestModel>generateTypeOfList(), new CallBack<List<TestModel>>() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(List<TestModel> response) {

            }
        }, new CacheCallBack<TestModel>() {
            @Override
            public void onCacheResponse(List<TestModel> response) {
            }

            @Override
            public void onCacheErrorResponse(VolleyError error) {
            }
        });
RequestManager.addRequest(request, this);
```