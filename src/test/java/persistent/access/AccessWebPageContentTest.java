package persistent.access;

import com.reader.net.webpage.AccessWebPage;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 24 11:21
 */


public class AccessWebPageContentTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        AccessWebPage accessWebPage = new AccessWebPage("www.baidu.com");
        accessWebPage.run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}