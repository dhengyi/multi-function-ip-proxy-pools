package ip.proxy.pool.api;

import ip.proxy.pool.gentemplate.GenEntry;
import ip.proxy.pool.utilclass.ParamValidateUtil;
import org.dom4j.Document;

/**
 * @author dhengyi
 * @create 2019/05/05 22:54
 * @description 解析模板生成类--api接口
 */

public class SiteTemplate {

    public static void genTemplate() {
        genTemplate(1);
    }

    public static void genTemplate(Integer number) {
        // 参数校验
        if (!ParamValidateUtil.validateRange(1, 5, number)) {
            throw new RuntimeException();
        }

        Document doc = GenEntry.genTemplate(number);
        GenEntry.saveDocument(doc);
    }
}
