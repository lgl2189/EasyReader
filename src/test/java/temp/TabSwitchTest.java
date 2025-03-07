package temp;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v121.page.Page;
import org.openqa.selenium.devtools.v121.target.Target;

import java.io.File;
import java.util.Optional;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 03 11:45
 */


public class TabSwitchTest {

    private static final String CHROME_DRIVER_PATH;
    private static final String CHROME_PATH;
    private static final ChromeOptions CHROME_OPTIONS;
    private static final ChromeDriver CHROME_DRIVER;

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
        CHROME_DRIVER = new ChromeDriver(CHROME_OPTIONS);
    }

    public static void main(String[] args) throws InterruptedException {
        DevTools devTools = CHROME_DRIVER.getDevTools();
        devTools.createSession();
        devTools.send(Page.enable());
        devTools.send(Target.setDiscoverTargets(true, Optional.empty()));
        devTools.send(Target.setAutoAttach(true, false, Optional.of(true), Optional.empty()));

        devTools.addListener(Target.targetCreated(), event -> {
            System.out.println("Target Created: " + event.getTargetId());
        });

        devTools.addListener(Target.targetInfoChanged(), event -> {
            System.out.println("Target IfoChanged: " + event.getTargetId());
        });

        devTools.addListener(Target.targetDestroyed(), event -> {
            System.out.println("Target Destroyed: " + event.toString());
        });

        CHROME_DRIVER.get("https://www.baidu.com");

        new Thread(() -> {
            String lastHandle = CHROME_DRIVER.getWindowHandle();
            while (true) {
                try {
                    Thread.sleep(100); // 每隔500毫秒检查一次
                    String currentHandle = CHROME_DRIVER.getWindowHandle();
//                    System.out.println("当前标签页: " + currentHandle);
                    if (!lastHandle.equals(currentHandle)) {
                        System.out.println("标签页已切换到: " + currentHandle);
                        lastHandle = currentHandle;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();


        Thread.sleep(1000000);
        CHROME_DRIVER.quit();
    }

}