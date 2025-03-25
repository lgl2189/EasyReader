package com.reader.net.webpage;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：李冠良
 * @description ：继承自AccessWebPage，实现菜单功能
 * @date ：2025 3月 25 08:26
 */


public class AccessWebPageWithMenu extends AccessWebPage {

    private String menuScript;
    private final List<MenuItem> menuItemList = new ArrayList<>();

    static{

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

    @Override
    protected void setWebViewAttribute(WebView webView) {
        super.setWebViewAttribute(webView);
        // 禁用默认菜单
        webView.setContextMenuEnabled(false);
        // 监听右键点击事件
        webView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showCustomContextMenu(webView, event.getScreenX(), event.getScreenY());
                event.consume();
            }
        });

        // 注入JS禁用网页右键
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webView.getEngine().executeScript("window");
                window.setMember("javaBridge", new JavaBridge());

                webView.getEngine().executeScript(
                        "document.addEventListener('contextmenu', function(e) {" +
                                "   var elementType = e.target.tagName.toLowerCase();" +
                                "   window.javaBridge.showMenu(elementType);" +
                                "   e.preventDefault();" +
                                "});"
                );
            }
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

    protected class JavaBridge implements AccessWebPage.JavaBridge {
        public void showMenu(String elementType) {
            // 可根据elementType（如"a", "img"）动态调整菜单
            System.out.println("右键点击元素类型: " + elementType);
        }
    }
}