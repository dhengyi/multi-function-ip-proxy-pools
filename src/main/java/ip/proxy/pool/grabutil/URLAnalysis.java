package ip.proxy.pool.grabutil;

import ip.proxy.pool.ipmodel.IPMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * @author dhengyi
 * @create 2019/04/07 17:08
 * @description 对xici代理网的url进行解析，提取出其中的代理ip
 */

public class URLAnalysis {

    // 使用本机ip爬取xici代理网站的第一页
    public static void urlParse(String url, List<IPMessage> ipMessages) {
        String html = MyHttpClient.getHtml(url);

        // 将html解析成DOM结构
        Document document = Jsoup.parse(html);

        // 提取所需要的数据
        Elements trs = document.select("table[id=ip_list]").select("tbody").select("tr");
        getIPMessages(ipMessages, trs);
    }

    // 使用代理进行爬取
    public static boolean urlParse(String url, String ip, String port,
                                   List<IPMessage> ipMessages) {
        String html = MyHttpClient.getHtml(url, ip, port);

        if(html != null) {
            Document document = Jsoup.parse(html);
            Elements trs = document.select("table[id=ip_list]").select("tbody").select("tr");

            getIPMessages(ipMessages, trs);

            return true;
        }

        return false;
    }

    public static void getIPMessages(List<IPMessage> ipMessages, Elements trs) {
        for (int i = 1; i < trs.size(); i++) {
            IPMessage ipMessage = new IPMessage();

            String ipAddress = trs.get(i).select("td").get(1).text();
            String ipPort = trs.get(i).select("td").get(2).text();
            String ipType = trs.get(i).select("td").get(5).text();
            String ipSpeed = trs.get(i).select("td").get(6).select("div[class=bar]").
                    attr("title");

            ipMessage.setIPAddress(ipAddress);
            ipMessage.setIPPort(ipPort);
            ipMessage.setIPType(ipType);
            ipMessage.setIPSpeed(ipSpeed);

            ipMessages.add(ipMessage);
        }
    }
}
