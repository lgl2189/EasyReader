package temp;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v121.page.Page;
import org.openqa.selenium.devtools.v121.runtime.Runtime;
import org.openqa.selenium.devtools.v121.runtime.model.RemoteObject;
import org.openqa.selenium.devtools.v121.target.Target;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DevtoolsTest_03 {
    private static final String CHROME_DRIVER_PATH;
    private static final String CHROME_PATH;
    private static final ChromeOptions CHROME_OPTIONS;
    private static final ChromeDriver CHROME_DRIVER;

    static {
        CHROME_DRIVER_PATH = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "selenium" + File.separator + "ChromeDriver121.0.6156.2" + File.separator + "chromedriver.exe";
        CHROME_PATH = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "selenium" + File.separator + "Chrome121.0.6156.2" + File.separator + "chrome.exe";
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        CHROME_OPTIONS = new ChromeOptions();
        CHROME_OPTIONS.setBinary(CHROME_PATH);
        CHROME_DRIVER = new ChromeDriver(CHROME_OPTIONS);
    }

    private static final Set<String> windowHandles = ConcurrentHashMap.newKeySet();
    private static final Map<String, DevTools> devToolsMap = new ConcurrentHashMap<>();
    private static final Map<String, DevTools> targetDevToolsMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        DevTools mainDevTools = CHROME_DRIVER.getDevTools();
        mainDevTools.createSession();

        // 初始化 CDP 监听
        initializeCDPListeners(mainDevTools);

        // 启动后台监听线程
        startBackgroundListener();

        CHROME_DRIVER.get("https://www.baidu.com");
        Thread.sleep(1000000);
        CHROME_DRIVER.quit();
    }

    private static void initializeCDPListeners(DevTools devTools) {
        // 启用必要模块
        devTools.send(Page.enable());
        devTools.send(Runtime.enable());
        devTools.send(Target.setDiscoverTargets(true,Optional.empty()));

        // 监听目标创建
        devTools.addListener(Target.targetCreated(), targetCreated -> {
            String targetId = targetCreated.getTargetId().toString();
            System.out.println("New target created: " + targetId);

            // 附加到新目标
            devTools.send(Target.attachToTarget( targetCreated.getTargetId(), Optional.of(true)));

            // 创建独立 DevTools 会话
            DevTools targetDevTools = CHROME_DRIVER.getDevTools();
            targetDevTools.createSession();
            targetDevToolsMap.put(targetId, targetDevTools);

            // 初始化新标签页
            setupTargetDevTools(targetDevTools, targetId);
        });

        // 监听目标销毁
        devTools.addListener(Target.targetDestroyed(), targetDestroyed -> {
            String closedId = targetDestroyed.toString();
            System.out.println("Target destroyed: " + closedId);
            DevTools closedDevTools = targetDevToolsMap.remove(closedId);
            if (closedDevTools != null) closedDevTools.close();
        });
    }

    private static void setupTargetDevTools(DevTools devTools, String targetId) {
        // 公共配置
        devTools.send(Page.enable());
        devTools.send(Runtime.enable());

        // 预注入脚本
        devTools.send(Page.addScriptToEvaluateOnNewDocument(createInjectionScript(),
                Optional.empty(), Optional.empty(), Optional.empty()));

        // 页面加载监听
        devTools.addListener(Page.loadEventFired(), loadEvent -> {
            System.out.println("Target [" + targetId + "] loaded");
            injectRuntimeScript(devTools);
        });
    }

    private static void injectRuntimeScript(DevTools devTools) {
        // 动态注入运行时脚本
        devTools.send(Runtime.evaluate(
                createInjectionScript(),
                Optional.empty(),  // objectGroup
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(true), // returnByValue
                Optional.empty(),
                Optional.of(true), // userGesture
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        ));
    }

    private static void startBackgroundListener() {
        new Thread(() -> {
            while (true) {
                try {
                    checkAllTargets();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Background error: " + e.getMessage());
                }
            }
        }).start();
    }

    private static void checkAllTargets() {
        targetDevToolsMap.forEach((targetId, devTools) -> {
            try {
                // 执行检查脚本
                Runtime.EvaluateResponse response = devTools.send(Runtime.evaluate(
                        "document.getElementById('selenium_result_holder')?.textContent || ''",
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(true),
                        Optional.empty(),
                        Optional.of(true),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                ));

                handleEvaluationResult(response, targetId, devTools);
            } catch (Exception e) {
                System.err.println("Error checking target [" + targetId + "]: " + e.getMessage());
            }
        });
    }

    private static void handleEvaluationResult(Runtime.EvaluateResponse response, String targetId, DevTools devTools) {
        if (response == null || response.getResult() == null) return;

        RemoteObject result = response.getResult();
        if (result.getValue().isPresent()) {
            String text = result.getValue().get().toString();
            if (!text.isEmpty()) {
                System.out.println("Target [" + targetId + "] double click: " + text);
                clearResultHolder(devTools);
            }
        }
    }

    private static void clearResultHolder(DevTools devTools) {
        try {
            devTools.send(Runtime.evaluate(
                    "document.getElementById('selenium_result_holder').textContent = ''",
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(true),
                    Optional.empty(),
                    Optional.of(true),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            ));
        } catch (Exception e) {
            System.err.println("Clear result failed: " + e.getMessage());
        }
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
            }
            catch (NoSuchWindowException e) {
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
                        checkResultHolder();
                    }
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                catch (WebDriverException e) {
                    if (e.getMessage().contains("disconnected")) {
                        System.out.println("Driver connection lost, exiting listener.");
                        break;
                    }
                    System.err.println("WebDriver error: " + e.getMessage());
                }
                catch (Exception e) {
                    System.err.println("Error checking holder: " + e.getMessage());
                }
            }
        }).start();
    }

    private static boolean isDriverDead(WebDriver driver) {
        try {
            driver.getWindowHandles();
            return false;
        }
        catch (WebDriverException e) {
            return true;
        }
    }

    private static void checkResultHolder() {
        devToolsMap.forEach((windowHandle, devTools) -> {
            try {
                // 使用新版 API 调用方式
                Runtime.EvaluateResponse result = devTools.send(Runtime.evaluate("var holder = document.getElementById('selenium_result_holder');" + "holder ? holder.textContent : '';",
                        Optional.empty(),  // objectGroup
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(true),
                        Optional.empty(),
                        Optional.of(true),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                ));

                // 处理结果
                RemoteObject remoteObject = result.getResult();
                if (remoteObject != null && remoteObject.getValue().isPresent()) {
                    String text = remoteObject.getValue().get().toString();
                    if (!text.isEmpty()) {
                        System.out.println("Double clicked (CDP): " + text);
                        clearResultHolderViaCDP(devTools);
                    }
                }
            }
            catch (Exception e) {
                System.err.println("CDP command error: " + e.getMessage());
            }
        });
    }

    private static void clearResultHolderViaCDP(DevTools devTools) {
        try {
            devTools.send(Runtime.evaluate("var holder = document.getElementById('selenium_result_holder');" + "if(holder) holder.textContent = '';",
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(true),
                    Optional.empty(),
                    Optional.of(true),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));
        }
        catch (Exception e) {
            System.err.println("Error clearing holder via CDP: " + e.getMessage());
        }
    }

    private static void safeInjectScripts(WebDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
            ((JavascriptExecutor) driver).executeScript(createInjectionScript());
        }
        catch (WebDriverException e) {
            System.err.println("Inject failed - window closed: " + e.getMessage());
        }
        catch (Exception e) {
            System.err.println("Inject failed: " + e.getMessage());
        }
    }

    private static String createInjectionScript() {
        return "var styleNode = document.createElement('style');" + "styleNode.textContent = '.highlight { border: 2px solid black; }';" + "document.head.appendChild(styleNode);" + "var resultHolder = document.createElement('div');" + "resultHolder.id = 'selenium_result_holder';" + "resultHolder.style.display = 'none';" + "document.body.appendChild(resultHolder);" + "document.addEventListener('mouseover', function(e){" + "   document.querySelectorAll('.highlight').forEach(el => el.classList.remove('highlight'));" + "   e.target.classList.add('highlight');" + "});" + "document.addEventListener('dblclick', function(e){" + "   document.getElementById('selenium_result_holder').textContent = e.target.outerHTML;" + "});";
    }

}