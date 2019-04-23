package ip.proxy.pool.entrance;

import ip.proxy.pool.logutil.LogManager;

/**
 * @author dhengyi
 * @create 2019/04/22 22:36
 * @description 测试入口
 */

public class MainTest {
    public static void main(String[] args) {
        LogManager.init();
        IPProxyPool.startExecute(new Object());
    }
}
