package ip.proxy.pool.entrance;

import ip.proxy.pool.jobthread.IPProxyPoolThread;

/**
 * @author dhengyi
 * @create 2019/04/17 14:39
 * @description 执行ip代理池
 */

public class IPProxyPool {

    // lock用来实现生产者/消费者模型
    public static void startExecute(Object lock) {
        Thread ipProxyPool = new Thread(new IPProxyPoolThread(lock));
        ipProxyPool.setName("ip-proxy-pool");
        ipProxyPool.start();
    }
}
