package temp;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 3月 25 17:07
 */


import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebViewTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // 获取Cookie
                String cookies = (String) webView.getEngine().executeScript("document.cookie");
                System.out.println("Cookies: " + cookies);

                // 获取LocalStorage
                String localStorage = (String) webView.getEngine().executeScript("JSON.stringify(window.localStorage)");
                System.out.println("LocalStorage: " + localStorage);
            }
        });

        webView.getEngine().load("https://www.baidu.com");

        primaryStage.setScene(new Scene(webView, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}