package com.tony;

import com.alibaba.fastjson.JSONObject;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author jiangwj20966 2018/9/6
 */
public class MainTest {

    private static String ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static String GENERATE_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
    private static String appKey = "3RQGeLiC4m2fM8A1x56KRP6G";
    private static String appSecret = "IXfU7qOTcOEdhM57Rl5hAE5j0IUnoGi0";

    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\jiangwj20966\\Desktop\\TIM截图20180906170440.png";
        File image = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(image);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int l = -1;
        while ((l = fileInputStream.read(buff)) > 0) {
            outputStream.write(buff, 0, l);
        }
        String base64Str = Base64.encodeBase64String(outputStream.toByteArray());
        System.out.println(base64Str);

        String accessToken = getAccessToken();

        getResult(base64Str, accessToken);

    }


    private static String getResult(String imgBase64, String accessToken) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder().add("image", imgBase64)
//                .add("grant_type", "client_credentials")
                .add("access_token", accessToken).build();
        Request request = new Request.Builder().addHeader("Content-Type", "application/x-www-form-urlencoded").url(GENERATE_URL).post(formBody).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response != null && response.body() != null) {
            JSONObject result = (JSONObject) JSONObject.parse(response.body().string());
            System.out.println(result.toJSONString());
        }
        return null;
    }

    private static String getAccessToken() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder().add("grant_type", "client_credentials")
                .add("client_id", appKey)
                .add("client_secret", appSecret).build();
        Request request = new Request.Builder().url(ACCESS_TOKEN_URL).post(formBody).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response != null && response.body() != null) {
            JSONObject result = (JSONObject) JSONObject.parse(response.body().string());
            String accessKey = result.getString("access_token");
            return accessKey;
        }
        return null;
    }
}
