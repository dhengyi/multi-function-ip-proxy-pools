package ip.proxy.pool.ipoperation;

import ip.proxy.pool.model.IPMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * @author dhengyi
 * @create 2019/05/08 18:10
 * @description ip提取方法集合类
 */

public class IPExtraction {

    // 随机获取代理ip
    public static IPMessage getIPMessageRandom(List<IPMessage> ipMessages) {
        if (CollectionUtils.isEmpty(ipMessages)) {
            return null;
        }

        int rand = RandomUtils.nextInt(0, ipMessages.size());

        return ipMessages.get(rand);
    }
}
