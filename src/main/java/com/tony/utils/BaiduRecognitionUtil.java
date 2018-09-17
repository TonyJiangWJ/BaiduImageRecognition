package com.tony.utils;

import com.alibaba.fastjson.JSONObject;
import javafx.scene.image.Image;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * @author jiangwj20966 2018/9/14
 */
public class BaiduRecognitionUtil {

    private static String ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static String GENERATE_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
    private static String appKey = "";
    private static String appSecret = "";

    private static final String CONFIG_FILE_LOCATION = "./config.properties";

    private static final String APP_KEY_K = "app.key";
    private static final String APP_SECRET_K = "app.secret";

    private static final double MAX_WIDTH_OR_HEIGHT = 4096;

    public static String getPicContent(byte[] captureImageArray) throws Exception {
        String base64Str = Base64.encodeBase64String(captureImageArray);
        System.out.println(base64Str);
        String accessToken = getAccessToken();
        return getResult(base64Str, accessToken);
    }

    public static String getPicContent(File imageFile) throws Exception {

        try (
                FileInputStream fileInputStream = new FileInputStream(imageFile);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {
            String fileName = imageFile.getName();
            String format = fileName.substring(fileName.lastIndexOf('.') + 1);
            BufferedImage image = ImageIO.read(fileInputStream);
            double width = image.getWidth();
            double height = image.getHeight();

            // 超大的图片进行压缩
            if (width > MAX_WIDTH_OR_HEIGHT || height > MAX_WIDTH_OR_HEIGHT) {
                double scale = 1;
                if (width > height) {
                    scale = MAX_WIDTH_OR_HEIGHT / width;
                } else {
                    scale = MAX_WIDTH_OR_HEIGHT / height;
                }
                int newWidth = (int) (width * scale);
                int newHeight = (int) (height * scale);
                BufferedImage bufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = bufferedImage.createGraphics();
                graphics.drawImage(image, 0, 0, newWidth, newHeight, null);
                ImageIO.write(bufferedImage, format, outputStream);
            } else {
                ImageIO.write(image, format, outputStream);
            }

            String base64Str = Base64.encodeBase64String(outputStream.toByteArray());
            System.out.println(base64Str);

            String accessToken = getAccessToken();
            return getResult(base64Str, accessToken);
        }
    }


    private static String getResult(String imgBase64, String accessToken) throws IOException {
        if (accessToken == null) {
            return null;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder().add("image", imgBase64)
//                .add("grant_type", "client_credentials")
                .add("access_token", accessToken).build();
        Request request = new Request.Builder().addHeader("Content-Type", "application/x-www-form-urlencoded").url(GENERATE_URL).post(formBody).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response != null && response.body() != null) {
            JSONObject result = (JSONObject) JSONObject.parse(response.body().string());
            System.out.println(result.toJSONString());
            return result.toJSONString();
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


    static {

        Properties properties = new Properties();
        File file = new File(CONFIG_FILE_LOCATION);
        if (file.exists()) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(file);
            ) {
                properties.load(fileInputStream);
                BaiduRecognitionUtil.setAppKey(properties.getProperty(APP_KEY_K));
                BaiduRecognitionUtil.setAppSecret(properties.getProperty(APP_SECRET_K));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeConfigIntoProperties() {
        try (
                OutputStream outputStream = new FileOutputStream((CONFIG_FILE_LOCATION));
                PrintWriter printWriter = new PrintWriter(outputStream);
        ) {
            Properties properties = new Properties();
            if (BaiduRecognitionUtil.getAppKey() != null) {
                properties.setProperty(APP_KEY_K, BaiduRecognitionUtil.getAppKey());
            }
            if (BaiduRecognitionUtil.getAppSecret() != null) {
                properties.setProperty(APP_SECRET_K, BaiduRecognitionUtil.getAppSecret());
            }
            properties.list(printWriter);
            printWriter.flush();
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(BaiduRecognitionUtil.getAppKey());
        BaiduRecognitionUtil.setAppSecret("12341234");
        BaiduRecognitionUtil.setAppKey("123143212");
        BaiduRecognitionUtil.writeConfigIntoProperties();
    }

    public static String getAccessTokenUrl() {
        return ACCESS_TOKEN_URL;
    }

    public static void setAccessTokenUrl(String accessTokenUrl) {
        ACCESS_TOKEN_URL = accessTokenUrl;
    }

    public static String getGenerateUrl() {
        return GENERATE_URL;
    }

    public static void setGenerateUrl(String generateUrl) {
        GENERATE_URL = generateUrl;
    }

    public static String getAppKey() {
        return appKey;
    }

    public static void setAppKey(String appKey) {
        BaiduRecognitionUtil.appKey = appKey;
    }

    public static String getAppSecret() {
        return appSecret;
    }

    public static void setAppSecret(String appSecret) {
        BaiduRecognitionUtil.appSecret = appSecret;
    }


}
