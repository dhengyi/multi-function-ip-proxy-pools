package ip.proxy.pool.api;

import ip.proxy.pool.dbtool.MyRedis;
import ip.proxy.pool.entrance.Main;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.sitetemplate.GenEntry;
import ip.proxy.pool.utilclass.ParamValidateUtil;
import org.dom4j.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author dhengyi
 * @create 2019/05/09 16:46
 * @description ip代理池对外提供的api接口类
 */

public class IPProxyPool {

    public static void genTemplate() {
        genTemplate(1);
    }

    public static void genTemplate(Integer number) {
        // 参数校验
        if (!ParamValidateUtil.validateRange(1, 5, number)) {
            throw new RuntimeException();
        }

        Document doc = GenEntry.genTemplate(number);
        GenEntry.saveDocument(doc);
    }

    // 将MyRedis设置为单例
    private static MyRedis myRedis = new MyRedis();

    // 获取ip
    public static IPMessage getIPMessage() {
        // 判断ip代理池是否已经启动更新
        boolean flag = false;

        synchronized (IPProxyPool.class) {
            while (myRedis.isEmpty()) {
                if (!flag) {
                    Main.startExecute();
                }

                flag = true;
            }

            IPMessage ipMessage;
            do {
                ipMessage = myRedis.getIPFromList();
            } while (ipMessage.getUseCount() == 3);

            return ipMessage;
        }
    }

    // 放置ip
    public static void placeIPMessage(IPMessage ipMessage) {
        ipMessage.setUseCount(ipMessage.getUseCount() + 1);
        myRedis.setIPToList(ipMessage);
    }

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
            e.printStackTrace();
        }
    }

    public static void closeMyRedis() {
        myRedis.close();
    }
}
