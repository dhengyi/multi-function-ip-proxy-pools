package ip.proxy.pool.model;

import java.io.Serializable;

/**
 * @author dhengyi
 * @create 2019/04/12 15:54
 * @description IPMessage JavaBean
 */

public class IPMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ipAddress;
    private String ipPort;
    // 加入ipType字段是为了向前兼容
    private String ipType;
    private int useCount;   // 计数器，连续三次这个ip不能使用，就将其从ip代理池中清除

    public IPMessage() {
        this.useCount = 0;
    }

    public IPMessage(String ipAddress, String ipPort, String ipType, int useCount) {
        this.ipAddress = ipAddress;
        this.ipPort = ipPort;
        this.ipType = ipType;
        this.useCount = useCount;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpPort() {
        return ipPort;
    }

    public void setIpPort(String ipPort) {
        this.ipPort = ipPort;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    @Override
    public String toString() {
        return "IPMessage{" +
                "ipAddress='" + ipAddress + '\'' +
                ", ipPort='" + ipPort + '\'' +
                ", ipType='" + ipType + '\'' +
                ", useCount=" + useCount +
                '}';
    }
}