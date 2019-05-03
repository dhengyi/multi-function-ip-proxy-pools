package ip.proxy.pool.gentemplate;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author dhengyi
 * @create 2019/05/03 23:39
 * @description 解析模板生成类
 */

public class GenEntry {

    public static void main(String[] args) {
        Integer number = Integer.valueOf(args[1]);

        // 输入参数校验

        // 生成模板
        genTemplate(number);
    }

    public static void genTemplate(Integer number) {
        Document doc = DocumentHelper.createDocument();
        // 添加根节点
        Element config = doc.addElement("config");

        Element ips = config.addElement("ips");
        for (int i = 0; i < number; i++) {
            Element ip = ips.addElement("ip");
        }

        for (int i = 1; i <= number; i++) {
            Element dom = config.addElement("dom_" + i);
            for (int j = 1; j <= 3; j++) {
                Element label = dom.addElement("label_" + j);
                Element labelName = label.addElement("name");
                Element labelAttribute = label.addElement("attribute");
                Element attributeName = labelAttribute.addElement("name");
                Element attributeValue = labelAttribute.addElement("value");
            }
        }
    }
}
