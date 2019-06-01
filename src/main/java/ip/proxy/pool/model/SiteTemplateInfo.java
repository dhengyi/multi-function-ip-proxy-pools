package ip.proxy.pool.model;

/**
 * @author dhengyi
 * @create 2019/05/06 10:39
 * @description 网站解析模板--example-template.xml，所对应的Java Bean
 */

public class SiteTemplateInfo {

    private String url;
    private HtmlLabelInfo htmlLabelInfo1;
    private HtmlLabelInfo htmlLabelInfo2;
    private HtmlLabelInfo htmlLabelInfo3;

    public SiteTemplateInfo() {
    }

    public SiteTemplateInfo(String url, HtmlLabelInfo htmlLabelInfo1, HtmlLabelInfo htmlLabelInfo2,
                            HtmlLabelInfo htmlLabelInfo3) {
        this.url = url;
        this.htmlLabelInfo1 = htmlLabelInfo1;
        this.htmlLabelInfo2 = htmlLabelInfo2;
        this.htmlLabelInfo3 = htmlLabelInfo3;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HtmlLabelInfo getHtmlLabelInfo1() {
        return htmlLabelInfo1;
    }

    public void setHtmlLabelInfo1(HtmlLabelInfo htmlLabelInfo1) {
        this.htmlLabelInfo1 = htmlLabelInfo1;
    }

    public HtmlLabelInfo getHtmlLabelInfo2() {
        return htmlLabelInfo2;
    }

    public void setHtmlLabelInfo2(HtmlLabelInfo htmlLabelInfo2) {
        this.htmlLabelInfo2 = htmlLabelInfo2;
    }

    public HtmlLabelInfo getHtmlLabelInfo3() {
        return htmlLabelInfo3;
    }

    public void setHtmlLabelInfo3(HtmlLabelInfo htmlLabelInfo3) {
        this.htmlLabelInfo3 = htmlLabelInfo3;
    }

    @Override
    public String toString() {
        return "SiteTemplateInfo{" +
                "url='" + url + '\'' +
                ", htmlLabelInfo1=" + htmlLabelInfo1 +
                ", htmlLabelInfo2=" + htmlLabelInfo2 +
                ", htmlLabelInfo3=" + htmlLabelInfo3 +
                '}';
    }
}
