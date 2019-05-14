package ip.proxy.pool.entrance;

import ip.proxy.pool.dbtool.MyRedis;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.thread.SiteThread;
import ip.proxy.pool.model.SiteTemplateInfo;
import ip.proxy.pool.sitetemplate.ParseEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dhengyi
 * @create 2019/05/07 12:02
 * @description 执行ip代理池
 */

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    // ip代理池执行入口
    public static void startExecute() {
        LOGGER.info("ip代理池开始更新...");

        MyRedis myRedis = new MyRedis();

        List<SiteTemplateInfo> siteTemplateInfos = ParseEntry.parseTemplate();

        // 存储抓取到的所有代理ip
        List<IPMessage> ipMessagesAll = new LinkedList<>();
        // 对创建的子线程进行收集
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < siteTemplateInfos.size(); i++) {
            Thread site = new Thread(new SiteThread(siteTemplateInfos.get(i), ipMessagesAll));
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

        // 将ip存储至Redis数据库中
        myRedis.setIPToList(ipMessagesAll);

        myRedis.close();

        LOGGER.info("ip代理池更新完毕，ipMessagesAll.size：{}", ipMessagesAll.size());
    }
}
