package com.reader.net;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
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
 */
public class XPathGenerator {
    private static String xpathScript;
    private static String inputUrl;
    private static boolean isSingleElementParam;
    private static CompletableFuture<String> xpathFuture;
    private static CompletableFuture<List<String>> xpathListFuture;
    private static boolean isStart = false;
    private static final List<String> xpathList = new ArrayList<>();

    static {
        loadXPathScript();
    }

    private static void loadXPathScript() {
        try {
            InputStream inputStream = XPathGenerator.class.getResourceAsStream("/js/getXpath.js");
            if (inputStream == null) {
                throw new RuntimeException("加载脚本失败: /js/getXpath.js");
            }
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            StringBuilder script = new StringBuilder();
            int data;
            while ((data = reader.read()) != -1) {
                script.append((char) data);
            }
            reader.close();
            xpathScript = script.toString();
        }
        catch (Exception e) {
            throw new RuntimeException("加载脚本异常", e);
        }
    }

    public static void initData() {
        xpathFuture = new CompletableFuture<>();
        xpathListFuture = new CompletableFuture<>();
    }

    public static void getXpathBefore(String url, boolean isSingleElement) {
        if (isStart) {
            throw new RuntimeException("XPathGenerator 类同时只能有一个线程使用！");
        }
        inputUrl = url;
        if (!inputUrl.startsWith("https://") && !"http://".startsWith(inputUrl)) {
            inputUrl = "https://" + inputUrl;
        }
        isSingleElementParam = isSingleElement;
        initData();
        isStart = true;
    }

    public static String getXPathForUrl(String url) {
        getXpathBefore(url, true);
        Platform.runLater(XPathGenerator::createAndShowWindow);
        try {
            return xpathFuture.get();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            isStart = false;
        }
    }

    public static List<String> getXPathListForUrl(String url) {
        getXpathBefore(url, false);
        Platform.runLater(XPathGenerator::createAndShowWindow);
        try {
            return xpathListFuture.get();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            isStart = false;
        }
    }

    private static void createAndShowWindow() {
        Stage newStage = new Stage();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.setTitle("XPath生成器");

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        // 创建导航按钮
        javafx.scene.control.Button backButton = new javafx.scene.control.Button("←");
        javafx.scene.control.Button forwardButton = new javafx.scene.control.Button("→");

        // 初始化按钮状态
        backButton.setDisable(true);
        forwardButton.setDisable(true);

        // 按钮点击事件
        backButton.setOnAction(_ -> engine.getHistory().go(-1));
        forwardButton.setOnAction(_ -> engine.getHistory().go(1));

        // 监听历史变化更新按钮状态
        engine.getHistory().currentIndexProperty().addListener((_, _, newIndex) -> {
            int currentIndex = newIndex.intValue();
            backButton.setDisable(currentIndex <= 0);
            forwardButton.setDisable(currentIndex >= engine.getHistory().getEntries().size() - 1);
        });

        // 创建顶部按钮栏
        HBox buttonBar = new HBox(10, backButton, forwardButton);
        buttonBar.setPadding(new Insets(5));
        buttonBar.setStyle("-fx-background-color: #f0f0f0;");

        // 组合布局
        VBox root = new VBox(buttonBar, webView);
        root.setPrefSize(800, 600);

        engine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                injectXPathLogic(engine);
            }
        });
        engine.load(inputUrl);

        newStage.setScene(new Scene(root));
        newStage.setOnCloseRequest(_ -> {
            if (isSingleElementParam) {
                if (!xpathFuture.isDone()) {
                    xpathFuture.cancel(true);
                }
            }
            else {
                xpathListFuture.complete(xpathList);
            }
        });
        newStage.show();
    }

    private static void injectXPathLogic(WebEngine engine) {
        try {
            JSObject window = (JSObject) engine.executeScript("window");
            window.setMember("javaBridge", new JavaBridge());
            window.setMember("isSingleElement", isSingleElementParam);
            engine.executeScript(xpathScript);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class JavaBridge {
        public void onXPath(String xpath) {
            if (isSingleElementParam) {
                xpathFuture.complete(xpath);
                closeWindow();
            }
            else {
                xpathList.add(xpath);
            }
        }

        private void closeWindow() {
            Platform.runLater(() -> {
                Stage stage = (Stage) xpathFuture.thenApply(_ -> null).getNow(null);
                if (stage != null) {
                    stage.close();
                }
            });
        }
    }
}