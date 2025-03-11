package temp;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 10 23:33
 */

public class WebViewTest extends Application {
    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
            if (state == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaBridge", new JavaBridge());
                injectDoubleClickListener(webEngine);
            }
        });

        // 指定要打开的网页URL，这里以百度为例，你可以替换成其他网页
        String url = "https://www.baidu.com";
        webEngine.load(url);

        stage.setScene(new Scene(webView));
        stage.show();
    }

    private void injectDoubleClickListener(WebEngine engine) {
        String js = "document.addEventListener('dblclick', e => {" +
                "  let el = e.target;" +
                "  let html = el.outerHTML;" +
                "  javaBridge.onElementHTML(html);" +
                "  e.preventDefault();" +
                "}, true);";
        String js1 = "document.addEventListener('mouseover', e => {" +
                "  e.target.style.border = '1px solid black';" +
                "}, true);" +
                "document.addEventListener('mouseout', e => {" +
                "  e.target.style.border = '';" +
                "}, true);";
        engine.executeScript(js + js1);
    }

    public class JavaBridge {
        public void onElementHTML(String html) {
            System.out.println("Element HTML: " + html);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}