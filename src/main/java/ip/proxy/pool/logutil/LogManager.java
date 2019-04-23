package ip.proxy.pool.logutil;

/**
 * @author dhengyi
 * @create 2019/04/23 12:09
 * @description 日志控制类
 */

// TODO: DailyRollingFileAppender没有设定MaxBackupIndex的功能，后期看需要进行补充
public class LogManager {

    public static void init() {
        String rootPath = System.getProperty("user.dir");
        System.setProperty("log.base", rootPath);
    }
}
