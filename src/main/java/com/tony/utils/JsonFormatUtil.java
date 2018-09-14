package com.tony.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author jiangwj20966 2018/9/14
 */
public class JsonFormatUtil {

    public static String prettyJson(String uglyJSONString){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJSONString);
        return gson.toJson(je);
    }

    public static void main(String[] args) {
        System.out.println(JsonFormatUtil.prettyJson("{\"log_id\":7177980844217130789,\"words_result\":[{\"words\":\"卡号:8217566712165135\"},{\"words\":\"密码:\"},{\"words\":\"58jd2e4c\"}],\"words_result_num\":3}"));
    }
}
