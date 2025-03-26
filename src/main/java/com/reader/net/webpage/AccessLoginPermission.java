package com.reader.net.webpage;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author ：李冠良
 * @description ：继承自 AccessWebPageContent 类，实现登录权限的访问，手动登录后，获取登录后的 cookie 和 localStorage 信息。
 */
public class AccessLoginPermission extends AccessWebPageWithMenu {

    private final CompletableFuture<FutureWrapper> futureWrapper = new CompletableFuture<>();

    /**
     * 构造函数，初始化输入的 URL 并进行必要的初始化操作。
     *
     * @param inputUrl 要访问的网页的 URL
     * @see #init(String)
     */
    public AccessLoginPermission(String inputUrl) {
        super(inputUrl);
    }

    public FutureWrapper start() {
        run();
        try {
            return futureWrapper.get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doWithWebView(ContextWrapper context) {
        super.doWithWebView(context);
        WebEngine webEngine = context.webView().getEngine();
        // 设置Chrome的User-Agent（示例为Windows版Chrome 120）
        String chromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        webEngine.setUserAgent(chromeUserAgent);
    }

    @Override
    protected void doWithStage(ContextWrapper context) {
        super.doWithStage(context);
        WebView webView = context.webView();
        Stage stage = context.stage();
    }

    @Override
    protected void onCloseStage(WindowEvent event, ContextWrapper context) {
        super.onCloseStage(event, context);
        WebView webView = context.webView();
        Stage stage = context.stage();
        try {
            String cookie = (String) webView.getEngine().executeScript("document.cookie");
            String localStorage = (String) webView.getEngine().executeScript("JSON.stringify(window.localStorage)");
            futureWrapper.complete(new FutureWrapper(cookie, localStorage));
        }
        catch (Exception e) {
            e.printStackTrace();
            futureWrapper.completeExceptionally(e);
        }
        finally {
            stage.close();
        }
    }

    public static class FutureWrapper implements AccessWebPage.FutureWrapper {
        private final String cookie;
        private final String localStorage;

        public FutureWrapper(String cookie, String localStorage) {
            this.cookie = cookie;
            this.localStorage = localStorage;
        }

        public String getCookie() {
            return cookie;
        }

        public String getLocalStorage() {
            return localStorage;
        }
    }
}