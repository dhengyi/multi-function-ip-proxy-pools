package ip.proxy.pool.thread;

import ip.proxy.pool.grabutil.IPCollection;

import java.util.Queue;

/**
 * @author dhengyi
 * @create 2019/04/11 13:07
 * @description 创建代理ip抓取线程
 */

public class IPGrabThread implements Runnable {

    // 所有线程共享任务队列
    private Queue<String> urls;
    private IPCollection ipCollection;
    private Object taskLock;

    public IPGrabThread(Queue<String> urls, IPCollection ipCollection, Object taskLock) {
        this.urls = urls;
        this.ipCollection = ipCollection;
        this.taskLock = taskLock;
    }

    @Override
    public void run() {
        ipCollection.saveIP(urls, taskLock);
    }
}
