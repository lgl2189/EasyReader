package com.reader.net.webpage;

import com.reader.ui.util.JavaScriptUtil;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：李冠良
 * @description ：继承自AccessWebPage，实现菜单功能
 * @date ：2025 3月 25 08:26
 */


public abstract class AccessWebPageWithMenu extends AccessWebPage {

    private static final String menuScript;
    private final List<MenuItem> menuItemList = new ArrayList<>();

    static {
        menuScript = JavaScriptUtil.loadScript("/js/browserMenu.js");
    }

    /**
     * 构造函数，初始化输入的 URL 并进行必要的初始化操作。
     *
     * @param inputUrl 要访问的网页的 URL
     * @see #init(String)
     */
    public AccessWebPageWithMenu(String inputUrl) {
        super(inputUrl);
    }

    protected abstract JavaBridge setJavaBridgeObject();

    @Override
    protected void doWithWebView(ContextWrapper context) {
        super.doWithWebView(context);
        WebView webView = context.webView();
        WebEngine webEngine = webView.getEngine();
        webView.setContextMenuEnabled(true);
        // 监听右键点击事件
        webView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showCustomContextMenu(webView, event.getScreenX(), event.getScreenY());
            }
        });

        // 注入JS禁用网页右键
        JavaBridge javaBridge = setJavaBridgeObject();
        webEngine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                bindJavaBridge(webEngine, javaBridge);
            }
        });
        // 新增：监听WebView尺寸变化，重新绑定JavaBridge
    }

    private void bindJavaBridge(WebEngine webEngine, JavaBridge javaBridge) {
        // 在Platform.runLater中绑定JavaBridge，确保JavaBridge在UI线程中执行，否则可能出现javafx监听器和js监听器不一致的问题，js监听器可能无法生效
        Platform.runLater(() -> {
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javaBridge", javaBridge);
            webEngine.executeScript(menuScript);
        });
    }

    private void showCustomContextMenu(WebView webView, double x, double y) {
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(menuItemList);
        menu.show(webView, x, y);
    }

    public void addMenuItem(String title, javafx.event.EventHandler<ActionEvent> action) {
        if (isStart) {
            throw new IllegalCallerException("请在调用run()之前添加菜单项！");
        }
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(action);
        menuItemList.add(menuItem);
    }

    protected interface JavaBridge extends AccessWebPage.JavaBridge {
        void doOnContextMenu(String elementType);
    }
}