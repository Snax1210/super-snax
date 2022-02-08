package org.snax.supersnax.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    /**
     * 发送 http post 请求，参数以form表单键值对的形式提交。
     */
    public static void httpPostForm(String url, String param, Map<String, String> headers, String encode) {
        //        HttpResponse response = new HttpResponse();
        if (encode == null) {
            encode = "utf-8";
        }
        //HttpClients.createDefault()等价于 HttpClientBuilder.create().build();
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPost httpost = new HttpPost(url);

        //设置header
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        //组织请求参数
        httpost.setEntity(new StringEntity(param, encode));
        String content = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpost);
            //            HttpEntity entity = httpResponse.getEntity();
            //            content = EntityUtils.toString(entity, encode);
            //            response.setBody(content);
            //            response.setHeaders(httpResponse.getAllHeaders());
            //            response.setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
            //            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        return response;
    }

}