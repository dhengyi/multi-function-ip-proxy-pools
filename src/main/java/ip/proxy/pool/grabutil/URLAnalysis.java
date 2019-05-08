package ip.proxy.pool.grabutil;

import ip.proxy.pool.model.IPMessage;
import ip.proxy.pool.model.SiteTemplateInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dhengyi
 * @create 2019/05/07 15:42
 * @description 网页DOM结构解析类
 */

public class URLAnalysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(URLAnalysis.class);

    // 使用本机ip解析网站首页
    public static List<IPMessage> parseURLByRealIP(String url, SiteTemplateInfo siteTemplateInfo) {
        String html = MyHttpClient.getHtml(url);

        if (html == null) {
            LOGGER.error("使用本机ip，访问url：{}，返回html：null", url);
            throw new RuntimeException();
        }

        return extractIPMessages(html, siteTemplateInfo);
    }

    // 使用代理ip解析任务队列
    public static List<IPMessage> parseQueueByProxyIP(String url, String ipAddress,
                                                      String ipPort, SiteTemplateInfo siteTemplateInfo) {
        String html = MyHttpClient.getHtml(url, ipAddress, ipPort);

        if (html == null) {
            LOGGER.warn("使用代理ip：{}，访问url：{}，返回html：null", ipAddress + ":" + ipPort, url);
            return null;
        }

        return extractIPMessages(html, siteTemplateInfo);
    }

    // 提取网页代理ip信息
    private static List<IPMessage> extractIPMessages(String html, SiteTemplateInfo siteTemplateInfo) {
        Document document = Jsoup.parse(html);

        // 存储ip地址列与端口列所在索引
        List<Integer> ipMessageIndex;

        // 确定ip地址及端口所在表格列
        if (siteTemplateInfo.getHtmlLabelInfo2().getLabelName().equals("null")) {
            ipMessageIndex = getIPMessageIndexWithoutTableHead(document, siteTemplateInfo);
        } else {
            ipMessageIndex = getIPMessageIndexWithTableHead(document, siteTemplateInfo);
        }

        // 确定表格提供的代理ip信息所在标签行
        Elements trs = getIPElements(document, siteTemplateInfo);

        // 抓取代理ip信息
        if (siteTemplateInfo.getHtmlLabelInfo2().getLabelName().equals("null")) {
            return getIPMessageByIPMessageIndex(removeFirstElement(trs), ipMessageIndex);
        } else {
            return getIPMessageByIPMessageIndex(trs, ipMessageIndex);
        }
    }

    // 无表格头确定ip地址及端口所在表格列
    private static List<Integer> getIPMessageIndexWithoutTableHead(Document document,
                                                                   SiteTemplateInfo siteTemplateInfo) {
        String tableInfo = siteTemplateInfo.getHtmlLabelInfo1().getLabelName();
        String tbodyInfo = siteTemplateInfo.getHtmlLabelInfo3().getLabelName();

        if (!siteTemplateInfo.getHtmlLabelInfo1().getAttributeName().equals("null")) {
            tableInfo += "[" + siteTemplateInfo.getHtmlLabelInfo1().getAttributeName() + "=" +
                    siteTemplateInfo.getHtmlLabelInfo1().getAttributeValue() + "]";
        }
        if (!siteTemplateInfo.getHtmlLabelInfo3().getAttributeName().equals("null")) {
            tbodyInfo += "[" + siteTemplateInfo.getHtmlLabelInfo3().getAttributeName() + "=" +
                    siteTemplateInfo.getHtmlLabelInfo3().getAttributeValue() + "]";
        }

        Element tableHeadTr = document.select(tableInfo).select(tbodyInfo).select("tr").get(0);
        // TODO: 这里写死了标签名为th，实际上还有可能为td（66代理网）
        Elements ipMessageIndexs = tableHeadTr.select("th");

        return getIPMessageIndex(ipMessageIndexs);
    }

    // 有表格头确定ip地址及端口所在表格列
    private static List<Integer> getIPMessageIndexWithTableHead(Document document,
                                                                SiteTemplateInfo siteTemplateInfo) {
        String tableInfo = siteTemplateInfo.getHtmlLabelInfo1().getLabelName();
        String theadInfo = siteTemplateInfo.getHtmlLabelInfo2().getLabelName();

        if (!siteTemplateInfo.getHtmlLabelInfo1().getAttributeName().equals("null")) {
            tableInfo += "[" + siteTemplateInfo.getHtmlLabelInfo1().getAttributeName() + "=" +
                    siteTemplateInfo.getHtmlLabelInfo1().getAttributeValue() + "]";
        }
        if (!siteTemplateInfo.getHtmlLabelInfo2().getAttributeName().equals("null")) {
            theadInfo += "[" + siteTemplateInfo.getHtmlLabelInfo2().getAttributeName() + "=" +
                    siteTemplateInfo.getHtmlLabelInfo2().getAttributeValue() + "]";
        }

        Element tableHeadTr = document.select(tableInfo).select(theadInfo).select("tr").get(0);
        Elements ipMessageIndexs = tableHeadTr.select("th");

        return getIPMessageIndex(ipMessageIndexs);
    }

    // 确定ip地址列与端口列所在表格索引
    private static List<Integer> getIPMessageIndex(Elements ipMessageIndexs) {
        List<Integer> ipMessageIndex = new ArrayList<>();
        // 用于标记ip地址列与端口列索引是否已确定
        boolean flag1 = false, flag2 = false;

        /*
          事实上，flag1与flag2变量必不可少，原因在于有些网站提供的ip信息表格头中，包含“代理IP”，
          “IP匿名度”等字段，此时如果不引入标记变量，容易造成“IP匿名度”也被确认为待抓取索引列
         */
        for (int i = 0; i < ipMessageIndexs.size(); i++) {
            if (flag1 && flag2) {
                break;
            }

            if (!flag1 && (ipMessageIndexs.get(i).text().contains("ip") ||
                    ipMessageIndexs.get(i).text().contains("IP"))) {
                ipMessageIndex.add(i);
                flag1 = true;
            }

            if (!flag2 && ipMessageIndexs.get(i).text().contains("端口")) {
                ipMessageIndex.add(i);
                flag2 = true;
            }
        }

        return ipMessageIndex;
    }

    // 确定代理ip信息所在标签
    private static Elements getIPElements(Document document, SiteTemplateInfo siteTemplateInfo) {
        String tableInfo = siteTemplateInfo.getHtmlLabelInfo1().getLabelName();
        String tbodyInfo = siteTemplateInfo.getHtmlLabelInfo3().getLabelName();

        if (!siteTemplateInfo.getHtmlLabelInfo1().getAttributeName().equals("null")) {
            tableInfo += "[" + siteTemplateInfo.getHtmlLabelInfo1().getAttributeName() + "=" +
                    siteTemplateInfo.getHtmlLabelInfo1().getAttributeValue() + "]";
        }
        if (!siteTemplateInfo.getHtmlLabelInfo3().getAttributeName().equals("null")) {
            tbodyInfo += "[" + siteTemplateInfo.getHtmlLabelInfo3().getAttributeName() + "=" +
                    siteTemplateInfo.getHtmlLabelInfo3().getAttributeValue() + "]";
        }

        return document.select(tableInfo).select(tbodyInfo).select("tr");
    }

    // 无表格头时移除第一个tr标签
    private static Elements removeFirstElement(Elements trs) {
        Elements newTrs = new Elements();

        for (int i = 1; i < trs.size(); i++) {
            newTrs.add(trs.get(i));
        }

        return newTrs;
    }

    // 根据ip信息所在索引列获得ip信息
    private static List<IPMessage> getIPMessageByIPMessageIndex(Elements trs, List<Integer> ipMessageIndex) {
        if (ipMessageIndex.size() == 1) {
            return getIPMessagesWhenIndexSame(trs, ipMessageIndex);
        }

        return getIPMessagesWhenIndexDiff(trs, ipMessageIndex);
    }

    // ip地址与端口在表格同列
    private static List<IPMessage> getIPMessagesWhenIndexSame(Elements trs, List<Integer> ipMessageIndex) {
        List<IPMessage> ipMessages = new LinkedList<>();

        for (Element tr : trs) {
            IPMessage ipMessage = new IPMessage();

            String ipAddressAndIPPort = tr.select("td").get(ipMessageIndex.get(0)).text();
            String ipAddress = ipAddressAndIPPort.substring(0, ipAddressAndIPPort.indexOf(':'));
            String ipPort = ipAddressAndIPPort.substring(ipAddressAndIPPort.indexOf(':') + 1,
                    ipAddressAndIPPort.length());

            ipMessage.setIpAddress(ipAddress);
            ipMessage.setIpPort(ipPort);

            ipMessages.add(ipMessage);
        }

        return ipMessages;
    }

    // ip地址与端口在表格不同列
    private static List<IPMessage> getIPMessagesWhenIndexDiff(Elements trs, List<Integer> ipMessageIndex) {
        List<IPMessage> ipMessages = new LinkedList<>();

        for (Element tr : trs) {
            IPMessage ipMessage = new IPMessage();

            String ipAddress = tr.select("td").get(ipMessageIndex.get(0)).text();
            String ipPort = tr.select("td").get(ipMessageIndex.get(1)).text();

            ipMessage.setIpAddress(ipAddress);
            ipMessage.setIpPort(ipPort);

            ipMessages.add(ipMessage);
        }

        return ipMessages;
    }
}
