package com.moxiao.sqlmonitor.util;

import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLog(HttpUtils.class);
    
    private static final int timeOut = 5;

    private HttpUtils() {}
    
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String context) {
        logger.debug("准备发送请求地址：" + url + "，发送内容的为：" + context);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(context, JSON_TYPE)).build();
        try (Response response = client.newCall(request).execute()){
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String responseContext = responseBody.string();
                logger.debug("返回的消息内容为：" + responseContext);
                return responseContext;
            } else {
                logger.error("请求响应失败，状态码为：" + response.code());
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
