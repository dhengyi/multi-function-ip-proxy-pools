package ip.proxy.pool.dbtool;

import ip.proxy.pool.dbconfig.RedisConfig;
import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.utilclass.SerializeUtil;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author dhengyi
 * @create 2019/04/03 19:28
 * @description 集成对Redis数据库的操作
 */

public class MyRedis {

    private final Jedis jedis = RedisConfig.getJedis();
    // 创建一个读写锁（static保证其唯一性）
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // 对外提供读写锁
    public static ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    public static void setReadWriteLock(ReadWriteLock readWriteLock) {
        MyRedis.readWriteLock = readWriteLock;
    }

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
