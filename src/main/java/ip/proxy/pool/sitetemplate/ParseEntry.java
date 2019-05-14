package ip.proxy.pool.sitetemplate;

import ip.proxy.pool.model.HtmlLabelInfo;
import ip.proxy.pool.model.SiteTemplateInfo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dhengyi
 * @create 2019/05/06 10:55
 * @description 模板解析类--将xml文件中的数据转化为Java Bean
 */

public class ParseEntry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseEntry.class);

    // 解析模板
    public static List<SiteTemplateInfo> parseTemplate() {
        Document doc = getDocument();

        return getSiteTemplateInfos(doc);
    }

    // 读取xml文件
    private static Document getDocument() {
        String filePath = System.getProperty("user.dir") + "/template/config.xml";

        // 如果没有指定配置文件，则使用默认策略进行抓取
        if (!Files.exists(Paths.get(filePath))) {
            return null;
        }

        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(filePath));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return doc;
    }

    // 将xml文件中的数据存储为Java Bean
    private static List<SiteTemplateInfo> getSiteTemplateInfos(Document doc) {
        List<SiteTemplateInfo> siteTemplateInfos = new ArrayList<>();

        if (doc == null) {
            siteTemplateInfos.add(getDefaultSiteTemplateInfo());
            return siteTemplateInfos;
        }

        Element root = doc.getRootElement();
        Element ips = root.element("ips");
        Integer number = Integer.valueOf(ips.attributeValue("number"));

        List<Element> ipElements = ips.elements();
        // subList注意为左闭右开，得到的是一个视图，只能读不能写
        List<Element> domElements = root.elements().subList(1, number + 1);

        for (int i = 0; i < number; i++) {
            String url = ipElements.get(i).getText();
            Element domElement = domElements.get(i);

            siteTemplateInfos.add(getSiteTemplateInfo(url, domElement));
        }

        return siteTemplateInfos;
    }

    private static SiteTemplateInfo getSiteTemplateInfo(String url, Element domElement) {
        if (StringUtils.isEmpty(url) || domElement == null) {
            return null;
        }

        String label1Name = domElement.element("label_1").elementText("name");
        String attribute1Name = domElement.element("label_1").element("attribute").elementText("name");
        String attribute1Value = domElement.element("label_1").element("attribute").elementText("value");
        HtmlLabelInfo htmlLabelInfo1 = new HtmlLabelInfo(label1Name, attribute1Name, attribute1Value);

        String label2Name = domElement.element("label_2").elementText("name");
        String attribute2Name = domElement.element("label_2").element("attribute").elementText("name");
        String attribute2Value =domElement.element("label_2").element("attribute").elementText("value");
        HtmlLabelInfo htmlLabelInfo2 = new HtmlLabelInfo(label2Name, attribute2Name, attribute2Value);

        String label3Name = domElement.element("label_3").elementText("name");
        String attribute3Name = domElement.element("label_3").element("attribute").elementText("name");
        String attribute3Value = domElement.element("label_3").element("attribute").elementText("value");
        HtmlLabelInfo htmlLabelInfo3 = new HtmlLabelInfo(label3Name, attribute3Name, attribute3Value);

        return new SiteTemplateInfo(url, htmlLabelInfo1, htmlLabelInfo2, htmlLabelInfo3);
    }

    // 生成默认策略
    private static SiteTemplateInfo getDefaultSiteTemplateInfo() {
        String url = "https://www.xicidaili.com/nn/{}";

        String label1Name = "table";
        String attribute1Name = "id";
        String attribute1Value = "ip_list";
        HtmlLabelInfo htmlLabelInfo1 = new HtmlLabelInfo(label1Name, attribute1Name, attribute1Value);

        String label2Name = "null";
        String attribute2Name = "";
        String attribute2Value ="";
        HtmlLabelInfo htmlLabelInfo2 = new HtmlLabelInfo(label2Name, attribute2Name, attribute2Value);

        String label3Name = "tbody";
        String attribute3Name = "null";
        String attribute3Value = "";
        HtmlLabelInfo htmlLabelInfo3 = new HtmlLabelInfo(label3Name, attribute3Name, attribute3Value);

        return new SiteTemplateInfo(url, htmlLabelInfo1, htmlLabelInfo2, htmlLabelInfo3);
    }
}
