package com.reader.net;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author      ：李冠良
 * @description ：这个类不允许多线程启动，同时只能有一个线程在使用这个类。否则会抛出运行时错误。
 * @date        ：2025 3月 17 11:11
 */

public class XPathGenerator extends Application {
    private static String xpathScript;
    private static String inputUrl;
    private static boolean isSingleElementParam;
    private static final CompletableFuture<String> xpathFuture = new CompletableFuture<>();
    private static final CompletableFuture<List<String>> xpathListFuture = new CompletableFuture<>();
    private static boolean isStart = false;
    private static final List<String> xpathList = new ArrayList<>();

    static {
        try {
            // 从资源文件中读取 JavaScript 代码
            InputStream inputStream = XPathGenerator.class.getResourceAsStream("/js/getXpath.js");
            if (inputStream != null) {
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                StringBuilder script = new StringBuilder();
                int data;
                while ((data = reader.read()) != -1) {
                    script.append((char) data);
                }
                reader.close();
                xpathScript = script.toString();
            } else {
                throw new RuntimeException("加载" + "/js/getXpath.js" + "脚本失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getXpathBefore(String url, boolean isSingleElement) {
        if (isStart) {
            throw new RuntimeException("XPathGenerator 类同时只能有一个线程使用！");
        }
        inputUrl = url;
        isSingleElementParam = isSingleElement;
        xpathList.clear();
    }

    public static String getXPathForUrl(String url) {
        getXpathBefore(url, true);
        // 启动 JavaFX 应用程序
        Application.launch(XPathGenerator.class);
        try {
            // 等待 XPath 生成
            return xpathFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            isStart = false;
        }
    }

    public static List<String> getXPathListForUrl(String url) {
        getXpathBefore(url, false);
        // 启动 JavaFX 应用程序
        Application.launch(XPathGenerator.class);
        try {
            // 等待 XPath 生成
            return xpathListFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            isStart = false;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        // 注入点击监听逻辑
        engine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                injectXPathLogic(engine);
            }
        });
        engine.load(inputUrl);
        primaryStage.setScene(new Scene(webView, 800, 600));
        primaryStage.setTitle("XPath生成器");

        // 添加窗口关闭事件监听器
        primaryStage.setOnCloseRequest(event -> {
            if (!isSingleElementParam) {
                xpathListFuture.complete(xpathList);
            }
        });

        primaryStage.show();
    }

    private void injectXPathLogic(WebEngine engine) {
        try {
            JSObject window = (JSObject) engine.executeScript("window");
            window.setMember("javaBridge", new JavaBridge());
            window.setMember("isSingleElement", isSingleElementParam);

            if (xpathScript != null) {
                engine.executeScript(xpathScript);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Java 回调处理器
    public static class JavaBridge {
        @SuppressWarnings("unused")
        public void onXPath(String xpath) {
            // 完成 CompletableFuture 并关闭 JavaFX 应用程序
            if (isSingleElementParam) {
                xpathFuture.complete(xpath);
                Platform.exit();
            } else {
                xpathList.add(xpath);
            }
        }
    }
}