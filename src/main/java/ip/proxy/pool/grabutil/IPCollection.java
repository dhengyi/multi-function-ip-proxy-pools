package ip.proxy.pool.grabutil;

import ip.proxy.pool.ipfilter.IPFilter;
import ip.proxy.pool.model.IPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author dhengyi
 * @create 2019/04/10 09:25
 * @description ip收集类，使用多线程抓取西刺代理网
 */

public class IPCollection {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPCollection.class);

    // 成员变量（非线程安全）
    private List<IPMessage> ipMessages;
    // 创建供上述变量使用的读写锁
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public IPCollection(List<IPMessage> ipMessages) {
        this.ipMessages = ipMessages;
    }

    public void saveIP(Queue<String> urls, Object taskLock) {
//        int rand = 0;
//        readWriteLock.readLock().lock();
//        String ipAddress = ipMessages.get(rand).getIPAddress();
//        String ipPort = ipMessages.get(rand).getIPPort();
//        readWriteLock.readLock().unlock();
//
//        while (true) {
//            // 每个线程先将自己抓取下来的ip保存下来并进行过滤
//            List<IPMessage> ipMessages1 = new ArrayList<>();
//            String url;
//
//            if (urls.isEmpty()) {
//                LOGGER.info("任务队列已空");
//                break;
//            }
//
//            // 任务队列是共享变量，对其的读写必须进行正确的同步
//            synchronized (taskLock) {
//                url = urls.poll();
//            }
//
//            boolean success = true;
////            boolean success = URLAnalysis.urlParse(url, ipAddress, ipPort, ipMessages1);
//            // 如果ip代理池里面的ip不能用，或本页抓取失败，则切换下一个ip对本页进行重新抓取
//            if (!success) {
//                // 当抓取失败的时候重新拿取代理ip
//                rand = (int) (Math.random() * ipMessages.size());
//                /*
//                  随机挑选代理ip(本步骤由于其他线程有可能对ipMessages数量进行增加，虽说不会改变已经
//                  选择的代理ip的位置，但合情合理在对共享变量进行读写的时候还是要保证其原子性)
//                 */
//                readWriteLock.readLock().lock();
//                ipAddress = ipMessages.get(rand).getIPAddress();
//                ipPort = ipMessages.get(rand).getIPPort();
//                readWriteLock.readLock().unlock();
//
//                synchronized (taskLock) {
//                    urls.offer(url);
//                }
//                continue;
//            }
//
//            // 对ip重新进行过滤，只要速度在三秒以内的并且类型为HTTPS的
//            ipMessages1 = IPFilter.filter(ipMessages1);
//
//            // 将质量合格的ip合并到共享变量ipMessages中，进行合并的时候保证原子性
//            readWriteLock.writeLock().lock();
//            LOGGER.info("已进入合并区，待合并大小ipMessages1：{}", ipMessages1.size());
//            ipMessages.addAll(ipMessages1);
//            LOGGER.info("已成功合并，合并后ipMessage大小：{}", ipMessages.size());
//            readWriteLock.writeLock().unlock();
//        }
    }
}
