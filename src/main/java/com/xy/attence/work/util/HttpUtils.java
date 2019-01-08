package com.xy.attence.work.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * http请求工具类
 */
public class HttpUtils {
    public static String doPost(String url, JSONObject jsonObject) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(20000).setConnectionRequestTimeout(10000).build();

        httpPost.setConfig(requestConfig);
        String context = StringUtils.EMPTY;

        StringEntity body = new StringEntity(jsonObject.toString(), "utf-8");
        httpPost.setEntity(body);

        //设置回调接口接收的消息头
        httpPost.addHeader("Content-Type", "application/json");
        CloseableHttpResponse response = null;

        try{
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            context = EntityUtils.toString(entity, "utf-8");
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                response.close();
                httpPost.abort();
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return context;
    }


    /**
     * get请求传输数据
     *
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doGet(String url) {
        String result = "";

        try {
            // 创建httpclient对象
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // 创建get方式请求对象
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Content-type", "application/json");
            // 通过请求对象获取响应对象
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 获取结果实体
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
            // 释放链接
            response.close();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }
}
