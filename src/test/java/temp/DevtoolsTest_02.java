package temp;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.ConverterFunctions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.Event;
import org.openqa.selenium.devtools.v121.page.Page;
import org.openqa.selenium.devtools.v121.target.Target;
import org.openqa.selenium.devtools.v121.target.model.TargetInfo;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DevtoolsTest_02 {
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

    private static final Set<String> windowHandles = ConcurrentHashMap.newKeySet();
    private static final Map<String, DevTools> devToolsMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        DevTools devTools = CHROME_DRIVER.getDevTools();
        devTools.createSession();
        devTools.send(Page.enable());
        devTools.send(Target.setDiscoverTargets(true, Optional.empty()));
        devTools.send(Target.setAutoAttach(true, false, Optional.of(true), Optional.empty()));

        devTools.addListener(Page.loadEventFired(), loadEvent -> {
            System.out.println("Page loaded: " + loadEvent);
            safeInjectScripts(CHROME_DRIVER);
        });

        devTools.addListener(Target.targetCreated(), targetInfo -> {
            System.out.println("Target created: " + targetInfo);
            handleNewTarget(CHROME_DRIVER);
        });

        devTools.addListener(Target.targetDestroyed(), targetDestroyed -> {
            String targetId = targetDestroyed.toString();
            System.out.println("Target destroyed: " + targetId);
            windowHandles.removeIf(handle -> {
                try {
                    return !CHROME_DRIVER.getWindowHandles().contains(handle);
                } catch (WebDriverException e) {
                    return true;
                }
            });
        });

        CHROME_DRIVER.get("https://www.baidu.com");
        windowHandles.addAll(CHROME_DRIVER.getWindowHandles());
        devToolsMap.put(CHROME_DRIVER.getWindowHandle(), devTools);

        startDoubleClickListener(CHROME_DRIVER);
        Thread.sleep(1000000);
        CHROME_DRIVER.quit();
    }

    private static void handleNewTarget(ChromeDriver driver) {
        Set<String> newHandles = new HashSet<>(driver.getWindowHandles());
        newHandles.removeAll(windowHandles);
        newHandles.forEach(handle -> {
            try {
                CHROME_DRIVER.switchTo().window(handle);
                DevTools newDevTools = driver.getDevTools();
                newDevTools.createSession();
                newDevTools.send(Page.enable());
                safeInjectScripts(driver);
                windowHandles.add(handle);
                devToolsMap.put(handle, newDevTools);
            } catch (NoSuchWindowException e) {
                System.err.println("Window already closed: " + handle);
            }
        });
    }

    private static void startDoubleClickListener(ChromeDriver driver) {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (driver) {
                        if (isDriverDead(driver)) {
                            System.out.println("Driver disconnected, exiting listener.");
                            System.exit(0);
                            break;
                        }
                        checkResultHolder(driver);
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (WebDriverException e) {
                    if (e.getMessage().contains("disconnected")) {
                        System.out.println("Driver connection lost, exiting listener.");
                        break;
                    }
                    System.err.println("WebDriver error: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error checking holder: " + e.getMessage());
                }
            }
        }).start();
    }

    private static boolean isDriverDead(WebDriver driver) {
        try {
            driver.getWindowHandles();
            return false;
        } catch (WebDriverException e) {
            return true;
        }
    }

    private static void checkResultHolder(WebDriver driver) {
        try {
            Object result = ((JavascriptExecutor) driver).executeScript(
                    "var holder = document.getElementById('selenium_result_holder');" +
                            "return holder ? holder.textContent : null;");
            if (result instanceof String && !((String) result).isEmpty()) {
                System.out.println("Double clicked: " + result);
                ((JavascriptExecutor) driver).executeScript(
                        "var holder = document.getElementById('selenium_result_holder');" +
                                "if(holder) holder.textContent = '';");
            }
        } catch (JavascriptException e) {
            System.err.println("JavaScript execution failed: " + e.getMessage());
        }
    }

    private static void safeInjectScripts(WebDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
            ((JavascriptExecutor) driver).executeScript(createInjectionScript());
        } catch (WebDriverException e) {
            System.err.println("Inject failed - window closed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Inject failed: " + e.getMessage());
        }
    }

    private static String createInjectionScript() {
        return "var styleNode = document.createElement('style');" +
                "styleNode.textContent = '.highlight { border: 2px solid black; }';" +
                "document.head.appendChild(styleNode);" +
                "var resultHolder = document.createElement('div');" +
                "resultHolder.id = 'selenium_result_holder';" +
                "resultHolder.style.display = 'none';" +
                "document.body.appendChild(resultHolder);" +
                "document.addEventListener('mouseover', function(e){" +
                "   document.querySelectorAll('.highlight').forEach(el => el.classList.remove('highlight'));" +
                "   e.target.classList.add('highlight');" +
                "});" +
                "document.addEventListener('dblclick', function(e){" +
                "   document.getElementById('selenium_result_holder').textContent = e.target.outerHTML;" +
                "});";
    }

    public static Event<TargetInfo> activateTarget() {
        return new Event<>("Target.activateTarget", ConverterFunctions.map("targetInfo", org.openqa.selenium.devtools.v121.target.model.TargetInfo.class));
    }
}