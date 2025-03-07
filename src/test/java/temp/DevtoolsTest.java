package temp;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v121.page.Page;
import org.openqa.selenium.devtools.v121.target.Target;

import java.io.File;
import java.util.Optional;
import java.util.Set;
/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 2月 26 15:47
 */
public class DevtoolsTest {
    private static final String CHROME_DRIVER_PATH;
    private static final String CHROME_PATH;
    private static final ChromeOptions CHROME_OPTIONS;

    static {
        CHROME_DRIVER_PATH = System.getProperty("user.dir")
                + File.separator + "lib"
                + File.separator + "selenium"
                + File.separator + "ChromeDriver121.0.6156.2"
                + File.separator + "chromedriver.exe";
        CHROME_PATH = System.getProperty("user.dir")
                + File.separator + "lib"
                + File.separator + "selenium"
                + File.separator + "Chrome121.0.6156.2"
                + File.separator + "chrome.exe";
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        CHROME_OPTIONS = new ChromeOptions();
        CHROME_OPTIONS.setBinary(CHROME_PATH);
    }

    private static Set<String> windowHandles;

    public static void main(String[] args) throws InterruptedException {
        // 创建一个新的WebDriver实例
        ChromeDriver driver = new ChromeDriver(CHROME_OPTIONS);
        // 获取DevTools对象
        DevTools devTools = driver.getDevTools();
        // 创建并连接到DevTools session
        devTools.createSession();
        // 注册监听页面加载完成事件并在页面加载时注入JavaScript
        devTools.send(Page.enable());
        devTools.send(Target.setDiscoverTargets(true, Optional.empty()));
        devTools.send(Target.setAutoAttach(true, false,
                Optional.of(true), Optional.empty()));
        devTools.addListener(Page.loadEventFired(), logEntry -> {
            System.out.println("Page loaded: " + logEntry);
            injectScripts(driver);
        });
        devTools.addListener(Target.targetCreated(), targetInfo -> {
            System.out.println("Target created: " + targetInfo);
            Set<String> newWindowHandles = driver.getWindowHandles();
            for (String handle : newWindowHandles){
                if(!windowHandles.contains(handle)){
                    windowHandles.add(handle);
                    driver.switchTo().window(handle);
                    devTools.send(Page.enable());
                }
            }
        });
        // 导航到指定URL
        driver.get("https://www.baidu.com"); // 替换为你的目标网址
        //监听函数
        listenerDoubleClick(driver);
        windowHandles = driver.getWindowHandles();
        // 等待一段时间以便观察效果
        Thread.sleep(1000000);
        // 关闭浏览器
        driver.quit();
    }

    private static void listenerDoubleClick(WebDriver driver) {
        // 在main方法或者其他适当位置添加如下代码
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100); // 每秒检查一次
                    Object result = ((JavascriptExecutor) driver).executeScript(
                            "var holder = document.getElementById('selenium_result_holder');" +
                                    "return holder ? holder.textContent : null;"
                    );
                    if (result instanceof String && !((String) result).isEmpty()) {
                        System.out.println("Double clicked element HTML: " + result);
                        // 清空内容以避免重复打印
                        ((JavascriptExecutor) driver).executeScript(
                                "var holder = document.getElementById('selenium_result_holder'); if(holder) holder.textContent = '';"
                        );
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private static String createInjectionScript() {
        // 注入JavaScript代码来监听鼠标悬浮、双击事件，并保存双击结果到隐藏DOM元素
        return "var styleNode = document.createElement('style');" +
                "styleNode.type = 'text/css';" +
                "styleNode.innerHTML = '.highlight { border: 2px solid black; }';" +
                "document.getElementsByTagName('head')[0].appendChild(styleNode);" +
                "var resultHolder = document.createElement('div');" +
                "resultHolder.id = 'selenium_result_holder';" +
                "resultHolder.style.display = 'none';" +
                "document.body.appendChild(resultHolder);" +
                "document.addEventListener('mouseover', function(e){ var elems = document.querySelectorAll('.highlight'); [].forEach.call(elems, function(el) { el.classList.remove('highlight'); }); e.target.classList.add('highlight'); });" +
                "document.addEventListener('dblclick', function(e){ window.highlightedElementText = e.target.outerHTML; document.getElementById('selenium_result_holder').textContent = window.highlightedElementText; });";
    }

    //注入函数
    private static void injectScripts(WebDriver driver) {
        String script = createInjectionScript();
        ((JavascriptExecutor) driver).executeScript(script);
    }
}