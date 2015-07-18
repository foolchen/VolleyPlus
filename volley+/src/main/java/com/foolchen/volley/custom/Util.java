package com.foolchen.volley.custom;

import com.foolchen.volley.PolicyRequest;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * {@link PolicyRequest}的辅助工具类
 *
 * @author chenchong
 *         15/6/28
 *         下午7:53
 */
public class Util {

    /**
     * 生成需要解析的Type(在可以直接使用Class时,则不要使用该方法)<p/>
     * 该方法仅用于获取限定了泛型的集合类型
     *
     * @param <T> 集合中的类型
     *
     * @return 获取到的类型
     */
    public static <T> Type generateTypeOfList() {
        return new TypeToken<List<T>>() {}.getType();
    }
}
