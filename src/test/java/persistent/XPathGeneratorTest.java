package persistent;

import com.reader.net.XPathGenerator;

import java.util.List;

public class XPathGeneratorTest {
    public static void main(String[] args) {
        String url = "https://www.baidu.com";
        List<String> xpathList = XPathGenerator.getXPathListForUrl(url);
        System.out.println("Final XPath List Length: " + xpathList.size());
        System.out.println("Final XPath List:\n: " + xpathList);
    }
}