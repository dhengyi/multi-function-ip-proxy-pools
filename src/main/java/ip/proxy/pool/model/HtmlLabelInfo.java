package ip.proxy.pool.model;

/**
 * @author dhengyi
 * @create 2019/05/06 10:42
 * @description Java Bean--Html中的标签信息
 */

public class HtmlLabelInfo {

    private String labelName;
    private String attributeName;
    private String attributeValue;

    public HtmlLabelInfo() {
    }

    public HtmlLabelInfo(String labelName, String attributeName, String attributeValue) {
        this.labelName = labelName;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        return "HtmlLabelInfo{" +
                "labelName='" + labelName + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", attributeValue='" + attributeValue + '\'' +
                '}';
    }
}
