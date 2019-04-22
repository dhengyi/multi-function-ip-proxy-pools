package ip.proxy.pool.ipfilter;

import ip.proxy.pool.ipmodel.IPMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dhengyi
 * @create 2019/04/09 10:26
 * @description 对ip进行过滤
 */

public class IPFilter {
    public static List<IPMessage> filter(List<IPMessage> ipMessages1) {
        List<IPMessage> newIPMessages = new LinkedList<>();

        for (IPMessage ipMessage : ipMessages1) {
            String ipType = ipMessage.getIPType();
            String ipSpeed = ipMessage.getIPSpeed();

            ipSpeed = ipSpeed.substring(0, ipSpeed.indexOf('秒'));
            double Speed = Double.parseDouble(ipSpeed);

            if (ipType.equals("HTTPS") && Speed <= 3.0) {
                newIPMessages.add(ipMessage);
            }
        }

        return newIPMessages;
    }
}
