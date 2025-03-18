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
import java.util.concurrent.CompletableFuture;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 17 11:11
 */

public class XPathGenerator extends Application {
    private static String inputUrl;
    private static final CompletableFuture<String> xpathFuture = new CompletableFuture<>();
    private static String xpathLogicScript;

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
                xpathLogicScript = script.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getXPathForUrl(String url) {
        inputUrl = url;
        // 启动 JavaFX 应用程序
        new Thread(() -> Application.launch(XPathGenerator.class)).start();
        try {
            // 等待 XPath 生成
            return xpathFuture.get();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
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

        // 加载指定的 URL
        engine.load(inputUrl);

        primaryStage.setScene(new Scene(webView, 800, 600));
        primaryStage.setTitle("XPath生成器");
        primaryStage.show();
    }

    private void injectXPathLogic(WebEngine engine) {
        try {
            JSObject window = (JSObject) engine.executeScript("window");
            window.setMember("javaBridge", new JavaBridge());

            if (xpathLogicScript != null) {
                engine.executeScript(xpathLogicScript);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Java 回调处理器
    public static class JavaBridge {
        @SuppressWarnings("unused")
        public void onXPath(String xpath) {
            // 完成 CompletableFuture 并关闭 JavaFX 应用程序
            xpathFuture.complete(xpath);
            Platform.exit();
        }
    }
}