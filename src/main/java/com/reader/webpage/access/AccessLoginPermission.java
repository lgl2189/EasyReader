package com.reader.webpage.access;

import com.reader.entity.net.LoginStatus;
import com.reader.util.CookieUtil;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author ：李冠良
 * @description ：继承自 AccessWebPageContent 类，实现登录权限的访问，手动登录后，获取登录后的 cookie 和 localStorage 信息。
 */
public class AccessLoginPermission extends AccessWebPage {

    private final CompletableFuture<LoginStatus> loginStatusFuture = new CompletableFuture<>();
    private final CookieManager cookieManager = new CookieManager();

    /**
     * 构造函数，初始化输入的 URL 并进行必要的初始化操作。
     *
     * @param inputUrl 要访问的网页的 URL
     * @see #init(String)
     */
    public AccessLoginPermission(String inputUrl) {
        super(inputUrl);
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    public LoginStatus start() {
        run();
        try {
            return loginStatusFuture.get();
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
        String chromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36";
        webEngine.setUserAgent(chromeUserAgent);
    }

    @Override
    protected void onCloseStage(WindowEvent event, ContextWrapper context) {
        super.onCloseStage(event, context);
        WebView webView = context.webView();
        Stage stage = context.stage();
        try {
            CookieStore cookieStore = cookieManager.getCookieStore();
            String localStorage = (String) webView.getEngine().executeScript("JSON.stringify(window.localStorage)");
            loginStatusFuture.complete(new LoginStatus(
                    CookieUtil.parseCookies(cookieStore),
                    CookieUtil.parseLocalStorage(localStorage)
            ));
        }
        catch (Exception e) {
            e.printStackTrace();
            loginStatusFuture.completeExceptionally(e);
        }
        finally {
            stage.close();
        }
    }
}