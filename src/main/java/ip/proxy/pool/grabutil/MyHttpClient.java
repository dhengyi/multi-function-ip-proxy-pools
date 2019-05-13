package ip.proxy.pool.grabutil;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * @author dhengyi
 * @create 2019/04/15 18:15
 * @description HTTP请求模拟类
 *              关于HttpClient的使用，有许多大坑，推荐大家多在网上查阅一些相关资料
 */

public class MyHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpClient.class);

    // 使用本机ip构造请求
    public static String getHtml(String url) {
        CloseableHttpClient httpClient = getHttpClient();
        String entity = null;
        CloseableHttpResponse httpResponse = null;

        // 设置超时处理
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(1000).setConnectTimeout(1000).
                setSocketTimeout(1000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");

        try {
            // 客户端执行httpGet方法，返回响应
            httpResponse = httpClient.execute(httpGet);

            // 得到服务响应状态码
            int httpStatus = httpResponse.getStatusLine().getStatusCode();
            if (httpStatus == 200) {
                entity = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                LOGGER.info("使用本机ip成功抓取：{}，返回状态码httpStatus：{}", url, httpStatus);
            } else {
                LOGGER.warn("使用本机ip抓取：{}失败，返回状态码httpStatus：{}", url, httpStatus);
            }
        } catch (IOException e) {
            LOGGER.error("使用本机ip抓取：" + url + "，出现异常e：{}", e);
        } finally {
            closeResources(httpResponse, httpClient);
        }

        return entity;
    }

    // 对上一个方法的重载，使用代理进行网站爬取
    public static String getHtml(String url, String ipAddress, String ipPort) {
        CloseableHttpClient httpClient = getHttpClient();
        String entity = null;
        CloseableHttpResponse httpResponse = null;

        // 设置代理访问和超时处理
        HttpHost proxy = new HttpHost(ipAddress, Integer.valueOf(ipPort));
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectionRequestTimeout(1000).
                setConnectTimeout(1000).setSocketTimeout(1000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        try {
            // 客户端执行httpGet方法，返回响应
            httpResponse = httpClient.execute(httpGet);

            // 得到服务响应状态码
            int httpStatus = httpResponse.getStatusLine().getStatusCode();
            if (httpStatus == 200) {
                entity = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                LOGGER.info("使用代理ip：{}:{}，成功抓取：{}，返回状态码：{}", ipAddress, ipPort, url, httpStatus);
            } else {
                LOGGER.warn("使用代理ip：{}:{}，抓取：{}失败，返回状态码：{}", ipAddress, ipPort, url, httpStatus);
            }
        } catch (IOException e) {
            LOGGER.warn("使用代理ip：" + ipAddress + ":" + ipPort + "，抓取：" + url + "，出现异常e：{}", e);
        } finally {
            closeResources(httpResponse, httpClient);
        }

        return entity;
    }

    // 获取HttpClient--自定义HttpClient配置参数，关闭重试机制并且防止无限等待
    private static CloseableHttpClient getHttpClient() {
        // 连接成功后，多长时间数据没有返回断开连接
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(1000).build();

        // 设置HttpClient关闭重试机制
        HttpRequestRetryHandler httpRequestRetryHandler = (exception, executionCount, context) -> {
            if (executionCount > 0) {       // 不进行重试
                return false;
            }
            if (exception instanceof NoHttpResponseException) {     // 如果服务器丢掉了连接，那么就重试
                return false;
            }
            if (exception instanceof SSLHandshakeException) {       // 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {      // 超时
                return false;
            }
            if (exception instanceof UnknownHostException) {        // 目标服务器不可达
                return false;
            }
            if (exception instanceof SSLException) {        // SSL握手异常
                return false;
            }

            HttpClientContext clientContext = HttpClientContext
                    .adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 请求幂等
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return false;
            }

            return false;
        };

        return HttpClients.custom().setDefaultSocketConfig(socketConfig).
                setRetryHandler(httpRequestRetryHandler).build();
    }

    // 资源关闭
    private static void closeResources(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) {
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }

            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            LOGGER.error("httpResponse，httpClient资源关闭出现异常，e：{}", e);
        }
    }
}
