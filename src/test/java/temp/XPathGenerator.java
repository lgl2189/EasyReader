package temp;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class XPathGenerator extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        // 注入点击监听逻辑
        engine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                injectXPathLogic(engine);
            }
        });

        // 加载测试页面（内置示例HTML）
        engine.load("https://www.baidu.com");

        primaryStage.setScene(new Scene(webView, 800, 600));
        primaryStage.setTitle("XPath Generator Test");
        primaryStage.show();
    }

    private void injectXPathLogic(WebEngine engine) {
        try {
            JSObject window = (JSObject) engine.executeScript("window");
            window.setMember("javaBridge", new JavaBridge());

            // 关键修复点：修复 JavaScript 语法错误
            String js = "(function() {"
                    + "const handleClick = function(e) {"
                    + "   if (e.button === 2) {"
                    + "       e.preventDefault();"
                    + "       let el = e.target;"
                    + "       let path = [];"
                    + "       while (el && el.nodeType === 1) {"
                    + "           let seg = el.tagName.toLowerCase();"
                    + "           if (el.id) {"
                    + "               seg += '[@id=\"' + el.id + '\"]';"
                    + "               path.unshift(seg);"
                    + "               break;"
                    + "           } else {"
                    + "               let siblings = el.parentNode.children;"
                    + "               let index = Array.from(siblings).indexOf(el) + 1;"
                    + "               if (index > 1) seg += '[' + index + ']';"
                    + "           }"
                    + "           path.unshift(seg);"
                    + "           el = el.parentNode;"
                    + "       }"
                    + "       javaBridge.onXPath('//' + path.join('/'));"
                    + "   }"
                    + "};"
                    + "const handleMouseOver = function(e) {"
                    + "   e.target.style.outline = '1px solid black';"
                    + "};"
                    + "const handleMouseOut = function(e) {"
                    + "   e.target.style.outline = '';"
                    + "};"
                    + "document.addEventListener('contextmenu', handleClick, true);"
                    + "document.addEventListener('mouseover', handleMouseOver, true);"
                    + "document.addEventListener('mouseout', handleMouseOut, true);"
                    + "new MutationObserver(function(mutations) {"
                    + "   mutations.forEach(function(m) {"
                    + "       Array.from(m.addedNodes).forEach(function(n) {"
                    + "           if (n.nodeType === 1) {"
                    + "               n.addEventListener('contextmenu', handleClick);"
                    + "               n.addEventListener('mouseover', handleMouseOver);"
                    + "               n.addEventListener('mouseout', handleMouseOut);"
                    + "           }"
                    + "       });"
                    + "   });"
                    + "}).observe(document.documentElement, {"
                    + "   childList: true, subtree: true"
                    + "});"
                    + "})();";
            engine.executeScript(js);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Java 回调处理器
    public static class JavaBridge {
        public void onXPath(String xpath) {
            System.out.println("[Generated XPath] " + xpath);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}