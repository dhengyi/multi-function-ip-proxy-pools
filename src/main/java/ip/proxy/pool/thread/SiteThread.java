package ip.proxy.pool.thread;

import ip.proxy.pool.grabutil.URLAnalysis;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.model.SiteTemplateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author dhengyi
 * @create 2019/05/07 14:38
 * @description 管理分配给此线程对应网站的抓取工作
 */

public class SiteThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteThread.class);

    private final SiteTemplateInfo siteTemplateInfo;

    public SiteThread(SiteTemplateInfo siteTemplateInfo) {
        this.siteTemplateInfo = siteTemplateInfo;
    }

    @Override
    public void run() {
        Queue<String> urls = createURLs(siteTemplateInfo);

        List<IPMessage> ipMessages = getIPMessagesFromSiteFirstPage(urls.poll(), siteTemplateInfo);

//        for (int i = 0; i < urls.size(); i++) {
//            Thread jobThread = new Thread(new JobThread(urls.poll()));
//            jobThread.setName(Thread.currentThread().getName() + "-thread-" + i);
//            jobThread.start();
//        }
    }

    // 构造URL，创建任务队列
    private Queue<String> createURLs(SiteTemplateInfo siteTemplateInfo) {
        Queue<String> urls = new LinkedList<>();
        String url = siteTemplateInfo.getUrl();

        for (int i = 1; i <= 21; i++) {
            String newUrl = url.replace("{}", String.valueOf(i));
            urls.offer(newUrl);
        }

        return urls;
    }

    // 获取网站首页所提供的代理ip
    private List<IPMessage> getIPMessagesFromSiteFirstPage(String url, SiteTemplateInfo siteTemplateInfo) {
        return URLAnalysis.parseURLByRealIP(url, siteTemplateInfo);
    }
}
