package temp;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class HighlightTest {
    public static void main(String[] args) throws InterruptedException {
        // 设置ChromeDriver的路径
        System.setProperty("webdriver.chrome.driver", "D:\\Program Tools\\ChromeDriver121.0.6156.2\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setBinary("D:\\Program Tools\\Chrome121.0.6156.2\\chrome.exe");

        // 创建一个新的WebDriver实例
        WebDriver driver = new ChromeDriver(options);

        // 导航到指定URL
        driver.get("file:///C:/Users/12145/Desktop/EasyReader/2.html"); // 替换为你的目标网址

        // 添加一个隐藏的input元素用于存储元素文本
        WebElement hiddenInput = driver.findElement(By.tagName("body"));
        ((JavascriptExecutor) driver).executeScript(
                "var input = document.createElement('input'); " +
                        "input.type = 'hidden'; " +
                        "input.id = 'highlightedElementText'; " +
                        "document.body.appendChild(input);");

        // 注入JavaScript代码来监听鼠标悬浮事件
        String script =
                "var styleNode = document.createElement('style');" +
                        "styleNode.type = 'text/css';" +
                        "styleNode.innerHTML = '.highlight { border: 2px solid black; }';" +
                        "document.getElementsByTagName('head')[0].appendChild(styleNode);" +
                        "document.addEventListener('mouseover', function(e){" +
                        "   var elems = document.querySelectorAll('.highlight');" +
                        "   [].forEach.call(elems, function(el) {" +
                        "       el.classList.remove('highlight');" +
                        "   });" +
                        "   e.target.classList.add('highlight');" +
                        "   document.getElementById('highlightedElementText').value = e.target.outerHTML;" + // 更新隐藏input的值
                        "});";

        ((JavascriptExecutor) driver).executeScript(script);

        // 监听并打印高亮元素的内容
        while (true) {
            Thread.sleep(100); // 简单的轮询机制，等待用户交互
            String highlightedElementText = (String) ((JavascriptExecutor) driver)
                    .executeScript("return document.getElementById('highlightedElementText').value;");
            if (highlightedElementText != null && !highlightedElementText.isEmpty()) {
                System.out.println("Highlighted element content: " + highlightedElementText);
                // 清除内容以便下次可以正确检测
                ((JavascriptExecutor) driver).executeScript("document.getElementById('highlightedElementText').value = '';");
            }
        }

        // 注意：这里的driver.quit()不会被调用，因为程序将无限循环以保持监听状态。
        // 若要退出程序，需要手动停止运行。
    }
}