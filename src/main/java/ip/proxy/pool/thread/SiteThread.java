package ip.proxy.pool.thread;

import ip.proxy.pool.grabutil.URLAnalysis;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.model.SiteTemplateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author dhengyi
 * @create 2019/05/07 14:38
 * @description 管理分配给此线程对应网站的抓取工作
 */

public class SiteThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteThread.class);

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private SiteTemplateInfo siteTemplateInfo;
    private List<IPMessage> ipMessagesAll;

    public SiteThread(SiteTemplateInfo siteTemplateInfo, List<IPMessage> ipMessagesAll) {
        this.siteTemplateInfo = siteTemplateInfo;
        this.ipMessagesAll = ipMessagesAll;
    }

    @Override
    public void run() {
        Queue<String> urls = createURLs(siteTemplateInfo);

        List<IPMessage> ipMessages = getIPMessagesFromSiteFirstPage(urls.poll(), siteTemplateInfo);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; urls.size() != 0; i++) {
            Thread jobThread = new Thread(new JobThread(readWriteLock, urls.poll(), ipMessages, siteTemplateInfo));
            jobThread.setName(Thread.currentThread().getName() + "-childthread-" + i);
            threads.add(jobThread);
            jobThread.start();
        }

        threads.forEach(param -> {
            try {
                param.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        synchronized (SiteThread.class) {
            ipMessagesAll.addAll(ipMessages);
            LOGGER.info("已合并ipMessages：{}，ipMessagesAll：{}", ipMessages.size(), ipMessagesAll.size());
        }
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
