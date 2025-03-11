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
            String js = "(function() {"  // 改用传统函数声明，避免箭头函数潜在兼容性问题
                    + "const handleClick = function(e) {"  // 改用 function 声明
                    + "   e.preventDefault();"
                    + "   let el = e.target;"
                    + "   let path = [];"
                    + "   while (el && el.nodeType === 1) {"  // 确保 while 循环闭合
                    + "       let seg = el.tagName.toLowerCase();"
                    + "       if (el.id) {"
                    + "           seg += '[@id=\"' + el.id + '\"]';"  // 改用单引号避免转义问题
                    + "           path.unshift(seg);"
                    + "           break;"
                    + "       } else {"
                    + "           let siblings = el.parentNode.children;"
                    + "           let index = Array.from(siblings).indexOf(el) + 1;"
                    + "           if (index > 1) seg += '[' + index + ']';"  // 字符串拼接标准化
                    + "       }"
                    + "       path.unshift(seg);"  // 修复此处缺少分号
                    + "       el = el.parentNode;"
                    + "   }"
                    + "   javaBridge.onXPath('//' + path.join('/'));"  // 闭合括号
                    + "};"

                    + "document.addEventListener('click', handleClick, true);"

                    + "new MutationObserver(function(mutations) {"  // 改用传统函数
                    + "   mutations.forEach(function(m) {"
                    + "       Array.from(m.addedNodes).forEach(function(n) {"
                    + "           if (n.nodeType === 1) n.addEventListener('click', handleClick);"
                    + "       });"
                    + "   });"
                    + "}).observe(document.documentElement, {"
                    + "   childList: true, subtree: true"
                    + "});"
                    + "})();";  // 确保 IIFE 闭合

            engine.executeScript(js);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Java 回调处理器
    public class JavaBridge {
        public void onXPath(String xpath) {
            System.out.println("[Generated XPath] " + xpath);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}