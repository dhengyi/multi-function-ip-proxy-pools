package ip.proxy.pool.sitetemplate;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author dhengyi
 * @create 2019/05/03 23:39
 * @description 模板生成类
 */

public class GenEntry {

    // 生成模板内容
    public static Document genTemplate(Integer number) {
        Document doc = DocumentHelper.createDocument();
        // 添加根节点
        Element config = doc.addElement("config");

        Element ips = config.addElement("ips");
        ips.addAttribute("number", String.valueOf(number));
        ips.addComment(" 用户设定的待抓取网站ip地址，number值表示本次并发抓取的网站数，最小/默认为1，最大为5 ");
        for (int i = 0; i < number; i++) {
            Element ip = ips.addElement("ip");
            ip.setText("{ip地址}");
            if (i == 0) {
                ip.addComment(" ip地址中需要包含页码占位符，后端需要重新构造url，方便抓取更多代理ip ");
            }
        }

        for (int i = 1; i <= number; i++) {
            Element dom = config.addElement("dom_" + i);
            if (i == 1) {
                dom.addComment(" 根据ips中number的值生成相应数量的dom结构，dom_1代表第一个ip地址对应的网页结构，依次类推 ");
            }
            for (int j = 1; j <= 3; j++) {
                Element label = dom.addElement("label_" + j);
                if (j == 1) {
                    label.addComment(" 标签1，通常代表table标签 ");
                } else if (j == 2) {
                    label.addComment(" 标签2，通常代表thead标签，表示表格头 ");
                } else {
                    label.addComment(" 标签3，通常代表tbody标签，表示表格数据，当thead为null时，表格头通常包含在tbody中 ");
                }
                Element labelName = label.addElement("name");
                labelName.setText("{标签名}");
                if (j == 2) {
                    labelName.addComment(" 可以指定为null，表示无表格头 ");
                }
                Element labelAttribute = label.addElement("attribute");
                Element attributeName = labelAttribute.addElement("name");
                attributeName.setText("{属性名}");
                attributeName.addComment(" 可以指定为null，表示无属性 ");
                Element attributeValue = labelAttribute.addElement("value");
                attributeValue.setText("{属性值}");
            }
        }

        return doc;
    }

    // 生成xml文件
    public static void saveDocument(Document doc) {
        String rootPath = System.getProperty("user.dir");
        Path dirPath = Paths.get(rootPath + "/template");
        Path filePath = Paths.get(dirPath.toString() + "/config.xml");

        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);

            // 美化格式
            OutputFormat format = OutputFormat.createCompactFormat();
            // 指定XML编码
            format.setEncoding("UTF-8");
            XMLWriter output = new XMLWriter(new FileWriter(filePath.toString()), format);
            output.write(doc);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
