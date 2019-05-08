package ip.proxy.pool.crontab;

import ip.proxy.pool.dbtool.MyRedis;
import ip.proxy.pool.grabutil.IPCollection;
import ip.proxy.pool.grabutil.URLAnalysis;
import ip.proxy.pool.ipfilter.IPFilter;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.thread.IPGrabThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author dhengyi
 * @create 2019/04/02 11:31
 * @description 构建ip代理池任务
 */

public class MyTimeJob extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyTimeJob.class);

    // ip代理池线程是生产者，此锁用来实现等待/通知机制，实现生产者与消费者模型
    private final Object lock;

    MyTimeJob(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        MyRedis myRedis = new MyRedis();
        // 创建一个有关任务队列的锁
        Object taskLock = new Object();

        // 如果ip代理池中没有ip信息，则ip代理池进行工作
        while (true) {
            while (myRedis.isEmpty()) {
                synchronized (lock) {
                    LOGGER.info("ip代理池，开始更新...");

                    // 存放爬取下来的ip信息
                    List<IPMessage> ipMessages = new LinkedList<>();
                    // 创建任务队列
                    Queue<String> urls = new LinkedList<>();
                    // 对创建的子线程进行收集
                    List<Thread> threads = new ArrayList<>();

                    // 首先使用本机ip爬取xici代理网第一页
                    String url = "http://www.xicidaili.com/nn/1";
//                    URLAnalysis.urlParse(url, ipMessages);
                    // 对得到的ip进行筛选，将ip速度在三秒以内的并且类型是https的留下，其余删除
                    ipMessages = IPFilter.filter(ipMessages);

                    ipMessages.forEach(System.out::println);

                    // 构造种子url(2000条ip)
                    for (int i = 2; i <= 21; i++) {
                        urls.offer("http://www.xicidaili.com/nn/" + i);
                    }

                    // 使用多线程对urls进行解析并过滤,拿到所有目标ip，将所有的ip存储进ipMessages这个共享变量中
                    IPCollection ipCollection = new IPCollection(ipMessages);
                    for (int i = 0; i < 20; i++) {
                        IPGrabThread ipGrabThread = new IPGrabThread(urls, ipCollection, taskLock);
                        Thread thread = new Thread(ipGrabThread);
                        thread.setName("ip-proxy-pool-thread-" + i);
                        threads.add(thread);
                        thread.start();
                    }

                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            LOGGER.error("调用thread.join出现异常，e：{}", e);
                        }
                    }

                    // 将爬取下来的ip信息写进Redis数据库中(List集合)
                    myRedis.setIPToList(ipMessages);

                    LOGGER.info("ip代理池已经更新完毕...");

                    lock.notifyAll();
                }
            }
        }
    }
}
