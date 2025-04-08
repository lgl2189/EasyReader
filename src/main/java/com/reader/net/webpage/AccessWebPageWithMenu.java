package com.reader.net.webpage;

import com.google.gson.Gson;
import com.reader.ui.util.JavaScriptUtil;
import com.reader.util.IdGenerator;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        JavaBridge javaBridge = setJavaBridgeObject();
        webEngine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                bindJavaBridge(webEngine, javaBridge);
            }
        });
    }

    private void bindJavaBridge(WebEngine webEngine, JavaBridge javaBridge) {
        // 在Platform.runLater中绑定JavaBridge，确保JavaBridge在UI线程中执行，否则可能出现javafx监听器和js监听器不一致的问题，js监听器可能无法生效
        Platform.runLater(() -> {
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javaBridge", javaBridge);
            Gson gson = new Gson();
            String jsonMenuItems = gson.toJson(menuItemList.stream()
                    .map(item -> Map.of("id", item.id(), "title", item.title()))
                    .collect(Collectors.toList()));
            webEngine.executeScript("window.javaMenuItems = JSON.parse('" + jsonMenuItems + "')");
            webEngine.executeScript(menuScript);
        });
    }

    /**
     * 为浏览器自定义右键菜单添加菜单项
     * @param title 菜单项标题
     * @param action 菜单项点击事件，菜单项被点击时会触发该事件
     */
    public void addMenuItem(String title, javafx.event.EventHandler<ActionEvent> action) {
        if (isStart) {
            throw new IllegalCallerException("请在调用run()之前添加菜单项！");
        }
        MenuItem menuItem = new MenuItem(IdGenerator.generateIdFromString(title), title, action);
        menuItemList.add(menuItem);
    }

    /**
     * 为浏览器自定义右键菜单添加菜单项，该菜单项仅用于UI操作，action将在UI线程中执行
     * @param title 菜单项标题
     * @param action 菜单项点击事件，菜单项被点击时会触发该事件
     */
    public void addMenuItemForUiOperator(String title, javafx.event.EventHandler<ActionEvent> action) {
        javafx.event.EventHandler<ActionEvent> uiAction = event -> {
            Platform.runLater(()->{
                action.handle(event);
            });
        };
        addMenuItem(title, uiAction);
    }

    protected class JavaBridge implements AccessWebPage.JavaBridge {
        /**
         * 菜单项点击事件处理函数，该处理函数默认实现会直接调用菜单项的 action 事件处理函数，并在 UI 线程中执行
         * 如果不需要立刻执行 action 事件处理函数，可以重写该函数，在其中执行必要的操作，然后调用 handleAction 方法来执行 action 事件处理函数
         * @param menuItemId 菜单项 ID
         * @param elementInfoJson 选择的元素信息的 JSON 字符串
         */
        public void onMenuItemClicked(String menuItemId, String elementInfoJson) {
            handleAction(menuItemId);
        }

        protected void handleAction(String menuItemId) {
            menuItemList.stream()
                    .filter(item -> item.id().equals(menuItemId))
                    .findFirst()
                    .ifPresent(item -> {
                        Platform.runLater(() -> {
                            item.action().handle(new ActionEvent());
                        });
                    });
        }
    }

    protected record MenuItem(String id, String title, javafx.event.EventHandler<ActionEvent> action) {
    }
}