package ip.proxy.pool.jobthread;

import ip.proxy.pool.crontab.MyTimer;

/**
 * @author dhengyi
 * @create 2019/04/17 17:51
 * @description ip代理池后台线程
 */

public class IPProxyPoolThread implements Runnable {

    private final Object lock;

    public IPProxyPoolThread(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        MyTimer.startIPProxyPool(lock);
    }
}
