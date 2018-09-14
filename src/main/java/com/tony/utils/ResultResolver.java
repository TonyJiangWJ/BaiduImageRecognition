package com.tony.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.List;

/**
 * @author jiangwj20966 2018/9/10
 */
public class ResultResolver {

    public static String getFullText(JSONObject json) {
        JSONArray jsonArray = json.getJSONArray("words_result");

        StringBuilder stringBuilder = new StringBuilder();
        for (Object js : jsonArray) {
            JSONObject obj = (JSONObject) js;
            stringBuilder.append(obj.getString("words"));
        }
        return stringBuilder.toString();
    }

    public static String getSegmenterString(String string) {
        JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
        List<String> strList = jiebaSegmenter.sentenceProcess(string);
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strList) {
            stringBuilder.append(str).append("  ");
        }
        return stringBuilder.toString();
    }

}
