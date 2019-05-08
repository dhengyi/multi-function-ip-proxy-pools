package ip.proxy.pool.entrance;

import ip.proxy.pool.thread.SiteThread;
import ip.proxy.pool.model.SiteTemplateInfo;
import ip.proxy.pool.sitetemplate.ParseEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dhengyi
 * @create 2019/05/07 12:02
 * @description 执行ip代理池
 */

public class IPProxyPool {

    // ip代理池执行入口
    public static void startExecute() {
        List<SiteTemplateInfo> siteTemplateInfos = ParseEntry.parseTemplate();

        // 对创建的子线程进行收集
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < siteTemplateInfos.size(); i++) {
            Thread site = new Thread(new SiteThread(siteTemplateInfos.get(i)));
            site.setName("site-thread-" + i);
            threads.add(site);
            site.start();
        }

        threads.forEach(param -> {
            try {
                param.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // lock用来实现生产者/消费者模型
//    public static void startExecute(Object lock) {
//        // 根据xml文件中提供的ip地址，开辟对应的抓取线程
//        Thread ipProxyPool = new Thread(new IPProxyPoolThread(lock));
//        ipProxyPool.setName("ip-proxy-pool");
//        ipProxyPool.start();
//    }
}
