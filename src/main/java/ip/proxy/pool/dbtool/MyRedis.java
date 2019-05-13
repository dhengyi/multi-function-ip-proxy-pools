package ip.proxy.pool.dbtool;

import ip.proxy.pool.dbconfig.RedisConfig;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.utilclass.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author dhengyi
 * @create 2019/04/03 19:28
 * @description 集成对Redis数据库的操作
 */

public class MyRedis {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyRedis.class);

    private final Jedis jedis = RedisConfig.getJedis();

    // 将单个ip信息保存在Redis列表中
    public void setIPToList(IPMessage ipMessage) {
        // 将ipMessage进行序列化
        byte[] bytes = SerializeUtil.serialize(ipMessage);

        jedis.rpush("ip-proxy-pool".getBytes(), bytes);
    }

    // 将多个ip信息保存在Redis列表中
    public void setIPToList(List<IPMessage> ipMessages) {
        for (IPMessage ipMessage : ipMessages) {
            byte[] bytes = SerializeUtil.serialize(ipMessage);

            jedis.rpush("ip-proxy-pool".getBytes(), bytes);
        }
    }

    // 获取ip信息
    public IPMessage getIPFromList() {
        return (IPMessage) SerializeUtil.unserialize(jedis.lpop("ip-proxy-pool".getBytes()));
    }

    // 判断ip代理池是否为空
    public boolean isEmpty() {
        return jedis.llen("ip-proxy-pool".getBytes()) <= 0;
    }

    // 释放Redis资源
    public void close() {
        RedisConfig.close(jedis);
    }
}
