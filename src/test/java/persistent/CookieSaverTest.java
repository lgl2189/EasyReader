package persistent;

import com.reader.util.CookieSaver;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Set;

/**
 * @author      ：李冠良
 * @description ：无描述

 * @date        ：2025 2月 17 23:25
 */


public class CookieSaverTest {
    public static void main(String[] args) throws Exception {
        easyTest();
        seleniumTest();
        apacheTest();
    }

    public static void easyTest()throws Exception{
        BasicCookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(new BasicClientCookie("name1", "value1"));
        cookieStore.addCookie(new BasicClientCookie("name2", "value2"));
        cookieStore.addCookie(new BasicClientCookie("name3", "value3"));
        CookieSaver.save(cookieStore, "test1","C:\\Users\\12145\\Desktop\\新建文件夹");
        CookieStore cookieStore2 = CookieSaver.loadAsCookieStore("test1","C:\\Users\\12145\\Desktop\\新建文件夹");
        System.out.println(cookieStore2.getCookies());
    }

    public static void apacheTest()throws Exception{
        // 创建HttpClient实例
        BasicCookieStore cookieStore = new BasicCookieStore();
        try(CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build()){
            // 导航到指定的URL
            HttpGet httpGet = new HttpGet("https://www.bilibili.com");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.6156.2 Safari/537.36");
            httpClient.execute(httpGet);
            System.out.println(cookieStore.getCookies());
            CookieSaver.save(cookieStore, "test3","C:\\Users\\12145\\Desktop\\新建文件夹");
            System.out.println(CookieSaver.loadAsCookieStore("test3","C:\\Users\\12145\\Desktop\\新建文件夹"));
        }
    }

    public static void seleniumTest()throws Exception{
        // 打开浏览器
        System.setProperty("webdriver.chrome.driver", "D:\\Program Tools\\ChromeDriver121.0.6156.2\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setBinary("D:\\Program Tools\\Chrome121.0.6156.2\\chrome.exe");
        WebDriver driver = new ChromeDriver(options);
        try {
            // 导航到指定的URL
            driver.get("https://www.bilibili.com");
            Set<Cookie> cookieSet = driver.manage().getCookies();
            System.out.println(cookieSet);
            CookieSaver.save(cookieSet, "test2","C:\\Users\\12145\\Desktop\\新建文件夹");
        } finally {
            driver.quit();
        }
        System.out.println(CookieSaver.loadAsCookieSet("test2", "C:\\Users\\12145\\Desktop\\新建文件夹"));
    }
}