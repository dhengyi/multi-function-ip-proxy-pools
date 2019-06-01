package ip.proxy.pool.dbconfig;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

/**
 * @author dhengyi
 * @create 2019/04/03 14:01
 * @description Jedis的资源准备
 */

public class RedisConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    private static String addr;
    private static Integer port;
    private static String passwd;

    // 初始化连接
    static {
        try {
            // 先进行redis数据的参数配置
            JedisPoolConfig config = new JedisPoolConfig();
            // 链接耗尽时是否阻塞，false时抛出异常，默认是true，阻塞超时之后抛出异常
            config.setBlockWhenExhausted(true);
            // 逐出策略类名，当连接超过最大空闲时间或最大空闲数抛出异常
            config.setEvictionPolicyClassName("org.apache.commons.pool2." +
                    "impl.DefaultEvictionPolicy");
            // 是否启用pool的jmx管理功能，默认是true
            config.setJmxEnabled(true);
            // 最大空闲数，默认为8，一个pool最多有多少空闲的Jedis实例
            config.setMaxIdle(60);
            // 最大连接数
            config.setMaxTotal(100);
            // 当引入一个Jedis实例时，最大的等待时间，如果超过等待时间，抛出异常
            config.setMaxWaitMillis(1000*10);
            // 获得一个jedis实例的时候是否检查连接可用性（ping()）
            config.setTestOnBorrow(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 设置Jedis连接Redis数据库参数
    public static void setJedis(String redisAddr, Integer redisPort, String redisPasswd) {
        addr = redisAddr;
        port = redisPort;
        passwd = redisPasswd;
    }

    // 获取Jedis实例
    public synchronized static Jedis getJedis() {
        if (StringUtils.isEmpty(addr) || StringUtils.isEmpty(String.valueOf(port)) ||
                StringUtils.isEmpty(passwd)) {
            LOGGER.error("redis-config.properties配置文件内容有误，addr：{}，port：{}，passwd：{}",
                    addr, port, passwd);
            throw new RuntimeException("redis-config.properties配置文件内容有误");
        }

        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(addr, port);
        // 权限认证
        jedis.auth(passwd);

        return jedis;
    }

    // 释放Jedis资源
    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
