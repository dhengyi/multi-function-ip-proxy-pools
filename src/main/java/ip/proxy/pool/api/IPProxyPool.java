package ip.proxy.pool.api;

import ip.proxy.pool.dbtool.MyRedis;
import ip.proxy.pool.entrance.Main;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.sitetemplate.GenEntry;
import ip.proxy.pool.utilclass.ParamValidateUtil;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author dhengyi
 * @create 2019/05/09 16:46
 * @description ip代理池对外提供的api接口类
 */

public class IPProxyPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPProxyPool.class);

    // TODO: 到底应该将Redis设计为单例还是多例还有待考量
    // 将MyRedis设置为单例
    private static MyRedis myRedis = new MyRedis();

    // 生成模板
    public static void genTemplate() {
        genTemplate(1);
    }

    public static void genTemplate(Integer number) {
        LOGGER.info("模板生成中...");

        // 参数校验
        if (!ParamValidateUtil.validateRange(1, 5, number)) {
            throw new RuntimeException();
        }

        Document doc = GenEntry.genTemplate(number);
        GenEntry.saveDocument(doc);

        LOGGER.info("模板生成完毕...");
    }

    // TODO: 需要确定打包之后是否还可正常写入文件
    // 配置Redis环境
    public static void setMyRedisConfig(String addr, String port, String passwd) {
        Properties properties = new Properties();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/redis-config.properties");

            properties.setProperty("jedis.addr", addr);
            properties.setProperty("jedis.port", port);
            properties.setProperty("jedis.passwd", passwd);

            properties.store(fileOutputStream, null);

            fileOutputStream.close();
        } catch (IOException e) {
            LOGGER.info("Redis配置写入redis-config.properties文件出现异常");
            LOGGER.error("Redis配置写入redis-config.properties文件出现异常，e：{}", e);
            throw new RuntimeException();
        }
    }

    // 获取ip
    public static IPMessage getIPMessage() {
        ReadWriteLock readWriteLock = MyRedis.getReadWriteLock();
        // 判断ip代理池是否已经启动更新
        boolean flag = false;

        // 当ip代理池为空时，更新ip代理池
        readWriteLock.writeLock().lock();
        while (myRedis.isEmpty()) {
            if (!flag) {
                Main.startExecute();
            }

            flag = true;
        }
        readWriteLock.writeLock().unlock();

        readWriteLock.readLock().lock();
        IPMessage ipMessage;
        do {
            ipMessage = myRedis.getIPFromList();
        } while (ipMessage.getUseCount() == 3);
        readWriteLock.readLock().unlock();

        return ipMessage;
    }

    // 放置ip
    public static void placeIPMessage(IPMessage ipMessage, boolean usable) {
        ReadWriteLock readWriteLock = MyRedis.getReadWriteLock();

        // 判断ip是否可用
        if (!usable) {
            ipMessage.setUseCount(ipMessage.getUseCount() + 1);
        } else {
            ipMessage.setUseCount(0);
        }

        readWriteLock.writeLock().lock();
        myRedis.setIPToList(ipMessage);
        readWriteLock.writeLock().unlock();
    }

    // 释放Redis资源
    public static void closeMyRedis() {
        myRedis.close();
    }
}
