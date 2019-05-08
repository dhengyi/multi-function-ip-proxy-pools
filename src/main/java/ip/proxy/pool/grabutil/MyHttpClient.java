package ip.proxy.pool.grabutil;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author dhengyi
 * @create 2019/04/15 18:15
 * @description HTTP请求模拟类
 */

class MyHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpClient.class);

    // 使用本机ip构造请求
    static String getHtml(String url) {
        String entity = null;
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 设置超时处理(猜测setConnectTimeout是与网站建立HTTP链接的时间，setSocketTimeout是从网站获取数据的时间)
        RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).
                setSocketTimeout(3000).build();
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
            }

            LOGGER.info("本机ip抓取：{}，返回状态码httpStatus：{}", url, httpStatus);
        } catch (IOException e) {
            LOGGER.error("本机ip抓取：" + url + "，出现异常e：{}", e);
        } finally {
            closeResources(httpResponse, httpClient);
        }

        return entity;
    }

    // 对上一个方法的重载，使用代理进行网站爬取
    static String getHtml(String url, String ip, String port) {
        String entity = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;

        // 设置代理访问和超时处理
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(1000).
                setSocketTimeout(1000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "www.xicidaili.com");
        httpGet.setHeader("Pragma", "no-cache");
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
                LOGGER.info("使用代理ip：{}:{}，成功抓取xici代理网：{}", ip, port, url);
            } else {
                LOGGER.info("使用代理ip：{}:{}，抓取xici代理网：{}，返回状态码：{}", ip, port, url, httpStatus);
            }
        } catch (IOException e) {
            entity = null;
            LOGGER.warn("使用代理ip：{}:{}，抓取xici代理网：{}，出现异常，e：" + e, ip, port, url);
        } finally {
            closeResources(httpResponse, httpClient);
        }

        return entity;
    }

    private static void closeResources(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) {
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }

            if (httpClient != null) {
                httpClient.close();
            }
            LOGGER.info("httpResponse，httpClient资源关闭成功");
        } catch (IOException e) {
            LOGGER.error("httpResponse，httpClient资源关闭出现异常，e：{}", e);
        }
    }
}
