package com.reader.access;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 2月 26 15:57
 */


public class AccessXpath {

    private static final String CHROME_DRIVER_PATH;
    private static final String CHROME_PATH;
    private static final ChromeOptions CHROME_OPTIONS;
    private static final Logger log = LoggerFactory.getLogger(AccessXpath.class);

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

    private final List<String> url;
    private String xpath;

    public AccessXpath(String url) {
        this.url = new ArrayList<String>();
        this.url.add(url);
    }

    public AccessXpath(List<String> url) {
        this.url = url;
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

    public void selectElement(){
        
    }

    public String getXpath() {
        return xpath;
    }
}