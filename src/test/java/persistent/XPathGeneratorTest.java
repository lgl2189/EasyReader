package persistent;

import com.reader.net.XPathGenerator;

public class XPathGeneratorTest {
    public static void main(String[] args) {
        String url = "https://www.baidu.com";
        String xpath = XPathGenerator.getXPathForUrl(url);
        System.out.println("Final XPath: " + xpath);
    }
}