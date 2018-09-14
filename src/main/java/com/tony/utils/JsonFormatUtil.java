package com.tony.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author jiangwj20966 2018/9/14
 */
public class JsonFormatUtil {

    private StringBuffer buffer = new StringBuffer();

    public String formatJSONString(String jsonString) {
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String prettyJson = JSON.toJSON(jsonString).toString();
        System.out.println(prettyJson);
        format(jsonObject, 0, false);
        System.out.println(buffer.toString());
        return buffer.toString();
    }

    /**
     * 格式化json
     */
    private void format(Object json, int num, boolean isArray) {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            if (isArray) {
                buffer.append(getNSpace(num)).append("{\n");
            } else {
                buffer.append("{\n");
            }
            for (String k : jsonObject.keySet()) {
                buffer.append(getNSpace(num + 4)).append("\"").append(k).append("\"").append(": ");
                // 格式化子目录
                format(jsonObject.get(k), num, false);
            }
            buffer.append(getNSpace(num)).append("}\n");
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            buffer.append("[\n");
            num += 4;
            for (Object obj : jsonArray) {
                format(obj, num, true);
            }
            buffer.append(getNSpace(num)).append("]\n");
        } else {
            // 如果不是json对象就直接打印值
            buffer.append("\"").append(json.toString()).append("\"\n");
        }

    }

    /**
     * 获取num个数个空格
     *
     * @param num
     * @return
     */
    private String getNSpace(Integer num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        JsonFormatUtil jsonFormatUtil = new JsonFormatUtil();
        jsonFormatUtil.formatJSONString("{\"log_id\":7177980844217130789,\"words_result\":[{\"words\":\"卡号:8217566712165135\"},{\"words\":\"密码:\"},{\"words\":\"58jd2e4c\"}],\"words_result_num\":3}");
    }
}
