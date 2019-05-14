package ip.proxy.pool.api;

import ip.proxy.pool.dbtool.MyRedis;
import ip.proxy.pool.entrance.Main;
import ip.proxy.pool.logutil.LogManager;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.sitetemplate.GenEntry;
import ip.proxy.pool.utilclass.ParamValidateUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author dhengyi
 * @create 2019/05/09 16:46
 * @description ip代理池对外提供的api接口类
 */

public class IPProxyPool {

    static {
        LogManager.init();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(IPProxyPool.class);

    // TODO: 将Redis设计为单例会有问题
    private MyRedis myRedis = new MyRedis();

    // 生成模板
    public static void genTemplate() {
        genTemplate(1);
    }

    public static void genTemplate(Integer number) {
        // 参数校验
        if (!ParamValidateUtil.validateRange(1, 5, number)) {
            LOGGER.error("入参有误，number：{}", number);
            throw new RuntimeException("config.xml模板生成，入参有误");
        }

        LOGGER.info("模板生成中...");

        Document doc = GenEntry.genTemplate(number);
        GenEntry.saveDocument(doc);

        LOGGER.info("模板生成完毕...");
    }

    // TODO: 需要确定打包之后是否还可正常写入文件
    // 配置Redis环境
    public static void setMyRedisConfig(String addr, Integer port, String passwd) {
        if (StringUtils.isEmpty(addr) || StringUtils.isEmpty(String.valueOf(port)) || StringUtils.isEmpty(passwd)) {
            LOGGER.error("入参有误，addr：{}, port：{}，passwd：{}", addr, port, passwd);
            throw new RuntimeException("Redis配置设定，入参有误");
        }

        Properties properties = new Properties();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/redis-config.properties");

            properties.setProperty("jedis.addr", addr);
            properties.setProperty("jedis.port", String.valueOf(port));
            properties.setProperty("jedis.passwd", passwd);

            properties.store(fileOutputStream, null);

            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
      获取ip，此方法有可能返回null值，需要进行NPE检查
      客户端若获取到null值，则重新调用此方法，触发ip代理池更新即可
     */
    public IPMessage getIPMessage() {
        IPMessage ipMessage;
        // 判断ip代理池是否已经启动更新
        boolean flag = false;

        // 当ip代理池为空时，更新ip代理池
        synchronized (IPProxyPool.class) {
            while (myRedis.isEmpty()) {
                LOGGER.info("IPProxyPool已空");
                if (!flag) {
                    Main.startExecute();
                }

                flag = true;
            }

            do {
                ipMessage = myRedis.getIPFromList();
                if (ipMessage == null) {
                    break;
                }
            } while (ipMessage.getUseCount() == 3);
        }

        return ipMessage;
    }

    // 放置ip
    public void placeIPMessage(IPMessage ipMessage, boolean usable) {
        if (ipMessage == null) {
            return;
        }

        // 判断ip是否可用
        if (!usable) {
            ipMessage.setUseCount(ipMessage.getUseCount() + 1);
        } else {
            ipMessage.setUseCount(0);
        }

        synchronized (IPProxyPool.class) {
            myRedis.setIPToList(ipMessage);
        }
    }

    // 释放Redis资源
    public void closeMyRedis() {
        myRedis.close();
    }
}
