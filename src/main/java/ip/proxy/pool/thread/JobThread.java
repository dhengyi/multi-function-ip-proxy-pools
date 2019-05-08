package ip.proxy.pool.thread;

import ip.proxy.pool.grabutil.URLAnalysis;
import ip.proxy.pool.ipoperation.IPExtraction;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.model.SiteTemplateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author dhengyi
 * @create 2019/05/08 17:55
 * @description 负责解析任务队列中url的工作线程
 */

public class JobThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobThread.class);

    private ReadWriteLock readWriteLock;

    private String url;
    private List<IPMessage> ipMessages;
    private SiteTemplateInfo siteTemplateInfo;

    public JobThread(ReadWriteLock readWriteLock, String url, List<IPMessage> ipMessages,
                     SiteTemplateInfo siteTemplateInfo) {
        this.readWriteLock = readWriteLock;
        this.url = url;
        this.ipMessages = ipMessages;
        this.siteTemplateInfo = siteTemplateInfo;
    }

    @Override
    public void run() {
        List<IPMessage> ipMessagesTemp;

        do {
            readWriteLock.readLock().lock();
            IPMessage ipMessage = IPExtraction.getIPMessageRandom(ipMessages);
            readWriteLock.readLock().unlock();

            ipMessagesTemp = URLAnalysis.parseQueueByProxyIP(url, ipMessage.getIpAddress(),
                    ipMessage.getIpPort(), siteTemplateInfo);
        } while (ipMessagesTemp == null);

        readWriteLock.writeLock().lock();
        ipMessages.addAll(ipMessagesTemp);
        LOGGER.info("已合并ipMessagesTemp：{}，ipMessages：{}", ipMessagesTemp.size(), ipMessages.size());
        readWriteLock.writeLock().unlock();
    }
}
